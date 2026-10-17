"""Geometric diagnostic from actual arm joints; not a Minecraft runtime screenshot."""
from pathlib import Path
import subprocess,math
from PIL import Image,ImageDraw
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools/ecj.jar';cp=str(r/'build/test-base')+':'+str(r/'build/classes');p=r/'build/GuardPreview.java';p.write_text('import dev.zomboidmelee.boxing.*;public class GuardPreview{public static void main(String[]a){for(double phase:new double[]{1,.25})for(int side=0;side<2;side++){var j=Arms23.joint(0,side,0,phase);System.out.println(phase+" "+side+" "+j.upper()+" "+j.elbow()+" "+j.yaw()+" "+j.roll());}}}')
subprocess.run(['java','-jar',str(t),'-21','-proc:none','-cp',cp,'-d',str(r/'build'),str(p)],check=True);rows=[list(map(float,s.split()))for s in subprocess.check_output(['java','-cp',str(r/'build')+':'+cp,'GuardPreview'],text=True).splitlines()]
W,H=440,520
faces=[(0,1,3,2),(4,6,7,5),(0,4,5,1),(2,3,7,6),(0,2,6,4),(1,5,7,3)]
def rot(v,axis,a):x,y,z=v;c=math.cos(a);s=math.sin(a);return (x,c*y-s*z,s*y+c*z)if axis==0 else(c*x+s*z,y,-s*x+c*z)if axis==1 else(c*x-s*y,s*x+c*y,z)
def project(v):x,y,z=rot(v,1,.33);return W/2+x*14,142+y*14,z

def cube(lo,hi,fn,color,out):
 q=[project(fn((x,y,z)))for x in (lo[0],hi[0])for y in (lo[1],hi[1])for z in (lo[2],hi[2])]
 for i,f in enumerate(faces):out.append((sum(q[j][2]for j in f)/4,[q[j][:2]for j in f],tuple(int(c*(.72+i*.05))for c in color)))
frames=[]
for pose in range(2):
 im=Image.new('RGB',(W,H),(23,28,29));d=ImageDraw.Draw(im);out=[];cube((-4,-8,-4),(4,0,4),lambda v:v,(186,156,129),out);cube((-4,0,-2),(4,12,2),lambda v:v,(69,78,68),out);cube((-4,12,-2),(0,24,2),lambda v:v,(63,72,79),out);cube((0,12,-2),(4,24,2),lambda v:v,(63,72,79),out)
 for _,side,u,e,y,z in rows[pose*2:pose*2+2]:
  side=int(side);origin=(-5 if side==0 else 5,2,0)
  def arm(v,lower=False):
   if lower:v=rot(v,0,e);v=(v[0],v[1]+4,v[2])
   v=rot(rot(rot(v,0,u),1,y),2,z);return tuple(v[i]+origin[i]for i in range(3))
  x=-3 if side==0 else -1;cube((x,-2,-2),(x+4,4,2),arm,(101,86,66),out);cube((x,0,-2),(x+4,6,2),lambda v:arm(v,True),(177,147,123),out)
 for _,points,col in sorted(out,reverse=True,key=lambda v:v[0]):d.polygon(points,fill=col,outline=(28,29,27))
 d.text((12,12),'Geometry: '+('combat guard'if pose==0 else'jab extension'),fill=(230,230,220));frames.append(im)
sheet=Image.new('RGB',(W*2,H));sheet.paste(frames[0],(0,0));sheet.paste(frames[1],(W,0));sheet.save(r/'assets/guard-geometry.png')
