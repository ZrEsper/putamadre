from pathlib import Path
import subprocess, json, zipfile, hashlib, re
r=Path(__file__).resolve().parent;t=Path('/workspace/putamadre/hordes-melee-interiors-patch/build/tools');ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(x+'.jar'))for x in ['asm','asm-tree','asm-analysis']);cp=':'.join(str(r/'output'/f'{x}.jar')for x in ['gore','fa','npc'])
subprocess.run(ecj+['-cp',cp,'-d',str(r/'build/tests')]+list(map(str,(r/'tests').rglob('*.java'))),check=True)
results=[]
for c in ['dev.zomboid.gore.Test45','dev.zomboid.gore.Render45']:
 p=subprocess.run(['java','-Xverify:all','-cp',str(r/'build/tests')+':'+cp,c],check=True,text=True,capture_output=True);print(p.stdout,end='');results.append(p.stdout.strip())
subprocess.run(ecj+['-cp',asm,'-d',str(r/'build/tools'),str(r/'Verify45.java')],check=True)
p=subprocess.run(['java','-cp',str(r/'build/tools')+':'+asm,'Verify45']+list(map(str,(r/'output').glob('*.jar'))),check=True,text=True,capture_output=True);print(p.stdout,end='');results.append(p.stdout.strip())
inputs=json.loads((r/'inputs.json').read_text());preserved=0
for name,source in zip(['fa','compat','gore','rag','corpse','npc'],inputs):
 with zipfile.ZipFile(source)as original,zipfile.ZipFile(r/'output'/f'{name}.jar')as patched:
  assert patched.testzip()is None
  assert len(patched.namelist())==len(set(patched.namelist()))
  for n in original.namelist():
   if not n.endswith('.class'):
    assert original.read(n)==patched.read(n),(name,n);preserved+=1
  assert not any(n.startswith('net/minecraft/')for n in patched.namelist())
results.append(f'UNCHANGED_ORIGINAL_RESOURCES_BEFORE_METADATA_PACKAGING={preserved}')
(r/'test-results.json').write_text(json.dumps({'minecraft_runtime_tested':False,'boundary_objects':'Minecraft, native packet IO and retained-body registration simulated in behavioral tests; ASM checks use actual patched classfiles','results':results},indent=2)+'\n')
print(results[-1])
