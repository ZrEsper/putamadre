# Disparos lejanos: 768 bloques normales, 128 silenciados

Tactical .4 se basa en .3. Anade un mixin comun opcional sobre `dev.zomboid.tacz.Audio.range`: shoot/shoot_3p usan 768; silence/silence_3p usan 128. Se respetan las entradas con alcance desactivado y otros sonidos; reconoce nombres posicionales pzpos. No cambia balistica ni oido de la IA. Todas las clases originales de .3 se conservan byte por byte, incluidos efectos una sola vez y marcas de 180 segundos.

La ruta nativa de TaCZ .18 llama al mismo helper al enviar paquetes y reproducir disparos locales. Ya distribuye disparos a jugadores del mundo dentro del radio y codifica coordenadas: RemoteAudio reproduce incluso si la entidad del tirador no esta cargada. La atenuacion manual nativa es cuadratica con la distancia real al jugador; no usa la camara isometrica. Tactical debe actualizarse tanto en servidor como en clientes.

Construir: `python far-shots4/build.py`. Probar: `python far-shots4/test.py`. 81 comprobaciones de rangos y ejecucion del callback del mixin real; 7 auditorias de las rutas nativas de paquetes y reproduccion; regresiones de impactos y plazo de tres minutos. No se ha ejecutado Minecraft, red real ni OpenAL en este entorno. [Guia del paquete](../iso-audio31/GUIA.txt).
