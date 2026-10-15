from pathlib import Path
import hashlib,json,shutil,zipfile
root=Path(__file__).resolve().parent.parent
entries={}
for project in ['wall-breach41','screamer-sprint2']:
 r=root/project;v=json.loads((r/'validation.json').read_text());p=next((r/'output').glob('*.jar'));assert hashlib.sha256(p.read_bytes()).hexdigest()==v['output_sha256'];shutil.copy2(p,root/'mods'/p.name)
 entries['mods/'+p.name]=p.read_bytes();entries[project+'/GUIA.md']=(r/'GUIA.md').read_bytes();entries[project+'/validation.json']=(r/'validation.json').read_bytes()
entries['LEEME.txt']=('Cierra Minecraft y el servidor. Quita los JAR anteriores de zomboid-hordes y mutants-l4d2-addon. Copia los DOS JAR de mods a tus mods y a los del servidor. No dupliques versiones. Conserva el resto. Lee las dos guías.\n').encode()
entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(entries.items())).encode()
p=root/'distribuciones/Zombies-Poldo-Paredes-41-Screamer-40HP.zip'
with zipfile.ZipFile(p,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(p)as z:assert z.testzip()is None and len(z.namelist())==8
print(p.name,hashlib.sha256(p.read_bytes()).hexdigest())
