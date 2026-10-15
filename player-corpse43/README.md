# Infected player to native Corpse: Hordes .43, Remains .12, Corpse .2

Exact uploaded inputs and inspected Cosmetic Armor functional.2 hash are pinned in
inputs.json. Build/test/package from repo root with the three corresponding Python
scripts in this directory. Java 21 and the shared pinned ECJ/ASM cache are required.
The Hordes 1.6.3f PlayerZombie API was inspected from the existing repository artifact;
its native infection, conversion and inventory serialization are not changed.

HordeEvents.killed (LOWEST) captures accepted player deaths after The Hordes has created
its pending player zombie. Death metadata/equipment and cosmetic previews are persisted
on the zombie; they do not confer loot ownership. PlayerGrave's protected join path
restores visible copied equipment, preferring cosmetic armor per user choice, sets
zero equipment drop chance to avoid dropping appearance copies, and disables new mob
pickup. The authoritative stored inventory remains the source of corpse contents.

CosmeticArmor's native canceled-drop path can skip cosmetic drops entirely. A short
strong pending map (maximum 40 server ticks) waits until the zombie actually exists in
the level before moving matching original cosmetic stacks to its stored cargo. This
avoids consuming source items when spawning fails. Empty/changed slots and keep rules
are respected. If the mod already dropped cosmetics normally, the empty source cannot
produce another copy. Pending references are cleared on commit/expiry. An immediate
zombie death finalizes that transfer in its drops handler and adds late moved items to
the native fallback drop collection before attempting Corpse creation. A listener
exception after clearing the cosmetic source is treated as committed ownership.

A HIGHEST LivingDrops subscriber creates a real CorpseEntity with the original owner,
name/model, current location and equipped appearance. Its additional-items inventory
gets copies of the authoritative PlayerZombie cargo, without display copies or random
Remains loot. Only successful addFreshEntity commits ownership: source cargo and native
drops are then cleared, the event canceled and a persistent done flag prevents repeats.
If creation fails/rejects, native drops are not suppressed. The existing Corpse entity
provides inventory containers, gravity, persistence and access rules. No client-only
setDeath method is called on the dedicated server; the common private death field is
assigned before spawning, following native createFromDeath's field setup.

Remains' death handler independently excludes the PlayerZombie interface (including
Husk/Drowned variants) so it cannot turn this cargo into a Remains block. Other native
loot logic is byte-identical. Corpse's ItemUtils.saveItemList strips empty entries; its
positional Equipment reader then shifts armor. A narrow Equipment-only writer preserves
empty CompoundTags, leaving ordinary item-list serialization unchanged. The public
schema and network protocol stay the same. Cosmetic Armor itself is unchanged.

Validation: ASM BasicVerifier on four Hordes hooks/two classes plus one Remains and one
Corpse hook. 149 API-double checks exercise the actual new transfer helper and native
Corpse inventory serializer across immediate/deferred transfer, single ownership,
creation rejection, post-clear notification failure, repeated events, keep rules,
changed respawn gear, legacy cargo, 64 equipment masks and >100 stacks with components.
A separate native serializer probe reproduces HELMET_SLOT=0 on the exact old Corpse and
HELMET_SLOT=5 after the patch; ordinary item lists stay compact in both. These do not
execute Minecraft, its event bus, real networking, render layers or dedicated-server
lifecycle. Those remain explicit integration checks. Existing player zombies can recover
stored cargo; prior missing cosmetic items cannot be invented.

The combined distribution also includes the already-published Isometric .30 medical
input fix. FirstAid 3.6.3, LR .4 and Cosmetic Armor .2 are kept unchanged. A terminal
infection hit can interrupt a native rescue attempt; this patch does not turn the
defibrillator into an infection cure or change the server's rescue conditions.
