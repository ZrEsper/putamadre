from pathlib import Path
import zipfile,hashlib,json,tempfile,subprocess,os,sys,re,math
r=Path(__file__).resolve().parent
base=Path(sys.argv[1]).resolve();tools=Path(sys.argv[2]).resolve()
out=r.parent/'mods/doomsday_decoration-1.1.4.2-neoforge-1.21.1-zomboid-storage.2.jar'
sha=hashlib.sha256(base.read_bytes()).hexdigest()
assert sha=='8bf48165646d11f1622aa9a015e573070eaccac12e98052b37548df8b4240d52','Expected uploaded Doomsday 1.1.4.2'
for name,digest in json.loads((r/'tools.sha256.json').read_text()).items():assert hashlib.sha256((tools/name).read_bytes()).hexdigest()==digest,name
ids=re.findall(r'"([a-z0-9_]+)"',(r/'src/dev/zomboid/decor/Storage.java').read_text().split('static final ThreadLocal')[0])
assert len(ids)==71 and len(set(ids))==71
asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar','asm-util.jar'])
ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn']
with zipfile.ZipFile(base) as z:specs=json.loads(z.read('doomsday_decoration/block_specs.json'))
index={s['id']:s for s in specs}
assert set(ids)<=index.keys()
for i in ids:
 assert index[i]['type']=='Block'
 index[i]['container']=True
 # Prevent pistons separating a model from its linked cells.
 # Piston behavior is applied in Storage.configure, not in the original JSON schema.
footprint_ids={s['id'] for s in specs if s['type']=='Block' and any(b[0]<0 or b[1]<0 or b[2]<0 or b[3]>16 or b[4]>16 or b[5]>16 for boxes in (s.get('shapes') or {}).values() for b in boxes)}
# Each base has distinct ingredients; variants transform the base instead of conflicting recipes.
centers={'acrate':'iron_nugget','knapsack':'string','officedrawers_1':'paper','fridge':'packed_ice','carton':'wheat','plasticbox':'glass','goodsshelves':'book','vendingmachine':'repeater','insulationcabinet':'ice','indoorgarbagebin':'rotten_flesh','woodencrate':'stick','weaponbox':'iron_sword','toolbox':'iron_pickaxe','medicalbox':'white_wool','safe':'gold_ingot','luggage':'leather','ammunitionbox':'gunpowder','mailbox_1':'feather','metalcabinet':'iron_door','metaldrawer':'tripwire_hook','shelf':'oak_stairs','weapon_rack':'iron_bars','woodencabinet':'oak_door','woodendrawer':'oak_button','cabinet':'copper_ingot','chemicalprotectiveclothing':'yellow_wool','emptytesttuberack':'glass_bottle','testtuberack':'redstone','accessorybox_1':'compass'}
dyes=['red','blue','green','yellow','white','black','orange','cyan','purple','gray','pink','brown','light_blue','lime','magenta','light_gray']
recipes={};families={};groups={}
for i in ids:
 family=re.sub(r'_\d+$','',i)
 if i=='officedrawers_2':family='officedrawers'
 if i.endswith('luggage'):family='luggage'
 groups.setdefault(family,[]).append(i)
for family, members in groups.items():
 main=members[0]
 assert main in centers, main
 wood=main in ['acrate','carton','woodencrate','shelf','weapon_rack','woodencabinet','woodendrawer','knapsack','luggage','emptytesttuberack','testtuberack']
 material='oak_planks' if wood else 'iron_ingot'
 recipes[main]={'type':'minecraft:crafting_shaped','category':'misc','pattern':['M M','BCB','MMM'],'key':{'M':{'item':'minecraft:'+material},'B':{'item':'minecraft:barrel'},'C':{'item':'minecraft:'+centers[main]}},'result':{'id':'doomsday_decoration:'+main,'count':1}}
 for n,i in enumerate(members[1:]):recipes[i]={'type':'minecraft:crafting_shapeless','category':'misc','ingredients':[{'item':'doomsday_decoration:'+main},{'item':'minecraft:'+dyes[n]+'_dye'}],'result':{'id':'doomsday_decoration:'+i,'count':1}}
 assert len(members)-1<=len(dyes)
with tempfile.TemporaryDirectory() as temp:
 t=Path(temp);classes=t/'classes';patch=t/'patch';extra=t/'extra';tests=t/'tests'
 extra.mkdir()
 cases=[]
 for spec in specs:
  if spec['type']!='Block':continue
  for boxes in (spec.get('shapes') or {}).values():
   cells=set()
   for box in boxes:
    for x in range(math.floor(box[0]/16+1e-7),math.ceil(box[3]/16-1e-7)):
     for y in range(math.floor(box[1]/16+1e-7),math.ceil(box[4]/16-1e-7)):
      for z in range(math.floor(box[2]/16+1e-7),math.ceil(box[5]/16-1e-7)):cells.add((x,y,z))
   cells.discard((0,0,0))
   cases.append(';'.join(','.join(str(v/16) for v in b) for b in boxes)+'|'+ ';'.join(','.join(str(v) for v in c) for c in sorted(cells)))
 (t/'footprints.txt').write_text('\n'.join(cases)+'\n')
 def asset(name,data):
  p=extra/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(data,indent=2)+'\n')
 asset('doomsday_decoration/block_specs.json',specs)
 for i,data in recipes.items():
  asset('data/doomsday_decoration/recipe/'+i+'.json',data)
  ingredient='doomsday_decoration:'+i if data['type'].endswith('shapeless') else 'minecraft:'+centers[i]
  if data['type'].endswith('shapeless'):ingredient=data['ingredients'][0]['item']
  asset('data/doomsday_decoration/advancement/recipes/'+i+'.json',{'parent':'minecraft:recipes/root','criteria':{'has_material':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':[ingredient]}]}}},'rewards':{'recipes':['doomsday_decoration:'+i]}})
 asset('data/doomsday_decoration/loot_table/blocks/interaction_part.json',{'type':'minecraft:block','pools':[]})
 asset('assets/doomsday_decoration/blockstates/interaction_part.json',{'variants':{'':{'model':'doomsday_decoration:block/interaction_part'}}})
 asset('assets/doomsday_decoration/models/block/interaction_part.json',{'textures':{'particle':'minecraft:block/iron_block'},'elements':[]})
 for cmd in [ecj+['-d',str(classes)]+[str(p) for p in (r/'src').rglob('*.java')],ecj+['-cp',asm,'-d',str(patch),str(r/'Patch.java'),str(r/'Verify.java')],['java','-cp',str(patch)+os.pathsep+asm,'Patch',str(base),str(out),str(classes)]]:subprocess.run(cmd,check=True)
 with zipfile.ZipFile(out) as z:entries={n:z.read(n) for n in z.namelist()}
 entries.update({p.relative_to(extra).as_posix():p.read_bytes() for p in extra.rglob('*') if p.is_file()})
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
  for n,data in entries.items():z.writestr(zipfile.ZipInfo(n,(1980,1,1,0,0,0)),data,compress_type=zipfile.ZIP_DEFLATED)
 for cmd in [ecj+['-cp',str(out),'-d',str(tests)]+[str(p) for p in (r/'tests').rglob('*.java')],['java','-Xverify:all','-cp',str(tests)+os.pathsep+str(out),'dev.zomboid.decor.Checks',str(t/'footprints.txt')],['java','-cp',str(patch)+os.pathsep+asm,'Verify',str(out)]]:
  result=subprocess.run(cmd,text=True,capture_output=True)
  if result.stdout:print(result.stdout,end='')
  if result.stderr:print(result.stderr,end='',file=sys.stderr)
  result.check_returncode()
  if 'dev.zomboid.decor.Checks' in cmd:passed=int(re.search(r'Simulated checks passed: (\d+)',result.stdout).group(1))
 with zipfile.ZipFile(out) as z:
  assert z.testzip() is None
  actual=json.loads(z.read('doomsday_decoration/block_specs.json'))
  assert sum(s.get('container',False) for s in actual)==71
  assert all('data/doomsday_decoration/recipe/'+i+'.json' in z.namelist() for i in ids)
  assert not any(n.startswith(('tests/','net/minecraft/')) for n in z.namelist())
  assert all(json.loads(z.read(n)) is not None for n in z.namelist() if n.endswith('.json'))
 (r/'validation.json').write_text(json.dumps({'input_sha256':sha,'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'functional_blocks':len(ids),'physical_multiblock_ids':len(footprint_ids),'recipes':len(recipes),'simulated_checks_passed':passed,'minecraft_runtime_tested':False,'validation':'Footprints, ownership, placement, cleanup, inventories and cooler logic with simulated API; bytecode analyzed'},indent=2)+'\n')
