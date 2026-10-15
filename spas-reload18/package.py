from pathlib import Path
import zipfile,hashlib,json,shutil
r=Path(__file__).resolve().parent;root=r.parent
v=json.loads((r/'validation.json').read_text());p=next((r/'output').glob('*.jar'));assert hashlib.sha256(p.read_bytes()).hexdigest()==v['output_sha256']
t=json.loads((r/'test-results.json').read_text());assert t['original_crash_reproduced'] and t['result'].startswith('6421 checks passed')
shutil.copy2(p,root/'mods'/p.name)
entries={'mods/'+p.name:p.read_bytes(),'LEEME.txt':(r/'GUIA.md').read_bytes()}
entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(entries.items())).encode()
out=root/'distribuciones/Zombies-Poldo-TaCZ-18-Recarga-SPAS.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(out)as z:assert z.testzip()is None
print(out.name,hashlib.sha256(out.read_bytes()).hexdigest())
