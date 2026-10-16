from pathlib import Path
import os,json,subprocess,hashlib
r=Path(__file__).resolve().parent;os.chdir(r);t=r.parent/'hordes-melee-interiors-patch/build/tools';out=next(Path('output').glob('*.jar'));Path('build/tests').mkdir(exist_ok=True)
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',str(out),'-d','build/tests',*map(str,[x for x in Path('tests').rglob('*.java') if x.name!='NativeAudioChecks.java'])],check=True)
results=[]
for cls in ['checks.Checks','checks.RoomChecks']:
 result=subprocess.run(['java','-Xverify:all','-cp','build/tests'+os.pathsep+str(out),cls],text=True,capture_output=True);print(result.stdout);print(result.stderr);result.check_returncode();results.append(result.stdout.strip())
assert hashlib.sha256(Path('tests/fixtures/SoundPhysics-1.5.1.class').read_bytes()).hexdigest()==json.loads(Path('input.json').read_text())['sound_physics_compatibility']['fixture_class_sha256']
cp=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',cp,'-d','build/tool','tests/NativeAudioChecks.java'],check=True)
probe=subprocess.run(['java','-cp','build/tests:build/tool:'+str(out)+':'+cp,'NativeAudioChecks','tests/fixtures/SoundPhysics-1.5.1.class',str(out)],capture_output=True,text=True);print(probe.stdout,probe.stderr);probe.check_returncode();results.append(probe.stdout.strip())
Path('test-results.json').write_text(json.dumps({'checks':results,'minecraft_runtime_tested':False},indent=2)+'\n')
