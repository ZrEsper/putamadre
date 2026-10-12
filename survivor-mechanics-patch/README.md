# Habilidad Mecánica (.28)

Entrada: SurvivorCreator `.v27-small-death-first-person` del usuario. .28 añade `mechanics` a la lista de habilidades que `ActiveSkills.prune` conserva. La habilidad ya estaba registrada como CRAFTING pero era eliminada antes de poder ganar XP. Se usa su curva original de diez niveles y el sistema original de XP, guardado y notificación de subida de nivel.

Solo cambia `ActiveSkills.class` y se agrega un auxiliar para ampliar la lista sin duplicados. Se conservan íntegramente la pantalla de muerte, la creación del personaje, las texturas y el inicio en primera persona. Cuatro comprobaciones de lista, integridad ZIP y análisis del inicializador modificado; sin ejecución de Minecraft.

```sh
python survivor-mechanics-patch/build.py SURVIVOR_V27 TOOLS
```
