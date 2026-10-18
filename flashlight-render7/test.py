from pathlib import Path
import subprocess,json
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];new=next((r/'output').glob('flashlight*.jar'));cp=str(r/'build/test')+':'+str(root/'flashlight-render6/build/test')+':'+str(new);reports=[]
def run(*a):subprocess.run(list(map(str,a)),check=True)
(r/'build/test').mkdir(exist_ok=True);run(*ecj,'-cp',str(root/'flashlight-render6/build/test')+':'+str(new),'-d',r/'build/test',*(r/'tests').rglob('*.java'))
for name in ['net.flashlight.battery.CacheChecks7','net.flashlight.battery.ToggleChecks7','fixture.Checks','fixture.WakeChecks','net.flashlight.battery.ReloadChecks6','net.flashlight.battery.ShadowChecks6']:
 p=subprocess.run(['java','-Xverify:all','-cp',cp,name],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();reports.append(p.stdout.strip())
oldcp=cp.replace(str(new),str(r/'base/flash6.jar'));p=subprocess.run(['java','-Xverify:all','-cp',oldcp,'net.flashlight.battery.CacheChecks7'],capture_output=True,text=True);assert p.returncode!=0 and 'native rendering must bind' in p.stderr;reports.append('V6_DESYNCHRONIZED_NATIVE_TEXTURE_CACHE_NEGATIVE_CONTROL_PASS')
(r/'test-results.json').write_text(json.dumps(dict(checks=reports,minecraft_runtime_tested=False),indent=2)+'\n')
