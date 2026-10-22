# Corrección 68 — daño letal y comida isométrica

Reemplaza Gore .42 por .43 y dungeons_iso .36 por .37. No dejes versiones duplicadas. En servidor dedicado, actualiza Gore en cliente y servidor; la corrección de cámara corresponde al cliente. Conserva Zomboid Hordes .50, The Hordes .3 y First Aid .24.

Gore limitaba cada disparo al 45% de la vida restante para ciertas zonas y hasta el 8% para otras. Este parche conserva el contexto de impacto y elimina ese tope proporcional: el daño letal ya puede agotar la vida en el mismo impacto. No obliga a matar de un tiro ni elimina armaduras, resistencias, salud configurada, botín, ragdolls o destrucción anatómica. Tampoco borra cadáveres para simular una muerte inmediata.

El giro de consumibles reutilizaba el cálculo de puntería de armas, con colisiones y búsquedas de entidades a lo largo del rayo. La orientación ahora se calcula con una intersección matemática con un plano próximo al jugador, sin esas consultas, y como máximo una vez cada 45 ms. Las armas conservan su puntería nativa. Permanecen el clic derecho para consumir, WASD isométrico y la protección contra giros completos al cruzar +/-180 grados.

Comprobaciones: se reproduce el límite de daño anterior en 33 casos y se comprueba su retirada/contexto en los mismos casos; 32 comprobaciones de orientación, comida, bebida, cambio de objetos, cámara desactivada, vehículos, ángulos y límite de frecuencia. ASM BasicVerifier en 193 clases de Gore y 152 de cámara. Los recursos permanecen idénticos. Son pruebas con APIs/entidades de prueba: no se ha iniciado Minecraft ni medido FPS con tu modpack.

En los logs también aparece un aviso de EMF por recreación excesiva de modelos de armadura; no se atribuye todo el lag a la cámara ni se modifica otro mod sin identificar su origen. Si queda una pausa, el siguiente perfil de Spark permitirá medir qué ruta sigue costando tiempo.
