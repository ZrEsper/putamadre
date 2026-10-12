from pathlib import Path
import zipfile,hashlib,json,tempfile,subprocess,os,sys
r=Path(__file__).resolve().parent;base=Path(sys.argv[1]).resolve();tools=Path(sys.argv[2]).resolve();out=r.parent/'mods/dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.18-backpacks-ctrl-cutaway.jar'
assert hashlib.sha256(base.read_bytes()).hexdigest()=='759e099a7415c6e27748f01f8e10dba24122f85a13fb18ee0324dd724135618c','Expected uploaded Iso .17'
asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar']);ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn']
with tempfile.TemporaryDirectory() as temp:
 t=Path(temp);classes=t/'classes';patch=t/'patch';tests=t/'tests'
 for cmd in [ecj+['-cp',str(base),'-d',str(classes)]+[str(p) for p in (r/'src').rglob('*.java')],ecj+['-cp',asm,'-d',str(patch),str(r/'Patch.java')],['java','-cp',str(patch)+os.pathsep+asm,'Patch',str(base),str(out),str(classes)],ecj+['-cp',str(out),'-d',str(tests)]+[str(p) for p in (r/'tests').rglob('*.java')],['java','-Xverify:all','-cp',str(tests)+os.pathsep+str(out),'dev.zomboid.isotacz.BackpackChecks']]:subprocess.run(cmd,check=True)
 with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
  assert b.testzip() is None
  changed=[n for n in a.namelist() if a.read(n)!=b.read(n)];assert set(changed)=={'dev/zomboid/isotacz/Bridge.class','com/cleannrooster/dungeons_iso/util/InteractionTargeting.class','com/cleannrooster/dungeons_iso/compat/SodiumCompat.class','META-INF/neoforge.mods.toml'}
  assert not any(n.startswith(('net/','tests/')) for n in set(b.namelist())-set(a.namelist()))
 (r/'validation.json').write_text(json.dumps({'input_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed_entries':changed,'minecraft_runtime_tested':False,'simulated_checks_passed':15},indent=2)+'\n')
