# Zombies Poldo — Instalador sencillo 1.3.1

## Corregir tu carpeta actual

[Descargar actualización pequeña (2 MB)](Zombies-Poldo-Actualizacion-1.3.1.zip)

1. Cierra el instalador.
2. Extrae la actualización en tu carpeta **ZOMBIESPOLDO**, aceptando reemplazar los archivos del instalador.
3. Conserva tu archivo **datos** tal como está y abre **Instalar.cmd**.

La actualización no incluye datos.zip ni reemplaza tu modpack. El instalador detecta el formato real y lo descomprime automáticamente: **ZIP, RAR, 7Z, TAR, TAR.GZ, TAR.XZ, TAR.BZ2 o carpeta sin comprimir**. Un ZIP con extensión incorrecta también se reconoce por su contenido. Si encuentra varios paquetes, pide elegir uno. Archivos cifrados, dañados o incompletos producen un diagnóstico.

Lleva un extractor portable oficial de **7-Zip 25.01**, con licencia y enlace al código fuente. Se utiliza dentro de la caché del instalador, sin instalar un descompresor en Windows ni pedir administrador. `motor.zip` aporta la interfaz y el extractor aunque el archivo datos contenga solo mods/mapa o tenga una carpeta envolvente.

## Descargar la plantilla completa

[Instalador completo 1.3.1](Zombies-Poldo-Instalador-Sencillo-1.3.1.zip) · [Fuentes y pruebas](Zombies-Poldo-1.3.1-Fuentes-y-pruebas.zip)

Extrae el ZIP y abre **Instalar.cmd**. Elige tu juego, pulsa **Continuar** y después **Instalar ahora**. Incluye detección de perfiles de CurseForge y TLauncher, opción para preparar un ZIP importable en CurseForge, descarga automática de Java 21 cuando falta, guía sencilla, diagnósticos, respaldos y restauración. Conserva la estética y el sonido del instalador original.

**La plantilla completa no incluye mods ni mapa.** El creador debe añadir sus ZIP/JAR o carpetas estructuradas en `Para-el-creador/componentes` y abrir `Preparar-distribucion.cmd`. Los componentes internos del creador mantienen el formato ZIP/JAR; el formato flexible descrito arriba corresponde al contenedor principal que abre el instalador. El ZIP completo generado es el que se entrega al jugador.

![Vista del instalador](Vista-instalador-1.3.0.png)

## Validación

Pasaron **34 pruebas funcionales** de detección, instalación, respaldo, restauración y empaquetado, más **25 comprobaciones de archivos** con extracción real de ZIP, RAR5, 7Z y TAR.GZ, extensiones incorrectas, carpeta normal, rutas inseguras, archivo truncado y motor independiente. El código PowerShell se ejecutó en Linux con la compilación Linux oficial de 7-Zip 25.01; también se comprobó su sintaxis.

**Pendiente de ejecutar el bootstrap completo y los binarios Windows en Windows, así como CurseForge y Minecraft.** La descarga de Java desde Adoptium no pudo comprobarse desde esta nube (HTTP 403). Antes de distribuir el modpack, valida el ZIP completo en Windows y en una copia de una instancia con partidas.

[Informe de validación y SHA-256](VALIDACION-1.3.1.json)

La versión 1.3.0 se conserva como referencia; usa 1.3.1 para corregir la detección del archivo datos.
