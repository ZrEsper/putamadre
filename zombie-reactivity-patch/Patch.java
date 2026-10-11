import java.nio.file.*;import java.util.*;import java.util.jar.*;import java.util.zip.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.tree.analysis.*;
public class Patch implements Opcodes {
 static final String H="dev/zomboid/reactive/Reactive";
 static void load(InsnList h,int op,int slot){h.add(new VarInsnNode(op,slot));}
 static void call(InsnList h,String name,String desc){h.add(new MethodInsnNode(INVOKESTATIC,H,name,desc,false));}
 static void replace(MethodNode m,InsnList h){m.instructions=h;m.tryCatchBlocks.clear();if(m.localVariables!=null)m.localVariables.clear();if(m.visibleLocalVariableAnnotations!=null)m.visibleLocalVariableAnnotations.clear();if(m.invisibleLocalVariableAnnotations!=null)m.invisibleLocalVariableAnnotations.clear();}
 static byte[] patch(byte[] b,String entry){
  ClassNode c=new ClassNode();new ClassReader(b).accept(c,0);int changes=0;
  for(MethodNode m:c.methods){
   if(c.name.equals("dev/zomboid/hordes/ZombieIndividual")){
    if(m.name.equals("sees")){InsnList h=new InsnList();load(h,ALOAD,0);load(h,ALOAD,1);call(h,"sees","(Ljava/lang/Object;Ljava/lang/Object;)Z");h.add(new InsnNode(IRETURN));replace(m,h);changes++;}
    if(m.name.equals("profile"))for(AbstractInsnNode n:m.instructions.toArray())if(n.getOpcode()==ARETURN){InsnList h=new InsnList();h.add(new InsnNode(DUP));load(h,ALOAD,0);h.add(new InsnNode(SWAP));call(h,"profile","(Ljava/lang/Object;Ljava/lang/Object;)V");m.instructions.insertBefore(n,h);changes++;}
    if(m.name.equals("stats"))for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode x&&x.owner.equals("dev/zomboid/hordes/PaceRules")&&x.name.equals("navigationSpeed")){m.instructions.insertBefore(n,new VarInsnNode(ALOAD,0));x.owner=H;x.name="speed";x.desc="(ILjava/lang/Object;)D";changes++;}
   }
   if(c.name.equals("dev/zomboid/awareness/Awareness")&&Set.of("tick","close","hear","radius").contains(m.name)){
    InsnList h=new InsnList();String desc=switch(m.name){case "tick"->"(Ljava/lang/Object;)V";case "close"->"(Ljava/lang/Object;Ljava/lang/Object;)Z";case "hear"->"(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;DJZ)Z";default->"(Ljava/lang/String;DZ)D";};
    int i=0;for(Type t:Type.getArgumentTypes(desc)){load(h,t.getOpcode(ILOAD),i);i+=t.getSize();}call(h,m.name,desc);h.add(new InsnNode(Type.getReturnType(desc).getOpcode(IRETURN)));replace(m,h);changes++;
   }
   if(c.name.equals("dev/zomboid/hordes/EventAttraction")&&m.name.equals("tick")){InsnList h=new InsnList();h.add(new InsnNode(RETURN));replace(m,h);changes++;}
   if(c.name.equals("dev/zomboid/hordes/ZombieCombat")){
    if(m.name.equals("tick")){MethodInsnNode eventCall=null;for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode x&&x.name.equals("getEntity")){eventCall=x;break;}if(eventCall==null)throw new IllegalStateException();InsnList h=new InsnList();load(h,ALOAD,0);h.add(new MethodInsnNode(eventCall.getOpcode(),eventCall.owner,eventCall.name,eventCall.desc,eventCall.itf));call(h,"playerTick","(Ljava/lang/Object;)V");m.instructions.insert(h);changes++;}
    if(m.name.equals("allowAttack")){InsnList h=new InsnList();load(h,ALOAD,0);load(h,ALOAD,1);call(h,"allowAttack","(Ljava/lang/Object;Ljava/lang/Object;)Z");LabelNode pass=new LabelNode();h.add(new JumpInsnNode(IFNE,pass));h.add(new InsnNode(ICONST_0));h.add(new InsnNode(IRETURN));h.add(pass);h.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(h);changes++;}
   }
   if(c.name.endsWith("/IndividualIdleGoal"))for(AbstractInsnNode n:m.instructions.toArray()){
    if(n instanceof LdcInsnNode x&&Double.valueOf(.65).equals(x.cst)){x.cst=.25;changes++;}
    if(n instanceof LdcInsnNode x&&Double.valueOf(.2).equals(x.cst)){x.cst=.08;changes++;}
    if(m.name.equals("start")&&n instanceof IntInsnNode x&&x.operand==20){x.operand=8;changes++;}
    if(m.name.equals("start")&&n instanceof IntInsnNode x&&x.operand==61){x.operand=25;changes++;}
   }
   if(c.name.endsWith("/ZombiePaceMixin"))for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode x&&x.owner.equals("dev/zomboid/hordes/PaceRules")&&x.name.equals("cap")){m.instructions.insertBefore(n,new VarInsnNode(ALOAD,0));x.owner=H;x.desc="(FIZLjava/lang/Object;)F";changes++;}
   if(c.name.equals("com/corosus/zombieawareness/ZAUtil")){
    if(m.name.equals("tickAI")){InsnList h=new InsnList();load(h,ALOAD,0);h.add(new MethodInsnNode(INVOKESTATIC,"dev/zomboid/zabridge/Bridge","controlled","(Ljava/lang/Object;)Z",false));LabelNode fallback=new LabelNode();h.add(new JumpInsnNode(IFEQ,fallback));h.add(new InsnNode(RETURN));h.add(fallback);h.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(h);changes++;}
    if(m.name.equals("handleBlockBasedEvent")){InsnList h=new InsnList();load(h,ALOAD,0);load(h,ALOAD,1);load(h,ALOAD,2);h.add(new MethodInsnNode(INVOKESTATIC,"dev/zomboid/zabridge/Bridge","block","(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V",false));m.instructions.insert(h);changes++;}
    if(m.name.equals("hookPlayEvent")){InsnList h=new InsnList();load(h,ILOAD,0);load(h,ALOAD,1);load(h,DLOAD,2);load(h,DLOAD,4);load(h,DLOAD,6);h.add(new MethodInsnNode(INVOKESTATIC,"dev/zomboid/zabridge/Bridge","levelEvent","(ILjava/lang/Object;DDD)V",false));m.instructions.insert(h);changes++;}
    if(m.name.equals("hookSoundEvent")){InsnList h=new InsnList();load(h,ALOAD,0);load(h,ALOAD,1);load(h,DLOAD,2);load(h,DLOAD,4);load(h,DLOAD,6);load(h,FLOAD,8);h.add(new MethodInsnNode(INVOKESTATIC,"dev/zomboid/zabridge/Bridge","sound","(Ljava/lang/Object;Ljava/lang/Object;DDDF)V",false));m.instructions.insert(h);changes++;}
   }
   if(c.name.endsWith("WeakerdayzombieprocedureProcedure")&&m.name.equals("execute")&&Type.getArgumentTypes(m.desc).length==3){InsnList h=new InsnList();load(h,ALOAD,1);load(h,ALOAD,2);h.add(new MethodInsnNode(INVOKESTATIC,"dev/zomboid/daylight/Daylight","apply","(Ljava/lang/Object;Ljava/lang/Object;)V",false));h.add(new InsnNode(RETURN));replace(m,h);changes++;}
   if(c.name.endsWith("WdztoggleprocedureProcedure"))for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof LdcInsnNode x&&x.cst instanceof String s){if(s.equals("Zombie are now slowed during day")){x.cst="Zombie daylight penalty disabled";changes++;}if(s.equals("Zombie are now frozen during day")){x.cst="Zombies are gently slowed during day (no freezing)";changes++;}}
  }
  if(changes==0)throw new IllegalStateException("No edits: "+entry);
  ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);byte[] result=w.toByteArray();ClassNode check=new ClassNode();new ClassReader(result).accept(check,0);try{for(MethodNode m:check.methods)if((m.access&(ACC_NATIVE|ACC_ABSTRACT))==0)new Analyzer<BasicValue>(new BasicVerifier()).analyze(check.name,m);}catch(Exception e){throw new IllegalStateException(entry,e);}System.out.println(entry+" : "+changes+" edits verified");return result;
 }
 public static void main(String[] a)throws Exception {
  Set<String> entries=switch(a[0]){case "hordes"->Set.of("dev/zomboid/hordes/IndividualIdleGoal.class","dev/zomboid/hordes/ZombieIndividual.class","dev/zomboid/awareness/Awareness.class","dev/zomboid/hordes/EventAttraction.class","dev/zomboid/hordes/ZombieCombat.class","dev/zomboid/hordes/mixin/ZombiePaceMixin.class");case "awareness"->Set.of("com/corosus/zombieawareness/ZAUtil.class");case "weaker"->Set.of("net/mcreator/weakerdayzombieneoforge/procedures/WeakerdayzombieprocedureProcedure.class","net/mcreator/weakerdayzombieneoforge/procedures/WdztoggleprocedureProcedure.class");default->Set.of();};
  try(JarFile in=new JarFile(a[1]);ZipOutputStream out=new ZipOutputStream(Files.newOutputStream(Path.of(a[2])))){
   for(var es=in.entries();es.hasMoreElements();){JarEntry e=es.nextElement();byte[] b=in.getInputStream(e).readAllBytes();if(entries.contains(e.getName()))b=patch(b,e.getName());
    if(a[0].equals("hordes")&&e.getName().equals("META-INF/neoforge.mods.toml"))b=(new String(b,java.nio.charset.StandardCharsets.UTF_8)+"\n[[mixins]]\nconfig=\"zomboid-reactive.mixins.json\"\n").getBytes(java.nio.charset.StandardCharsets.UTF_8);
    out.putNextEntry(new ZipEntry(e.getName()));out.write(b);out.closeEntry();
   }
   try(var files=Files.walk(Path.of(a[3]))){for(Path p:files.filter(Files::isRegularFile).toList()){String relative=Path.of(a[3]).relativize(p).toString();if(relative.endsWith(".class")&&!relative.startsWith("dev/"))continue;
    if(relative.endsWith("/MutantBiteMixin.class")){ClassNode mixin=new ClassNode();new ClassReader(Files.readAllBytes(p)).accept(mixin,0);if(mixin.invisibleAnnotations==null||mixin.invisibleAnnotations.stream().noneMatch(x->x.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")))throw new IllegalStateException("Mixin requires a CLASS-retained invisible @Mixin annotation");System.out.println("Mixin CLASS annotation verified");}out.putNextEntry(new ZipEntry(relative));out.write(Files.readAllBytes(p));out.closeEntry();}}
  }
 }
}
