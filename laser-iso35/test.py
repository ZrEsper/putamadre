from pathlib import Path
import subprocess,json
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);melee=next((r/'output').glob('*melee*.jar'));iso=next((r/'output').glob('dungeons*.jar'));base=root/'action34/build/test-base';gore=root/'action20/output/zomboid-zombie-gore-1.0.10-neoforge-1.21.1-directional-momentum.jar';probe=root/'action34/base/tacz19-probes.jar';results=[]
def run(*a):subprocess.run(list(map(str,a)),check=True)
def check(cp,cls,*args):
 p=subprocess.run(['java','-Xverify:all','-cp',str(cp),cls,*map(str,args)],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();results.append(p.stdout.strip())
for d in ['build/tests','build/iso-tests']:(r/d).mkdir(parents=True,exist_ok=True)
run(*ecj,'-cp',str(melee)+':'+str(base),'-d',r/'build/tests',*(r/'tests').rglob('*.java'))
cp=str(r/'build/tests')+':'+str(base)+':'+str(melee)+':'+str(gore)+':'+str(probe)
check(cp,'dev.zomboidmelee.boxing.PhysicalLaserChecks')
oldcp=cp.replace(str(melee),str(r/'base/melee34.jar'))
p=subprocess.run(['java','-Xverify:all','-cp',oldcp,'dev.zomboidmelee.boxing.PhysicalLaserChecks'],capture_output=True,text=True);assert p.returncode!=0 and 'reticle movement cannot steer' in p.stderr;results.append('V34_RETICLE_STEERS_LASER_NEGATIVE_CONTROL_PASS')
for cls in ['StableLaserChecks33','ArmorShadowRegression','LocomotionChecks31']:
 check(cp,'dev.zomboidmelee.boxing.'+cls)
run(*ecj,'-cp',iso,'-d',r/'build/iso-tests',*(r/'iso-tests').rglob('*.java'));check(str(r/'build/iso-tests')+':'+str(iso),'dev.zomboid.isotacz.ShotChecks32')
run(*ecj,'-cp',asm,'-d',r/'build/tool',r/'NativeChecks.java');check(str(r/'build/tool')+':'+asm,'NativeChecks',iso,r/'base/native-targets.jar',r/'base/iso31.jar')
(r/'test-results.json').write_text(json.dumps(dict(checks=results,minecraft_runtime_tested=False),indent=2)+'\n')
