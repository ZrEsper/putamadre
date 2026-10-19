from pathlib import Path
import subprocess,json,zipfile
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';out=r/'build/tests';out.mkdir(exist_ok=True)
gore=r/'output/zomboid-zombie-gore-1.0.20-neoforge-1.21.1-layer-capture-fix.21.jar';flash=r/'output/flashlight-5.1-neoforge-1.21.1-gl-error-isolation.18.jar';rag=r/'output/ragdollified-1.21.1-1.1.5-module-capture-fix.18.jar'
ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];fixtures=[p for p in(root/'gore-anatomy14/tests').rglob('*.java')if p.name!='AnatomyChecks14.java']
subprocess.run([*ecj,'-cp',str(gore)+':'+str(flash)+':'+str(rag),'-d',str(out),*map(str,fixtures),*map(str,(root/'gore-anatomy20/tests').rglob('*.java')),*map(str,(r/'tests').rglob('*.java'))],check=True)
reports=[]
for cls,args in [('RepairChecks21',[]),('LayersChecks15',[str(r/'build/anatomy.json')]),('OwnerChecks20',[]),('SurfaceChecks19',[]),('BallisticChecks18',[]),('ReferenceChecks17',[]),('PhysicsChecks16',[]),('IntegrationChecks16',[]),('EffectsChecks14',[]),('ModelChecks14',[])]:
 p=subprocess.run(['java','-Xverify:all','-cp',':'.join(map(str,[out,gore,flash,rag])),'dev.zomboid.gore.'+cls,*args],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();reports.append(p.stdout.strip())
p=subprocess.run(['java','-Xverify:all','-cp',str(out)+':'+str(flash),'net.flashlight.battery.ErrorsChecks18'],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();reports.append(p.stdout.strip())
asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);subprocess.run([*ecj,'-cp',asm,'-d',str(out),str(r/'Inspect.java')],check=True);subprocess.run(['java','-cp',str(out)+':'+asm,'Inspect',str(rag)],check=True)
for jar in [gore,rag,flash]:
 with zipfile.ZipFile(jar)as z:assert z.testzip()is None
(r/'test-results.json').write_text(json.dumps({'reports':reports,'minecraft_runtime_tested':False},indent=2)+'\n')
# Resolve actual module descriptors, not just classes on a flat classpath.
subprocess.run([*ecj,'-d',str(out),str(r/'ModuleChecks22.java')],check=True)
melee=root/'laser-depth40/output/zomboid-global-melee-animations-2.3.1-v40-native-depth-format.jar'
previous=root/'mods/ragdollified-1.21.1-1.1.5-humanoid-texture-cache.17.jar'
old=subprocess.run(['java','-cp',str(out),'ModuleChecks22',*map(str,[gore,previous,flash,melee])],capture_output=True,text=True)
assert old.returncode!=0 and 'dev.zomboid.gore' in old.stderr
fixed=subprocess.run(['java','-cp',str(out),'ModuleChecks22',*map(str,[gore,rag,flash,melee])],capture_output=True,text=True);print(fixed.stdout,fixed.stderr,end='');fixed.check_returncode()
(r/'module-resolution-results.json').write_text(json.dumps({'previous':{'exit':old.returncode,'stderr':old.stderr},'fixed':{'exit':fixed.returncode,'stdout':fixed.stdout},'minecraft_runtime_tested':False},indent=2)+'\n')
