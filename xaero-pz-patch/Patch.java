import java.nio.file.*;import java.util.*;import java.util.jar.*;import java.util.zip.*;
import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.tree.analysis.*;
public class Patch implements Opcodes {
 static final String H="xaero/pz/Fog";
 static void load(InsnList list,int op,int slot){list.add(new VarInsnNode(op,slot));}
 static void hook(InsnList list,String name,String desc){list.add(new MethodInsnNode(INVOKESTATIC,H,name,desc,false));}
 static int local(MethodNode m,String name){return m.localVariables.stream().filter(v->v.name.equals(name)).findFirst().orElseThrow().index;}
 static byte[] change(byte[] bytes,String mode,String entry)throws Exception {
  ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);int changes=0;
  for(MethodNode m:c.methods){
   if(entry.endsWith("MinimapFBORenderer.class")&&m.name.equals("renderChunksToFBO")) {
    for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode call&&call.name.equals("endBatch")&&call.desc.equals("()V")) {
     InsnList h=new InsnList();load(h,ALOAD,local(m,"matrixStack"));load(h,ALOAD,local(m,"overlayBufferBuilder"));load(h,ILOAD,local(m,"xFloored"));load(h,ILOAD,local(m,"zFloored"));load(h,DLOAD,local(m,"radiusBlocks"));load(h,ALOAD,5);hook(h,"mini","(Ljava/lang/Object;Ljava/lang/Object;IIDLjava/lang/Object;)V");m.instructions.insertBefore(n,h);changes++;break;
    }
   }
   if(entry.endsWith("MinimapSafeModeRenderer.class")&&m.name.equals("updateMapFrameSafeMode")){InsnList h=new InsnList();hook(h,"prepare","()V");m.instructions.insert(h);changes++;}
   if(entry.endsWith("MinimapSafeModeRenderer.class")&&m.name.equals("getLoadedBlockColor"))for(AbstractInsnNode n:m.instructions.toArray())if(n.getOpcode()==RETURN){InsnList h=new InsnList();load(h,ALOAD,3);load(h,ILOAD,4);load(h,ILOAD,5);hook(h,"safeColor","([III)V");m.instructions.insertBefore(n,h);changes++;}
   if(entry.endsWith("GuiMap.class")&&m.name.equals("render")) {
    for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode call&&call.name.equals("endBatch")&&call.desc.equals("()V")) {
     // Only terrain FBO flush, not subsequent menus/map elements. Identify renderTypeBuffers local 120.
     AbstractInsnNode p=n.getPrevious();while(p instanceof LabelNode||p instanceof LineNumberNode||p instanceof FrameNode)p=p.getPrevious();
     if(!(p instanceof VarInsnNode v)||v.var!=local(m,"renderTypeBuffers"))continue;
     InsnList h=new InsnList();load(h,ALOAD,0);load(h,ALOAD,local(m,"matrix"));load(h,ALOAD,local(m,"overlayBuffer"));load(h,ILOAD,local(m,"flooredCameraX"));load(h,ILOAD,local(m,"flooredCameraZ"));
     for(String s:List.of("leftBorder","topBorder","rightBorder","bottomBorder"))load(h,DLOAD,local(m,s));hook(h,"big","(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;IIDDDD)V");m.instructions.insertBefore(n,h);changes++;break;
    }
   }
   if(entry.endsWith("module/MinimapRenderer.class")&&m.name.equals("render")&&!m.desc.contains("ModuleSession")){
    // Actual method (not compiler bridge). Context slot 2.
    InsnList h=new InsnList();load(h,ALOAD,2);hook(h,"layout","(Ljava/lang/Object;)V");m.instructions.insert(h);changes++;
    for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode call&&call.name.equals("onRender")){
     InsnList f=new InsnList();load(f,ALOAD,2);load(f,ALOAD,3);hook(f,"frame","(Ljava/lang/Object;Ljava/lang/Object;)V");m.instructions.insert(n,f);changes++;break;
    }
   }
   if(entry.endsWith("MinimapConfigClientUtils.class")&&m.name.equals("getEffectiveMinimapSize"))for(AbstractInsnNode n:m.instructions.toArray())if(n.getOpcode()==IRETURN){InsnList h=new InsnList();hook(h,"smaller","(I)I");m.instructions.insertBefore(n,h);changes++;}
   if(entry.endsWith("BuiltInInfoDisplays.class")&&m.name.startsWith("lambda$")){
    boolean coords=false;for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof InvokeDynamicInsnNode d)for(Object arg:d.bsmArgs)if(arg instanceof String s&&s.equals("\u0001, \u0001, \u0001"))coords=true;
    boolean time=false;for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof LdcInsnNode l&&Long.valueOf(6000L).equals(l.cst))time=true;
    if(time){m.instructions.clear();m.instructions.add(new InsnNode(RETURN));m.tryCatchBlocks.clear();m.localVariables.clear();changes++;}
    if(coords){for(AbstractInsnNode n:m.instructions.toArray())if(n.getOpcode()==RETURN){
      // Early disabled-display return must remain silent. Only inject after coordinates were emitted.
      AbstractInsnNode prev=n.getPrevious();while(prev instanceof LabelNode||prev instanceof LineNumberNode||prev instanceof FrameNode)prev=prev.getPrevious();
      if(prev instanceof MethodInsnNode call&&call.name.equals("addLine")) {InsnList h=new InsnList();load(h,ALOAD,1);hook(h,"info","(Ljava/lang/Object;)V");m.instructions.insertBefore(n,h);changes++;}
     }}
   }
   if(entry.endsWith("WorldMapSession.class")&&m.name.equals("cleanup")){InsnList h=new InsnList();hook(h,"flush","()V");m.instructions.insert(h);changes++;}
  }
  if(changes==0)throw new IllegalStateException("No hooks: "+entry);
  // A type-neutral hook uses existing stack frames; recompute only maximum stack size.
  ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);byte[] result=w.toByteArray();
  ClassNode check=new ClassNode();new ClassReader(result).accept(check,0);for(MethodNode m:check.methods)if((m.access&(ACC_ABSTRACT|ACC_NATIVE))==0)new Analyzer<BasicValue>(new BasicVerifier()).analyze(check.name,m);
  System.out.println(entry+": "+changes+" hooks, verified");return result;
 }
 public static void main(String[] a)throws Exception {
  String mode=a[0];Set<String> changed=mode.equals("world")?Set.of("xaero/map/gui/GuiMap.class","xaero/map/WorldMapSession.class"):Set.of("xaero/common/minimap/render/MinimapSafeModeRenderer.class","xaero/common/minimap/render/MinimapFBORenderer.class","xaero/hud/minimap/module/MinimapRenderer.class","xaero/hud/minimap/config/util/MinimapConfigClientUtils.class","xaero/hud/minimap/info/BuiltInInfoDisplays.class");
  try(JarFile in=new JarFile(a[1]);ZipOutputStream out=new ZipOutputStream(Files.newOutputStream(Path.of(a[2])))) {
   for(var entries=in.entries();entries.hasMoreElements();){JarEntry e=entries.nextElement();byte[] b=in.getInputStream(e).readAllBytes();if(changed.contains(e.getName()))b=change(b,mode,e.getName());out.putNextEntry(new ZipEntry(e.getName()));out.write(b);out.closeEntry();}
   if(mode.equals("world"))try(var files=Files.walk(Path.of(a[3]))){for(Path p:files.filter(Files::isRegularFile).toList())if(p.toString().endsWith(".class")){out.putNextEntry(new ZipEntry(Path.of(a[3]).relativize(p).toString()));out.write(Files.readAllBytes(p));out.closeEntry();}}
  }
 }
}
