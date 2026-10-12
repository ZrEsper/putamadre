from pathlib import Path
import subprocess,tempfile,os,zipfile,json,hashlib,sys
r=Path(__file__).resolve().parent;root=r.parent;tools=Path('/workspace/zombies-only-patch/tools');inputs={'hordes':Path(sys.argv[1]),'gore':Path(sys.argv[2])};outputs={'hordes':root/'mods/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.19-cached-path-retries.jar','gore':root/'mods/zomboid-zombie-gore-1.0.3-neoforge-1.21.1-cached-corpse-drops.jar'}
ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar']);results=[]
with tempfile.TemporaryDirectory() as t:
 c=Path(t)/'classes';p=Path(t)/'patch';tests=Path(t)/'tests'
 subprocess.run(ecj+['-cp',str(inputs['hordes']),'-d',str(c)]+[str(x) for x in (r/'src').rglob('*.java')],check=True)
 subprocess.run(ecj+['-cp',asm,'-d',str(p),str(r/'Patch.java')],check=True)
 if list((r/'tests').rglob('*.java')):
  subprocess.run(ecj+['-cp',str(c)+os.pathsep+str(inputs['hordes']),'-d',str(tests)]+[str(x) for x in (r/'tests').rglob('*.java')],check=True)
  for test in ['Checks','dev.zomboid.gore.CollectChecks']:subprocess.run(['java','-Xverify:all','-cp',str(tests)+os.pathsep+str(c)+os.pathsep+str(inputs['hordes']),test],check=True)
 for mode,base in inputs.items():
  out=outputs[mode];subprocess.run(['java','-cp',str(p)+os.pathsep+asm,'Patch',mode,str(base),str(out),str(c)],check=True)
  with zipfile.ZipFile(out) as z:data={n:z.read(n) for n in z.namelist()}
  if mode=='gore':data['META-INF/neoforge.mods.toml']=data['META-INF/neoforge.mods.toml'].replace(b'version="1.0.2"',b'version="1.0.3"')
  with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
   for n,b in data.items():z.writestr(n,b)
  with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
   assert b.testzip() is None;changed=[n for n in a.namelist() if a.read(n)!=b.read(n)];print(mode,'changed original entries',changed)
   expected={'dev/zomboid/reactive/R.class','dev/zomboid/hordes/ChunkReflection.class','dev/zomboid/hordes/PathThrottle.class','dev/zomboid/hordes/PathThrottle$State.class','dev/zomboid/hordes/PathThrottle$Budget.class'} if mode=='hordes' else {'dev/zomboid/gore/R.class','dev/zomboid/gore/Events.class','META-INF/neoforge.mods.toml'}
   assert set(changed)==expected
  results.append({'file':out.name,'input_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed':changed})
(r/'validation.json').write_text(json.dumps({'artifacts':results,'minecraft_runtime_tested':False},indent=2)+'\n')
