from pathlib import Path
import subprocess,hashlib,json,zipfile
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';src=r/'base/melee16.jar';out=r/'output/zomboid-global-melee-animations-2.3.1-range-plus-one-ezweight-v18-heavy-fists-camera-stagger.jar'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a):subprocess.run(list(map(str,a)),check=True)
for name,h in json.loads((root/'spas-reload18/inputs.json').read_text())['tools'].items():assert sha(t/(name+'.jar'))==h
assert sha(src)==json.loads((r/'inputs.json').read_text())['input_sha256']
for d in ['build/tools','build/api','build/classes','output']:(r/d).mkdir(parents=True,exist_ok=True)
cp=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];run(*ecj,'-cp',cp,'-d',r/'build/tools',r/'Patch.java');run(*ecj,'-d',r/'build/api',*(r/'compile-api').rglob('*.java'));run(*ecj,'-cp',str(src)+':'+str(r/'build/api'),'-d',r/'build/classes',*(r/'src').rglob('*.java'));run('java','-cp',str(r/'build/tools')+':'+cp,'Patch',src,out,r/'build/classes')
with zipfile.ZipFile(out)as z:e={n:z.read(n)for n in z.namelist()}
d=json.loads(e['zomboid_global_melee.mixins.json']);d['client']+=['BoxingHands17','BoxingCamera17','BoxingProne18'];d['mixins']+=['BoxingNoLunge17'];e['zomboid_global_melee.mixins.json']=json.dumps(d,indent=2).encode()
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(src)as a,zipfile.ZipFile(out)as b:
 assert b.testzip()is None;changed=[n for n in a.namelist()if a.read(n)!=b.read(n)];added=sorted(set(b.namelist())-set(a.namelist()));assert not any(n.startswith(('net/minecraft/','com/mojang/','org/joml/','yesman/'))for n in b.namelist())
(r/'validation.json').write_text(json.dumps({'output':out.name,'sha256':sha(out),'changed':changed,'added':added,'minecraft_runtime_tested':False},indent=2)+'\n')

gore=r/'base/gore7.jar';assert sha(gore)==json.loads((r/'gore-input.json').read_text())['sha256']
(r/'build/gore').mkdir(exist_ok=True)
run(*ecj,'-cp',gore,'-d',r/'build/gore',*(r/'gore-src').rglob('*.java'))
run(*ecj,'-cp',cp,'-d',r/'build/tools',r/'GorePatch.java')
gout=r/'output/zomboid-zombie-gore-1.0.8-neoforge-1.21.1-boxing-knockdown-stomp.jar'
run('java','-cp',str(r/'build/tools')+':'+cp,'GorePatch',gore,gout,r/'build/gore')
with zipfile.ZipFile(gout)as z:assert z.testzip()is None
(r/'gore-validation.json').write_text(json.dumps({'output':gout.name,'sha256':sha(gout),'minecraft_runtime_tested':False},indent=2)+'\n')
