from pathlib import Path
import json,matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d.art3d import Poly3DCollection
r=Path(__file__).resolve().parent;pieces=json.loads((r/'build/anatomy.json').read_text());colors={1:'#922733',2:'#d5d1c6',3:'#a95062',4:'#261820',5:'#e6e2d8'}
fig=plt.figure(figsize=(12,8),facecolor='#192022')
for panel,stage,title in [(1,1,'Músculo bajo la piel'),(2,3,'Órganos expuestos'),(3,4,'Esqueleto'),(4,6,'Huesos destruidos')]:
 ax=fig.add_subplot(1,4,panel,projection='3d',facecolor='#192022');ax.set_proj_type('ortho')
 for p in pieces:
  if p['stage']!=stage:continue
  region=p['region'];b={0:(-4,-8,-4,4,0,4),2:(4,0,-2,8,12,2),3:(-8,0,-2,-4,12,2),4:(-.1,12,-2,3.9,24,2),5:(-3.9,12,-2,.1,24,2),6:(-4,0,-2,4,12,2)}[region]
  x,y,z,X,Y,Z=p['box'];x,X=b[0]+x*(b[3]-b[0]),b[0]+X*(b[3]-b[0]);y,Y=24-(b[1]+Y*(b[4]-b[1])),24-(b[1]+y*(b[4]-b[1]));z,Z=b[2]+z*(b[5]-b[2]),b[2]+Z*(b[5]-b[2]);v=[(x,z,y),(X,z,y),(X,Z,y),(x,Z,y),(x,z,Y),(X,z,Y),(X,Z,Y),(x,Z,Y)]
  ax.add_collection3d(Poly3DCollection([[v[i]for i in q]for q in [(0,1,2,3),(4,7,6,5),(0,4,5,1),(2,6,7,3),(1,5,6,2),(0,3,7,4)]],facecolor=colors[p['material']],edgecolor='#44413e',linewidth=.1))
 ax.set(xlim=(-9,9),ylim=(-6,6),zlim=(0,33));ax.set_box_aspect((18,12,33));ax.view_init(7,-80);ax.axis('off');ax.set_title(title,color='white',fontsize=10)
fig.suptitle('Gore .16 · Geometría interna por capas\nVista técnica sin piel; no es una captura de Minecraft',color='white');fig.tight_layout();fig.savefig(r/'vista-capas.png',dpi=140,facecolor=fig.get_facecolor())
