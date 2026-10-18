from pathlib import Path
import ctypes as c,json,math,numpy as np,moderngl
r=Path(__file__).resolve().parent;ctx=moderngl.create_standalone_context(backend='egl',require=330);gl=c.CDLL('libGL.so.1');gl.glDepthMask.argtypes=[c.c_ubyte];gl.glBlendFuncSeparate.argtypes=[c.c_uint]*4
p=ctx.program(vertex_shader=(r/'shaders/laser37.vert').read_text(),fragment_shader=(r/'shaders/laser37.frag').read_text());vao=ctx.vertex_array(p,[]);w,h=160,120;xx,yy=np.meshgrid(np.arange(w),np.arange(h));ndc=np.stack([(xx+.5)/w*2-1,(yy+.5)/h*2-1,np.zeros((h,w)),np.ones((h,w))],-1);n=0;report={}
def norm(v):return v/np.linalg.norm(v)
def perspective():
 f=1/math.tan(math.radians(35));q=np.zeros((4,4));q[0,0]=f/(w/h);q[1,1]=f;q[2,2]=-(128+.05)/(128-.05);q[2,3]=-2*128*.05/(128-.05);q[3,2]=-1;return q
for name,cam,ortho in [('first',np.array([0,1.6,.1]),False),('third',np.array([2.8,2.6,4.5]),False),('isometric',np.array([8,12,8]),True)]:
 target=np.array([0,1.5,-6]);forward=norm(target-cam);right=norm(np.cross(forward,[0,1,0]));up=np.cross(right,forward);view=np.eye(4);view[:3,:3]=[right,up,-forward];projection=perspective()
 if ortho:projection=np.eye(4);projection[0,0]=1/8;projection[1,1]=1/6;projection[2,2]=-2/(128-.05);projection[2,3]=-(128+.05)/(128-.05)
 vp=projection@view;inverse=np.linalg.inv(vp);p['ViewProjection'].write(vp.astype('f4').T.tobytes());p['InverseViewProjection'].write(inverse.astype('f4').T.tobytes());p['Viewport'].value=(w,h);p['From'].value=tuple(np.array([-.15,1.5,-.12])-cam);p['To'].value=tuple(target-cam);p['WidthPixels'].value=.75;p['DotPixels'].value=2.2;p['WorldDepth'].value=0
 ends=[]
 for z in [-1,1]:
  clip=ndc.copy();clip[:,:,2]=z;q=clip@inverse.T;ends.append(q[:,:,:3]/q[:,:,3:]+cam)
 a,b=ends;direction=b-a
 def plane(z):
  t=(z-a[:,:,2])/direction[:,:,2];points=a+direction*t[:,:,None];q=np.concatenate([points-cam,np.ones((h,w,1))],-1)@vp.T;return(q[:,:,2]/q[:,:,3]*.5+.5).astype('f4')
 tx=ctx.texture((w,h),1,plane(-10).tobytes(),dtype='f4');tx.filter=(moderngl.NEAREST,moderngl.NEAREST);tx.use(0);out=ctx.texture((w,h),4,dtype='f4');handDepth=ctx.depth_texture((w,h));fb=ctx.framebuffer([out],handDepth);fb.use();gl.glDepthMask(1);fb.clear(.13,.16,.2,1,depth=1);before=np.frombuffer(handDepth.read(),dtype='f4').copy()
 def render(color):
  gl.glDepthMask(1);ctx.disable(moderngl.BLEND);fb.clear(.13,.16,.2,1,depth=1);gl.glDepthMask(0);ctx.disable(moderngl.DEPTH_TEST);ctx.enable(moderngl.BLEND);gl.glBlendFuncSeparate(770,1,1,0);p['Color'].value=color
  for shape in [0,1]:p['Shape'].value=shape;vao.render(vertices=6)
  return np.frombuffer(fb.read(components=4,dtype='f4'),dtype='f4').reshape(h,w,4).copy()
 q=vp@np.append(target-cam,1);uv=q[:2]/q[3]*.5+.5;ix,iy=int(uv[0]*w),int(uv[1]*h)
 for color in [(1,0,0),(0,1,0),(0,0,1),(.7,.1,.5)]:
  lit=render(color);delta=lit[iy,ix,:3]-[.13,.16,.2];assert np.max(delta)>.4*max(color),(name,color,delta);assert np.max(abs(delta/delta.max()-np.array(color)/max(color)))<1e-5;(n:=n+2)
  assert abs(lit[iy,ix,3]-251/255)<1e-6;n+=1
 clear=render((1,0,0));visible=int(np.count_nonzero(clear[:,:,0]>.2));tx.write(plane(-3).tobytes());blocked=render((1,0,0));assert abs(blocked[iy,ix,0]-.13)<1e-5,'world wall still occludes beam after hand-depth clear';n+=1
 assert 0<int(np.count_nonzero(blocked[:,:,0]>.2))<visible;(n:=n+1)
 assert np.max(abs(np.frombuffer(handDepth.read(),dtype='f4')-before))==0;n+=1
 report[name]={'visible_beam_pixels':visible,'wall_occludes_dot':True,'color_preserved':True,'main_depth_untouched':True}
 tx.release();fb.release();out.release();handDepth.release()
gl.glDepthMask(1);assert gl.glGetError()==0
(r/'shader-test-results.json').write_text(json.dumps({'checks':n,'real_opengl_render':True,'first_third_isometric':report,'minecraft_shaderpack_runtime_tested':False},indent=2)+'\n');print('REAL_GL_LATE_LASER_THREE_CAMERAS_COLOR_CACHED_WORLD_DEPTH_WALL_ALPHA_CHECKS='+str(n))
