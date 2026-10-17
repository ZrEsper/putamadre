from pathlib import Path
import subprocess,zipfile,json,shutil
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';info=json.loads((r/'validation.json').read_text());jar=r/'output'/info['output'];src=r/'build/test-src';out=r/'build/test';shutil.rmtree(src,ignore_errors=True);src.mkdir();out.mkdir(exist_ok=True);ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn']
def run(*a):
 p=subprocess.run(list(map(str,a)),capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();return p.stdout.strip()
for p in (root/'vision-impact5/tests/nvg').rglob('*.java'):
 d=src/p.relative_to(root/'vision-impact5/tests/nvg');d.parent.mkdir(parents=True,exist_ok=True);d.write_bytes(p.read_bytes())
p=src/'com/raiiiden/warborn/common/util/HelmetVisionHandler.java';p.write_text(p.read_text().replace('case "thermal"', 'case "thermal_hybrid"->"htvg";case "thermal"'))
fixtures={
'com/mojang/blaze3d/vertex/DefaultVertexFormat.java':'package com.mojang.blaze3d.vertex;public class DefaultVertexFormat{public static Object NEW_ENTITY=new Object();}',
'com/mojang/blaze3d/vertex/VertexConsumer.java':'package com.mojang.blaze3d.vertex;public interface VertexConsumer{VertexConsumer addVertex(float x,float y,float z);VertexConsumer setColor(int r,int g,int b,int a);VertexConsumer setUv(float u,float v);VertexConsumer setUv1(int u,int v);VertexConsumer setUv2(int u,int v);VertexConsumer setNormal(float x,float y,float z);}',
'net/minecraft/client/renderer/MultiBufferSource.java':'package net.minecraft.client.renderer;public interface MultiBufferSource{com.mojang.blaze3d.vertex.VertexConsumer getBuffer(RenderType t);}',
'net/minecraft/client/renderer/RenderType.java':'package net.minecraft.client.renderer;public class RenderType{Object format;public RenderType(Object f){format=f;}public Object format(){return format;}public static RenderType entityCutoutNoCull(net.minecraft.resources.ResourceLocation id){return new RenderType(com.mojang.blaze3d.vertex.DefaultVertexFormat.NEW_ENTITY);}}'
}
for n,s in fixtures.items():p=src/n;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s)
run(*ecj,'-cp',jar,'-d',out,*src.rglob('*.java'),*(r/'tests').rglob('*.java'))
checks=[run('java','-Xverify:all','-cp',str(out)+':'+str(jar),'checks.NvgChecks'),run('java','-Xverify:all','-cp',str(out)+':'+str(jar),'checks.HybridChecks'),run('java','-Xverify:all','-cp',str(out)+':'+str(jar),'checks.HybridModeChecks')]
asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
run(*ecj,'-cp',asm,'-d',r/'build/tool',r/'Inspect.java')
checks.append(run('java','-Xverify:all','-cp',str(r/'build/tool')+':'+asm,'Inspect',jar))
with zipfile.ZipFile(root/'mods/fracturepoint-1.21.1-3.0.0-PT2-digital-exposure.5.jar')as old,zipfile.ZipFile(jar)as new:
 for n in old.namelist():
  if n.startswith('assets/fracturepoint/shaders/')or '/textures/'in n or n.endswith('/NvgSupport33.class') or n.endswith('ServerboundNVGTogglePacket.class')or n=='META-INF/neoforge.mods.toml':assert old.read(n)==new.read(n),n
 assert new.testzip()is None
checks.append('LEGACY_SHADERS_BATTERY_NETWORK_ASSETS_PRESERVED_CHECKS=1')
(r/'test-results.json').write_text(json.dumps({'checks':checks,'minecraft_runtime_tested':False},indent=2)+'\n')
