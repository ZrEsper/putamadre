from pathlib import Path
import subprocess,json
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';v=json.loads((r/'validation.json').read_text());jar=r/'output'/v['file'];out=r/'build/tests';out.mkdir(exist_ok=True);base=root/'action34/build/test-base';cp=str(jar)+':'+str(base)
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',cp,'-d',str(out),*map(str,(r/'tests').rglob('*.java')),str(root/'laser-iso35/tests/dev/zomboidmelee/boxing/PhysicalLaserChecks.java')],check=True)
runtime=':'.join(map(str,[out,base,jar,root/'action20/output/zomboid-zombie-gore-1.0.10-neoforge-1.21.1-directional-momentum.jar',root/'action34/base/tacz19-probes.jar']));results=[]
for cls in ['ColorDepthChecks36','PhysicalLaserChecks','StableLaserChecks33','ArmorShadowRegression','LocomotionChecks31']:
 p=subprocess.run(['java','-Xverify:all','-cp',runtime,'dev.zomboidmelee.boxing.'+cls],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();results.append(p.stdout.strip())
(r/'test-results.json').write_text(json.dumps({'checks':results,'minecraft_runtime_tested':False},indent=2)+'\n')
