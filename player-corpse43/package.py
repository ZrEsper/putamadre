from pathlib import Path
import hashlib,json,zipfile,shutil
r=Path(__file__).resolve().parent;root=r.parent;checks=json.loads((r/'test-results.json').read_text());assert checks['checks']=='PLAYER_CORPSE_CHECKS=149' and checks['native_corpse_equipment_before_after']==['HELMET_SLOT=0','HELMET_SLOT=5'];v=json.loads((r/'validation.json').read_text());e={}
for data in v.values():
 p=r/'output'/data['output'];assert hashlib.sha256(p.read_bytes()).hexdigest()==data['output_sha256'];shutil.copy2(p,root/'mods'/p.name);e['mods/'+p.name]=p.read_bytes()
m=root/'iso-medical30';mv=json.loads((m/'validation.json').read_text());mt=json.loads((m/'test-results.json').read_text());assert mt['checks']=='MEDICAL30_CHECKS=69';p=root/'mods'/mv['output'];assert hashlib.sha256(p.read_bytes()).hexdigest()==mv['output_sha256'];e['mods/'+p.name]=p.read_bytes();e['LEEME.txt']=(r/'GUIA.md').read_bytes();e['GUIA-CURACIONES.txt']=(m/'GUIA.md').read_bytes();e['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(e.items())).encode()
out=root/'distribuciones/Zombies-Poldo-Cadaver-Jugador-43-Curaciones-30.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(out)as z:assert z.testzip()is None and len(z.namelist())==7
print(out.name,hashlib.sha256(out.read_bytes()).hexdigest())
