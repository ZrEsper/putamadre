from pathlib import Path
from PIL import Image,ImageDraw
root=Path(__file__).resolve().parent
out=root/'assets/mts/textures/guis';out.mkdir(parents=True,exist_ok=True)
colors=[(83,199,138,255),(231,190,72,255),(215,78,68,255),(116,126,132,255)]
def silhouette(kind,color):
 im=Image.new('RGBA',(180,240));d=ImageDraw.Draw(im);edge=(198,214,216,255)
 if kind=='helicopter':
  d.polygon([(80,129),(99,129),(95,211),(87,231),(82,211)],fill=color,outline=edge,width=2)
  d.rounded_rectangle((56,53,121,160),radius=29,fill=color,outline=edge,width=2)
  d.polygon([(63,66),(112,66),(116,96),(59,96)],fill=(25,42,48,255),outline=edge,width=2)
  d.line((36,83,36,174),fill=edge,width=5);d.line((141,83,141,174),fill=edge,width=5)
  d.line((44,4,139,202),fill=(90,107,113,255),width=8);d.line((169,56,10,139),fill=(90,107,113,255),width=8)
  d.ellipse((81,94,98,111),fill=(28,39,44,255),outline=edge,width=2)
  d.line((62,212,111,224),fill=edge,width=4)
 elif kind=='boat':
  d.polygon([(90,7),(119,40),(147,105),(143,213),(126,230),(53,230),(36,213),(32,105),(60,40)],fill=color,outline=edge,width=3)
  d.polygon([(90,30),(119,73),(132,118),(48,118),(61,73)],fill=(22,40,47,255),outline=edge,width=2)
  d.rounded_rectangle((48,126,132,209),radius=7,fill=(23,32,35,255),outline=edge,width=2)
  for y in [141,178]:d.rounded_rectangle((56,y,124,y+14),radius=4,fill=color,outline=edge,width=1)
  d.rectangle((78,215,102,239),fill=(64,74,78,255),outline=edge,width=2)
 elif kind=='motorcycle':
  for y in [25,180]:d.rounded_rectangle((73,y,108,y+53),radius=13,fill=(25,29,32,255),outline=edge,width=3)
  d.polygon([(82,73),(99,73),(117,132),(109,184),(73,184),(64,132)],fill=color,outline=edge,width=2)
  d.ellipse((71,91,110,129),fill=color,outline=edge,width=2)
  d.rounded_rectangle((75,135,108,181),radius=8,fill=(23,34,40,255),outline=edge,width=2)
  d.line((42,70,91,82,138,70),fill=edge,width=5)
  d.rectangle((60,152,67,213),fill=(125,139,147,255),outline=edge,width=1)
 elif kind=='plane':
  d.polygon([(90,6),(102,28),(104,81),(169,122),(169,143),(105,126),(101,201),(126,217),(126,229),(90,219),(55,229),(55,217),(79,201),(76,126),(9,143),(9,122),(75,81),(77,28)],fill=color,outline=edge,width=2)
  d.rounded_rectangle((79,48,102,84),radius=7,fill=(25,42,48,255),outline=edge,width=2)
  d.line((68,25,113,25),fill=edge,width=3)
 return im
for kind,accent in [('helicopter',(88,170,183)),('boat',(67,137,212)),('motorcycle',(203,156,78)),('plane',(135,159,215))]:
 im=Image.new('RGBA',(1024,512));d=ImageDraw.Draw(im)
 d.rounded_rectangle((0,0,619,369),radius=9,fill=(15,23,29,246),outline=accent,width=2)
 d.line((18,46,601,46),fill=accent,width=1);d.line((198,54,198,353),fill=(53,70,79),width=1)
 for row in range(8):d.rounded_rectangle((208,73+row*27,603,99+row*27),radius=4,fill=(24,36,44),outline=(46,63,72))
 for box in [(210,297,325,321),(335,297,450,321),(460,297,600,321),(210,331,400,355)]:d.rounded_rectangle(box,radius=4,fill=(30,48,59),outline=accent)
 for i,color in enumerate(colors):im.alpha_composite(silhouette(kind,color),(640+i%2*180,i//2*240))
 im.save(out/f'workshop_{kind}.png')
