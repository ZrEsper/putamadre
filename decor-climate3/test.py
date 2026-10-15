from pathlib import Path
import os,subprocess,json
r=Path(__file__).resolve().parent;os.chdir(r);t=r.parent/'hordes-melee-interiors-patch/build/tools';out=list(Path('output').glob('*.jar'));cp=os.pathsep.join(map(str,out));Path('build/tests').mkdir(exist_ok=True)
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',cp,'-d','build/tests',*map(str,Path('tests').rglob('*.java'))],check=True)
results=[]
for check in ['dev.zomboid.decor.Checks','dev.zomboid.decor.ClimateChecks']:
 result=subprocess.run(['java','-Xverify:all','-cp','build/tests'+os.pathsep+cp,check],check=True,capture_output=True,text=True);print(result.stdout);results.append(result.stdout.strip())
Path('test-results.json').write_text(json.dumps({'checks':results,'minecraft_runtime_tested':False},indent=2)+'\n')
