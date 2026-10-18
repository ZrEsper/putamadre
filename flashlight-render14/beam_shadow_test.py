from pathlib import Path
import numpy as np,moderngl,json,math,sys
r=Path(__file__).resolve().parent;old='--old'in sys.argv;s=((r.parent/'flashlight-render10'if old else r)/'shaders/light.frag').read_text();ctx=moderngl.create_standalone_context(backend='egl',require=320);w=1024;vertex=(r/'shaders/light.vert').read_text();checks=0;report={}
def upload(p,name,value):
 if name in p:p[name].write(np.array(value,dtype="f4").ravel()[:p[name].dimension*p[name].array_length].tobytes())
def draw(fragment,setup):
 p=ctx.program(vertex_shader=vertex,fragment_shader=fragment);setup(p);target=ctx.texture((w,1),4,dtype='f4');fb=ctx.framebuffer([target]);fb.use();v=ctx.vertex_array(p,[]);v.render(vertices=3);out=np.frombuffer(fb.read(components=4,dtype='f4'),dtype='f4').reshape(w,4).copy();v.release();fb.release();target.release();p.release();return out
# Execute the angular-envelope expressions directly from the production shader.
expr=s[s.index('vec3 v=p-Origin[i]'):s.index('if(distance>=Range[i]')]
fragment='#version 150\nin vec2 texCoord;out vec4 fragColor;uniform float HalfTan[2],Range[2],NearFill[2];uniform vec3 Forward[2],Origin[2];void main(){int i=0;float angle=radians(mix(-75.,75.,texCoord.x));vec3 p=vec3(0.,sin(angle),-cos(angle))*3.;'+expr+'fragColor=vec4(beamPower,0.,0.,1.);}'
def setup(p,tan,external):
 for name,value in {'HalfTan':[tan,tan],'Range':[25.,25.],'NearFill':[external,0.]}.items():upload(p,name,value)
 for name,value in {'Forward':[(0,0,-1),(0,0,0)],'Origin':[(0,0,0),(0,0,0)]}.items():upload(p,name,value)
for angle in [16,20,25]:
 tan=math.tan(math.radians(angle))*1.55
 for external in [0.,1.]:
  a=draw(fragment,lambda p:setup(p,tan,external))[:,0];delta=np.diff(a);sign=np.sign(delta[abs(delta)>1e-6]);turns=np.count_nonzero(sign[1:]!=sign[:-1]);assert turns==1,('beam must have one peak, not near and far lobes',angle,external,turns);checks+=1
  if external==0:
   angles=np.linspace(-75+75/w,75-75/w,w);radius=float(np.max(abs(angles[a>.001])));assert radius>angle+2,(angle,radius);checks+=1;report[str(angle)+'_wide_half_angle']=radius
# Execute the production PCF function with a controlled sharp blocker map.
shadow=s[s.index('float shadow('):s.index('\n\nvoid main()')]
fragment='#version 150\nin vec2 texCoord;out vec4 fragColor;uniform sampler2D Shadow;uniform float HalfTan[2],Range[2];uniform vec3 Origin[2],ShadowOrigin[2],ShadowForward[2],ShadowRight[2],ShadowUp[2];vec2 screenVisibility(vec3 o,vec3 p){return vec2(1.,0.);}\n'+shadow+'\nvoid main(){vec3 p=vec3(mix(-1.,1.,texCoord.x),0.,-5.);float value=shadow(0,p,vec3(0,0,1));fragColor=vec4(value,value,value,1.);}'
arr=np.full((24,12),25,dtype='f4');arr[:12,:6]=2;texture=ctx.texture((12,24),1,arr.tobytes(),dtype='f4');texture.filter=(moderngl.NEAREST,moderngl.NEAREST);texture.use(0)
def shadow_setup(p):
 p['Shadow'].value=0
 for name,value in {'HalfTan':[math.tan(math.radians(25))]*2,'Range':[25.,25.]}.items():upload(p,name,value)
 for name,value in {'ShadowOrigin':(0,0,0),'ShadowForward':(0,0,-1),'ShadowRight':(1,0,0),'ShadowUp':(0,1,0)}.items():upload(p,name,[value,(0,0,0)])
curve=draw(fragment,shadow_setup)[:,0];step=float(np.max(abs(np.diff(curve))));assert step<.02,('blocky shadow edge',step);assert curve.min()<.01 and curve.max()>.99;checks+=2;report['max_shadow_pixel_step']=step
(r/'beam-shadow-test-results.json').write_text(json.dumps(dict(checks=checks,production_glsl_expressions=True,results=report,minecraft_runtime_tested=False),indent=2)+'\n');print('SINGLE_PEAK_ALL_APERTURES_WIDER_RADIUS_CONTINUOUS_SHADOW_CHECKS='+str(checks))
