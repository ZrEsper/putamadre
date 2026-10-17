from pathlib import Path
import moderngl,array,json,zipfile,math
r=Path(__file__).resolve().parent;d=json.loads((r/'validation.json').read_text());ctx=moderngl.create_standalone_context(backend='egl',require=320)
vertex='''#version 150
out vec2 texCoord;
void main(){vec2 v[3]=vec2[3](vec2(-1,-1),vec2(3,-1),vec2(-1,3));gl_Position=vec4(v[gl_VertexID],0,1);texCoord=v[gl_VertexID]*0.5+0.5;}'''
with zipfile.ZipFile(r/'output'/d['output'])as z:fragment=z.read('assets/fracturepoint/shaders/program/hybrid-thermal-vision.fsh').decode()
program=ctx.program(vertex_shader=vertex,fragment_shader=fragment);program['DiffuseSampler'].value=0;vao=ctx.vertex_array(program,[])
# Explicit alpha stamp: material RGB must never select the heat mask.
samples=[('black',(0,0,0,1)),('wall',(.3,.3,.3,1)),('sky',(.9,.9,.9,1)),('brick',(.8,.1,.05,1)),('redstone',(.9,0,0,1)),('magenta',(1,0,1,1)),('old_marker_wall',(239/255,1/255,137/255,1)),('old_false_brick',(.45,.025,.27,1)),('alpha252_wall',(.4,.2,.1,252/255)),('alpha254_wall',(.4,.2,.1,254/255)),('transparent_sky',(0,0,0,0))]
for light in [.05,.1,.25,.5,.75,1.0]:samples.append(('entity_'+str(light),(light,light,light,253/255)))
for name,color in [('red',(1,0,0)),('blue',(0,0,1)),('green',(0,1,0)),('black',(0,0,0))]:samples.append(('entity_'+name,(*color,253/255)))
size=(len(samples),1);program['OutSize'].value=size;program['Time'].value=0.0;pixels=array.array('f',[v for name,p in samples for v in p]);scene=ctx.texture(size,4,pixels.tobytes(),dtype='f4');scene.filter=(moderngl.NEAREST,moderngl.NEAREST);scene.use(0);target=ctx.texture(size,4,dtype='f4');fb=ctx.framebuffer([target]);fb.use();vao.render(vertices=3);result=array.array('f');result.frombytes(fb.read(components=4,dtype='f4'));rows={name:list(result[i*4:i*4+3])for i,(name,p)in enumerate(samples)}
for name,_ in samples:
 if name.startswith('entity_'):continue
 red,green,blue=rows[name];assert blue>green>red,(name,rows[name])
for name in rows:
 if name.startswith('entity_'):
  red,green,blue=rows[name];assert red>.55 and red>green*3 and red>blue*3,(name,rows[name])
assert rows['black'][2]<rows['wall'][2]<rows['sky'][2]<.8
assert all(math.isfinite(v)for rgb in rows.values()for v in rgb)
# Temporal noise must be visible only as very subtle grain, never a bright flash.
base=list(result);program['Time'].value=.135;vao.render(vertices=3);changed=array.array('f');changed.frombytes(fb.read(components=4,dtype='f4'));delta=max(abs(a-b) for a,b in zip(base,changed));assert 0.0001<delta<.012,delta
report={'temporal_max_delta':delta,'renderer':ctx.info['GL_RENDERER'],'actual_glsl_compiled_and_rendered':True,'checks':len(rows)+3,'colors':rows,'minecraft_runtime_tested':False};(r/'shader-test-results.json').write_text(json.dumps(report,indent=2)+'\n');print('HYBRID_REAL_GLSL_COLOR_CHECKS='+str(report['checks']))
# Exercise an actual RGBA8 framebuffer: opaque terrain, explicit entity stamp,
# and ordinary depth testing. Identical RGB is cold on walls, hot on entities.
geom=ctx.program(vertex_shader='''#version 150
in vec3 Position;void main(){gl_Position=vec4(Position,1);}''',fragment_shader='''#version 150
uniform vec4 Color;out vec4 fragColor;void main(){fragColor=Color;}''')
size2=(64,32);scene2=ctx.texture(size2,4,dtype='f1');depth=ctx.depth_renderbuffer(size2);f=ctx.framebuffer([scene2],depth);f.use();f.clear(.45,.025,.27,1,depth=1);ctx.enable(moderngl.DEPTH_TEST);ctx.disable(moderngl.BLEND)
def rect(x0,y0,x1,y1,z,color):
 data=array.array('f',[x0,y0,z,x1,y0,z,x1,y1,z,x0,y0,z,x1,y1,z,x0,y1,z]);buf=ctx.buffer(data);va=ctx.vertex_array(geom,[(buf,'3f','Position')]);geom['Color'].value=color;va.render();va.release();buf.release()
rect(-1,-1,0,1,-.2,(239/255,1/255,137/255,1))
rect(-.8,-.8,.8,.8,.3,(239/255,1/255,137/255,253/255))
ctx.disable(moderngl.DEPTH_TEST);scene2.filter=(moderngl.NEAREST,moderngl.NEAREST);scene2.use(0);out2=ctx.texture(size2,4,dtype='f4');f2=ctx.framebuffer([out2]);f2.use();program['OutSize'].value=size2;program['Time'].value=0;vao.render(vertices=3);actual=array.array('f');actual.frombytes(f2.read(components=4,dtype='f4'))
def pixel(x,y):return actual[(y*64+x)*4:(y*64+x)*4+4]
left=pixel(16,16);right=pixel(48,16);outside=pixel(62,16)
assert left[2]>left[0] and outside[2]>outside[0],(list(left),list(outside))
assert right[0]>right[1]*3 and right[0]>.7,list(right)
assert all(abs(actual[i]-1)<1e-6 for i in range(3,len(actual),4))
report.update({'rgba8_entity_wall_same_rgb_depth_checks':3,'occluded_entity_pixel':list(left),'visible_entity_pixel':list(right),'output_alpha_restored':True});report['checks']+=3
(r/'shader-test-results.json').write_text(json.dumps(report,indent=2)+'\n');print('RGBA8_ENTITY_WALL_DEPTH_MASK_CHECKS=3')
