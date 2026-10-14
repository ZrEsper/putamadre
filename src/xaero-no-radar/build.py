from pathlib import Path
import zipfile,subprocess
b=Path('work/xaero-no-radar');flags=['--add-exports','java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED','--add-exports','java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED']
mods=[('attachments/c52e4c21-34f4-439b-96c5-358f4668feb1/xaerominimap-neoforge-1.21.1-26.6.0-pz-exploration-no-deaths.1.jar','xaerominimap-neoforge-1.21.1-26.6.0-pz-exploration-no-radar-priority.2.jar',['xaero/hud/minimap/radar/state/RadarStateUpdater','xaero/common/minimap/radar/MinimapRadar','xaero/hud/minimap/radar/render/element/RadarRenderer','xaero/common/events/ModClientEventsNeoForge']),('attachments/f206a761-91b8-49e4-8f0c-0dfa151ef522/xaeroworldmap-neoforge-1.21.1-1.47.0-pz-exploration-no-deaths.1.jar','xaeroworldmap-neoforge-1.21.1-1.47.0-pz-exploration-no-radar-priority.2.jar',['xaero/map/mods/minimap/element/RadarRendererWrapperHelper','xaero/map/radar/tracker/PlayerTrackerMapElementCollector','xaero/map/radar/tracker/PlayerTrackerMapElementRenderer'])]
outputs=[]
for source,name,classes in mods:
 with zipfile.ZipFile(source) as z:files={n:z.read(n) for n in z.namelist()}
 for cls in classes:
  p=b/'patched'/(cls+'.class');p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(files[cls+'.class']);subprocess.run(['java',*flags,'-cp',str(b/'classes'),'Patch',str(p)],check=True);files[cls+'.class']=p.read_bytes()
 out=b/name
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
  for n,data in files.items():z.writestr(n,data)
 with zipfile.ZipFile(source) as old,zipfile.ZipFile(out) as z:
  assert z.testzip() is None
  assert all(old.read(n)==z.read(n) for n in old.namelist() if n not in [c+'.class' for c in classes])
 outputs.append(out)
print('PASS archive integrity and unchanged terrain/waypoint/exploration entries')
(b/'LEEME.txt').write_text('XAERO · SIN RADAR DE ENTIDADES\nReemplaza ambos JAR de Xaero por los incluidos, solo en el cliente. Reinicia Minecraft.\nOculta radar de zombis, animales, objetos y otros jugadores en el minimapa y mapa completo. Se omite la actualizacion de listas de radar y el dibujo de iconos/puntos. El minimapa se dibuja encima de la capa de efectos del jugador.\nConserva terreno, exploracion, waypoints normales y la eliminacion previa de marcadores de muerte. La posicion propia en el mapa se conserva.\nDesactivacion fija en estos JAR, incluso si la configuracion anterior tenia radar activado.\nNo elimina ni cambia entidades del mundo. No modifica archivos del mapa guardado.\nComprobados bytecode e integridad; falta probar dentro de Minecraft. La mejora de FPS depende de la parte de carga que correspondia al radar.\n',encoding='utf-8')
with zipfile.ZipFile(b/'Xaero-sin-radar-entidades.2.zip','w',zipfile.ZIP_DEFLATED) as z:
 for p in outputs+[b/'LEEME.txt']:z.write(p,p.name)
