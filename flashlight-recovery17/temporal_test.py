from pathlib import Path
import json
r=Path(__file__).resolve().parent
# Reuse the full production-shader scene setup; only vary the delayed collision map.
core=(r/'shader_test.py').read_text().split('checks=0\n')[0]
def measure(old):
 source=core.replace("else(r/'shaders/light.frag')","else(r.parent/'flashlight-render12/shaders/light.frag')")if old else core
 ns={'__file__':str(r/'temporal_test.py')};exec(compile(source,'full-scene-setup','exec'),ns)
 np=ns['np'];p=ns['p'];textures=ns['textures'];render=ns['render'];shadow=ns['shadow'];w,h=ns['w'],ns['h'];errors=[]
 for frame in range(24):
  x=.035*np.sin(frame*.3);p['Origin'].write(np.array([(x,0,0),(0,0,0)],dtype='f4').tobytes())
  textures[3].write(shadow.tobytes());reference=render()
  textures[3].write(np.full_like(shadow,.5+frame%3).tobytes());delayed=render()
  mask=np.zeros((h,w),dtype=bool);mask[h//2-6:h//2+6,w//2-6:w//2+6]=True
  errors.append(float(np.max(abs(reference[mask]-delayed[mask]))))
 ns['ctx'].release();return max(errors)
old=measure(True);new=measure(False);assert old>.25,old;assert new<1e-5,new
(r/'temporal-test-results.json').write_text(json.dumps({'frames':24,'old_visible_shadow_pop':old,'new_visible_shadow_pop':new,'real_glsl_render':True,'minecraft_runtime_tested':False},indent=2)+'\n');print('MOVING_VISIBLE_LIGHT_STALE_GRID_POP_OLD='+str(old)+' NEW='+str(new))
