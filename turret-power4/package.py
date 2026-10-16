from pathlib import Path
import json,hashlib,zipfile,shutil
r=Path(__file__).resolve().parent;root=r.parent
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
report=json.loads((r/'validation.json').read_text());entries={};recipe_checks=0
for kind,v in report.items():
 p=r/'output'/v['output'];assert sha(p)==v['sha256'];shutil.copyfile(p,root/'mods'/p.name);entries['mods/'+p.name]=p.read_bytes()
 with zipfile.ZipFile(p)as z:
  assert z.testzip()is None
  for n in z.namelist():
   assert not n.startswith('net/minecraft/')
   if n.endswith('.json'):json.loads(z.read(n))
  if kind=='decor':
   specs=json.loads(z.read('doomsday_decoration/block_specs.json'));boards=[s['id']for s in specs if s['id'].startswith('planking_')];assert len(boards)>1
   for b in boards:
    recipe=json.loads(z.read('data/doomsday_decoration/recipe/'+b+'.json'));assert recipe['result']['id']=='doomsday_decoration:'+b;recipe_checks+=1
   base=json.loads(z.read('data/doomsday_decoration/recipe/planking_0.json'));assert base['result']['count']==6 and base['pattern']==['PPP'] and base['key']['P']['tag']=='minecraft:planks'
   for b in ['bed','medical_bed']:
    recipe=json.loads(z.read('data/doomsday_decoration/recipe/'+b+'.json'));assert recipe['key']['W']['tag']=='minecraft:wool' and sum(row.count('W')for row in recipe['pattern'])==3;recipe_checks+=1
  if kind=='turret':
   sounds=json.loads(z.read('assets/tacz_turrets/sounds.json'))
   for s in ['servo_idle','target_alert']:assert s in sounds and z.read('assets/tacz_turrets/sounds/'+s+'.ogg').startswith(b'OggS')
hordes=root/'mods/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.43-infected-player-corpse.jar';assert sha(hordes)=='3a1e9de1323bcda95fa9294d4c417469b2885bca9bac33b202a9f0f366a61047';entries['mods/'+hordes.name]=hordes.read_bytes()
entries['LEEME.txt']=(r/'GUIA.txt').read_bytes();entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(entries.items())).encode()
out=root/'distribuciones/Zombies-Poldo-Torretas-Doomsday-4.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(out)as z:assert z.testzip()is None and len([n for n in z.namelist()if n.endswith('.jar')])==4
(r/'package-validation.json').write_text(json.dumps({'file':str(out.relative_to(root)),'sha256':sha(out),'jars':4,'recipes_verified':recipe_checks,'minecraft_runtime_tested':False},indent=2)+'\n')
print(out.name+': four JARs, '+str(recipe_checks)+' recipes checked, SHA-256 '+sha(out))
