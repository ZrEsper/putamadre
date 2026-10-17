# Global Melee v30

[Instalación y controles](GUIA.txt). Integra inclinación visual en esquinas y compatibilidad opcional con Carry On, conserva brazos slim/wide y modifica los recorridos de los puños en primera persona.

- `Lean30` detecta un borde expuesto mediante rayos COLLIDER/NONE a altura de ojos; comprueba el barrido lateral de cámara, conserva posición/velocidad/rotación física y amortigua el regreso al centro. El gancho de Camera.setup y el evento de ángulos componen posición y roll. No cambia el origen servidor de las balas ni sincroniza una hitbox inclinada.
- `Carry30` consulta la API CarryOnDataManager.getCarryData(Player).isCarrying sin dependencia obligatoria. La misma comprobación bloquea los golpes y parkour en servidor; el render de combate cede a Carry On. Un mixin opcional en CarriedObjectRender.drawFirstPerson añade brazos con la piel y mangas correctas después del objeto nativo. La pose corporal y scripts siguen a cargo de Carry On.
- `View30` distingue preparación, extensión y recuperación, conserva asimetría y alinea la extensión con los tiempos de impacto existentes. `First24.drawFrame` comparte las mallas slim/wide sin reemplazar el renderer de armas.

El mod Tactical Movement adjunto corresponde a Forge 1.20.1; no se incluye como dependencia ni se copia su código. Las referencias externas y hashes constan en provenance.json. No se incluyó el mod Carry On.

```sh
python action30/build.py
python action30/test.py
python action30/view_preview.py
```

Los tests usan APIs simuladas y comprobaciones ASM; el diagnóstico de geometría generado es una proyección matemática, no una captura de Minecraft. La prueba visual con el pack completo y el JAR real de Carry On queda pendiente.
