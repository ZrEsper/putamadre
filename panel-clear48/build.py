from pathlib import Path
import subprocess,hashlib,json,zipfile
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';src=root/'mods/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.47-environment-heat.jar';out=r/'output/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.48-clear-panel.jar'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
for name,h in json.loads((root/'spas-reload18/inputs.json').read_text())['tools'].items():assert sha(t/(name+'.jar'))==h
assert sha(src)==json.loads((r/'inputs.json').read_text())['input_sha256']
(r/'build/tool').mkdir(parents=True,exist_ok=True);out.parent.mkdir(exist_ok=True);cp=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',cp,'-d',str(r/'build/tool'),str(r/'Patch.java')],check=True);subprocess.run(['java','-cp',str(r/'build/tool')+':'+cp,'Patch',str(src),str(out)],check=True)
with zipfile.ZipFile(src)as a,zipfile.ZipFile(out)as b:
 assert set(a.namelist())==set(b.namelist()) and b.testzip()is None
 changed=[n for n in a.namelist()if a.read(n)!=b.read(n)]
 assert set(changed)=={'dev/zomboid/hordes/client/AdminScreen.class','dev/zomboid/hordes/client/LootPreviewScreen.class'}
(r/'validation.json').write_text(json.dumps({'output':out.name,'sha256':sha(out),'input_sha256':sha(src),'changed':changed,'all_settings_loot_population_network_assets_unchanged':True,'minecraft_runtime_tested':False},indent=2)+'\n')
