from pathlib import Path
import json,hashlib,zipfile,shutil
r=Path(__file__).resolve().parent;root=r.parent;reports=json.loads((r/'validation.json').read_text());tests=json.loads((r/'test-results.json').read_text());assert len(tests['checks'])==9 and all('CHECKS='in s for s in tests['checks']);assert tests['native_assets_and_medical_player_rules_preserved'];entries={}
for info in reports.values():
 p=r/'output'/info['output'];data=p.read_bytes();assert hashlib.sha256(data).hexdigest()==info['sha256'];shutil.copyfile(p,root/'mods'/p.name);entries['mods/'+p.name]=data
flash_root=root/'flashlight-battery4';flash_tests=json.loads((flash_root/'test-results.json').read_text());assert flash_tests['checks']=='FLASHLIGHT_BATTERY_CHECKS=78' and flash_tests['lifecycle_checks']=='FLASHLIGHT_SPAWN_REVIVAL_CHECKS=9'
for info in json.loads((flash_root/'validation.json').read_text()).values():
 p=flash_root/'output'/info['output'];data=p.read_bytes();assert hashlib.sha256(data).hexdigest()==info['sha256'];shutil.copyfile(p,root/'mods'/p.name);entries['mods/'+p.name]=data
compatibility=json.loads((r/'metadata-regression.json').read_text());assert compatibility['matches_inclusive_minimum'] and compatibility['sha256']==reports['corpse']['sha256'];entries['VALIDACION-COMPATIBILIDAD.json']=(r/'metadata-regression.json').read_bytes()
entries['VALIDACION-LINTERNA.json']=(flash_root/'test-results.json').read_bytes()
import re
identity=(r/'src/mod/cdv/tnpc/tactical/Identity20.java').read_text();first=re.findall(r'"([^"]+)"',re.search(r'FIRST=\{([^}]+)\}',identity).group(1));last=re.findall(r'"([^"]+)"',re.search(r'LAST=\{([^}]+)\}',identity).group(1));entries['NOMBRES-Y-APARIENCIAS.json']=json.dumps({'reference_only':True,'male_first_names':first[::2],'female_first_names':first[1::2],'last_names':last,'age_min':18,'age_max':72,'male_skins':24,'female_skins':24},ensure_ascii=False,indent=2).encode()
entries['LEEME.txt']=(r/'GUIA.txt').read_bytes();entries['VALIDACION.json']=(r/'test-results.json').read_bytes();entries['SHA256SUMS.txt']=''.join(hashlib.sha256(data).hexdigest()+'  '+name+'\n'for name,data in sorted(entries.items())).encode();out=root/'distribuciones/Zombies-Poldo-Supervivientes-20.1-Correccion-Arranque.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,data in sorted(entries.items()):info=zipfile.ZipInfo(n,(1980,1,1,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,data)
with zipfile.ZipFile(out)as z:assert z.testzip()is None and len([n for n in z.namelist()if n.endswith('.jar')])==7
(r/'package-validation.json').write_text(json.dumps({'file':str(out.relative_to(root)),'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'jars':7,'minecraft_runtime_tested':False},indent=2)+'\n');print(out.name)

# Small replacement for users who already installed release 20.
corpse=reports['corpse'];jar=r/'output'/corpse['output'];patch_entries={'mods/'+jar.name:jar.read_bytes(),'LEEME.txt':('Cierra Minecraft y el servidor.\nQuita corpse-neoforge-1.21.1-1.1.13-npc-survivor-cargo.3.jar de mods.\nCopia el nuevo Corpse .4. No dejes dos Corpse. Hazlo en clientes y servidor.\nConserva los otros mods de Supervivientes 20.\nSe restaura la version interna original 1.21.1-1.1.13 exigida por Zombie Remains.\nEl codigo, las skins de cadaveres y el inventario son identicos a Corpse .3.\n').encode()};patch_entries['VALIDACION.json']=(r/'metadata-regression.json').read_bytes();patch_entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(patch_entries.items())).encode()
patch_out=root/'distribuciones/Zombies-Poldo-Corpse-4-Correccion-Arranque.zip'
with zipfile.ZipFile(patch_out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(patch_entries.items()):info=zipfile.ZipInfo(n,(1980,1,1,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,b)
print(patch_out.name)
