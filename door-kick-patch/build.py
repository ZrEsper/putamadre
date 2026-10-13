from pathlib import Path
import subprocess,zipfile,hashlib,json
root=Path(__file__).resolve().parent
base=root.parent/'mods/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.11-interaction-priority.jar'
out=root.parent/'mods/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.12-instant-door-kick.jar'
work=Path('/workspace/door-kick-work');cp=':'.join(map(str,(root.parent/'weapon-interaction-patch/build/tools').glob('asm*.jar')))
subprocess.run(['java','-jar','/workspace/onboarding/tools/ecj.jar','-21','-proc:none','-warn:none','-cp',cp,'-d',str(work/'classes'),str(root/'Patch.java')],check=True)
subprocess.run(['java','-cp',str(work/'classes')+':'+cp,'Patch',str(base),str(out)],check=True)
with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
 assert b.testzip() is None
 changed=[n for n in a.namelist() if a.read(n)!=b.read(n)]
 assert len(changed)==3
 assert set(a.namelist())==set(b.namelist())
 assert all(n.endswith('.class') for n in changed)
report={'input_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed':changed,'minecraft_runtime_tested':False}
(root/'validation.json').write_text(json.dumps(report,indent=2)+'\n')
print(out)
