from pathlib import Path
import zipfile,json
r=Path(__file__).resolve().parent;v=json.loads((r/'validation.json').read_text());jar=r/'output'/v['file']
with zipfile.ZipFile(r/'base/fracture13.jar')as old,zipfile.ZipFile(jar)as new:
 changed=[n for n in old.namelist()if old.read(n)!=new.read(n)];assert set(changed)=={'com/raiiiden/warborn/hybrid/Gpu12.class','com/raiiiden/warborn/hybrid/Gpu12$State.class'}
 for n in old.namelist():
  if n.startswith('assets/'):assert old.read(n)==new.read(n)
source=(r.parent/'flashlight-render14/src/net/flashlight/battery/G6.java').read_text().replace('package net.flashlight.battery;','package com.raiiiden.warborn.hybrid;').replace('public final class G6','public final class Gpu12').replace('G6.','Gpu12.').replace('R.','VR12.').replace(' Engine6.capturing=false;','')
assert source==(r/'src/com/raiiiden/warborn/hybrid/Gpu12.java').read_text()
(r/'test-results.json').write_text(json.dumps({'only_gpu_state_helpers_changed':True,'all_filter_shader_audio_visor_assets_unchanged':True,'same_tested_texture_sampler_blend_scope_as_flashlight':True,'minecraft_runtime_tested':False},indent=2)+'\n');print('NVG_GPU_STATE_HELPERS_ONLY_SAME_TESTED_SCOPE_ALL_ASSETS_IDENTICAL=PASS')
