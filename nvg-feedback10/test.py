from pathlib import Path
import subprocess,json
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];jar=next((r/'output').glob('*.jar'));base=root/'hybrid-mask8/build/test';out=r/'build/test';out.mkdir(exist_ok=True)
subprocess.run([*ecj,'-cp',str(base)+':'+str(jar),'-d',str(out),*map(str,(r/'tests').rglob('*.java'))],check=True);results=[]
for name in ['SoundChecks10','TextureChecks10']:
 p=subprocess.run(['java','-Xverify:all','-cp',str(out)+':'+str(base)+':'+str(jar),'com.raiiiden.warborn.hybrid.'+name],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();results.append(p.stdout.strip())
old=str(out)+':'+str(base)+':'+str(r/'build/classes')+':'+str(r/'base/fracture9.jar')
p=subprocess.run(['java','-cp',old,'com.raiiiden.warborn.hybrid.SoundChecks10'],capture_output=True,text=True);assert p.returncode!=0 and 'accepted real N key plays ON immediately' in p.stderr;results.append('V9_KEY_AUDIO_ANIMATION_DELAY_NEGATIVE_CONTROL_PASS')
p=subprocess.run(['java','-cp',str(out)+':'+str(base)+':'+str(r/'base/fracture9.jar'),'com.raiiiden.warborn.hybrid.TextureChecks10'],capture_output=True,text=True);assert p.returncode!=0 and 'hybrid must retain original entity skin' in p.stderr;results.append('V9_WHITE_ENTITY_TEXTURE_NEGATIVE_CONTROL_PASS')
(r/'test-results.json').write_text(json.dumps(dict(checks=results,minecraft_runtime_tested=False),indent=2)+'\n')
