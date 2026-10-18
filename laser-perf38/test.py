from pathlib import Path
import subprocess,json,zipfile
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';v=json.loads((r/'validation.json').read_text());jar=r/'output'/v['file'];out=r/'build/tests';out.mkdir(exist_ok=True);base=root/'action34/build/test-base'
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',str(jar)+':'+str(base)+':'+str(root/'flashlight-render14/build/test')+':'+str(root/'flashlight-render7/build/test'),'-d',str(out),*map(str,(r/'tests').rglob('*.java'))],check=True)
runtime=':'.join(map(str,[out,base,jar,root/'action20/output/zomboid-zombie-gore-1.0.10-neoforge-1.21.1-directional-momentum.jar',root/'action34/base/tacz19-probes.jar']));reports=[]
for cls in ['LatePhysicalChecks37','LateQueueChecks37','StableLaserChecks33','ArmorShadowRegression','LocomotionChecks31','CorpseChecks38']:
 p=subprocess.run(['java','-Xverify:all','-cp',runtime,'dev.zomboidmelee.boxing.'+cls],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();reports.append(p.stdout.strip())
with zipfile.ZipFile(jar)as z:
 cfg=json.loads(z.read('zomboid_global_melee.mixins.json'));assert 'LateLaser37'in cfg['client'];code=z.read('dev/zomboidmelee/mixin/LateLaser37.class');assert b'TAIL'in code and b'renderLevel(Lnet/minecraft/client/DeltaTracker;)V'in code
 reports.append('LATE_PASS_AFTER_WORLD_DEPTH_SNAPSHOT_AND_HANDS_BEFORE_GUI')
(r/'test-results.json').write_text(json.dumps({'checks':reports,'minecraft_runtime_tested':False},indent=2)+'\n')

glcp=':'.join(map(str,[out,root/'flashlight-render14/build/test',root/'flashlight-render7/build/test',jar]))
p=subprocess.run(['java','-Xverify:all','-cp',glcp,'dev.zomboidmelee.boxing.NativeTextureChecks37'],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();reports.append(p.stdout.strip())
(r/'test-results.json').write_text(json.dumps({'checks':reports,'minecraft_runtime_tested':False},indent=2)+'\n')

asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',asm,'-d',str(r/'build/classes'),str(r/'Inspect.java')],check=True)
subprocess.run(['java','-cp',str(r/'build/classes')+':'+asm,'Inspect',str(r/'base/AnimationManager.class'),str(r/'build/CorpseAnimation38.class')],check=True)
