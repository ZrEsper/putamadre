"""Actual EGL copies: reproduce format mismatch, then preserve all source formats."""
from pathlib import Path
import ctypes as c,json,moderngl,numpy as np
ctx=moderngl.create_standalone_context(backend='egl',require=430);g=c.CDLL('libGL.so.1')
def fn(n,t):v=getattr(g,n);v.argtypes=t;return v
bind=fn('glBindTexture',[c.c_uint,c.c_uint]);gen=fn('glGenTextures',[c.c_int,c.POINTER(c.c_uint)]);image=fn('glTexImage2D',[c.c_uint,c.c_int,c.c_int,c.c_int,c.c_int,c.c_int,c.c_uint,c.c_uint,c.c_void_p]);copy=fn('glCopyImageSubData',[c.c_uint,c.c_uint,*([c.c_int]*4),c.c_uint,c.c_uint,*([c.c_int]*7)]);read=fn('glGetTexImage',[c.c_uint,c.c_int,c.c_uint,c.c_uint,c.c_void_p])
def tex(fmt):
 t=c.c_uint();gen(1,c.byref(t));bind(3553,t.value);packed=fmt in [35056,36013];image(3553,0,fmt,32,32,0,34041 if packed else 6402,36269 if fmt==36013 else 34042 if packed else 5126,None);g.glTexParameteri(3553,10241,9728);g.glTexParameteri(3553,10240,9728);assert g.glGetError()==0;return t.value
def cp(a,b):copy(a,3553,0,0,0,0,b,3553,0,0,0,0,32,32,1);return g.glGetError()
a,b=tex(36012),tex(33190);assert cp(a,b)==1282,'mismatched depth copy must reproduce GL_INVALID_OPERATION'
reports=[]
for fmt in [6402,33189,33190,33191,36012,35056,36013]:
 a,b=tex(fmt),tex(fmt);fb=c.c_uint();g.glGenFramebuffers(1,c.byref(fb));g.glBindFramebuffer(36160,fb.value);g.glFramebufferTexture2D(36160,36096,3553,a,0);g.glDrawBuffer(0);g.glReadBuffer(0);g.glClearDepth(c.c_double(.37));g.glClear(256);assert g.glGetError()==0
 assert cp(a,b)==0,fmt
 bind(3553,b);data=np.zeros((32,32),dtype='f4');read(3553,0,6402,5126,data.ctypes.data);assert g.glGetError()==0;assert np.max(abs(data-.37))<3e-5,(fmt,data.min(),data.max());reports.append(fmt)
Path(__file__).with_name('depth-formats-results.json').write_text(json.dumps({'mismatched_copy_reproduced_gl1282':True,'matching_formats_copied_and_depth_verified':reports,'minecraft_runtime_tested':False},indent=2)+'\n');print('REAL_GL: mismatched depth reproduces 1282; all seven matching formats preserve depth')
