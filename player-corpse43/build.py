from pathlib import Path
import os,json,hashlib,zipfile,subprocess
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools'
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a):subprocess.run(list(map(str,a)),check=True)
pins=json.loads(Path('inputs.json').read_text());toolpins=json.loads((root/'spas-reload18/inputs.json').read_text())['tools']
for n,h in toolpins.items():assert sha(t/(n+'.jar'))==h
for d in ['build/tool','output']:Path(d).mkdir(parents=True,exist_ok=True)
compiler=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];cp=os.pathsep.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);run(*compiler,'-cp',cp,'-d','build/tool','Patch.java')
report={}
for kind,name in [('hordes','zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.43-infected-player-corpse.jar'),('corpse','corpse-neoforge-1.21.1-1.1.13-player-equipment-slots.2.jar'),('remains','zombie-remains-loot-1.6.1-neoforge-1.21.1-military-nvg-mythic.12-player-zombie-exclusion.jar')]:
 base=Path('base')/pins[kind]['file'];assert sha(base)==pins[kind]['sha256'];classes=Path('build')/kind;classes.mkdir(exist_ok=True);sources=list(Path('src/dev/zomboid',kind).glob('*.java'));run(*compiler,'-cp',base,'-d',classes,*sources);out=Path('output')/name;run('java','-cp',cp+os.pathsep+'build/tool','Patch',base,out,classes,kind)
 with zipfile.ZipFile(base)as a,zipfile.ZipFile(out)as b:
  changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));added=sorted(set(b.namelist())-set(a.namelist()));assert len(changed)==(2 if kind=='hordes' else 1);assert len(added)==(2 if kind=='hordes' else 1);assert b.testzip()is None
 report[kind]={'output':name,'input_sha256':sha(base),'output_sha256':sha(out),'changed':changed,'added':added,'minecraft_runtime_tested':False}
Path('validation.json').write_text(json.dumps(report,indent=2)+'\n')
