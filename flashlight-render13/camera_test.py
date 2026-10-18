from pathlib import Path
import numpy as np,moderngl,json,math,sys
r=Path(__file__).resolve().parent;ctx=moderngl.create_standalone_context(backend='egl',require=320);frag=(r.parent/'flashlight-render8/shaders/light.frag'if'--old'in sys.argv else r/'shaders/light.frag').read_text();program=ctx.program(vertex_shader=(r/'shaders/light.vert').read_text(),fragment_shader=frag);vao=ctx.vertex_array(program,[]);w,h=160,120;n=0;reports={}
def norm(v):return v/np.linalg.norm(v)
def perspective():
 f=1/math.tan(math.radians(35));p=np.zeros((4,4));p[0,0]=f/(w/h);p[1,1]=f;p[2,2]=-(128+.05)/(128-.05);p[2,3]=-2*128*.05/(128-.05);p[3,2]=-1;return p
def ortho():
 p=np.eye(4);p[0,0]=1/8;p[1,1]=1/6;p[2,2]=-2/(128-.05);p[2,3]=-(128+.05)/(128-.05);return p
origin=np.array([-.15,1.5,-.12]);forward=np.array([0,0,-1]);worldTarget=np.array([0,1.5,-6]);yy,xx=np.mgrid[0:h,0:w];ndc=np.stack([(xx+.5)/w*2-1,(yy+.5)/h*2-1,np.zeros((h,w)),np.ones((h,w))],axis=-1)
for name,cam,projection in [('first',np.array([0,1.6,.1]),perspective()),('third',np.array([2.8,2.6,4.5]),perspective()),('isometric',np.array([8,12,8]),ortho())]:
 f=norm(worldTarget-cam);right=norm(np.cross(f,np.array([0,1,0])));up=np.cross(right,f);view=np.eye(4);view[:3,:3]=np.stack([right,up,-f]);vp=projection@view;inv=np.linalg.inv(vp)
 rays=[]
 for z in [-1,1]:
  clip=ndc.copy();clip[:,:,2]=z;q=clip@inv.T;rays.append(q[:,:,:3]/q[:,:,3:4]+cam)
 a,b=rays;direction=b-a
 def plane(z):
  t=(z-a[:,:,2])/direction[:,:,2];point=a+direction*t[:,:,None];clip=np.concatenate([point-cam,np.ones((h,w,1))],axis=-1)@vp.T;d=(clip[:,:,2]/clip[:,:,3]*.5+.5).astype('f4');return point,d
 wall,wallDepth=plane(-6);entity,entityDepth=plane(-4);entityMask=(abs(entity[:,:,0])<.45)&(entity[:,:,1]>.7)&(entity[:,:,1]<2.2);mainDepth=np.where(entityMask,entityDepth,wallDepth)
 scene=np.full((h,w,4),.006,dtype='f4');scene[:,:,3]=1;scene[entityMask,:3]=(.006,.03,.008)
 albedo=np.empty((h,w,4),dtype='f4');albedo[:]=(.7,.14,.08,1);shadow=np.full((24,12,1),25,dtype='f4')
 arrays=[('Scene',scene),('Depth',mainDepth[:,:,None]),('Albedo',albedo),('Shadow',shadow),('AlbedoDepth',wallDepth[:,:,None])];textures=[]
 for unit,(sampler,array)in enumerate(arrays):
  texture=ctx.texture((array.shape[1],array.shape[0]),array.shape[2],array.tobytes(),dtype='f4');texture.filter=(moderngl.NEAREST,moderngl.NEAREST);texture.use(unit);textures.append(texture)
  if sampler in program:program[sampler].value=unit
 program['InverseViewProjection'].write(inv.astype('f4').T.tobytes());program['ViewProjection'].write(vp.astype('f4').T.tobytes());program['Count'].value=1
 for key,value in {'Origin':origin-cam,'Forward':forward,'ShadowOrigin':origin-cam,'ShadowForward':forward,'ShadowRight':(1,0,0),'ShadowUp':(0,1,0)}.items():program[key].write(np.array([value,(0,0,0)],dtype='f4').tobytes())
 program['Range'].write(np.array([25,25],dtype='f4').tobytes());program['HalfTan'].write(np.array([math.tan(math.radians(20))]*2,dtype='f4').tobytes())
 target=ctx.texture((w,h),4,dtype='f4');fb=ctx.framebuffer([target]);fb.use();vao.render(vertices=3);output=np.frombuffer(fb.read(components=4,dtype='f4'),dtype='f4').reshape(h,w,4).copy()
 def pixel(point):
  clip=vp@np.append(point-cam,1);q=clip[:2]/clip[3]*.5+.5;return min(h-1,max(0,int(q[1]*h))),min(w-1,max(0,int(q[0]*w)))
 ep=pixel(np.array([0,1.5,-4]));entityColor=output[ep][:3];assert entityColor[1]>entityColor[0]*2,'entity must keep its own green pixels, never receive red wall texture';assert entityColor[1]>.12,(name,entityColor);n+=2
 wp=pixel(np.array([.8,1.5,-6]));wallColor=output[wp][:3];assert wallColor[0]>.20,(name,wallColor);n+=1
 corner=output[2,2];assert np.all(np.isfinite(output));n+=1
 reports[name]={'entity_original_texture':entityColor.tolist(),'wall_in_cone':wallColor.tolist(),'projection':'orthographic'if name=='isometric'else'perspective'}
 for texture in textures:texture.release()
 fb.release();target.release()
(r/'camera-test-results.json').write_text(json.dumps(dict(checks=n,real_glsl_render=True,cases=reports,minecraft_runtime_tested=False),indent=2)+'\n');print('FIRST_THIRD_ISOMETRIC_REAL_GLSL_DEPTH_MATCH_ENTITY_TEXTURE_LIGHTING_CHECKS='+str(n))
