from pathlib import Path
import runpy,zipfile,json,numpy as np
r=Path(__file__).resolve().parent;ns=runpy.run_path(str(r/'shader_test.py'));info=json.loads((r/'validation.json').read_text());name=next(n for n in info['filters']if'hybrid'in n)
with zipfile.ZipFile(r/'output'/info['file'])as z:
 fragment=z.read(name).decode();ns['render'].__globals__['config']=json.loads(z.read(name.replace('.fsh','.json')))
scene=ns['scene'].copy();scene[:12,:32,:3]=(.68,.46,.31);scene[:12,32:,:3]=(.12,.14,.16);scene[:12,:,3]=1
ns['tex'].write(np.round(scene*255).astype('u1').tobytes());ns['tex'].use(0);out=ns['render'](fragment,.13);skin=out[3,20,:3];gun=out[3,45,:3]
assert skin[2]>skin[0]*1.2 and gun[2]>gun[0]*1.2,(skin,gun)
assert not np.allclose(skin,scene[3,20,:3])and not np.allclose(gun,scene[3,45,:3])
assert skin[2]>gun[2]+.05,'filter must retain the hands/weapon material contrast'
(r/'hands-test-results.json').write_text(json.dumps({'hybrid_hands_tinted':skin.tolist(),'hybrid_weapon_tinted':gun.tolist(),'texture_contrast_preserved':True,'real_glsl_render':True,'minecraft_runtime_tested':False},indent=2)+'\n');print('FULL_FRAME_HYBRID_HANDS_WEAPON_TINT_TEXTURE_CHECKS=5')
