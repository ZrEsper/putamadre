from pathlib import Path
import subprocess,json,zipfile,shutil
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';out=r/'build/tests';shutil.rmtree(out,ignore_errors=True);out.mkdir(exist_ok=True)
gore=r/'output/zomboid-zombie-gore-1.0.20-neoforge-1.21.1-visible-tissue-debris.23.jar';rag=r/'output/ragdollified-1.21.1-1.1.5-low-impact.19.jar'
ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];fixtures=[p for p in(root/'gore-anatomy14/tests').rglob('*.java')if p.name!='AnatomyChecks14.java']
subprocess.run([*ecj,'-cp',str(gore)+':'+str(rag),'-d',str(out),*map(str,fixtures),*map(str,[p for p in(root/'gore-anatomy20/tests').rglob('*.java')if p.name!='Shards13.java']),*map(str,(r/'tests').rglob('*.java'))],check=True);reports=[]
for cls,args in [('ImpactChecks23',[]),('LayersChecks15',[str(r/'build/anatomy.json')]),('OwnerChecks20',[]),('SurfaceChecks19',[]),('BallisticChecks18',[]),('ReferenceChecks17',[]),('PhysicsChecks16',[]),('IntegrationChecks16',[]),('EffectsChecks14',[]),('ModelChecks14',[])]:
 p=subprocess.run(['java','-Xverify:all','-cp',':'.join(map(str,[out,gore,rag])),'dev.zomboid.gore.'+cls,*args],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();reports.append(p.stdout.strip())
subprocess.run([*ecj,'-d',str(out),str(root/'render-repair21/ModuleChecks22.java')],check=True)
p=subprocess.run(['java','-cp',str(out),'ModuleChecks22',*map(str,[gore,rag,root/'render-repair21/output/flashlight-5.1-neoforge-1.21.1-gl-error-isolation.18.jar',root/'laser-depth40/output/zomboid-global-melee-animations-2.3.1-v40-native-depth-format.jar'])],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode()
with zipfile.ZipFile(rag)as z:assert not any(n.startswith('dev/zomboid/gore/')for n in z.namelist())
(r/'test-results.json').write_text(json.dumps({'results':reports,'module_resolution':p.stdout.strip(),'minecraft_runtime_tested':False},indent=2)+'\n')
