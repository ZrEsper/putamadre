from pathlib import Path
import subprocess,tempfile,os,zipfile,json,hashlib
r=Path(__file__).resolve().parent;root=r.parent;tools=Path('/workspace/zombies-only-patch/tools');base=root/'mods/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.7.jar';out=root/'mods/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.8-scope-zoom.jar'
ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar'])
with tempfile.TemporaryDirectory() as t:
 c=Path(t)/'classes';p=Path(t)/'patch';tests=Path(t)/'tests'
 subprocess.run(ecj+['-cp',str(base),'-d',str(c)]+[str(x) for x in (r/'src').rglob('*.java')],check=True)
 subprocess.run(ecj+['-cp',str(c)+os.pathsep+str(base),'-d',str(tests)]+[str(x) for x in (r/'tests').rglob('*.java')],check=True)
 subprocess.run(['java','-cp',str(tests)+os.pathsep+str(c),'Checks'],check=True)
 subprocess.run(['java','-cp',str(tests)+os.pathsep+str(c)+os.pathsep+str(base),'dev.zomboid.tacz.Flow'],check=True)
 subprocess.run(ecj+['-cp',asm,'-d',str(p),str(r/'Patch.java')],check=True)
 subprocess.run(['java','-cp',str(p)+os.pathsep+asm,'Patch',str(base),str(out),str(c)],check=True)
with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
 assert b.testzip() is None
 changed=[n for n in a.namelist() if a.read(n)!=b.read(n)]
 assert changed==['com/tacz/guns/client/event/CameraSetupEvent.class']
(r/'validation.json').write_text(json.dumps({'input_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed_original_entries':changed,'minecraft_runtime_tested':False},indent=2)+'\n')
print(out.name)
