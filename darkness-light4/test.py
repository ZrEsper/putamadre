from pathlib import Path
import moderngl,array,zipfile,json
r=Path(__file__).resolve().parent;ctx=moderngl.create_standalone_context(backend='egl',require=320)
vert='''#version 150
out vec2 texCoord;void main(){vec2 p[3]=vec2[3](vec2(-1,-1),vec2(3,-1),vec2(-1,3));gl_Position=vec4(p[gl_VertexID],0,1);texCoord=p[gl_VertexID]*.5+.5;}'''
meta=json.loads((r/'validation.json').read_text());src=r/'base/darkness3.jar';dst=r/'output'/meta['file'];n=meta['only_changed'][0]
colors=[(.08,0,0,250/255),(0,.08,0,250/255),(0,0,.08,250/255),(.08,0,0,1),(.12,.12,.12,1),(1,.6,.2,1),(.1,.2,.3,253/255)]
scene=ctx.texture((len(colors),1),4,bytes(round(v*255)for p in colors for v in p));scene.filter=(moderngl.NEAREST,moderngl.NEAREST);scene.use(0);target=ctx.texture((len(colors),1),4,dtype='f4');fb=ctx.framebuffer([target]);fb.use()
def render(jar,strength):
 with zipfile.ZipFile(jar)as z:s=z.read(n).decode()
 p=ctx.program(vertex_shader=vert,fragment_shader=s);p['DiffuseSampler'].value=0
 for k,v in {'DarknessStrength':strength,'DarknessFloor':.018,'KeepStart':.06,'KeepEnd':.34,'DaylightPreserve':0.}.items():p[k].value=v
 vao=ctx.vertex_array(p,[]);vao.render(vertices=3);out=array.array('f');out.frombytes(fb.read(components=4,dtype='f4'));vao.release();p.release();return list(out)
checks=0
for strength in [0.,.3,.82,1.]:
 old,new=render(src,strength),render(dst,strength)
 for i in range(3):
  expected=[round(v*255)/255 for v in colors[i]];assert all(abs(new[i*4+k]-expected[k])<1e-5 for k in range(4));checks+=1
 for i in range(3,len(colors)):
  assert all(abs(new[i*4+k]-old[i*4+k])<1e-6 for k in range(4)),i;checks+=1
old,new=render(src,1.),render(dst,1.);assert old[0]*255<.5 and new[0]*255>19;checks+=1
report={'checks':checks,'actual_glsl_compiled_rendered':True,'old_faint_red_laser':old[:4],'new_faint_red_laser':new[:4],'ordinary_scene_pixels_unchanged':True,'minecraft_runtime_tested':False,'renderer':ctx.info['GL_RENDERER']};(r/'test-results.json').write_text(json.dumps(report,indent=2)+'\n');print('DARKNESS_REAL_SHADER_FLASHLIGHT_PRESERVATION_CHECKS='+str(checks));print('ORIGINAL_FAINT_RED',old[:3],'PATCHED',new[:3])
