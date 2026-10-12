# TZP — cuerpos completos y lectura de spark

Cierra Minecraft. Extrae `TZP-cuerpos-completos-parche.1.zip` dentro de resourcepacks y ejecuta Aplicar-TZP.bat. El programa encuentra el ZIP TZP en su carpeta o la carpeta anterior; si no, permite introducir su ruta. Crea un respaldo en TZP-respaldo y aplica los modelos al mismo ZIP, conservando su nombre y prioridad. No actives el ZIP del parche como resourcepack. El script PowerShell no se ha ejecutado en Windows en esta sesion; la conversion equivalente se valido aqui sobre tu ZIP. Requiere EMF/ETF para modelos y animaciones personalizados; las texturas TZP se conservan.

Esta entrega reemplaza los 26 archivos CEM personalizados de heridas visuales por siete archivos originales de Fresh Animations 1.10.4 de FreshLX, procedentes del pack suministrado por el usuario: modelos de zombie, husk, drowned y drowned_outer, con sus tres archivos de animaciones. Los 3947 archivos restantes del ZIP recibido, salvo pack.mcmeta, se conservan byte por byte. Se mantienen los créditos. Se retiran las representaciones visuales personalizadas de amputación/quemadura: el mod de heridas mantiene sus efectos de juego. No es una nueva simulación de amputación. No se ejecutó Minecraft; ZIP, JSON, referencias a JPM y conservación de archivos fueron comprobados.

Perfil hyCcr2Z6N9: captura del hilo Server thread durante unos 60 segundos muestreados (65.79 s de captura; 1062 ticks). TPS de 1 minuto: 15.98. 1368 entidades registradas, entre ellas 801 objetos tirados, 256 zombis y 76 rastros de Zombie Awareness. Los contadores corresponden al instante registrado en el perfil y no a un promedio.

Categorías sin contar dos veces nodos descendientes de la misma categoría:
- Guardado/restauración y movimiento virtual de zombis de Integration: 17.36 % del tiempo muestreado del hilo, incluyendo espera de disco; no es únicamente tiempo de CPU.
- Llamadas de IO nativo de Windows: 12.64 %; unas 7.56 s, principalmente dentro del archivado de Integration. Esta categoría se solapa con la anterior y no deben sumarse.
- Comandos/funciones del servidor: 13.13 %.
- Creación de rutas: 8.95 %; predomina ChunkReflection.call de Hordes (~4.41 s).
- Código de Zombie Gore: 0.26 %.

Este perfil es del servidor: no permite medir el coste de los modelos EMF ni explicar por sí solo los FPS. Los modelos corregidos resuelven la sustitución de los cuerpos partidos; no se afirma que eliminen el lag del servidor. Las siguientes optimizaciones deberían reducir el IO síncrono del archivado, los barridos de las funciones del datapack y los recálculos de rutas. El número elevado de objetos merece revisar las fuentes de drops y su recogida; no se elimina loot automáticamente en esta entrega.
