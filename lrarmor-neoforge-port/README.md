# LesRaisinsArmor — NeoForge 1.21.1 port

Descarga: `mods/lrarmor-1.21.1-0.1.4.4-neoforge.1.jar`.

Instalar en cliente y servidor, sustituyendo `lrarmor-1.20.1-0.1.4.4.jar`. Requiere Minecraft 1.21.1, NeoForge **21.1.255 o posterior** y GeckoLib **4.9.3** (rango 4.9.3–4.9.x). No instalar el mod Forge anterior junto con este port.

## Qué se portó

Reconstrucción de los 29 archivos fuente del JAR original, conversión de sus 99 símbolos Minecraft SRG a nombres Mojang y adaptación a las APIs reales de 1.21.1. Se conservan IDs de las 62 piezas, los conjuntos, sus datos configurables, 62 recetas, modelos, texturas, animaciones, traducciones y configuración de visibilidad de partes del jugador.

- Forge → NeoForge: registros, eventos, especificaciones de configuración y aislamiento de clases cliente.
- Materiales y atributos: holders, IDs de modificadores, componentes de ItemStack y evento de atributos para que la armadura efectiva sea la del JSON, no la del material vanilla usado como base.
- Efectos: holders de efectos y callback booleano moderno para expirar con seguridad al quitar una pieza del conjunto.
- Red: se reemplaza SimpleChannel por CustomPacketPayload, StreamCodec y RegisterPayloadHandlersEvent. Se conserva la sincronización de configuraciones al entrar o recargar datapacks.
- Cliente: GeoRenderProvider de GeckoLib 4.9.3 y GeoArmorRenderer; se mantiene la configuración para ocultar partes del modelo vanilla debajo de las armaduras.
- Recetas: directorio singular `recipe`, resultados con `id` y etiquetas comunes `c:`; `leather`/`string` pasan a `leathers`/`strings`. Se mantiene vidrio negro con el ingrediente vanilla equivalente.
- Mixin: descriptor Holder del efecto y nombres de 1.21.1, sin el refmap Forge/SRG antiguo.

**Limitación:** no incluye la integración opcional antigua con Epic Fight 1.20.1. El cliente gráfico no pudo probarse en este entorno sin servidor de pantalla: compilación de las APIs cliente y preservación de recursos sí comprobadas, apariencia en juego no comprobada.

Autor original: **xjqsh**. Se conserva la declaración de licencia **All-Rights-Reserved** del mod original. El port no cambia la autoría de sus recursos.

## Construir y comprobar

Con un JDK 21 completo instalado y acceso a los repositorios de NeoForge/Mojang:

```sh
./gradlew --no-daemon build
./gradlew --no-daemon runGameTestServer
```

Dependencia de compilación local: colocar el GeckoLib 4.9.3 utilizado en `libs/geckolib-neoforge-1.21.1-4.9.3.jar`. Gradle 9.2.1 y ModDevGradle 2.0.148 se fijan en la plantilla. Se usan mappings oficiales, sin dependencia Parchment.

Resultados reales: build correcto y **5 GameTests pasados** en Minecraft 1.21.1 / NeoForge 21.1.255 / GeckoLib 4.9.3. Comprueban registro de 62 piezas y carga de sus datos, defensa/durabilidad efectiva, codec de sincronización, 62 recetas con ingredientes resueltos y efecto completo/incompleto del conjunto. Estos GameTests y su estructura no se incluyen en el JAR de distribución.

La auditoría ZIP comprueba integridad, ausencia de clases Forge y símbolos SRG antiguos, metadatos NeoForge y preservación byte a byte de los **101 archivos visuales** originales (PNG, geo y animaciones). `validation.json` contiene los hashes y resultados. No se suben mundos ni logs de ejecución.
