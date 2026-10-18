from pathlib import Path
import subprocess,json
r=Path(__file__).resolve().parent;root=r.parent;info=json.loads((r/'validation.json').read_text());jar=r/'output'/info['file'];cp=':'.join(map(str,[root/'nvg-feedback10/build/test',root/'hybrid-mask8/build/test',jar]));reports=[]
for cls in ['SoundChecks10','TextureChecks10']:
 p=subprocess.run(['java','-Xverify:all','-cp',cp,'com.raiiiden.warborn.hybrid.'+cls],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();reports.append(p.stdout.strip())
p=subprocess.run(['python',str(r/'shader_test.py'),'--old'],capture_output=True,text=True);assert p.returncode!=0 and 'AssertionError' in p.stderr;reports.append('V10_TOO_FAINT_STATIC_NEGATIVE_CONTROL_PASS')
(r/'test-results.json').write_text(json.dumps(dict(checks=reports,minecraft_runtime_tested=False),indent=2)+'\n')
