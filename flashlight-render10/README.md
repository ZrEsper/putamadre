# Flashlight .10: texturas y luz cercana

Construcción: `python flashlight-render10/build.py` con Java 21 y ECJ/ASM existentes. Base .8 fijada por SHA en inputs.json; conserva CaptureStage9 y los controles de .9. No necesita cambios de entorno respecto a .9.

Validación: ejecutar `test.py`, `shader_test.py`, `camera_test.py` y `appearance_test.py` dentro de esta carpeta. Reutiliza fixtures de flashlight-render6 y flashlight-render7. Requiere moderngl, numpy y EGL, ya disponibles.

`appearance_test.py --old` debe fallar con el shader .9 por pérdida del color verde del terreno diurno. Los demás casos verifican luz sobre suelo a dos bloques en cámaras externas y la ausencia de franjas de autosombra al comparar un plano con su mapa de rayos. Una pared cercana sigue bloqueando el haz.

El nuevo NearFill solo se activa para la luz del jugador local cuando la cámara está separada, incluida una cámara modificada que no marque isDetached. La primera persona conserva el cono principal; la iluminación cercana comparte origen y sombras y se extingue a cinco bloques. La comparación PCF usa el centro real del texel y la intersección con el plano de la superficie.

Se conservan las marcas alfa 250/251/253 y las exclusiones de etapas secundarias. No hay segundo dibujo de entidades. Véase LEEME.txt para instalación y límites de las pruebas: no hay Minecraft ejecutable en este entorno.
