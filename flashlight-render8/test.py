from pathlib import Path
import subprocess,json
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);lamp=next((r/'output').glob('flashlight*.jar'));integration=next((r/'output').glob('zomboid-integration*.jar'));reports=[]
def check(cp,cls,*a):
 p=subprocess.run(['java','-Xverify:all','-cp',str(cp),cls,*map(str,a)],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();reports.append(p.stdout.strip())
cp=str(root/'flashlight-render7/build/test')+':'+str(root/'flashlight-render6/build/test')+':'+str(lamp)
for name in ['net.flashlight.battery.CacheChecks7','net.flashlight.battery.ToggleChecks7','fixture.Checks','fixture.WakeChecks','net.flashlight.battery.ReloadChecks6','net.flashlight.battery.ShadowChecks6']:check(cp,name)
subprocess.run([*ecj,'-cp',asm,'-d',str(r/'build/tool'),str(r/'NativeChecks.java')],check=True)
check(str(r/'build/tool')+':'+asm,'NativeChecks',integration,r/'base/integration710.jar')
p=subprocess.run(['java','-cp',str(r/'build/tool')+':'+asm,'NativeChecks',str(r/'base/integration710.jar'),str(r/'base/integration710.jar')],capture_output=True,text=True);assert p.returncode!=0 and 'player block-light path' in p.stderr;reports.append('INTEGRATION710_DUPLICATE_PLAYER_BLOCK_LIGHT_NEGATIVE_CONTROL_PASS')
p=subprocess.run(['python',str(r/'shader_test.py')],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();reports.append(p.stdout.strip())
p=subprocess.run(['python',str(r/'shader_test.py'),'--old'],capture_output=True,text=True);assert p.returncode!=0 and 'old shadow frustum must not erase' in p.stderr;reports.append('V7_FAST_TURN_BEAM_CUTOFF_NEGATIVE_CONTROL_PASS')
(r/'test-results.json').write_text(json.dumps(dict(checks=reports,minecraft_runtime_tested=False),indent=2)+'\n')
