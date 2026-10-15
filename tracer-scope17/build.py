from pathlib import Path
import sys,os,json,zipfile,hashlib,subprocess,io,random,math
from PIL import Image,ImageDraw
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a,**kw):return subprocess.run(list(map(str,a)),check=True,**kw)
source=Path(sys.argv[1]).resolve();tactical=next(Path('base').glob('*.jar'));expected=json.loads(Path('inputs.sha256.json').read_text());assert sha(source)==expected['tacz'];assert sha(tactical)==expected['tactical']
for n,h in {'ecj':'01f5a92ac19bb2b3bf85e295a68f2c73c264369109158b566ce9b490af982948','asm':'8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281','asm-tree':'9929881f59eb6b840e86d54570c77b59ce721d104e6dfd7a40978991c2d3b41f','asm-analysis':'85b29371884ba31bb76edf22323c2c24e172c3267a67152eba3d1ccc2e041ef2'}.items():assert sha(t/(n+'.jar'))==h
for d in ['build/tool','output']:Path(d).mkdir(parents=True,exist_ok=True)
compiler=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];cp=os.pathsep.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);run(*compiler,'-cp',cp,'-d','build/tool','Patch.java')
report={}
for kind,base,pkg,name in [('tacz',source,'dev/zomboid/tacz','tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.17-tracers-impacts-scope-limits.jar'),('tactical',tactical,'dev/tactical/tacz','tacz-tactical-1.2.4-neoforge-1.21.1-tracers-impacts-scope-limits.1.jar')]:
 classes=Path('build')/kind;classes.mkdir(exist_ok=True);run(*compiler,'-cp',base,'-d',classes,*Path('src',pkg).glob('*.java'));out=Path('output')/name;run('java','-cp',cp+os.pathsep+'build/tool','Patch',base,out,classes,kind)
 with zipfile.ZipFile(out)as z:entries={n:z.read(n)for n in z.namelist()}
 if kind=='tacz':
  sprites=['bullet_hole']+['bullet_hole_'+c for c in ['pistol','rifle','heavy','shotgun']];atlas=json.loads(entries.get('assets/minecraft/atlases/particles.json',b'{"sources":[]}'));atlas['sources'] += [{'type':'minecraft:single','resource':'tacz:particle/'+s,'sprite':'tacz:'+s}for s in sprites];entries['assets/minecraft/atlases/particles.json']=json.dumps(atlas).encode()
  for index,c in enumerate(['pistol','rifle','heavy','shotgun']):
   img=Image.new('RGBA',(128,128),(0,0,0,0));draw=ImageDraw.Draw(img);rng=random.Random(1700+index);radius=[19,24,31,21][index]
   for j in range(8):
    angle=j*math.tau/8+rng.random()*.3;length=radius+rng.randrange(12,25);x=64+math.cos(angle)*length;y=64+math.sin(angle)*length;draw.line([(64,64),(x,y)],fill=(32,28,23,170),width=3 if j%2 else 5)
   draw.ellipse((64-radius-7,64-radius-7,64+radius+7,64+radius+7),fill=(54,47,40,120));draw.ellipse((64-radius,64-radius,64+radius,64+radius),fill=(21,19,17,230));draw.ellipse((64-radius*.62,64-radius*.62,64+radius*.62,64+radius*.62),fill=(5,5,5,255));draw.arc((64-radius-2,64-radius-2,64+radius+2,64+radius+2),20,130,fill=(125,115,97,150),width=4)
   img=img.resize((32,32),Image.Resampling.LANCZOS);buf=io.BytesIO();img.save(buf,format='PNG');entries['assets/tacz/textures/particle/bullet_hole_'+c+'.png']=buf.getvalue()
 else:
  n='tacz_tactical.mixins.json';data=json.loads(entries[n]);assert data['client'].count('HoleSpriteMixin')==1;data['client'].remove('HoleSpriteMixin');entries[n]=json.dumps(data,indent=2).encode()
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
  for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
 with zipfile.ZipFile(base)as a,zipfile.ZipFile(out)as b:
  assert b.testzip()is None and set(a.namelist())<=set(b.namelist());assert a.read('META-INF/neoforge.mods.toml')==b.read('META-INF/neoforge.mods.toml');changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));added=sorted(set(b.namelist())-set(a.namelist()));assert all(not n.startswith('net/minecraft/')for n in added)
  for n in added+changed:
   if n.endswith('.json'):json.loads(b.read(n))
 report[kind]={'output':out.name,'input_sha256':sha(base),'output_sha256':sha(out),'changed':changed,'added':added,'minecraft_runtime_tested':False}
if Path('expected-changes.json').exists():assert json.loads(Path('expected-changes.json').read_text())=={k:{x:v[x]for x in ['changed','added']}for k,v in report.items()}
Path('validation.json').write_text(json.dumps(report,indent=2)+'\n')
