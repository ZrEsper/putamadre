# Selección de variantes: contrato inspeccionado

EMF 3.3.11, `EMFManager.injectIntoModelRootGetter`: los modelos de namespace distinto de minecraft usan el nombre de ModelLayerLocation y su namespace cuando allowedCEM permite modelos de mods.

EMF 3.3.11, `EMFModelPartRoot` (lectura de variaciones): resuelve `<nombre-layer>.properties` mediante directoryContext; llama `ETFApi.getVariantSupplierOrNull(propertyID, modelID, new String[]{"models"})`. Para cada sufijo busca `<nombre-layer><sufijo>.jem` en el mismo namespace. `addAndSetVariantOfJem` aplica getCustomTexture a los estados de las partes nativas. `attach:true` conserva sus cubos e hijos.

ETF 7.2.5, `PropertiesRandomProvider.getAllValidPropertyObjects`: recorre reglas numeradas ordenadas, lee sufijos usando la clave recibida, pesos y propiedades. `NameProperty.getPropertyOrNull` admite `name` y `names`; compara el nombre personalizado con respeto a mayúsculas. Las reglas 1–3 son explícitas para Gore1–Gore3; regla 4 es el caso general de tres variantes con peso igual.

La validación del build comprueba la tabla de reglas y todos los JEM/texturas referenciados; **no es una ejecución del parser ETF ni del cliente Minecraft**. El diagnóstico con tres nombres permite comprobar la selección real sin depender de probabilidades dentro del modpack.
