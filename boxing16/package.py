from pathlib import Path
import json,hashlib,shutil,zipfile
r=Path(__file__).resolve().parent;root=r.parent;v=json.loads((r/'validation.json').read_text());test=json.loads((r/'test-results.json').read_text());assert len(test['checks'])==2;p=r/'output'/v['output'];assert hashlib.sha256(p.read_bytes()).hexdigest()==v['sha256'];shutil.copyfile(p,root/'mods'/p.name);entries={'mods/'+p.name:p.read_bytes(),'LEEME.txt':(r/'GUIA.txt').read_bytes(),'VALIDACION.json':(r/'test-results.json').read_bytes(),'ORIGEN-ANIMACIONES.json':(r/'asset-provenance.json').read_bytes(),'AVISO-ASSETS-EPIC-FIGHT.txt':(r/'assets/LICENSE-ASSETS-EpicFight.txt').read_bytes()};entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(entries.items())).encode();out=root/'distribuciones/Zombies-Poldo-Boxeo-Tactico-16.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(out)as z:assert z.testzip()is None
(r/'package-validation.json').write_text(json.dumps({'file':str(out.relative_to(root)),'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'minecraft_runtime_tested':False},indent=2)+'\n');print(out.name)
