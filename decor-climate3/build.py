from pathlib import Path
import os,subprocess,hashlib,json,zipfile,re
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a,**kw):return subprocess.run(list(map(str,a)),check=True,**kw)
for n,h in {'ecj':'01f5a92ac19bb2b3bf85e295a68f2c73c264369109158b566ce9b490af982948','asm':'8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281','asm-tree':'9929881f59eb6b840e86d54570c77b59ce721d104e6dfd7a40978991c2d3b41f','asm-analysis':'85b29371884ba31bb76edf22323c2c24e172c3267a67152eba3d1ccc2e041ef2'}.items():assert sha(t/(n+'.jar'))==h
inputs=json.loads(Path('inputs.sha256.json').read_text())
for n,h in inputs.items():assert sha(root/n)==h
for d in ['build/tool','output']:Path(d).mkdir(parents=True,exist_ok=True)
compiler=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];cp=os.pathsep.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);run(*compiler,'-cp',cp,'-d','build/tool','Patch.java')
groups=[('decor','decor','doomsday_decoration-1.1.4.2-neoforge-1.21.1-zomboid-climate-storage.3.jar'),('homeo','homeoac','homeostatic-1.21.1-neoforge-2.12.0.0-doomsday-ac.1.jar'),('tan','tanac','ToughAsNails-neoforge-1.21.1-10.1.0.13-doomsday-ac.1.jar'),('hordes','hordes','zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.42-window-barricades.jar')]
report={}
for (kind,pkg,name),source in zip(groups,inputs):
 source=root/source;classes=Path('build')/pkg;classes.mkdir(exist_ok=True);run(*compiler,'-cp',source,'-d',classes,*Path('src/dev/zomboid',pkg).glob('*.java'));out=Path('output')/name;run('java','-cp',cp+os.pathsep+'build/tool','Patch',source,out,classes,kind)
 if kind=='decor':
  with zipfile.ZipFile(out)as z:entries={n:z.read(n)for n in z.namelist()}
  specs=json.loads(entries['doomsday_decoration/block_specs.json']);ids=set(re.findall(r'"([a-z0-9_]+)"',Path('src/dev/zomboid/decor/Storage.java').read_text().split('static final ThreadLocal')[0]));assert len(ids)==94
  for s in specs:
   if s['id']in ids:s['container']=True
   if s['id'].startswith('planking_'):s['destroy_time']=6.;s['explosion_resistance']=12.;s['requires_correct_tool']=False
  index={s['id']for s in specs};assert ids<=index
  entries['doomsday_decoration/block_specs.json']=json.dumps(specs,separators=(',',':')).encode()
  recipes={}
  for i in sorted(ids-set(re.findall(r'"([a-z0-9_]+)"',(root/'doomsday-functional-patch/src/dev/zomboid/decor/Storage.java').read_text().split('static final ThreadLocal')[0]))):
   if 'aircondition'in i:recipe={'type':'minecraft:crafting_shaped','category':'redstone','pattern':['IRI','CFC','III'],'key':{'I':{'item':'minecraft:iron_ingot'},'R':{'item':'minecraft:redstone'},'C':{'item':'minecraft:copper_ingot'},'F':{'item':'minecraft:packed_ice'}},'result':{'id':'doomsday_decoration:aircondition','count':1}} if i=='aircondition' else {'type':'minecraft:stonecutting','ingredient':{'item':'doomsday_decoration:aircondition'},'result':{'id':'doomsday_decoration:'+i,'count':1}}
   else:recipe={'type':'minecraft:crafting_shaped','category':'misc','pattern':['PPP',' B ','PPP'],'key':{'P':{'tag':'minecraft:planks'},'B':{'item':'minecraft:barrel'}},'result':{'id':'doomsday_decoration:desk','count':1}} if i=='desk' else {'type':'minecraft:stonecutting','ingredient':{'item':'doomsday_decoration:desk'},'result':{'id':'doomsday_decoration:'+i,'count':1}}
   recipes[i]=recipe
  for s in specs:
   i=s['id']
   if i.startswith('planking_'):recipes[i]={'type':'minecraft:crafting_shaped','category':'building','pattern':['PPP'],'key':{'P':{'tag':'minecraft:planks'}},'result':{'id':'doomsday_decoration:planking_0','count':6}} if i=='planking_0' else {'type':'minecraft:stonecutting','ingredient':{'item':'doomsday_decoration:planking_0'},'result':{'id':'doomsday_decoration:'+i,'count':1}}
  for i,recipe in recipes.items():
   entries['data/doomsday_decoration/recipe/'+i+'.json']=json.dumps(recipe).encode()
   entries['data/doomsday_decoration/advancement/recipes/'+i+'.json']=json.dumps({'parent':'minecraft:recipes/root','criteria':{'has_material':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':['minecraft:iron_ingot' if 'aircondition'in i else 'minecraft:oak_planks']}]}}},'rewards':{'recipes':['doomsday_decoration:'+i]}}).encode()
  sound=json.loads(entries.get('assets/doomsday_decoration/sounds.json',b'{}'));sound['air_fan']={'subtitle':'subtitles.doomsday_decoration.air_fan','sounds':[{'name':'doomsday_decoration:air_fan','stream':False}]};entries['assets/doomsday_decoration/sounds.json']=json.dumps(sound).encode()
  run('ffmpeg','-hide_banner','-loglevel','error','-y','-f','lavfi','-i','anoisesrc=color=pink:amplitude=0.18:duration=4:sample_rate=44100:seed=312','-af','highpass=f=80,lowpass=f=1100,afade=t=in:d=0.08,afade=t=out:st=3.92:d=0.08','-ac','1','-c:a','libvorbis','build/air_fan.ogg');entries['assets/doomsday_decoration/sounds/air_fan.ogg']=Path('build/air_fan.ogg').read_bytes()
  for locale,messages in {'es_es':['Ventilador del aire acondicionado','Aire encendido: radio 15 bloques · cultivos +35% · 400 FE/s','Aire apagado. Mayús + clic para encender','Sin energía: necesita un generador con combustible a 25 bloques o menos'], 'en_us':['Air conditioner fan','AC running: 15-block radius · crops +35% · 400 FE/s','AC off. Shift + click to turn on','No power: needs a fueled generator within 25 blocks'], 'de_de':['Klimaanlagenlüfter','Klimaanlage aktiv: 15 Blöcke · Pflanzen +35% · 400 FE/s','Klimaanlage aus. Umschalt + Klick zum Einschalten','Kein Strom: Generator mit Brennstoff in 25 Blöcken erforderlich']}.items():
   n='assets/doomsday_decoration/lang/'+locale+'.json';lang=json.loads(entries.get(n,b'{}'));lang.update(dict(zip(['subtitles.doomsday_decoration.air_fan','message.doomsday_decoration.ac_on','message.doomsday_decoration.ac_off','message.doomsday_decoration.ac_no_power'],messages)));entries[n]=json.dumps(lang,ensure_ascii=False).encode()
  # Preserve the compatible Storage .2 internal version used by Survival's explicit range.
  with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
   for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
 with zipfile.ZipFile(source)as a,zipfile.ZipFile(out)as b:
  assert b.testzip()is None and set(a.namelist())<=set(b.namelist());changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));added=sorted(set(b.namelist())-set(a.namelist()));assert a.read('META-INF/neoforge.mods.toml')==b.read('META-INF/neoforge.mods.toml')
  for n in b.namelist():
   if n.endswith('.json'):json.loads(b.read(n))
  assert all(not n.startswith('net/minecraft/') for n in added)
 report[kind]={'input_sha256':sha(source),'output':name,'output_sha256':sha(out),'changed':changed,'added':added,'minecraft_runtime_tested':False}
expected=Path('expected-changes.json')
if expected.exists():assert json.loads(expected.read_text())=={k:{x:v[x]for x in ['changed','added']}for k,v in report.items()}
Path('validation.json').write_text(json.dumps(report,indent=2)+'\n')
