# Láser físico e isométrica

Parches mínimos sobre los JAR originales fijados por SHA-256 en inputs.json. El vídeo 20.03 muestra el haz siguiendo la vista; 20.35 muestra la prueba isométrica al disparar.

`python laser-iso35/build.py` usa Java 21, ECJ y ASM de hordes-melee-interiors-patch/build/tools. Para el API de compilación, requiere los stubs construidos por `python action34/build.py`; no los incluye en el mod. `python laser-iso35/test.py` utiliza las fixtures creadas por `python action34/test.py` para verificar el render y sus regresiones, más fixtures propias para probar el Bridge real del archivo isométrico. La antigua prueba ReticleChecks34 se sustituye por PhysicalLaserChecks porque la nueva solicitud cambia deliberadamente el comportamiento de convergencia al cursor. El resto de las clases y recursos de Melee v34 se conserva.

Las clases nativas de referencia incluidas en base/native-targets.jar son únicamente fixtures de verificación, extraídas de TaCZ .19 aportado anteriormente y Third Person Shooting .2 del repositorio. No se distribuyen dentro del ZIP de instalación.

Ver LEEME.txt para controles, instalación y límites de validación.
