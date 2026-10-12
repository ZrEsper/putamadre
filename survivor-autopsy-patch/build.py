from pathlib import Path
import sys,subprocess,tempfile,zipfile,json,hashlib,os
r=Path(__file__).resolve().parent;base=Path(sys.argv[1]);tools=Path(sys.argv[2]);asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar']);out=r.parent/'mods/survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v26-minimal-death-first-person.jar'
with tempfile.TemporaryDirectory() as t:
 t=Path(t);c=t/'classes';p=t/'patch';ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn']
 subprocess.run(ecj+['-d',str(c)]+[str(x) for x in (r/'src').rglob('*.java')],check=True)
 subprocess.run(ecj+['-cp',str(c),'-d',str(t/'tests')]+[str(x) for x in (r/'tests').rglob('*.java')],check=True)
 subprocess.run(['java','-Xverify:all','-cp',str(t/'tests')+os.pathsep+str(c),'dev.zomboid.autopsy.Checks'],check=True)
 subprocess.run(['java','-Xverify:all','-cp',str(t/'tests')+os.pathsep+str(c),'dev.zomboid.autopsy.RenderChecks'],check=True)
 subprocess.run(ecj+['-cp',asm,'-d',str(p),str(r/'Patch.java')],check=True)
 subprocess.run(['java','-cp',str(p)+os.pathsep+asm,'Patch',str(base),str(out),str(c)],check=True)
 with zipfile.ZipFile(out) as z:data={n:z.read(n) for n in z.namelist()}
 for p in (r/'resources').rglob('*'):
  if p.is_file():data[p.relative_to(r/'resources').as_posix()]=p.read_bytes()
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
  for n,b in data.items():z.writestr(n,b)
 with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
  assert b.testzip() is None
  changed=[n for n in a.namelist() if a.read(n)!=b.read(n)]
  assert set(changed)=={'dev/survivorcreator/hearing/DeathPanel.class','dev/survivorcreator/hearing/CreationAudio.class'},changed
 (r/'validation.json').write_text(json.dumps({'input_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed_original_entries':changed,'simulated_checks_passed':12,'minecraft_runtime_tested':False},indent=2)+'\n')
