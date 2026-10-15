# Manual visor NVG .3

Uses the exact uploaded `.2` artifact, SHA-256 in inputs.json. Build with
`python nvg-visor3/build.py`, then `python nvg-visor3/test.py`. Java 21 and pinned
ECJ/ASM caches are shared with preceding patch projects. Tests are API doubles;
Minecraft/GPU validation remains outstanding.

The previous version interpreted absent helmet_top_open as closed and reconciled
that state into automatic activation. It also retained a separate manual fallback
for helmets without a physical visor. This revision requires a tagged physical visor,
initializes its saved position to raised and starts each newly worn helmet/session
raised. A per-stack UUID distinguishes genuine equipment changes from synchronized
copies of the same helmet; a weak per-player map tracks the worn token. Repeated
checks do not rewrite stack components. N still drives the original animation; its
native midpoint is what changes position and consequently the filter/effect/drain.

WBArmorItem.isTopOpen now shares the same default/state reading as the linked
controller. No alternate non-visor activation remains. The original server energy
cache, NvgSupport33 infinite effect/backup and 900 active seconds of drain are retained.

A HEAD callback in the existing ShaderProtectionMixin processes the native registry
at GameRenderer.render(DeltaTracker,boolean), independently of AFTER_PARTICLES delivery.
The .2 chain-identity recovery remains. No shader JSON, GLSL, textures, presets or
uniform defaults change. This addresses scheduling and state handling; it does not
establish the cause of a specific GPU failure without a current runtime log.

Only four existing classes change. ASM BasicVerifier validates the two rewritten
native classes. 935 checks execute actual helpers and NvgSupport33 against doubles,
including native infinite-effect application and repair, instead of claiming that
shader flags alone demonstrate visual rendering. The frame driver is tested against
a registry double; real GPU rendering is explicitly untested.
