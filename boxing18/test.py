from pathlib import Path
import subprocess,json,zipfile
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';v=json.loads((r/'validation.json').read_text());jar=r/'output'/v['output'];out=r/'build/tests';out.mkdir(exist_ok=True)
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',str(jar),'-d',str(out),*map(str,(r/'tests').rglob('*.java'))],check=True)
p=subprocess.run(['java','-Xverify:all','-cp',str(out)+':'+str(jar),'dev.zomboidmelee.boxing.Checks16'],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode()
with zipfile.ZipFile(jar)as z:
 for name in ['dev/zomboidmelee/boxing/Boxing16.class','dev/zomboidmelee/boxing/BoxingClient16.class']:assert b'yesman/' not in z.read(name)
 assert 'epicfight'not in z.read('META-INF/neoforge.mods.toml').decode()
checks=[p.stdout.strip()]
for cls in ['AnimationRegression','ArmorShadowRegression','RawInputChecks']:
 p=subprocess.run(['java','-Xverify:all','-cp',str(out)+':'+str(jar),'dev.zomboidmelee.boxing.'+cls],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();checks.append(p.stdout.strip())
for cls,needle in [('AnimationRegression','only stationary first-person guard'),('ArmorShadowRegression','xRot')]:
 p=subprocess.run(['java','-Xverify:all','-cp',str(out)+':'+str(r/'base/melee16.jar'),'dev.zomboidmelee.boxing.'+cls],capture_output=True,text=True);assert p.returncode!=0 and needle in p.stderr,(cls,p.stdout,p.stderr)
checks.append('OLD_V16_FIRST_PERSON_AND_ARMOR_CRASH_REPRODUCED_CHECKS=2')
asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',str(out)+':'+asm,'-d',str(r/'build/tools'),str(r/'Inspect17.java')],check=True)
p=subprocess.run(['java','-Xverify:all','-cp',str(r/'build/tools')+':'+str(out)+':'+str(jar)+':'+asm,'Inspect17',str(jar)],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();checks.append(p.stdout.strip())
(r/'test-results.json').write_text(json.dumps({'checks':checks,'minecraft_runtime_tested':False},indent=2)+'\n')

g=json.loads((r/'gore-validation.json').read_text());gore=r/'output'/g['output']
extra=r/'build/tests18';extra.mkdir(exist_ok=True)
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',str(out)+':'+str(jar)+':'+str(gore),'-d',str(extra),*map(str,(r/'tests18').rglob('*.java'))],check=True)
g=json.loads((r/'gore-validation.json').read_text());gore=r/'output'/g['output']
p=subprocess.run(['java','-Xverify:all','-cp',str(extra)+':'+str(out)+':'+str(jar)+':'+str(gore),'dev.zomboidmelee.boxing.CombatChecks18'],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();checks.append(p.stdout.strip())
(r/'test-results.json').write_text(json.dumps({'checks':checks,'minecraft_runtime_tested':False},indent=2)+'\n')

for cls in ['ProtocolChecks18','LegRenderChecks18']:
 p=subprocess.run(['java','-Xverify:all','-cp',str(extra)+':'+str(out)+':'+str(jar)+':'+str(gore),'dev.zomboidmelee.boxing.'+cls],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();checks.append(p.stdout.strip())
(r/'test-results.json').write_text(json.dumps({'checks':checks,'minecraft_runtime_tested':False},indent=2)+'\n')
