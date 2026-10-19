from pathlib import Path
import subprocess,json
r=Path(__file__).parent;t=Path('/workspace/putamadre/hordes-melee-interiors-patch/build/tools');ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];base=Path('/workspace/medical45/release');fa='/workspace/medical47/output/firstaid-1.21.1-ZOMBOID-v3.6.16-bite-class-fix.jar';g=str(r/'output/gore.jar');cp=':'.join([g,fa,str(base/'tacznpcs-2.1.0-1.21.1-tactical.31-single-corpse.jar')])
subprocess.run(ecj+['-cp',cp,'-d',str(r/'build/regression45')]+list(map(str,Path('/workspace/medical45/tests').rglob('*.java'))),check=True)
p=subprocess.run(['java','-Xverify:all','-cp',str(r/'build/regression45')+':'+cp,'dev.zomboid.gore.Test45'],capture_output=True,text=True);print(p.stdout,p.stderr);assert p.returncode==0
# The actual Bullet13.tick regression from the double-boxing crash stays passing.
subprocess.run(ecj+['-cp',g,'-d',str(r/'build/regression46')]+list(map(str,Path('/workspace/medical46/tests').rglob('*.java'))),check=True)
b=subprocess.run(['java','-Xverify:all','-cp',str(r/'build/regression46')+':'+g,'dev.zomboid.gore.BulletRegression46'],capture_output=True,text=True);print(b.stdout,b.stderr);assert b.returncode==0
report=json.loads((r/'test-results.json').read_text());report['previous_medical_persistence']=p.stdout.strip();report['previous_bullet_crash']=b.stdout.strip();(r/'test-results.json').write_text(json.dumps(report,indent=2)+'\n')
