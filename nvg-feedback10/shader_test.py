from pathlib import Path
import zipfile,json,numpy as np,moderngl
r=Path(__file__).resolve().parent;info=json.loads((r/'validation.json').read_text());ctx=moderngl.create_standalone_context(backend='egl',require=320);vertex='''#version 150
out vec2 texCoord;out vec2 oneTexel;void main(){vec2 p[3]=vec2[3](vec2(-1,-1),vec2(3,-1),vec2(-1,3));gl_Position=vec4(p[gl_VertexID],0,1);texCoord=p[gl_VertexID]*.5+.5;oneTexel=vec2(1./64.,1./32.);}''';w,h=64,32
scene=np.full((h,w,4),.1,dtype='f4');scene[:,:,3]=1;scene[:,8:16,3]=253/255;scene[:,16:24,:3]=.8;scene[:,16:24,3]=253/255
for i,color in enumerate([(1,0,0),(0,1,0),(0,0,1),(.7,.1,.5)]):scene[:,i,:3]=color;scene[:,i,3]=251/255
tex=ctx.texture((w,h),4,np.round(scene*255).astype('u1').tobytes());tex.filter=(moderngl.NEAREST,moderngl.NEAREST);tex.use(0);aux=ctx.texture((1,1),4,bytes([64,64,64,255]));aux.use(1);target=ctx.texture((w,h),4,dtype='f4');fb=ctx.framebuffer([target]);fb.use();n=0;rows={}
def render(frag,time):
 p=ctx.program(vertex_shader=vertex,fragment_shader=frag);p['DiffuseSampler'].value=0
 for u in config.get('uniforms',[]):
  if u['name']in p:
   try:p[u['name']].value=u['values'][0]if len(u['values'])==1 else tuple(u['values'])
   except (TypeError,ValueError):pass
 for name in p:
  try:
   if name=='Time':p[name].value=time
   elif name in ['InSize','OutSize']:p[name].value=(w,h)
   elif 'Sampler'in name and name!='DiffuseSampler':p[name].value=1
  except (TypeError,AttributeError):pass
 vao=ctx.vertex_array(p,[]);vao.render(vertices=3);out=np.frombuffer(fb.read(components=4,dtype='f4'),dtype='f4').reshape(h,w,4).copy();vao.release();p.release();return out
with zipfile.ZipFile(r/'output'/info['file'])as z:
 for name in info['filters']:
  config=json.loads(z.read(name.replace('.fsh','.json')));s=z.read(name).decode();plain=s.split('// Shared faint sensor grain')[0]+'\nvoid main(){visionMain10();}\n';a=render(s,.13);b=render(plain,.13);mask=(b[:,:,0]>=0)&(b[:,:,0]<=1);mask[:,:4]=False;delta=a[:,:,0]-b[:,:,0];assert np.count_nonzero(abs(delta[mask])>.0001)>8,name;assert np.max(abs(delta[mask]))<.009,name;n+=2
  for i in range(4):assert np.max(abs(a[h//2,i,:3]-np.round(scene[h//2,i,:3]*255)/255))<1e-5,name;n+=1
  assert np.all(np.isfinite(a));n+=1
  if 'hybrid'in name:
   dark=a[h//2,10,:3];bright=a[h//2,20,:3];assert bright[0]-dark[0]>.25,(dark,bright);assert dark[0]>dark[1]*1.4 and dark[0]>dark[2]*1.4;assert a[h//2,40,2]>a[h//2,40,0];n+=3;rows['hybrid_dark_texture']=dark.tolist();rows['hybrid_bright_texture']=bright.tolist()
  rows[name]={'interference_peak':float(np.max(abs(delta[mask])))}
report=dict(checks=n,eight_filters_compiled_and_rendered=True,renderer=ctx.info['GL_RENDERER'],results=rows,minecraft_runtime_tested=False);(r/'shader-test-results.json').write_text(json.dumps(report,indent=2)+'\n');print('EIGHT_NVG_REAL_GLSL_INTERFERENCE_TEXTURE_CONTRAST_RED_TINT_LASER_COLOR_CHECKS='+str(n))
