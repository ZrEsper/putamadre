from pathlib import Path
import re,json,zipfile,hashlib
root=Path(__file__).resolve().parent
source=root.parent/'zombie-progressive-gore-patch/TZP-1.21.1-Gore.5-progressive-trauma.zip'
output=root/'TZP-1.21.1-Gore.6-emf-syntax-fix.zip'
# Mirrors EMF 3.3.11 MathExpressionParser's strict OptiFine decimal restriction.
DECIMAL=re.compile(r'(?<![\w.])\.(\d+)')
models=0;lines=0;fixed=0
with zipfile.ZipFile(source) as a,zipfile.ZipFile(output,'w',zipfile.ZIP_DEFLATED) as b:
 for name in a.namelist():
  content=a.read(name)
  if name.endswith('.jem'):
   data=json.loads(content)
   for model in data['models']:
    for animation in model.get('animations',[]):
     for key,expression in animation.items():
      expression,count=DECIMAL.subn(r'0.\1',expression);fixed+=count;lines+=1
      assert not DECIMAL.search(expression),(name,key,expression)
      animation[key]=expression
   content=(json.dumps(data,indent=2)+'\n').encode();models+=1
  elif name=='pack.mcmeta':
   data=json.loads(content);data['pack']['description']='TZP Gore 6: progressive injuries / EMF strict syntax corrected';content=json.dumps(data).encode()
  b.writestr(name,content)
assert models==4 and fixed>0
with zipfile.ZipFile(source) as a,zipfile.ZipFile(output) as b:
 assert b.testzip() is None
 unchanged=[n for n in a.namelist() if not n.endswith('.jem') and n!='pack.mcmeta']
 assert all(a.read(n)==b.read(n) for n in unchanged)
 # Regression: the bad expression from the user's actual EMF error is rejected,
 # while all generated animation lines now satisfy this exact restriction.
 bad='if(distance<24,-sin(hurt_time*.7)*hurt_time*.04,0)'
 assert DECIMAL.search(bad)
 assert DECIMAL.sub(r'0.\1',bad)=='if(distance<24,-sin(hurt_time*0.7)*hurt_time*0.04,0)'
(root/'validation.json').write_text(json.dumps({'source_sha256':hashlib.sha256(source.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(output.read_bytes()).hexdigest(),'models':models,'animation_lines':lines,'decimal_literals_fixed':fixed,'other_entries_preserved':len(unchanged),'minecraft_runtime_tested':False},indent=2)+'\n')
print(f'Corrected {fixed} decimal literals in {lines} animation lines / {models} models; preserved {len(unchanged)} other entries')
