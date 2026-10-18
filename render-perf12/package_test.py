"""Regression for the IllegalClassLoadError in the user's 2026-10-18 crash.
Mixin owns every class under a configured package, including classes not listed as mixins.
"""
from pathlib import Path
import json,zipfile
r=Path(__file__).resolve().parent;info=json.loads((r/'validation.json').read_text());jar=r/'output'/info['integration']['file']
with zipfile.ZipFile(jar)as z:
 cfg=json.loads(z.read('renderperf12.mixins.json'));prefix=cfg['package'].replace('.','/')+'/'
 mixins={prefix+name.replace('.','/')+'.class'for name in cfg['client']}
 owned={name for name in z.namelist()if name.startswith(prefix)and name.endswith('.class')}
 assert owned==mixins,('Non-mixin classes inside reserved package',owned-mixins)
 helpers={'dev/zomboid/perf12/GraphOrder12.class','dev/zomboid/perf12/GraphOrder12$Access.class','dev/zomboid/perf12/GraphOrder12$Edge.class'}
 assert helpers.issubset(z.namelist())and not any(name.startswith(prefix)for name in helpers)
 assert 'dev/zomboid/perf12/IrisGraphMixin12.class'not in z.namelist()
 mixin=z.read(next(iter(mixins)));assert b'dev/zomboid/perf12/GraphOrder12'in mixin
 assert b'dev/zomboid/perf12/mixin/GraphOrder12'not in mixin
 assert b'1.7.13-iris-mixin-package-fix'in z.read('META-INF/neoforge.mods.toml')
with zipfile.ZipFile(r/'base/integration12-regression.jar')as old:
 oldcfg=json.loads(old.read('renderperf12.mixins.json'));oldprefix=oldcfg['package'].replace('.','/')+'/'
 assert any(name.startswith(oldprefix)and 'GraphOrder12'in name for name in old.namelist()),'Old artifact must reproduce reserved helper package'
report={'old_1_7_12_reproduces_package_violation':True,'reserved_package':cfg['package'],'only_registered_mixins_in_reserved_package':True,'helper_and_nested_classes_outside_reserved_package':True,'injected_call_targets_normal_helper':True,'old_mixin_class_absent':True,'minecraft_runtime_tested':False}
(r/'package-test-results.json').write_text(json.dumps(report,indent=2)+'\n');print('MIXIN_RESERVED_PACKAGE_CLASS_LOADING_REGRESSION=PASS')
