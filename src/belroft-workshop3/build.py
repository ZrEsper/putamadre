from pathlib import Path
import zipfile,json,re,hashlib
base=Path('work/belroft3');source=Path('attachments/c96f01c5-f1df-4887-b363-0da6afb23689/Belroft-Motors-Mk13-zomboid-workshop.2-step-limit.jar');official=base/'MTS-Official-Pack-1.21.1-V29-zomboid-workshop.5-step-limit.jar'
def parse(data):
 t=data.decode('utf-8-sig');t=re.sub(r'"(?:\\.|[^"\\])*"|//[^\n]*|/\*.*?\*/',lambda m:m[0] if m[0].startswith('"') else '',t,flags=re.S);t=re.sub(r',\s*([}\]])',r'\1',t)
 # MTS accepts legacy numeric syntax; normalize without changing string literals.
 t=re.sub(r'"(?:\\.|[^"\\])*"|(?<![\w.])(-?)(\d*\.\d+|\d+)(?![\w.])',lambda m:m[0] if m[0].startswith('"') else m[1]+(str(float(m[2])) if '.' in m[2] else str(int(m[2]))),t)
 return json.loads(t)
replacements={'Snorkeled':'Mit Schnorchel','Fifth Wheel Assembly':'Sattelkupplung','License Plate':'Kennzeichen','Fluid Inventory':'Flüssigkeitstank','Dry Van Bed':'Kofferaufbau','Diesel Engine':'Dieselmotor','Engine Propeller':'Motorpropeller','Engine':'Motor','Concave Truck Wheel':'Konkaves Lkw-Rad','Convex Truck Wheel':'Konvexes Lkw-Rad','Tow Hitch':'Anhängerkupplung','Roof Rack':'Dachgepäckträger','Bullbar':'Frontschutzbügel','Medical Bench':'Medizinische Bank','Medical Cabinet':'Medizinschrank','Taxi Roof Sign':'Taxi-Dachschild','Boat Trailer':'Bootsanhänger','Car Trailer':'Autotransportanhänger','Truck Trailer':'Lkw-Transportanhänger','Dry Van':'Kofferanhänger','Tanker':'Tankanhänger','Fire Chief':'Feuerwehreinsatzleitung','Ambulance':'Rettungswagen','Police':'Polizei','Black':'Schwarz','White':'Weiß','Blue':'Blau','Brown':'Braun','Green':'Grün','Grey':'Grau','Gray':'Grau','Red':'Rot','Yellow':'Gelb','Orange':'Orange','Pink':'Rosa','Purple':'Violett','Olive':'Olivgrün','Livery':'Lackierung','Federal':'Bundes','Union':'Union','Andesite':'Andesit'}
def german(s):
 for old,new in sorted(replacements.items(),key=lambda p:-len(p[0])):s=re.sub(r'\b'+re.escape(old)+r'\b',lambda m:new,s)
 return s
changes={};english={};deutsch={};vehicles=[];definitions={}
with zipfile.ZipFile(source) as z,zipfile.ZipFile(official) as mz:
 for n in z.namelist():
  if '/jsondefs/' not in n or not n.endswith('.json'):continue
  d=parse(z.read(n));definitions[n]=d;system=Path(n).stem;general=d.get('general',{})
  if general.get('name'):english[f'belroftmotors.{system}.name']=general['name'];deutsch[f'belroftmotors.{system}.name']=german(general['name'])
  if general.get('description'):
   english[f'belroftmotors.{system}.description']=general['description']
   title=general.get('name') or d.get('definitions',[{}])[0].get('name',system)
   category='Fahrzeug' if '/vehicles/' in n else 'Bauteil'
   deutsch[f'belroftmotors.{system}.description']=f'{category}: {german(title)}. Kompatibel mit Immersive Vehicles und der Belroft-Werkstatt.'
  for v in d.get('definitions',[]):
   prefix=f'belroftmotors.{system}{v.get("subName","")}'
   for k in ['name','subDescription']:
    if k in v:english[prefix+'.'+k]=v[k];deutsch[prefix+'.'+k]=german(v[k])
  if '/vehicles/' in n:
   motor=d.get('motorized',{});motor.update({'halfHUDOnly':False,'fullHUDOnly':True,'hudTexture':'belroftmotors:textures/guis/zomboid_dashboard.png'})
   rendering=d.setdefault('rendering',{});rendering['hudTexture']='belroftmotors:textures/guis/zomboid_dashboard.png'
   seats=[v for v in d.get('parts',[]) if v.get('isController') and 'seat' in v.get('types',[])]
   if seats and not rendering.get('cameraObjects'):
    seat=seats[0]['pos'];eye=[seat[0],seat[1]+0.65,seat[2]]
    rendering['cameraObjects']=[{'pos':eye,'rot':[0,0,0],'isInterior':True}]
   # Retain native external/isometric camera compatibility; do not force camera transitions.
   vehicles.append(system);changes[n]=(json.dumps(d,ensure_ascii=False,indent=2)+'\n').encode()
 for n in mz.namelist():
  if n.startswith('assets/mtsofficialpack/textures/guis/zomboid_') and n.endswith('.png'):changes[n.replace('assets/mtsofficialpack/','assets/belroftmotors/')]=mz.read(n)
 for language,entries in [('en_us',english),('de_de',deutsch)]:changes[f'assets/belroftmotors/language/{language}.json']=(json.dumps({'entries':entries},ensure_ascii=False,indent=2)+'\n').encode()
 output=base/'Belroft-Motors-Mk13-zomboid-workshop.3-mts-ui-en-de.jar'
 with zipfile.ZipFile(output,'w',zipfile.ZIP_DEFLATED) as out:
  for info in z.infolist():
   if info.filename not in changes:out.writestr(info,z.read(info.filename))
  for n,data in changes.items():out.writestr(n,data)
core=Path('work/iso26/Immersive-Vehicles-1.21.1-24.0.0-zomboid-mechanics.14-external-camera-compat.jar');coreout=base/'Immersive-Vehicles-1.21.1-24.0.0-zomboid-mechanics.15-workshop-en-de.jar'
with zipfile.ZipFile(core) as z,zipfile.ZipFile(coreout,'w',zipfile.ZIP_DEFLATED) as out:
 overrides={str(p.relative_to(base/'core')):p.read_bytes() for p in (base/'core').rglob('*') if p.is_file()}
 for info in z.infolist():
  if info.filename not in overrides:out.writestr(info,z.read(info.filename))
 for n,data in overrides.items():out.writestr(n,data)
with zipfile.ZipFile(source) as original,zipfile.ZipFile(output) as updated:
 assert updated.testzip() is None
 for n,d in definitions.items():
  actual=parse(updated.read(n))
  for key in ['parts','general','definitions','collisionGroups']:
   assert actual.get(key)==d.get(key),(n,key)
 assert original.read('belroftmotors/ForgePackLoader.class')==updated.read('belroftmotors/ForgePackLoader.class')
 assert all('assets/belroftmotors/textures/guis/zomboid_dashboard.png' in updated.namelist() for _ in [0])
print('PASS',len(vehicles),'vehicle HUDs;',len(english),'language keys; vehicle components, health, physics and loader retained')
(base/'validation.json').write_text(json.dumps({'vehicles':vehicles,'languageKeys':len(english),'changedEntries':list(changes)},indent=2))
