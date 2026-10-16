from pathlib import Path
import subprocess,hashlib,json,os,zipfile
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a):subprocess.run(list(map(str,a)),check=True)
for n,h in json.loads((root/'spas-reload18/inputs.json').read_text())['tools'].items():assert sha(t/(n+'.jar'))==h
pins=json.loads(Path('inputs.json').read_text());sources={k:root/v['file']for k,v in pins.items()}
for k,p in sources.items():assert sha(p)==pins[k]['sha256']
cp=os.pathsep.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn']
for p in ['build/tool','output']:Path(p).mkdir(parents=True,exist_ok=True)
run(*ecj,'-cp',cp,'-d','build/tool','Patch.java');names={'turret':'tacz_turrets-neoforge-3.0.0-1.21.1-doomsday-scrap-sounds.2.jar','remains':'zombie-remains-loot-1.6.1-neoforge-1.21.1-military-nvg-mythic.13-electronic-scrap.jar','hordes':'zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.44-electronic-scrap-barrels.jar'};report={}
for kind,src in sources.items():
 classes=Path('build')/kind;classes.mkdir(exist_ok=True);pkg='turretpower'if kind=='turret'else kind;run(*ecj,'-cp',src,'-d',classes,*Path('src/dev/zomboid',pkg).glob('*.java'));out=Path('output')/names[kind];run('java','-cp',cp+':build/tool','Patch',src,out,classes,kind)
 with zipfile.ZipFile(out)as z:entries={n:z.read(n)for n in z.namelist()}
 if kind=='turret':
  recipe={'type':'minecraft:crafting_shapeless','category':'misc','ingredients':[{'item':'minecraft:'+i}for i in ['lapis_lazuli','redstone','copper_ingot','iron_ingot']],'result':{'id':'tacz_turrets:electronic_scrap','count':2}};entries['data/tacz_turrets/recipe/electronic_scrap.json']=json.dumps(recipe).encode()
  n='data/tacz_turrets/recipe/turret.json';recipe=json.loads(entries[n]);recipe['pattern']=['ISI',' B ','ESE'];recipe['key']['S']={'item':'tacz_turrets:electronic_scrap'};entries[n]=json.dumps(recipe).encode()
  entries['assets/tacz_turrets/models/item/electronic_scrap.json']=json.dumps({'parent':'minecraft:item/generated','textures':{'layer0':'tacz_turrets:item/electronic_scrap'}}).encode();entries['assets/tacz_turrets/textures/item/electronic_scrap.png']=Path('assets/electronic_scrap.png').read_bytes()
  for name,item in [('electronic_scrap','minecraft:redstone'),('turret','tacz_turrets:electronic_scrap')]:entries['data/tacz_turrets/advancement/recipes/'+name+'.json']=json.dumps({'parent':'minecraft:recipes/root','criteria':{'has_material':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':[item]}]}}},'rewards':{'recipes':['tacz_turrets:'+name]}}).encode()
  sounds=json.loads(entries['assets/tacz_turrets/sounds.json'])
  for name in ['servo_idle','servo_move','target_alert']:sounds[name]={'subtitle':'subtitles.tacz_turrets.'+name,'sounds':[{'name':'tacz_turrets:'+name}]};entries['assets/tacz_turrets/sounds/'+name+'.ogg']=Path('assets',name+'.ogg').read_bytes()
  entries['assets/tacz_turrets/sounds.json']=json.dumps(sounds).encode()
  for locale,words in {'es_es':['Chatarra electrónica','Torreta girando','Torreta: objetivo reconocido'],'en_us':['Electronic Scrap','Turret turning','Turret: target recognized'],'de_de':['Elektronikschrott','Geschützturm dreht sich','Geschützturm: Ziel erkannt']}.items():
   n='assets/tacz_turrets/lang/'+locale+'.json';v=json.loads(entries.get(n,b'{}'));v.update(dict(zip(['item.tacz_turrets.electronic_scrap','subtitles.tacz_turrets.servo_move','subtitles.tacz_turrets.target_alert'],words)));entries[n]=json.dumps(v,ensure_ascii=False).encode()
  with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
   for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
 with zipfile.ZipFile(src)as a,zipfile.ZipFile(out)as b:
  assert b.testzip()is None and set(a.namelist())<=set(b.namelist());assert a.read('META-INF/neoforge.mods.toml')==b.read('META-INF/neoforge.mods.toml');added=sorted(set(b.namelist())-set(a.namelist()));assert not any(n.startswith('net/minecraft/')for n in added)
  for n in b.namelist():
   if n.endswith('.json')and not n.endswith('/'):json.loads(b.read(n))
  report[kind]={'input_sha256':sha(src),'output':out.name,'sha256':sha(out),'changed':sorted(n for n in a.namelist()if a.read(n)!=b.read(n)),'added':added,'minecraft_runtime_tested':False}
Path('validation.json').write_text(json.dumps(report,indent=2)+'\n')
