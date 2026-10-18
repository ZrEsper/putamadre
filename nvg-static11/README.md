# Fracture Point .11: estática permanente visible

`python nvg-static11/build.py` usa únicamente la biblioteca estándar de Python y la base .10 subida por el usuario, fijada por SHA-256. Modifica exclusivamente los ocho shaders de visión. Verifica igualdad de todas las clases, recursos de sonido, modelos, configuraciones y demás entradas del JAR.

`python nvg-static11/shader_test.py` necesita moderngl, numpy y EGL, disponibles en el entorno. Verifica grano con amplitud visible, variación en varios instantes y entre fotogramas, textura térmica y colores de láser. `--old` usa el shader .10 y debe fallar en la intensidad mínima. No simula una ejecución completa del modpack.

`python nvg-static11/test.py` reutiliza fixtures de nvg-feedback10 y hybrid-mask8 para ejecutar los métodos reales de sonido y marcado de textura del nuevo JAR; además comprueba el control negativo .10. No requiere instalar dependencias nuevas ni modificar el entorno.

El grano se aplica después del filtro en cada invocación de main y usa Time para variar la semilla; no contiene condiciones de brillo, daño o movimiento. La excepción alfa 251 protege el color del láser. El HUD no recibe una nueva capa independiente. El visor mantiene el control manual existente.
