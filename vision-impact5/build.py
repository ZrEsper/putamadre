from pathlib import Path
import os,hashlib,json,subprocess,zipfile
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';inputs=json.loads(Path('inputs.json').read_text())
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a):subprocess.run(list(map(str,a)),check=True)
for name,h in json.loads((root/'spas-reload18/inputs.json').read_text())['tools'].items():assert sha(t/(name+'.jar'))==h
for item in inputs.values():assert sha(Path('base')/item['file'])==item['sha256']
for d in ['build/tool','output']:Path(d).mkdir(parents=True,exist_ok=True)
ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];cp=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);run(*ecj,'-cp',cp,'-d','build/tool','Patch.java');reports={}
for kind,name,hooks in [('nvg','fracturepoint-1.21.1-3.0.0-PT2-nvg-filter-priority.4.jar',0),('aid','firstaid-1.21.1-ZOMBOID-v3.6.4-nvg-filter-priority.jar',1),('tac','tacz-tactical-1.2.4-neoforge-1.21.1-marks200-85s.5.jar',3)]:
 src=Path('base')/inputs[kind]['file'];classes=Path('build/classes')/kind;classes.mkdir(parents=True,exist_ok=True);run(*ecj,'-cp',src,'-d',classes,*Path('src',kind).rglob('*.java'));out=Path('output')/name;run('java','-cp','build/tool:'+cp,'Patch',src,out,classes,hooks)
 with zipfile.ZipFile(src)as a,zipfile.ZipFile(out)as b:
  assert b.testzip()is None;changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));added=sorted(set(b.namelist())-set(a.namelist()));expected={'nvg':['com/raiiiden/warborn/client/util/NvgClient2.class'],'aid':['ichttt/mods/firstaid/client/PainVisualEffectsController.class'],'tac':['dev/tactical/tacz/ImpactMarks.class','dev/tactical/tacz/OnceEffects.class','dev/tactical/tacz/TimedMarks.class']}[kind];assert changed==expected,(kind,changed)
 reports[kind]={'input_sha256':sha(src),'output':name,'sha256':sha(out),'changed':changed,'added':added,'minecraft_runtime_tested':False}
Path('validation.json').write_text(json.dumps(reports,indent=2)+'\n')
