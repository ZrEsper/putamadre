from pathlib import Path
import subprocess,json,zipfile
r=Path(__file__).parent;t=Path('/workspace/putamadre/hordes-melee-interiors-patch/build/tools');e=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];cp=str(r/'output/gore.jar')
asm=':'.join(str(t/(x+'.jar'))for x in ['asm','asm-tree','asm-analysis'])
subprocess.run(e+['-cp',asm,'-d',str(r/'build/tools'),str(r/'ExtractCleanup48.java')],check=True)
subprocess.run(['java','-cp',str(r/'build/tools')+':'+asm,'ExtractCleanup48',str(r/'output/rag.jar'),str(r/'build/tests')],check=True)
fixtures=list((r/'tests').rglob('*.java'))+[Path('/workspace/medical45/tests/dev/zomboid/gore/Render45.java'),Path('/workspace/medical45/tests/net/minecraft/resources/ResourceLocation.java'),Path('/workspace/medical45/tests/net/minecraft/client/renderer/RenderType.java'),Path('/workspace/medical46/tests/net/minecraft/world/phys/Vec3.java')]
subprocess.run(e+['-cp',cp,'-d',str(r/'build/tests')]+list(map(str,fixtures)),check=True)
# Resources must be present during tests, before final packaging.
import shutil
shutil.copytree(r/'resources',r/'build/tests',dirs_exist_ok=True)
p=subprocess.run(['java','-Xverify:all','-cp',str(r/'build/tests')+':'+cp,'dev.zomboid.gore.Check48'],capture_output=True,text=True);print(p.stdout,p.stderr);assert p.returncode==0
asm=':'.join(str(t/(x+'.jar'))for x in ['asm','asm-tree','asm-analysis'])
subprocess.run(e+['-cp',asm,'-d',str(r/'build/tools'),'/workspace/medical45/Verify45.java'],check=True)
v=subprocess.run(['java','-cp',str(r/'build/tools')+':'+asm,'Verify45']+list(map(str,(r/'output').glob('*.jar'))),capture_output=True,text=True);print(v.stdout,v.stderr);assert v.returncode==0
(r/'test-results.json').write_text(json.dumps({'minecraft_runtime_tested':False,'boundary':'World, registry, render buffer and native value objects are simulated. Tests execute the new production helpers and actual reflection implementation. ASM analyzes every method in the three output jars.','behavior':p.stdout.strip(),'bytecode':v.stdout.strip()},indent=2)+'\n')
