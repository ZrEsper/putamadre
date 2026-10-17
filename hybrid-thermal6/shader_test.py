from pathlib import Path
import moderngl,array,json,zipfile,math
r=Path(__file__).resolve().parent;d=json.loads((r/'validation.json').read_text());ctx=moderngl.create_standalone_context(backend='egl',require=320)
vertex='''#version 150
out vec2 texCoord;
void main(){vec2 v[3]=vec2[3](vec2(-1,-1),vec2(3,-1),vec2(-1,3));gl_Position=vec4(v[gl_VertexID],0,1);texCoord=v[gl_VertexID]*0.5+0.5;}'''
with zipfile.ZipFile(r/'output'/d['output'])as z:fragment=z.read('assets/fracturepoint/shaders/program/hybrid-thermal-vision.fsh').decode()
program=ctx.program(vertex_shader=vertex,fragment_shader=fragment);program['DiffuseSampler'].value=0;vao=ctx.vertex_array(program,[])
# Quantized RGBA8-equivalent marker values cover dark/bright directional shading.
samples=[('black',(0,0,0)),('wall',(.3,.3,.3)),('sky',(.9,.9,.9)),('brick',(.8,.1,.05)),('redstone',(.9,0,0)),('magenta',(1,0,1))]
for light in [.05,.1,.25,.5,.75,1.0]:samples.append(('entity_'+str(light),tuple(round(v*light)/255 for v in (239,1,137))))
size=(len(samples),1);pixels=array.array('f',[v for name,p in samples for v in (*p,1)]);scene=ctx.texture(size,4,pixels.tobytes(),dtype='f4');scene.filter=(moderngl.NEAREST,moderngl.NEAREST);scene.use(0);target=ctx.texture(size,4,dtype='f4');fb=ctx.framebuffer([target]);fb.use();vao.render(vertices=3);result=array.array('f');result.frombytes(fb.read(components=4,dtype='f4'));rows={name:list(result[i*4:i*4+3])for i,(name,p)in enumerate(samples)}
for name in ['black','wall','sky','brick','redstone','magenta']:
 red,green,blue=rows[name];assert blue>green>red,(name,rows[name])
for name in rows:
 if name.startswith('entity_'):
  red,green,blue=rows[name];assert red>.55 and red>green*5 and red>blue*5,(name,rows[name])
assert rows['black'][2]<rows['wall'][2]<rows['sky'][2]<.8
assert all(math.isfinite(v)for rgb in rows.values()for v in rgb)
report={'renderer':ctx.info['GL_RENDERER'],'actual_glsl_compiled_and_rendered':True,'checks':len(rows)+2,'colors':rows,'minecraft_runtime_tested':False};(r/'shader-test-results.json').write_text(json.dumps(report,indent=2)+'\n');print('HYBRID_REAL_GLSL_COLOR_CHECKS='+str(report['checks']))
