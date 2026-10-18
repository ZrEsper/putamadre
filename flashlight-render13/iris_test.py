from pathlib import Path
import subprocess,json
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools';out=r/'build/iris-test';out.mkdir(exist_ok=True);jar=next((r/'output').glob('*.jar'))
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',str(jar),'-d',str(out),*map(str,(r/'iris-tests').rglob('*.java'))],check=True)
p=subprocess.run(['java','-Xverify:all','-cp',str(out)+':'+str(jar),'net.flashlight.battery.IrisChecks12'],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode()
source=(r/'src/net/flashlight/battery/Engine6.java').read_text();capture=source.split('static void capture(Object event)')[1].split('public static void render')[0];render=source.split('public static void render')[1]
assert 'try(Iris12 iris=Iris12.enter' in capture and 'Iris12' not in render
assert 'albedo(m,' in capture and 'albedo(m,' not in render
assert 'after_translucent_blocks' in render and 'after_level' in render
assert 'failed=true' not in capture+render
(r/'iris-test-results.json').write_text(json.dumps({'checks':[p.stdout.strip(),'CAPTURE_BEFORE_FINALIZE_COMPOSITE_AFTER_FINALIZE','NO_PERMANENT_DISABLE_ON_FRAME_FAILURE'],'minecraft_shaderpack_runtime_tested':False},indent=2)+'\n')
