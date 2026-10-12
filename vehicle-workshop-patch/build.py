from pathlib import Path
import zipfile,json,re,io,hashlib,sys
from PIL import Image,ImageDraw
r=Path(__file__).resolve().parent;repo=r.parent;official=repo/'mods/MTS-Official-Pack-1.21.1-V29-zomboid-resistant-maintenance.3.jar';belroft=Path(sys.argv[1]);changed=[]
def parse(raw):
 s=raw.decode('utf-8-sig');s=re.sub(r'"(?:\\.|[^"\\])*"|//[^\n]*|/\*[\s\S]*?\*/',lambda m:m[0] if m[0].startswith('"') else '',s);s=re.sub(r'"(?:\\.|[^"\\])*"|,(?=\s*[}\]])',lambda m:m[0] if m[0].startswith('"') else '',s);s=re.sub(r'"(?:\\.|[^"\\])*"|(?<![\w.])0+(\d+)(?=[.,\]\s])',lambda m:m[0] if m[0].startswith('"') else str(int(m[0])),s);s=re.sub(r'"(?:\\.|[^"\\])*"|(?<![\w.])\.(?=\d)',lambda m:m[0] if m[0].startswith('"') else '0.',s);return json.loads(s)
def save(data,n,d):data[n]=(json.dumps(d,ensure_ascii=False,indent=2)+'\n').encode();changed.append(n)
def write(out,data):
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
  for n,b in data.items():z.writestr(n,b)
 with zipfile.ZipFile(out) as z:assert z.testzip() is None
with zipfile.ZipFile(official) as z:data={n:z.read(n) for n in z.namelist()}
for car in ['scout','merc230','fordmustang69','gmcbrig','firetruck','quad']:
 n='assets/mtsofficialpack/jsondefs/vehicles/'+car+'.json';d=parse(data[n]);d['motorized']['climbSpeed']=.12;save(data,n,d)
for wheel in ['wheelsmall','wheelmedium','wheellarge','wheelhuge']:
 n='assets/mtsofficialpack/jsondefs/parts/'+wheel+'.json';d=parse(data[n]);d['ground']['climbHeight']=2.5;save(data,n,d)
# Static panel and the car's silhouette; coloured component masks live outside the visible panel.
im=Image.new('RGBA',(1024,512));dr=ImageDraw.Draw(im);dr.rounded_rectangle((0,0,619,369),radius=8,fill=(16,19,19,242),outline=(108,118,114,255),width=2);dr.line((18,48,600,48),fill=(94,107,100,255));dr.line((197,55,197,318),fill=(69,79,74,255));dr.line((418,55,418,318),fill=(50,62,55,255))
outline=[(55,68),(118,68),(137,81),(151,117),(152,260),(143,283),(127,306),(45,306),(28,281),(21,254),(22,118),(37,82)]
dr.polygon(outline,fill=(8,12,13,255));dr.line(outline+[outline[0]],fill=(210,219,211,255),width=2);dr.polygon([(44,78),(129,78),(140,127),(31,127)],fill=(34,40,38,255));dr.line((41,165,132,165),fill=(145,160,148,255),width=2);dr.line((46,258,127,258),fill=(145,160,148,255),width=2);dr.line((54,288,119,288),fill=(145,160,148,255),width=2)
for box in [(10,133,23,169),(149,133,162,169),(10,255,23,291),(149,255,162,291)]:dr.rounded_rectangle(box,radius=3,fill=(8,8,9,255),outline=(179,191,183,255),width=2)
for line in [(145,80,175,80),(22,144,2,144),(152,144,184,144),(23,278,3,278),(149,278,185,278)]:dr.line(line,fill=(145,160,148,255),width=1)
colors=[(80,215,40,205),(225,181,46,205),(218,62,42,205),(108,117,113,160)]
shapes=[[(10,8),(54,8),(62,58),(2,58)],[(0,3),(64,3),(55,62),(8,62)],[(5,1),(59,1),(59,63),(5,63)],[(2,0),(62,0),(60,64),(3,64)],[(2,0),(62,0),(60,64),(3,64)],[(0,2),(64,2),(54,61),(10,61)],[(4,4),(60,4),(57,58),(7,58)]]
for region in range(12):
 for band,color in enumerate(colors):
  tile=Image.new('RGBA',(64,64));td=ImageDraw.Draw(tile)
  if region<7:
   td.polygon(shapes[region],fill=(0,0,0,0) if region==2 else color);td.line(shapes[region]+[shapes[region][0]],fill=color if region==2 else (225,236,222,255),width=4 if region==2 else 2)
  elif region<11:
   td.rounded_rectangle((8,2,56,62),radius=9,fill=color,outline=(225,236,222,255),width=2)
   for dy in range(10,59,9):td.line((13,dy,50,dy),fill=(28,55,33,200),width=2)
  else:
   td.rounded_rectangle((4,15,58,57),radius=4,fill=color,outline=(225,236,222,255),width=2);td.rectangle((14,3,26,20),fill=color,outline=(225,236,222,255),width=2);td.rectangle((37,4,48,20),fill=color,outline=(225,236,222,255),width=2)
  im.alpha_composite(tile,(640+(region%6)*64,(region//6)*256+band*64))
buf=io.BytesIO();im.save(buf,format='PNG')
for suffix in ['','_lit']:n='assets/mtsofficialpack/textures/guis/zomboid_check'+suffix+'.png';data[n]=buf.getvalue();changed.append(n)
for suffix in ['','_lit']:
 n='assets/mtsofficialpack/textures/guis/zomboid_controls'+suffix+'.png';control=Image.new('RGBA',(512,256));cd=ImageDraw.Draw(control);cd.rounded_rectangle((0,0,479,204),radius=8,fill=(17,22,20,230),outline=(103,122,110,255),width=2);b=io.BytesIO();control.save(b,format='PNG');data[n]=b.getvalue();changed.append(n)
out=repo/'mods/MTS-Official-Pack-1.21.1-V29-zomboid-workshop.4.jar';write(out,data);official_hash=hashlib.sha256(out.read_bytes()).hexdigest();im.crop((0,0,620,370)).save(r/'workshop-layout.png')
# Illustrative preview of the new layout, not a screenshot from Minecraft.
preview=im.crop((0,0,620,370));pd=ImageDraw.Draw(preview)
from PIL import ImageFont
font=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSansMono.ttf',10);title=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSansMono.ttf',12)
rects=[(52,77,70,54),(47,132,78,35),(55,171,64,83),(29,169,22,90),(123,169,22,90),(48,260,76,24),(53,285,66,19),(23,135,20,32),(133,135,20,32),(23,259,20,32),(133,259,20,32),(164,78,28,34)]
for i,(x,y,w,h) in enumerate(rects):
 sx=640+i%6*64;sy=i//6*256;preview.alpha_composite(im.crop((sx,sy,sx+64,sy+64)).resize((w,h)),(x,y))
pd.text((18,16),'MECANICA DEL VEHICULO',font=title,fill='white');pd.text((210,16),'Belroft Adventure',font=title,fill='white');pd.text((210,35),'Estado general: 95%',font=font,fill='white');pd.text((210,54),'CARROCERIA',font=font,fill='white');pd.text((430,54),'PIEZAS / SUMINISTROS',font=font,fill='white')
for col,rows in enumerate([['Chasis: 95%','Defensa delantera: 86%','Defensa trasera: 100%','Capo: 90%','Puerta izquierda: 100%','Puerta derecha: 100%','Parabrisas: 100%','Ventanas izquierdas: 100%','Ventanas derechas: 100%','Cristal trasero: 100%'],['Rueda tras. izq.: 100%','Rueda tras. der.: 100%','Rueda del. izq.: 100%','Rueda del. der.: 100%','Motor 1: 94%','Asiento conductor: 100%','Asiento 2: 100%','Bateria (carga): 98%','Combustible: 76%']]):
 for i,row in enumerate(rows):pd.text((210+col*220,76+i*15),row,font=font,fill=(100,235,65))
pd.rounded_rectangle((210,335,455,360),radius=3,outline=(125,135,125));pd.text((223,342),'Cambiar caja: manual / auto',font=font,fill='white');pd.rounded_rectangle((475,335,600,360),radius=3,outline=(125,135,125));pd.text((480,342),'Abrir / cerrar capo',font=font,fill='white');pd.text((18,345),'Vista previa',font=font,fill=(150,155,150));preview.save(r/'workshop-preview.png')
with zipfile.ZipFile(official) as a,zipfile.ZipFile(out) as b:assert {n for n in a.namelist() if a.read(n)!=b.read(n)}==set(changed)
with zipfile.ZipFile(belroft) as z:bd={n:z.read(n) for n in z.namelist()}
changed=[];road=[]
for n in list(bd):
 if '/jsondefs/vehicles/' not in n or not n.endswith('.json'):continue
 d=parse(bd[n]);slots=d.get('parts',[])
 if not any('engine_car' in part.get('types',[]) for part in slots):continue
 road.append(Path(n).stem);d['general']['health']=max(1,d['general'].get('health',300))*12;d['motorized']['climbSpeed']=.12
 for group in d.get('collisionGroups',[]):
  if group.get('health',0)>0:group['health']*=4
 rear=min(s['pos'][2] for s in slots if 'pos' in s)-.3;slots.append({'pos':[0,.6,rear],'types':['interactable_crate'],'customTypes':['zomboid_trunk'],'defaultPart':'mtsofficialpack:zomboid_trunk','isPermanent':True})
 for obj in d.get('rendering',{}).get('particles',[])[:]:
  if any(a.get('variable')=='damage_totaled' for a in obj.get('activeAnimations',[])) and any('/fire' in t or '/bang' in t for t in obj.get('textureList',[])):d['rendering']['particles'].remove(obj)
 save(bd,n,d)
for n in list(bd):
 if '/jsondefs/parts/' not in n or not n.endswith('.json'):continue
 d=parse(bd[n]);kind=d.get('generic',{}).get('type','')
 if kind not in ['engine_car','ground_wheel']:continue
 if d.get('general',{}).get('health',0)>0:d['general']['health']*=8
 if kind=='ground_wheel':d.setdefault('ground',{})['climbHeight']=2.5
 save(bd,n,d)
bout=repo/'mods/Belroft-Motors-Mk13-zomboid-workshop.1.jar';write(bout,bd)
with zipfile.ZipFile(belroft) as a,zipfile.ZipFile(bout) as b:assert {n for n in a.namelist() if a.read(n)!=b.read(n)}==set(changed)
(r/'validation.json').write_text(json.dumps({'belroft_input_sha256':hashlib.sha256(belroft.read_bytes()).hexdigest(),'belroft_output_sha256':hashlib.sha256(bout.read_bytes()).hexdigest(),'official_output_sha256':official_hash,'road_vehicles':road,'belroft_changed_entries':changed,'terrain_climb_speed':.12,'maximum_step':2.5,'minecraft_runtime_tested':False},indent=2)+'\n')
print('Workshop assets verified; Belroft road vehicles:',len(road))
