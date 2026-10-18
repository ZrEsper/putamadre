from pathlib import Path
import subprocess,shutil,json
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools';ecj=['java','-jar',str(t/'ecj.jar'),'-21','-nowarn','-proc:none'];fixtures=r/'build/fixtures';base=r/'build/test';shutil.rmtree(fixtures,ignore_errors=True);shutil.copytree(r.parent/'flashlight-battery4/tests',fixtures)
p=fixtures/'fixture/Checks.java';s=p.read_text().replace('Power.range(p)==Power.RANGE[tier]','Power.range(p)==Math.min(2,Power.RANGE[tier])');p.write_text(s)
subprocess.run([*ecj,'-cp',str(r/'build/classes'),'-d',str(base),*map(str,fixtures.rglob('*.java')),*map(str,(r/'tests').rglob('*.java'))],check=True)
reports=[]
for name in ['fixture.Checks','fixture.WakeChecks','net.flashlight.battery.ReloadChecks6','net.flashlight.battery.ShadowChecks6']:
 p=subprocess.run(['java','-Xverify:all','-cp',str(base)+':'+str(r/'build/classes'),name],text=True,capture_output=True);print(p.stdout,p.stderr,end='');p.check_returncode();reports.append(p.stdout.strip())
(r/'test-results.json').write_text(json.dumps({'checks':reports,'minecraft_runtime_tested':False},indent=2)+'\n')
