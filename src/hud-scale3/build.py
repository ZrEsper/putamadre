from pathlib import Path
import zipfile,json
b=Path('work/hud-scale3')
mods={'firstaid': ('work/revival352/firstaid-1.21.1-ZOMBOID-v3.5.2-right-click-revival.jar', 'firstaid-1.21.1-ZOMBOID-v3.5.3-fixed-hud-bandage-boosts.jar'), 'survivor': ('attachments/32236d8b-7bc6-4599-a78c-242b2c31cde3/survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v29-en-de.jar', 'survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v30-fixed-hud-scale3.jar')}
trans={'es_es': {'item.firstaid.bandage': 'Venda médica', 'firstaid.dressing.medical': 'Venda médica', 'firstaid.dressing.tactical': 'Venda táctica', 'firstaid.dressing.medical_boost': 'Al aplicarla: Regeneración I durante 10 s.', 'firstaid.dressing.tactical_boost': 'Al aplicarla: Regeneración I + Absorción I durante 10 s.'}, 'en_us': {'item.firstaid.bandage': 'Medical bandage', 'firstaid.dressing.medical': 'Medical bandage', 'firstaid.dressing.tactical': 'Tactical bandage', 'firstaid.dressing.medical_boost': 'On application: Regeneration I for 10 s.', 'firstaid.dressing.tactical_boost': 'On application: Regeneration I + Absorption I for 10 s.'}, 'de_de': {'item.firstaid.bandage': 'Medizinischer Verband', 'firstaid.dressing.medical': 'Medizinischer Verband', 'firstaid.dressing.tactical': 'Taktischer Verband', 'firstaid.dressing.medical_boost': 'Nach Anwendung: Regeneration I für 10 s.', 'firstaid.dressing.tactical_boost': 'Nach Anwendung: Regeneration I + Absorption I für 10 s.'}}
for key,(source,name) in mods.items():
 prefix='dev/zomboid/firstaid/' if key=='firstaid' else 'dev/survivorcreator/'
 overrides={str(p.relative_to(b/'patched')):p.read_bytes() for p in (b/'patched').rglob('*.class') if (str(p.relative_to(b/'patched')).startswith(prefix) or key=='firstaid' and str(p.relative_to(b/'patched')).startswith('ichttt/'))}
 for p in (b/'classes').rglob('*.class'):
  n=str(p.relative_to(b/'classes'))
  if n.startswith(prefix) and ('hudscale/' in n or n.endswith('/DressingBoost.class')):overrides[n]=p.read_bytes()
 with zipfile.ZipFile(source) as z:
  if key=='firstaid':
   for lang,values in trans.items():
    n='assets/firstaid/lang/'+lang+'.json';d=json.loads(z.read(n)) if n in z.namelist() else {};d.update(values);overrides[n]=(json.dumps(d,ensure_ascii=False,indent=2)+'\n').encode()
   for file,texture in [('bandage-icon-final.png','assets/firstaid/textures/item/bandage.png'),('bandage-uv-final.png','assets/firstaid/textures/consumable/carfak_bandage_uv.png')]:
    if (b/file).exists():overrides[texture]=(b/file).read_bytes()
  with zipfile.ZipFile(b/name,'w',zipfile.ZIP_DEFLATED) as out:
   for info in z.infolist():
    if info.filename not in overrides:out.writestr(info,z.read(info.filename))
   for n,data in overrides.items():out.writestr(n,data)
 with zipfile.ZipFile(b/name) as z:assert z.testzip() is None
 print(name,(b/name).stat().st_size)
