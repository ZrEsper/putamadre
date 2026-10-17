from pathlib import Path
import hashlib,json,zipfile,re,shutil
r=Path(__file__).resolve().parent
p=r/'base/fracture8.jar'
if not p.exists():shutil.copy2(next(Path('/workspace/attachments').rglob('*hybrid-entity-mask.8.jar')),p)
hash0=hashlib.sha256(p.read_bytes()).hexdigest()
inputfile=r/'inputs.json'
if inputfile.exists():assert json.loads(inputfile.read_text())['fracture8.jar']==hash0
else:inputfile.write_text(json.dumps({'fracture8.jar':hash0},indent=2)+'\n')
with zipfile.ZipFile(p)as z:e={n:z.read(n)for n in z.namelist()}
changed=[]
for n in list(e):
 if n.startswith('assets/fracturepoint/shaders/program/') and n.endswith('.fsh') and not n.endswith('/scene-brightness.fsh'):
  s=e[n].decode();assert 'DiffuseSampler' in s and 'texCoord' in s
  s,count=re.subn(r'void\s+main\s*\(\s*\)\s*\{', '''void main(){
 // Explicit laser geometry stamp; never infer heat or laser hue from terrain RGB.
 vec4 laserPixel32=texture(DiffuseSampler,texCoord);
 if(abs(laserPixel32.a-251.0/255.0)<0.0015){fragColor=vec4(laserPixel32.rgb,1.0);return;}
''',s,count=1);assert count==1,n;e[n]=s.encode();changed.append(n)
assert len(changed)==8
out=r/'output/fracturepoint-1.21.1-3.0.0-PT2-nvg-laser-colors.9.jar'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(p)as a,zipfile.ZipFile(out)as b:
 assert sorted(n for n in a.namelist()if a.read(n)!=b.read(n))==sorted(changed)
 assert set(a.namelist())==set(b.namelist()) and b.testzip()is None
(r/'validation.json').write_text(json.dumps({'output':out.name,'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed_only':changed,'minecraft_runtime_tested':False},indent=2)+'\n')
print('FRACTURE9_EIGHT_FILTERS_PATCHED_PRESERVING_ALL_OTHER_ASSETS')
