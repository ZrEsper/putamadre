from pathlib import Path
import os,subprocess,tempfile,zipfile,json,hashlib
r=Path(__file__).resolve().parent;tools=Path('/workspace/zombies-only-patch/tools');base=r.parent/'mods/zombie-remains-loot-1.6.1-neoforge-1.21.1-military-nvg-mythic.4-expanding-corpse.jar';out=r.parent/'mods/zombie-remains-loot-1.6.1-neoforge-1.21.1-military-nvg-mythic.5-valorant-lrarmor.jar'
cp=os.pathsep.join([str(base),str(Path('/workspace/lrarmor-port/build/moddev/artifacts/neoforge-21.1.255.jar'))]+[str(p) for p in Path('/workspace/lrarmor-gradle-cache/caches/modules-2/files-2.1').rglob('*.jar')]);asm=os.pathsep.join(str(tools/x) for x in ['asm.jar','asm-tree.jar','asm-analysis.jar'])
with tempfile.TemporaryDirectory() as t:
 c=Path(t)/'classes';patch=Path(t)/'patch';ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn']
 subprocess.run(ecj+['-cp',cp,'-d',str(c)]+[str(p) for p in (r/'src').rglob('*.java')],check=True)
 subprocess.run(ecj+['-cp',asm,'-d',str(patch),str(r/'Patch.java')],check=True)
 subprocess.run(['java','-cp',str(patch)+os.pathsep+asm,'Patch',str(base),str(out),str(c)],check=True)
with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
 assert b.testzip() is None
 changed=[n for n in a.namelist() if a.read(n)!=b.read(n)];assert changed==['dev/zomboid/remains/ZombieRemainsLoot.class']
(r/'jar-validation.json').write_text(json.dumps({'changed_original_entries':changed,'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'compiled_against_real_minecraft_apis':True},indent=2)+'\n')
