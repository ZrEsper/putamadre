from pathlib import Path
import os,json,subprocess
r=Path(__file__).resolve().parent;os.chdir(r);t=r.parent/'hordes-melee-interiors-patch/build/tools';out=next(Path('output').glob('*.jar'));Path('build/tests').mkdir(exist_ok=True)
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',str(out),'-d','build/tests',*map(str,Path('tests').rglob('*.java'))],check=True)
result=subprocess.run(['java','-Xverify:all','-cp','build/tests'+os.pathsep+str(out),'checks.NvgChecks'],text=True,capture_output=True);print(result.stdout);print(result.stderr);result.check_returncode();Path('test-results.json').write_text(json.dumps({'checks':result.stdout.strip(),'minecraft_runtime_tested':False},indent=2)+'\n')
