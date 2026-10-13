from pathlib import Path
import zipfile,json,re
r=Path(__file__).resolve().parent
paths={'guns':'/workspace/attachments/10c1fc7d-de46-4ad9-9e48-cf94933ce131/valorant-gunpack-0.1.4-neoforge-1.21.1-block-data-fix.2.jar','knives':'/workspace/attachments/34493b85-5d3b-424f-bdc3-da85d0714690/valorant-knife-pack-0.1.0-neoforge-1.21.1.jar','armor':'/workspace/attachments/fb6b6e26-a1ff-4cfd-a2b3-a9a5645b655d/lrarmor-1.21.1-0.1.4.4-neoforge.1.jar'}
cat={'guns':[],'knives':[],'armor':[],'ammo':[],'attachments':[]}
with zipfile.ZipFile(paths['guns']) as z:
 for name in z.namelist():
  if not name.endswith('.json') or '/index/' not in name:continue
  match=re.search(r'/data/([^/]+)/index/(guns|ammo|attachments)/(.+)\.json$',name)
  if not match:continue
  ns,kind,path=match.groups();idx=json.loads(z.read(name));id=ns+':'+path
  if kind=='guns':
   dataId=idx['data'];dn,dp=dataId.split(':',1);prefix=name.split('/data/',1)[0];data=json.loads(z.read(prefix+'/data/'+dn+'/data/guns/'+dp+'.json'))
   quality={'classic':0,'ghost':1,'sheriff':1,'outlaw':2,'judge':2,'vandal':2,'phantom':2,'operator':3,'odin':3,'golden':4}[path]
   cat['guns'].append({'id':id,'ammo':data['ammo'],'capacity':data['ammo_amount'],'mode':data['fire_mode'][0].upper(),'min_quality':quality})
  else:cat[kind].append(id)
with zipfile.ZipFile(paths['knives']) as z:
 for name in z.namelist():
  m=re.search(r'/data/([^/]+)/index/melee/(.+)\.json$',name)
  if m:cat['knives'].append({'id':m[1]+':'+m[2],'base_item':json.loads(z.read(name))['base_item'],'min_quality':1 if m[2] in ['future_dagger','eternal_karambit'] else 2})
with zipfile.ZipFile(paths['armor']) as z:
 cat['armor']=sorted(n[len('assets/lrarmor/models/item/'):].removesuffix('.json') for n in z.namelist() if n.startswith('assets/lrarmor/models/item/') and n.endswith('.json'))
assert len(cat['guns'])==10 and len(cat['knives'])==6 and len(cat['armor'])==62
(r/'catalog.json').write_text(json.dumps(cat,indent=2)+'\n')
java='package dev.zomboid.remains;\npublic final class AddonCatalog {\npublic record Gun(String id,String ammo,int capacity,String mode,int quality){}\npublic record Melee(String id,String base,int quality){}\n'
java+='public static final Gun[] GUNS={'+','.join('new Gun('+json.dumps(g['id'])+','+json.dumps(g['ammo'])+','+str(g['capacity'])+','+json.dumps(g['mode'])+','+str(g['min_quality'])+')' for g in cat['guns'])+'};\n'
java+='public static final Melee[] KNIVES={'+','.join('new Melee('+json.dumps(g['id'])+','+json.dumps(g['base_item'])+','+str(g['min_quality'])+')' for g in cat['knives'])+'};\n'
java+='public static final String[] ARMOR={'+','.join(json.dumps('lrarmor:'+v) for v in cat['armor'])+'};\n}'
java=java.removesuffix('}')+'public static final String[] ATTACHMENTS={'+','.join(json.dumps(id) for id in cat['attachments'])+'};\n}'
(r/'src/dev/zomboid/remains/AddonCatalog.java').write_text(java)
