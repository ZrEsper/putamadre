"""Original 64x64 Minecraft UV textures, built from pixel primitives (no borrowed skin artwork)."""
from pathlib import Path
from PIL import Image,ImageDraw
import random
r=Path(__file__).resolve().parent;out=r/'skins';out.mkdir(exist_ok=True)
skin=['#e6b58f','#b77d58','#81513a','#f0cfac','#d29b72','#5d3a2c'];hair=['#302820','#674330','#ad753c','#c9a665','#211f25','#686469'];shirts=['#344c47','#47536d','#865544','#705675','#a39d80','#c2c5bd','#4d6340','#33343d'];pants=['#384553','#4a4338','#373a36','#62645b']
# (x,y,width,height) of all six faces: top, bottom, side, front, other side, back.
def faces(d,boxes,color,rng):
 for x,y,w,h in boxes:
  d.rectangle((x,y,x+w-1,y+h-1),fill=color)
  for _ in range(max(1,w*h//12)):
   px=x+rng.randrange(w);py=y+rng.randrange(h);rgb=tuple(max(0,min(255,c+rng.choice([-8,7])))for c in Image.new('RGB',(1,1),color).getpixel((0,0)));d.point((px,py),fill=rgb)
HEAD=[(8,0,8,8),(16,0,8,8),(0,8,8,8),(8,8,8,8),(16,8,8,8),(24,8,8,8)]
BODY=[(20,16,8,4),(28,16,8,4),(16,20,4,12),(20,20,8,12),(28,20,4,12),(32,20,8,12)]
ARMS=[[(44,16,4,4),(48,16,4,4),(40,20,4,12),(44,20,4,12),(48,20,4,12),(52,20,4,12)],[(36,48,4,4),(40,48,4,4),(32,52,4,12),(36,52,4,12),(40,52,4,12),(44,52,4,12)]]
LEGS=[[(4,16,4,4),(8,16,4,4),(0,20,4,12),(4,20,4,12),(8,20,4,12),(12,20,4,12)],[(20,48,4,4),(24,48,4,4),(16,52,4,12),(20,52,4,12),(24,52,4,12),(28,52,4,12)]]
for index in range(48):
 rng=random.Random(2000+index);female=index%2==1;im=Image.new('RGBA',(64,64));d=ImageDraw.Draw(im);sc=skin[index//2%len(skin)];hc=hair[(index//3)%len(hair)];shirt=shirts[(index//2)%len(shirts)];trousers=pants[(index//5)%len(pants)]
 faces(d,HEAD,sc,rng);faces(d,BODY,shirt,rng)
 for boxes in ARMS:
  faces(d,boxes,shirt,rng)
  for x,y,w,h in boxes[2:]:d.rectangle((x,y+9,x+w-1,y+h-1),fill=sc)
 for boxes in LEGS:
  faces(d,boxes,trousers,rng)
  for x,y,w,h in boxes[2:]:d.rectangle((x,y+9,x+w-1,y+h-1),fill='#242724')
 # Hair cap, eyebrows, two eyes, nose and mouth. All faces stay at canonical UV positions.
 d.rectangle((8,0,15,7),fill=hc);d.rectangle((24,8,31,15),fill=hc)
 for x in [0,8,16]:d.rectangle((x,8,x+7,9 if female else 10),fill=hc)
 d.rectangle((8,10,8,14 if female else 11),fill=hc);d.rectangle((15,10,15,14 if female else 11),fill=hc)
 for x in [10,13]:d.point((x,11),fill='#e4e5d7');d.point((x,12),fill=['#3e5244','#394c63','#594534'][index%3])
 d.point((11,13),fill='#a66b54');d.line((11,14,12,14),fill='#9a514e' if female else '#754b3d')
 # Neckline, shirt seams, belt, pockets, and clothing variations (jackets, workwear, patterned shirts).
 d.rectangle((22,20,25,21),fill=sc);d.line((23,23,23,30),fill='#252c29');d.line((20,30,27,30),fill='#3b3028');d.point((24,30),fill='#b49a5c')
 if index%4==0:
  for y in [24,27]:d.line((20,y,27,y),fill='#777960')
 elif index%4==1:d.rectangle((20,24,22,26),outline='#b9a57f');d.rectangle((25,24,27,26),outline='#b9a57f')
 elif index%4==2:
  d.line((21,22,21,29),fill='#bc9a58');d.line((26,22,26,29),fill='#bc9a58')
 else:d.rectangle((21,25,22,27),fill='#c8c8b4')
 if female:
  # Long hair on the back of the head and shoulders, with an independent outer hair layer.
  d.rectangle((40,0,47,7),fill=hc);d.rectangle((56,8,63,15),fill=hc);d.rectangle((32,8,39,15),fill=hc);d.rectangle((48,8,55,15),fill=hc)
  d.rectangle((40,8,47,9),fill=hc);d.line((40,10,40,15),fill=hc);d.line((47,10,47,15),fill=hc);d.rectangle((32,20,39,23+(index%3)),fill=hc)
 elif index%6==0:
  d.rectangle((10,14,13,15),fill=hc);d.rectangle((9,13,9,15),fill=hc);d.rectangle((14,13,14,15),fill=hc)
 im.save(out/f'survivor_{index:02}.png')
print('Original survivor textures:',48)
