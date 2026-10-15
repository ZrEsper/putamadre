# Screamer más rápido y armadura por zona

## Instalar los dos reemplazos

Cierra Minecraft y el servidor. Haz una copia de tu mundo y configuración. En `mods`, retira el addon Mutants L4D2 1.4.0 anterior y FirstAid 3.6.1. Pon los dos JAR de la carpeta `mods` de este ZIP. No dejes dos versiones de ninguno de esos mods. Actualiza servidor y todos los clientes.

Conserva el mod base `MutantsZombies-1.3.1-NeoForge-mc1.21.jar`, Integration 1.7.8 y tus demás mods, incluido Hordes .39 si es el que ya estás usando. El addon conserva su versión interna 1.4.0 para seguir dentro del rango de Integration 1.7.8; el nombre del archivo distingue este parche. El mod base MutantsZombies no se modifica.

## Huevo del screamer

Ahora existe `mutants_l4d2:screamer_spawn_egg`. Está en la pestaña de huevos de aparición del inventario creativo, con nombre en español, inglés y alemán según el idioma del juego.

También puedes conseguirlo con `/give @s mutants_l4d2:screamer_spawn_egg` o invocar directamente al monstruo con `/summon mutants_l4d2:screamer ~ ~ ~`. Los comandos requieren permisos. Usar el huevo no depende de la probabilidad de aparición natural.

Este parche no cambia sus filtros ni las probabilidades de aparición natural. Un huevo que funciona no garantiza que el generador de hordas lo seleccione; eso sigue dependiendo de la configuración y de los generadores que tengas.

## Más rápido y con animación más retorcida

La velocidad base pasa de 0,36 a 0,48 y la velocidad de navegación durante la persecución de 1,20 a 1,35. Mantiene sus 48 puntos de salud y su función de apoyo: no se le añade daño cuerpo a cuerpo.

Al moverse, lleva el torso encorvado y torcido, la cabeza ladeada con sacudidas breves, los brazos en posiciones distintas y una zancada más rápida y desigual. Durante el grito abre mucho más la mandíbula y levanta los brazos de forma asimétrica. La pose se reinicia por fotograma, evitando acumular giros sin límite.

La animación del grito se avisa desde el servidor a los clientes y dura unos 10,1 segundos. El resto de su textura, modelo base y habilidades se conserva.

## Tu audio y llamada a las hordas

Se usa el archivo «SCP 096 SCREAM.ogg» que adjuntaste. Dura aproximadamente 10,1 segundos. Se convierte a OGG mono para que el sonido salga de la posición del monstruo y tenga dirección y atenuación por distancia. Mantiene el tono original.

Al adquirir un jugador como objetivo válido grita inmediatamente. Mientras sigue persiguiendo al mismo objetivo, deja 12 segundos entre gritos para que el audio no se superponga en esa persecución. Un cambio de objetivo dispara un grito inmediato nuevo. Se incorpora un subtítulo traducido.

Cada grito conserva la llamada a `HordeScent.alert` en un radio de 96 bloques. El sistema existente avisa al módulo de atracción de Hordes cuando está disponible y dirige hasta 32 infectados cercanos al objetivo. Atrae los que hay alrededor; no crea hordas ilimitadas de la nada. El sonido tiene alcance nominal de 96 bloques, sujeto a volumen del cliente y a tus mods de sonido.

## Armadura: 8 puntos equivalen a 16 tiros de pistola EN TOTAL

FirstAid usa los puntos de la pieza que protege la zona alcanzada: casco para cabeza; pechera para torso y brazos; pantalones para piernas; botas para pies. No suma todo el conjunto para proteger la cabeza.

La referencia es la Glock 17 de TaCZ registrada en tu instalación: se lee el daño de su proyectil y su multiplicador de cabeza. Si falta, se busca otra pistola registrada; como último recurso se usa daño 5 y multiplicador de cabeza 2.

La resistencia nominal de cada zona es el mayor de estos valores: la resistencia sin armadura y dos veces los puntos de su pieza. Por eso 8 puntos dan 16 impactos de referencia en total, y 12 puntos dan 24. Una pieza débil nunca te hace más frágil que ir sin ella.

Se conservan como referencia sin armadura los 8 impactos de cabeza y 15 de torso de tu versión anterior. Para las extremidades se conserva la referencia derivada de su salud y la escala balística anterior. Eso significa que una armadura pequeña puede no superar todavía la resistencia básica: no se suman 16 impactos extra a los que ya aguantabas.

## Distancia, arma y tipo de bala

El cálculo convierte el daño REAL del impacto que llega desde TaCZ a una pérdida de salud por zona. No aplica el mismo daño a todas las armas ni inventa otra curva de distancia encima de la del arma.

| Situación de referencia, zona con 8 puntos | Resistencia nominal |
| --- | --- |
| Pistola de referencia, impacto normal | 16 impactos en total |
| Impacto cuatro veces más dañino | Aproximadamente 4 impactos |
| Impacto con la mitad de daño por distancia | Aproximadamente 32 impactos |
| Munición/arma con 1,5 veces ese daño efectivo | Aproximadamente 11 impactos |

Se conserva la caída por distancia, los cambios de daño del arma, la munición y los accesorios tal como TaCZ los entrega. Si un pack define dos municiones con las mismas propiedades efectivas, este parche no inventa diferencias solo por su nombre o calibre.

También se conserva la penetración nativa: TaCZ separa la parte normal del impacto y la que ignora armadura. La segunda no recibe la protección extra de la pieza. No se aplica el porcentaje de penetración una segunda vez. Por ejemplo, en la prueba de cabeza con 8 puntos, un impacto con 50% de daño que atraviesa armadura baja la resistencia a unos 11 impactos.

Los números son referencias con la zona sana y la pieza conservando sus puntos. Influyen heridas previas, distancia, potencia real del arma, penetración, efectos y configuración. Las escopetas pueden entregar varios impactos por cartucho: un disparo del gatillo no equivale necesariamente a un solo impacto.

## Durabilidad, heridas y demás daños

Se mantiene el desgaste de la armadura. Si el casco se rompe o lo quitas, pierdes su protección para los siguientes impactos; no se restaura la salud. Curarte sigue dependiendo del sistema médico existente.

Se evita multiplicar esta protección nueva por otra reducción balística normal de armadura de FirstAid. Los daños que no son balas conservan el comportamiento anterior, incluidos golpes, caídas y quemaduras. Los encantamientos y efectos que modifiquen el daño entrante siguen formando parte de su cálculo.

Las demás funciones de tu FirstAid 3.6.1 —tratamientos, heridas, reanimación y cura de infección— permanecen en el JAR. El cambio se limita al cálculo balístico y al manejo correcto de daño sobrante entre partes.

## Comprobaciones y límites

Screamer: 18.020 comprobaciones con APIs simuladas, incluyendo la ejecución del tick real parcheado para detección, persecución, grito, intervalo y llamada a hordas; registro de huevo, pestaña creativa y poses acotadas.

Armadura: 14.773 comprobaciones de lógica con APIs simuladas: ocho zonas, puntos locales, 16 impactos totales, armas más potentes, daño reducido por distancia, penetración dividida, heridas previas, pérdida de casco y daño no balístico.

Se verifica el bytecode modificado, los hashes de entrada/salida, integridad de JAR y conservación del resto de recursos. El audio se valida como Vorbis mono de unos 10,1 segundos. No se ha ejecutado Minecraft: falta comprobar arranque con tu conjunto de mods, aspecto de la animación, reproducción espacial del audio, bus de eventos, red y comportamiento en combate real. Las simulaciones no sustituyen esa prueba.
