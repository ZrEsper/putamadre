from pathlib import Path
import json,hashlib,zipfile,shutil
r=Path(__file__).resolve().parent;root=r.parent;v=json.loads((r/'validation.json').read_text());tests=json.loads((r/'test-results.json').read_text());assert 'NEEDS_BATHROOM_WIRE_CHECKS=41'in tests['checks'] and 'NEEDS_CLIENT_NETWORK_CHECKS=12'in tests['checks']and'NATIVE_HOOK_AND_SIDE_CHECKS=11'in tests['checks']
p=r/'output'/v['output'];data=p.read_bytes();assert hashlib.sha256(data).hexdigest()==v['sha256'];shutil.copyfile(p,root/'mods'/p.name);entries={'mods/'+p.name:data,'LEEME.txt':(r/'GUIA.txt').read_bytes()};entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(entries.items())).encode();out=root/'distribuciones/Zombies-Poldo-Doomsday-6-Necesidades-Banos.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(out)as z:assert z.testzip()is None and len([n for n in z.namelist()if n.endswith('.jar')])==1
(r/'package-validation.json').write_text(json.dumps({'file':str(out.relative_to(root)),'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'jars':1,'minecraft_runtime_tested':False},indent=2)+'\n');print(out.name)
