from pathlib import Path
import zipfile,json,hashlib,re,tomllib,io
from PIL import Image,ImageDraw
r=Path(__file__).resolve().parent;dest=Path('/workspace/outputs');dest.mkdir(parents=True,exist_ok=True);release=r/'release';release.mkdir(exist_ok=True)
names={
'fa':'firstaid-1.21.1-ZOMBOID-v3.6.15-procedural-visible-wounds.jar',
'compat':'tacz-firstaid-compat-1.0.3-impact-coordinates.jar',
'gore':'zomboid-zombie-gore-1.0.20-neoforge-1.21.1-medical-corpse.30.jar',
'rag':'ragdollified-1.21.1-1.1.5-single-corpse.24.jar',
'corpse':'corpse-neoforge-1.21.1-1.1.13-ragdoll-injuries.5.jar',
'npc':'tacznpcs-2.1.0-1.21.1-tactical.31-single-corpse.jar'}
old='1.3.3+neoforge1.21.1-zomboid-v3.6.14-unified-medical-care';new='1.3.3+neoforge1.21.1-zomboid-v3.6.15-procedural-visible-wounds'
im=Image.new('RGBA',(32,16));draw=ImageDraw.Draw(im);draw.polygon([(7,2),(10,3),(11,6),(14,7),(12,10),(10,11),(8,14),(5,11),(2,10),(4,7),(3,4)],fill=(190,124,128,255));draw.rectangle((6,5,9,11),fill=(106,26,33,255));draw.rectangle((7,6,8,9),fill=(48,12,17,255));draw.rectangle((16,0,31,15),fill=(250,248,239,255));
for y in (2,6,10,14):draw.line((16,y,31,y),fill=(195,188,167,255))
atlas=io.BytesIO();im.save(atlas,format='PNG');manifest=[]
for key,name in names.items():
 with zipfile.ZipFile(r/'output'/f'{key}.jar')as z:
  assert z.testzip()is None
  data={n:z.read(n)for n in z.namelist()}
 meta=data['META-INF/neoforge.mods.toml'].decode()
 if key=='fa':meta=meta.replace(old,new).replace('v3.6.14 · Salud unificada','v3.6.15 · Heridas visibles')
 if key=='compat':meta=meta.replace('version="1.0.2"','version="1.0.3"').replace(old,new)
 if key=='gore':meta=meta.replace('version="1.0.20"','version="1.0.20.45"').replace('Gore .29','Gore .30');data['assets/zomboid_gore/textures/entity/medical45.png']=atlas.getvalue()
 if key=='rag':meta=meta.replace('version="1.1.5-gore20"','version="1.1.5-gore20-medical45"').replace('[1.0.20,1.0.21)','[1.0.20.45,1.0.21)')
 if key=='npc':meta=meta.replace('2.1.0-tactical.29-committed-decisions','2.1.0-tactical.31-single-corpse')
 if key=='corpse':meta+='\n[[dependencies.corpse]]\nmodId="zomboid_gore"\ntype="required"\nversionRange="[1.0.20.45,1.0.21)"\nordering="AFTER"\nside="BOTH"\n'
 data['META-INF/neoforge.mods.toml']=meta.encode();parsed=tomllib.loads(meta)
 with zipfile.ZipFile(release/name,'w',zipfile.ZIP_DEFLATED)as z:
  for n,b in sorted(data.items()):z.writestr(n,b)
 with zipfile.ZipFile(release/name)as z:assert z.testzip()is None;assert len(z.namelist())==len(set(z.namelist()))
 manifest.append({'file':name,'modid':parsed['mods'][0]['modId'],'version':parsed['mods'][0]['version'],'sha256':hashlib.sha256((release/name).read_bytes()).hexdigest()})
(release/'manifest.json').write_text(json.dumps({'minecraft':'1.21.1','loader':'NeoForge','minecraft_runtime_tested':False,'artifacts':manifest},indent=2)+'\n')
checks=json.loads((r/'test-results.json').read_text());(release/'test-results.json').write_text(json.dumps(checks,indent=2)+'\n')
(release/'LEEME.md').write_bytes((r/'LEEME.md').read_bytes())
(release/'SHA256SUMS.txt').write_text(''.join(x['sha256']+'  mods/'+x['file']+'\n'for x in manifest))
archive=dest/'Heridas-Procedurales-Cadaver-Unico-45.zip'
with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED)as z:
 for name in names.values():z.write(release/name,'mods/'+name)
 for name in ['LEEME.md','manifest.json','test-results.json','SHA256SUMS.txt']:z.write(release/name,name)
with zipfile.ZipFile(dest/'Heridas-Cadaver-Unico-45-Fuentes.zip','w',zipfile.ZIP_DEFLATED)as z:
 for d in ['src','tests']:
  for p in (r/d).rglob('*.java'):z.write(p,p.relative_to(r).as_posix())
 for name in ['Patch45.java','Verify45.java','ModuleCheck45.java','build.py','test.py','package.py','inputs.json','input-hashes.json','LEEME.md','test-results.json']:z.write(r/name,name)
print(archive,archive.stat().st_size,hashlib.sha256(archive.read_bytes()).hexdigest())
