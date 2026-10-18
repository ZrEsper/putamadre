from pathlib import Path
import subprocess,zipfile,hashlib,json
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools';base=r/'base/melee36.jar';pins={base.name:hashlib.sha256(base.read_bytes()).hexdigest()};p=r/'inputs.json'
if p.exists():assert json.loads(p.read_text())==pins
else:p.write_text(json.dumps(pins,indent=2)+'\n')
for n in ['build/classes','build/tool','output']:(r/n).mkdir(parents=True,exist_ok=True)
asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn']
subprocess.run([*ecj,'-cp',str(base)+':'+str(r.parent/'action34/build/api'),'-d',str(r/'build/classes'),*map(str,(r/'src').rglob('*.java'))],check=True)
subprocess.run([*ecj,'-cp',asm,'-d',str(r/'build/tool'),str(r/'Hooks.java')],check=True)
subprocess.run(['java','-cp',str(r/'build/tool')+':'+asm,'Hooks',str(r/'build/LateLaser37.class')],check=True)
with zipfile.ZipFile(base)as z:e={n:z.read(n)for n in z.namelist()}
for p in (r/'build/classes').rglob('*.class'):e[p.relative_to(r/'build/classes').as_posix()]=p.read_bytes()
e['dev/zomboidmelee/mixin/LateLaser37.class']=(r/'build/LateLaser37.class').read_bytes()
cfg=json.loads(e['zomboid_global_melee.mixins.json']);cfg['client'].append('LateLaser37');e['zomboid_global_melee.mixins.json']=json.dumps(cfg,indent=2).encode()
for p in (r/'shaders').glob('*'):e['assets/zomboid_global_melee/shaders/'+p.name]=p.read_bytes()
out=r/'output/zomboid-global-melee-animations-2.3.1-v37-late-world-depth-laser.jar'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(base)as a,zipfile.ZipFile(out)as b:
 assert b.testzip()is None
 changes=[n for n in a.namelist()if a.read(n)!=b.read(n)]
 assert set(changes)=={'dev/zomboidmelee/boxing/Laser32.class','zomboid_global_melee.mixins.json'}
(r/'validation.json').write_text(json.dumps({'file':out.name,'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed':changes,'minecraft_runtime_tested':False},indent=2)+'\n')
