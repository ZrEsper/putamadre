from pathlib import Path
import subprocess,json
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools';jar=r/'output/zomboid-zombie-gore-1.0.15-neoforge-1.21.1-layered-destruction.jar';out=r/'build/test';out.mkdir(exist_ok=True)
fixtures=[p for p in (r.parent/'gore-anatomy14/tests').rglob('*.java')if p.name!='AnatomyChecks14.java']
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',str(jar),'-d',str(out),*map(str,fixtures),*map(str,(r/'tests').rglob('*.java'))],check=True)
results=[]
for main,args in [('LayersChecks15',[str(r/'build/anatomy.json')]),('EffectsChecks14',[]),('ModelChecks14',[])]:
 p=subprocess.run(['java','-Xverify:all','-cp',str(out)+':'+str(jar),'dev.zomboid.gore.'+main,*args],capture_output=True,text=True);print(p.stdout,p.stderr);p.check_returncode();results.append(p.stdout.strip())
(r/'test-results.json').write_text(json.dumps({'results':results,'minecraft_runtime_tested':False},indent=2)+'\n')
