Pruebas automáticas realizadas (NO dentro de Minecraft):
- 150 clases verificadas con ASM BasicVerifier y seis enganches exactos.
- 277 comprobaciones de regresión y 15126 de Fase 2.6 con JVM -Xverify:all.
- 5000 identidades: persistencia, clones, concordancia y diversidad de perfiles.
- Dos entidades de apoyo, cobertura/chunks, traición, tregua y fallo de generación.
- Privacidad, medicina transferida, acusaciones, permisos y comandos Brigadier reales.
- Chat celeste, limpieza de opciones, reinicio bloqueado, distancia y oyentes privados.
- Siete escenarios en JVMs independientes: archivo nuevo/relectura, copia de .36,
  schema2 personalizado, JSON roto, variable prohibida y schema1 personalizado.
Las APIs de Minecraft están simuladas. No equivale a ejecutar el modpack.

Tabla para probar en Minecraft real — TODOS PENDIENTES:

| Prueba | Cómo | Resultado esperado |
|---|---|---|
| Instalación | Reemplazar .36 en cliente y servidor | Carga sin duplicar mod |
| Claridad | Hablar y contestar varias veces | TE HABLAN y RESPONDER celestes; solo opciones vigentes |
| Repetición | Hacer clic varias veces en el NPC | No reinicia la apertura ni llena el chat |
| Distancia | Acercarse desarmado a 12 bloques, con visión | Inicia diálogo sin acercarse hasta tocarlo |
| Oyentes | Segundo jugador a 5/15/25 bloques | Oye según susurro/normal/grito; no recibe opciones |
| Persistencia | /npcdebug, guardar y recargar mundo | Misma semilla e identidad |
| Variación | Generar varios NPCs de igual voz | Historias/nombres distintos, sin personajes fijos |
| Enfermo | /npcexample cansado familiar_enfermo, ofrecer medicina | Consume una; +30 confianza, -25 desesperación, acuerdo |
| Privacidad | Preguntar con confianza baja y luego alta | Datos privados solo al alcanzar confianza/revelación |
| Emboscada | NPC nuevo entre coches/paredes; ejemplo seco | Dos compañeros ocultos reales; al acercarse combaten |
| Acusación | Acusar al mentiroso | Revela mentira sin pérdida de confianza |
| Retirada | Elegir salida pacífica | Nadie dispara durante tregua |
| Sin cobertura | Mismo ejemplo en planicie vacía | Falla sin aparición visible ni duplicados |
| Recarga | Editar pausa/bancos y /npcreload | Nuevo JSON activo; identidad existente estable |
| JSON inválido | Variable desconocida y /npcvalidate | Archivo/campo; /npcreload conserva último válido |
| Regresión | Arma/carrera/disparo/daño, silencio 12 s | Rupturas anteriores y opciones caducadas conservadas |
