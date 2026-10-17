"""Actual first-person transform diagnostic. No Minecraft execution or edited game images."""
from pathlib import Path
import math,subprocess
from PIL import Image,ImageDraw
r=Path(__file__).resolve().parent;cp=str(r/'build/test-base')+':'+str(r/'build/classes');src=r/'build/View24.java';src.write_text('import dev.zomboidmelee.boxing.*;public class View24{public static void main(String[]a){for(int kind:new int[]{0,1})for(double phase:new double[]{1,.10,.25,.46,.65})for(int side=0;side<2;side++){var q=First24.frame(kind,side,0,phase);System.out.println(kind+" "+phase+" "+side+" "+q.x()+" "+q.y()+" "+q.z()+" "+q.pitch()+" "+q.yaw()+" "+q.roll()+" "+q.wrist());}}}')
subprocess.run(['java','-jar',str(r.parent/'hordes-melee-interiors-patch/build/tools/ecj.jar'),'-21','-proc:none','-cp',cp,'-d',str(r/'build'),str(src)],check=True);rows=[list(map(float,s.split()))for s in subprocess.check_output(['java','-cp',str(r/'build')+':'+cp,'View24'],text=True).splitlines()]
W,H=500,330;faces=[(0,1,3,2),(4,6,7,5),(0,4,5,1),(2,3,7,6),(0,2,6,4),(1,5,7,3)]
def rot(v,axis,a):x,y,z=v;c=math.cos(math.radians(a));s=math.sin(math.radians(a));return (x,c*y-s*z,s*y+c*z)if axis==0 else(c*x+s*z,y,-s*x+c*z)if axis==1 else(c*x-s*y,s*x+c*y,z)
def project(v):x,y,z=v;depth=-z;return(W/2+x*H/.84/2/depth,H/2-y*H/.84/2/depth,depth)if depth>.02 else None

def box(lo,hi,fn,col,out):
 q=[project(fn((x/16,y/16,z/16)))for x in (lo[0],hi[0])for y in (lo[1],hi[1])for z in (lo[2],hi[2])]
 for i,f in enumerate(faces):
  if any(q[j]is None for j in f):continue
  out.append((sum(q[j][2]for j in f)/4,[q[j][:2]for j in f],tuple(int(c*(.76+i*.045))for c in col)))
images=[]
for n in range(0,len(rows),2):
 out=[];kind,phase=rows[n][:2]
 for row in rows[n:n+2]:
  _,_,side,ox,oy,oz,pitch,yaw,roll,wrist=row;side=int(side);cx=-1 if side==0 else 1
  def transform(v,hand=False):
   if hand:
    v=(v[0]-cx/16,v[1],v[2]);v=rot(v,1,wrist);v=(v[0]+cx/16,v[1]+4/16,v[2])
   else:v=(v[0],v[1]*2.5-6/16,v[2])
   v=rot(rot(rot(v,0,pitch),2,roll),1,yaw);return(ox+v[0]*.82,oy-v[1]*.82,oz+v[2]*.82)
  x=-3 if side==0 else -1;box((x,0,-2),(x+4,4,2),transform,(132,110,90),out);box((x,0,-2),(x+4,2,2),lambda v:transform(v,True),(184,151,122),out)
 im=Image.new('RGB',(W,H),(22,29,31));d=ImageDraw.Draw(im)
 for _,q,col in sorted(out,reverse=True,key=lambda x:x[0]):d.polygon(q,fill=col,outline=(28,26,25))
 d.line((W/2-5,H/2,W/2+5,H/2),fill=(180,180,180));d.line((W/2,H/2-5,W/2,H/2+5),fill=(180,180,180));d.text((12,12),f'Viewmodel geometry: {"jab"if kind==0 else"heavy"} {phase:.2f}',fill=(225,225,210));images.append(im)
sheet=Image.new('RGB',(W*5,H*2))
for i,im in enumerate(images):sheet.paste(im,((i%5)*W,(i//5)*H))
sheet.save(r/'assets/first-person-geometry.jpg')
