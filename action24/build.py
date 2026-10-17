from pathlib import Path
import subprocess,json,hashlib,zipfile
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools';ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
def run(*a):subprocess.run(list(map(str,a)),check=True)
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
for n,h in json.loads((r.parent/'spas-reload18/inputs.json').read_text())['tools'].items():assert sha(t/(n+'.jar'))==h
assert sha(r/'base/melee20.jar')==json.loads((r/'inputs.json').read_text())['melee20.jar']
old=(r.parent/'action23/src/dev/zomboidmelee/boxing/Arms23.java').read_text();new=(r/'src/dev/zomboidmelee/boxing/Arms23.java').read_text();assert old[:old.index(' public static void first(')]==new[:new.index(' public static void first(')],'Do not alter accepted third-person guard'
for d in ['api','patch','classes']:(r/'build'/d).mkdir(parents=True,exist_ok=True)
(r/'output').mkdir(exist_ok=True)
run(*ecj,'-d',r/'build/api',*(r/'compile-api').rglob('*.java'))
run(*ecj,'-cp',str(r/'base/melee20.jar')+':'+str(r/'build/api'),'-d',r/'build/classes',*(r/'src').rglob('*.java'))
run(*ecj,'-cp',asm,'-d',r/'build/patch',r/'Patch21.java')
p=r/'output/zomboid-global-melee-animations-2.3.1-v24-first-person-directional-falls.jar'
run('java','-cp',str(r/'build/patch')+':'+asm,'Patch21',r/'base/melee20.jar',p,r/'build/classes')
with zipfile.ZipFile(p)as z:e={n:z.read(n)for n in z.namelist()}
n='zomboid_global_melee.mixins.json';mix=json.loads(e[n]);mix['mixins']+=['PronePushable21','PronePush21'];mix['client']+=['ArticulatedArms23','ArticulatedEmfArms23'];e[n]=json.dumps(mix,indent=2).encode()
e['assets/zomboid_global_melee/textures/entity/stomp_boot.png']=(r/'assets/stomp_boot.png').read_bytes()
with zipfile.ZipFile(p,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(p)as z:assert z.testzip()is None;assert not any(n.startswith(('net/minecraft/','com/mojang/','org/joml/'))for n in z.namelist())
with zipfile.ZipFile(r/'base/melee20.jar')as a,zipfile.ZipFile(p)as b:
 changed=[n for n in a.namelist()if a.read(n)!=b.read(n)];added=sorted(set(b.namelist())-set(a.namelist()))
(r/'validation.json').write_text(json.dumps({'file':p.name,'sha256':sha(p),'minecraft_runtime_tested':False,'changed':changed,'added':added},indent=2)+'\n')
print('V24_COMPILED_BYTECODE_VERIFIED')
