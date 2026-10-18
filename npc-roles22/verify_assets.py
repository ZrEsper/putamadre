from pathlib import Path
import zipfile,json
r=Path(__file__).resolve().parent;info=json.loads((r/'validation.json').read_text());checks=0
with zipfile.ZipFile(r/'base/npc21.jar')as base,zipfile.ZipFile(r/'output'/info['file'])as out:
 assert out.testzip()is None;checks+=1
 for name in base.namelist():
  if name.startswith(('dev/zomboid/npcprobe/','assets/tacznpcs/entity/','assets/tacznpcs/sounds/')):assert base.read(name)==out.read(name),name;checks+=1
 before_sounds=json.loads(base.read('assets/tacznpcs/sounds.json'));after_sounds=json.loads(out.read('assets/tacznpcs/sounds.json'));assert all(after_sounds[k]==v for k,v in before_sounds.items());checks+=1
 for loc in ['es_es','es_mx','en_us']:
  before=json.loads(base.read('assets/tacznpcs/lang/'+loc+'.json'));after=json.loads(out.read('assets/tacznpcs/lang/'+loc+'.json'))
  for key,value in before.items():
   if key!='item.tacznpcs.bandit_spawn_egg':assert after[key]==value,key
  for egg in info['eggs']:assert after['item.tacznpcs.'+egg];checks+=1
 for egg in info['eggs']:assert json.loads(out.read('assets/tacznpcs/models/item/'+egg+'.json'))['parent']=='minecraft:item/template_spawn_egg';checks+=1
 for name in out.namelist():assert not name.startswith(('net/','com/tacz/','com/raiiiden/'))
 checks+=1
 for state in ['detect','search','patrol']:
  for sample in after_sounds['military_radio_'+state]['sounds']:
   assert out.read('assets/tacznpcs/sounds/'+sample['name'].split(':',1)[1]+'.ogg').startswith(b'OggS');checks+=1
(r/'asset-test-results.json').write_text(json.dumps(dict(checks=checks,native_skins_sounds_dialogue_preserved=True,no_game_api_stubs_shipped=True,minecraft_runtime_tested=False),indent=2)+'\n');print('NPC_ASSETS_TRANSLATIONS_EGG_MODELS_NO_API_STUBS_CHECKS='+str(checks))
