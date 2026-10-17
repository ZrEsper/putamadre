from pathlib import Path
import subprocess,json,zipfile
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';v=json.loads((r/'validation.json').read_text());jar=r/'output'/v['output'];out=r/'build/tests';out.mkdir(exist_ok=True)
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',str(jar),'-d',str(out),*map(str,(r/'tests').rglob('*.java'))],check=True)
p=subprocess.run(['java','-Xverify:all','-cp',str(out)+':'+str(jar),'dev.zomboidmelee.boxing.Checks16'],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode()
with zipfile.ZipFile(jar)as z:
 for name in ['dev/zomboidmelee/boxing/Boxing16.class','dev/zomboidmelee/boxing/BoxingClient16.class']:assert b'yesman/' not in z.read(name)
 assert 'epicfight'not in z.read('META-INF/neoforge.mods.toml').decode()
asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',asm,'-d',str(r/'build/tools'),str(r/'Inspect.java')],check=True)
check=subprocess.run(['java','-cp',str(r/'build/tools')+':'+asm,'Inspect',str(jar)],capture_output=True,text=True);print(check.stdout,check.stderr,end='');check.check_returncode()
(r/'test-results.json').write_text(json.dumps({'checks':[p.stdout.strip(),check.stdout.strip()],'minecraft_runtime_tested':False},indent=2)+'\n')
