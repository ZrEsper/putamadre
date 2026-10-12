from pathlib import Path
from PIL import Image,ImageDraw
import zipfile,io
root=Path(__file__).resolve().parent
with zipfile.ZipFile(root.parent/'mods/MTS-Official-Pack-1.21.1-V29-zomboid-workshop.5-step-limit.jar') as z:im=Image.open(io.BytesIO(z.read('assets/mtsofficialpack/textures/guis/zomboid_check.png'))).convert('RGBA')
p=im.load()
for y in range(im.height):
 for x in range(im.width):
  r,g,b,a=p[x,y]
  if g>r*1.5 and g>b*1.5 and g>120:p[x,y]=(76,166,128,a)
d=ImageDraw.Draw(im);d.rectangle((0,0,619,369),fill=(16,22,26,244));d.rounded_rectangle((0,0,619,369),radius=9,outline=(89,114,125),width=2);d.line((18,48,600,48),fill=(89,114,125));d.line((198,56,198,323),fill=(55,71,78));d.line((420,56,420,323),fill=(55,71,78))
# Long sedan viewed from above; its coloured overlays follow the same transformed coordinates.
outline=[(72,57),(101,57),(116,65),(126,88),(130,114),(130,215),(126,240),(113,252),(61,252),(48,239),(43,215),(43,114),(48,88),(58,65)]
d.polygon(outline,fill=(28,40,45));d.line(outline+[outline[0]],fill=(165,187,191),width=2)
d.line((64,62,108,62),fill=(83,105,115),width=3);d.line((62,243,112,243),fill=(83,105,115),width=3)
# Hood crease, greenhouse, rear deck and inset front/rear seats.
d.line((58,78,58,102),fill=(103,135,136));d.line((116,78,116,102),fill=(103,135,136))
d.rounded_rectangle((63,136,80,162),radius=5,fill=(41,54,60),outline=(82,102,111));d.rounded_rectangle((92,136,109,162),radius=5,fill=(41,54,60),outline=(82,102,111));d.rounded_rectangle((62,180,109,199),radius=4,fill=(41,54,60),outline=(82,102,111))
# Mirrors, lamp clusters and tyre details.
for box in [(38,119,44,129),(130,119,136,129)]:d.rounded_rectangle(box,radius=2,fill=(89,111,119),outline=(172,190,193))
for box in [(56,66,69,72),(104,66,117,72)]:d.rounded_rectangle(box,radius=2,fill=(178,197,194))
for box in [(53,230,63,235),(109,230,119,235)]:d.rounded_rectangle(box,radius=2,fill=(134,76,72))
for x,y in [(43,120),(123,120),(43,212),(123,212)]:
 d.rounded_rectangle((x,y,x+9,y+23),radius=3,fill=(9,15,18),outline=(117,135,143))
 for yy in range(y+5,y+20,4):d.line((x+2,yy,x+7,yy),fill=(66,79,85))
d.rounded_rectangle((15,295,187,311),radius=3,fill=(9,14,18),outline=(58,83,92));d.rectangle((0,480,7,487),fill=(92,169,211,255))
im.save(root/'assets/mts/textures/guis/workshop_car.png')
