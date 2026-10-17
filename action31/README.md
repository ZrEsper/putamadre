# Global Melee v31

[Instalación y controles](GUIA.txt). Reemplazo de Global Melee para NeoForge 1.21.1, protocolo boxing31. Mantiene TaCZ .19 y Carry On.

`Lean30` ahora calcula inclinación lateral/frontal desde el desplazamiento real, con comprobaciones de obstáculos para la cámara. `LeanPose31` rodea el render del jugador con una transformación que incluye armas y armaduras 3D; la inclinación de jugadores remotos se deriva de sus posiciones recibidas. No modifica hitboxes ni el origen del disparo.

`StepEvents31` recibe PlaySoundSourceEvent y `Step31` traslada el procesamiento al hilo principal. Se filtran pasos de la categoría PLAYERS cerca del jugador, con movimiento y suelo, y se elimina el doble disparo inmediato. El ciclo avanza medio período por paso, en lugar de usar un reloj independiente. `Run29` reduce el recorrido y conserva su peso durante el salto; `First24` comparte la misma guardia de base antes de mezclar la carrera, eliminando el cambio de base al cruzar el umbral de v30.

`Mantle31` sustituye la postura fija por alcance, apoyo, empuje, rodilla y recuperación, según la altura del destino aprobado. Incluye transiciones con la pose de carrera y mano de apoyo al llevar arma. `Owned31` y tres mixins opcionales hacen ceder únicamente las poses nativas de carrera, prone y wall-ready de TaCZ durante el movimiento propio. `SlidePose28` coloca la empuñadura a la altura del hombro bajo.

`Bodycam27` baja la posición de cadera y conserva inercia moderada durante ADS. La postura de deslizamiento/escalada mantiene su desplazamiento incluso con ADS pulsado. Se conserva el equilibrio de los scopes de pose nativa y arma.

```sh
python action31/build.py
python action31/test.py
python action31/package.py
```

Las bases y herramientas tienen hashes fijados. `Entry31` contrasta los métodos de destino con clases del TaCZ .19 suministrado; `locomotion19-probes.jar` es solo un archivo de comprobación y no se incluye en el mod. Las pruebas funcionales usan APIs simuladas. No se ejecutó Minecraft: la apariencia final y la compatibilidad gráfica del pack requieren prueba en el juego.
