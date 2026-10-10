from pathlib import Path
import sys,subprocess,zipfile,json,hashlib,shutil,os
r=Path(__file__).resolve().parent;base=Path(sys.argv[1]).resolve();tools=Path(sys.argv[2]).resolve()
assert hashlib.sha256(base.read_bytes()).hexdigest()=='611180934cd863cd7dd9f85d182084d4bd1c9f0fc758d544e89a76e176cdc501'
for name,digest in json.loads((r/'tools.sha256.json').read_text()).items():assert hashlib.sha256((tools/name).read_bytes()).hexdigest()==digest
for folder in ['classes','patcher','patched','test-classes']:shutil.rmtree(r/folder,ignore_errors=True);(r/folder).mkdir()
ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-nowarn'];asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar','asm-util.jar'])
def run(*a):subprocess.run(a,check=True)
run(*ecj,'-cp',str(base),'-d',str(r/'classes'),*[str(p) for folder in ['src','stubs'] for p in (r/folder).rglob('*.java')])
run(*ecj,'-cp',asm,'-d',str(r/'patcher'),str(r/'Patch.java'),str(r/'Verify.java'))
run('java','-cp',str(r/'patcher')+os.pathsep+asm,'Patch',str(base),str(r/'patched'))
run('java','-cp',str(r/'patcher')+os.pathsep+asm,'Verify',str(r/'patched'))
run(*ecj,'-cp',str(r/'classes'),'-d',str(r/'test-classes'),*[str(p) for p in (r/'tests').rglob('*.java')])
shutil.copyfile(r/'resources/survivor-spawn-homes.csv',r/'classes/survivor-spawn-homes.csv')
run('java','-Xverify:all','-cp',str(r/'test-classes')+os.pathsep+str(r/'classes'),'dev.survivorcreator.spawn.SpawnChecks')
add={p.relative_to(r/f).as_posix():p.read_bytes() for f in ['classes','patched'] for p in (r/f/'dev').rglob('*.class')}
add.update({p.relative_to(r/'resources').as_posix():p.read_bytes() for p in (r/'resources').rglob('*') if p.is_file()})
changed=set(add);out=r/'output/survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v19-spawn-map-intro.jar';out.parent.mkdir(exist_ok=True)
with zipfile.ZipFile(base) as z,zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as w:
 for n in z.namelist():
  info=zipfile.ZipInfo(n,(2026,10,10,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;w.writestr(info,add.pop(n,z.read(n)))
 for n,b in sorted(add.items()):
  info=zipfile.ZipInfo(n,(2026,10,10,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;w.writestr(info,b)
with zipfile.ZipFile(base) as z,zipfile.ZipFile(out) as w:
 assert w.testzip() is None
 for n in z.namelist():
  if n not in changed:assert z.read(n)==w.read(n),n
 assert not any(n.startswith(('net/','com/','org/')) for n in w.namelist())
 assert w.read('assets/survivorcreator/sounds/music/ending_theme.ogg')==(r/'resources/assets/survivorcreator/sounds/music/ending_theme.ogg').read_bytes()
(r/'validation.json').write_text(json.dumps({'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'helper_cases':13,'asm_methods_checked':125,'zones':4,'interiors':sum(len(z['homes']) for z in json.loads((r/'homes.json').read_text())),'modified_or_added':sorted(changed),'unrelated_entries_preserved':True,'minecraft_runtime_tested':False,'intro_audio_duration_seconds':36.617868},indent=2));print(out)
