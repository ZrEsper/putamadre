from pathlib import Path
import zipfile,json,array
import moderngl
root=Path(__file__).resolve().parent.parent;reports=json.loads((root/'validation.json').read_text());inputs=json.loads((root/'inputs.json').read_text());ctx=moderngl.create_standalone_context(backend='egl',require=320)
vertex='''#version 150
out vec2 texCoord;out vec2 oneTexel;
void main(){vec2 vertices[3]=vec2[3](vec2(-1,-1),vec2(3,-1),vec2(-1,3));vec2 p=vertices[gl_VertexID];gl_Position=vec4(p,0,1);texCoord=p*0.5+0.5;oneTexel=vec2(1.0/128.0,1.0/32.0);}'''
results={};size=(128,32);pixels=array.array('f')
for y in range(size[1]):
 for x in range(size[0]):
  light=.30 if x<42 else .65 if x<84 else .90
  pixels.extend([light,light,light])
scene=ctx.texture(size,3,pixels.tobytes(),dtype='f4');scene.filter=(moderngl.NEAREST,moderngl.NEAREST);meter=ctx.texture((1,1),4,array.array('f',[.5,.9,.65**.25,1]).tobytes(),dtype='f4');noise=ctx.texture((1,1),1,b'\x80');target=ctx.texture(size,4,dtype='f4');frame=ctx.framebuffer([target])
for name,path in [('old',root/'base'/inputs['nvg']['file']),('new',root/'output'/reports['nvg']['output'])]:
 with zipfile.ZipFile(path)as z:fragment=z.read('assets/fracturepoint/shaders/program/digital-white-vision.fsh').decode()
 program=ctx.program(vertex_shader=vertex,fragment_shader=fragment);vao=ctx.vertex_array(program,[])
 for key,value in {'DiffuseSampler':0,'BrightnessSampler':1,'NoiseSampler':2,'Time':0.,'InSize':size,'VignetteEnabled':0.,'VignetteRadius':.65,'Brightness':1.,'NoiseAmplification':0.}.items():
  if key in program:program[key].value=value
 scene.use(0);meter.use(1);noise.use(2);frame.use();vao.render(vertices=3);data=array.array('f');data.frombytes(frame.read(components=4,dtype='f4'));bands=[]
 for start,end in [(8,32),(48,76),(92,120)]:bands.append(sum(data[(y*size[0]+x)*4+1]for y in range(6,26)for x in range(start,end))/(20*(end-start)))
 results[name]={'shadow_wall_sky_green_channel':bands,'mean_green':sum(data[1::4])/len(data[1::4])};vao.release();program.release()
assert results['new']['mean_green']<results['old']['mean_green']*.55
v=results['new']['shadow_wall_sky_green_channel'];assert .02<v[0]<v[1]<v[2]<.60 and v[2]-v[0]>.10
report={'renderer':ctx.info['GL_RENDERER'],'actual_glsl_compiled_and_rendered':True,'bright_scene':results,'checks':'Digital filter reduces bright-scene luminance while retaining ordered shadow/wall/sky contrast','minecraft_runtime_tested':False};(root/'shader-test-results.json').write_text(json.dumps(report,indent=2)+'\n');print(json.dumps(report))
