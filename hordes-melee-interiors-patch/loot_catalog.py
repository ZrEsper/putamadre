import json,collections
BASE='data/zomboid_hordes/loot_table/profiles/'
CATEGORIES=['armas','medicinas','comida','herramientas','ropa','municion','accesorios','materiales']
FOOD={'milk_bottle','tomato','cabbage','tomato_sauce','energy_drink','cod_slice','salmon_slice','kelp_roll','grilled_salmon','chicken_cuts','minced_beef','bacon','ham','smoked_ham','mutton_chops','egg','cooked_bacon','cooked_chicken_cuts','cooked_cod_slice','cooked_salmon_slice','onion','raw_pasta','wheat_dough','condensed_milk'}
TOOLS={'lead','binoculars','mounted_light','oak_boat','spruce_boat','bamboo_raft','writable_book','map','spyglass','name_tag','red_candle','lantern','ladder','crowbar','torch','campfire','bowl'}
def category(entry,old):
 name=entry['name'];path=name.split(':')[-1];tag=' '.join(json.dumps(f.get('tag',''),ensure_ascii=False) for f in entry.get('functions',[]))
 if name=='lrtactical:consumable':return 'comida' if 'condensed_milk' in tag else 'medicinas'
 if path in {'ice_pack','smelling_salts'}:return 'medicinas'
 if any(t in path for t in ['sandwich','stew','soup','cookie','pie','hamburger','hot_cocoa','apple_cider','bacon_and_eggs','roast_chicken']) or path in FOOD:return 'comida'
 if any(t in path for t in ['_shoes','_boots','_helmet','_chestplate','_leggings','_uniform']) or name=='lrtactical:flash_shield':return 'ropa'
 if path in {'throwable','flashbang','bola_iron','detonator','skillet','rotten_tomato'} or 'cleaver' in path:return 'armas'
 if path in TOOLS:return 'herramientas'
 return old
ADDITIONS=[('herramientas',{'type':'minecraft:item','name':'minecraft:white_bed','weight':1}),('herramientas',{'type':'minecraft:item','name':'minecraft:stone_pickaxe','weight':1})]
def repair(resources):
 tables={c:json.loads(resources[BASE+c+'.json']) for c in CATEGORIES};original=[];out={c:[] for c in CATEGORIES};moved=[]
 for cat,t in tables.items():
  assert len(t['pools'])==1
  for entry in t['pools'][0]['entries']:
   original.append(json.dumps(entry,sort_keys=True));dest=category(entry,cat);out[dest].append(entry)
   if dest!=cat:moved.append({'item':entry['name'],'from':cat,'to':dest})
 for cat,entry in ADDITIONS:
  if not any(e['name']==entry['name'] for e in out[cat]):out[cat].append(entry)
 preserved=collections.Counter(json.dumps(e,sort_keys=True) for c in CATEGORIES for e in out[c])
 assert all(preserved[k]>=v for k,v in collections.Counter(original).items()),'Lost original item data'
 for cat in CATEGORIES:
  tables[cat]['pools'][0]['entries']=out[cat];resources[BASE+cat+'.json']=(json.dumps(tables[cat],ensure_ascii=False,indent=2)+'\n').encode()
 return {'moved':moved,'additions':ADDITIONS,'entries_per_category':{c:len(out[c]) for c in CATEGORIES},'catalog':{c:out[c] for c in CATEGORIES}}
