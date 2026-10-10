from pathlib import Path
import json,gzip,zipfile,collections
r=Path(__file__).resolve().parent
catalog=json.loads(gzip.decompress((r/'catalogo.json.gz').read_bytes()))
groups=collections.defaultdict(list)
for e in catalog:groups[(e['dim'],e['x']//16,e['z']//16)].append(e)
f={}
def fn(name,text):f['data/mosslorn/function/force/'+name+'.mcfunction']=text.strip()+'\n'
bootstrap=['data modify storage mosslorn:force chunks set value {}']
for dim in sorted({k[0] for k in groups}):bootstrap.append('data modify storage mosslorn:force chunks.'+json.dumps(dim)+' set value {}')
for i,((dim,x,z),entries) in enumerate(sorted(groups.items())):
 name='catalogo/'+str(i);bootstrap.append('function mosslorn:force/'+name)
 fn(name,'data modify storage mosslorn:force chunks.'+json.dumps(dim)+'.'+json.dumps(str(x)+'_'+str(z))+' set value '+json.dumps({'entries':entries,'done':0},separators=(',',':')))
fn('catalogo', '\n'.join(bootstrap))
fn('reiniciar','''scoreboard players set #fi ml_tmp 0
scoreboard players set #ftotal ml_tmp 0
execute store result score #ftotal ml_tmp run data get storage mosslorn:registro cofres
scoreboard players set #force_build ml_tmp 1
scoreboard players set #force_clock ml_tmp 0
scoreboard players set #force_reset ml_tmp 0
scoreboard players set #force_missing ml_tmp 0
scoreboard players set #force_done ml_tmp 0
scoreboard players set #force_chunks ml_tmp 3836
scoreboard players set #auto_scan ml_config 0
data modify storage mosslorn:runtime activo set value 0b
function mosslorn:loot/scan/cancelar
data modify storage mosslorn:force queue set value []
function mosslorn:force/catalogo
tellraw @s {"text":"Reinicio TOTAL solicitado: se vaciarán cofres y barriles y se restaurará su tabla. Preparando el índice; los chunks descargados se reiniciarán al acercarte. Usa /function mosslorn:force/estado para ver el progreso.","color":"yellow"}
execute if score #ftotal ml_tmp matches 0 run function mosslorn:force/listo''')
fn('index_step','''execute if score #fi ml_tmp >= #ftotal ml_tmp run return 0
execute store result storage mosslorn:force pointer.i int 1 run scoreboard players get #fi ml_tmp
function mosslorn:force/index_read with storage mosslorn:force pointer
scoreboard players add #fi ml_tmp 1''')
fn('index_read','''$data modify storage mosslorn:force entry set from storage mosslorn:registro cofres[$(i)]
execute store result score #fx ml_tmp run data get storage mosslorn:force entry.x
execute store result score #fz ml_tmp run data get storage mosslorn:force entry.z
execute if score #fx ml_tmp matches ..-1 run scoreboard players remove #fx ml_tmp 15
execute if score #fz ml_tmp matches ..-1 run scoreboard players remove #fz ml_tmp 15
scoreboard players operation #fx ml_tmp /= #16 ml_tmp
scoreboard players operation #fz ml_tmp /= #16 ml_tmp
execute store result storage mosslorn:force entry.cx int 1 run scoreboard players get #fx ml_tmp
execute store result storage mosslorn:force entry.cz int 1 run scoreboard players get #fz ml_tmp
function mosslorn:force/index_entry with storage mosslorn:force entry''')
fn('index_entry','''$execute unless data storage mosslorn:force chunks."$(dim)" run data modify storage mosslorn:force chunks."$(dim)" set value {}
$execute unless data storage mosslorn:force chunks."$(dim)"."$(cx)_$(cz)" run scoreboard players add #force_chunks ml_tmp 1
$execute unless data storage mosslorn:force chunks."$(dim)"."$(cx)_$(cz)" run data modify storage mosslorn:force chunks."$(dim)"."$(cx)_$(cz)" set value {entries:[],done:0}
$data remove storage mosslorn:force chunks."$(dim)"."$(cx)_$(cz)".entries[{x:$(x),y:$(y),z:$(z)}]
$data modify storage mosslorn:force chunks."$(dim)"."$(cx)_$(cz)".entries append from storage mosslorn:force entry''')
fn('listo','''scoreboard players set #force_build ml_tmp 0
tellraw @a {"text":"Índice listo. Reiniciando contenedores cercanos; los demás quedan pendientes hasta visitar sus chunks. No abras contenedores durante el reinicio de su zona.","color":"green"}''')
fn('cerca','''execute store result score #base_x ml_tmp run data get entity @s Pos[0]
execute store result score #base_z ml_tmp run data get entity @s Pos[2]
execute if score #base_x ml_tmp matches ..-1 run scoreboard players remove #base_x ml_tmp 15
execute if score #base_z ml_tmp matches ..-1 run scoreboard players remove #base_z ml_tmp 15
scoreboard players operation #base_x ml_tmp /= #16 ml_tmp
scoreboard players operation #base_z ml_tmp /= #16 ml_tmp
data modify storage mosslorn:force candidate set value {dim:"minecraft:overworld"}
data modify storage mosslorn:force candidate.dim set from entity @s Dimension
'''+'\n'.join('execute unless data storage mosslorn:force queue[0] run function mosslorn:force/candidato_'+str(i) for i in range(9)))
for i,(dx,dz) in enumerate([(0,0)]+[(x,z) for x in (-1,0,1) for z in (-1,0,1) if (x,z)!=(0,0)]):
 fn('candidato_'+str(i),f'''scoreboard players operation #fx ml_tmp = #base_x ml_tmp
scoreboard players operation #fz ml_tmp = #base_z ml_tmp
scoreboard players {'remove' if dx<0 else 'add'} #fx ml_tmp {abs(dx)}
scoreboard players {'remove' if dz<0 else 'add'} #fz ml_tmp {abs(dz)}
execute store result storage mosslorn:force candidate.cx int 1 run scoreboard players get #fx ml_tmp
execute store result storage mosslorn:force candidate.cz int 1 run scoreboard players get #fz ml_tmp
scoreboard players operation #fx ml_tmp *= #16 ml_tmp
scoreboard players operation #fz ml_tmp *= #16 ml_tmp
execute store result storage mosslorn:force candidate.x int 1 run scoreboard players get #fx ml_tmp
execute store result storage mosslorn:force candidate.z int 1 run scoreboard players get #fz ml_tmp
function mosslorn:force/elegir with storage mosslorn:force candidate''')
fn('elegir','''$execute unless data storage mosslorn:force chunks."$(dim)"."$(cx)_$(cz)"{done:0} run return 0
$execute in $(dim) positioned $(x) 0 $(z) unless loaded ~ ~ ~ run return 0
$data modify storage mosslorn:force queue set from storage mosslorn:force chunks."$(dim)"."$(cx)_$(cz)".entries
data modify storage mosslorn:force selected set from storage mosslorn:force candidate''')
fn('reset_step','''execute unless data storage mosslorn:force queue[0] run return 0
data modify storage mosslorn:force work set from storage mosslorn:force queue[0]
function mosslorn:force/visitar with storage mosslorn:force work''')
fn('visitar','''$execute in $(dim) positioned $(x) $(y) $(z) unless loaded ~ ~ ~ run function mosslorn:force/suspender with storage mosslorn:force selected
$execute in $(dim) positioned $(x) $(y) $(z) unless loaded ~ ~ ~ run return 0
$execute in $(dim) positioned $(x) $(y) $(z) if block ~ ~ ~ #mosslorn:reset_containers run function mosslorn:force/preparar
$execute in $(dim) positioned $(x) $(y) $(z) unless block ~ ~ ~ #mosslorn:reset_containers run scoreboard players add #force_missing ml_tmp 1
data remove storage mosslorn:force queue[0]
execute unless data storage mosslorn:force queue[0] run function mosslorn:force/terminar_chunk with storage mosslorn:force selected''')
fn('suspender','''$data modify storage mosslorn:force chunks."$(dim)"."$(cx)_$(cz)".entries set from storage mosslorn:force queue
data modify storage mosslorn:force queue set value []''')
fn('preparar','''execute if data block ~ ~ ~ LootTable run data modify storage mosslorn:force work.tabla set from block ~ ~ ~ LootTable
function mosslorn:force/aplicar with storage mosslorn:force work''')
fn('aplicar','''data remove block ~ ~ ~ Items
data remove block ~ ~ ~ LootTableSeed
$data modify block ~ ~ ~ LootTable set value "$(tabla)"
scoreboard players add #force_reset ml_tmp 1''')
fn('terminar_chunk','''$data modify storage mosslorn:force chunks."$(dim)"."$(cx)_$(cz)".done set value 1
scoreboard players add #force_done ml_tmp 1''')
fn('estado','''tellraw @s [{"text":"Reinicio TOTAL | índice: ","color":"yellow"},{"score":{"name":"#fi","objective":"ml_tmp"}},{"text":" / "},{"score":{"name":"#ftotal","objective":"ml_tmp"}},{"text":" | contenedores reiniciados: "},{"score":{"name":"#force_reset","objective":"ml_tmp"}},{"text":" | chunks completados: "},{"score":{"name":"#force_done","objective":"ml_tmp"}},{"text":" / "},{"score":{"name":"#force_chunks","objective":"ml_tmp"}},{"text":" | bloques ausentes: "},{"score":{"name":"#force_missing","objective":"ml_tmp"}}]''')
fn('tick','''execute unless data storage mosslorn:force chunks run return 0
scoreboard players add #force_clock ml_tmp 1
'''+('\n'.join('execute if score #force_build ml_tmp matches 1 run function mosslorn:force/index_step' for _ in range(64)))+'''
execute if score #force_build ml_tmp matches 1 if score #fi ml_tmp >= #ftotal ml_tmp run function mosslorn:force/listo
execute if score #force_build ml_tmp matches 1 if score #force_clock ml_tmp matches 100.. run tellraw @a [{"text":"Preparando reinicio: ","color":"yellow"},{"score":{"name":"#fi","objective":"ml_tmp"}},{"text":" / "},{"score":{"name":"#ftotal","objective":"ml_tmp"}}]
execute if score #force_clock ml_tmp matches 100.. run scoreboard players set #force_clock ml_tmp 0
execute if score #force_build ml_tmp matches 1 run return 0
execute unless data storage mosslorn:force queue[0] as @a at @s run function mosslorn:force/cerca
'''+('\n'.join('function mosslorn:force/reset_step' for _ in range(8))))
base=r.parent/'mosslorn-lazy-loot-patch/mosslorn-expanded.8.1-loot-al-abrir-fix.zip';out=r/'mosslorn-expanded.9-reinicio-total.zip'
with zipfile.ZipFile(base) as z,zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as w:
 for name in z.namelist():
  b=z.read(name)
  if name=='data/mosslorn/function/loot/reiniciar.mcfunction':w.writestr('data/mosslorn/function/loot/reposicion_vacios.mcfunction',b);b=b'function mosslorn:force/reiniciar\n'
  if name=='data/mosslorn/function/tick.mcfunction':b=b.replace(b'function mosslorn:loot/reiniciar',b'function mosslorn:loot/reposicion_vacios')+b'function mosslorn:force/tick\n'
  if name=='data/mosslorn/function/load.mcfunction':b=b.replace(b'Mosslorn v8:',b'Mosslorn v9:')+b'execute unless score #force_build ml_tmp matches 0..1 run scoreboard players set #force_build ml_tmp 0\n'
  w.writestr(name,b)
 for name,text in f.items():w.writestr(name,text)
 w.writestr('LEEME-v9.md',(r/'README.md').read_text())
 w.writestr('data/mosslorn/tags/block/reset_containers.json',json.dumps({'values':['minecraft:chest','minecraft:trapped_chest','minecraft:barrel']}))
print('Built',out,'catalogue',len(catalog),'chunks',len(groups),'functions',len(f))
