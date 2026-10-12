from pathlib import Path
import subprocess,tempfile,os,zipfile,json,hashlib,sys
r=Path(__file__).resolve().parent;root=r.parent;tools=Path('/workspace/zombies-only-patch/tools');base=Path(sys.argv[1]);out=root/'mods/zombie-remains-loot-1.6.1-neoforge-1.21.1-military-nvg-mythic.4-expanding-corpse.jar'
ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar'])
with tempfile.TemporaryDirectory() as t:
 c=Path(t)/'classes';p=Path(t)/'patch';tests=Path(t)/'tests'
 subprocess.run(ecj+['-cp',str(root/'mods/zomboid-zombie-gore-1.0.3-neoforge-1.21.1-cached-corpse-drops.jar'),'-d',str(c)]+[str(x) for directory in ['src','stubs'] for x in (r/directory).rglob('*.java')],check=True)
 subprocess.run(ecj+['-cp',str(c),'-d',str(tests),str(r/'tests/Checks.java'),str(r/'tests/NativeChecks.java'),str(r/'tests/NativeDropChecks.java')],check=True)
 subprocess.run(['java','-Xverify:all','-cp',str(tests)+os.pathsep+str(c),'Checks'],check=True)
 subprocess.run(ecj+['-cp',asm,'-d',str(p),str(r/'Patch.java')],check=True)
 subprocess.run(['java','-cp',str(p)+os.pathsep+asm,'Patch',str(base),str(out),str(c)],check=True)
 subprocess.run(['java','-Xverify:all','-cp',str(tests)+os.pathsep+str(c)+os.pathsep+str(out),'NativeChecks'],check=True)
 gore_base=root/'mods/zomboid-zombie-gore-1.0.3-neoforge-1.21.1-cached-corpse-drops.jar';gore_out=root/'mods/zomboid-zombie-gore-1.0.4-neoforge-1.21.1-expanding-corpse-drops.jar'
 with zipfile.ZipFile(gore_base) as z:data={n:z.read(n) for n in z.namelist()}
 data['dev/zomboid/gore/CorpseCollect.class']=(c/'dev/zomboid/gore/CorpseCollect.class').read_bytes()
 data['META-INF/neoforge.mods.toml']=data['META-INF/neoforge.mods.toml'].replace(b'version="1.0.3"',b'version="1.0.4"')
 with zipfile.ZipFile(gore_out,'w',zipfile.ZIP_DEFLATED) as z:
  for n,b in data.items():z.writestr(n,b)
 with zipfile.ZipFile(gore_base) as a,zipfile.ZipFile(gore_out) as b:
  assert b.testzip() is None
  assert set(n for n in a.namelist() if a.read(n)!=b.read(n))=={'dev/zomboid/gore/CorpseCollect.class','META-INF/neoforge.mods.toml'}
 subprocess.run(['java','-Xverify:all','-cp',str(tests)+os.pathsep+str(c)+os.pathsep+str(gore_out)+os.pathsep+str(out),'NativeDropChecks'],check=True)
 print(gore_out.name)

with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
 assert b.testzip() is None
 changed=[n for n in a.namelist() if a.read(n)!=b.read(n)]
 assert set(changed)=={'dev/zomboid/remains/ZombieRemainsLoot.class','dev/zomboid/remains/ZombieRemainsLoot$Corpse.class','dev/zomboid/remains/ZombieRemainsLoot$CorpseData.class'}
 assert not any(n.startswith(('net/minecraft/','net/neoforged/')) for n in b.namelist())
(r/'validation.json').write_text(json.dumps({'input_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed_original_entries':changed,'minecraft_runtime_tested':False,'storage_menu_checks':42,'native_save_load_checks_with_api_doubles':10,'gore_collector_integration_checks_with_api_doubles':7,'gore_input_sha256':hashlib.sha256(gore_base.read_bytes()).hexdigest(),'gore_output_sha256':hashlib.sha256(gore_out.read_bytes()).hexdigest()},indent=2)+'\n')
print(out.name)
