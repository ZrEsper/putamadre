"""Package exactly the validated four mods and their offline guide."""
import pathlib,json,hashlib,subprocess,zipfile,shutil
R=pathlib.Path(__file__).resolve().parent;repo=R.parent
subprocess.run(['python',str(R/'make_guide.py')],cwd=R,check=True)
report=json.loads((R/'validation.json').read_text());assert report['behavior_checks_with_API_doubles']>=374
entries={}
for m in report['mods']:
 p=R/'output'/m['file'];assert hashlib.sha256(p.read_bytes()).hexdigest()==m['sha256'];shutil.copy2(p,repo/'mods'/p.name);entries['mods/'+p.name]=p.read_bytes()
for name in ['GUIA.html','GUIA.md','validation.json','professions.json']:entries[name]=(R/name).read_bytes()
entries['SHA256SUMS.txt']=(''.join(m['sha256']+'  mods/'+m['file']+'\n'for m in report['mods'])).encode()
p=repo/'distribuciones/Zombies-Poldo-Apocalipsis-31.zip'
with zipfile.ZipFile(p,'w',zipfile.ZIP_DEFLATED)as z:
 for name,data in sorted(entries.items()):info=zipfile.ZipInfo(name,(1980,1,1,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,data)
with zipfile.ZipFile(p)as z:assert z.testzip()is None;assert len(z.namelist())==len(entries)
print(str(p),p.stat().st_size,hashlib.sha256(p.read_bytes()).hexdigest())
