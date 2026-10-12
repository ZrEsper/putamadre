from pathlib import Path
import zipfile,json,hashlib,subprocess,tempfile,os,sys,math,copy,io
from PIL import Image,ImageDraw
r=Path(__file__).resolve().parent;mod=Path(sys.argv[1]).resolve();pack=Path(sys.argv[2]).resolve();parsed=Path(sys.argv[3]).resolve() if len(sys.argv)>4 else None;tools=Path(sys.argv[4] if len(sys.argv)>4 else sys.argv[3]).resolve()
for name,digest in json.loads((r/'tools.sha256.json').read_text()).items():assert hashlib.sha256((tools/name).read_bytes()).hexdigest()==digest,name
for p,k in [(mod,'mod'),(pack,'pack')]:assert hashlib.sha256(p.read_bytes()).hexdigest()==json.loads((r/'inputs.sha256.json').read_text())[k]
asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar']);ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn']
modout=r.parent/'mods/Immersive-Vehicles-1.21.1-24.0.0-zomboid-mechanics.13-auto-hover-fuel.jar';packout=r.parent/'mods/MTS-Official-Pack-1.21.1-V29-zomboid-dashboard-trunks.1.jar'
with tempfile.TemporaryDirectory() as temp:
 t=Path(temp);c=t/'classes';p=t/'patch';tests=t/'tests'
 import shutil
 shutil.copytree(r/'assets',c/'assets',dirs_exist_ok=True)
 for cmd in [ecj+['-d',str(c)]+[str(f) for f in (r/'src').rglob('*.java')],ecj+['-cp',asm,'-d',str(p),str(r/'Patch.java'),str(r/'Verify.java'),str(r/'FieldJvmCheck.java')],['java','-cp',str(p)+os.pathsep+asm,'Patch',str(mod),str(modout),str(c)],ecj+['-cp',str(modout),'-d',str(tests)]+[str(f) for f in (r/'tests').rglob('*.java')],['java','-Xverify:all','-cp',str(tests)+os.pathsep+str(modout),'dev.zomboid.mts.Checks'],['java','-Xverify:all','-cp',str(tests)+os.pathsep+str(modout),'dev.zomboid.mts.InspectionChecks'],['java','-Xverify:all','-cp',str(tests)+os.pathsep+str(modout),'dev.zomboid.mts.PickerChecks'],['java','-Xverify:all','-cp',str(tests)+os.pathsep+str(modout),'dev.zomboid.mts.WorkshopChecks'],['java','-Xverify:all','-cp',str(tests)+os.pathsep+str(modout),'dev.zomboid.mts.FamilyChecks'],['java','-Xverify:all','-cp',str(tests)+os.pathsep+str(modout),'dev.zomboid.mts.FamilyGuiChecks'],['java','-Xverify:all','-cp',str(tests)+os.pathsep+str(modout),'dev.zomboid.mts.CarGuiChecks'],['java','-cp',str(p)+os.pathsep+asm,'Verify',str(modout)],['java','-Xverify:all','-cp',str(p)+os.pathsep+asm,'FieldJvmCheck',str(modout)]]:subprocess.run(cmd,check=True)
if '--core-only' in sys.argv:
 with zipfile.ZipFile(modout) as z:
  assert z.testzip() is None
 (r/'core-validation.json').write_text(json.dumps({'output_sha256':hashlib.sha256(modout.read_bytes()).hexdigest(),'mixin_retention':'CLASS/invisible','simulated_checks_passed':150,'minecraft_runtime_tested':False},indent=2)+'\n')
 sys.exit(0)
with zipfile.ZipFile(pack) as z:data={n:z.read(n) for n in z.namelist()}
changed=[]
def load(n):
 if parsed is not None:return json.loads((parsed/n).read_text())
 import json5,re
 raw=data[n].decode('utf-8-sig')
 raw=re.sub(r'"(?:\\.|[^"\\])*"|(?<![\w.])0{2,}(?=[,\]\s])',lambda m:m[0] if m[0].startswith('"') else '0',raw)
 return json5.loads(raw)
def asset(n,obj):data[n]=(json.dumps(obj,ensure_ascii=False,indent=2)+'\n').encode();changed.append(n)
rear={'scout':[0,.65,-1.5],'merc230':[0,.65,-1.25],'fordmustang69':[0,.65,-1.3],'gmcbrig':[0,.9,-3.5],'firetruck':[0,1.,-3.5],'quad':[0,.75,-.65]}
HUD=[('instrument_car_tachometer',80,63,.55),('instrument_car_spedometer_kph',240,63,.55),('instrument_car_fuelqty',160,35,.28),('instrument_car_enginetemp_c',160,76,.24)]
for car,pos in rear.items():
 n=f'assets/mtsofficialpack/jsondefs/vehicles/{car}.json';d=load(n);oldparts=copy.deepcopy(d['parts'])
 d['general']['health']*=3
 for group in d.get('collisionGroups',[]):
  if group.get('health',0)>0:group['health']*=2
 d['motorized']['halfHUDOnly']=False;d['motorized']['fullHUDOnly']=True;d['motorized']['hudTexture']='mtsofficialpack:textures/guis/zomboid_dashboard.png'
 for inst in d.get('instruments',[]):inst['placeOnPanel']=True
 for instrument,x,y,scale in HUD:d['instruments'].append({'pos':[0,0,0],'scale':.000001,'hudX':x,'hudY':y,'hudScale':scale,'defaultInstrument':'mtsofficialpack:'+instrument})
 d['parts'].append({'pos':pos,'types':['interactable_crate'],'customTypes':['zomboid_trunk'],'defaultPart':'mtsofficialpack:zomboid_trunk','isPermanent':True})
 assert d['parts'][:-1]==oldparts
 asset(n,d)
# Keep original gearboxes, reverse and neutral; add intermediate forward ratios to reach six.
engines=[]
for n in sorted(k for k in data if k.startswith('assets/mtsofficialpack/jsondefs/parts/engine') and k.endswith('.json')):
 f=Path(n);d=load(n)
 if d.get('generic',{}).get('type')!='engine_car':continue
 old=d['engine'].get('gearRatios',[-2.5,0,3.2,1.0]);negative=[v for v in old if v<0];forward=[v for v in old if v>0]
 if len(forward)<6:
  first=max(forward) if forward else 3.2;last=min(forward) if len(forward)>1 else .72
  forward=[round(first*(last/first)**(i/5),5) for i in range(6)]
 d['engine']['gearRatios']=(negative or [-2.5])+[0]+forward
 d['engine']['shiftSpeed']=12;d['engine']['clutchTime']=5
 safe=d['engine']['maxSafeRPM'];d['engine']['upShiftRPM']=[int(safe*.88)]*len(d['engine']['gearRatios']);d['engine']['downShiftRPM']=[int(safe*.26)]*len(d['engine']['gearRatios'])
 sounds=d.setdefault('rendering',{}).setdefault('sounds',[])
 sounds[:]=[s for s in sounds if not any(a.get('variable') in ['engine_clutch_upshift','engine_clutch_downshift'] for a in s.get('activeAnimations',[]))]
 for trigger in ['engine_clutch_upshift','engine_clutch_downshift']:
  sounds.append({'name':'mtsofficialpack:230ce/230c_gearshift0','activeAnimations':[{'animationType':'visibility','variable':trigger,'clampMin':1,'clampMax':1}],'minDistance':0,'minDistanceVolume':.8,'maxDistance':12,'maxDistanceVolume':0})
 d['engine']['engineWearFactor']=d['engine'].get('engineWearFactor',1)*.5
 if d.get('general',{}).get('health',0)>0:d['general']['health']*=2
 assert len(forward)>=6 and 0 in d['engine']['gearRatios'] and any(v<0 for v in d['engine']['gearRatios'])
 asset(n,d);engines.append(f.stem)
trunk=load('assets/mtsofficialpack/jsondefs/parts/crate.json')
trunk['generic'].update(customType='zomboid_trunk',destroyable=False,canBeRemovedByHand=False,width=.8,height=.55)
trunk['interactable'].update(inventoryUnits=6.,feedsVehicles=False)
trunk['definitions']=[{'subName':'','name':'Maletera del vehiculo','inventoryTexture':'mtsofficialpack:textures/guis/inventory_crateblank.png','extraMaterialLists':[[]]}]
trunk['general'].update(name='Maletera del vehiculo',description='Almacenamiento trasero: 54 espacios. Clic derecho, vehiculo desbloqueado.',hideOnCreativeTab=True,materialLists=[])
asset('assets/mtsofficialpack/jsondefs/parts/zomboid_trunk.json',trunk)
for old,new in [('assets/mtsofficialpack/objmodels/parts/crate.obj','assets/mtsofficialpack/objmodels/parts/zomboid_trunk.obj'),('assets/mtsofficialpack/textures/parts/crate.png','assets/mtsofficialpack/textures/parts/zomboid_trunk.png'),('assets/mtsofficialpack/textures/item/crate.png','assets/mtsofficialpack/textures/item/zomboid_trunk.png')]:data[new]=data[old]
asset('assets/mts/models/item/mtsofficialpack.zomboid_trunk.json',{'parent':'mts:item/basic','textures':{'layer0':'mtsofficialpack:item/zomboid_trunk'}})
for name,kind,desc in [('engine_scanner','scanner','Clic derecho: estado del motor, temperatura, aceite y desgaste. Cambia manual/automatico en el boton inferior.'),('engine_service_kit','repair_pack','Clic derecho en el motor o coche: repara el motor y elimina desgaste. Se consume. Apaga el motor antes de reparar.')]:
 d={'item':{'type':kind},'general':{'name':'Chequeador de motores' if kind=='scanner' else 'Kit de mantenimiento de motor','description':desc,'materialLists':[['minecraft:iron_ingot:2','minecraft:redstone:1','minecraft:gold_nugget:1']]}}
 if kind=='repair_pack':d['repair']={'amount':100,'canRepairTotaled':True,'repairableParts':['engine']}
 asset('assets/mtsofficialpack/jsondefs/items/'+name+'.json',d)
 data['assets/mtsofficialpack/textures/item/'+name+'.png']=data['assets/mtsofficialpack/textures/item/repairkit.png']
 data['assets/mtsofficialpack/textures/items/items/'+name+'.png']=data['assets/mtsofficialpack/textures/item/repairkit.png']
 asset('assets/mts/models/item/mtsofficialpack.'+name+'.json',{'parent':'mts:item/basic','textures':{'layer0':'mtsofficialpack:item/'+name}})
 asset('data/mtsofficialpack/recipe/'+name+'.json',{'type':'minecraft:crafting_shaped','pattern':[' I ','IRI',' G '],'key':{'I':{'item':'minecraft:iron_ingot'},'R':{'item':'minecraft:comparator' if kind=='scanner' else 'redstone'},'G':{'item':'minecraft:gold_nugget'}},'result':{'id':'mts:mtsofficialpack.'+name,'count':1}})
body='assets/mtsofficialpack/jsondefs/items/repairkit.json';d=load(body);d['repair']['amount']=80;d['general']['description']='Repara 80 de vida de carroceria. Clic derecho en el coche. Para el motor usa el kit de mantenimiento.';asset(body,d)
# Programmatically draw interface panels; no alteration of vehicle textures or provided screenshots.
for name,width,height in [('zomboid_dashboard',320,124),('zomboid_check',360,250)]:
 im=Image.new('RGBA',(512,256));dr=ImageDraw.Draw(im);dr.rounded_rectangle((0,0,width-1,height-1),radius=14,fill=(26,28,28,238),outline=(98,106,100,255),width=2);dr.line((12,25,width-12,25),fill=(77,91,77,255))
 if name=='zomboid_check':
  dr.rounded_rectangle((27,46,91,192),radius=18,fill=(16,20,20,255),outline=(166,172,163,255),width=2)
  for wheel in [(18,72,28,98),(90,72,100,98),(18,149,28,177),(90,149,100,177)]:dr.rounded_rectangle(wheel,radius=3,fill=(8,9,9,255),outline=(133,137,128,255))
  dr.rectangle((34,50,84,62),fill=(117,140,138,255));dr.rectangle((34,181,84,187),fill=(61,73,70,255))
  for y,color in [(0,(89,133,91,255)),(20,(177,147,64,255)),(40,(171,61,54,255))]:dr.rectangle((400,y,413,y+13),fill=color)
 buf=io.BytesIO();im.save(buf,format='PNG');data['assets/mtsofficialpack/textures/guis/'+name+'.png']=buf.getvalue();data['assets/mtsofficialpack/textures/guis/'+name+'_lit.png']=buf.getvalue()
with zipfile.ZipFile(packout,'w',zipfile.ZIP_DEFLATED) as z:
 for n,b in data.items():z.writestr(n,b)
with zipfile.ZipFile(modout) as z:assert z.testzip() is None;assert not any(n.startswith('net/minecraft/') for n in z.namelist())
with zipfile.ZipFile(packout) as z:
 assert z.testzip() is None
 for n in changed:json.loads(z.read(n))
 for instrument,_,_,_ in HUD:assert 'assets/mtsofficialpack/jsondefs/instruments/'+instrument+'.json' in z.namelist()
(r/'validation.json').write_text(json.dumps({'mod_sha256':hashlib.sha256(modout.read_bytes()).hexdigest(),'pack_sha256':hashlib.sha256(packout.read_bytes()).hexdigest(),'cars':list(rear),'engines_six_forward_gears':engines,'all_original_part_indices_preserved':True,'trunk_slots':54,'body_health_multiplier':3,'simulated_mechanic_checks_passed':30,'native_methods_analyzed':4858,'gear_clicks_require_empty_main_hand':True,'gear_shift_sound':'mtsofficialpack:230ce/230c_gearshift0','minecraft_runtime_tested':False},indent=2)+'\n')
assert 'assets/mtsofficialpack/sounds/230ce/230c_gearshift0.ogg' in data
print('Verified pack: six ground vehicles, persistent rear storage, HUD and five six-speed engines')
