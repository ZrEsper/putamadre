from pathlib import Path
import subprocess,zipfile,json,hashlib,shutil,re
r=Path(__file__).resolve().parent;w=Path('/workspace/survivor-language-work');base=r/'base.jar'
if not base.exists():shutil.copy2('/workspace/attachments/2a6e42e5-04a3-4b81-b5b0-3a134d60f265/survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v28-mechanics.jar',base)
subprocess.run(['python',str(r/'translate.py')],check=True);subprocess.run(['python',str(r/'extra.py')],check=True)
cp=':'.join(map(str,(r.parent/'weapon-interaction-patch/build/tools').glob('asm*.jar')));ecj=['java','-jar','/workspace/onboarding/tools/ecj.jar','-21','-proc:none','-warn:none']
subprocess.run(ecj+['-cp',cp,'-d',str(w/'patch-tool'),str(r/'Patch.java')],check=True)
subprocess.run(['java','-cp',str(w/'patch-tool')+':'+cp,'Patch',str(base),str(w/'patched.jar'),str(r/'mapping.tsv'),str(w/'used.txt')],check=True)
stub=w/'compile-stubs/net/minecraft/locale/Language.java';stub.parent.mkdir(parents=True,exist_ok=True);stub.write_text('package net.minecraft.locale;public abstract class Language {public static Language getInstance(){throw new AssertionError();}public abstract String getOrDefault(String key,String fallback);}')
chat=stub.parents[1]/'network/chat';chat.mkdir(parents=True,exist_ok=True)
(chat/'Component.java').write_text('package net.minecraft.network.chat;public interface Component {static MutableComponent translatableWithFallback(String key,String fallback,Object...args){throw new AssertionError();}static MutableComponent literal(String value){throw new AssertionError();}}')
(chat/'MutableComponent.java').write_text('package net.minecraft.network.chat;public class MutableComponent implements Component {}')
subprocess.run(ecj+['-d',str(w/'classes'),*map(str,(w/'compile-stubs').rglob('*.java')),*map(str,(r/'src').rglob('*.java'))],check=True)
out=r.parent/'mods/survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v29-en-de.jar'
langs={p.name:json.loads(p.read_text()) for p in (r/'lang').glob('*.json')}
for code in ['es_ar','es_cl','es_ec','es_mx','es_uy','es_ve']:langs[code+'.json']=langs['es_es.json']
assert len({tuple(sorted(d)) for d in langs.values()})==1
for k in langs['es_es.json']:
 pattern=r'%(?:\d+\$)?(?:\d+)?(?:\.\d+)?[sdif%]'
 expected=re.findall(pattern,langs['es_es.json'][k])
 for l,d in langs.items():assert expected==re.findall(pattern,d[k]),(k,l,expected,re.findall(pattern,d[k]))
with zipfile.ZipFile(w/'patched.jar') as a,zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as b:
 for e in a.infolist():
  if e.filename.startswith('assets/survivorcreator/lang/') and e.filename.endswith('.json'):continue
  b.writestr(e,a.read(e))
 for name,d in langs.items():b.writestr('assets/survivorcreator/lang/'+name,json.dumps(d,ensure_ascii=False,indent=2)+'\n')
 b.writestr('assets/survivorcreator/localized-map.tsv',(r/'mapping.tsv').read_bytes())
 for p in (w/'classes/dev').rglob('*.class'):b.writestr(p.relative_to(w/'classes').as_posix(),p.read_bytes())
with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
 assert b.testzip() is None
 changed=[n for n in a.namelist() if a.read(n)!=b.read(n)]
 assert all(n.endswith('.class') or '/lang/' in n for n in changed)
 assert all(a.read(n)==b.read(n) for n in a.namelist() if n.startswith('data/') or 'mixins' in n)
report={'input_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'language_key_counts':{n:len(d) for n,d in langs.items()},'changed_entries':changed,'minecraft_runtime_tested':False}
(r/'validation.json').write_text(json.dumps(report,indent=2)+'\n');print(out)
