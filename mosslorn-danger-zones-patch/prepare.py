from pathlib import Path
import csv,json,collections,zipfile,copy
R=Path(__file__).resolve().parent
zones=[dict(name='central_nuclear',min_x=-646,max_x=-454,min_z=-714,max_z=-522,multiplier=3,bonus=.65),dict(name='piramide',min_x=-38,max_x=218,min_z=-613,max_z=-357,multiplier=2,bonus=.4)]
# Tall, densely stocked chunks are candidates only, not automatic danger zones.
p=Path('/workspace/map-analysis/containers.csv');candidate=[]
if p.exists():
 groups=collections.defaultdict(list)
 for c in csv.DictReader(p.open()):groups[(int(c['x'])//16,int(c['z'])//16)].append(int(c['y']))
 candidate=[dict(chunk_x=x,chunk_z=z,containers=len(ys),height_span=max(ys)-min(ys)) for (x,z),ys in groups.items() if len(ys)>=30 and max(ys)-min(ys)>=50]
 # Infer tall developed chunks from above-ground container positions; explicit landmarks take priority.
 tall=[]
 for (x,z),ys in groups.items():
  above=[y for y in ys if y>=60]
  if len(above)>=30 and max(above)-min(above)>=50:
   tall.append(dict(name='edificios_altos',min_x=x*16,max_x=x*16+15,min_z=z*16,max_z=z*16+15,multiplier=2,bonus=.35))
 zones.extend(tall)
(R/'zones.json').write_text(json.dumps({'zones':zones,'bounds_are_estimated':True,'district_boundary_z':192,'bridge':[93,73,192],'coordinates_source':'user: pyramid summit 90 165 -485; nuclear exterior fence -550 65 -618','tall_structure_detection':'At least 30 container positions at Y>=60 with >=50 blocks vertical span in a chunk. Approximation, not building recognition.', 'tall_chunks_activated':len(tall),'tall_structure_candidates':candidate},indent=2))
(R/'zones.csv').write_text('\n'.join(','.join(str(z[k]) for k in ['min_x','max_x','min_z','max_z','multiplier']) for z in zones)+'\n')
base=R.parent/'mosslorn-district-loot-patch/mosslorn-expanded.4-districts.zip'
with zipfile.ZipFile(base) as z:files={n:z.read(n) for n in z.namelist()}
def put(path,d):files['data/mosslorn/loot_table/'+path+'.json']=(json.dumps(d,indent=2)+'\n').encode()
# User supplied actual bridge coordinate: canal boundary Z=192.
for name in ('world_mix','crate_office'):
 key='data/mosslorn/loot_table/chests/'+name+'.json';d=json.loads(files[key])
 def change(o):
  if isinstance(o,dict):
   if o.get('condition')=='minecraft:location_check':
    z=o.get('predicate',{}).get('position',{}).get('z',{})
    if z.get('max')==239.999999:z['max']=191.999999
    if z.get('min')==240:z['min']=192
   for v in o.values():change(v)
  elif isinstance(o,list):
   for v in o:change(v)
 change(d);files[key]=(json.dumps(d,indent=2)+'\n').encode()
def ref(p,w=1):return dict(type='minecraft:loot_table',value='mosslorn:'+p,weight=w)
def table(entries,chance=None):
 p=dict(rolls=1,entries=entries)
 if chance is not None:p['conditions']=[dict(condition='minecraft:random_chance',chance=chance)]
 return dict(type='minecraft:chest',pools=[p])
# Bonuses contain medicine, tools and electronics; no reroll of the same guaranteed supplies.
weights={'central_nuclear':{'crate_pharmacy':30,'crate_electronics':25,'crate_mechanic':20,'crate_workshop':15,'crate_military':10},'piramide':{'crate_pharmacy':30,'crate_electronics':25,'crate_office':25,'crate_workshop':20},'edificios_altos':{'crate_pharmacy':25,'crate_electronics':25,'crate_office':25,'crate_workshop':20,'crate_police':5}}
for zone in zones:
 es=[ref('variants/'+n+'_stocked',w) for n,w in weights[zone['name']].items()];assert sum(weights[zone['name']].values())==100
 put('danger/'+zone['name'],table(es,zone['bonus']))
# Append one spatial bonus to each context, not to world_mix (which delegates).
for n in list(files):
 if not n.startswith('data/mosslorn/loot_table/chests/') or n.endswith('/world_mix.json'):continue
 d=json.loads(files[n]);entries=[]
 for zone in zones:
  e=ref('danger/'+zone['name']);e['conditions']=[{'condition':'minecraft:location_check','predicate':{'position':{'x':{'min':zone['min_x'],'max':zone['max_x']+1-1e-6},'z':{'min':zone['min_z'],'max':zone['max_z']+1-1e-6}}}}];entries.append(e)
 # Outside danger zones the empty alternative adds nothing; minimum supplies remain intact.
 entries.append({'type':'minecraft:empty'});d['pools'].append({'rolls':1,'entries':[{'type':'minecraft:alternatives','children':entries}]});files[n]=(json.dumps(d,indent=2)+'\n').encode()
meta=json.loads(files['pack.mcmeta']);meta['pack']['description']='Mosslorn: distritos y zonas de peligro, nuclear y pirámide';files['pack.mcmeta']=json.dumps(meta).encode()
with zipfile.ZipFile(R/'mosslorn-expanded.5-danger-zones.zip','w',zipfile.ZIP_DEFLATED) as z:
 for n,b in sorted(files.items()):z.writestr(n,b)
print('Prepared',len(zones),'zones;',len(candidate),'vertical candidates reviewed; tall chunks inferred from above-ground containers')
