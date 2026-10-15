# Doomsday Climate / Storage .3

Build and test from repository root:

```bash
python decor-climate3/build.py
python decor-climate3/test.py
python decor-climate3/package.py
```

Inputs are the three exact uploaded binaries and Hordes .41, pinned in inputs.sha256.json. ECJ and ASM hashes are verified. expected-changes.json asserts every added/changed entry. All original metadata and dependency ranges are retained byte-for-byte, including Storage .2's compatible internal version. File names distinguish the new revisions.

The reported desk and washingmachine blocks actually had `container:false`; Jade's display does not establish a MenuProvider. Storage.IDS grows from 71 to 94 (17 additional storage blocks plus six AC devices). Specs select the existing PortedAcrateBlock factory; its shared BlockEntityType valid block array expands through the existing Storage.validBlocks hook. Missing entities on existing selected furniture are created server-side on use or in the existing near-player maintenance pass. Foreign or already-populated block entities are never replaced. Native inventory save/load is retained. Air conditioners reject item insertion and expose no automation slots or inventory menu.

Climate adds a server ticker to all six ACs and a registered, synthesized four-second mono fan sound. Power uses MachineEntity.GENERATORS within 25 blocks, requires positive native fuel-burn ticks or queued furnace fuel (avoiding a full-battery/refuel deadlock), simulates then extracts 400 FE once per 20 ticks and marks the generator dirty. It does not independently delete fuel or alter GeneratorFuel. ddIce=-1 persists manual off; ddChill indicates current power; existing save/load fields are reused. Weak world-scoped position leases expire after 20 ticks and validate loaded, existing, enabled AC entities; no chunk loads. An overlapping area is a boolean union, not stacked effects.

Homeostatic hooks EnvironmentData's constructor, BodyTemperature's five-argument constructor and Temperature.checkTemperatureLevel. The former selects a 22 °C local environment; the latter two normalize core/skin/last-skin and suppress thermal damage only in a powered area. Native attachment writes and packets remain active. TAN hooks its server onPlayerTick: set neutral current/target, clear hyperthermia and delay, remove its heat-exhaustion modifier, then skip the competing thermal update. Native server-player change detection sends the usual packet. Both bridges are optional and fall back to native behavior if Doomsday is absent; bridge failures log once.

CropGrowEvent.Post performs one 35% roll after genuine growth, only for CropBlock and only in a powered union area. It increments the existing age property rather than replacing other state properties. It neither runs random ticks itself nor bypasses normal first-stage growth checks. Reaching maximum age naturally caps the extra stage; this is an expected growth-event bonus, not an exact 35% reduction in maturation time for every plant.

Boards have destroy_time 6, hand harvesting allowed and unchanged block IDs. Boards.factor returns 3 only for glass/window blocks with adjacent Doomsday planking. PlayerEvent.BreakSpeed divides mining speed; optional Hordes BarricadeLink scales native BarrierBreakGoal duration and SiegeZombie.breakBlock timing. Original speed settings, permission checks and grief veto remain native. Other Hordes classes remain byte-identical to .41.

Tests run the real replacement helpers, optional adapters and Hordes bridge against API doubles. They cover furniture occupancy/save/load regression, 94 legal inventory configurations, missing entity repair without replacing foreign data, generator energy/fuel shortage, duplicate ticks, shutdown, removal/unload/lease expiry, radius/dimensions, all six AC variants, blocked hopper insertion, both temperature APIs, 35% growth distribution, overlap equality, mature crops and barricade timer scaling. Minecraft itself and the complete modpack have not been launched.

The uploaded log is startup only and does not capture a failed furniture interaction. The known missing registrations were confirmed from the uploaded JAR, not inferred from unrelated startup warnings.
