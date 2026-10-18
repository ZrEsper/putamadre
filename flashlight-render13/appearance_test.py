from pathlib import Path
import json,math,sys
import moderngl,numpy as np
r=Path(__file__).resolve().parent
ctx=moderngl.create_standalone_context(backend='egl',require=320)
old='--old'in sys.argv
p=ctx.program(vertex_shader=(r/'shaders/light.vert').read_text(),fragment_shader=((r.parent/'flashlight-render9'if old else r)/'shaders/light.frag').read_text());vao=ctx.vertex_array(p,[])
w,h=240,180;yy,xx=np.mgrid[:h,:w];uv=np.stack([(xx+.5)/w*2-1,(yy+.5)/h*2-1],-1)
def norm(v):return v/np.linalg.norm(v)
origin=np.array([-.15,1.5,-.12]);forward=np.array([0,0,-1]);tan=math.tan(math.radians(20));checks=0;report={}
def render(cam,aim,ground=False,ortho=False,near=0,day=False,mapped=False,blocker=False):
 cam=np.array(cam,dtype=float);aim=np.array(aim,dtype=float);f=norm(aim-cam);right=norm(np.cross(f,[0,1,0]));up=np.cross(right,f);view=np.eye(4);view[:3,:3]=[right,up,-f]
 projection=np.zeros((4,4));zfar,znear=128,.05
 if ortho:projection=np.eye(4);projection[0,0]=1/8;projection[1,1]=1/6;projection[2,2]=-2/(zfar-znear);projection[2,3]=-(zfar+znear)/(zfar-znear)
 else:
  fov=1/math.tan(math.radians(35));projection[0,0]=fov/(w/h);projection[1,1]=fov;projection[2,2]=-(zfar+znear)/(zfar-znear);projection[2,3]=-2*zfar*znear/(zfar-znear);projection[3,2]=-1
 vp=projection@view;inv=np.linalg.inv(vp);ends=[]
 for z in [-1,1]:
  clip=np.concatenate([uv,np.full((h,w,1),z),np.ones((h,w,1))],-1);q=clip@inv.T;ends.append(q[:,:,:3]/q[:,:,3:]+cam)
 a,b=ends;ray=b-a;axis=1 if ground else 2;t=((0 if ground else -6)-a[:,:,axis])/ray[:,:,axis];points=a+ray*t[:,:,None];clip=np.concatenate([points-cam,np.ones((h,w,1))],-1)@vp.T;depth=(clip[:,:,2]/clip[:,:,3]*.5+.5).astype('f4');depth[(t<0)|(depth<0)|(depth>1)]=1
 checker=((np.floor(points[:,:,0]*5)+np.floor(points[:,:,2 if ground else 1]*5))%2)==0
 albedo=np.empty((h,w,4),dtype='f4');albedo[:]=(.24,.56,.13,1);albedo[checker,:3]*=.5
 scene=albedo.copy();scene[:,:,:3]*=.65 if day else .012
 shadows=np.full((24,12,1),25,dtype='f4')
 if mapped:
  for y in range(12):
   for x in range(12):
    ray=np.array([(2*(x+.5)/12-1)*tan*1.4,(2*(y+.5)/12-1)*tan*1.4,-1]);d=-origin[1]/ray[1] if ray[1]<0 else 25;shadows[y,x,0]=min(25,d)
 if blocker:shadows[:]=1
 textures=[]
 for i,(name,arr)in enumerate([('Scene',scene),('Depth',depth[:,:,None]),('Albedo',albedo),('Shadow',shadows),('AlbedoDepth',depth[:,:,None])]):
  tx=ctx.texture((w,h)if i!=3 else(12,24),arr.shape[-1],arr.tobytes(),dtype='f4');tx.filter=(moderngl.NEAREST,moderngl.NEAREST);tx.use(i);p[name].value=i;textures.append(tx)
 p['InverseViewProjection'].write(inv.astype('f4').T.tobytes());p['ViewProjection'].write(vp.astype('f4').T.tobytes());p['Count'].value=1
 for key,value in {'Origin':origin-cam,'Forward':forward,'ShadowOrigin':origin-cam,'ShadowForward':forward,'ShadowRight':(1,0,0),'ShadowUp':(0,1,0)}.items():p[key].write(np.array([value,(0,0,0)],dtype='f4').tobytes())
 p['Range'].write(np.array([25,25],dtype='f4').tobytes());p['HalfTan'].write(np.array([tan,tan],dtype='f4').tobytes())
 if 'NearFill'in p:p['NearFill'].write(np.array([near,0],dtype='f4').tobytes())
 target=ctx.texture((w,h),4,dtype='f4');fb=ctx.framebuffer([target]);fb.use();vao.render(vertices=3);out=np.frombuffer(fb.read(components=4,dtype='f4'),dtype='f4').reshape(h,w,4).copy()
 for tx in textures:tx.release()
 fb.release();target.release()
 def pixel(point):
  q=vp@np.append(np.array(point)-cam,1);q=q[:2]/q[3]*.5+.5;return int(q[1]*h),int(q[0]*w)
 return out,scene,points,pixel
# Day grass must preserve hue and texture instead of bleaching to white.
out,base,points,pixel=render([0,1.6,.1],[0,1.5,-6],day=True)
y,x=pixel([0,1.5,-6]);c=out[y,x,:3];assert c[1]>c[0]*1.8,('day grass lost its green texture',c);checks+=1
mask=(abs(points[:,:,0])<.12)&(abs(points[:,:,1]-1.5)<.12);ratios=out[:,:,1]/np.maximum(.00001,base[:,:,1]);assert np.max(ratios[mask])-np.min(ratios[mask])<.08,'texture contrast changed between checker squares';checks+=1
report['day_green_pixel']=c.tolist()
# External views illuminate ground 2 blocks ahead; first person main cone is unchanged.
for name,cam,ortho in [('third',[2.8,2.6,4.5],False),('isometric',[8,12,8],True)]:
 args=dict(cam=cam,aim=[0,0,-2],ground=True,ortho=ortho)
 out,base,points,pixel=render(**args,near=1);y,x=pixel([0,0,-2]);nearColor=out[y,x,:3]
 assert nearColor[1]>base[y,x,1]*5,(name,nearColor,base[y,x]);checks+=1
 inactive,*_=render(**args,near=0);assert inactive[y,x,1]<nearColor[1]*.25;checks+=1
 report[name+'_near_ground']=nearColor.tolist()
# Same planar ground must not cast PCF stripes on itself.
args=dict(cam=[2.8,2.6,4.5],aim=[0,0,-8],ground=True)
clear,base,points,pixel=render(**args);mapped,*_=render(**args,mapped=True)
mask=(points[:,:,2]<-5)&(points[:,:,2]>-12)&(abs(points[:,:,0])<.6)&(clear[:,:,1]>base[:,:,1]*2)
assert np.count_nonzero(mask)>30
ratio=mapped[:,:,1]/np.maximum(clear[:,:,1],1e-6);assert np.min(ratio[mask])>.90,('planar self-shadow bands',np.min(ratio[mask]));checks+=1
blocked,*_=render(**args,blocker=True);assert np.max(abs(blocked[mask]-clear[mask]))<1e-5,'stale coarse map must not darken a fully visible clear plane';checks+=1
report['ground_shadow_min_ratio']=float(np.min(ratio[mask]))
(r/'appearance-test-results.json').write_text(json.dumps(dict(checks=checks,real_glsl_render=True,results=report,minecraft_runtime_tested=False),indent=2)+'\n');print('DAY_TEXTURE_EXTERNAL_NEAR_GROUND_PLANAR_SHADOW_CHECKS='+str(checks))
