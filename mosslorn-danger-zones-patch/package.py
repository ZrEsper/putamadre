from pathlib import Path
import zipfile,json,hashlib
r=Path(__file__).resolve().parent;base=r.parent/'mods/zomboid-hordes-1.8.3-neoforge-1.21.1-zombies-mutants-only.2.jar';out=r/'zomboid-hordes-1.8.3-neoforge-1.21.1-mosslorn-danger-zones.3.jar'
classes=Path('/workspace/danger-classes');add={p.relative_to(classes).as_posix():p.read_bytes() for p in classes.rglob('*') if p.is_file()}
with zipfile.ZipFile(base) as z,zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as w:
 for n in z.namelist():w.writestr(n,add.get(n,z.read(n)))
 for n,b in add.items():
  if n not in z.namelist():w.writestr(n,b)
with zipfile.ZipFile(base) as z,zipfile.ZipFile(out) as w:
 assert w.testzip() is None
 for n in z.namelist():
  if n!='dev/zomboid/hordes/ChunkPopulation.class':assert z.read(n)==w.read(n)
refs=0
with zipfile.ZipFile(r/'mosslorn-expanded.5-danger-zones.zip') as z:
 ids={n.removeprefix('data/mosslorn/loot_table/').removesuffix('.json') for n in z.namelist() if n.endswith('.json')}
 def walk(o):
  if isinstance(o,dict):
   yield o
   for v in o.values():yield from walk(v)
  elif isinstance(o,list):
   for v in o:yield from walk(v)
 for n in z.namelist():
  if not n.endswith('.json'):continue
  for e in walk(json.loads(z.read(n))):
   if e.get('type')=='minecraft:loot_table' and e['value'].startswith('mosslorn:'):assert e['value'][9:] in ids;refs+=1
 for name in ('world_mix','crate_office'):
  bounds=[e['predicate']['position']['z'] for e in walk(json.loads(z.read('data/mosslorn/loot_table/chests/'+name+'.json'))) if e.get('condition')=='minecraft:location_check' and set(e['predicate']['position'])=={'z'}];assert {'max':191.999999} in bounds and {'min':192} in bounds
 for n in z.namelist():
  if n.startswith('data/mosslorn/loot_table/chests/') and not n.endswith('/world_mix.json'):
   p=json.loads(z.read(n))['pools'][0];assert not p.get('conditions') and p['rolls']['min']==1
(r/'validation.json').write_text(json.dumps({'reference_checks':refs,'helper_cases':11,'unrelated_jar_entries_preserved':True,'district_boundary_z':192,'zones':len(json.loads((r/'zones.json').read_text())['zones']),'minecraft_runtime_tested':False,'sha256':{p.name:hashlib.sha256(p.read_bytes()).hexdigest() for p in [out,r/'mosslorn-expanded.5-danger-zones.zip']}},indent=2))
print('PASS:',refs,'references, Z=192, guaranteed provisions, unrelated JAR entries preserved')
