from PIL import Image,ImageDraw
from pathlib import Path
W,H=256,448
paths=[[(17,0),(21,0),(24,3),(24,8),(22,11),(19,12),(15,10),(13,7),(14,3)],[(15,11),(22,11),(23,14),(27,15),(26,24),(24,30),(25,34),(22,38),(14,38),(11,34),(12,30),(10,24),(10,15),(14,14)],[(27,15),(30,17),(31,24),(32,29),(33,35),(31,37),(29,35),(28,29),(27,25),(25,19)],[(9,15),(11,19),(9,25),(8,29),(7,35),(5,37),(3,35),(4,29),(5,24),(6,17)],[(19,37),(25,35),(25,44),(24,49),(25,56),(23,59),(20,57),(20,49),(18,43)],[(11,35),(17,37),(18,43),(16,49),(16,57),(13,59),(11,56),(12,49),(11,44)],[(20,57),(24,57),(25,60),(28,61),(28,63),(22,64),(19,62)],[(12,57),(16,57),(17,62),(14,64),(8,63),(8,61),(11,60)]]
atlas=Image.new('RGBA',(W*4,H*2));preview=Image.new('RGBA',(W,H),(18,23,21,255))
for i,path in enumerate(paths):
 im=Image.new('RGBA',(W*2,H*2));d=ImageDraw.Draw(im);p=[(round(x/36*W*2),round(y/64*H*2)) for x,y in path];d.polygon(p,fill=(198,199,191,255));d.line(p+[p[0]],fill=(99,111,104,255),width=5,joint='curve')
 if i==0:
  im=Image.new('RGBA',(W*2,H*2));d=ImageDraw.Draw(im);d.ellipse((13/36*W*2,0,24/36*W*2,11/64*H*2),fill=(198,199,191,255),outline=(99,111,104,255),width=5)
 if i in (2,3):
  shoulder=(27 if i==2 else 9);d.ellipse(((shoulder-2.3)/36*W*2,14/64*H*2,(shoulder+2.3)/36*W*2,20/64*H*2),fill=(198,199,191,255),outline=(99,111,104,255),width=3)
 # Internal anatomical contours: ribcage, face, articulations.
 def line(coords):d.line([(int(x/36*W*2),int(y/64*H*2)) for x,y in coords],fill=(130,143,135,230),width=3,joint='curve')
 if i==0:
  line([(16,5),(18,5)]);line([(20,5),(22,5)]);line([(19,5),(18,8),(20,8)]);line([(17,9),(21,9)])
 if i==1:
  line([(18,15),(18,31)]);line([(11,19),(15,18),(18,20),(22,18),(26,19)])
  for y in (22,25,28):line([(12,y),(16,y+1),(18,y),(20,y+1),(24,y)])
  line([(13,33),(18,35),(24,33)])
 if i in (2,3):line([(path[1][0],23),(path[2][0]-.8,27),(path[4][0]-1,33)])
 if i in (4,5):line([(path[0][0]+1,39),(path[0][0]+2,46),(path[0][0]+2,53)])
 im=im.resize((W,H),Image.Resampling.LANCZOS);atlas.alpha_composite(im,((i%4)*W,(i//4)*H));preview.alpha_composite(im)
r=Path(__file__).parent/'resources/assets/survivorcreator/textures/gui';r.mkdir(parents=True,exist_ok=True);atlas.save(r/'autopsy_body.png');preview.save('/workspace/autopsy-preview.png')
