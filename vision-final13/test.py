from pathlib import Path
import subprocess,json,zipfile
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';v=json.loads((r/'validation.json').read_text());jar=r/'output'/v['file'];out=r/'build/test';out.mkdir(exist_ok=True)
cp=':'.join(map(str,[root/'render-perf12/build/test',root/'nvg-feedback10/build/test',root/'hybrid-mask8/build/test',jar]))
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',cp,'-d',str(out),*map(str,(r/'tests').rglob('*.java'))],check=True)
# New production Vision12 must precede the previous fixture output; fake Gpu12 comes from render-perf12.
runtime=':'.join(map(str,[out,r/'build/classes',root/'render-perf12/build/test',root/'nvg-feedback10/build/test',root/'hybrid-mask8/build/test',jar]))
# Explicit fixture GPU class before jar, without an old Vision12 alongside it.
gpu=out/'com/raiiiden/warborn/hybrid';gpu.mkdir(parents=True,exist_ok=True)
import shutil
for p in (root/'render-perf12/build/test/com/raiiiden/warborn/hybrid').glob('Gpu12*.class'):shutil.copy2(p,gpu/p.name)
checks=[]
for cls in ['com.raiiiden.warborn.hybrid.VisionChecks12','com.raiiiden.warborn.hybrid.SoundChecks10','com.raiiiden.warborn.hybrid.TextureChecks10']:
 p=subprocess.run(['java','-Xverify:all','-cp',runtime,cls],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();checks.append(p.stdout.strip())
with zipfile.ZipFile(jar)as z:
 cfg=json.loads(z.read('fracturepoint.mixins.json'));assert 'client.NvgFinalPass13'in cfg['client'];assert 'com/raiiiden/warborn/hybrid/VisionStage12.class'not in z.namelist()
 code=z.read('com/raiiiden/warborn/mixin/client/NvgFinalPass13.class');assert b'renderLevel(Lnet/minecraft/client/DeltaTracker;)V'in code and b'TAIL'in code and b'finish'in code
 checks.append('AFTER_HANDS_TAIL_BEFORE_GUI_NO_DUPLICATE_WORLD_EVENT')
(r/'test-results.json').write_text(json.dumps({'checks':checks,'minecraft_runtime_tested':False},indent=2)+'\n')
