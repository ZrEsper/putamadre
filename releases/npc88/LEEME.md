# tactical.54 — desbloquear trabajo compartido

Sustituye únicamente tactical.53 por el JAR de esta carpeta; conserva una copia de tu mundo. No exige borrar NPCs ni regenerar la base. No cambia los JARs de cadáveres, gore o decoración.

## Problemas corregidos

- Los ayudantes de una base compartida no podían entrar a los oficios porque se exigían cuatro muebles personales que el antiguo ramal del líder nunca les dejaba completar. Ahora admiten los muebles de un vecino vivo, del mismo bando y base próxima (radio 12, desnivel máximo 3). Comparte cofre, horno y mesa si faltan; no comparte la cama. No crea muebles extra.
- El despachador de trabajo se ejecuta después de recuperación y antes de deambular por ruidos y del patrullaje antiguo. Mantiene órdenes explícitas, conversación, peligro, servicio de comida/bebida, incapacitación y miedo.
- Migración única de militares sin oficio o con guardia: la primera necesidad no cubierta entre trabajadores migrados de la base es granjero, después constructor, minero y guardia. Conserva otros oficios guardados. Los guardias intentan reforestación y fortificación antes de su puesto de vigilancia.
- La reacción verbal y la investigación genérica de disparos tienen una espera de 1200 ticks por NPC. Solo un disparo reciente de prioridad suficiente activa esa espera. No se desactivan daño, adquisición de enemigos, amenazas ni defensa real.
- Una parcela aún no iniciada puede seleccionar entre doce ubicaciones cargadas y despejadas, buscando cielo abierto si el punto inicial queda bajo techo. No destruye edificios. Persiste sus coordenadas; no desplaza huertos existentes.
- El depósito conserva herramientas agrícolas, cubeta de agua y hasta ocho semillas/tierra que realmente llevaba el agricultor. El resto se aporta al cofre. Retira de vuelta objetos reales: no duplica suministros.
- El minero ya no abandona la fabricación de un pico precisamente cuando no tiene ninguno. Intenta la receta de piedra y, con materiales reales, la de madera como arranque.

## Comprobaciones

90 controles JVM: 74 de asentamientos anteriores, 13 nuevos de base compartida, reparto, ruido, parcela bajo techo y conservación del inventario; 3 del orden de ejecución extraído de Life79.work con delegados controlados. Además pasan los 79 controles anteriores de diálogo, inventarios, cadáveres, puertas, amenazas, sueño y acciones. Compila Java 21 y verifica bytecode.

Estas pruebas NO ejecutan Minecraft ni NeoForge real. No verifican dos días reales de autonomía, navegación del modpack, rendimiento con hordas ni crecimiento natural de cultivos en tu partida. Los delegados de la prueba de orden aíslan tareas; no son una simulación completa del cerebro.

## Prueba en Minecraft pendiente

1. Usa la misma base y dos o más NPCs, de día y sin enemigos activos. El HUD debe añadir oficio y actividad, en lugar de quedar solo en `vigilar base y aliados`.
2. Debe aparecer un granjero entre militares migrados de esa base y comenzar un huerto exterior si hay un sitio válido. No necesita crear cuatro muebles propios por ayudante.
3. Espera al menos un minuto mientras trabaja: debe conservar azada y cubeta antes de acabar el bancal. Comprueba semillas repartidas entre mochila/cofre sin multiplicarse.
4. El constructor necesita materiales reales. Dar tierra, madera o piedra al almacén permite comprobar que prioriza fortificar antes de rondar. No hay una concesión infinita de bloques.
5. Con disparos continuos, las frases genéricas no deben repetirse cada diez segundos por NPC; pueden repetirse una vez por minuto. Ataques reales siguen interrumpiendo trabajos.
6. Guarda y carga: comprueba que conserva oficio, base y parcela. No está comprobado aquí en Minecraft real.

## Límites y peticiones añadidas

La fortificación y las zonas conservan los límites geométricos de la .53; el terreno complejo puede requerir otro plan. Si no hay parcela abierta entre las doce candidatas, no se inventa un cultivo. El kit agrícola sigue siendo finito; herramientas agotadas y falta de materias primas pueden detener tareas. No hay garantía de autosuficiencia en cualquier edificio.

Esta entrega NO incorpora entierros, animaciones funerarias, nuevos vínculos afectivos, venganza de facción, nuevos edificios con ventanas ni colocación/mantenimiento de generadores, refrigeradores o AC.

Se inspeccionaron los tres JARs aportados: Doomsday Decoration .5, Refurbished Furniture 1.0.22 y Supplementaries 3.9.9. El AC de Doomsday sí tiene mejora de cultivos, pero su alimentación busca `dev.zomboid.survival.MachineEntity.GENERATORS`, clase ausente de esos tres JARs. El generador de Refurbished Furniture no comparte automáticamente ese registro. Falta el mod que proporciona esa máquina para integrar alimentación real sin inventar compatibilidad. No se publican copias de los JARs aportados ni logs personales.
