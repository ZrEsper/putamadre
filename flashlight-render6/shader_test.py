from pathlib import Path
import moderngl,numpy as np,array,json,zipfile,math
r=Path(__file__).resolve().parent;ctx=moderngl.create_standalone_context(backend='egl',require=320);p=ctx.program(vertex_shader=(r/'shaders/light.vert').read_text(),fragment_shader=(r/'shaders/light.frag').read_text());vao=ctx.vertex_array(p,[])
w,h=96,64;f=1/math.tan(math.radians(35));proj=np.zeros((4,4),dtype='f4');proj[0,0]=f/(w/h);proj[1,1]=f;proj[2,2]=-(1024+.05)/(1024-.05);proj[2,3]=-2*1024*.05/(1024-.05);proj[3,2]=-1;p['InverseViewProjection'].write(np.linalg.inv(proj).astype('f4').T.tobytes());p['Count'].value=1
for name,v in {'Origin':(0,0,0),'Forward':(0,0,-1),'ShadowOrigin':(0,0,0),'ShadowForward':(0,0,-1),'ShadowRight':(1,0,0),'ShadowUp':(0,1,0)}.items():p[name].write(np.array([v,(0,0,0)],dtype='f4').tobytes())
p['Range'].write(np.array([25,25],dtype='f4').tobytes());p['HalfTan'].write(np.array([math.tan(math.radians(20))]*2,dtype='f4').tobytes())
scene=np.zeros((h,w,4),dtype='f4');scene[:,:,:3]=.001;scene[:,:,3]=1
albedo=np.ones((h,w,4),dtype='f4');albedo[:,:,:3]=(.65,.4,.2)
for y in range(h):
 for x in range(w):
  if(x//4+y//4)%2:albedo[y,x,:3]*=.5
z=-5;point=proj@np.array([0,0,z,1]);depth=np.full((h,w,1),point[2]/point[3]*.5+.5,dtype='f4');shadow=np.full((24,12,1),25,dtype='f4')
textures=[]
for i,(name,a)in enumerate([('Scene',scene),('Depth',depth),('Albedo',albedo),('Shadow',shadow)]):
 t=ctx.texture((a.shape[1],a.shape[0]),a.shape[2],a.tobytes(),dtype='f4');t.filter=(moderngl.NEAREST,moderngl.NEAREST);t.use(i);p[name].value=i;textures.append(t)
target=ctx.texture((w,h),4,dtype='f4');fb=ctx.framebuffer([target]);fb.use()
def render():vao.render(vertices=3);return np.frombuffer(fb.read(components=4,dtype='f4'),dtype='f4').reshape(h,w,4).copy()
checks=0
lit=render();centre=lit[h//2,w//2];edge=lit[h//2,2];assert centre[0]>.3 and centre[0]>centre[1]>centre[2];assert np.max(abs(edge-scene[h//2,2]))<1e-6;assert abs(centre[3]-250/255)<1e-6;checks+=3
assert lit[h//2,w//2+4,0]<centre[0]*.7;checks+=1
p['Range'].write(np.array([2,2],dtype='f4').tobytes());out_of_range=render();assert np.max(abs(out_of_range-scene))<1e-6;checks+=1;p['Range'].write(np.array([25,25],dtype='f4').tobytes())
p['Origin'].write(np.array([(2,0,0),(0,0,0)],dtype='f4').tobytes());offset=render();assert offset[h//2,66,0]>.2 and offset[h//2,w//2,0]<.01;checks+=1;p['Origin'].write(np.zeros((2,3),dtype='f4').tobytes())
# Blocker shadow depth at 2 metres must stop a surface at 5 metres.
textures[3].write(np.full_like(shadow,2).tobytes());blocked=render();assert np.max(abs(blocked-scene))<1e-6;checks+=1
textures[3].write(shadow.tobytes());p['Count'].value=0;off=render();assert np.max(abs(off-scene))<1e-6;checks+=1;p['Count'].value=1
# Sky is never illuminated as if it were a nearby wall.
textures[1].write(np.ones_like(depth).tobytes());sky=render();assert np.max(abs(sky-scene))<1e-6;checks+=1;textures[1].write(depth.tobytes())
# Preserve independent laser and entity-thermal stamps.
special=scene.copy();special[h//2,w//2]=(.1,0,0,251/255);special[h//2,w//2+1]=(.1,.1,.1,253/255);textures[0].write(special.tobytes());s=render();assert np.max(abs(s[h//2,w//2]-special[h//2,w//2]))<1e-6;assert abs(s[h//2,w//2+1,3]-253/255)<1e-6;checks+=2
# Actual Darkness .4 shader after flashlight pass: lit colour survives; outside remains dark.
meta=json.loads((r.parent/'darkness-light4/validation.json').read_text())
with zipfile.ZipFile(r.parent/'darkness-light4/output'/meta['file'])as j:frag=j.read(meta['only_changed'][0]).decode()
dark=ctx.program(vertex_shader=(r/'shaders/light.vert').read_text(),fragment_shader=frag);dark['DiffuseSampler'].value=0
for k,v in {'DarknessStrength':1.,'DarknessFloor':.018,'KeepStart':.06,'KeepEnd':.34,'DaylightPreserve':0.}.items():dark[k].value=v
input0=ctx.texture((w,h),4,lit.tobytes(),dtype='f4');input0.use(0);dvao=ctx.vertex_array(dark,[]);dvao.render(vertices=3);result=np.frombuffer(fb.read(components=4,dtype='f4'),dtype='f4').reshape(h,w,4);assert np.max(abs(result[h//2,w//2]-lit[h//2,w//2]))<1e-6;assert result[h//2,2,0]<lit[h//2,2,0]*.01;checks+=2
assert np.all(np.isfinite(lit));checks+=1
report={'checks':checks,'real_opengl_compiled_and_rendered':True,'centre_lit':centre.tolist(),'outside':edge.tolist(),'wall_shadow':blocked[h//2,w//2].tolist(),'darkness_compat_centre':result[h//2,w//2].tolist(),'renderer':ctx.info['GL_RENDERER'],'minecraft_runtime_tested':False};(r/'shader-test-results.json').write_text(json.dumps(report,indent=2)+'\n');print('REAL_CONE_TEXTURE_SHADOW_RANGE_SKY_TAG_DARKNESS_SHADER_CHECKS='+str(checks))
