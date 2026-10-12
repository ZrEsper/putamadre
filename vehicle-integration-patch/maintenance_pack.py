from pathlib import Path
import zipfile,json,io,hashlib,sys
from PIL import Image,ImageDraw
r=Path(__file__).resolve().parent;base=r.parent/'mods/MTS-Official-Pack-1.21.1-V29-zomboid-invisible-trunk.2.jar';out=r.parent/'mods/MTS-Official-Pack-1.21.1-V29-zomboid-resistant-maintenance.3.jar';parsed=Path(sys.argv[1]);changed=[]
with zipfile.ZipFile(base) as z:data={n:z.read(n) for n in z.namelist()}
def save(name,d):data[name]=(json.dumps(d,indent=2)+'\n').encode();changed.append(name)
for car in ['scout','merc230','fordmustang69','gmcbrig','firetruck','quad']:
 n='assets/mtsofficialpack/jsondefs/vehicles/'+car+'.json';d=json.loads(data[n]);d['general']['health']*=4;d['motorized']['climbSpeed']=.22;d['motorized']['steeringForceFactor']=max(.25,d['motorized'].get('steeringForceFactor',0));d['motorized']['steeringForceIgnoresSpeed']=False
 for g in d.get('collisionGroups',[]):
  if g.get('health',0)>0:g['health']*=4
 d['rendering']['particles']=[part for part in d['rendering'].get('particles',[]) if not (any(a.get('variable')=='damage_totaled' for a in part.get('activeAnimations',[])) and any('/fire' in t or '/bang' in t for t in part.get('textureList',[])))]
 model='assets/mtsofficialpack/objmodels/vehicles/'+car+'.obj'
 windows=[line[2:].strip() for line in data.get(model,b'').decode(errors='ignore').splitlines() if line.startswith('o ') and ('window' in line.lower() or 'wlndow' in line.lower()) and 'door' in line.lower()]
 objects=d['rendering'].setdefault('animatedObjects',[])
 for window in windows:
  existing=next((o for o in objects if o['objectName']==window),None)
  if existing is None:existing={'objectName':window,'animations':[]};objects.append(existing)
  existing.setdefault('animations',[]).append({'animationType':'visibility','variable':'pz_windows_open','clampMin':0,'clampMax':0})
 save(n,d)
for engine in ['engineamci4','enginedetroitdiesel','enginefordfe428','enginemercedesm102','enginequad']:
 n='assets/mtsofficialpack/jsondefs/parts/'+engine+'.json';d=json.loads(data[n]);d['general']['health']*=4;save(n,d)
for wheel in ['wheelsmall','wheelmedium','wheellarge','wheelhuge']:
 n='assets/mtsofficialpack/jsondefs/parts/'+wheel+'.json';d=json.loads((parsed/n).read_text());d['ground']['climbHeight']=1.75;d['ground']['motiveFriction']=min(1,d['ground']['motiveFriction']+.12);d['ground']['lateralFriction']=min(1,d['ground']['lateralFriction']+.1)
 if d.get('general',{}).get('health',0)>0:d['general']['health']*=4
 save(n,d)
im=Image.new('RGBA',(512,512));draw=ImageDraw.Draw(im);draw.rounded_rectangle((0,0,499,309),radius=12,fill=(24,28,26,245),outline=(108,119,110,255),width=2);draw.line((14,30,484,30),fill=(100,125,98,255));draw.rounded_rectangle((27,46,105,245),radius=20,fill=(12,17,16,255),outline=(160,174,161,255),width=2)
for box in [(19,71,28,105),(104,71,113,105),(19,181,28,215),(104,181,113,215)]:draw.rounded_rectangle(box,radius=3,fill=(8,10,9,255),outline=(108,119,110,255))
draw.rectangle((35,52,97,65),fill=(92,125,120,255));draw.rectangle((35,220,97,233),fill=(92,125,120,255))
# Palette outside the visible 500-pixel panel, sampled by the condition indicators.
for y,color in [(0,(89,133,91,255)),(20,(177,147,64,255)),(40,(171,61,54,255))]:draw.rectangle((500,y,511,y+13),fill=color)
b=io.BytesIO();im.save(b,format='PNG')
for suffix in ['','_lit']:n='assets/mtsofficialpack/textures/guis/zomboid_check'+suffix+'.png';data[n]=b.getvalue();changed.append(n)
for asset,width,height in [('zomboid_dashboard',340,66),('zomboid_controls',480,170)]:
 panel=Image.new('RGBA',(512,256));dr=ImageDraw.Draw(panel);dr.rounded_rectangle((0,0,width-1,height-1),radius=8,fill=(17,22,20,220),outline=(103,122,110,255),width=2)
 if asset=='zomboid_dashboard':dr.line((10,24,width-10,24),fill=(60,85,65,255))
 stream=io.BytesIO();panel.save(stream,format='PNG')
 for suffix in ['','_lit']:n='assets/mtsofficialpack/textures/guis/'+asset+suffix+'.png';data[n]=stream.getvalue();changed.append(n)
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
 for n,v in data.items():z.writestr(n,v)
with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
 assert b.testzip() is None;assert set(n for n in a.namelist() if a.read(n)!=b.read(n))==set(changed).intersection(a.namelist())
 assert a.read('assets/mtsofficialpack/jsondefs/parts/zomboid_trunk.json')==b.read('assets/mtsofficialpack/jsondefs/parts/zomboid_trunk.json')
 for car in ['scout','merc230','fordmustang69','gmcbrig','firetruck','quad']:
  d=json.loads(b.read('assets/mtsofficialpack/jsondefs/vehicles/'+car+'.json'));assert d['general']['health']==json.loads(a.read('assets/mtsofficialpack/jsondefs/vehicles/'+car+'.json'))['general']['health']*4
(r/'maintenance-validation.json').write_text(json.dumps({'pack_input_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'pack_output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed_entries':changed,'health_multiplier_relative_to_pack_2':4,'steering_speed_dependent':True,'climb_speed':.22,'wheel_climb_height':1.75,'minecraft_runtime_tested':False},indent=2)+'\n')
print('Verified resistant pack: 6 road vehicles, 5 engines, 4 wheels; invisible trunk preserved')
