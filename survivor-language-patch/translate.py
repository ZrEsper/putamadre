import json,zipfile
from pathlib import Path
r=Path(__file__).resolve().parent
base=r/'base.jar'
z=zipfile.ZipFile(base);en=json.loads(z.read('assets/survivorcreator/lang/en_us.json'));es=json.loads(z.read('assets/survivorcreator/lang/es_es.json'))
fix={'axe':'Axes','long_blunt':'Long Blunt','short_blunt':'Short Blunt','long_blade':'Long Blade','short_blade':'Short Blade','spear':'Spears','maintenance':'Maintenance','aiming':'Aiming','reloading':'Reloading'}
for k,v in fix.items():en['skill.survivorcreator.'+k]=v
for k,v in zip(['loadout','boosts','profiles','profile_help','profile_name','profile_save','profile_load','profile_delete','profile_back'],['Selection and starting items','Character bonuses','Profiles: save / load','Choose a profile to load, or enter a name to save.','Profile name','Save','Load','Delete','Back']):en['screen.survivorcreator.'+k]=v
# Ordered against the original language keys; validation ensures complete coverage.
de_lines='''ÜBERLEBENDEN ERSTELLEN
Verfügbare Punkte: %s
BERUF
VORTEILE
NACHTEILE
AUSGEWÄHLT
EIGENSCHAFTSDETAILS
BERUFSDETAILS
Wähle einen Beruf, um seine Details zu sehen.
BONI
ENTHALTEN
UNVEREINBAR MIT
Benötigt: %s
Konflikt: %s
Kosten: -%s Punkte
Bonus: +%s Punkte
Kosten: -%s Punkte
Gewinn: +%s Punkte
(Beruf)
Vorteile %s/%s · Nachteile %s/%s
ZURÜCK
SCHLIESSEN
ZURÜCKSETZEN
ZUFÄLLIG
BESTÄTIGEN
Charakter wird gespeichert...
Dein Charakter wurde erstellt.
Du musst einen Beruf wählen.
Dein Charakter wurde bereits bestätigt.
Unbekannter Beruf: %s
Unbekannte Eigenschaft: %s
Diese Eigenschaft kann nicht wiederholt werden: %s
Diese Eigenschaften widersprechen sich: %s und %s
Diese Eigenschaft benötigt: %s
Diese Eigenschaft benötigt einen passenden Beruf: %s
Dein Beruf ist unvereinbar mit: %s
Zu viele Vorteile. Maximum: %s
Zu viele Nachteile. Maximum: %s
Mit negativen Punkten kannst du nicht bestätigen.
Du hast nicht genügend Punkte.
Geladene Berufe: %s
Geladene Eigenschaften: %s
Spieler: %s · abgeschlossen: %s · Beruf: %s · Eigenschaften: %s · Punkte: %s
Charaktererstellung für %s zurückgesetzt.
PRÜFEN
CHARAKTERÜBERSICHT
BERUF
EIGENSCHAFTEN
Gehe zurück, um die Auswahl zu ändern.
ÜBERLEBENDER
STARTAUSRÜSTUNG
Keine zusätzliche Ausrüstung.
Wähle, wer du warst, bevor alles endete.
DIES SIND DIE LETZTEN TAGE
ES GAB KEINE HOFFNUNG AUF ÜBERLEBEN
SO WIRST DU STERBEN
Zum Starten klicken
Fitness
Stärke
Sprinten
Leichtfüßigkeit
Gewandtheit
Schleichen
Äxte
Lange Schlagwaffen
Kurze Schlagwaffen
Lange Klingen
Kurze Klingen
Speere
Wartung
Tischlerei
Kochen
Landwirtschaft
Erste Hilfe
Elektrik
Metallbearbeitung
Mechanik
Schneiderei
Zielen
Nachladen
Angeln
Fallenstellen
Sammeln
Passiv
Beweglichkeit
Kampf
Handwerk
Schusswaffen
Überleben
%s hat Stufe %s erreicht!
Verbessert: %s
Verteidigung +%s (gesamt +%s)
Nahkampfschaden +%s (gesamt +%s)
STUFENAUFSTIEG!
%s · St. %s
Fähigkeiten von %s:
%s  St. %s/%s  (EP %s/%s)  %s
%s  St. %s (MAX)  %s
%s  (noch nicht umgesetzt)
Unbekannte oder inaktive Fähigkeit: %s
%s EP in %s für %s hinzugefügt.
Dieser Spieler hat seinen Charakter noch nicht bestätigt.
DU BIST GESTORBEN
Hier endete deine Geschichte.
Zum Fortfahren klicken
Dein Überlebender ist gestorben. Beruf, Eigenschaften, Stufen und EP wurden zurückgesetzt.
Arbeitsgeschwindigkeit mit Werkzeugen +%s%%
Zusätzliche Sättigung pro Mahlzeit: +%s
Chance auf zusätzliche Ernte: %s%%
Zusätzliche Tragkraft: +%s kg
Praktischer Fortschritt: Stufe %s
Übung erfasst; noch kein zusätzlicher Bonus umgesetzt.
Taubheit
%s EP bis zur nächsten Stufe
Maximalstufe
Unbekannte Todesursache
Nicht verfügbar
Aktive Verletzungen (Körperbereiche): %s
Verletzte Körperbereiche: %s
Überlebenszeit: %s
Linksklick zum Fortfahren
Auswahl und Startgegenstände
Charakterboni
Profile: speichern / laden
Wähle ein Profil zum Laden oder gib einen Namen zum Speichern ein.
Profilname
Speichern
Laden
Löschen
Zurück
Plündern
Todesursache: %s
Tödlicher Schaden: %s
Körperzustand
Alle Verletzungen
Keine Verletzungen erfasst
Getötete Zombies: %s
Getötete Überlebende: %s
Mausrad: mehr anzeigen
Verbände / Schienen
verbunden
geschient
Kopf
Rumpf
Linker Arm
Rechter Arm
Linkes Bein
Rechtes Bein
Linker Fuß
Rechter Fuß
Schusswunde
Steckende Kugeln
Schnittwunde
Biss
Infektion
Verschmutzte Wunde
Bruch
Blutung
Schaden an diesem Bereich
Schaden ohne bestimmte Quelle
Allgemeine Tötung
Kreatur
Spieler
Sturz
Im Feuer
In Flammen
Ertrinken
Verhungern
In einer Wand
Außerhalb der Welt
Magie
Indirekte Magie
Explosion
Kreaturenprojektil
Pfeil
Dreizack
Erfrieren
Wither
Kaktus
Süßbeerenstrauch
Heißer Boden
Erdrückung
Flug gegen eine Wand
Blitzschlag
Lava'''.splitlines()
assert len(de_lines)==len(en),(len(de_lines),len(en))
de=dict(zip(en,de_lines))
# ID | English name | German name | English description | German description
rows='''angler|Amateur Angler|Hobbyangler|Fishing +2 and a fishing rod at the start; shorter waits and better fishing chances.|Angeln +2 und eine Angelrute zum Start; kürzere Wartezeiten und bessere Fangchancen.
asthmatic|Asthmatic|Asthmatiker|Running, jumping and carrying weight take a greater toll. A bad combination with hordes.|Laufen, Springen und das Tragen von Lasten belasten dich stärker. Eine schlechte Kombination mit Horden.
athletic|Athletic|Athletisch|You handle exertion better: ideal for running, jumping and fighting without tiring as quickly.|Du verkraftest Anstrengung besser: ideal zum Laufen, Springen und Kämpfen, ohne so schnell zu ermüden.
bad_aim|Poor Aim|Schlechter Schütze|You struggle to control weapons and react accurately.|Du hast Schwierigkeiten, Waffen zu kontrollieren und präzise zu reagieren.
brave|Brave|Mutig|You stay calm under pressure. Better suited to combat professions.|Du bleibst unter Druck ruhig. Besonders geeignet für Kampfberufe.
brawler|Street Brawler|Straßenschläger|Short Blunt +2, Strength +1 and a small increase to knockback when hitting.|Kurze Schlagwaffen +2, Stärke +1 und etwas mehr Rückstoß bei Treffern.
clumsy|Clumsy|Ungeschickt|Poorer body control. You struggle to maneuver in close combat.|Schlechtere Körperbeherrschung. Du hast Schwierigkeiten, dich im Nahkampf zu bewegen.
easily_exhausted|Easily Exhausted|Schnell erschöpft|Uses 18% more energy during exertion.|Verbraucht bei Anstrengung 18% mehr Energie.
elusive|Elusive|Unauffällig|Your footsteps are audible over 45% less distance, even when running. Crouching further reduces noise. Does not silence gunfire or explosions.|Deine Schritte sind 45% weniger weit hörbar, auch beim Rennen. Schleichen verringert den Lärm zusätzlich. Schüsse und Explosionen werden nicht gedämpft.
fast|Fast|Schnell|You move a little faster, with a moderate bonus to keep chases balanced.|Du bewegst dich etwas schneller. Der moderate Bonus hält Verfolgungen ausgeglichen.
fast_learner|Fast Learner|Schneller Lerner|Gains 20% more skill XP and keeps a small luck bonus.|Erhält 20% mehr Fähigkeits-EP und behält einen kleinen Glücksbonus.
fearful|Fearful|Ängstlich|Panic makes you miss and take damage more severely.|Panik lässt dich verfehlen und Schaden schlechter verkraften.
field_medic|Field Medic|Feldsanitäter|First Aid +2. Starts with paper and honey; levels improve treatment and healing.|Erste Hilfe +2. Startet mit Papier und Honig; höhere Stufen verbessern Behandlung und Heilung.
first_aid|First Aid|Erste Hilfe|You know how to stabilize wounds and survive after taking damage.|Du weißt, wie man Wunden stabilisiert und nach Verletzungen überlebt.
fragile|Fragile|Gebrechlich|Wounds affect you more. Very dangerous with First Aid and fractures.|Wunden wirken sich stärker aus. Besonders gefährlich mit First Aid und Knochenbrüchen.
fragile_bones|Fragile Bones|Brüchige Knochen|You take 12% more damage. Cannot be combined with Tough.|Du erleidest 12% mehr Schaden. Nicht mit Robust kombinierbar.
good_aim|Good Shot|Guter Schütze|You control firearms and ranged combat better. Provides little improvement to melee.|Du beherrschst Schusswaffen und Fernkampf besser. Verbessert den Nahkampf nur wenig.
handy|Handy|Handwerklich begabt|Better prepared for tools, repairs and maintenance.|Besser auf Werkzeuge, Reparaturen und Wartung vorbereitet.
hard_of_hearing|Hard of Hearing|Schwerhörig|Deafness II: sounds play at 50% of their normal volume.|Taubheit II: Geräusche werden mit 50% ihrer normalen Lautstärke wiedergegeben.
hungry|Quick to Hunger|Schnell hungrig|You need food more often.|Du brauchst häufiger Nahrung.
lucky|Lucky|Glückspilz|Better overall chances without making loot too easy.|Bessere allgemeine Chancen, ohne die Beute zu leicht zugänglich zu machen.
marksman|Trained Marksman|Ausgebildeter Schütze|Aiming +2 and Reloading +1 at the start. Reduces spread and reload time.|Zielen +2 und Nachladen +1 zum Start. Verringert Streuung und Nachladezeit.
organized|Organized|Organisiert|You start with organized supplies. Good for builds with limited weight or inventory.|Du startest mit geordneten Vorräten. Gut bei begrenztem Traggewicht oder Inventar.
outdoorsman|Outdoorsman|Naturbursche|You adapt better to long trips, hunger, fatigue and life outside shelters.|Du passt dich besser an lange Reisen, Hunger, Müdigkeit und das Leben außerhalb von Unterkünften an.
poor_fighter|Inexperienced Fighter|Unerfahrener Kämpfer|Removes one starting level of Strength and Short Blunt; melee damage -0.5.|Stärke und Kurze Schlagwaffen starten eine Stufe niedriger; Nahkampfschaden -0,5.
ready_for_trouble|Ready for Trouble|Auf Ärger vorbereitet|With OK Additions, starts with a light shield and two stone bolas; also brings three loaves of bread.|Mit OK Additions: ein leichter Schild und zwei Steinbolas; außerdem drei Brote.
resourceful|Resourceful|Improvisationstalent|Maintenance +1 and, with OK Additions, a crowbar and tool lubricant.|Wartung +1 und mit OK Additions eine Brechstange und Werkzeugschmiermittel.
runner|Runner|Läufer|Fitness +1 and Sprinting +2; an additional 15% maximum stamina and 10% less food energy consumption.|Fitness +1 und Sprinten +2; zusätzlich 15% maximale Ausdauer und 10% weniger Verbrauch an Nahrungsenergie.
scavenger|Scavenger|Plünderer|Maintenance +1, Luck +0.25 and a bundle to organize your finds.|Wartung +1, Glück +0,25 und ein Bündel zum Ordnen deiner Funde.
sedentary|Sedentary|Bewegungsmuffel|Fitness and Sprinting start one level lower; you use 10% more energy. Levels never drop below zero.|Fitness und Sprinten starten eine Stufe niedriger; du verbrauchst 10% mehr Energie. Stufen sinken nie unter null.
shaky_hands|Shaky Hands|Zittrige Hände|Aiming and Reloading start one level lower; attack speed is reduced by 0.15.|Zielen und Nachladen starten eine Stufe niedriger; Angriffsgeschwindigkeit wird um 0,15 verringert.
slow|Slow|Langsam|You move more slowly, making it harder to escape hordes.|Du bewegst dich langsamer und entkommst Horden schwerer.
slow_healer|Slow Healer|Langsame Heilung|You recover more slowly; every wound matters more.|Du erholst dich langsamer; jede Wunde fällt stärker ins Gewicht.
slow_learner|Slow Learner|Langsamer Lerner|Gains 20% less skill XP.|Erhält 20% weniger Fähigkeits-EP.
strong|Strong|Stark|You hit harder and carry weight better, without making melee weapons overpowered.|Du schlägst härter zu und trägst Lasten besser, ohne Nahkampfwaffen übermächtig zu machen.
thirsty|Quick to Thirst|Schnell durstig|Your body uses fluids faster; dangerous with Tough As Nails or Homeostatic.|Dein Körper verbraucht Flüssigkeit schneller; gefährlich mit Tough As Nails oder Homeostatic.
tough|Tough|Robust|You withstand physical punishment better without becoming invincible.|Du verkraftest körperliche Belastung besser, ohne unbesiegbar zu werden.
unlucky|Unlucky|Pechvogel|Bad coincidences follow you.|Unglückliche Zufälle verfolgen dich.
weak|Weak|Schwach|You tire more easily and your melee blows have less force.|Du ermüdest leichter und deine Nahkampfschläge sind schwächer.
blacksmith|Blacksmith|Schmied|Metal, tools and workshop strength.|Metall, Werkzeuge und Kraft für die Werkstatt.
burger_flipper|Fast Food Worker|Fast-Food-Mitarbeiter|Some cooking, few supplies and a low cost.|Etwas Kochen, wenige Vorräte und geringe Kosten.
burglar|Burglar|Einbrecher|Fast, discreet and good at getting in and out unseen.|Schnell, unauffällig und gut darin, ungesehen hinein- und hinauszugelangen.
butcher|Butcher|Metzger|Short Blade +2 and Cooking +2. A cutting tool and food, without firearms training.|Kurze Klingen +2 und Kochen +2. Schneidewerkzeug und Nahrung, ohne Schusswaffenausbildung.
carpenter|Carpenter|Tischler|Construction and basic repairs.|Bauen und grundlegende Reparaturen.
chef|Cook|Koch|Knows how to manage food and prepare supplies.|Kann Nahrung verwalten und Vorräte vorbereiten.
construction_worker|Construction Worker|Bauarbeiter|Physical strength and endurance for heavy work.|Körperliche Kraft und Ausdauer für schwere Arbeit.
delivery_worker|Delivery Worker|Lieferfahrer|Sprinting +2, the Fast trait, a compass and food; little combat equipment.|Sprinten +2, die Eigenschaft Schnell, ein Kompass und Nahrung; wenig Kampfausrüstung.
diy_expert|DIY Expert|Heimwerker|Home repairs, tools and quick solutions.|Reparaturen zu Hause, Werkzeuge und schnelle Lösungen.
doctor|Doctor|Arzt|Specialist in trauma, wounds and medical survival.|Spezialist für Traumata, Wunden und medizinisches Überleben.
electrician|Electrician|Elektriker|Handling electrical systems and technical supplies.|Umgang mit elektrischen Anlagen und technischen Vorräten.
engineer|Engineer|Ingenieur|Planning, structures and fast learning.|Planung, Konstruktionen und schnelles Lernen.
farmer|Farmer|Landwirt|Rural survival and food production.|Überleben auf dem Land und Nahrungsproduktion.
fire_officer|Firefighter|Feuerwehrmann|Tough, brave and prepared for dangerous situations.|Robust, mutig und auf gefährliche Situationen vorbereitet.
fisherman|Professional Fisherman|Berufsfischer|Fishing +3 and Cooking +1. A rod, fish and an improvised knife.|Angeln +3 und Kochen +1. Eine Angelrute, Fisch und ein improvisiertes Messer.
fishing_guide|Fishing Guide|Angelführer|Knows emergency food and outdoor living.|Kennt Notnahrung und das Leben im Freien.
fitness_instructor|Fitness Instructor|Fitnesstrainer|Excellent physical fitness and endurance.|Hervorragende körperliche Fitness und Ausdauer.
hunter|Hunter|Jäger|Aiming +2, Reloading +1 and Fishing +1. Brings a bow, arrows and food for the first trips.|Zielen +2, Nachladen +1 und Angeln +1. Bringt Bogen, Pfeile und Nahrung für die ersten Ausflüge mit.
lumberjack|Lumberjack|Holzfäller|Strong and used to axes and physical exertion.|Stark und an Äxte und körperliche Anstrengung gewöhnt.
maintenance_worker|Maintenance Worker|Wartungstechniker|Maintenance +3 and Carpentry +1; simple tools and materials for a shelter.|Wartung +3 und Tischlerei +1; einfache Werkzeuge und Materialien für eine Unterkunft.
mechanic|Mechanic|Mechaniker|Knows how to repair, improvise and make use of parts.|Kann reparieren, improvisieren und Teile wiederverwenden.
musician|Musician|Musiker|Not a fighter: some food and OK Additions instruments to play during breaks.|Kein Kämpfer: etwas Nahrung und Instrumente aus OK Additions zum Spielen in den Pausen.
nurse|Nurse|Pflegekraft|Practical care and emergency first aid.|Praktische Pflege und Erste Hilfe im Notfall.
paramedic|Paramedic|Rettungssanitäter|Aid under pressure: First Aid +2 and Fitness +1, plus the First Aid trait.|Hilfe unter Druck: Erste Hilfe +2 und Fitness +1 sowie die Eigenschaft Erste Hilfe.
park_ranger|Park Ranger|Parkwächter|Navigation, routes and survival outside the city.|Orientierung, Routen und Überleben außerhalb der Stadt.
police_officer|Police Officer|Polizist|Weapons training, discipline and threat control.|Waffenausbildung, Disziplin und Kontrolle von Bedrohungen.
rancher|Rancher|Viehzüchter|Rural life, animals and outdoor endurance.|Landleben, Tiere und Ausdauer im Freien.
scout|Scout|Späher|Aiming +1 and Fitness +1. With OK Additions, brings binoculars, a marker and a stone tomahawk.|Zielen +1 und Fitness +1. Mit OK Additions: Fernglas, Markierer und Steintomahawk.
scrap_worker|Scrap Worker|Schrottsammler|Metalworking +2 and Maintenance +2. Salvages and works metal with basic equipment.|Metallbearbeitung +2 und Wartung +2. Gewinnt und bearbeitet Metall mit einfacher Ausrüstung.
security_guard|Security Guard|Sicherheitswächter|Used to patrols, watch duty and tension.|An Patrouillen, Wachdienst und Anspannung gewöhnt.
supply_runner|Supply Runner|Versorgungsläufer|Sprinting +2 and Maintenance +1. Prioritizes mobility and organized carrying.|Sprinten +2 und Wartung +1. Setzt auf Beweglichkeit und geordnetes Tragen.
tailor|Tailor|Schneider|Clothing, light equipment and managing materials.|Kleidung, leichte Ausrüstung und Materialverwaltung.
unemployed|Unemployed|Arbeitslos|No formal training. Starts with more points but no free advantages.|Keine formale Ausbildung. Startet mit mehr Punkten, aber ohne kostenlose Vorteile.
veteran|Veteran Soldier|Veteran|Combat experience. Mentally strong, but not overpowered at the start.|Kampferfahrung. Mental stark, aber zum Start nicht übermächtig.
welder|Welder|Schweißer|Heavy metalwork, protection and tools.|Schwere Metallarbeit, Schutz und Werkzeuge.'''
translations={line.split('|')[0]:line.split('|')[1:] for line in rows.splitlines()}
for n in z.namelist():
 if n.startswith('data/survivorcreator/survivor/') and n.endswith('.json'):
  data=json.loads(z.read(n));kind='perk' if '/perks/' in n else 'profession';id=Path(n).stem
  if 'name' not in data:continue
  a,b,c,d=translations[id]
  for field,ev,dv in [('name',a,b),('description',c,d)]:
   k=kind+'.survivorcreator.'+id+('' if field=='name' else '.'+field);en[k]=ev;de[k]=dv;es[k]=data[field]
for lang,data in [('en_us',en),('de_de',de),('es_es',es)]:
 (r/'lang'/f'{lang}.json').write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n')
print('Language keys:',len(en),len(de),len(es))
