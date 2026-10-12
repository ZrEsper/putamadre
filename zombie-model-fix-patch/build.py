from pathlib import Path
import sys,zipfile,json,hashlib
from models import resources
r=Path(__file__).resolve().parent;source=Path(sys.argv[1]);out=r/'TZP-1.21.1-Gore.3-normal-spawn-model-fix.zip'
with zipfile.ZipFile(source) as z:data={n:z.read(n) for n in z.namelist() if not n.endswith('/') and not n.lower().endswith('thumbs.db')}
for n in list(data):
 if n.startswith('assets/minecraft/optifine/cem/') and n.endswith(('.jem','.jpm','.properties')):del data[n]
resources(data)
data['pack.mcmeta']=json.dumps({'pack':{'pack_format':34,'description':'TZP + Zomboid Gore: corrected native pivots and top-level EMF injury animations'}}).encode()
checks=0
for name,raw in data.items():
 if not name.endswith('.jem'):continue
 d=json.loads(raw);animations=d['models'][0]['animations'];flat={k:v for a in animations for k,v in a.items()};ids=set()
 def inspect(node,root=False):
  global checks
  ids.add(node['id'])
  if not root:assert 'animations' not in node;checks+=1
  for child in node.get('submodels',[]):inspect(child)
 for node in d['models']:
  assert node['translate']==[0,0,0];checks+=1;inspect(node,True)
 for ident in ids:
  if ident.endswith('_char'):assert 'var.burn>=' in flat[ident+'.visible'];checks+=1
  if ident.endswith(('_fallen','_wound','_exit','_stump')):assert ident+'.visible' in flat;checks+=1
 # Legacy TZP limb textures use the right-hand regions with mirrored left parts.
 for node in d['models']:
  if node['part'] in ['left_arm','right_arm','left_leg','right_leg']:
   expected=[40,16] if 'arm' in node['part'] else [0,16]
   assert node['submodels'][0]['boxes'][0]['textureOffset']==expected;checks+=1
 with_original_geometry={node['part']:node['translate'] for node in d['models']}
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
 for n,b in data.items():z.writestr(n,b)
with zipfile.ZipFile(source) as a,zipfile.ZipFile(out) as b:
 assert b.testzip() is None
 originals=[n for n in a.namelist() if n.endswith(('.png','.ogg'))];assert all(a.read(n)==b.read(n) for n in originals)
(r/'validation.json').write_text(json.dumps({'source_sha256':hashlib.sha256(source.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'structural_checks':checks,'original_images_and_sounds_preserved':len(originals),'minecraft_runtime_tested':False},indent=2)+'\n')
print('EMF animation placement, zero local pivots and UV checks:',checks,'Original images/sounds preserved:',len(originals))
