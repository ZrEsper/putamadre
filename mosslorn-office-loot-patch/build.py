"""Amplía crate_office; conserva intactas las demás tablas del pack base."""
from pathlib import Path
import json,zipfile,copy,hashlib
R=Path(__file__).resolve().parent
BASE=R.parent/'mosslorn-loot-patch/mosslorn-expanded.1.zip'
WEIGHTS={'office':28,'medical':15,'electronics':10,'maintenance':10,'civilian_clothes':8,'breakroom':8,'research':6,'camping':5,'library':4,'gardening':3,'emergency':3}
STATES={'ransacked':(50,.2,1),'normal':(40,.65,2),'stocked':(10,1,3)}
def item(n,w=1):return {'type':'minecraft:item','name':n,'weight':w}
def vanilla(*names):return [item('minecraft:'+n) for n in names]
def walk(x):
 if isinstance(x,dict):
  yield x
  for v in x.values():yield from walk(v)
 elif isinstance(x,list):
  for v in x:yield from walk(v)
def ref(path,w):return {'type':'minecraft:loot_table','value':'mosslorn:office/'+path,'weight':w}
def table(entries,chance=1,cap=1):
 p={'rolls':{'type':'minecraft:uniform','min':1,'max':cap},'entries':entries}
 if chance<1:p['conditions']=[{'condition':'minecraft:random_chance','chance':chance}]
 return {'type':'minecraft:chest','pools':[p]}
def build():
 with zipfile.ZipFile(BASE) as z:files={n:z.read(n) for n in z.namelist()}
 catalog={}
 for n,b in files.items():
  if n.endswith('.json'):
   for e in walk(json.loads(b)):
    if e.get('type')=='minecraft:item':catalog.setdefault(e['name'],copy.deepcopy(e))
 def known(*names):return [copy.deepcopy(catalog[n]) for n in names]
 profiles={
 'office':vanilla('paper','book','feather','ink_sac','glass_bottle','string'),
 'medical':known('firstaid:bandage','firstaid:plaster','firstaid:cloth','firstaid:painkillers','firstaid:splint','okadditions:ice_pack','okadditions:smelling_salts'),
 'electronics':vanilla('redstone','copper_ingot','iron_nugget','repeater','comparator','clock')+known('okadditions:mounted_light'),
 'maintenance':vanilla('iron_nugget','string','leather','chain','torch')+known('okadditions:crowbar','okadditions:iron_rod'),
 'civilian_clothes':known('luckyswardrobe:hat','luckyswardrobe:scarf','luckyswardrobe:wool_vest','luckyswardrobe:entertainers_shirt','luckyswardrobe:entertainers_pants','luckyswardrobe:entertainers_shoes'),
 'breakroom':vanilla('bread','apple','bowl')+known('farmersdelight:hot_cocoa','farmersdelight:honey_cookie','farmersdelight:sweet_berry_cookie','farmersdelight:apple_cider','farmersdelight:milk_bottle'),
 'research':vanilla('glass_bottle','paper','book','redstone','copper_ingot','amethyst_shard'),
 'camping':vanilla('compass','torch','coal','string','leather')+known('okadditions:binoculars'),
 'library':vanilla('book','paper','ink_sac','feather','writable_book'),
 'gardening':vanilla('wheat_seeds','beetroot_seeds','bone_meal','flower_pot','carrot','potato'),
 'emergency':vanilla('torch','compass','bread')+known('firstaid:bandage','firstaid:plaster','okadditions:energy_drink')}
 # Small stacks even for entries copied from the original pack.
 for es in profiles.values():
  for e in es:
   e['weight']=1
   for f in e.get('functions',[]):
    if f.get('function')=='minecraft:set_count':f['count']={'type':'minecraft:uniform','min':1,'max':2}
 def save(path,d):files['data/mosslorn/loot_table/'+path+'.json']=(json.dumps(d,indent=2)+'\n').encode()
 for state,(weight,chance,cap) in STATES.items():
  for profile,es in profiles.items():save('office/'+profile+'_'+state,table(es,chance,cap))
  save('office/select_'+state,table([ref(p+'_'+state,w) for p,w in WEIGHTS.items()]))
 save('chests/crate_office',table([ref('select_'+s,v[0]) for s,v in STATES.items()]))
 # Guaranteed modest provisions for every container context, even when bonus pools fail.
 guaranteed=table(vanilla('bread','apple','carrot','potato','paper','string'))['pools'][0]
 for n in list(files):
  if n.startswith('data/mosslorn/loot_table/chests/') and n.endswith('.json') and not n.endswith('/world_mix.json'):
   d=json.loads(files[n]);d['pools'].insert(0,copy.deepcopy(guaranteed));files[n]=(json.dumps(d,indent=2)+'\n').encode()
 meta=json.loads(files['pack.mcmeta']);meta['pack']['description']='Mosslorn ampliado: Office Cabinet con 11 perfiles y suministros médicos — 1.21.1';files['pack.mcmeta']=json.dumps(meta,indent=2).encode()
 ids={n.removeprefix('data/mosslorn/loot_table/').removesuffix('.json') for n in files if n.startswith('data/mosslorn/loot_table/') and n.endswith('.json')}
 checks=0
 for n,b in files.items():
  if not n.endswith('.json'):continue
  for e in walk(json.loads(b)):
   if e.get('type')=='minecraft:loot_table' and e.get('value','').startswith('mosslorn:'):assert e['value'].split(':',1)[1] in ids;checks+=1
   if e.get('type')=='minecraft:item':assert e['name'].startswith('minecraft:') or e['name'] in catalog;checks+=1
 assert sum(WEIGHTS.values())==sum(v[0] for v in STATES.values())==100
 # Base variants and world_mix remain unchanged; all context selectors gain provisions.
 with zipfile.ZipFile(BASE) as z:
  for n in z.namelist():
   if n!='pack.mcmeta' and not n.startswith('data/mosslorn/loot_table/chests/'):assert files[n]==z.read(n)
   if n.startswith('data/mosslorn/loot_table/chests/') and not n.endswith('/world_mix.json'):
    guaranteed_pool=json.loads(files[n])['pools'][0]
    assert not guaranteed_pool.get('conditions') and guaranteed_pool['rolls']['min']==1
    assert all(e['type']=='minecraft:item' and e['name'].startswith('minecraft:') and not e.get('conditions') for e in guaranteed_pool['entries'])
 target=R/'mosslorn-expanded.3-office.zip'
 with zipfile.ZipFile(target,'w',zipfile.ZIP_DEFLATED) as z:
  for n,b in sorted(files.items()):
   info=zipfile.ZipInfo(n,(2026,10,10,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,b)
 (R/'validation.json').write_text(json.dumps({'tables':len(ids),'profiles':WEIGHTS,'states':STATES,'checks':checks,'base_variants_unchanged':True,'guaranteed_provisions_contexts':18,'minecraft_runtime_tested':False,'sha256':hashlib.sha256(target.read_bytes()).hexdigest()},indent=2))
 print('PASS:',len(ids),'tablas;',checks,'referencias e IDs; provisiones garantizadas en 18 categorías')
if __name__=='__main__':build()
