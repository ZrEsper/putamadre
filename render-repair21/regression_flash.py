"""Run existing battery, inventory, NPC and binding checks against the patched JAR."""
from pathlib import Path
r=Path(__file__).resolve().parent;original=r.parent/'flashlight-recovery17/test.py'
s=original.read_text().replace("jar=next((r/'output').glob('*.jar'))","jar=root/'render-repair21/output/flashlight-5.1-neoforge-1.21.1-gl-error-isolation.18.jar'").replace("(r/'test-results.json')","(root/'render-repair21/flashlight-regression-results.json')")
exec(compile(s,str(original),'exec'),{'__file__':str(original)})
