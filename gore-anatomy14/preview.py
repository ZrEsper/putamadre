from pathlib import Path
import json
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d.art3d import Poly3DCollection
r=Path(__file__).resolve().parent;pieces=json.loads((r/'build/anatomy.json').read_text());colors={1:'#8d2831',2:'#ded1b0',3:'#ad6269',4:'#36232a',5:'#e7dcc4'}
fig=plt.figure(figsize=(12,6),facecolor='#192022')
for panel,region,title in [(1,0,'Cráneo y mandíbula'),(2,6,'Caja torácica y órganos'),(3,2,'Huesos, tendón y músculo')]:
 ax=fig.add_subplot(1,3,panel,projection='3d',facecolor='#192022');ax.set_proj_type('ortho')
 for p in pieces:
  if p['region']!=region:continue
  x,y,z,X,Y,Z=p['box']; y,Y=1-Y,1-y
  v=[(x,z,y),(X,z,y),(X,Z,y),(x,Z,y),(x,z,Y),(X,z,Y),(X,Z,Y),(x,Z,Y)]
  faces=[[v[i]for i in q]for q in [(0,1,2,3),(4,7,6,5),(0,4,5,1),(2,6,7,3),(1,5,6,2),(0,3,7,4)]]
  ax.add_collection3d(Poly3DCollection(faces,facecolor=colors[p['material']],edgecolor='#281c1c',linewidth=.17,alpha=1))
 ax.set(xlim=(0,1),ylim=(0,1),zlim=(0,1));ax.set_box_aspect((1,.6,1.4 if region!=0 else 1));ax.view_init(12,-68);ax.axis('off');ax.set_title(title,color='white',fontsize=12)
fig.suptitle('Gore .14 · Vista técnica de geometría interna (sin piel)\nNo es una captura de Minecraft',color='white',fontsize=14)
fig.tight_layout();fig.savefig(r/'anatomia-vista-tecnica.png',dpi=140,facecolor=fig.get_facecolor());plt.close(fig)
