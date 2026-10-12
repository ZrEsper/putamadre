from pathlib import Path
import zipfile,json,sys,hashlib
from models import resources
pack=Path(sys.argv[1]);out=Path(__file__).resolve().parent/'TZP-1.21.1-Gore.2-cached-injuries.zip'
with zipfile.ZipFile(pack) as z:data={n:z.read(n) for n in z.namelist() if not n.endswith('/') and not n.lower().endswith('thumbs.db')}
# Remove prior connected zombie CEM and its animation include; retain every original texture/sound.
for n in list(data):
 if n.startswith('assets/minecraft/optifine/cem/') and n.endswith(('.jem','.jpm','.properties')):del data[n]
resources(data);data['pack.mcmeta']=json.dumps({'pack':{'pack_format':34,'description':'TZP textures + synced injuries for Zomboid Gore; cached model flags'}}).encode()
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
 for n,b in data.items():z.writestr(n,b)
with zipfile.ZipFile(pack) as a,zipfile.ZipFile(out) as b:
 assert b.testzip() is None
 original=[n for n in a.namelist() if n.endswith(('.png','.ogg'))];assert all(a.read(n)==b.read(n) for n in original)
 models=[n for n in b.namelist() if n.endswith('.jem')]
 for n in models:
  model=json.loads(b.read(n));assert len(model['models'])==7
  variables=model['models'][0]['animations'][0]
  assert all(f'var.{prefix}{i}' in variables for prefix in ['l','w','e'] for i in range(8))
 print('Preserved textures/sounds:',len(original),'cached injury models:',len(models))
Path(__file__).with_name('validation.json').write_text(json.dumps({'source_sha256':hashlib.sha256(pack.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'original_images_and_sounds_preserved':len(original),'model_variants':len(models),'minecraft_runtime_tested':False},indent=2)+'\n')
