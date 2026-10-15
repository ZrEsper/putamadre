# Isometric player audio and aim interaction guard .29

Exact uploaded `.28-medical-cursor-stability` base is pinned in inputs.json.
Run `python iso-audio29/build.py` and `python iso-audio29/test.py`. Java 21 and
SHA-pinned ECJ/ASM caches are shared with spas-reload18. Tests use API doubles;
no Minecraft integration or audible listening test has been performed.

SoundReference.point previously blended detached camera position with the player's
eyes using soundListenerBias. It now returns interpolated player eyes (camera fallback
when absent). The existing ClientWorldMixin uses this same point for sound delay.
The existing SoundSystemMixin callback captures position and head orientation on the
render thread, then submits an immutable ListenerTransform on SoundEngineExecutor.
The forward/up basis is orthogonal, including vertical looking directions. Native
listener updates remain available when isometric is disabled or initialization fails.

A single Bridge.mouse entry guard prioritizes RMB gun aiming over interactable cursor
hits, leaving native release handling intact. Shift+RMB passes to existing interaction
logic when not already aiming. A client-only MultiPlayerGameMode mixin guards useItemOn,
interact and interactAt with PASS while holding a TaCZ gun and aiming. It leaves useItem,
shooting and unrelated screens/input untouched. Ordinary block and entity interactions
are covered; arbitrary GUI packets/custom actions from other mods are not globally
intercepted. CameraLock's existing aim state is used; server-side gun mechanics do not
change. The normal interaction paths return immediately when aim ends.

Three native hooks plus one mixin with three exact method selectors; ASM BasicVerifier
checks modified/new methods. Only those native classes and mixin configuration change;
all other .28 code/resources remain byte-identical. 380 behavioral checks exercise the
actual new helper classes with API doubles. Mixin application in Minecraft remains an
explicit runtime validation requirement.
