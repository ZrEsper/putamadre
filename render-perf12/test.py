from pathlib import Path
import subprocess,json
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';info=json.loads((r/'validation.json').read_text());out=r/'build/test';out.mkdir(exist_ok=True);cp=':'.join(map(str,[root/'nvg-feedback10/build/test',root/'hybrid-mask8/build/test',r/'output'/info['nvg']['file'],r/'output'/info['integration']['file']]))
def run(cmd):
 p=subprocess.run(list(map(str,cmd)),capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();return p.stdout.strip()
run(['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn','-cp',cp,'-d',out,*(r/'tests').rglob('*.java')]);checks=[]
for cls in ['com.raiiiden.warborn.hybrid.VisionChecks12','dev.zomboid.perf12.GraphChecks12','com.raiiiden.warborn.hybrid.SoundChecks10','com.raiiiden.warborn.hybrid.TextureChecks10']:
 checks.append(run(['java','-Xverify:all','-cp',str(out)+':'+cp,cls]))
(r/'test-results.json').write_text(json.dumps(dict(checks=checks,minecraft_runtime_tested=False),indent=2)+'\n')

import zipfile
for key,base in [('nvg','fracture11.jar'),('integration','integration11.jar')]:
 with zipfile.ZipFile(r/'base'/base)as before,zipfile.ZipFile(r/'output'/info[key]['file'])as after:
  for name in before.namelist():
   if name.startswith('assets/'):assert before.read(name)==after.read(name),name
checks.append('ALL_EXISTING_ASSETS_BYTE_IDENTICAL')
asm=':'.join(str(t/(name+'.jar'))for name in ['asm','asm-tree','asm-analysis'])
run(['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn','-cp',asm,'-d',out,r/'Inspect12.java'])
checks.append(run(['java','-cp',str(out)+':'+asm,'Inspect12',*(r/'output').glob('*.jar')]))
(r/'test-results.json').write_text(json.dumps(dict(checks=checks,minecraft_runtime_tested=False),indent=2)+'\n')
