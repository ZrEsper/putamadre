from pathlib import Path
import subprocess,json,hashlib,zipfile
r=Path(__file__).resolve().parent;root=r.parent;tools=root/'hordes-melee-interiors-patch/build/tools';asm=':'.join(str(tools/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn']
def run(*args):subprocess.run(list(map(str,args)),cwd=r,check=True)
def sha(b):return hashlib.sha256(b).hexdigest()
for d in ['build/tool','build/flash','build/survivor','output']:(r/d).mkdir(parents=True,exist_ok=True)
run(*ecj,'-cp',asm,'-d','build/tool','Patch.java');reports={}
for kind,src,out in [('flash','src','flashlight-5.1-neoforge-1.21.1-batteries-auto-wake.5.jar'),('survivor','src-survivor','survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v34-flashlight-battery.jar')]:
 base=next((r/'base').glob('flashlight*'if kind=='flash'else'survivor*'));assert sha(base.read_bytes())==json.loads((r/'inputs.json').read_text())[kind]['sha256'];run(*ecj,'-d','build/'+kind,*map(str,(r/src).rglob('*.java')));p=r/'output'/out;run('java','-cp','build/tool:'+asm,'Patch',base,p,'build/'+kind,kind)
 with zipfile.ZipFile(p)as z:entries={n:z.read(n)for n in z.namelist()}
 def js(n,data):entries[n]=json.dumps(data,ensure_ascii=False,separators=(',',':')).encode()
 if kind=='flash':
  def recipe(name,pattern,key,result):js('data/flashlight/recipe/'+name+'.json',{'type':'minecraft:crafting_shaped','category':'equipment','pattern':pattern,'key':{k:{'item':v}for k,v in key.items()},'result':{'id':result,'count':1}})
  recipe('batterycraft',[' C ','RLR',' C '],{'C':'minecraft:copper_ingot','R':'minecraft:redstone','L':'minecraft:lapis_lazuli'},'flashlight:battery')
  js('data/flashlight/recipe/batterycraft.json',{'type':'minecraft:crafting_shapeless','category':'equipment','ingredients':[{'item':i}for i in ['minecraft:copper_ingot','minecraft:redstone','minecraft:lapis_lazuli']],'result':{'id':'flashlight:battery','count':1}})
  recipe('flashlight_craft',[' CG',' IR',' I '],{'C':'minecraft:copper_ingot','G':'minecraft:glass','I':'minecraft:iron_ingot','R':'minecraft:redstone'},'flashlight:flashlight_off')
  for name,pattern,key in [('expedition',['ICI','RFR','ICI'],{'I':'minecraft:iron_ingot','C':'minecraft:copper_ingot','R':'minecraft:redstone','F':'flashlight:flashlight_off'}),('eco',[' C ','GFG',' R '],{'C':'minecraft:copper_ingot','G':'minecraft:gold_ingot','F':'flashlight:flashlight_off','R':'minecraft:redstone'}),('tactical',[' D ','IFI',' L '],{'D':'minecraft:diamond','I':'minecraft:iron_ingot','F':'flashlight:flashlight_off','L':'minecraft:glass'})]:recipe(name,pattern,key,'flashlight:'+name+'_off')
  for name in ['batterycraft','flashlight_craft','expedition','eco','tactical']:
   item='minecraft:copper_ingot'if name in ['batterycraft','flashlight_craft']else'flashlight:flashlight_off'
   js('data/flashlight/advancement/recipes/'+name+'.json',{'parent':'minecraft:recipes/root','criteria':{'has_material':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':item}]}}},'rewards':{'recipes':['flashlight:'+name]}})
  for name in ['expedition','eco','tactical']:
   for suffix in ['','_off']:
    original='flashlight'+suffix;js('assets/flashlight/models/item/'+name+suffix+'.json',{'parent':'flashlight:item/'+original})
  langs={
  'es_es':(['Linterna de expedición','Linterna económica','Linterna táctica'],['Batería cambiada.','Necesitas una batería. Agáchate y haz clic derecho para cambiarla.','La batería ya está llena.','Queda un minuto de batería.','Batería agotada.'],['Carga: %s min %s s / %s min','Clic derecho: encender/apagar. Agachado + clic derecho: cambiar batería.']),
  'en_us':(['Expedition flashlight','Economy flashlight','Tactical flashlight'],['Battery replaced.','You need a battery. Sneak + right-click to replace it.','Battery is already full.','One minute of battery remaining.','Battery depleted.'],['Charge: %s min %s s / %s min','Right-click: toggle. Sneak + right-click: replace battery.']),
  'de_de':(['Expeditions-Taschenlampe','Spar-Taschenlampe','Taktische Taschenlampe'],['Batterie gewechselt.','Du brauchst eine Batterie. Schleichen + Rechtsklick zum Wechseln.','Batterie ist bereits voll.','Noch eine Minute Batterieladung.','Batterie leer.'],['Ladung: %s Min. %s Sek. / %s Min.','Rechtsklick: ein/aus. Schleichen + Rechtsklick: Batterie wechseln.'])}
  for loc,(names,msg,tip)in langs.items():
   path='assets/flashlight/lang/'+loc+'.json';data=json.loads(entries.get(path,b'{}'))
   for name,label in zip(['expedition','eco','tactical'],names):data['item.flashlight.'+name]=label;data['item.flashlight.'+name+'_off']=label
   for key,value in zip(['reloaded','no_battery','full','low','empty'],msg):data['message.flashlight.'+key]=value
   for key,value in zip(['charge','controls'],tip):data['tooltip.flashlight.'+key]=value
   js(path,data)
   if loc=='es_es':js('assets/flashlight/lang/es_mx.json',data)
 with zipfile.ZipFile(p,'w',zipfile.ZIP_DEFLATED)as z:
  for n,b in sorted(entries.items()):info=zipfile.ZipInfo(n,(1980,1,1,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,b)
 reports[kind]={'input':base.name,'input_sha256':sha(base.read_bytes()),'output':out,'sha256':sha(p.read_bytes())}
(r/'validation.json').write_text(json.dumps(reports,indent=2)+'\n')
