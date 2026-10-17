"""Geometry diagnostic of the actual motion track, not a Minecraft screenshot."""
from pathlib import Path
import math,json,subprocess
from PIL import Image,ImageDraw
r=Path(__file__).resolve().parent
java=r/'build/Preview21.java';java.write_text('import dev.zomboidmelee.boxing.*;public class Preview21{public static void main(String[]a){for(int i=0;i<=120;i++){double p=i/120d;var q=StompMotion21.pose(p,.7,.2,.08,1.6);System.out.printf(java.util.Locale.ROOT,"%.6f %.6f %.6f %.6f %.6f %.6f %.6f%n",p,q.hipX(),q.hipY(),q.hipZ(),q.thigh(),q.knee(),q.ankle());}}}')
subprocess.run(['java','-jar',str(r.parent/'hordes-melee-interiors-patch/build/tools/ecj.jar'),'-21','-proc:none','-cp',str(r/'build/classes'),'-d',str(r/'build'),str(java)],check=True)
rows=subprocess.check_output(['java','-cp',str(r/'build')+':'+str(r/'build/classes'),'Preview21'],text=True).splitlines()
W,H=480,360
faces=[(0,1,3,2),(4,6,7,5),(0,4,5,1),(2,3,7,6),(0,2,6,4),(1,5,7,3)]
def rx(v,a):x,y,z=v;c=math.cos(a);s=math.sin(a);return x,c*y-s*z,s*y+c*z
def world(v,origin,a):x,y,z=rx(v,math.radians(a));return origin[0]+x,origin[1]-y,origin[2]-z
def project(v):x,y,z=rx(v,math.radians(60));depth=-z;return (W/2+x*270/depth,H/2-y*270/depth,depth)if depth>.06 else None
def box(origin,a,lo,hi,color,out):
 pts=[world((x/16,y/16,z/16),origin,a)for x in (lo[0],hi[0])for y in (lo[1],hi[1])for z in (lo[2],hi[2])];q=[project(v)for v in pts]
 for i,f in enumerate(faces):
  if any(q[j]is None for j in f):continue
  c=tuple(max(0,min(255,int(v*(.73+i*.055))))for v in color);out.append((sum(q[j][2]for j in f)/4,[q[j][:2]for j in f],c))
def frame(row):
 p,x,y,z,t,k,a=map(float,row.split());im=Image.new('RGB',(W,H),(21,27,30));d=ImageDraw.Draw(im)
 for n in range(-8,9):
  for line in [[(n*.25,-1.6,-.05),(n*.25,-1.6,-4)],[(-2,-1.6,-.2-n*.25),(2,-1.6,-.2-n*.25)]]:
   q=[project(v)for v in line]
   if all(q):d.line([v[:2]for v in q],fill=(38,48,50))
 out=[];hip=(x,y,z);knee=world((0,7/16,0),hip,t);ankle=world((0,5/16,0),knee,t+k)
 box(hip,t,(-2,0,-2),(2,7,2),(77,88,70),out);box(knee,t+k,(-2,0,-2),(2,5,2),(75,85,69),out)
 box(ankle,a,(-2.3,0,-3.5),(2.3,2.8,2.3),(56,63,55),out);box(ankle,a,(-2,.25,-4.3),(2,2.7,-3.3),(65,69,59),out);box(ankle,a,(-2.45,2.8,-4.4),(2.45,3.7,2.5),(26,28,28),out)
 for i in range(5):box(ankle,a,(-2.2,3.7,-4.1+i*1.3),(2.2,3.9,-3.5+i*1.3),(66,67,61),out)
 for _,q,c in sorted(out,reverse=True,key=lambda v:v[0]):d.polygon(q,fill=c,outline=(18,22,21))
 d.line((W/2-4,H/2,W/2+4,H/2),fill=(150,150,150));d.text((12,12),f'Geometry preview - phase {p:.2f}',fill=(210,215,210));return im
frames=[frame(row)for row in rows];frames[0].save(r/'assets/stomp-geometry.gif',save_all=True,append_images=frames[1:],duration=8,loop=0)
indices=[0,14,34,46,62,70,90,120];sheet=Image.new('RGB',(W*4,H*2))
for n,i in enumerate(indices):sheet.paste(frames[i],((n%4)*W,(n//4)*H))
sheet.save(r/'assets/stomp-geometry.jpg')
