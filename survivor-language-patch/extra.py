from pathlib import Path
import json
r=Path(__file__).resolve().parent
# Spanish source | English | German; {ARG} denotes a Java string-concat argument.
rows='''Elige dónde sobrevivir|Choose where to survive|Wähle deinen Überlebensort
M O S S L O R N  /  ELIGE TU ZONA|M O S S L O R N  /  CHOOSE YOUR AREA|M O S S L O R N  /  WÄHLE DEIN GEBIET
Mapa no disponible|Map unavailable|Karte nicht verfügbar
COMENZAR EN ESTA ZONA|START IN THIS AREA|IN DIESEM GEBIET STARTEN
1–9 / rueda: zona   Enter: comenzar|1–9 / wheel: area   Enter: start|1–9 / Mausrad: Gebiet   Eingabe: Start
Archivo de perfiles inválido|Invalid profile file|Ungültige Profildatei
Elige un perfil guardado|Choose a saved profile|Wähle ein gespeichertes Profil
Elige una profesión y rasgos|Choose a profession and traits|Wähle einen Beruf und Eigenschaften
La profesión guardada ya no existe|The saved profession no longer exists|Der gespeicherte Beruf existiert nicht mehr
Nombre: entre 1 y 64 caracteres|Name: between 1 and 64 characters|Name: zwischen 1 und 64 Zeichen
Perfil eliminado|Profile deleted|Profil gelöscht
Perfil guardado|Profile saved|Profil gespeichert
Último personaje|Last character|Letzter Charakter
Sin efecto activo|No active effect|Kein aktiver Effekt
Sin mejoras seleccionadas|No bonuses selected|Keine Boni ausgewählt
Estamina máxima +15% adicional|Additional maximum stamina +15%|Zusätzliche maximale Ausdauer +15%
XP de habilidades +20%|Skill XP +20%|Fähigkeits-EP +20%
XP de habilidades −20%|Skill XP −20%|Fähigkeits-EP −20%
Audición|Hearing|Gehör
Sonidos atenuados|Reduced sound volume|Gedämpfte Geräusche
Consumo de energía|Energy consumption|Energieverbrauch
Daño recibido|Damage taken|Erlittener Schaden
Combate · Armas de fuego|Combat · Firearms|Kampf · Schusswaffen
Combate · Melee|Combat · Melee|Kampf · Nahkampf
Contundentes cortos|Short Blunt|Kurze Schlagwaffen
Contundentes largos|Long Blunt|Lange Schlagwaffen
Hojas cortas|Short Blade|Kurze Klingen
Hojas largas|Long Blade|Lange Klingen
Puntería|Aiming|Zielen
HAS MUERTO|YOU DIED|DU BIST GESTORBEN
Clic izquierdo para continuar|Left click to continue|Linksklick zum Fortfahren
Sin lesiones registradas|No recorded injuries|Keine Verletzungen erfasst
Sin registro|No record|Kein Eintrag
Tórax|Torso|Rumpf
Brazo derecho|Right arm|Rechter Arm
Brazo izquierdo|Left arm|Linker Arm
Pierna derecha|Right leg|Rechtes Bein
Pierna izquierda|Left leg|Linkes Bein
Pie derecho|Right foot|Rechter Fuß
Pie izquierdo|Left foot|Linker Fuß
Asfixia registrada|Recorded suffocation|Erstickung erfasst
Hemorragia / shock hipovolémico probable|Likely bleeding / hypovolemic shock|Wahrscheinliche Blutung / hypovolämischer Schock
Hemorragia fatal registrada|Recorded fatal bleeding|Tödliche Blutung erfasst
Lesión térmica fatal|Fatal thermal injury|Tödliche thermische Verletzung
Mecanismo no determinado|Undetermined mechanism|Ungeklärter Mechanismus
Paro cardíaco registrado|Recorded cardiac arrest|Herzstillstand erfasst
Traumatismo grave probable|Likely severe trauma|Wahrscheinliches schweres Trauma
Traumatismo penetrante probable|Likely penetrating trauma|Wahrscheinliches penetrierendes Trauma
Residencial noroeste|Northwest residential area|Wohngebiet im Nordwesten
Residencial noreste|Northeast residential area|Wohngebiet im Nordosten
Ciudad suroeste|Southwest city|Stadt im Südwesten
Ciudad sureste|Southeast city|Stadt im Südosten
Periferia de la central|Power plant outskirts|Randgebiet des Kraftwerks
Franja residencial oeste|Western residential strip|Westlicher Wohngebietsstreifen
Extremo noreste|Northeastern outskirts|Äußerster Nordosten
Esquina suroeste|Southwestern corner|Südwestliche Ecke
Extremo sureste|Southeastern outskirts|Äußerster Südosten
Básico|Basic|Grundlegend
Básico–variado|Basic–varied|Grundlegend–vielfältig
Doméstico y materiales|Household supplies and materials|Haushaltsvorräte und Materialien
Valioso cerca de la planta|Valuable near the plant|Wertvoll nahe dem Kraftwerk
Casas e interiores: comida, agua y provisiones domésticas. Puntos aleatorios en distintas plantas.|Houses and interiors: food, water and household supplies. Random locations on different floors.|Häuser und Innenräume: Nahrung, Wasser und Haushaltsvorräte. Zufällige Orte auf verschiedenen Stockwerken.
Viviendas del este: alimentos, ropa y herramientas básicas. Interiores aleatorios cerca de contenedores.|Eastern homes: food, clothing and basic tools. Random interiors near containers.|Östliche Wohnhäuser: Nahrung, Kleidung und einfache Werkzeuge. Zufällige Innenräume nahe Behältern.
Edificios urbanos: provisiones, herramientas y material de oficina; mayor exposición a zombis al salir.|City buildings: supplies, tools and office materials; greater exposure to zombies outside.|Stadtgebäude: Vorräte, Werkzeuge und Büromaterial; draußen größere Gefahr durch Zombies.
Edificios y oficinas: suministros variados y medicamentos según el contenedor. Plantas elegidas al azar.|Buildings and offices: assorted supplies and medicine depending on the container. Randomly selected floors.|Gebäude und Büros: verschiedene Vorräte und Medikamente je nach Behälter. Zufällig gewählte Stockwerke.
Refugios al borde de la central nuclear del noroeste. Zona muy peligrosa; materiales y herramientas valiosos en el sector nuclear.|Shelters at the edge of the northwestern nuclear power plant. Very dangerous area; valuable materials and tools in the nuclear sector.|Unterkünfte am Rand des nordwestlichen Kernkraftwerks. Sehr gefährliches Gebiet; wertvolle Materialien und Werkzeuge im Kernkraftwerksbereich.
Edificios del borde occidental, entre ambos sectores. Comida, ropa y herramientas según el contenedor; rutas alternativas hacia el centro.|Buildings on the western edge, between both sectors. Food, clothing and tools depending on the container; alternative routes to the center.|Gebäude am westlichen Rand zwischen beiden Sektoren. Nahrung, Kleidung und Werkzeuge je nach Behälter; alternative Wege ins Zentrum.
Interiores más alejados al noreste, próximos al sector de la pirámide. Provisiones y materiales; explorar grandes estructuras aumenta el riesgo.|Remote northeastern interiors near the pyramid sector. Supplies and materials; exploring large structures increases the risk.|Abgelegene Innenräume im Nordosten nahe dem Pyramidensektor. Vorräte und Materialien; große Bauwerke zu erkunden erhöht das Risiko.
Interiores del extremo suroeste, lejos del norte residencial. Provisiones domésticas y de oficina; salida urbana peligrosa.|Interiors in the far southwest, away from the northern residential area. Household and office supplies; a dangerous route into the city.|Innenräume im äußersten Südwesten, fern vom nördlichen Wohngebiet. Haushalts- und Bürovorräte; gefährlicher Weg in die Stadt.
Edificios del extremo sureste. Comida, materiales y suministros de oficina; las recompensas dependen de las tablas de cada contenedor.|Buildings in the far southeast. Food, materials and office supplies; rewards depend on each container's loot table.|Gebäude im äußersten Südosten. Nahrung, Materialien und Bürovorräte; die Beute hängt von der Tabelle jedes Behälters ab.
La casa dejó de estar segura; se conserva tu posición actual.|The house is no longer safe; your current position is preserved.|Das Haus ist nicht mehr sicher; deine aktuelle Position bleibt erhalten.
La selección de casas está preparada para Mosslorn en el Overworld.|House selection is configured for Mosslorn in the Overworld.|Die Hausauswahl ist für Mosslorn in der Oberwelt eingerichtet.
No hay una casa segura disponible en esa zona. Elige otra zona.|No safe house is available in that area. Choose another area.|In diesem Gebiet ist kein sicheres Haus verfügbar. Wähle ein anderes Gebiet.
Protección de aparición: 10 segundos — Velocidad II|Spawn protection: 10 seconds — Speed II|Startschutz: 10 Sekunden — Geschwindigkeit II
Terminó la protección de aparición|Spawn protection has ended|Der Startschutz ist abgelaufen
Zona de aparición inválida.|Invalid spawn area.|Ungültiges Startgebiet.
Daño cuerpo a cuerpo +%.1f; vida máxima +%d; carga +%d kg|Melee damage +%.1f; maximum health +%d; carry capacity +%d kg|Nahkampfschaden +%.1f; maximale Gesundheit +%d; Tragkraft +%d kg
Espera −%d%%; suerte +%.1f; pez extra %d%%|Wait −%d%%; luck +%.1f; extra fish %d%%|Wartezeit −%d%%; Glück +%.1f; zusätzlicher Fisch %d%%
Estamina máxima +%d%%; armadura +%.1f; vida máxima +%d; carga +%.1f kg|Maximum stamina +%d%%; armor +%.1f; maximum health +%d; carry capacity +%.1f kg|Maximale Ausdauer +%d%%; Rüstung +%.1f; maximale Gesundheit +%d; Tragkraft +%.1f kg
Estamina máxima +%d%%; capacidad de carga +%.1f kg|Maximum stamina +%d%%; carry capacity +%.1f kg|Maximale Ausdauer +%d%%; Tragkraft +%.1f kg
Saturación al comer +%.1f|Meal saturation +%.1f|Sättigung beim Essen +%.1f
Error de lectura: {ARG}|Read error: {ARG}|Lesefehler: {ARG}
No se pudo guardar el último perfil: {ARG}|Could not save the last profile: {ARG}|Das letzte Profil konnte nicht gespeichert werden: {ARG}
Rasgo no disponible: {ARG}|Trait unavailable: {ARG}|Eigenschaft nicht verfügbar: {ARG}
Nivel {ARG}|Level {ARG}|Stufe {ARG}
Peligro: {ARG} / Loot: {ARG}|Danger: {ARG} / Loot: {ARG}|Gefahr: {ARG} / Beute: {ARG}
Daño +{ARG}%; crítico +{ARG}%|Damage +{ARG}%; critical +{ARG}%|Schaden +{ARG}%; kritischer Treffer +{ARG}%
Desgaste evitado: {ARG}% por punto|Wear prevented: {ARG}% per point|Vermiedener Verschleiß: {ARG}% pro Punkt
Dispersión −{ARG}%|Spread −{ARG}%|Streuung −{ARG}%
Posibilidad de cosecha extra +{ARG}%|Extra harvest chance +{ARG}%|Chance auf zusätzliche Ernte +{ARG}%
Tiempo de recarga −{ARG}%|Reload time −{ARG}%|Nachladezeit −{ARG}%
Tiempo de tratamiento −{ARG}%; curación +{ARG}%|Treatment time −{ARG}%; healing +{ARG}%|Behandlungszeit −{ARG}%; Heilung +{ARG}%
Velocidad de trabajo +{ARG}%|Work speed +{ARG}%|Arbeitsgeschwindigkeit +{ARG}%
Causa de muerte: {ARG}|Cause of death: {ARG}|Todesursache: {ARG}
Tiempo sobrevivido: {ARG}|Time survived: {ARG}|Überlebenszeit: {ARG}
Zombis abatidos: {ARG}|Zombies killed: {ARG}|Getötete Zombies: {ARG}
Búsqueda: {ARG} · +{ARG} hallazgos · +{ARG} suerte · mejores rarezas · +{ARG} unidades/hallazgo · -{ARG}% desgaste de herramientas|Scavenging: {ARG} · +{ARG} finds · +{ARG} luck · better rarities · +{ARG} items/find · -{ARG}% tool wear|Plündern: {ARG} · +{ARG} Funde · +{ARG} Glück · bessere Seltenheiten · +{ARG} Gegenstände/Fund · -{ARG}% Werkzeugverschleiß'''
langs={l:json.loads((r/'lang'/f'{l}.json').read_text()) for l in ['es_es','en_us','de_de']};mapping={}
for i,line in enumerate(rows.splitlines()):
 values=line.split('|');assert len(values)==3,line
 source,en,de=values;source=source.replace('{ARG}','\1');key='survivorcreator.localized.'+str(i)
 mapping[source]=key
 for lang,v in zip(['es_es','en_us','de_de'],[source,en.replace('{ARG}','\1'),de.replace('{ARG}','\1')]):
  if '\1' in source:v=v.replace('%','%%').replace('\1','%s')
  langs[lang][key]=v
(r/'mapping.json').write_text(json.dumps(mapping,ensure_ascii=False,indent=2)+'\n')
for l,d in langs.items():(r/'lang'/f'{l}.json').write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
print(len(mapping),'hardcoded text templates')
more=[('Bajo','Low','Niedrig'),('Bajo–medio','Low–moderate','Niedrig–mittel'),('Alto','High','Hoch'),('Extremo','Extreme','Extrem'),('Medio','Moderate','Mittel'),('Variado','Varied','Vielfältig'),('Corredor','Runner','Läufer'),('Aprendizaje','Learning','Lernen'),('CRÍTICO','CRITICAL','KRITISCH'),('infección','infection','Infektion'),('contaminación','contamination','Verunreinigung')]
for i,(source,en,de) in enumerate(more):
 key='survivorcreator.localized.extra'+str(i);mapping[source]=key
 for lang,val in zip(['es_es','en_us','de_de'],[source,en,de]):langs[lang][key]=val
import base64
(r/'mapping.tsv').write_text(''.join(base64.b64encode(s.encode()).decode()+'\t'+k+'\n' for s,k in mapping.items()))
(r/'mapping.json').write_text(json.dumps(mapping,ensure_ascii=False,indent=2)+'\n')
for l,d in langs.items():(r/'lang'/f'{l}.json').write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
for k,v in langs['es_es'].items():
 if k.startswith('skill.survivorcreator.') and not '.' in k[len('skill.survivorcreator.'):]:mapping.setdefault(v,k)
for source,en,de in [('Perfil','Profile','Profil'),('Cabeza','Head','Kopf')]:
 key='survivorcreator.localized.'+('profile' if source=='Perfil' else 'head');mapping[source]=key
 for lang,val in zip(['es_es','en_us','de_de'],[source,en,de]):langs[lang][key]=val
(r/'mapping.tsv').write_text(''.join(base64.b64encode(s.encode()).decode()+'\t'+k+'\n' for s,k in mapping.items()))
(r/'mapping.json').write_text(json.dumps(mapping,ensure_ascii=False,indent=2)+'\n')
for l,d in langs.items():(r/'lang'/f'{l}.json').write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
injury='\1 · \1 lesión\1\n\1';key='survivorcreator.localized.injury_count';mapping[injury]=key
for lang,val,suffix in [('es_es','%s · %s lesión%s\n%s','es'),('en_us','%s · %s wound%s\n%s','s'),('de_de','%s · %s Verletzung%s\n%s','en')]:
 langs[lang][key]=val;langs[lang]['survivorcreator.localized.plural_suffix']=suffix
(r/'mapping.tsv').write_text(''.join(base64.b64encode(s.encode()).decode()+'\t'+k+'\n' for s,k in mapping.items()))
(r/'mapping.json').write_text(json.dumps(mapping,ensure_ascii=False,indent=2)+'\n')
for l,d in langs.items():(r/'lang'/f'{l}.json').write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
for lang,val in [('es_es','%s · %s lesiones%s\n%s'),('en_us','%s · %s wound%s\n%s'),('de_de','%s · %s Verletzung%s\n%s')]:langs[lang]['survivorcreator.localized.injury_count_plural']=val
for l,d in langs.items():(r/'lang'/f'{l}.json').write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
