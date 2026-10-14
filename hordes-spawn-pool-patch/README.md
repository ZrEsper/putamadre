# Hordes .28: mezcla de personalidades, spawn y atributos

Parche del JAR exacto enviado `admin-panel.27-action-sounds-loot-profiles`. Conserva los perfiles de loot, sonidos de acciones, limpieza, zonas, reservas y demás recursos originales.

## Instalar y usar

1. Cierra Minecraft y detén el servidor.
2. Reemplaza el JAR anterior de **Zomboid Hordes** por `zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.28-spawn-personality-pool.jar`, en cliente y servidor. Deja una sola versión de este mod en cada carpeta `mods`.
3. Abre `/zhorda panel` como administrador.
4. En **Spawn e IA**, cambia los valores o pulsa **Cambiar preset**: Fácil → Equilibrado → Difícil. Abajo se muestran los porcentajes reales.
5. En **Velocidad y daño**, ajusta los atributos de cada personalidad. Pulsa **Aplicar** para guardar los cambios.

Para distribuir con el instalador, el JAR va en `Para-el-creador/componentes/mods`, sustituyendo el anterior; después prepara de nuevo la distribución.

## Mezcla de zombis

Los tres pesos determinan qué personalidad se asigna al aparecer un zombi:

| Personalidad | Comportamiento base | Ejemplo fácil |
|---|---|---:|
| Tontos | Reacción lenta; comportamiento torpe existente | 80 |
| Brutos | Reacción intermedia; comportamiento normal existente | 15 |
| Astutos | Reacción rápida; comportamiento inteligente existente | 5 |

80/15/5 equivale a 80 % / 15 % / 5 %. También sirve 8/1.5/0.5: los pesos se normalizan automáticamente. **0 excluye un tipo**. No se permite guardar los tres en cero. Son probabilidades por zombi, no cuotas exactas de una oleada pequeña.

«Brutos» es una personalidad, no la especie `mutant_brute`. Las rarezas y especies mutantes siguen usando sus selectores originales.

Los pesos afectan a zombis nuevos. Los existentes conservan su personalidad al actualizar, guardar o recargar el mundo. Para repoblar con una nueva mezcla, el panel original ofrece regenerar población; esa acción retira la población actual y no se ejecuta al cambiar un preset.

## Controles

**1x = comportamiento original**, 0.5x = la mitad y 2x = el doble.

| Control | Rango | Qué cambia |
|---|---:|---|
| Inteligencia / reacción | 0.25–3x | Demora para detectar y tiempo de memoria; la personalidad se elige con los pesos |
| Agresividad de la IA | 0.25–3x | Preparación y frecuencia de mordiscos, persistencia de persecución; no cambia daño |
| Oído | 0–3x | Alcance de audición individual, incluidos disparos y acciones; 0 = sordos |
| Velocidad de aparición | 0.1–10x | Reposición y oleadas, mediante el ajuste original de frecuencia |
| Cantidad de zombis | 0.1–10x | Población objetivo; conserva límite de zona y reserva |
| Zombis enfurecidos | -1 o 0–100 % | Perfil adicional original rápido/fuerte; -1 conserva el comportamiento original |
| Distancia de visión | -1 o 0–128 | Ajuste original; -1 mantiene la visión propia de la personalidad |
| Velocidad por personalidad | 0–3x | Multiplica su velocidad original; 0 = inmóvil |
| Daño por personalidad | 0–5x | Multiplica el atributo de ataque cuerpo a cuerpo; 0 = sin daño de ese ataque |

Inteligencia, oído, agresividad y atributos se actualizan también en zombis existentes. Velocidad/daño se refrescan mediante el ciclo original de atributos, hasta aproximadamente un segundo después. Los proyectiles o ataques especiales de otros mods que no usan el atributo de ataque conservan su propio daño.

La velocidad conserva el ritmo individual aleatorio, los efectos originales y sus límites de navegación compatibles. El daño conserva los modificadores de heridas, astucia y enfurecimiento, combinando el nuevo multiplicador. No cambia salud ni loot.

Los presets de **Spawn e IA** solo cambian esa pestaña; los de **Velocidad y daño** solo cambian sus seis atributos. Ambos dejan intactos loot, limpieza y acciones de regeneración. Los valores se preparan primero; **Aplicar** los guarda.

| Preset de spawn | Tontos / brutos / astutos | Inteligencia / agresividad / oído | Frecuencia / cantidad |
|---|---|---|---|
| Fácil | 80 / 15 / 5 | 0.65 / 0.65 / 0.65 | 0.7 / 0.7 |
| Equilibrado | 45 / 40 / 15 | 1 / 1 / 1 | 1 / 1 |
| Difícil | 15 / 35 / 50 | 1.6 / 1.6 / 1.5 | 1.2 / 1.2 |

Fácil desactiva el perfil adicional de enfurecimiento; Equilibrado usa original; Difícil lo activa en el 20 %. El preset Equilibrado conserva los defaults efectivos de la versión enviada. Ningún preset se impone al instalar.

## Persistencia y compatibilidad

Guarda los nuevos valores junto con los controles existentes en `<mundo>/serverconfig/zomboid-hordes-controls.properties`. Una configuración antigua recibe valores nuevos de 1x y mezcla 45/40/15. Los perfiles guardados de los zombis se mantienen. Un pool vacío escrito a mano se recupera con 45/40/15 al cargar y deja aviso en el log.

El protocolo del panel pasa a 28: **cliente y servidor deben actualizar juntos** para compartir la misma lista de ajustes. Se conservan permisos de administrador, validación del servidor, comprobación de revisión y escritura atómica originales.

## Validación

88 comprobaciones: muestreo 80/15/5 y pesos que no suman 100, exclusión de tipos, errores y límites, personalidades guardadas, nuevos spawns, ajustes de oído/reacción sin multiplicaciones acumulativas, presets, vista de porcentajes y aislamiento del loot. Incluye ejecución de los métodos nativos modificados de atributos y navegación con dobles de las APIs: reducción y cero daño, restauración, ausencia de acumulación, velocidad aumentada y límite de movimiento.

ASM BasicVerifier verifica los métodos cambiados; el build comprueba CRC del JAR, entradas sin duplicados, inventario exacto de siete clases cambiadas y dos añadidas, y recursos idénticos al original. Los dobles y herramientas no se incluyen en el JAR.

**Minecraft no se ejecutó en este entorno.** Falta verificar el panel visual, la sincronización real cliente/servidor y el comportamiento en partida, especialmente zombis de otros mods y sus ataques especiales.

Para probar: guardar una mezcla 100/0/0 y observar nuevos zombis; repetir con 0/100/0 y 0/0/100; comparar daño 0x/0.5x/2x y velocidad 0x/0.5x/2x; cambiar oído a 0 y 2x; reiniciar y comprobar persistencia. Un jugador sin permisos no debe poder aplicar cambios.

## Reconstruir

`python3 build.py` con Java 21. Descarga ECJ/ASM desde Maven Central con hashes fijos, verifica la entrada, compila los helpers, aplica parches concretos, ejecuta pruebas y genera `output/validation.json`. El JAR base enviado está en `base/`.
