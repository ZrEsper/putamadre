from pathlib import Path
import hashlib,json,os,subprocess,tempfile,sys,zipfile
r=Path(__file__).resolve().parent;base=Path(sys.argv[1]);tools=Path(sys.argv[2]);out=r.parent/'mods/dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.21-ground-preserved.jar';asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar']);ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn']
assert hashlib.sha256(base.read_bytes()).hexdigest()==hashlib.sha256((r.parent/'mods/dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.19-vehicles-food.jar').read_bytes()).hexdigest()
with tempfile.TemporaryDirectory() as tmp:
 t=Path(tmp)
 for cmd in [ecj+['-cp',str(base),'-d',str(t/'classes')]+[str(f) for f in (r/'src').rglob('*.java')],ecj+['-cp',asm,'-d',str(t/'patch'),str(r/'Patch.java')],['java','-cp',str(t/'patch')+os.pathsep+asm,'Patch',str(base),str(out),str(t/'classes')],ecj+['-cp',str(out),'-d',str(t/'tests')]+[str(f) for f in (r/'tests').rglob('*.java')],['java','-Xverify:all','-cp',str(t/'tests')+os.pathsep+str(out),'dev.zomboid.isotacz.ViewChecks']]:subprocess.run(cmd,check=True)
with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
 assert b.testzip() is None
 changed=[n for n in a.namelist() if a.read(n)!=b.read(n)]
 assert b.read('dev/zomboid/isotacz/BackpackInteraction.class')==a.read('dev/zomboid/isotacz/BackpackInteraction.class')
(r/'validation.json').write_text(json.dumps({'input_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed_entries':changed,'simulated_checks_passed':12,'minecraft_runtime_tested':False},indent=2)+'\n')
