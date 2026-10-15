from pathlib import Path
import zipfile,hashlib,json
r=Path(__file__).resolve().parent;root=r.parent
src=root/'distribuciones/Zombies-Poldo-Escasez-NVG-Reanimacion-33.zip';out=root/'distribuciones/Zombies-Poldo-Escasez-NVG-Reanimacion-34.zip'
with zipfile.ZipFile(src)as z:entries={n:z.read(n)for n in z.namelist()if n!='SHA256SUMS.txt'}
old=next(n for n in entries if n.startswith('mods/zomboid-hordes-'));del entries[old]
v=json.loads((r/'validation.json').read_text());p=root/'mods'/v['output'];assert hashlib.sha256(p.read_bytes()).hexdigest()==v['output_sha256'];entries['mods/'+p.name]=p.read_bytes()
entries['CORRECCION-34.md']=(r/'GUIA.md').read_bytes();entries['validation-hotfix34.json']=(r/'validation.json').read_bytes()
entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(entries.items())).encode()
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):
  i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(out)as z:assert z.testzip()is None;assert len([n for n in z.namelist()if n.startswith('mods/')])==5
print(out.name,hashlib.sha256(out.read_bytes()).hexdigest())
