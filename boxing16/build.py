from pathlib import Path
import subprocess,zipfile,json,hashlib
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';src=r/'base/melee15.jar';out=r/'output/zomboid-global-melee-animations-2.3.1-range-plus-one-ezweight-v16-tactical-boxing.jar'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a):subprocess.run(list(map(str,a)),check=True)
for n,h in json.loads((root/'spas-reload18/inputs.json').read_text())['tools'].items():assert sha(t/(n+'.jar'))==h
for n,h in json.loads((r/'inputs.json').read_text()).items():
 if n=='epicfight.jar' and not (r/'base'/n).exists():continue
 assert sha(r/'base'/n)==h
if (r/'base/epicfight.jar').exists():run('python',r/'extract.py')
for n,h in json.loads((r/'baked-assets.json').read_text()).items():assert sha(r/n)==h
cp=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];(r/'build/tools').mkdir(parents=True,exist_ok=True);out.parent.mkdir(exist_ok=True)
run(*ecj,'-cp',cp,'-d',r/'build/tools',r/'Patch.java');run(*ecj,'-cp',src,'-d',r/'build/classes',*(r/'src').rglob('*.java'));run('java','-cp',str(r/'build/tools')+':'+cp,'Patch',src,out,r/'build/classes')
with zipfile.ZipFile(out)as z:e={n:z.read(n)for n in z.namelist()}
for p in (r/'assets').iterdir():e['assets/zomboid_global_melee/boxing/'+p.name]=p.read_bytes()
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(src)as a,zipfile.ZipFile(out)as b:
 assert b.testzip()is None;changed=[n for n in a.namelist()if a.read(n)!=b.read(n)];assert set(changed)=={'dev/zomboidmelee/mixin/VanillaCombatMixin.class','dev/zomboidmelee/mixin/BodyPushMixin.class'}
 assert not any(n.startswith(('yesman/','net/minecraft/','com/mojang/'))for n in b.namelist())
(r/'validation.json').write_text(json.dumps({'output':out.name,'sha256':sha(out),'changed':changed,'added':sorted(set(b.namelist())-set(a.namelist())),'no_epic_fight_runtime_classes':True,'minecraft_runtime_tested':False},indent=2)+'\n')
