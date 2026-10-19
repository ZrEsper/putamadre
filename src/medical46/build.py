from pathlib import Path
import subprocess,json,zipfile,hashlib
r=Path(__file__).resolve().parent;t=Path('/workspace/putamadre/hordes-melee-interiors-patch/build/tools')
for p in [r/'build/tools',r/'output']:p.mkdir(parents=True,exist_ok=True)
old=Path('/workspace/medical45/release/zomboid-zombie-gore-1.0.20-neoforge-1.21.1-medical-corpse.30.jar');new=r/'output/zomboid-zombie-gore-1.0.20-neoforge-1.21.1-corpse-bullet-double-fix.31.jar'
e=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(s+'.jar'))for s in ['asm','asm-tree','asm-analysis'])
subprocess.run(e+['-cp',asm,'-d',str(r/'build/tools'),str(r/'Fix46.java')],check=True)
subprocess.run(['java','-cp',str(r/'build/tools')+':'+asm,'Fix46',str(old),str(new)],check=True)
subprocess.run(e+['-cp',str(old),'-d',str(r/'build/tests')]+list(map(str,(r/'tests').rglob('*.java'))),check=True)
reports={}
for key,jar in [('before',old),('after',new)]:
 p=subprocess.run(['java','-Xverify:all','-cp',str(r/'build/tests')+':'+str(jar),'dev.zomboid.gore.BulletRegression46'],text=True,capture_output=True);reports[key]={'exit_code':p.returncode,'stdout':p.stdout,'stderr':p.stderr};print(key,p.returncode,p.stdout or p.stderr[:300])
 if key=='before':assert p.returncode!=0 and 'scale' in p.stderr and 'NoSuchMethodException' in p.stderr
 else:assert p.returncode==0
with zipfile.ZipFile(old)as a,zipfile.ZipFile(new)as b:
 assert b.testzip()is None
 changed=[n for n in a.namelist()if a.read(n)!=b.read(n)]
 assert set(changed)=={'dev/zomboid/gore/Bullet13.class','META-INF/neoforge.mods.toml'}
 reports['changed_entries']=changed
reports['input_sha256']=hashlib.sha256(old.read_bytes()).hexdigest();reports['output_sha256']=hashlib.sha256(new.read_bytes()).hexdigest();reports['minecraft_runtime_tested']=False
(r/'test-results.json').write_text(json.dumps(reports,indent=2)+'\n')
