from pathlib import Path
import ctypes as c,json,numpy as np,moderngl
r=Path(__file__).resolve().parent;ctx=moderngl.create_standalone_context(backend='egl',require=320);gl=c.CDLL('libGL.so.1');mask=gl.glDepthMask;mask.argtypes=[c.c_ubyte];gl.glBlendFuncSeparate.argtypes=[c.c_uint]*4;gl.glDepthFunc.argtypes=[c.c_uint]
w,h=96,64;color=ctx.texture((w,h),4,dtype='f4');depth=ctx.depth_texture((w,h));fbo=ctx.framebuffer([color],depth);fbo.use()
vertex='''#version 150
uniform int Shape;uniform float Z;void main(){vec2 full[3]=vec2[3](vec2(-1,-1),vec2(3,-1),vec2(-1,3));vec2 beam[6]=vec2[6](vec2(-.018,-.7),vec2(.018,-.7),vec2(-.018,.7),vec2(-.018,.7),vec2(.018,-.7),vec2(.018,.7));gl_Position=vec4(Shape==0?full[gl_VertexID]:beam[gl_VertexID],Z,1.);}'''
p=ctx.program(vertex_shader=vertex,fragment_shader='#version 150\nuniform vec4 Color;out vec4 fragColor;void main(){fragColor=Color;}');vao=ctx.vertex_array(p,[])
def draw(shape,z,rgba):p['Shape'].value=shape;p['Z'].value=z;p['Color'].value=rgba;vao.render(vertices=3 if shape==0 else 6)
def readdepth():return np.frombuffer(depth.read(),dtype='f4').copy()
def readcolor():return np.frombuffer(color.read(),dtype='f4').reshape(h,w,4).copy()
results={}
for write in [True,False]:
 for behind in [False,True]:
  mask(1);ctx.disable(moderngl.BLEND);ctx.enable(moderngl.DEPTH_TEST);gl.glDepthFunc(515);fbo.clear(0,0,0,1,depth=1);draw(0,.2,(.1,.1,.1,1));before=readdepth();base=readcolor()
  ctx.enable(moderngl.BLEND);gl.glBlendFuncSeparate(770,1,1,0);mask(int(write));draw(1,.8 if behind else-.6,(1,0,0,251/255));after=readdepth();lit=readcolor()
  changed=int(np.count_nonzero(abs(after-before)>1e-6));visible=float(np.max(lit[:,:,0]-base[:,:,0]))
  assert (changed>20)==(write and not behind),(write,behind,changed)
  assert (visible>.5)==(not behind),(write,behind,visible)
  results[str(write)+'_behind_'+str(behind)]={'depth_pixels_changed':changed,'added_red':visible}
mask(1);assert gl.glGetError()==0
(r/'depth-test-results.json').write_text(json.dumps({'real_opengl_render':True,'native_depth_write_makes_beam_solid':True,'new_color_only_preserves_depth_and_wall_occlusion':True,'cases':results,'minecraft_runtime_tested':False},indent=2)+'\n');print('REAL_GL_LASER_DEPTH_WRITE_FALSE_SHADOW_FRONT_WALL_OCCLUSION_CHECKS=8')
