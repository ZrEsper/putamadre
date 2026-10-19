from pathlib import Path
import zipfile,json,re,subprocess,hashlib,io
r=Path(__file__).resolve().parent;base=Path('/workspace/medical45/release');gore=next(Path('/workspace/medical46/output').glob('*.jar'));jars=list((r/'output').glob('*.jar'))+[gore]+[p for p in base.glob('*.jar')if not p.name.startswith(('firstaid-','tacz-firstaid-compat-','zomboid-zombie-gore-'))]
classes=set()
for p in jars:
 with zipfile.ZipFile(p)as z:classes.update(n[:-6].replace('/','.')for n in z.namelist()if n.endswith('.class'))
b=subprocess.run(['git','show','3286f6236e99c6a2e692833c2ded67a7187704d0:mods/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.10-addon-compat.jar'],cwd='/workspace/putamadre',capture_output=True,check=True).stdout
with zipfile.ZipFile(io.BytesIO(b))as z:classes.update(n[:-6].replace('/','.')for n in z.namelist()if n.endswith('.class'))
refs=set()
for p in Path('/workspace/medical45/src').rglob('*.java'):
 s=p.read_text().replace('ichttt.mods.firstaid.survival.TreatmentV33','ichttt.mods.firstaid.compat.lrtactical.TreatmentV33').replace('ru.ranazy.tacz_firstaid_compat.compat.firstaid.BodypartSlot','ru.ranazy.tacz_firstaid_compat.compat.firstaid.BodypartHitbox$BodypartSlot')
 refs.update(re.findall(r'"((?:ichttt|dev\.zomboid|com\.raiiiden|mod\.cdv|de\.maxhenkel|ru\.|com\.tacz)[A-Za-z0-9_.$]+)"',s))
missing=sorted(x for x in refs if x not in classes);assert not missing,missing
report=json.loads((r/'test-results.json').read_text());report['owned_reflective_class_names_checked']=sorted(refs);report['tacz_namespace_audit_baseline']='pz.10; no TaCZ classes modified'
t=Path('/workspace/putamadre/hordes-melee-interiors-patch/build/tools');asm=':'.join(str(t/(s+'.jar'))for s in ['asm','asm-tree','asm-analysis'])
p=subprocess.run(['java','-cp','/workspace/medical45/build/tools:'+asm,'Verify45',*map(str,(r/'output').glob('*.jar'))],check=True,text=True,capture_output=True);report['asm_verification']=p.stdout.strip();print(p.stdout.strip())
p=subprocess.run(['java','-cp','/workspace/medical45/build/tools','ModuleCheck45',*map(str,jars)],check=True,text=True,capture_output=True);report['module_verification']=p.stdout.strip();print(p.stdout.strip());(r/'test-results.json').write_text(json.dumps(report,indent=2)+'\n')
archive=Path('/workspace/outputs/FirstAid-3.6.16-Crash-Mordeduras.zip')
with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED)as z:
 for p in (r/'output').glob('*.jar'):z.write(p,'mods/'+p.name)
 for name in ['LEEME.md','test-results.json']:z.write(r/name,name)
 z.writestr('SHA256SUMS.txt',''.join(hashlib.sha256(p.read_bytes()).hexdigest()+'  mods/'+p.name+'\n'for p in (r/'output').glob('*.jar')))
print('ARCHIVE_SHA256='+hashlib.sha256(archive.read_bytes()).hexdigest(),'REFERENCES='+str(len(refs)))
