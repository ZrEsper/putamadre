"""Offline diagnostic geometry/UV renderer; this is not a Minecraft screenshot."""
from pathlib import Path
import json,zipfile,sys,io,re,math
import numpy as np
from PIL import Image,ImageDraw
r=Path(__file__).resolve().parent
source=zipfile.ZipFile(sys.argv[1]);pack=zipfile.ZipFile(r/'TZP-1.21.1-Gore.4-original-skins-cartoon-lesions.zip')
model=json.loads(pack.read('assets/minecraft/optifine/cem/zombie.jem'));original=json.loads(source.read('assets/minecraft/optifine/cem/zombie.jem'))
base='assets/minecraft/optifine/cem/';skin=np.asarray(Image.open(io.BytesIO(source.read('assets/minecraft/textures/entity/zombie/zombie.png'))).convert('RGBA'))
textures={'':skin};textures.update({name:np.asarray(Image.open(io.BytesIO(pack.read(base+name))).convert('RGBA')) for name in ['zg_flesh.png','zg_bone.png','zg_bullet.png']})
def transform(t=(0,0,0),rx=0,ry=0,rz=0):
 x,y,z=t;c,s=math.cos,math.sin
 X=np.array([[1,0,0],[0,c(rx),-s(rx)],[0,s(rx),c(rx)]])
 Y=np.array([[c(ry),0,s(ry)],[0,1,0],[-s(ry),0,c(ry)]])
 Z=np.array([[c(rz),-s(rz),0],[s(rz),c(rz),0],[0,0,1]])
 a=np.eye(4);a[:3,:3]=Z@Y@X;a[:3,3]=[x,y,z];return a
bones={'root':transform(),'head':transform(),'headwear':transform(),'body':transform(),'left_arm':transform((5,2,0),-1.05),'right_arm':transform((-5,2,0),-1.05),'left_leg':transform((1.9,12,0)),'right_leg':transform((-1.9,12,0))}
def condition(expr,loss,wounds,exit):
 for prefix,val in [('l',loss),('w',wounds),('e',exit)]:
  expr=re.sub(r'var\.zg'+prefix+r'(\d)',lambda m:str((val>>int(m.group(1)))&1),expr)
 expr=expr.replace('var.zg_detail','1').replace('&&',' and ').replace('||',' or ').replace('!',' not ')
 return bool(eval(expr,{'__builtins__':{}},{}))
def render(data,loss=0,wounds=0,exit=0):
 canvas=np.zeros((320,288,3),dtype=np.uint8);canvas[:]=[24,29,28];depth=np.full((320,288),1e9);driver=data['models'][0].get('animations',[{}])[-1]
 view=transform(rx=-.13,ry=.32)[:3,:3]
 def polygon(vertices,uv,tex,shade):
  p=(view@vertices.T).T;p[:,0]=p[:,0]*8+144;p[:,1]=p[:,1]*8+75
  u0,v0,u1,v1=uv;uvs=np.array([[u0,v1],[u1,v1],[u1,v0],[u0,v0]])
  for idx in [(0,1,2),(0,2,3)]:
   tri=p[list(idx)];tt=uvs[list(idx)];x0=max(0,int(np.floor(tri[:,0].min())));x1=min(287,int(np.ceil(tri[:,0].max())));y0=max(0,int(np.floor(tri[:,1].min())));y1=min(319,int(np.ceil(tri[:,1].max())))
   if x1<x0 or y1<y0:continue
   xx,yy=np.meshgrid(np.arange(x0,x1+1)+.5,np.arange(y0,y1+1)+.5)
   a,b,c=tri;den=(b[1]-c[1])*(a[0]-c[0])+(c[0]-b[0])*(a[1]-c[1])
   if abs(den)<1e-7:continue
   wa=((b[1]-c[1])*(xx-c[0])+(c[0]-b[0])*(yy-c[1]))/den;wb=((c[1]-a[1])*(xx-c[0])+(a[0]-c[0])*(yy-c[1]))/den;wc=1-wa-wb
   z=wa*a[2]+wb*b[2]+wc*c[2];u=wa*tt[0,0]+wb*tt[1,0]+wc*tt[2,0];v=wa*tt[0,1]+wb*tt[1,1]+wc*tt[2,1]
   color=tex[np.clip(v.astype(int),0,tex.shape[0]-1),np.clip(u.astype(int),0,tex.shape[1]-1)];region=depth[y0:y1+1,x0:x1+1];mask=(wa>=0)&(wb>=0)&(wc>=0)&(z<region)&(color[:,:,3]>100)
   rgb=np.clip(color[:,:,:3]*shade,0,255).astype(np.uint8);canvas[y0:y1+1,x0:x1+1][mask]=rgb[mask];region[mask]=z[mask]
 def node(n,parent):
  ident=n.get('id','');cond=driver.get(ident+'.visible')
  if cond is not None and not condition(cond,loss,wounds,exit):return
  if n.get('scale',1)==0 and cond is None:return
  inv=n.get('invertAxis','');t=[v*(-1 if axis in inv else 1) for axis,v in zip('xyz',n.get('translate',[0,0,0]))];mat=parent@transform(t)
  tex=textures.get(n.get('texture',''),skin)
  for box in n.get('boxes',[]):
   x,y,z,w,h,d=box['coordinates'];x=-x-w if 'x' in inv else x;y=-y-h if 'y' in inv else y;z=-z-d if 'z' in inv else z;add=box.get('sizeAdd',0)
   pts=np.array([[x-add,y-add,z-add],[x+w+add,y-add,z-add],[x+w+add,y+h+add,z-add],[x-add,y+h+add,z-add],[x-add,y-add,z+d+add],[x+w+add,y-add,z+d+add],[x+w+add,y+h+add,z+d+add],[x-add,y+h+add,z+d+add]])
   pts=(mat@np.column_stack([pts,np.ones(8)]).T).T[:,:3];u,v=box.get('textureOffset',[0,0])
   faces=[('uvNorth',[0,1,2,3],[u+d,v+d,u+d+w,v+d+h],.93),('uvSouth',[5,4,7,6],[u+2*d+w,v+d,u+2*d+2*w,v+d+h],.8),('uvWest',[4,0,3,7],[u,v+d,u+d,v+d+h],.72),('uvEast',[1,5,6,2],[u+d+w,v+d,u+2*d+w,v+d+h],.84),('uvUp',[4,5,1,0],[u+d,v,u+d+w,v+d],1),('uvDown',[3,2,6,7],[u+d+w,v,u+d+2*w,v+d],.65)]
   for key,ix,default,shade in faces:
    uv=box.get(key,default)
    if 'textureOffset' not in box and key not in box:continue
    if n.get('mirrorTexture')=='u':uv=[uv[2],uv[1],uv[0],uv[3]]
    polygon(pts[ix],uv,tex,shade)
  for child in n.get('submodels',[]):node(child,mat)
 for n in data['models']:node(n,bones.get(n.get('part'),transform()))
 return Image.fromarray(canvas)
frames=[('Original TZP',render(original)),('Intacto - nuevo',render(model)),('Bala - lesion',render(model,wounds=1|64|4)),('Craneo expuesto',render(model,loss=1,wounds=1)),('Torso / brazo',render(model,loss=64|4,wounds=64|4))]
assert np.array_equal(np.asarray(frames[0][1]),np.asarray(frames[1][1])), 'Healthy geometry/UV render differs from original'
out=Image.new('RGB',(288*len(frames),355),(17,21,20));draw=ImageDraw.Draw(out)
for i,(name,im) in enumerate(frames):out.paste(im,(i*288,30));draw.text((i*288+12,10),name,fill=(226,229,219))
draw.text((12,340),'Previsualizacion tecnica offline; no es una captura de Minecraft.',fill=(171,181,173));out.save(r/'geometry-preview.png');print('Healthy source/new geometry preview is pixel-identical; injury previews written')
