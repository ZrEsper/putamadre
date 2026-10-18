from pathlib import Path
import subprocess,json
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';out=r/'build/test';out.mkdir(exist_ok=True);jar=r/'output'/json.loads((r/'validation.json').read_text())['file'];cp=':'.join(map(str,[root/'npc-social17/build/test',jar,*list((root/'npc-social17/output').glob('faction*.jar')),root/'npc-social17/build/api']))
p=subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',cp,'-d',str(out),*map(str,(r/'tests').rglob('*.java'))],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode()
cp=str(out)+':'+cp
reports=[]
for cls in ['RolesChecks22','EggChecks22','Social21Checks','Conversation19Checks']:
 p=subprocess.run(['java','-Xverify:all','-cp',cp,'mod.cdv.tnpc.tactical.'+cls],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();reports.append(p.stdout.strip())
(r/'test-results.json').write_text(json.dumps(dict(checks=reports,minecraft_runtime_tested=False),indent=2)+'\n')
