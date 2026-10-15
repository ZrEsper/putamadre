from pathlib import Path
import hashlib,json,zipfile,shutil
r=Path(__file__).resolve().parent;root=r.parent;entries={}
for project,expected in [('nvg-visor3','NVG3_CHECKS=935'),('iso-audio29','ISO29_CHECKS=380')]:
 p=root/project;v=json.loads((p/'validation.json').read_text());assert json.loads((p/'test-results.json').read_text())['checks']==expected
 jar=p/'output'/v['output'];assert hashlib.sha256(jar.read_bytes()).hexdigest()==v['output_sha256'];shutil.copy2(jar,root/'mods'/jar.name);entries['mods/'+jar.name]=jar.read_bytes();entries[project+'-GUIA.txt']=(p/'GUIA.md').read_bytes()
entries['LEEME.txt']='Cierra Minecraft. Reemplaza Fracture Point NVG .2 por .3 en clientes y servidor, y dungeons_iso .28 por .29 en clientes. No dupliques mods. N baja el visor y enciende su filtro; N otra vez lo sube y apaga. Clic derecho apunta; para interactuar con arma, deja de apuntar y usa Mayús + clic derecho. Lee las dos guías. Pruebas automatizadas aprobadas; pendiente de prueba dentro de Minecraft.\n'.encode()
entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(entries.items())).encode()
out=root/'distribuciones/Zombies-Poldo-NVG-Manual-3-Iso-Audio-29.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(out)as z:assert z.testzip()is None
print(out.name,hashlib.sha256(out.read_bytes()).hexdigest())
