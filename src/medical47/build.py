from pathlib import Path
import subprocess,zipfile,json,hashlib,re
r=Path(__file__).resolve().parent;t=Path('/workspace/putamadre/hordes-melee-interiors-patch/build/tools');base=Path('/workspace/medical45/release')
for p in [r/'output',r/'build/tools']:p.mkdir(parents=True,exist_ok=True)
old=base/'firstaid-1.21.1-ZOMBOID-v3.6.15-procedural-visible-wounds.jar';new=r/'output/firstaid-1.21.1-ZOMBOID-v3.6.16-bite-class-fix.jar'
compat=base/'tacz-firstaid-compat-1.0.3-impact-coordinates.jar';newcompat=r/'output/tacz-firstaid-compat-1.0.4-firstaid-3.6.16.jar'
ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(x+'.jar'))for x in ['asm','asm-tree','asm-analysis'])
subprocess.run(ecj+['-cp',asm,'-d',str(r/'build/tools'),str(r/'Fix47.java')],check=True)
subprocess.run(['java','-cp',str(r/'build/tools')+':'+asm,'Fix47',str(old),str(new)],check=True)
with zipfile.ZipFile(compat)as z:data={n:z.read(n)for n in z.namelist()}
meta='META-INF/neoforge.mods.toml';data[meta]=data[meta].decode().replace('version="1.0.3"','version="1.0.4"').replace('v3.6.15-procedural-visible-wounds','v3.6.16-bite-class-fix').encode()
with zipfile.ZipFile(newcompat,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in data.items():z.writestr(n,b)
subprocess.run(ecj+['-cp',str(old),'-d',str(r/'build/tests')]+list(map(str,(r/'tests').rglob('*.java'))),check=True)
report={'minecraft_runtime_tested':False}
for name,jar in [('before',old),('after',new)]:
 p=subprocess.run(['java','-Xverify:all','-cp',str(r/'build/tests')+':'+str(jar)+':'+str(compat),'dev.zomboid.firstaid.BiteRegression47'],text=True,capture_output=True);report[name]={'exit_code':p.returncode,'stdout':p.stdout,'stderr':p.stderr};print(name,p.returncode,p.stdout or p.stderr[:300])
 if name=='before':assert p.returncode!=0 and 'ClassNotFoundException: ichttt.mods.firstaid.survival.TreatmentV33' in p.stderr
 else:assert p.returncode==0
for a,b,expected in [(old,new,{'dev/zomboid/firstaid/Wounds45.class',meta}),(compat,newcompat,{meta})]:
 with zipfile.ZipFile(a)as x,zipfile.ZipFile(b)as y:
  assert y.testzip()is None;changed={n for n in x.namelist()if x.read(n)!=y.read(n)};assert changed==expected,changed
 report[b.name]={'sha256':hashlib.sha256(b.read_bytes()).hexdigest(),'changed_entries':sorted(changed)}
(r/'test-results.json').write_text(json.dumps(report,indent=2)+'\n')
