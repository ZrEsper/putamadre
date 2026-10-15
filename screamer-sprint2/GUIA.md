# Screamer corredor — revisión 2

Cierra el juego y el servidor. Quita el JAR anterior de **mutants-l4d2-addon** y el anterior de **zomboid-hordes**. Copia los dos JAR del paquete conjunto a `mods`, tanto en el servidor como en los clientes. No dejes versiones duplicadas. Conserva MutantsZombies y las demás dependencias que ya usas.

- **Vida:** 40 puntos (20 corazones). Los screamers existentes también se ajustan; los heridos conservan su daño.
- **Carrera:** velocidad base 0,50 y persecución con multiplicador 1,50. El ajuste evita que Hordes lo vuelva a ralentizar como a un zombi corriente. Está configurado para superar la carrera normal del jugador; el terreno, los obstáculos y otros efectos pueden modificar el desplazamiento.
- **Acercamiento:** sigue corriendo hasta aproximadamente 1,2 bloques del jugador. Antes paraba a tres bloques.
- **Grito:** conserva el audio que enviaste, el huevo y la animación grotesca. Grita al detectar un objetivo y repite cada 12 segundos.
- **Horda:** el centro de la llamada es el screamer, con radio de 96 bloques. Los zombis disponibles investigan su posición durante 24 segundos; el siguiente grito actualiza el punto. Se limita el trabajo a los 128 infectados más cercanos por grito, dentro de las entidades cargadas. No crea zombis nuevos.
- Los zombis no atacan al screamer por oírlo. Si ya están combatiendo, conservan su objetivo. Si encuentran al jugador, la detección y persecución habituales siguen funcionando.

El JAR de Hordes 41 conserva el panel sencillo y añade la corrección de rotura de paredes. Consulta la guía correspondiente para activar romper bloques.

Si usas **zomboid-integration**, necesitas la versión compatible **1.7.8** publicada anteriormente; la 1.7.7 rechaza el addon 1.4.0. Este cambio conserva la versión interna 1.4.0 del addon para esa compatibilidad.

Prueba recomendada en una copia del mundo: invoca un screamer nuevo, corre en un suelo plano en supervivencia y comprueba su acercamiento. Coloca zombis sin objetivo cerca del screamer y verifica que se dirijan a él al gritar. Repite con un screamer que ya existía y comprueba que tenga 40 de vida máxima.

Validación: 18.020 comprobaciones del grito, huevo y animación; 17 de carrera, vida y llamada posicional; 57 de rotura de paredes. Se usan dobles de las APIs y verificación del bytecode. Falta probar el conjunto dentro de Minecraft.
