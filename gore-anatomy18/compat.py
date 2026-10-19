"""Reproducible metadata hotfix; run build.py first."""
from pathlib import Path
import zipfile,hashlib,json,tomllib,re,shutil
r=Path(__file__).resolve().parent
cfg='META-INF/neoforge.mods.toml'
specs=[('../gore-anatomy14/base/dismember3.jar','mobdismembermentneoforge-1.21.1-1.4.0-gore18-compat.8.jar','1ba80372334b4898f889cd2b071d8443978ed133687643f08d7858dfd964a3e6'),('../gore-anatomy14/base/remains16.jar','zombie-remains-loot-1.6.1-neoforge-1.21.1-physical-corpse-loot.21-gore18.jar','1166b398b856226768eda77f2877999e6e8c32784ee9202608abb36c144a587a'),('output/ragdollified-1.21.1-1.1.5-gore-light.11-wounds.jar','ragdollified-1.21.1-1.1.5-gore-light.12-wounds-compat.jar',None)]
report={}
for src,name,pin in specs:
 p=r/src
 if pin:assert hashlib.sha256(p.read_bytes()).hexdigest()==pin
 with zipfile.ZipFile(p)as z:original={n:z.read(n)for n in z.namelist()}
 entries=original.copy();entries[cfg]=entries[cfg].replace(b'[1.0.13,1.0.14)',b'[1.0.18,1.0.19)')
 assert all(entries[n]==v for n,v in original.items()if n!=cfg)
 out=r/'output'/name
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
  for n,v in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,v)
 report[name]={'metadata_only':True,'sha256':hashlib.sha256(out.read_bytes()).hexdigest()}
gore=r/'output/zomboid-zombie-gore-1.0.18-neoforge-1.21.1-damage-scaled-wounds.jar'
with zipfile.ZipFile(gore)as z:version=tomllib.loads(z.read(cfg).decode())['mods'][0]['version']
assert version=='1.0.18'
for name in report:
 with zipfile.ZipFile(r/'output'/name)as z:meta=tomllib.loads(z.read(cfg).decode())
 deps=[d for ds in meta['dependencies'].values()for d in ds if d['modId']=='zomboid_gore'];assert len(deps)==1
 dep=deps[0];assert dep['type']=='required' and dep['versionRange']=='[1.0.18,1.0.19)'
 nums=lambda s:tuple(map(int,s.split('.')))
 lo,hi=dep['versionRange'][1:-1].split(',');assert nums(lo)<=nums(version)<nums(hi)
 report[name]['gore_dependency']=dep
report[gore.name]={'version':version,'sha256':hashlib.sha256(gore.read_bytes()).hexdigest()}
readme='''GORE: HERIDAS SEGUN DANO Y PERDIDA DE ENERGIA 1.0.18

Cierra Minecraft. Sustituye las versiones anteriores de estos CUATRO mods por los JAR de este ZIP: Gore, Ragdollified, Zombie Remains Loot y Mob Dismemberment. Deja una sola version de cada uno.
Mob Dismemberment: solo cliente. Los otros tres: cliente y servidor.
Conserva Splatter blood-budget.2 y las demas dependencias existentes.

Gore .18: las heridas de bala dependen del dano recibido; marca pequena, perforacion, trauma local y destruccion progresiva. Se conserva la piel alrededor de impactos leves. Colaterales: cada cuerpo deja el 65% del dano para el siguiente (100%, 65%, 42.25%, ...). Los cadaveres usan el dano nativo de la bala en vez de un valor fijo. Esqueleto reconstruido con la referencia: craneo cubico ancho, cuencas rectangulares, mandibula abierta, tres costillas gruesas por lado y huesos rectos. Color hueso gris blanco, sin el tinte crema anterior. Ojos, corazon, higado y estomago se desprenden con fisica visual independiente: gravedad, rotacion, colision de bloques y rebote. Intestinos siguen sujetos. Maximo 24 piezas, duran 30 segundos; no son items recogibles ni entidades de servidor. Cada zona puede desaparecer completamente; conserva la amputacion existente. Sonidos y chorros arteriales de .14 conservados, con presupuesto de particulas.
Los complementos conservan sus clases y recursos salvo el renderer de Ragdoll ya actualizado para la luz. Sus dependencias ahora aceptan Gore 1.0.18. No borra el loot al destruir la geometria.
Validacion: intervalos TOML comprobados contra la version real; integridad de recursos y clases comprobada. No se ha ejecutado Minecraft en este entorno. Se incluye vista-capas.png como vista tecnica, no captura del juego.
'''
archive=r.parent/'distribuciones/Zombies-Poldo-Gore18-Heridas-Balistica.zip'
with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED)as z:
 for name in report:
  p=r/'output'/name;z.write(p,name);shutil.copyfile(p,r.parent/'mods'/name)
 z.write(r/'vista-capas.png','vista-capas.png');z.writestr('LEEME.txt',readme);z.writestr('SHA256.txt','\n'.join(v['sha256']+'  '+k for k,v in report.items())+'\n')
(r/'compat-validation.json').write_text(json.dumps(report,indent=2)+'\n')
print('Validated Gore 1.0.18 against all three consumers; preserved all class/resource bytes.')
print(archive)
