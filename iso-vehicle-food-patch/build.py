from pathlib import Path
import hashlib,json,subprocess,tempfile,os,zipfile,sys
r=Path(__file__).resolve().parent;tools=Path(sys.argv[1]).resolve();base=r.parent/'mods/dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.18-backpacks-ctrl-cutaway.jar';out=r.parent/'mods/dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.19-vehicles-food.jar'
for n,d in json.loads((r.parent/'doomsday-functional-patch/tools.sha256.json').read_text()).items():assert hashlib.sha256((tools/n).read_bytes()).hexdigest()==d
asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar']);ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn']
with tempfile.TemporaryDirectory() as temp:
 t=Path(temp)
 for cmd in [ecj+['-cp',str(base),'-d',str(t/'classes')]+[str(p) for p in (r/'src').rglob('*.java')],ecj+['-cp',asm,'-d',str(t/'patch'),str(r/'Patch.java')],['java','-cp',str(t/'patch')+os.pathsep+asm,'Patch',str(base),str(out),str(t/'classes')],ecj+['-cp',str(out),'-d',str(t/'tests')]+[str(p) for p in (r/'tests').rglob('*.java')]+[str(r.parent/'iso-backpack-patch/tests/net/minecraft/world/InteractionHand.java')],['java','-Xverify:all','-cp',str(t/'tests')+os.pathsep+str(out),'dev.zomboid.isotacz.Checks']]:subprocess.run(cmd,check=True)
 with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
  assert b.testzip() is None
  changed=[n for n in a.namelist() if a.read(n)!=b.read(n)]
  assert a.read('dev/zomboid/isotacz/BackpackInteraction.class')==b.read('dev/zomboid/isotacz/BackpackInteraction.class')
  assert a.read('dev/zomboid/isotacz/PanRecut.class')==b.read('dev/zomboid/isotacz/PanRecut.class')
  assert not any(n.startswith('net/') for n in set(b.namelist())-set(a.namelist()))
 (r/'validation.json').write_text(json.dumps({'base_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed_entries':changed,'simulated_checks_passed':11,'minecraft_runtime_tested':False},indent=2)+'\n')
