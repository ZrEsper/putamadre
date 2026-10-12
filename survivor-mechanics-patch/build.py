from pathlib import Path
import hashlib,json,sys,subprocess,tempfile,os,zipfile
r=Path(__file__).resolve().parent;base=Path(sys.argv[1]);tools=Path(sys.argv[2]);out=r.parent/'mods/survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v28-mechanics.jar';asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar']);ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn']
with tempfile.TemporaryDirectory() as tmp:
 t=Path(tmp)
 for cmd in [ecj+['-d',str(t/'classes')]+[str(f) for f in (r/'src').rglob('*.java')],ecj+['-cp',asm,'-d',str(t/'patch'),str(r/'Patch.java')],['java','-cp',str(t/'patch')+os.pathsep+asm,'Patch',str(base),str(out),str(t/'classes')],ecj+['-cp',str(out),'-d',str(t/'tests')]+[str(f) for f in (r/'tests').rglob('*.java')],['java','-Xverify:all','-cp',str(t/'tests')+os.pathsep+str(out),'dev.zomboid.skills.ListChecks']]:subprocess.run(cmd,check=True)
with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
 assert b.testzip() is None
 changed=[n for n in a.namelist() if a.read(n)!=b.read(n)]
 assert changed==['dev/survivorcreator/skills/ActiveSkills.class'],changed
 assert 'skill.survivorcreator.mechanics' in b.read('assets/survivorcreator/lang/en_us.json').decode()
(r/'validation.json').write_text(json.dumps({'input_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed_original_entries':changed,'simulated_checks_passed':4,'minecraft_runtime_tested':False},indent=2)+'\n')
