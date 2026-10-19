from pathlib import Path
import json,matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d.art3d import Poly3DCollection
r=Path(__file__).resolve().parent;pieces=json.loads((r/'build/anatomy.json').read_text());colors={1:'#922733',2:'#dededb',3:'#a95062',4:'#2f3031',5:'#eeeeea'}
fig=plt.figure(figsize=(11,10),facecolor='#22262a');grid=fig.add_gridspec(2,2,width_ratios=[1,1.1])
for slot,region,title in [(grid[:,0],None,'Silueta completa'),(grid[0,1],0,'Cráneo y mandíbula'),(grid[1,1],6,'Tres costillas por lado')]:
 ax=fig.add_subplot(slot,projection='3d',facecolor='#22262a');ax.set_proj_type('ortho')
 for p in pieces:
  if p['stage']!=4 or region is not None and p['region']!=region:continue
  part=p['region'];b={0:(-4,-8,-4,4,0,4),2:(4,0,-2,8,12,2),3:(-8,0,-2,-4,12,2),4:(-.1,12,-2,3.9,24,2),5:(-3.9,12,-2,.1,24,2),6:(-4,0,-2,4,12,2)}[part]
  x,y,z,X,Y,Z=p['box'];x,X=b[0]+x*(b[3]-b[0]),b[0]+X*(b[3]-b[0]);y,Y=24-(b[1]+Y*(b[4]-b[1])),24-(b[1]+y*(b[4]-b[1]));z,Z=b[2]+z*(b[5]-b[2]),b[2]+Z*(b[5]-b[2]);v=[(x,z,y),(X,z,y),(X,Z,y),(x,Z,y),(x,z,Y),(X,z,Y),(X,Z,Y),(x,Z,Y)]
  ax.add_collection3d(Poly3DCollection([[v[i]for i in q]for q in [(0,1,2,3),(4,7,6,5),(0,4,5,1),(2,6,7,3),(1,5,6,2),(0,3,7,4)]],facecolor=colors[p['material']],edgecolor='#969693',linewidth=.24))
 if region is None:ax.set(xlim=(-9,9),ylim=(-5,5),zlim=(0,33));ax.set_box_aspect((18,10,33))
 elif region==0:ax.set(xlim=(-4.2,4.2),ylim=(-4.2,4.2),zlim=(23.8,32.2));ax.set_box_aspect((1,1,1))
 else:ax.set(xlim=(-4.4,4.4),ylim=(-2.5,2.5),zlim=(11.5,24.5));ax.set_box_aspect((8.8,5,13))
 ax.view_init(9,-74);ax.axis('off');ax.set_title(title,color='white',fontsize=12)
fig.suptitle('Gore .18 · Esqueleto conservado; esta vista no muestra la piel\nVista técnica del modelo; no es una captura del juego',color='white',fontsize=14);fig.tight_layout();fig.savefig(r/'vista-capas.png',dpi=140,facecolor=fig.get_facecolor())
