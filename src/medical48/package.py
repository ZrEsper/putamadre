from pathlib import Path
import zipfile,json,tomllib,hashlib,random,io
from PIL import Image,ImageDraw
r=Path(__file__).parent;out=r/'release';out.mkdir(exist_ok=True)
# A deterministic woven gauze atlas: left wound material, right cloth.
im=Image.new('RGBA',(128,64),(0,0,0,0));px=im.load();randomizer=random.Random(48)
for y in range(64):
 for x in range(64,128):
  grain=randomizer.randint(-5,5);weave=-18 if x%4==0 or y%4==0 else 0;edge=-20 if y<3 or y>60 else 0;shade=weave+edge+grain;px[x,y]=(max(0,245+shade),max(0,240+shade),max(0,222+shade),255)
d=ImageDraw.Draw(im);d.ellipse((4,6,58,58),fill=(183,65,72,255));d.polygon([(18,17),(38,11),(46,27),(39,47),(25,53),(14,35)],fill=(103,27,35,255));d.line((24,17,29,43),fill=(57,14,22,255),width=5)
for y in (5,57):d.line((64,y,127,y),fill=(191,182,158,255),width=1)
b=io.BytesIO();im.save(b,format='PNG')
names={'gore':'zomboid-zombie-gore-1.0.20-neoforge-1.21.1-remains-local-cuts.32.jar','rag':'ragdollified-1.21.1-1.1.5-stable-impacts-remains.25.jar','npc':'tacznpcs-2.1.0-1.21.1-tactical.32-corpse-guard.jar'}
entries=[]
for kind,name in names.items():
 with zipfile.ZipFile(r/'output'/f'{kind}.jar')as z:data={n:z.read(n)for n in z.namelist()}
 meta=data['META-INF/neoforge.mods.toml'].decode()
 if kind=='gore':
  meta=meta.replace('version="1.0.20.46"','version="1.0.20.48"').replace('Gore .31','Gore .32');data['assets/zomboid_gore/textures/entity/medical45.png']=b.getvalue();data['dev/zomboid/gore/remains48.txt']=(r/'resources/dev/zomboid/gore/remains48.txt').read_bytes()
  cfg=json.loads(data['zomboid_gore.mixins.json']);cfg['client'].append('ArmorLayer48');cfg['client'].remove('CorpseBullet13');cfg['mixins'].append('CorpseBullet13');data['zomboid_gore.mixins.json']=(json.dumps(cfg,indent=2)+'\n').encode()
  sounds=json.loads(data['assets/zomboid_gore/sounds.json']);assert 'blood_splash28' in sounds
 if kind=='rag':meta=meta.replace('1.1.5-gore20-medical45','1.1.5-gore20-medical48').replace('[1.0.20.45,1.0.21)','[1.0.20.48,1.0.21)')
 if kind=='npc':meta=meta.replace('2.1.0-tactical.31-single-corpse','2.1.0-tactical.32-corpse-guard')
 data['META-INF/neoforge.mods.toml']=meta.encode();parsed=tomllib.loads(meta);assert not any(n.startswith('net/minecraft/')for n in data)
 with zipfile.ZipFile(out/name,'w',zipfile.ZIP_DEFLATED)as z:
  for n,v in sorted(data.items()):z.writestr(n,v)
 with zipfile.ZipFile(out/name)as z:assert z.testzip()is None;assert len(z.namelist())==len(set(z.namelist()))
 entries.append({'file':name,'mod_id':parsed['mods'][0]['modId'],'version':parsed['mods'][0]['version'],'sha256':hashlib.sha256((out/name).read_bytes()).hexdigest()})
(out/'manifest.json').write_text(json.dumps({'minecraft':'1.21.1','loader':'NeoForge','minecraft_runtime_tested':False,'artifacts':entries},indent=2)+'\n');(out/'SHA256SUMS.txt').write_text(''.join(e['sha256']+'  mods/'+e['file']+'\n'for e in entries))
for f in ['LEEME.md','test-results.json']:(out/f).write_bytes((r/f).read_bytes())
archive=out/'Cadaveres-Impactos-Vendas-48.zip'
with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED)as z:
 for e in entries:z.write(out/e['file'],'mods/'+e['file'])
 for f in ['LEEME.md','manifest.json','test-results.json','SHA256SUMS.txt']:z.write(out/f,f)
Path('/workspace/outputs').mkdir(exist_ok=True);(Path('/workspace/outputs')/archive.name).write_bytes(archive.read_bytes());print(archive,archive.stat().st_size,hashlib.sha256(archive.read_bytes()).hexdigest())
