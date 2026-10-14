from pathlib import Path
import zipfile,json,re,io,hashlib
from PIL import Image,ImageDraw
b=Path('work/mts-hud6');ns={};s=Path('work/belroft3/build.py').read_text();exec(s[s.index('def parse('):s.index('replacements=')],dict(json=json,re=re),ns);parse=ns['parse'];changes={};affected=[]
image=Image.new('RGBA',(1024,1024),(0,0,0,0));draw=ImageDraw.Draw(image);draw.rounded_rectangle((0,0,259,53),radius=4,fill=(10,17,14,220),outline=(100,123,108,220),width=1);buffer=io.BytesIO();image.save(buffer,format='PNG');changes['assets/mtsofficialpack/textures/guis/zomboid_telemetry_compact.png']=buffer.getvalue()
original=b/'MTS-Official-Pack-1.21.1-V29-zomboid-workshop.5-step-limit.jar';pack=b/'MTS-Official-Pack-1.21.1-V29-zomboid-workshop.6-compact-aircraft-hud.jar'
with zipfile.ZipFile(original) as z:
 for n in z.namelist():
  if '/jsondefs/vehicles/' not in n or not n.endswith('.json'):continue
  d=parse(z.read(n));types={t for p in d.get('parts',[]) for t in p.get('types',[])}
  if not(d.get('motorized',{}).get('isAircraft') or any(t.startswith(('engine_helicopter','engine_aircraft','engine_jet','ground_pontoon')) for t in types)):continue
  d.setdefault('motorized',{}).update({'halfHUDOnly':False,'fullHUDOnly':True,'hudTexture':'mtsofficialpack:textures/guis/zomboid_telemetry_compact.png'});d.setdefault('rendering',{})['hudTexture']='mtsofficialpack:textures/guis/zomboid_telemetry_compact.png';changes[n]=(json.dumps(d,ensure_ascii=False,indent=2)+'\n').encode();affected.append(n)
 with zipfile.ZipFile(pack,'w',zipfile.ZIP_DEFLATED) as out:
  for info in z.infolist():
   if info.filename not in changes:out.writestr(info,z.read(info.filename))
  for n,data in changes.items():out.writestr(n,data)
with zipfile.ZipFile(original) as a,zipfile.ZipFile(pack) as z:
 assert z.testzip() is None
 for n in affected:
  before=parse(a.read(n));after=parse(z.read(n))
  for k in ['parts','instruments','general','definitions','collisionGroups']:assert before.get(k)==after.get(k),(n,k)
corebase=Path('work/belroft3/Immersive-Vehicles-1.21.1-24.0.0-zomboid-mechanics.15-workshop-en-de.jar');core=b/'Immersive-Vehicles-1.21.1-24.0.0-zomboid-mechanics.16-compact-telemetry.jar'
with zipfile.ZipFile(corebase) as z:
 overrides={'minecrafttransportsimulator/guis/instances/GUIHUD.class':(b/'GUIHUD-fixed.class').read_bytes(),'dev/zomboid/mts/HudTelemetry.class':(b/'classes/dev/zomboid/mts/HudTelemetry.class').read_bytes(),'dev/zomboid/mts/workshop-translations.tsv':z.read('dev/zomboid/mts/workshop-translations.tsv')+b'Consumo\tConsumption\tVerbrauch\n'}
 with zipfile.ZipFile(core,'w',zipfile.ZIP_DEFLATED) as out:
  for info in z.infolist():
   if info.filename not in overrides:out.writestr(info,z.read(info.filename))
  for n,data in overrides.items():out.writestr(n,data)
print('PASS',len(affected),'aircraft definitions; original physics, controls and instruments retained; fresh input hash:',hashlib.sha256(original.read_bytes()).hexdigest()[:16])
(b/'LEEME.txt').write_text('''MTS · HUD COMPACTO DE AVIONES, HELICOPTEROS Y BOTES
Reemplaza MTS Official Pack workshop.5 por workshop.6 incluido.
Reemplaza Immersive Vehicles por mechanics.16 incluido. No mantengas dos versiones de cada mod.
El parche del HUD necesita ambos JAR: datos/texturas en Official Pack y codigo de interfaz en Immersive Vehicles.
Mantiene compatibilidad de camaras externas de mechanics.14 y traducciones de mechanics.15.

HUD de 260x54 (unidades de GUI), texto a escala 0.7 y tres lineas:
Velocidad km/h y RPM reales; hasta cuatro motores separados, con contador para adicionales.
Combustible restante en porcentaje y litros/capacidad.
Consumo total en L/min (suma de fuelFlow de los motores encendidos; misma conversion usada por MTS).
Desaparece el panel nativo grande en esas familias. Los coches mantienen su HUD anterior.
Funciona para botes detectados por MTS, incluido Belroft; el pack oficial enviado no contiene botes propios.
Los instrumentos fisicos de cabina, controles y fisica no se modifican.

Verificado: 8 hooks del ciclo de HUD, deteccion de familias, calculos con multiples motores, motor apagado y ausencia de datos; archivos JAR y definiciones revisados. Sin prueba visual en Minecraft.
''',encoding='utf-8')
with zipfile.ZipFile(b/'MTS-HUD-compacto-aviones-helicopteros-botes.1.zip','w',zipfile.ZIP_DEFLATED) as z:
 for p in [pack,core,b/'LEEME.txt']:z.write(p,p.name)
print('Bundle bytes:',(b/'MTS-HUD-compacto-aviones-helicopteros-botes.1.zip').stat().st_size)
