from pathlib import Path
import array,json,zipfile,moderngl
r=Path(__file__).resolve().parent;v=json.loads((r/'validation.json').read_text());ctx=moderngl.create_standalone_context(backend='egl',require=320)
vertex='''#version 150
out vec2 texCoord;out vec2 oneTexel;void main(){vec2 p[3]=vec2[3](vec2(-1,-1),vec2(3,-1),vec2(-1,3));gl_Position=vec4(p[gl_VertexID],0,1);texCoord=p[gl_VertexID]*.5+.5;oneTexel=vec2(1.0/32.0);}'''
samples=[(1.,0.,0.,251/255),(0.,1.,0.,251/255),(0.,0.,1.,251/255),(.7,.1,.5,251/255),(.4,.2,.1,1.),(.4,.2,.1,253/255)]
# RGBA8 input tests the exact stamp after framebuffer quantisation.
scene=ctx.texture((len(samples),1),4,bytes(round(x*255)for p in samples for x in p));scene.filter=(moderngl.NEAREST,moderngl.NEAREST);scene.use(0)
aux=ctx.texture((1,1),4,bytes([64,64,64,255]));aux.use(1)
rows={};checks=0
with zipfile.ZipFile(r/'output'/v['output'])as z:
 for n in v['changed_only']:
  p=ctx.program(vertex_shader=vertex,fragment_shader=z.read(n).decode());p['DiffuseSampler'].value=0
  for name in p:
   u=p[name]
   if name=='DiffuseSampler' or name.startswith('gl_'):continue
   try:
    if 'Sampler' in name:u.value=1
    elif name in ['InSize','OutSize']:u.value=(len(samples),1)
    elif name=='Time':u.value=0.
   except (AttributeError,TypeError):pass
  target=ctx.texture((len(samples),1),4,dtype='f4');fb=ctx.framebuffer([target]);fb.use();vao=ctx.vertex_array(p,[]);vao.render(vertices=3);out=array.array('f');out.frombytes(fb.read(components=4,dtype='f4'))
  for i in range(4):
   expected=[round(c*255)/255 for c in samples[i][:3]]+[1.]
   assert all(abs(out[i*4+k]-expected[k])<1e-5 for k in range(4)),(n,i,list(out[i*4:i*4+4]));checks+=1
  if 'hybrid' in n:
   wall=out[16:20];entity=out[20:24];assert wall[2]>wall[0] and entity[0]>entity[2]*3,(wall,entity);checks+=2
  rows[n]=list(out[:16]);vao.release();p.release();fb.release();target.release()
report={'actual_glsl_compiled_rendered':8,'rgba8_original_laser_colors_checks':checks,'renderer':ctx.info['GL_RENDERER'],'colors':rows,'minecraft_runtime_tested':False};(r/'test-results.json').write_text(json.dumps(report,indent=2)+'\n');print('ALL_EIGHT_NVG_REAL_GLSL_LASER_COLOR_CHECKS='+str(checks))
