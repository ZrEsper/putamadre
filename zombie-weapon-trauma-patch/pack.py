from pathlib import Path
import zipfile,json,copy,re,io,hashlib
from PIL import Image
r=Path(__file__).resolve().parent
source=r.parent/'zombie-gore-emf-syntax-patch/TZP-1.21.1-Gore.6-emf-syntax-fix.zip'
out=r/'TZP-1.21.1-Gore.7-weapon-trauma.zip';base='assets/minecraft/optifine/cem/'
with zipfile.ZipFile(source) as z:data={n:z.read(n) for n in z.namelist()}
count=0
for kind in ['zombie','husk','drowned','drowned_outer']:
 model=json.loads(data[base+kind+'.jem']);root=model['models'][0];cache=root['animations'][0];driver=root['animations'][-1]
 for label,prefix in [('ballistic','p'),('blunt','b'),('cut','c')]:
  cache['var.zg_'+label]='zg_'+label
  for bit in range(8):cache[f'var.zgt{prefix}{bit}']=f'fmod(floor(var.zg_{label}/{1<<bit}),2)'
 def visit(node):
  global count
  additions=[]
  for child in node.get('submodels',[]):
   visit(child)
   if child.get('texture')!='zg_bullet.png':continue
   ident=child['id'];bit=int(re.search(r'(\d+)$',ident)[1]);old=driver[ident+'.visible']
   ballistic=f'(var.zgtp{bit}>0||(var.zgtb{bit}==0&&var.zgtc{bit}==0))'
   driver[ident+'.visible']=f'({old})&&{ballistic}'
   for axis in ['sx','sy','sz']:driver[ident+'.'+axis]=f'if({driver[ident+".visible"]},1,0)'
   for style,material,prefix in [('blunt','zg_blunt.png','b'),('cut','zg_cut.png','c')]:
    n=copy.deepcopy(child);newid=ident+'_'+style;n['id']=newid;n['texture']=material;n['scale']=0
    cond=f'({old})&&var.zgt{prefix}{bit}>0';driver[newid+'.visible']=cond
    for axis in ['sx','sy','sz']:driver[newid+'.'+axis]=f'if({cond},1,0)'
    additions.append(n);count+=1
  node.setdefault('submodels',[]).extend(additions)
 for node in model['models']:visit(node)
 data[base+kind+'.jem']=(json.dumps(model,indent=2)+'\n').encode()
# Irregular crush surface with red torn muscle and small pale bone chips;
# separate thin diagonal cuts instead of the round ballistic hole.
for style in ['blunt','cut']:
 im=Image.new('RGBA',(64,64),(0,0,0,0));p=im.load()
 for y in range(64):
  for x in range(64):
   a,b=x%8,y%8
   if style=='blunt':
    inside=1<=a<=6 and 1<=b<=6 and (a+b)%9!=0
    if inside:p[x,y]=(116,30,37,255) if (a+2*b)%3 else (171,63,60,255)
    if (a,b) in [(3,3),(4,3),(3,4)]:p[x,y]=(197,187,149,255)
    if (a,b) in [(2,5),(3,5),(5,2)]:p[x,y]=(49,18,22,255)
   else:
    if abs(a+b-7)<=1 and 1<=a<=6:p[x,y]=(48,17,22,255) if a+b==7 else (169,47,49,255)
    elif abs(a+b-7)==2 and 1<=a<=6:p[x,y]=(111,28,35,215)
 buf=io.BytesIO();im.save(buf,format='PNG');data[base+'zg_'+style+'.png']=buf.getvalue()
data['pack.mcmeta']=json.dumps({'pack':{'pack_format':34,'description':'TZP Gore 7: ballistic holes, blunt muscle/bone trauma, blade cuts'}}).encode()
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
 for n,b in data.items():z.writestr(n,b)
with zipfile.ZipFile(source) as a,zipfile.ZipFile(out) as b:
 assert b.testzip() is None
 preserved=[n for n in a.namelist() if n.endswith(('.png','.ogg'))]
 assert all(a.read(n)==b.read(n) for n in preserved)
(root:=r/'pack-validation.json').write_text(json.dumps({'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'new_style_overlays':count,'preserved_images_and_sounds':len(preserved),'minecraft_runtime_tested':False},indent=2)+'\n')
print('Distinct type overlays:',count,'Preserved images/sounds:',len(preserved))
