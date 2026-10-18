# Exercise indexed GL state queries/restoration used by G6.State with distinct shaderpack MRT blends.
from pathlib import Path
import ctypes as c,json,moderngl
r=Path(__file__).resolve().parent;ctx=moderngl.create_standalone_context(backend='egl',require=400);gl=c.CDLL('libGL.so.1')
def fun(name,args,ret=None):
 f=getattr(gl,name);f.argtypes=args;f.restype=ret;return f
integer=fun('glGetIntegerv',[c.c_uint,c.POINTER(c.c_int)]);indexed=fun('glGetIntegeri_v',[c.c_uint,c.c_uint,c.POINTER(c.c_int)]);enabled=fun('glIsEnabledi',[c.c_uint,c.c_uint],c.c_ubyte);enable=fun('glEnablei',[c.c_uint,c.c_uint]);disable=fun('glDisablei',[c.c_uint,c.c_uint]);blend=fun('glBlendFuncSeparatei',[c.c_uint]*5);global_blend=fun('glBlendFuncSeparate',[c.c_uint]*4);error=fun('glGetError',[],c.c_uint)
n=c.c_int();integer(34852,c.byref(n));count=min(n.value,8)
expected=[]
for i in range(count):
 b=(770,771,1,0)if i%2 else(1,1,0,1)
 (enable if i%2 else disable)(3042,i);blend(i,*b);expected.append((i%2,*b))
def snapshot():
 rows=[]
 for i in range(count):
  row=[int(enabled(3042,i))]
  for key in [32969,32968,32971,32970]:
   value=c.c_int();indexed(key,i,c.byref(value));row.append(value.value)
  rows.append(tuple(row))
 return rows
saved=snapshot();assert saved==expected
# Terrain RenderTypes and post effects set global blend functions and enable masks.
global_blend(770,771,1,771)
for i in range(count):enable(3042,i)
assert snapshot()!=saved
for i,b in enumerate(saved):
 (enable if b[0] else disable)(3042,i);blend(i,*b[1:])
assert snapshot()==saved and error()==0
(r/'mrt-test-results.json').write_text(json.dumps({'real_opengl_indexed_targets_restored':count,'renderer':ctx.info['GL_RENDERER'],'minecraft_shaderpack_runtime_tested':False},indent=2)+'\n');print('SHADERPACK_MRT_BLEND_RESTORE_REAL_GL_TARGETS='+str(count))
