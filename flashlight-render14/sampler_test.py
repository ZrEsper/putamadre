from pathlib import Path
import ctypes as c,json,numpy as np,moderngl
r=Path(__file__).resolve().parent;ctx=moderngl.create_standalone_context(backend='egl',require=330);gl=c.CDLL('libGL.so.1')
def f(name,types,ret=None):x=getattr(gl,name);x.argtypes=types;x.restype=ret;return x
gen=f('glGenTextures',[c.c_int,c.POINTER(c.c_uint)]);bind=f('glBindTexture',[c.c_uint,c.c_uint]);active=f('glActiveTexture',[c.c_uint]);image=f('glTexImage2D',[c.c_uint,c.c_int,c.c_int,c.c_int,c.c_int,c.c_int,c.c_uint,c.c_uint,c.c_void_p]);param=f('glTexParameteri',[c.c_uint,c.c_uint,c.c_int]);gen_sampler=f('glGenSamplers',[c.c_int,c.POINTER(c.c_uint)]);sampler_param=f('glSamplerParameteri',[c.c_uint,c.c_uint,c.c_int]);bind_sampler=f('glBindSampler',[c.c_uint,c.c_uint]);get=f('glGetIntegeri_v',[c.c_uint,c.c_uint,c.POINTER(c.c_int)])
texture=c.c_uint();gen(1,c.byref(texture));active(33984);bind(3553,texture.value);data=np.full((4,4,4),[196,80,20,255],dtype='u1');image(3553,0,32856,4,4,0,6408,5121,data.ctypes.data);param(3553,10241,9728);param(3553,10240,9728)
sampler=c.c_uint();gen_sampler(1,c.byref(sampler));sampler_param(sampler.value,10241,9987);sampler_param(sampler.value,10240,9729);bind_sampler(0,sampler.value)
program=ctx.program(vertex_shader=(r/'shaders/light.vert').read_text(),fragment_shader='#version 150\nuniform sampler2D Input;in vec2 texCoord;out vec4 fragColor;void main(){fragColor=texture(Input,texCoord);}')
program['Input'].value=0;vao=ctx.vertex_array(program,[]);target=ctx.texture((4,4),4,dtype='f4');fb=ctx.framebuffer([target]);fb.use();active(33984);bind(3553,texture.value)
def draw():vao.render(vertices=3);return np.frombuffer(fb.read(components=4,dtype='f4'),dtype='f4').reshape(4,4,4)[2,2].copy()
old=draw();saved=c.c_int();get(35097,0,c.byref(saved));bind_sampler(0,0);new=draw();bind_sampler(0,saved.value);restored=c.c_int();get(35097,0,c.byref(restored));assert restored.value==sampler.value
assert max(old[:3])<1e-6,old;assert np.max(abs(new[:3]-data[0,0,:3]/255))<1e-5,new;assert gl.glGetError()==0
(r/'sampler-test-results.json').write_text(json.dumps({'real_opengl_render':True,'inherited_mipmap_sampler_black':old.tolist(),'isolated_sampler_texture':new.tolist(),'original_sampler_restored':True,'minecraft_runtime_tested':False},indent=2)+'\n');print('REAL_GL_INHERITED_SAMPLER_BLACK_TEXTURE_ISOLATION_RESTORE_CHECKS=4')
