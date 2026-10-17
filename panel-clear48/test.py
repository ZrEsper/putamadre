from pathlib import Path
import subprocess,json
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';cp=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);v=json.loads((r/'validation.json').read_text());out=r/'build/test';out.mkdir(exist_ok=True)
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',cp,'-d',str(out),str(r/'RenderRegression.java'),*map(str,(r/'tests').rglob('*.java'))],check=True)
p=subprocess.run(['java','-Xverify:all','-cp',str(out)+':'+cp,'RenderRegression',str(root/'mods/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.47-environment-heat.jar'),str(r/'output'/v['output'])],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();(r/'test-results.json').write_text(json.dumps({'checks':[p.stdout.strip()],'minecraft_runtime_tested':False},indent=2)+'\n')
