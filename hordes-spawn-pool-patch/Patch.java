import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
import java.nio.file.*;import java.util.*;import java.util.zip.*;
public class Patch implements Opcodes {
 static final String P="dev/zomboid/hordes/SpawnPool",U="dev/zomboid/hordes/SpawnPoolUi",ZI="dev/zomboid/hordes/ZombieIndividual",AS="dev/zomboid/hordes/client/AdminScreen";
 static int changes;
 static void count(String s){changes++;System.out.println("Patched "+s);}
 static void need(int n,int expected,String s){if(n!=expected)throw new IllegalStateException(s+" "+n+" != "+expected);}
 static MethodInsnNode call(String owner,String name,String desc){return new MethodInsnNode(INVOKESTATIC,owner,name,desc,false);}
 static void verify(ClassNode c)throws Exception{for(MethodNode m:c.methods)if((m.access&(ACC_ABSTRACT|ACC_NATIVE))==0)new Analyzer<BasicValue>(new BasicVerifier()).analyze(c.name,m);}
 static void delegate(MethodNode m,String owner,String name,String desc){m.instructions.clear();m.tryCatchBlocks.clear();if(m.localVariables!=null)m.localVariables.clear();m.instructions.add(new VarInsnNode(ILOAD,0));m.instructions.add(call(owner,name,desc));m.instructions.add(new InsnNode(IRETURN));count(name);}
 static byte[] patch(byte[] b)throws Exception {
  ClassNode c=new ClassNode();new ClassReader(b).accept(c,0);int before=changes;
  for(MethodNode m:c.methods){
   if(c.name.equals("dev/zomboid/hordes/ZoneSettings")){
    if(m.name.equals("install")){for(AbstractInsnNode n:m.instructions.toArray())if(n.getOpcode()==RETURN){InsnList h=new InsnList();for(int i=0;i<4;i++)h.add(new VarInsnNode(ALOAD,i));h.add(call(P,"install","(Ljava/util/Map;Ljava/util/Map;Ljava/util/Map;Ljava/util/Map;)V"));m.instructions.insertBefore(n,h);}count("persistent pool settings");}
    if(m.name.equals("applied")){InsnList h=new InsnList();h.add(new VarInsnNode(ALOAD,0));h.add(call(P,"sanitize","(Ljava/util/Map;)V"));m.instructions.insert(h);count("invalid saved pool fallback");}
    if(m.name.equals("persistence")){InsnList h=new InsnList();h.add(new VarInsnNode(ALOAD,0));h.add(call(P,"validate","(Ljava/util/Map;)V"));m.instructions.insert(h);count("pool validation before save");}
   }
   if(c.name.equals(ZI)){
    if(m.name.equals("profile")){
     InsnList h=new InsnList();h.add(new VarInsnNode(ALOAD,0));h.add(call(ZI,"data","(Lnet/minecraft/world/entity/Mob;)Lnet/minecraft/nbt/CompoundTag;"));h.add(call(P,"prepare","(Ljava/lang/Object;)V"));m.instructions.insert(h);
     for(AbstractInsnNode n:m.instructions.toArray())if(n.getOpcode()==ARETURN){InsnList a=new InsnList();a.add(new InsnNode(DUP));a.add(new VarInsnNode(ALOAD,0));a.add(new InsnNode(SWAP));a.add(call(P,"apply","(Ljava/lang/Object;Ljava/lang/Object;)V"));m.instructions.insertBefore(n,a);}count("authoritative final profile after all migrations");
    }
    if(m.name.equals("stats")){int hits=0;boolean damage=false;for(AbstractInsnNode n:m.instructions.toArray()){
     if(n instanceof FieldInsnNode f&&f.getOpcode()==GETSTATIC&&f.owner.equals(ZI)&&f.name.equals("DAMAGE"))damage=true;
     if(damage&&n instanceof MethodInsnNode mc&&mc.owner.equals(ZI)&&mc.name.equals("stat")){InsnList h=new InsnList();h.add(new VarInsnNode(ALOAD,0));h.add(call(P,"damageModifier","(DLjava/lang/Object;)D"));m.instructions.insertBefore(n,h);hits++;damage=false;}
    }need(hits,1,"damage modifier");count("personality damage multiplier");}
    if(m.name.equals("stat")){int hits=0;for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof JumpInsnNode j&&j.getOpcode()==IFLE&&n.getPrevious().getOpcode()==DCMPL){j.setOpcode(IFEQ);hits++;}need(hits,1,"negative attribute modifiers");count("damage reduction including zero damage");}
   }
   if(c.name.equals("dev/zomboid/reactive/Rules")&&Set.of("memory","windup","cooldown").contains(m.name))delegate(m,P,m.name,m.desc);
   if(c.name.equals("dev/zomboid/reactive/Reactive")){
    if(m.name.equals("speed")){int hits=0;for(AbstractInsnNode n:m.instructions.toArray())if(n.getOpcode()==DRETURN){InsnList h=new InsnList();h.add(new VarInsnNode(ALOAD,1));h.add(call(P,"speed","(DLjava/lang/Object;)D"));m.instructions.insertBefore(n,h);hits++;}need(hits,4,"speed branches");count("speed per personality");}
    if(m.name.equals("cap")){int hits=0;for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode mc&&mc.owner.equals("dev/zomboid/reactive/Rules")&&mc.name.equals("speed")){m.instructions.insertBefore(n,new VarInsnNode(ALOAD,3));mc.owner="dev/zomboid/reactive/Reactive";mc.desc="(ILjava/lang/Object;)D";hits++;}need(hits,1,"movement cap");count("navigation cap matches configured speed");}
   }
   if(c.name.equals("dev/zomboid/hordes/client/ScrollSpeed")&&m.name.equals("step")){InsnList h=new InsnList();h.add(new VarInsnNode(ALOAD,1));h.add(call(P,"wheelKey","(Ljava/lang/String;)Ljava/lang/String;"));h.add(new VarInsnNode(ASTORE,1));m.instructions.insert(h);count("fine wheel increments for multipliers");}
   if(c.name.equals(AS)){
    if(m.name.equals("<clinit>")){for(AbstractInsnNode n:m.instructions.toArray())if(n.getOpcode()==RETURN){InsnList h=new InsnList();for(String key:List.of("KEYS","LABELS","HINTS")){h.add(new FieldInsnNode(GETSTATIC,AS,key,"[[Ljava/lang/String;"));h.add(new LdcInsnNode(key));h.add(call(U,"rows","([[Ljava/lang/String;Ljava/lang/String;)[[Ljava/lang/String;"));h.add(new FieldInsnNode(PUTSTATIC,AS,key,"[[Ljava/lang/String;"));}m.instructions.insertBefore(n,h);}count("two simple spawn/personality tabs");}
    if(m.name.equals("init")){int tabs=0,layout=0;for(AbstractInsnNode n:m.instructions.toArray()){
     if(n instanceof LdcInsnNode ldc&&ldc.cst.equals("Cad\u00e1veres")){AbstractInsnNode end=n;while(end!=null&&end.getOpcode()!=AASTORE)end=end.getNext();if(end==null)throw new IllegalStateException("tabs");m.instructions.insert(end,call(U,"tabs","([Ljava/lang/String;)[Ljava/lang/String;"));tabs++;}
     if(n.getOpcode()==ICONST_3&&n.getNext() instanceof LabelNode){ // only second-row number of tab columns; exact shape tested below
      AbstractInsnNode next=n.getNext();while(next instanceof LabelNode||next instanceof FrameNode||next instanceof LineNumberNode)next=next.getNext();
      if(next instanceof VarInsnNode v&&v.getOpcode()==ISTORE){m.instructions.set(n,new InsnNode(ICONST_5));layout++;}
     }
     if(n instanceof LdcInsnNode ldc&&(ldc.cst.equals("Ver ejemplo")||ldc.cst.equals("Equilibrado"))){InsnList h=new InsnList();h.add(new VarInsnNode(ALOAD,0));h.add(call(U,"buttonText","(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;"));m.instructions.insert(n,h);}
    }need(tabs,1,"tab names");need(layout,1,"second row columns");count("nine tabs in two rows");}
    if(m.name.startsWith("lambda$init$")&&m.instructions.iterator().hasNext()){
     boolean preview=false;for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof TypeInsnNode t&&t.desc.equals("dev/zomboid/hordes/client/LootPreviewScreen"))preview=true;
     if(preview){LabelNode old=new LabelNode();InsnList h=new InsnList();h.add(new VarInsnNode(ALOAD,0));h.add(call(U,"preset","(Ljava/lang/Object;)Z"));h.add(new JumpInsnNode(IFEQ,old));h.add(new InsnNode(RETURN));h.add(old);h.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(h);count("easy balanced hard presets without changing loot");}
    }
    if(m.name.equals("render")){int hits=0;for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof FieldInsnNode f&&f.owner.equals(AS)&&f.name.equals("status")){InsnList h=new InsnList();h.add(new VarInsnNode(ALOAD,0));h.add(call(U,"status","(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;"));m.instructions.insert(n,h);hits++;}need(hits,1,"live normalized pool preview");count("live normalized percentages");}
    if(m.name.equals("mouseScrolled")){int hooks=0;for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode mc&&mc.owner.equals(AS)&&mc.name.equals("format")){InsnList h=new InsnList(); // string key local discovered from getKey cast
      int key=-1;for(AbstractInsnNode a:m.instructions.toArray())if(a instanceof TypeInsnNode cast&&cast.getOpcode()==CHECKCAST&&cast.desc.equals("java/lang/String")&&a.getNext() instanceof VarInsnNode v&&v.getOpcode()==ASTORE){key=v.var;break;}
      if(key<0)throw new IllegalStateException("scroll key");h.add(new VarInsnNode(ALOAD,key));h.add(new InsnNode(DUP_X2));h.add(new InsnNode(POP));h.add(call(U,"scroll","(Ljava/lang/String;D)D"));m.instructions.insertBefore(n,h);hooks++;
     }need(hooks,1,"wheel limits");count("wheel respects custom scalar limits");}
   }
   if(c.name.equals("dev/zomboid/hordes/AdminNetwork")&&m.name.equals("register")){int hits=0;for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof LdcInsnNode l&&l.cst.equals("26")){l.cst="28";hits++;}need(hits,1,"admin protocol");count("matched client server admin schema");}
  }
  if(before==changes)return b;ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);ClassNode v=new ClassNode();new ClassReader(w.toByteArray()).accept(v,0);verify(v);return w.toByteArray();
 }
 public static void main(String[] args)throws Exception{
  Map<String,byte[]> entries=new TreeMap<>();try(ZipFile zip=new ZipFile(args[0])){for(ZipEntry e:Collections.list(zip.entries())){byte[] b=zip.getInputStream(e).readAllBytes();entries.put(e.getName(),e.getName().endsWith(".class")?patch(b):b);}}
  for(String name:List.of(P,U)){byte[] b=Files.readAllBytes(Path.of(args[2],name+".class"));ClassNode c=new ClassNode();new ClassReader(b).accept(c,0);verify(c);entries.put(name+".class",b);}
  try(ZipOutputStream out=new ZipOutputStream(Files.newOutputStream(Path.of(args[1])))){for(var e:entries.entrySet()){ZipEntry entry=new ZipEntry(e.getKey());entry.setTime(0);out.putNextEntry(entry);out.write(e.getValue());out.closeEntry();}}
  System.out.println("Completed "+changes+" targeted patches.");
 }
}
