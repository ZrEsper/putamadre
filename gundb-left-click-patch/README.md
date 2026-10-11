# Gun Durability: clic de disparo para destrabar

[Descargar Gun Durability modificado](../mods/gundb-2.2.2-1.21.1-left-click-unjam.1.jar).

Reemplaza `gundb-2.2.2-1.21.1.jar` en el cliente, sin duplicarlo. El servidor puede conservar el original: no cambian componentes, paquetes ni registros. TACZ y sus dependencias siguen siendo necesarios.

Al intentar disparar un arma trabada, inicia la acción nativa de inspección/destrabado de Gun Durability. Mantiene la animación `unjam` o su alternativa de inspección y el tiempo/paquetes originales del servidor. Mantener el disparo no reinicia la animación. El indicador de trabajo se limpia al destrabarse o cambiar de arma/jugador. Los fallos por cargador vacío o durabilidad agotada no se convierten en destrabados. La tecla de inspección original sigue disponible. También funciona con el disparo de mando.

Ocho comprobaciones del ciclo con APIs simuladas, verificación ASM y comparación ZIP: solo cambia `ClientEvents.class` y se añaden los helpers. No probado dentro de Minecraft.

```sh
python -B gundb-left-click-patch/build.py /ruta/gundb-2.2.2-1.21.1.jar /ruta/herramientas
```
