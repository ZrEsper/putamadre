from pathlib import Path
import subprocess,tempfile,os,zipfile,json,hashlib
r=Path(__file__).resolve().parent;root=r.parent;tools=Path('/workspace/zombies-only-patch/tools');base=root/'mods/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.19-cached-path-retries.jar';out=root/'mods/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.21-half-population.jar'
ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar'])
with tempfile.TemporaryDirectory() as t:
 c=Path(t)/'classes';p=Path(t)/'patch';subprocess.run(ecj+['-d',str(c)]+[str(x) for x in (r/'src').rglob('*.java')],check=True);subprocess.run(ecj+['-cp',asm,'-d',str(p),str(r/'Patch.java')],check=True);subprocess.run(['java','-cp',str(p)+os.pathsep+asm,'Patch',str(base),str(out),str(c)],check=True)
with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
 assert b.testzip() is None;changed=[n for n in a.namelist() if a.read(n)!=b.read(n)];assert set(changed)=={'dev/zomboid/danger/Zones.class','dev/zomboid/hordes/HordeRules.class'}
(r/'validation.json').write_text(json.dumps({'input_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed':changed,'population_multiplier':.5,'minecraft_runtime_tested':False},indent=2)+'\n')
