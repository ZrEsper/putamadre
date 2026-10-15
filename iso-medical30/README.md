# Isometric medical input .30

Base is the preceding .29 release; SHA-256 and the two exact inspected user input hashes
are recorded in inputs.json. FirstAid 3.6.3 and LR .4 remain unchanged. Run:

```
python iso-medical30/build.py
python iso-medical30/test.py
python iso-medical30/package.py
```

Java 21 and SHA-pinned shared ECJ/ASM tools are required.

AimStability's registry-name heuristic missed LR's generic consumable items, whose
individual medical identity resides in data. NativeActions only excluded vehicles and
food; KeyBindingMixin could therefore replace keyUse.isDown with Mod.using, while
medical/toggle handlers used the same button to maintain treatment and self-defib.

MedicalInput30 recognizes the optional LR IConsumable interface and existing FirstAid/
medical items. Recognition intentionally never reads keyUse, avoiding recursion through
KeyBindingMixin -> NativeActions -> recognition. An active use item takes priority, then
held medical items; an offhand medical item doesn't hijack a normal gun unless the
FirstAid model says critical. Missing optional mod classes are handled as absent.

The existing nativeInput bypass now handles medical items before the first key read.
A transition clears click-to-move targets and stale gun input without zeroing velocity
or blocking manual movement. KeyboardInputMixin skips cursor navigation during medical
use, AimStability uses the same treatment recognition and the late Minecraft camera
controller yields while treating. No player health, charge counts, rescue validation,
medical animation code or movement speeds change. .29 audio/aim interaction fixes remain.

69 checks execute new helpers and the real patched NativeActions against API doubles.
A separate process runs the same physical self-defib key probe on the old and new native
NativeActions: false before, true after. This establishes the client input conflict;
it does not claim to explain every possible server-side rescue rejection. ASM BasicVerifier
checks all four modified native classes. Minecraft/multiplayer/visual validation remains
outstanding. No replacement FirstAid/LR binaries or new rescue network messages are needed.
