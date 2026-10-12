import json,math,io,random,copy
from PIL import Image,ImageDraw
# Model state remains bounded: eight damage zones, four burn phases, five cached skeleton variants.
def model(index,kind):
 variant=[(1,0),(.94,.07),(1.06,-.05),(1,.12),(.97,-.1)][index-1];w,lean=variant
 loss=lambda bit:f'var.l{bit}>0'
 wound=lambda bit:f'var.w{bit}>0'
 exit=lambda bit:f'var.e{bit}>0'
 crawl=f'({loss(4)}||{loss(5)})'
 fire='is_burning'
 models=[]
 # Native humanoid pivots and UV offsets are explicit; no enlarged mutant geometry.
 desc=[('head',[0,0,0],None,None),('body',[0,0,0],None,None),('left_arm',[5,2,0],[-1,-2,-2,4,12,4],[40,16]),('right_arm',[-5,2,0],[-3,-2,-2,4,12,4],[40,16]),('left_leg',[1.9,12,0],[-2,0,-2,4,12,4],[0,16]),('right_leg',[-1.9,12,0],[-2,0,-2,4,12,4],[0,16])]
 zones={'left_arm':2,'right_arm':3,'left_leg':4,'right_leg':5}
 for part,pivot,box,uv in desc:
  node={'part':part,'id':part,'translate':[0,0,0],'submodels':[],'animations':[]}
  if part.startswith('left_'):node['mirrorTexture']='u'
  if part=='head':
   pieces=[('cranium',0,[-4,-8,-4,8,5,8],[0,0]),('jaw',1,[-4,-3,-4,8,3,8],[0,5])]
   node['animations']=[{'var.loss':'zg_loss','var.wounds':'zg_wounds','var.burn':'zg_burn','var.exit':'zg_exit','head.rx':f'torad(head_pitch)+if({loss(0)},sin(age*.3)*.12,0)','head.ry':'torad(head_yaw)','head.ty':f'if({crawl},21,0)','head.tz':f'if({crawl},-8,0)'}]
  elif part=='body':
   pieces=[('body_left',6,[0,0,-2,4,12,4],[20,16]),('body_right',7,[-4,0,-2,4,12,4],[16,16])]
   node['animations']=[{'body.rx':f'if({crawl},-pi/2,{lean}+sin(age*.05)*.02)','body.ty':f'if({crawl},21,0)','body.sx':str(w)}]
  else:
   pieces=[(part+'_flesh',zones[part],box,uv)]
   phase=0 if part.startswith('left') else math.pi
   if 'arm' in part:
    node['animations']=[{part+'.rx':f'if({crawl},-pi/2+sin(limb_swing+{phase})*.4,if({fire},-1.3+sin(age*.7+{phase})*.75,-1.1+sin(limb_swing+{phase})*limb_speed*.45))',part+'.rz':f'if({fire},sin(age*.6+{phase})*.35,0)',part+'.ty':f'if({crawl},21,2)',part+'.tz':f'if({crawl},-8,0)'}]
   else:node['animations']=[{part+'.rx':f'if({crawl},pi/2,cos(limb_swing*.6662+{phase})*limb_speed*1.4)',part+'.ty':f'if({crawl},21,12)'}]
  for ident,bit,coords,uv in pieces:
   timer='var.cut'+str(bit)
   # Head and torso splits preserve the original face UV rather than repeating the whole head on each fragment.
   b={'coordinates':coords,'textureOffset':uv}
   if part=='head':
    low,high=(8,13) if bit==0 else (13,16)
    b={'coordinates':coords,'uvNorth':[8,low,16,high],'uvSouth':[24,low,32,high],'uvWest':[0,low,8,high],'uvEast':[16,low,24,high],'uvUp':[8,0,16,8],'uvDown':[16,0,24,8]}
   if part=='body':
    left=bit==6;a=24 if left else 20;b={'coordinates':coords,'uvNorth':[a,20,a+4,32],'uvSouth':[32 if left else 36,20,36 if left else 40,32],'uvWest':[16,20,20,32],'uvEast':[28,20,32,32],'uvUp':[a,16,a+4,20],'uvDown':[28 if left else 32,16,32 if left else 36,20]}
   node['submodels'].append({'id':ident,'mirrorTexture':'u' if part.startswith('left_') else '', 'boxes':[b],'animations':[{ident+'.visible':f'!({loss(bit)})'}]})
   fall=ident+'_fallen'
   node['submodels'].append({'id':fall,'mirrorTexture':'u' if part.startswith('left_') else '', 'boxes':[b],'animations':[{timer:f'if({loss(bit)},if({timer}>0,{timer},age),0)',fall+'.visible':f'({loss(bit)})&&age-{timer}<45&&distance<16',fall+'.ty':f'min(24,(age-{timer})*.65)',fall+'.rz':f'(age-{timer})*.07',fall+'.sx':f'max(.01,1-max(0,age-{timer}-30)/15)',fall+'.sy':f'max(.01,1-max(0,age-{timer}-30)/15)',fall+'.sz':f'max(.01,1-max(0,age-{timer}-30)/15)'}]})
   x,y,z,a,h,d=coords
   stump=ident+'_stump'
   cap=[x,y,z,a,.2,d]
   if bit==0:cap=[-4,-3.1,-4,8,.2,8]
   if bit==1:cap=[-4,-3.1,-4,8,.2,8]
   if bit>=6:cap=[-.1,0,-2,.2,12,4]
   node['submodels'].append({'id':stump,'texture':'zg_wound.png','boxes':[{'coordinates':cap,'textureOffset':[0,0]}],'animations':[{stump+'.visible':f'({loss(bit)})&&distance<16'}]})
   for back in [False,True]:
    mark=ident+('_exit' if back else '_wound');scale=.95 if back else .7
    node['submodels'].append({'id':mark,'texture':'zg_wound.png','boxes':[{'coordinates':[x+a/2-scale,y+h*.4,z+d+.01 if back else z-.03,scale*2,scale*2,.02],'textureOffset':[0,0]}],'animations':[{mark+'.visible':f'({wound(bit)})&&'+(f'({exit(bit)})&&' if back else '')+f'!({loss(bit)})&&distance<16'}]})
   burn=ident+'_char'
   node['submodels'].append({'id':burn,'texture':'zg_char.png','boxes':[{'coordinates':coords,'textureOffset':uv,'sizeAdd':.015}],'animations':[{burn+'.visible':f'var.burn>={1 if part=="body" else 2 if "arm" in part else 3}&&!({loss(bit)})'}]})
  models.append(node)
 hat={'part':'headwear','id':'headwear','translate':[0,0,0],'submodels':[],'animations':[{'headwear.rx':f'torad(head_pitch)+if({loss(0)},sin(age*.3)*.12,0)','headwear.ry':'torad(head_yaw)','headwear.ty':f'if({crawl},21,0)','headwear.tz':f'if({crawl},-8,0)','headwear.visible':'var.burn<3'}]}
 for bit,coords,lo,hi in [(0,[-4,-8,-4,8,5,8],8,13),(1,[-4,-3,-4,8,3,8],13,16)]:
  ident='hat_'+str(bit);hat['submodels'].append({'id':ident,'boxes':[{'coordinates':coords,'sizeAdd':.25,'uvNorth':[40,lo,48,hi],'uvSouth':[56,lo,64,hi],'uvWest':[32,lo,40,hi],'uvEast':[48,lo,56,hi],'uvUp':[40,0,48,8],'uvDown':[48,0,56,8]}],'animations':[{ident+'.visible':f'!({loss(bit)})'}]})
 # Compact Fresh Animations inspired pose: breathing, weight transfer, stagger and foot planting.
 # Cache trig once per rendered model; skip secondary motion beyond 24 blocks.
 head=models[0]['animations'][0]
 head.update({'var.near':'if(distance<16,1,0)','var.walk':'limb_swing*.6662','var.step':'sin(var.walk)','var.sway':'if(var.near>0,sin(var.walk*.5),0)','var.breath':'if(var.near>0,sin(age*.08+id)*.025,0)','var.hit':'if(var.near>0,-sin(hurt_time*.7)*hurt_time*.025,0)','var.flail':'if(var.near>0&&is_burning,sin(age*.65)*.6,0)'})
 # Variables precede all dependent pose assignments in the same ordered animation dictionary.
 variables={k:v for k,v in head.items() if k.startswith('var.')}
 for field,prefix in [('loss','l'),('wounds','w'),('exit','e')]:
  for bit in range(8):variables[f'var.{prefix}{bit}']=f'fmod(floor(var.{field}/{1<<bit}),2)'

 head={**variables,**{k:v for k,v in head.items() if not k.startswith('var.')}}
 head.update({'head.rx':f'torad(head_pitch)*.75+var.breath+var.hit+if({loss(0)},var.sway*.18,0)', 'head.rz':'var.sway*limb_speed*.06+var.hit*.3', 'head.tx':'var.sway*limb_speed*.2'})
 models[0]['animations']=[head]
 body=models[1]['animations'][0]
 body.update({'body.rx':f'if({crawl},-pi/2,{lean}+.12+var.breath+var.hit+if({loss(0)},.14,0))','body.ry':'var.sway*limb_speed*.07','body.rz':f'var.step*limb_speed*.035+if({loss(2)},.08,0)-if({loss(3)},.08,0)','body.ty':f'if({crawl},21,if(var.near>0,abs(var.step)*limb_speed*.35,0))'})
 for node in models[2:]:
  part=node['part'];left=part.startswith('left');sign=1 if left else -1;bit=zones[part]
  anim=node['animations'][0]
  if 'arm' in part:
   anim[part+'.rx']=f'if({crawl},-pi/2+var.step*{sign}*.4,-1.05+var.step*{sign}*limb_speed*.35+var.flail*{sign}+var.hit+sin(pi*swing_progress)*.5)'
   anim[part+'.rz']=f'{sign}*.07+var.flail*{sign}*.5+var.hit*.2'
  else:
   anim[part+'.rx']=f'if({crawl},pi/2+.1,var.step*{sign}*limb_speed*1.15+var.hit*.3)'
   anim[part+'.ty']=f'if({crawl},21,12+if(var.near>0,max(0,var.step*{sign})*limb_speed*.5,0))'
 # Outer head layer follows exactly the same hurt and locomotion pose.
 hat['animations']=[{('headwear'+k[4:]):v for k,v in head.items() if k.startswith('head.')}]
 hat['animations'][0]['headwear.visible']='var.burn<3'
 models.append(hat)
 return {'credit':'TZP textures by Tissou; original injury models and compact animations inspired by Fresh Animations by FreshLX','textureSize':[64,64],'models':models}
def resources(data):
 for kind in ['zombie','husk','drowned','drowned_outer']:
  for i in range(1,6):
   d=model(i,kind)
   if kind=='drowned_outer':
    for node in d['models']:
     node['submodels']=[part for part in node.get('submodels',[]) if not any(suffix in part['id'] for suffix in ['_fallen','_wound','_exit','_char','_stump'])]
     for part in node['submodels']:
      for box in part.get('boxes',[]):box['sizeAdd']=.25
      for anim in part.get('animations',[]):
       for key in list(anim):
        if key.endswith('.visible'):anim[key]='('+anim[key]+')&&var.burn<'+str(1 if node['part']=='body' else 2 if 'arm' in node['part'] else 3)
   # EMF 3.3.11 reads animations on top-level model entries only.
   # Collect every child's visibility/fragment animation into the first root entry.
   animations=[]
   def hoist(node):
    animations.extend(node.pop('animations',[]))
    for child in node.get('submodels',[]):hoist(child)
   for node in d['models']:hoist(node)
   d['models'][0]['animations']=animations
   data[f'assets/minecraft/optifine/cem/{kind}{"" if i==1 else i}.jem']=(json.dumps(d,indent=2)+'\n').encode()
  data[f'assets/minecraft/optifine/cem/{kind}.properties']=(''.join(f'name.{i}=Gore{i}\nmodels.{i}={i}\n' for i in range(1,6))+'models.6=1 2 3 4 5\nweights.6=4 2 2 1 1\n').encode()
 rng=random.Random(8021)
 for name in ['char','wound']:
  im=Image.new('RGBA',(64,64));pix=im.load()
  for y in range(64):
   for x in range(64):
    n=rng.randrange(22);pix[x,y]=(21+n,18+n,16+n,255) if name=='char' else (85+n,12+n//2,17+n//2,255 if (x%4!=0 and y%4!=0) or rng.random()>.45 else 0)
  bio=io.BytesIO();im.save(bio,format='PNG');data['assets/minecraft/optifine/cem/zg_'+name+'.png']=bio.getvalue()
