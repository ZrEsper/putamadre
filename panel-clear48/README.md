# Admin panel .48: remove blur from custom content

The .47 basic admin screen draws `SimplePanel.render` before calling `Screen.render`. The parent's background blur therefore affects the already drawn labels, icons and sliders, while widgets drawn afterwards remain sharp. The loot preview has the same order.

Adds no-op `renderBackground(GuiGraphics,int,int,float)` and `renderBlurredBackground(float)` overrides to `AdminScreen` and `LootPreviewScreen`. Each custom screen already draws its own background. This disables blur only for these two screens and does not change global menu settings, controls, profiles, loot, population, protocol or saved values. Every other ZIP entry is byte-identical to .47.

`python panel-clear48/build.py` builds with pinned ECJ/ASM. `python panel-clear48/test.py` extracts and executes the original basic/preview render branches with a parent background sequence: the .47 sequence reproduces blurred custom content; the new overrides preserve sharp content and still draw widgets. ASM also verifies all methods. This is a bytecode/API regression, not a Minecraft runtime test.

Distributed with FracturePoint .8 in `distribuciones/Zombies-Poldo-Vision-8-Panel-48.zip`. Existing Quietly .18 is retained.
