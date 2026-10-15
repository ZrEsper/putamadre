from pathlib import Path
import os,json,subprocess
r=Path(__file__).resolve().parent;os.chdir(r);t=r.parent/'hordes-melee-interiors-patch/build/tools';out=next(Path('output').glob('*.jar'));Path('build/tests').mkdir(exist_ok=True)
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',str(out),'-d','build/tests',*map(str,Path('tests').rglob('*.java'))],check=True)
result=subprocess.run(['java','-Xverify:all','-cp','build/tests'+os.pathsep+str(out),'checks.Checks'],text=True,capture_output=True);print(result.stdout);print(result.stderr);result.check_returncode();base=r.parent/'mods/dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.29-player-audio-aim-interactions.jar'
for artifact,expected in [(base,'SELF_DEFIB_USE=false'),(out,'SELF_DEFIB_USE=true')]:
 probe=subprocess.run(['java','-Xverify:all','-cp','build/tests'+os.pathsep+str(artifact),'checks.Probe'],text=True,capture_output=True,check=True);print(probe.stdout.strip());assert probe.stdout.strip()==expected
Path('test-results.json').write_text(json.dumps({'checks':result.stdout.strip(),'original_native_input_probe':'SELF_DEFIB_USE=false','patched_native_input_probe':'SELF_DEFIB_USE=true','minecraft_runtime_tested':False},indent=2)+'\n')
