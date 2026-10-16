from pathlib import Path
from zipfile import ZipFile,ZIP_DEFLATED
import json,hashlib
root=Path('work/datapack-perf');out=root/'release';out.mkdir(exist_ok=True)
dyn=next(Path('attachments/71261d4a-14b3-45e4-9253-3802ee563258').glob('*.zip'))
with ZipFile(dyn) as z:
 meta=json.loads(z.read('pack.mcmeta'));overlays=meta['overlays']['entries'];dirs=[e['directory'] for e in overlays]
 files={n:z.read(n) for n in z.namelist() if not n.endswith('/') and not any(n.startswith(d+'/') for d in dirs)}
 for e in overlays:
  lo,hi=e['formats']
  if lo<=48<=hi:
   prefix=e['directory']+'/'
   for n in z.namelist():
    if n.startswith(prefix) and not n.endswith('/'):files[n[len(prefix):]]=z.read(n)
files['pack.mcmeta']=json.dumps({'pack':{'pack_format':48,'description':"Tschipcraft Dynamic Lights 1.9.3 — NeoForge 1.21.1 Curios cache patch"}},indent=2).encode()
base='data/dynamiclights/function/'
core=files[base+'internal/sources/core.mcfunction'].decode();lines=core.splitlines();new=[];added=False
for line in lines:
 if '$curios_install' in line:
  if not added:new.append('execute if score $curios_install ts.dl.version matches 1 as @s[type=minecraft:player] run function dynamiclights:internal/sources/mod_support/curios/cached');added=True
 else:new.append(line)
assert added
files[base+'internal/sources/core.mcfunction']=('\n'.join(new)+'\n').encode()
files[base+'perf_load.mcfunction']=b'''# Per-player cache. Refresh immediately after reload/login if absent.
scoreboard objectives add ts.dl.curiosAge dummy
scoreboard players reset @a ts.dl.curiosAge
'''
tag='data/minecraft/tags/function/load.json';load=json.loads(files[tag]);load['values'].insert(0,'dynamiclights:perf_load');files[tag]=json.dumps(load,indent=2).encode()
files[base+'internal/sources/mod_support/curios/cached.mcfunction']=b'''# Preserve parsed light scores between checks; moving lights still update every tick.
scoreboard players add @s ts.dl.curiosAge 0
execute if score @s ts.dl.curiosAge matches 0 run function dynamiclights:internal/sources/mod_support/curios/refresh_neoforge
scoreboard players add @s ts.dl.curiosAge 1
execute if score @s ts.dl.curiosAge matches 10.. run scoreboard players set @s ts.dl.curiosAge 0
'''
files[base+'internal/sources/mod_support/curios/refresh_neoforge.mcfunction']=b'''# Clear shared scratch data FIRST: failed reads must never reuse another player's equipment.
data remove storage dynamiclights:curios stacks
data modify storage dynamiclights:curios stacks set from entity @s neoforge:attachments.curios:inventory.Curios
# Always reset parsed scores, including when all accessories were removed.
function dynamiclights:internal/sources/mod_support/curios/main
'''
files[base+'uninstall.mcfunction']+=b'\nscoreboard objectives remove ts.dl.curiosAge\n'
# Ensure timers/explosions and light placement are byte-identical to effective 1.21.1 inputs.
for k in [base+'internal/main.mcfunction',base+'internal/explosion_detection/tnt.mcfunction']:
 with ZipFile(dyn) as z:assert files[k]==z.read(k)
for n,b in files.items():
 if n.endswith('.json') or n=='pack.mcmeta':json.loads(b)
with ZipFile(out/'dynamiclights-1.9.3-neoforge-1.21.1-curios-cache.1.zip','w',ZIP_DEFLATED) as z:
 for n,b in files.items():z.writestr(n,b)
# Mosslorn: coalesce repeated player serialization, preserving values and all loot tables.
moss=next(Path('attachments/546aff29-25f4-4f30-92bc-cb05b428ace5').glob('*.zip'))
with ZipFile(moss) as z:mf={n:z.read(n) for n in z.namelist() if not n.endswith('/')}
changed=[]
for tail in ['loot/scan/seleccionar','force/cerca','zona/consulta']:
 key='data/mosslorn/function/'+tail+'.mcfunction';s=mf[key].decode();ls=s.splitlines();idx=[i for i,l in enumerate(ls) if 'from entity @s Dimension' in l or 'data get entity @s Pos[' in l]
 assert len(idx)>=2
 last=idx[-1];first=idx[0];res=[]
 for i,l in enumerate(ls):
  if i==first:res.append('data modify storage mosslorn:runtime perf_player set from entity @s')
  l=l.replace('data get entity @s Pos[','data get storage mosslorn:runtime perf_player.Pos[').replace('set from entity @s Dimension','set from storage mosslorn:runtime perf_player.Dimension')
  res.append(l)
  if i==last:res.append('data remove storage mosslorn:runtime perf_player')
 mf[key]=('\n'.join(res)+'\n').encode();changed.append(key)
meta=json.loads(mf['pack.mcmeta']);meta['pack']['description']='Mosslorn v14: same loot + coalesced player NBT reads';mf['pack.mcmeta']=json.dumps(meta,indent=2).encode()
with ZipFile(moss) as src:
 for n,b in mf.items():
  if n not in changed+['pack.mcmeta']:assert b==src.read(n)
with ZipFile(out/'mosslorn-expanded.14-loot-preserved-nbt-cache.zip','w',ZIP_DEFLATED) as z:
 for n,b in mf.items():z.writestr(n,b)
# Counter simulation: immediate check then every ten calls, independently per player.
for steps in [1,10,20,100]:
 age=0;checks=0
 for tick in range(steps):
  if age==0:checks+=1
  age+=1
  if age>=10:age=0
 assert checks==(steps+9)//10
print('PASS JSON, effective overlays, unchanged light/TNT tick, unchanged Mosslorn loot, cache interval model')
(out/'LEEME.txt').write_text('''Optimización de datapacks — NeoForge 1.21.1

Cerrar el mundo y sustituir los DOS ZIP originales por los dos ZIP de este paquete.
Conservar los nombres nuevos y quitar las versiones anteriores; no usar duplicados.
Si Paxi los instala automáticamente, sustituir los ZIP también en su carpeta de origen.
Los ZIP de datapack permanecen comprimidos; no poner el ZIP contenedor en datapacks.
Al volver al mundo ejecutar /reload y /datapack list enabled para comprobar activación.

Dynamic Lights: Curios se consulta cada 10 ticks (0.5 s a 20 TPS), con una única lectura
NBT por jugador y consulta. Se conserva la luz dinámica por tick, incluido movimiento,
objetos en la mano y protección de explosiones. Accesorios Curios pueden tardar hasta
0.5 s en actualizar su luz al equiparse/quitarse. Versión especializada en NeoForge 1.21.1.
Se limpia la caché temporal antes de cada jugador y se restablece al retirar accesorios.

Mosslorn: posiciones/dimensión se extraen de una sola instantánea por invocación en
selección de escaneo, búsqueda cercana del reinicio y consulta de zona.
Se conservan TODAS las tablas de loot, cantidades, probabilidades y límites de escaneo.

Farmer's Delight Cutting Compat, Supplementaries Compat y Torch Fix: sin cambios;
no tienen funciones por tick. MinecraftTransportSimulator-master es código fuente,
NO un datapack: no instalar ese ZIP en datapacks ni en mods.

Validación estática y simulación de intervalos realizadas. No ejecutado dentro de Minecraft.
El porcentaje real de mejora requiere repetir Spark 120 s en la misma zona/actividad.
Las correcciones no garantizan eliminar todos los tirones ni arreglan errores de otros packs.
Para revertir, cerrar el mundo y restaurar los dos ZIP originales.
''')
(out/'SHA256.txt').write_text('\n'.join(hashlib.sha256(p.read_bytes()).hexdigest()+'  '+p.name for p in out.glob('*.zip'))+'\n')
with ZipFile(root/'Datapacks-Optimizacion-1.21.1.1.zip','w',ZIP_DEFLATED) as z:
 for p in out.iterdir():z.write(p,p.name)
