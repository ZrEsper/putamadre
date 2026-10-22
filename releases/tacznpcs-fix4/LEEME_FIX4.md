# tacznpcs tactical.43 - fix4 (sustituye a fix1-3)

Mismas 7 clases que fix3 (CalmBrain, CalmBrain$State, Shelter77, Shelter77$Camp, Shelter77$Cell,
Hunt78, Hunt78$Run), Java 21. NO PROBADO EN MINECRAFT. Guarda copia del jar original.

## Cambio sobre fix3
Hunt78.prepareChest() ignoraba gathering.next y volvia a llamar a gather() cada 20 ticks, saltandose
las pausas y desplazamientos que gather() fija. Ahora, si todavia no vencio gathering.next:
 - con wait=true (sin madera): descansa el tiempo restante (restBlocked) y libera al NPC;
 - con wait=false (desplazandose a otra zona): no escanea y deja que siga caminando.
Resto igual que fix3 (backoff 6000/12000/24000/48000, giveUp, releaseHelper, plazo de gracia 6000 ticks).

## Sigue sin verificar
Puertas, terreno irregular (wander usa la Y actual del NPC), convivencia entre tareas (Life79.work
re-evalua prioridades en cada llamada) y Hunt78 recompilada desde codigo descompilado.
