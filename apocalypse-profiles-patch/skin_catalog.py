"""Inspect actual TZP pixels offline. Conservative outfit heuristics; every match is editable."""
import pathlib,zipfile,hashlib,io,json
from PIL import Image

def generate(root,source):
 z=zipfile.ZipFile(source);profiles={};analysis=[]
 for n in z.namelist():
  if '/zombie/zombie' not in n or not n.endswith('.png') or n.endswith('_e.png'):continue
  image=Image.open(io.BytesIO(z.read(n))).convert('RGBA').resize((64,64),Image.Resampling.NEAREST)
  torso=[p for p in image.crop((20,20,28,32)).get_flattened_data() if p[3]];legs=[p for p in image.crop((4,20,8,32)).get_flattened_data() if p[3]]
  def share(pixels,condition):return sum(condition(p)for p in pixels)/max(1,len(pixels))
  white=share(torso,lambda p:min(p[:3])>145 and max(p[:3])-min(p[:3])<45)
  orange=share(torso,lambda p:p[0]>100 and p[0]>p[1]*1.15 and p[1]>p[2]*1.8)
  green=lambda p:p[1]>p[0]*1.1 and p[1]>p[2]*1.25 and p[1]>35
  # Green shirt alone is not sufficient to call a zombie military.
  military=min(share(torso,green),share(legs,green))
  role='doctor' if white>.5 else 'construction_worker' if orange>.3 else 'military' if military>.42 else None
  digest=hashlib.sha256(z.read(n)).hexdigest();key='minecraft:'+n[len('assets/minecraft/'):]
  if role:profiles[key]=role+'|'+digest
  analysis.append({'texture':key,'role':role,'white_uniform':white,'orange_vest':orange,'green_uniform':military,'sha256':digest})
 p=root/'resources/apocalypse31';p.mkdir(parents=True,exist_ok=True)
 (p/'skin-profiles.properties').write_text('\n'.join(k.replace(':','\\:')+'='+v for k,v in sorted(profiles.items()))+'\n')
 (root/'skin-analysis.json').write_text(json.dumps({'source_sha256':hashlib.sha256(pathlib.Path(source).read_bytes()).hexdigest(),'method':'Offline clothing-color heuristics, editable mappings; unknown skins remain unclassified','textures':analysis},ensure_ascii=False,indent=2)+'\n')
 print('Known TZP appearance matches:',len(profiles),'of',len(analysis))
