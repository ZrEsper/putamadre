from pathlib import Path
import zipfile,json,copy,io,random,re,hashlib,sys
from PIL import Image
r=Path(__file__).resolve().parent
source=Path(sys.argv[1])
out=r/'TZP-1.21.1-Gore.8-local-blended-surfaces.zip';base='assets/minecraft/optifine/cem/'
with zipfile.ZipFile(source) as z:data={n:z.read(n) for n in z.namelist() if not n.endswith('/') and not n.lower().endswith('thumbs.db')}
cache={'var.zg_loss':'zg_loss','var.zg_wounds':'zg_wounds','var.zg_broken':'zg_broken','var.zg_ballistic':'zg_ballistic','var.zg_blunt':'zg_blunt','var.zg_cut':'zg_cut','var.zg_rear':'zg_rear','var.zg_detail':'if(distance<20,1,0)'}
for label,code,count in [('loss','l',8),('wounds','w',8),('broken','b',4),('ballistic','p',8),('blunt','t',8),('cut','c',8),('rear','r',8)]:
 for bit in range(count):cache[f'var.zg{code}{bit}']=f'fmod(floor(var.zg_{label}/{1<<bit}),2)'
cache['varb.zg_crawl']='var.zgl4>0||var.zgl5>0||(var.zgb2>0&&var.zgb3>0)'
# UV rectangles are on the original skin atlas; overlays are transparent everywhere else.
regions={0:(8,8,16,13),1:(8,13,16,16),2:(44,20,48,32),3:(44,20,48,32),4:(4,20,8,32),5:(4,20,8,32),6:(24,20,28,32),7:(20,20,24,32)}
for zone,rect in regions.items():
 for style in ['ballistic','blunt','cut']:
  for severity in ['mild','severe']:
   im=Image.new('RGBA',(64,64));px=im.load();x0,y0,x1,y1=rect;w=x1-x0;h=y1-y0
   cx=(x0+x1-1)/2;cy=y0+h*.48;rng=random.Random(zone*43+len(style)*7+(severity=='severe'))
   for y in range(y0,y1):
    for x in range(x0,x1):
     dx=(x-cx)/max(1,w*.45);dy=(y-cy)/max(1,min(h*.35,3));dist=dx*dx+dy*dy
     if style=='cut':inside=abs(dx+dy*.65)<(.65 if severity=='severe' else .38) and dist<2.2
     else:inside=dist<(2.5 if severity=='severe' else 1.2)+rng.uniform(-.3,.3)
     if not inside:continue
     edge=dist>1.0 if severity=='mild' else dist>1.9
     colors=[(56,27,26),(85,35,32),(111,46,40),(130,63,51)]
     color=colors[rng.randrange(len(colors))]
     if style=='ballistic' and severity=='mild' and dist<.35:color=(30,23,21)
     elif (severity=='severe' or style=='blunt') and abs(dx)<.4 and abs(dy)<.65:
      color=(158,150,121) if (x+y)%3 else (117,112,89)
     px[x,y]=(*color,155 if edge else 245)
   if zone==0 and severity=='severe':
    # Exposed skull is painted within the face, never a detached model.
    for y in range(y0+1,y1):
     for x in range(x0+1,x0+4):px[x,y]=(152+(x+y)%3*7,145+(x+y)%3*6,117+(x+y)%3*5,250)
    px[x0+2,y0+2]=(36,29,24,250)
   buf=io.BytesIO();im.save(buf,format='PNG');data[base+f'zg_surface_{zone}_{style}_{severity}.png']=buf.getvalue()
checks=0
for kind in ['zombie','husk','drowned','drowned_outer']:
 original=json.loads(data[base+kind+'.jem']);m=copy.deepcopy(original);root=m['models'][0];jpm=json.loads(data[base+root.pop('model')]);driver={};root['animations']=[cache]+copy.deepcopy(jpm['animations'])
 for node in m['models']:
  part=node.get('part');target=node;bits={'body':[6,7],'left_arm':[2],'right_arm':[3],'left_leg':[4],'right_leg':[5]}.get(part,[])
  if part=='headwear':target=next(n for n in node['submodels'] if n['id']=='head2');bits=[0,1]
  if kind=='drowned_outer':continue
  def boxed(n):
   if n.get('boxes'):return n
   for child in n.get('submodels',[]):
    found=boxed(child)
    if found is not None:return found
   return None
  if bits and not target.get('boxes'):
   target=boxed(target)
   if target is None:continue
  for bit in bits:
   for style,mask in [('ballistic','p'),('blunt','t'),('cut','c')]:
    # Legacy untyped injuries default to blunt, never a bullet puncture.
    typ=f'var.zg{mask}{bit}>0'
    if style=='blunt':typ+=f'||(var.zgp{bit}==0&&var.zgt{bit}==0&&var.zgc{bit}==0)'
    for severity in ['mild','severe']:
     ident=f'zg_surface_{bit}_{style}_{severity}';lost=f'var.zgl{bit}>0'
     cond=f'var.zgw{bit}>0&&var.zg_detail>0&&({typ})&&'+(lost if severity=='severe' else f'!({lost})')
     boxes=copy.deepcopy(target['boxes'][:1]);boxes[0]['sizeAdd']=0.26 if part=='headwear' else 0.004
     surface={'id':ident,'invertAxis':target.get('invertAxis',''),'mirrorTexture':target.get('mirrorTexture',''),'translate':[0,0,0],'scale':0,'texture':ident+'.png','boxes':boxes}
     target.setdefault('submodels',[]).append(surface);driver[ident+'.visible']=cond
     for axis in ['sx','sy','sz']:driver[ident+'.'+axis]=f'if({cond},1,0)'
 if kind!='drowned_outer':
  def rear(n):
   for child in list(n.get('submodels',[])):
    ident=child.get('id','')
    if ident.startswith('zg_surface_'):
     bit=int(ident.split('_')[2]);old=driver[ident+'.visible']
     driver[ident+'.visible']=f'({old})&&var.zgr{bit}==0'
     for axis in ['sx','sy','sz']:driver[ident+'.'+axis]=f'if({driver[ident+".visible"]},1,0)'
     back=copy.deepcopy(child);bid=ident+'_rear';back['id']=bid;back['texture']=bid+'.png'
     cond=f'({old})&&var.zgr{bit}>0';driver[bid+'.visible']=cond
     for axis in ['sx','sy','sz']:driver[bid+'.'+axis]=f'if({cond},1,0)'
     n['submodels'].append(back)
     front=Image.open(io.BytesIO(data[base+ident+'.png'])).convert('RGBA');im=Image.new('RGBA',(64,64))
     x0,y0,x1,y1=regions[bit];shift={0:16,1:16,2:8,3:8,4:8,5:8,6:8,7:16}[bit]
     im.paste(front.crop((x0,y0,x1,y1)),(x0+shift,y0));buf=io.BytesIO();im.save(buf,format='PNG');data[base+bid+'.png']=buf.getvalue()
    else:rear(child)
  for n in m['models']:rear(n)
 # Rotate the shared skeleton, rather than relocating each detached piece separately.
 pose={'root.rx':'if(varb.zg_crawl,pi/2,0)','root.ty':'if(varb.zg_crawl,21,0)','body.rx':'if(varb.zg_crawl,0,body.rx)','head.rx':'if(varb.zg_crawl,-pi/2,head.rx)'}
 for part in ['body','head','headwear','left_arm','right_arm','left_leg','right_leg']:
  ty=2 if 'arm' in part else 12 if 'leg' in part else 0
  pose[part+'.ty']=f'if(varb.zg_crawl,{ty},{part}.ty)';pose[part+'.tz']=f'if(varb.zg_crawl,0,{part}.tz)'
 for part,bit,broken in [('left_arm',2,0),('right_arm',3,1)]:
  pose[part+'.rx']=f'if(var.zgl{bit}>0||var.zgb{broken}>0,0.15,if(varb.zg_crawl,-pi+sin(limb_swing)*0.25,{part}.rx))'
 for part in ['left_leg','right_leg']:pose[part+'.rx']=f'if(varb.zg_crawl,sin(limb_swing)*0.08,{part}.rx)'
 pose['headwear.rx']='head.rx';pose['headwear.ry']='head.ry';pose['headwear.rz']='head.rz'
 root['animations']+=[pose,driver]
 for n,old in zip(m['models'],original['models']):
  assert n.get('translate')==old.get('translate') and n.get('boxes')==old.get('boxes');checks+=1
 def check(n):
  global checks
  if n.get('id','').startswith('zg_surface_'):
   assert n['scale']==0 and n['translate']==[0,0,0];checks+=1
  for child in n.get('submodels',[]):check(child)
 for n in m['models']:check(n)
 for n in m['models']:
  for a in n.get('animations',[]):
   for expression in a.values():assert not re.search(r'(?<![\w.])\.\d+',expression)
 data[base+kind+'.jem']=(json.dumps(m,indent=2)+'\n').encode();data[base+kind+'.properties']=b'models.1=1\n'
data['pack.mcmeta']=json.dumps({'pack':{'pack_format':34,'description':'TZP Gore 8: localized blended skin lesions / connected crawl skeleton'}}).encode()
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
 for n,b in data.items():z.writestr(n,b)
with zipfile.ZipFile(source) as a,zipfile.ZipFile(out) as b:
 assert b.testzip() is None
 preserved=[n for n in a.namelist() if n.endswith(('.png','.ogg'))];assert all(a.read(n)==b.read(n) for n in preserved)
(r/'pack-validation.json').write_text(json.dumps({'geometry_checks':checks,'original_images_and_sounds_preserved':len(preserved),'minecraft_runtime_tested':False,'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest()},indent=2)+'\n')
print('Unchanged original geometry and attached surface checks:',checks,'Original images/sounds:',len(preserved))
