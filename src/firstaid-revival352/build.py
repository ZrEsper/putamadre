from pathlib import Path
import zipfile,json
b=Path('work/revival352');src=Path('attachments/14e17716-160a-4e79-a4bc-907e9e5be769/firstaid-1.21.1-ZOMBOID-v3.5.1-revival-timer-fix.jar');out=b/'firstaid-1.21.1-ZOMBOID-v3.5.2-right-click-revival.jar';changes={str(p.relative_to(b/'patched')):p.read_bytes() for p in (b/'patched').rglob('*.class')}
translations={'en_us':{'firstaid.gui.rescue_prompt_title':'Revive %s','firstaid.gui.rescue_prompt_item':'Right-click the downed player. No item required.','firstaid.gui.rescue_prompt_crouch':'Right-click to start. Stay within 3 blocks for %s seconds.'},'es_es':{'firstaid.gui.rescue_prompt_title':'Reanimar a %s','firstaid.gui.rescue_prompt_item':'Clic derecho sobre el jugador caído. No necesitas objetos.','firstaid.gui.rescue_prompt_crouch':'Clic derecho para iniciar. Permanece a menos de 3 bloques durante %s segundos.'},'de_de':{'firstaid.gui.rescue_prompt_title':'%s wiederbeleben','firstaid.gui.rescue_prompt_item':'Rechtsklick auf den Spieler. Kein Gegenstand nötig.','firstaid.gui.rescue_prompt_crouch':'Mit Rechtsklick starten. Für %s Sekunden innerhalb von 3 Blöcken bleiben.'},'zh_cn':{'firstaid.gui.rescue_prompt_title':'复苏 %s','firstaid.gui.rescue_prompt_item':'右键点击倒地玩家，无需物品。','firstaid.gui.rescue_prompt_crouch':'右键开始，在3格内停留%s秒。'}}
with zipfile.ZipFile(src) as z:
 for lang,values in translations.items():
  n='assets/firstaid/lang/'+lang+'.json';d=json.loads(z.read(n)) if n in z.namelist() else {};d.update(values);changes[n]=(json.dumps(d,ensure_ascii=False,indent=2)+'\n').encode()
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as target:
  for info in z.infolist():
   if info.filename not in changes:target.writestr(info,z.read(info.filename))
  for n,data in changes.items():target.writestr(n,data)
with zipfile.ZipFile(out) as z:assert z.testzip() is None
print(out,out.stat().st_size)
