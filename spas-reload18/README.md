# SPAS reload guard .18

Base: exact user-uploaded TaCZ `.17-tracers-impacts-scope-limits`; SHA-256 pinned
in `inputs.json`. Tactical `.1` remains unchanged. Scope is the reported server
crash `tacz_spas_12_gun_logic:84`, indexing missing `interrupted_time`.

`build.py` verifies the base and cached compiler/ASM dependencies, extracts the
bundled LuaJ runtime and builds the patch. `test.py` uses that actual LuaJ engine,
reproduces the original crash and compares valid old/new reload traces. API doubles
stand in for inventory/player methods; this is not a Minecraft integration test.

The single native call substitution in `ModernKineticGunItem.lambda$tickReload$12`
routes through `Reload18.invoke`. For `tacz:spas_12`, an absent/malformed standard
reload cache returns `NOT_RELOADING, -1`, letting the original shooter state machine
clear the timestamp. Nothing is synthesized from an elapsed timestamp and no ammo
methods are called on cancellation. This also protects existing extracted default
packs; an embedded Lua guard covers fresh ones. Valid calls and unrelated errors
continue through the original Lua function. Other gun IDs bypass cache validation.
A custom SPAS script must retain the default reload-cache contract.

The log proves missing reload state; it does not establish which upstream action
removed it. This patch guards the failure instead of claiming an unproven root
cause. Original server reload checks, other functions, ballistics and `.17` visual
changes remain untouched. ASM BasicVerifier checks all methods of the changed class.
The pinned enum has NOT_RELOADING ordinal zero. No network protocol changes.

Run from the checkout:

```
python spas-reload18/build.py
python spas-reload18/test.py
python spas-reload18/package.py
```

Java 21 required. Cached ECJ/ASM paths and hashes follow the preceding patch projects.
Minecraft/NeoForge runtime validation is outstanding.
