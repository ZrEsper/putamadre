from pathlib import Path
import subprocess,json,zipfile
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools';v=json.loads((r/'validation.json').read_text());jar=r/'output'/v['file'];asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',asm,'-d',str(r/'build/tool'),str(r/'Checks.java')],check=True)
p=subprocess.run(['java','-Xverify:all','-cp',str(r/'build/tool')+':'+asm,'Checks',str(jar),str(r.parent/'mods/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.18-spas-reload-guard.jar')],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode()
with zipfile.ZipFile(jar)as z:
 for config in ['ads14.mixins.json','renderperf12.mixins.json']:
  cfg=json.loads(z.read(config));prefix=cfg['package'].replace('.','/')+'/'
  listed={prefix+n.replace('.','/')+'.class'for group in ['mixins','client']for n in cfg.get(group,[])}
  owned={n for n in z.namelist()if n.startswith(prefix)and n.endswith('.class')};assert owned==listed,(config,owned-listed)
(r/'test-results.json').write_text(json.dumps({'checks':[p.stdout.strip(),'NO_RUNTIME_HELPERS_INSIDE_RESERVED_MIXIN_PACKAGES'],'minecraft_runtime_tested':False},indent=2)+'\n')
