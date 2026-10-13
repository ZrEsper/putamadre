# Panel 25: limpieza de objetos tirados

Sustituye el JAR de Zomboid Hordes anterior por `zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.25-timed-item-cleanup.jar` en el cliente y en el servidor. No instales dos versiones a la vez. El protocolo del panel cambió a 25.

Abre `/zhorda panel` con permisos de administrador (nivel 2), entra en **Objetos tirados** y pulsa **Aplicar** después de editar.

- Limpieza automática: inicialmente desactivada.
- Mínimo/máximo: 1–1440 minutos; normal 10–20. Cada ciclo sortea un intervalo entre ambos. Iguales: intervalo fijo. El mínimo no puede superar el máximo.
- Tiempo mínimo en el suelo: 0–1440 minutos; normal 1. Con 0 también elimina objetos recién soltados.
- Avisar en chat: opcional e inicialmente desactivado. Cuenta atrás ajustable de 1–300 segundos; normal 30.
- Mensaje editable: hasta 160 caracteres. `{segundos}` se sustituye por la antelación. Vacío: texto predeterminado.
- Limpiar objetos ahora: acción única al Aplicar; respeta la antigüedad mínima y el aviso.

La limpieza elimina entidades de objetos tirados en los chunks cargados de todas las dimensiones. No carga chunks lejanos ni toca inventarios, cofres, cadáveres o entidades distintas de objetos tirados. Borra como máximo 32 objetos examinados por tick, con un presupuesto adicional de 2 ms. Tras finalizar programa el siguiente intervalo; al desactivar cancela las limpiezas automáticas pendientes. Los intervalos usan tiempo real mientras el servidor está funcionando; el temporizador se reinicia al volver a abrir el mundo.

Los ajustes y el mensaje se guardan por mundo en `serverconfig/zomboid-hordes-controls.properties`. Se mantiene íntegra la lógica de prioridad de combate y reserva de zombis de la versión 24.

Verificación: pruebas de intervalos, cuenta atrás, protección de objetos recientes, eliminación por tandas, avisos opcionales, cancelación y guardado/aislamiento entre mundos. Verificación del bytecode: 104 clases y 857 métodos. No probado dentro de Minecraft.
