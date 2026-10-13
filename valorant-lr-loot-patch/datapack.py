from pathlib import Path
import json,zipfile,hashlib
r=Path(__file__).resolve().parent;c=json.loads((r/'catalog.json').read_text());source=Path('/workspace/attachments/d331eacd-e6a8-4889-8ae3-79099a0fcb18/mosslorn-expanded.11-paced-scans.zip');out=r/'mosslorn-expanded.12-valorant-lrarmor.zip'
with zipfile.ZipFile(source) as z:files={n:z.read(n) for n in z.namelist()}
root='data/mosslorn/loot_table/';added=set();changed=[]
def item(id,tag=None,count=None,weight=1):
 e={'type':'minecraft:item','name':id,'weight':weight};f=[]
 if tag:f.append({'function':'minecraft:set_custom_data','tag':tag})
 if count:f.append({'function':'minecraft:set_count','count':{'type':'minecraft:uniform','min':count[0],'max':count[1]}})
 if f:e['functions']=f
 return e
def ref(path,weight=1):return {'type':'minecraft:loot_table','value':'mosslorn:addons/'+path,'weight':weight}
def pool(entries,chance=1):
 p={'rolls':1,'entries':entries}
 if chance<1:p['conditions']=[{'condition':'minecraft:random_chance','chance':chance}]
 return p
def put(name,pools):
 key=root+'addons/'+name+'.json';files[key]=(json.dumps({'type':'minecraft:chest','pools':pools},indent=2)+'\n').encode();added.add(key)
for g in c['guns']:
 slug=g['id'].replace(':','_');current=min(g['capacity'],max(1,g['capacity']//3))
 put('loadouts/'+slug,[pool([item('tacz:modern_kinetic_gun','{GunId:"'+g['id']+'",GunFireMode:"'+g['mode']+'",GunCurrentAmmoCount:'+str(current)+'}')]),pool([item('tacz:ammo','{AmmoId:"'+g['ammo']+'"}',(8,24))])])
for category in ['military','police','safehouse']:
 for state,threshold in [('ransacked',1),('normal',2),('stocked',4)]:
  guns=[g for g in c['guns'] if g['min_quality']<=threshold and (category=='military' or g['id'].split(':')[1] in ['classic','ghost','sheriff','judge','outlaw','phantom'])]
  put('guns_'+category+'_'+state,[pool([ref('loadouts/'+g['id'].replace(':','_'),1 if g['min_quality']>=3 else 4) for g in guns])])
put('knives',[pool([item(k['base_item'],'{MeleeWeaponId:"'+k['id']+'"}',weight=3 if k['min_quality']==1 else 1) for k in c['knives']])])
put('ammo',[pool([item('tacz:ammo','{AmmoId:"'+a+'"}',(4,20)) for a in c['ammo']])])
put('attachments',[pool([item('tacz:attachment','{AttachmentId:"'+a+'"}') for a in c['attachments']])])
groups={'military':['armored_chemical','attacker','defender','medical','scout','sniper'],'police':['dea_armed','dea','atf','atf_vest','irs','fbi','fbi_armed'],'medical':['medical'],'workshop':['armored_chemical','chemical_protective'],'camping':['scout','chemical_protective'],'civilian':['joker','joker_armed']}
for group,suits in groups.items():
 armor=[id for id in c['armor'] if any(id.startswith(suit+'_') for suit in suits)]
 put('armor_'+group,[pool([item('lrarmor:'+id) for id in armor])])
settings={'military':('military',[.08,.18,.32],[.035,.08,.14],[.10,.23,.40]),'police':('police',[.035,.10,.18],[.015,.04,.07],[.08,.18,.30]),'safehouse':('military',[.01,.04,.08],[.01,.035,.06],[.03,.07,.12]),'camping':('camping',None,[.015,.04,.075],[.02,.045,.08]),'workshop':('workshop',None,[.02,.045,.09],[.025,.06,.10]),'house':('civilian',None,None,[.008,.015,.025]),'pharmacy':('medical',None,None,[.025,.05,.08])}
for key in list(files):
 if not key.startswith(root) or not key.endswith('.json') or key in added:continue
 relative=key[len(root):];category=None;state=None
 for cat in settings:
  for s in ['ransacked','normal','stocked']:
   if relative=='variants/crate_'+cat+'_'+s+'.json':category,state=cat,s
 if relative.startswith('office/emergency_'):category='safehouse';state=relative.removeprefix('office/emergency_').removesuffix('.json')
 if relative.startswith('office/civilian_clothes_'):category='house';state=relative.removeprefix('office/civilian_clothes_').removesuffix('.json')
 if not category or state not in ['ransacked','normal','stocked']:continue
 armor,gunCh,knifeCh,armorCh=settings[category];index=['ransacked','normal','stocked'].index(state);table=json.loads(files[key]);originalPools=table['pools'].copy()
 if gunCh:table['pools'].append(pool([ref('guns_'+category+'_'+state if category in ['military','police','safehouse'] else 'guns_safehouse_'+state)],gunCh[index]))
 if knifeCh:table['pools'].append(pool([ref('knives')],knifeCh[index]))
 table['pools'].append(pool([ref('armor_'+armor)],armorCh[index]))
 if category in ['military','police','safehouse']:table['pools'].append(pool([ref('ammo')],[.06,.12,.22][index]));table['pools'].append(pool([ref('attachments')],[.01,.025,.06][index]))
 assert table['pools'][:len(originalPools)]==originalPools
 files[key]=(json.dumps(table,indent=2)+'\n').encode();changed.append(key)
meta=json.loads(files['pack.mcmeta']);meta['pack']['description']='Mosslorn v12: paced scans + Valorant guns/knives and LRArmor loot';files['pack.mcmeta']=json.dumps(meta,indent=2).encode()
ids={k[len(root):-5] for k in files if k.startswith(root) and k.endswith('.json')};references=0
def walk(o):
 if isinstance(o,dict):
  yield o
  for v in o.values():yield from walk(v)
 elif isinstance(o,list):
  for v in o:yield from walk(v)
for key in added|set(changed):
 for obj in walk(json.loads(files[key])):
  if obj.get('type')=='minecraft:loot_table' and obj['value'].startswith('mosslorn:'):assert obj['value'][9:] in ids;references+=1
  if obj.get('type')=='minecraft:item' and obj['name'].startswith('lrarmor:'):assert obj['name'][8:] in c['armor']
# Every requested weapon and armor ID has a reachable entry in at least one new pool.
joined='\n'.join(files[k].decode() for k in added)
for g in c['guns']:assert g['id'] in joined
for k in c['knives']:assert k['id'] in joined
for a in c['armor']:assert 'lrarmor:'+a in joined
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
 for k,b in files.items():z.writestr(k,b)
with zipfile.ZipFile(source) as a,zipfile.ZipFile(out) as b:
 assert b.testzip() is None
 untouched=[k for k in a.namelist() if k not in changed and k!='pack.mcmeta'];assert all(a.read(k)==b.read(k) for k in untouched)
(r/'datapack-validation.json').write_text(json.dumps({'new_tables':len(added),'extended_tables':len(changed),'reference_checks':references,'unchanged_original_entries':len(untouched),'original_scan_functions_preserved':True,'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest()},indent=2)+'\n')
print('Added tables:',len(added),'Extended container tables:',len(changed),'Reference checks:',references)
