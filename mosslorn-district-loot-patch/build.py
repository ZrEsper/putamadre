from pathlib import Path
import json,zipfile,argparse,hashlib
R=Path(__file__).resolve().parent
WORLD={'north':{'crate_house':30,'crate_kitchen':16,'crate_fridge':10,'crate_pharmacy':9,'crate_workshop':8,'crate_farm':7,'crate_camping':6,'crate_books':5,'crate_bathroom':5,'crate_safehouse':4},'south':{'crate_office':30,'crate_electronics':16,'crate_pharmacy':14,'crate_workshop':10,'crate_kitchen':8,'crate_books':7,'crate_safehouse':6,'crate_mechanic':5,'crate_police':3,'crate_military':1}}
OFFICE={'north':{'office':15,'medical':15,'electronics':5,'maintenance':12,'civilian_clothes':12,'breakroom':18,'research':3,'camping':8,'library':5,'gardening':5,'emergency':2},'south':{'office':28,'medical':17,'electronics':16,'maintenance':10,'civilian_clothes':6,'breakroom':9,'research':7,'camping':2,'library':3,'gardening':1,'emergency':1}}
def ref(path,weight=1):return {'type':'minecraft:loot_table','value':'mosslorn:'+path,'weight':weight}
def table(es):return {'type':'minecraft:chest','pools':[{'rolls':1,'entries':es}]}
def location(**bounds):return {'condition':'minecraft:location_check','predicate':{'position':{'z':bounds}}}
def regional(kind,split):
 a=ref('districts/'+kind+'_north');a['conditions']=[location(max=split-0.000001)]
 b=ref('districts/'+kind+'_south');b['conditions']=[location(min=split)]
 return {'type':'minecraft:alternatives','children':[a,b,ref('districts/'+kind+'_fallback')]}
def build(split):
 with zipfile.ZipFile(R.parent/'mosslorn-office-loot-patch/mosslorn-expanded.3-office.zip') as z:files={n:z.read(n) for n in z.namelist()}
 def get(path):return json.loads(files['data/mosslorn/loot_table/'+path+'.json'])
 def put(path,d):files['data/mosslorn/loot_table/'+path+'.json']=(json.dumps(d,indent=2)+'\n').encode()
 original_office=get('chests/crate_office');put('districts/world_fallback',get('chests/world_mix'))
 # Preserve guaranteed provisions in the outer Office Cabinet table.
 put('districts/office_fallback',{'type':'minecraft:chest','pools':original_office['pools'][1:]})
 for district,weights in WORLD.items():
  assert sum(weights.values())==100
  put('districts/world_'+district,table([ref('chests/'+n,w) for n,w in weights.items()]))
 for district,weights in OFFICE.items():
  assert sum(weights.values())==100
  for state in ('ransacked','normal','stocked'):put('districts/office_'+district+'_'+state,table([ref('office/'+n+'_'+state,w) for n,w in weights.items()]))
  put('districts/office_'+district,table([ref('districts/office_'+district+'_'+s,w) for s,w in [('ransacked',50),('normal',40),('stocked',10)]]))
 put('chests/world_mix',table([regional('world',split)]))
 original_office['pools'][1:]=table([regional('office',split)])['pools'];put('chests/crate_office',original_office)
 meta=json.loads(files['pack.mcmeta']);meta['pack']['description']=f'Mosslorn: distritos norte/sur (Z={split}), medicina y provisiones garantizadas';files['pack.mcmeta']=json.dumps(meta,indent=2).encode()
 ids={n.removeprefix('data/mosslorn/loot_table/').removesuffix('.json') for n in files if n.startswith('data/mosslorn/loot_table/')}
 def walk(o):
  if isinstance(o,dict):
   yield o
   for v in o.values():yield from walk(v)
  elif isinstance(o,list):
   for v in o:yield from walk(v)
 checks=0
 for n,b in files.items():
  if not n.endswith('.json'):continue
  for e in walk(json.loads(b)):
   if e.get('type')=='minecraft:loot_table' and e['value'].startswith('mosslorn:'):assert e['value'][9:] in ids;checks+=1
 # Validate the actual generated location predicates and unconditional fallback.
 for kind in ('world','office'):
  children=regional(kind,split)['children']
  for z,expected in [(split-1,0),(split,1),(split+1,1),(None,2)]:
   matched=[]
   for index,entry in enumerate(children):
    conditions=entry.get('conditions',[])
    if not conditions:matched.append(index);continue
    bounds=conditions[0]['predicate']['position']['z']
    if z is not None and bounds.get('min',float('-inf'))<=z<=bounds.get('max',float('inf')):matched.append(index)
   assert matched[0]==expected
 for n in files:
  if n.startswith('data/mosslorn/loot_table/chests/') and not n.endswith('/world_mix.json'):
   p=json.loads(files[n])['pools'][0];assert not p.get('conditions') and p['rolls']['min']==1
 target=R/'mosslorn-expanded.4-districts.zip'
 with zipfile.ZipFile(target,'w',zipfile.ZIP_DEFLATED) as z:
  for n,b in sorted(files.items()):
   info=zipfile.ZipInfo(n,(2026,10,10,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,b)
 (R/'validation.json').write_text(json.dumps({'split_z':split,'boundary_is_provisional':True,'world_weights':WORLD,'office_weights':OFFICE,'tables':len(ids),'reference_checks':checks,'guaranteed_categories':18,'minecraft_runtime_tested':False,'sha256':hashlib.sha256(target.read_bytes()).hexdigest()},indent=2))
 print('PASS',len(ids),'tablas,',checks,'referencias; límite Z=',split)
if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('--split-z',type=int,default=240);a=p.parse_args();build(a.split_z)
