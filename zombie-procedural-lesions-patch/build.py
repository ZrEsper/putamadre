from pathlib import Path
import zipfile,json,copy,io,random,hashlib,sys
from PIL import Image
r=Path(__file__).resolve().parent;source=Path(sys.argv[1]);out=r/'TZP-1.21.1-Gore.4-original-skins-cartoon-lesions.zip'
with zipfile.ZipFile(source) as z:data={n:z.read(n) for n in z.namelist() if not n.endswith('/') and not n.lower().endswith('thumbs.db')}
base='assets/minecraft/optifine/cem/'
# Start from the user's original connected Fresh models, not the previous replacement skeleton.
originals={kind:json.loads(data[base+kind+'.jem']) for kind in ['zombie','husk','drowned','drowned_outer']}
loss=lambda bit:f'var.zgl{bit}>0';wound=lambda bit:f'var.zgw{bit}>0';exitflag=lambda bit:f'var.zge{bit}>0'
cache={'var.zg_loss':'zg_loss','var.zg_wounds':'zg_wounds','var.zg_exit':'zg_exit','var.zg_detail':'if(distance<16,1,0)','var.zg_walk':'if(distance<16,sin(limb_swing/1.4)*limb_speed,0)','var.zg_hit':'if(distance<24,-sin(hurt_time*.7)*hurt_time*.04,0)','var.zg_fire':'if(is_burning&&distance<24,sin(age*.8)*.55,0)'}
for field,prefix in [('loss','l'),('wounds','w'),('exit','e')]:
 for bit in range(8):cache[f'var.zg{prefix}{bit}']=f'fmod(floor(var.zg_{field}/{1<<bit}),2)'
checks=0;added=0
# All geometry-changing lesion helpers have a safe, hidden default independent of animation parsing.
def lesion(node,ident,boxes,condition,material,driver):
 global added
 part={'id':ident,'invertAxis':node.get('invertAxis',''),'translate':[0,0,0],'scale':0,'texture':material,'boxes':boxes}
 node.setdefault('submodels',[]).append(part)
 driver[ident+'.visible']=condition
 for axis in ['sx','sy','sz']:driver[ident+'.'+axis]=f'if({condition},1,0)'
 added+=1
 return part

def preserve(node,ident,condition,driver):
 boxes=node.pop('boxes',[])
 if not boxes:return
 part={'id':ident,'invertAxis':node.get('invertAxis',''),'mirrorTexture':node.get('mirrorTexture',''),'translate':[0,0,0],'boxes':copy.deepcopy(boxes)}
 node.setdefault('submodels',[]).insert(0,part);driver[ident+'.visible']=condition
 assert part['boxes']==boxes
 return boxes

def splitbox(coords,uvs,sizeadd=0):
 box={'coordinates':coords,**uvs}
 if sizeadd:box['sizeAdd']=sizeadd
 return box

def head_split(node,driver,outer):
 full=copy.deepcopy(node.get('boxes',[]));preserve(node,'zg_healthy_head',f'!({loss(0)})&&!({loss(1)})',driver)
 # Keep the original head/hat completely unchanged until the head or jaw actually loses tissue.
 for ident,bit,y,height,low,high in [('zg_cranium',0,3,5,8,13),('zg_jaw',1,0,3,13,16)]:
  boxes=[]
  for i,b in enumerate(full):
   offset=32 if i==1 else 0
   uvs={'uvNorth':[offset+8,low,offset+16,high],'uvSouth':[offset+24,low,offset+32,high],'uvWest':[offset,low,offset+8,high],'uvEast':[offset+16,low,offset+24,high],'uvUp':[offset+8,0,offset+16,8],'uvDown':[offset+16,0,offset+24,8]}
   boxes.append(splitbox([-4,y,-4,8,height,8],uvs,b.get('sizeAdd',0)))
  # Remaining original tissue when only the other head section is missing.
  cond=f'({loss(0)}||{loss(1)})&&!({loss(bit)})'
  p=lesion(node,ident,boxes,cond,'',driver);p.pop('texture')
 if outer:return
 detail='var.zg_detail>0'
 # Exposed internal skull / jaw and red tissue, not a black skin replacement.
 lesion(node,'zg_skull_flesh',[{'coordinates':[-4,3.1,-3.8,4,4.8,7.6],'textureOffset':[0,0]}],f'({loss(0)})&&{detail}','zg_flesh.png',driver)
 lesion(node,'zg_skull_bone',[{'coordinates':[-3.7,3.5,-3.91,3.2,4.1,7.32],'textureOffset':[0,0]}],f'({loss(0)})&&{detail}','zg_bone.png',driver)
 # Retain the uninjured half of the skull's original skin and hat.
 half=[]
 for i,b in enumerate(full):
  off=32 if i==1 else 0
  uvs={'uvNorth':[off+8,8,off+12,13],'uvSouth':[off+28,8,off+32,13],'uvWest':[off,8,off+8,13],'uvEast':[off+16,8,off+24,13],'uvUp':[off+8,0,off+12,8],'uvDown':[off+16,0,off+20,8]}
  half.append(splitbox([0,3,-4,4,5,8],uvs,b.get('sizeAdd',0)))
 p=lesion(node,'zg_skull_remaining_skin',half,f'({loss(0)})','',driver);p.pop('texture')
 lesion(node,'zg_jaw_bone',[{'coordinates':[-2.8,2.4,-3.5,5.6,.6,6.5],'textureOffset':[0,0]}],f'({loss(1)})&&{detail}','zg_bone.png',driver)
 lesion(node,'zg_jaw_flesh',[{'coordinates':[-3.7,2.7,-3.7,7.4,.4,7.4],'textureOffset':[0,0]}],f'({loss(1)})&&{detail}','zg_flesh.png',driver)
 for bit,y in [(0,5.8),(1,1.3)]:
  mark=[{'coordinates':[-.85,y,-4.03,1.7,1.5,.025],'textureOffset':[0,0]}]
  lesion(node,'zg_head_mark'+str(bit),mark,f'({wound(bit)})&&!({loss(bit)})&&{detail}','zg_bullet.png',driver)
  lesion(node,'zg_head_exit'+str(bit),[{'coordinates':[-1,y,4.01,2,1.6,.025],'textureOffset':[0,0]}],f'({exitflag(bit)})&&!({loss(bit)})&&{detail}','zg_flesh.png',driver)

def augment(kind,model):
 driver={};outer=kind=='drowned_outer'
 for node in model['models']:
  part=node.get('part','')
  if part=='headwear':
   head=next((x for x in node.get('submodels',[]) if x.get('id')=='head2'),None)
   if head is not None:
    head_split(head,driver,outer)
    if any(x.get('id')=='eyes' for x in head.get('submodels',[])):driver['eyes.visible']=f'!({loss(0)})'
  if part not in ['body','left_arm','right_arm','left_leg','right_leg']:continue
  original=copy.deepcopy(node.get('boxes',[]));ident='zg_original_'+part
  if part=='body':
   preserve(node,ident,f'!({loss(6)})&&!({loss(7)})',driver)
   # Split only after torso tissue loss; an intact body uses the exact original box and UV.
   for bit,x in [(6,-4),(7,0)]:
    u=24 if bit==6 else 20
    uvs={'uvNorth':[u,20,u+4,32],'uvSouth':[32 if bit==6 else 36,20,36 if bit==6 else 40,32],'uvWest':[16,20,20,32],'uvEast':[28,20,32,32],'uvUp':[u,16,u+4,20],'uvDown':[28 if bit==6 else 32,16,32 if bit==6 else 36,20]}
    boxes=[splitbox([x,12,-2,4,12,4],uvs,original[0].get('sizeAdd',0))]
    p=lesion(node,'zg_torso_remaining'+str(bit),boxes,f'({loss(6)}||{loss(7)})&&!({loss(bit)})','',driver);p.pop('texture')
    if not outer:
     lesion(node,'zg_torso_meat'+str(bit),[{'coordinates':[x+.35,12.4,-1.7,3.3,11.2,3.4],'textureOffset':[0,0]}],f'({loss(bit)})&&var.zg_detail>0','zg_flesh.png',driver)
     lesion(node,'zg_torso_ribs'+str(bit),[{'coordinates':[x+1,16+i*1.7,-1.78,2,.55,.18],'textureOffset':[0,0]} for i in range(4)],f'({loss(bit)})&&var.zg_detail>0','zg_bone.png',driver)
   bits=[6,7]
  else:
   bit={'left_arm':2,'right_arm':3,'left_leg':4,'right_leg':5}[part];bits=[bit]
   preserve(node,ident,f'!({loss(bit)})',driver)
   if not outer and original:
    x,y,z,w,h,d=original[0]['coordinates']
    # Interior wound surface and small protruding bone remain connected to the joint.
    joint=y+h-1 if 'arm' in part else y+h-1
    lesion(node,'zg_joint_flesh'+str(bit),[{'coordinates':[x+.15,joint,z+.15,w-.3,.8,d-.3],'textureOffset':[0,0]}],f'({loss(bit)})&&var.zg_detail>0','zg_flesh.png',driver)
    lesion(node,'zg_joint_bone'+str(bit),[{'coordinates':[x+w/2-.4,joint-.7,z+d/2-.4,.8,1.5,.8],'textureOffset':[0,0]}],f'({loss(bit)})&&var.zg_detail>0','zg_bone.png',driver)
  if not outer and original:
   x,y,z,w,h,d=original[0]['coordinates']
   for bit in bits:
    lateral=(.28 if bit==6 else .7 if bit==7 else .5)
    lesion(node,'zg_entry'+str(bit),[{'coordinates':[x+w*lateral-.65,y+h*.48,z-.03,1.3,1.6,.025],'textureOffset':[0,0]}],f'({wound(bit)})&&!({loss(bit)})&&var.zg_detail>0','zg_bullet.png',driver)
    lesion(node,'zg_exit'+str(bit),[{'coordinates':[x+w*lateral-.9,y+h*.45,z+d+.005,1.8,2,.025],'textureOffset':[0,0]}],f'({exitflag(bit)})&&!({loss(bit)})&&var.zg_detail>0','zg_flesh.png',driver)
 # Root animations are the only animation location read by EMF 3.3.11.
 root=model['models'][0];jpm=json.loads(data[base+root.pop('model')]);fresh=copy.deepcopy(jpm['animations'])
 root['animations']=[cache]+fresh
 # Add elastic reactions to the Fresh pose, rather than overriding it with static extended arms.
 reaction={'body.rx':'body.rx+var.zg_hit*.7','body.rz':'body.rz+var.zg_hit*.9+var.zg_walk*.06+if(is_burning,sin(age*.55)*.12,0)','head.rz':f'head.rz+var.zg_hit*.65-var.zg_walk*.12+if({loss(0)},sin(age*.18)*.13,0)','left_arm.rx':'left_arm.rx+var.zg_fire','right_arm.rx':'right_arm.rx-var.zg_fire','left_arm.rz':'left_arm.rz+var.zg_fire*.7','right_arm.rz':'right_arm.rz+var.zg_fire*.7'}
 crawl=f'({loss(4)}||{loss(5)})'
 reaction.update({'body.rx':f'if({crawl},-pi/2,body.rx+var.zg_hit*.7)','body.ty':f'if({crawl},21,body.ty)','head.ty':f'if({crawl},21,head.ty)','head.tz':f'if({crawl},-8,head.tz)','headwear.ty':f'if({crawl},21,headwear.ty)','headwear.tz':f'if({crawl},-8,headwear.tz)','left_arm.ty':f'if({crawl},21,left_arm.ty)','right_arm.ty':f'if({crawl},21,right_arm.ty)','left_arm.tz':f'if({crawl},-8,left_arm.tz)','right_arm.tz':f'if({crawl},-8,right_arm.tz)','left_arm.rx':f'if({crawl},-pi/2+sin(limb_swing)*.5,left_arm.rx+var.zg_fire+var.zg_walk*.3)','right_arm.rx':f'if({crawl},-pi/2-sin(limb_swing)*.5,right_arm.rx-var.zg_fire-var.zg_walk*.3)','left_leg.ty':f'if({crawl},21,left_leg.ty)','right_leg.ty':f'if({crawl},21,right_leg.ty)','left_leg.rx':f'if({crawl},pi/2,left_leg.rx)','right_leg.rx':f'if({crawl},pi/2,right_leg.rx)'})
 reaction['head.rx']=f'head.rx+if({loss(0)},.14+sin(age*.11)*.08,0)'
 reaction['headwear.rx']='head.rx';reaction['headwear.ry']='head.ry';reaction['headwear.rz']='head.rz'
 reaction['body.rz']+=f'+if({loss(2)},.09,0)-if({loss(3)},.09,0)'
 root['animations'] += [reaction,driver]
 # Explicit atlas regions: a one-pixel-width cuboid must not sample a transparent corner of the wound image.
 def materials(n):
  material=n.get('texture','')
  if material.startswith('zg_'):
   for box in n.get('boxes',[]):
    box.pop('textureOffset',None)
    region=[0,0,8,8] if material=='zg_bullet.png' else [0,0,16,16] if material=='zg_flesh.png' else [16,0,24,8]
    for face in ['uvNorth','uvSouth','uvEast','uvWest','uvUp','uvDown']:box[face]=region
    if n['id']=='zg_skull_bone':box['uvNorth']=[4,0,8,5]
    if n['id']=='zg_jaw_bone':box['uvNorth']=[0,6,8,8]
  for child in n.get('submodels',[]):materials(child)
 for n in model['models']:materials(n)

 model['credit']='Original connected Fresh Animations by FreshLX and TZP textures by Tissou; injury overlays hidden by default'
 return model

for kind,orig in originals.items():
 modified=augment(kind,copy.deepcopy(orig));data[base+kind+'.jem']=(json.dumps(modified,indent=2)+'\n').encode();data[base+kind+'.properties']=b'models.1=1\n'
 # Validate the intact geometry independently of lesion animations.
 before={n['part']:n for n in orig['models']};after={n['part']:n for n in modified['models']}
 for part,node in before.items():
  changed=after[part];assert node.get('translate')==changed.get('translate');assert node.get('invertAxis')==changed.get('invertAxis');checks+=2
  if node.get('boxes'):
   healthy=next(x for x in changed['submodels'] if x['id']=='zg_original_'+part)
   assert node['boxes']==healthy['boxes'];assert healthy['translate']==[0,0,0];checks+=2
 def inspect(node,top=False):
  global checks
  if not top:assert 'animations' not in node;checks+=1
  if 'texture' in node and node['texture'].startswith('zg_'):assert node['scale']==0;checks+=1
  for child in node.get('submodels',[]):inspect(child)
 for n in modified['models']:inspect(n,True)
 assert 'burn' not in json.dumps(modified).replace('is_burning','')
 assert 'zg_char' not in json.dumps(modified)
 checks+=2
# New procedural materials only; none of the user's skin images are edited.
rng=random.Random(4081)
for material in ['flesh','bone','bullet']:
 im=Image.new('RGBA',(64,64));pixels=im.load()
 flesh=[(69,25,30),(101,35,40),(131,51,54),(158,71,66),(174,91,79)]
 bone=[(104,95,72),(142,134,105),(181,173,140),(205,198,166)]
 for y in range(64):
  for x in range(64):
   if material=='flesh':
    band=(y//2+(x//5))%5;index=max(0,min(4,band+rng.choice([-1,0,0,0,1])))
    color=(*flesh[index],255)
    if (x+2*y)%17==0:color=(*flesh[0],255)
   elif material=='bone':color=(*bone[rng.choice([1,2,2,2,3])],255)
   else:
    dx=(x%8)-3.5;dy=(y%8)-3.5;dist=dx*dx+dy*dy
    color=(29,18,19,255) if dist<2.8 else (85,27,32,255) if dist<6 else (139,48,47,230) if dist<10+rng.randrange(3) else (0,0,0,0)
   pixels[x,y]=color
 if material=='bone':
  # Eight-pixel skull face: eye sockets, nasal cavity, and individually separated teeth.
  skull=['.BBBBBB.','BBBBBBBB','BBBBBBBB','B..BB..B','B..BB..B','BB....BB','.B.BB.B.','..BBBB..']
  for y,row in enumerate(skull):
   for x,c in enumerate(row):pixels[x,y]=(*((33,29,24) if c=='.' else bone[2 if (x+y)%3 else 3]),255)
 buf=io.BytesIO();im.save(buf,format='PNG');data[base+'zg_'+material+'.png']=buf.getvalue()
data['pack.mcmeta']=json.dumps({'pack':{'pack_format':34,'description':'Original TZP skins / Fresh poses + procedural flesh, bones and bullet injuries; no charcoal layers'}}).encode()
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
 for n,b in data.items():z.writestr(n,b)
with zipfile.ZipFile(source) as a,zipfile.ZipFile(out) as b:
 assert b.testzip() is None
 original=[n for n in a.namelist() if n.endswith(('.png','.ogg'))];assert all(a.read(n)==b.read(n) for n in original)
 assert not any('zg_char' in n for n in b.namelist())
(r/'validation.json').write_text(json.dumps({'source_sha256':hashlib.sha256(source.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'structural_checks':checks,'original_images_and_sounds_preserved':len(original),'injury_subparts_with_hidden_default':added,'minecraft_runtime_tested':False},indent=2)+'\n')
print('Original geometry, hidden injury parts and animation placement checks:',checks,'Original images/sounds preserved:',len(original),'Injury parts:',added)
