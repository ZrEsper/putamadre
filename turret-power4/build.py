from pathlib import Path
import subprocess,hashlib,os,json,zipfile
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;tools=root/'hordes-melee-interiors-patch/build/tools'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a):subprocess.run(list(map(str,a)),check=True)
for n,h in {'ecj':'01f5a92ac19bb2b3bf85e295a68f2c73c264369109158b566ce9b490af982948','asm':'8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281','asm-tree':'9929881f59eb6b840e86d54570c77b59ce721d104e6dfd7a40978991c2d3b41f','asm-analysis':'85b29371884ba31bb76edf22323c2c24e172c3267a67152eba3d1ccc2e041ef2'}.items():assert sha(tools/(n+'.jar'))==h
sources={'turret':r/'inputs/tacz_turrets-neoforge-3.0.0-1.21.1.jar','decor':root/'mods/doomsday_decoration-1.1.4.2-neoforge-1.21.1-zomboid-climate-storage.3.jar','survival':root/'mods/zomboid-survival-1.21.1-1.1.0-ff-preservation-doomsday-compat.1.jar'}
expected=json.loads(Path('inputs.json').read_text());assert {k:sha(v)for k,v in sources.items()}==expected
names={'turret':'tacz_turrets-neoforge-3.0.0-1.21.1-doomsday-power-burst.1.jar','decor':'doomsday_decoration-1.1.4.2-neoforge-1.21.1-zomboid-beds-barricades.4.jar','survival':'zomboid-survival-1.21.1-1.1.0-generator-tiers.2.jar'}
cp=os.pathsep.join(str(tools/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);ecj=['java','-jar',tools/'ecj.jar','-21','-proc:none','-nowarn']
Path('build/tool').mkdir(parents=True,exist_ok=True);Path('output').mkdir(exist_ok=True);run(*ecj,'-cp',cp,'-d','build/tool','Patch.java');report={}
for kind,source in sources.items():
 classes=Path('build')/kind;classes.mkdir(exist_ok=True);pkg={'turret':'turretpower','decor':'decor','survival':'survival'}[kind];run(*ecj,'-cp',source,'-d',classes,*Path('src/dev/zomboid',pkg).glob('*.java'));out=Path('output')/names[kind];run('java','-cp',cp+os.pathsep+'build/tool','Patch',source,out,classes,kind)
 with zipfile.ZipFile(out)as z:entries={n:z.read(n)for n in z.namelist()}
 if kind=='decor':
  specs=json.loads(entries['doomsday_decoration/block_specs.json']);planks=[s['id']for s in specs if s['id'].startswith('planking_')]
  for i in planks:
   recipe={'type':'minecraft:crafting_shaped','category':'building','pattern':['PPP'],'key':{'P':{'tag':'minecraft:planks'}},'result':{'id':'doomsday_decoration:'+i,'count':6}} if i=='planking_0' else {'type':'minecraft:stonecutting','ingredient':{'item':'doomsday_decoration:planking_0'},'result':{'id':'doomsday_decoration:'+i,'count':1}}
   entries['data/doomsday_decoration/recipe/'+i+'.json']=json.dumps(recipe).encode()
  for i in ['bed','medical_bed']:
   recipe={'type':'minecraft:crafting_shaped','category':'misc','pattern':['WWW'],'key':{'W':{'tag':'minecraft:wool'}},'result':{'id':'doomsday_decoration:'+i,'count':1}}
   entries['data/doomsday_decoration/recipe/'+i+'.json']=json.dumps(recipe).encode()
   entries['data/doomsday_decoration/advancement/recipes/'+i+'.json']=json.dumps({'parent':'minecraft:recipes/root','criteria':{'has_wool':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':['minecraft:white_wool']}]}}},'rewards':{'recipes':['doomsday_decoration:'+i]}}).encode()
  # Distinct recipes: normal mattress horizontal, medical mattress vertical.
  med='data/doomsday_decoration/recipe/medical_bed.json';v=json.loads(entries[med]);v['pattern']=['W','W','W'];entries[med]=json.dumps(v).encode()
 if kind=='turret':
  sounds=json.loads(entries['assets/tacz_turrets/sounds.json'])
  for n in ['servo_idle','target_alert']:
   sounds[n]={'subtitle':'subtitles.tacz_turrets.'+n,'sounds':[{'name':'tacz_turrets:'+n}]};entries['assets/tacz_turrets/sounds/'+n+'.ogg']=Path('assets',n+'.ogg').read_bytes()
  entries['assets/tacz_turrets/sounds.json']=json.dumps(sounds).encode()
  for locale,labels in {'es_es':['Motor de torreta','Torreta: objetivo detectado'],'en_us':['Turret servo','Turret: target detected'],'de_de':['Geschützturmservo','Geschützturm: Ziel erkannt']}.items():
   path='assets/tacz_turrets/lang/'+locale+'.json';v=json.loads(entries.get(path,b'{}'));v.update(dict(zip(['subtitles.tacz_turrets.servo_idle','subtitles.tacz_turrets.target_alert'],labels)));entries[path]=json.dumps(v,ensure_ascii=False).encode()
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
  for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
 with zipfile.ZipFile(source)as a,zipfile.ZipFile(out)as b:
  assert b.testzip()is None and set(a.namelist())<=set(b.namelist());assert a.read('META-INF/neoforge.mods.toml')==b.read('META-INF/neoforge.mods.toml')
  changed=[n for n in a.namelist()if a.read(n)!=b.read(n)];added=sorted(set(b.namelist())-set(a.namelist()));assert not any(n.startswith('net/minecraft/')for n in added)
  for n in b.namelist():
   if n.endswith('.json'):json.loads(b.read(n))
 report[kind]={'input_sha256':sha(source),'output':names[kind],'sha256':sha(out),'changed':sorted(changed),'added':added,'minecraft_runtime_tested':False}
Path('validation.json').write_text(json.dumps(report,indent=2)+'\n')
