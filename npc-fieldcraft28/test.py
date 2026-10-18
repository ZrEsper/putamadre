from pathlib import Path
import subprocess,json
r=Path(__file__).resolve().parent;root=r.parent;jar=next((r/'output').glob('*.jar'));out=r/'build/test';out.mkdir(exist_ok=True)
cp=':'.join(map(str,[root/'npc-roles22/build/test',jar,root/'npc-social17/build/test',*list((root/'npc-social17/output').glob('faction*.jar')),root/'npc-social17/build/api']))
subprocess.run(['java','-jar',str(root/'hordes-melee-interiors-patch/build/tools/ecj.jar'),'-21','-proc:none','-nowarn','-cp',cp,'-d',str(out),*map(str,(r/'tests').rglob('*.java'))],check=True)
reports=[]
for name in ['TacticsChecks24','MotionChecks26','FlashChecks26','ModelChecks26','ThreatChecks27','FieldChecks28']:
 p=subprocess.run(['java','-Xverify:all','-cp',str(out)+':'+cp,'mod.cdv.tnpc.tactical.'+name],capture_output=True,text=True);print(p.stdout,p.stderr);p.check_returncode();reports.append(p.stdout.strip())
(r/'test-results.json').write_text(json.dumps(dict(results=reports,minecraft_runtime_tested=False),indent=2))
