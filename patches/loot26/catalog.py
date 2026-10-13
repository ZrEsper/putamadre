from pathlib import Path
import json,zipfile,collections,copy
r=Path('work/loot26');groups={k:{} for k in ['armas','medicinas','comida','herramientas','ropa','municion','accesorios','materiales']}
food={'apple','bread','carrot','potato','baked_potato','beetroot','beetroot_soup','mushroom_stew','rabbit_stew','suspicious_stew','melon_slice','sweet_berries','glow_berries','cookie','pumpkin_pie','honey_bottle','dried_kelp','golden_apple','enchanted_golden_apple','golden_carrot','beef','porkchop','chicken','rabbit','mutton','cod','salmon','tropical_fish','pufferfish','cooked_beef','cooked_porkchop','cooked_chicken','cooked_rabbit','cooked_mutton','cooked_cod','cooked_salmon','rotten_flesh'}
def category(e):
 id=e['name'];low=id.lower();ns,name=low.split(':',1)
 if name in ['potion','splash_potion','lingering_potion'] or ns=='firstaid' or any(s in name for s in ['bandage','medkit','first_aid','medical_kit','painkiller','antibiotic','splint','tourniquet','disinfect','suture','morphine']):return 'medicinas'
 if name in ['arrow','tipped_arrow','spectral_arrow'] or 'ammo' in name or 'cartridge' in name or 'magazine' in name:return 'municion'
 if 'attachment' in name or any(s in name for s in ['scope','suppressor','silencer','foregrip','optic','sight']):return 'accesorios'
 if ns in ['valorantpack','apocalypticarsenal'] or name in ['modern_kinetic_gun','melee','bow','crossbow','trident'] or any(name.endswith(s) for s in ['_sword','_bow','_crossbow','_knife','_katana','_machete','_fire_axe']):return 'armas'
 if ns=='minecraft' and name in food or ns in ['farmersdelight','fffood','fffoodspoilageandpreservation','homeostatic','toughasnails'] and any(s in name for s in ['water','juice','tea','coffee','rice','food','meat','soup','sandwich']):return 'comida'
 if any(s in name for s in ['pickaxe','shovel','hoe','wrench','hammer','screwdriver','saw','shears','fishing_rod','flint_and_steel','compass','clock','bucket']) or name.endswith('_axe'):return 'herramientas'
 if ns in ['selfexpression','lrarmor','luckyswardrobe'] or ns in ['fracturepoint','brimm'] and not any(s in name for s in ['plate','fabric','thread','cloth','steel','ingot','leather','fiber','paste']) or any(s in name for s in ['helmet','chestplate','leggings','boots','uniform','backpack','vest','pants','shirt','gloves','jacket','mask']):return 'ropa'
 return 'materiales'
with zipfile.ZipFile('work/loot-expand/dist/mosslorn-expanded.13-apocalyptic-brimm-gucci.zip') as z:
 for n in z.namelist():
  if '/loot_table/' not in n or '/danger/' in n:continue
  j=json.loads(z.read(n))
  def walk(entries,functions):
   for e in entries:
    if e.get('type')=='minecraft:item':
     e=copy.deepcopy(e);e['functions']=e.get('functions',[])+functions;e['weight']=1;e.pop('quality',None)
     if not e['functions']:e.pop('functions')
     # Dynamic profiles set category probabilities, so remove old random entry gates only.
     e['conditions']=[c for c in e.get('conditions',[]) if c.get('condition') not in ['minecraft:random_chance','minecraft:random_chance_with_enchanted_bonus']]
     if not e['conditions']:e.pop('conditions')
     groups[category(e)][json.dumps(e,sort_keys=True)]=e
    walk(e.get('children',[]),functions)
  for pool in j.get('pools',[]):walk(pool.get('entries',[]),pool.get('functions',[]))
for k,entries in groups.items():
 p=r/'resources/data/zomboid_hordes/loot_table/profiles'/(k+'.json');p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps({'type':'minecraft:chest','pools':[{'rolls':1,'entries':list(entries.values())}]},indent=2))
 print(k,len(entries))
(r/'catalog-summary.json').write_text(json.dumps({k:len(v) for k,v in groups.items()}))
