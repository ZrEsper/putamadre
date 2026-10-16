from pathlib import Path
import os,subprocess,json,hashlib,zipfile
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';inputs=json.loads(Path('inputs.json').read_text());ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a):subprocess.run(list(map(str,a)),check=True)
for n,h in json.loads((root/'spas-reload18/inputs.json').read_text())['tools'].items():assert sha(t/(n+'.jar'))==h
for k,v in inputs.items():assert sha(Path('base')/v['file'])==v['sha256']
for d in ['build/tool','build/api','output']:Path(d).mkdir(parents=True,exist_ok=True)
run(*ecj,'-cp',asm,'-d','build/tool','Patch.java');run(*ecj,'-d','build/api',*Path('api').rglob('*.java'));reports={}
for k,name,hooks in [('turret','tacz_turrets-neoforge-3.0.0-1.21.1-reaction08-interval05.3.jar',0),('aid','firstaid-1.21.1-ZOMBOID-v3.6.5-blackout-defib.jar',3),('nvg','fracturepoint-1.21.1-3.0.0-PT2-digital-exposure.5.jar',0)]:
 src=Path('base')/inputs[k]['file'];classes=Path('build/classes')/k;classes.mkdir(parents=True,exist_ok=True)
 sources=list(Path('src',k).rglob('*.java'))
 if sources:run(*ecj,'-cp','build/api:'+str(src),'-d',classes,*sources)
 out=Path('output')/name;run('java','-cp','build/tool:'+asm,'Patch',src,out,classes,hooks)
 with zipfile.ZipFile(out)as z:entries={n:z.read(n)for n in z.namelist()}
 if k=='nvg':
  n='assets/fracturepoint/shaders/program/digital-white-vision.fsh';s=entries[n].decode();s=s.replace('MIN_GAIN = 1.0','MIN_GAIN = 0.25').replace('MAX_GAIN = 14.0','MAX_GAIN = 5.0').replace('TARGET_LUMA = 0.22','TARGET_LUMA = 0.18').replace('BLACK_LEVEL = 0.03','BLACK_LEVEL = 0.015').replace('gain * max(Brightness, 0.05)','gain * clamp(Brightness, 0.05, 1.0)').replace('pow(signal, 1.05)','pow(signal, 1.15)').replace('signal) * 0.3','signal) * 0.08').replace(' * NoiseAmplification;',' * NoiseAmplification * 0.55;');entries[n]=s.encode()
 if k=='aid':
  translations={
   'en_us':{'title':'Unconscious','rise':'Get up (defibrillator)','wait':'Wait for help','waiting':'Waiting for another player to revive you.','help':'You are unconscious. You need help.','reviving':'Using defibrillator...','time':'Time remaining: %s s','complete':'Revived. Treat your injuries.','interrupted':'Revival interrupted.'},
   'es_es':{'title':'Inconsciente','rise':'Levantarse (desfibrilador)','wait':'Esperar ayuda','waiting':'Esperando a que otro jugador te reanime.','help':'Estás inconsciente. Necesitas ayuda.','reviving':'Usando el desfibrilador...','time':'Tiempo restante: %s s','complete':'Reanimado. Cura tus heridas.','interrupted':'Reanimación interrumpida.'}}
  translations['es_mx']=translations['es_es']
  for locale,strings in translations.items():
   n='assets/firstaid/lang/'+locale+'.json';d=json.loads(entries.get(n,b'{}'));d.update({'firstaid.blackout.'+key:v for key,v in strings.items()});entries[n]=json.dumps(d,ensure_ascii=False).encode()
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
  for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
 with zipfile.ZipFile(src)as a,zipfile.ZipFile(out)as b:
  assert b.testzip()is None;changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));added=sorted(set(b.namelist())-set(a.namelist()));assert a.read('META-INF/neoforge.mods.toml')==b.read('META-INF/neoforge.mods.toml')
 reports[k]={'input_sha256':sha(src),'output':name,'sha256':sha(out),'changed':changed,'added':added,'minecraft_runtime_tested':False}
Path('validation.json').write_text(json.dumps(reports,indent=2)+'\n')
