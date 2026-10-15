import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.tree.analysis.*;import java.nio.file.*;import java.util.*;import java.util.zip.*;
public class Patch implements Opcodes{
 static final String H="dev/zomboid/hordes/PlayerCorpse43";static int hooks;
 static MethodInsnNode call(String name,String desc){return new MethodInsnNode(INVOKESTATIC,H,name,desc,false);}
 static void event(ClassNode c){MethodNode m=new MethodNode(ACC_PUBLIC|ACC_STATIC,"zp43$playerZombieDrops","(Lnet/neoforged/neoforge/event/entity/living/LivingDropsEvent;)V",null,null);AnnotationNode a=new AnnotationNode("Lnet/neoforged/bus/api/SubscribeEvent;");a.values=new ArrayList<>(List.of("priority",new String[]{"Lnet/neoforged/bus/api/EventPriority;","HIGHEST"}));m.visibleAnnotations=new ArrayList<>(List.of(a));m.instructions.add(new VarInsnNode(ALOAD,0));m.instructions.add(call("drops","(Ljava/lang/Object;)V"));m.instructions.add(new InsnNode(RETURN));c.methods.add(m);hooks++;}
 static byte[]patch(byte[]b,String kind)throws Exception{ClassNode c=new ClassNode();new ClassReader(b).accept(c,0);int before=hooks;
 for(MethodNode m:c.methods){
 if(kind.equals("hordes")&&c.name.equals("dev/zomboid/hordes/HordeEvents")){
 if(m.name.equals("killed")){InsnList x=new InsnList();x.add(new VarInsnNode(ALOAD,0));x.add(call("capture","(Ljava/lang/Object;)V"));m.instructions.insert(x);hooks++;}
 if(m.name.equals("serverTick")){m.instructions.insert(call("tick","()V"));hooks++;}}
 if(kind.equals("hordes")&&c.name.equals("dev/zomboid/hordes/PlayerGrave")&&m.name.equals("event")){
 for(AbstractInsnNode n:m.instructions.toArray())if(n.getOpcode()==ICONST_1&&n.getNext().getOpcode()==IRETURN){InsnList x=new InsnList();x.add(new VarInsnNode(ALOAD,1));x.add(call("join","(Ljava/lang/Object;)V"));m.instructions.insertBefore(n,x);hooks++;}}
 if(kind.equals("remains")&&c.name.equals("dev/zomboid/remains/ZombieRemainsLoot")&&m.name.equals("death")){
 // Independent guard has no hard dependency on our Hordes helper.
 InsnList x=new InsnList();x.add(new VarInsnNode(ALOAD,0));x.add(new MethodInsnNode(INVOKEVIRTUAL,"net/neoforged/neoforge/event/entity/living/LivingDropsEvent","getEntity","()Lnet/minecraft/world/entity/LivingEntity;",false));x.add(new MethodInsnNode(INVOKESTATIC,"dev/zomboid/remains/PlayerZombieGuard43","entity","(Ljava/lang/Object;)Z",false));LabelNode pass=new LabelNode();x.add(new JumpInsnNode(IFEQ,pass));x.add(new InsnNode(RETURN));x.add(pass);x.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(x);hooks++;}
 if(kind.equals("corpse")&&c.name.equals("de/maxhenkel/corpse/corelib/item/ItemUtils")&&m.name.equals("saveItemList")){
 InsnList x=new InsnList();for(int i=0;i<4;i++)x.add(new VarInsnNode(ALOAD,i));x.add(new MethodInsnNode(INVOKESTATIC,"dev/zomboid/corpse/EquipmentCodec43","write","(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Z",false));LabelNode pass=new LabelNode();x.add(new JumpInsnNode(IFEQ,pass));x.add(new InsnNode(RETURN));x.add(pass);x.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(x);hooks++;}
 }
 if(kind.equals("hordes")&&c.name.equals("dev/zomboid/hordes/HordeEvents"))event(c);
 if(hooks==before)return b;ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);ClassNode check=new ClassNode();new ClassReader(w.toByteArray()).accept(check,0);for(MethodNode m:check.methods)if((m.access&(ACC_NATIVE|ACC_ABSTRACT))==0)new Analyzer<BasicValue>(new BasicVerifier()).analyze(check.name,m);return w.toByteArray();}
 public static void main(String[]a)throws Exception{Map<String,byte[]>e=new TreeMap<>();try(ZipFile z=new ZipFile(a[0])){for(ZipEntry n:Collections.list(z.entries())){byte[]b=z.getInputStream(n).readAllBytes();e.put(n.getName(),n.getName().endsWith(".class")?patch(b,a[3]):b);}}int expected=a[3].equals("hordes")?4:1;if(hooks!=expected)throw new IllegalStateException(a[3]+" hooks "+hooks);try(var fs=Files.walk(Path.of(a[2]))){for(Path p:fs.filter(p->p.toString().endsWith(".class")).toList())e.put(Path.of(a[2]).relativize(p).toString(),Files.readAllBytes(p));}try(ZipOutputStream z=new ZipOutputStream(Files.newOutputStream(Path.of(a[1])))){for(var n:e.entrySet()){ZipEntry ze=new ZipEntry(n.getKey());ze.setTime(0);z.putNextEntry(ze);z.write(n.getValue());z.closeEntry();}}System.out.println(a[3]+": "+hooks+" hooks verified");}
}
