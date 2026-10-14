import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.tree.analysis.*;import java.nio.file.*;import java.util.*;import java.util.zip.*;
public class Patch implements Opcodes {
 static final String CP="dev/zomboid/hordes/CombatPanel",CS="dev/zomboid/hordes/CombatSync",IS="dev/zomboid/hordes/IndoorSpawn",HELP="dev/zomboid/hordes/PanelHelp",M="dev/zomboidmelee/combat/PanelCooldown",AS="dev/zomboid/hordes/client/AdminScreen";
 static int changes;static void count(String s){changes++;System.out.println("Patched "+s);}static void need(int actual,int expected,String s){if(actual!=expected)throw new IllegalStateException(s+": "+actual+" != "+expected);}static MethodInsnNode call(String o,String n,String d){return new MethodInsnNode(INVOKESTATIC,o,n,d,false);}
 static void verify(ClassNode c)throws Exception{for(MethodNode m:c.methods)if((m.access&(ACC_ABSTRACT|ACC_NATIVE))==0)new Analyzer<BasicValue>(new BasicVerifier()).analyze(c.name,m);}
 static byte[] patch(byte[] b,String kind)throws Exception {
 ClassNode c=new ClassNode();new ClassReader(b).accept(c,0);int before=changes;
 for(MethodNode m:c.methods){
  if(kind.equals("hordes")){
   if(c.name.equals("dev/zomboid/hordes/ZoneSettings")&&m.name.equals("install")){for(AbstractInsnNode n:m.instructions.toArray())if(n.getOpcode()==RETURN){InsnList h=new InsnList();for(int i=0;i<4;i++)h.add(new VarInsnNode(ALOAD,i));h.add(call(CP,"install","(Ljava/util/Map;Ljava/util/Map;Ljava/util/Map;Ljava/util/Map;)V"));m.instructions.insertBefore(n,h);}count("world melee and indoor settings");}
   if(c.name.equals("dev/zomboid/hordes/HordeEvents")){
    if(m.name.equals("serverTick")){InsnList h=new InsnList();h.add(new VarInsnNode(ALOAD,0));h.add(call(CS,"tick","(Ljava/lang/Object;)V"));m.instructions.insert(h);count("all-player cooldown sync");}
    if(m.name.equals("spawn")){int hits=0;for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode mc&&mc.owner.equals("dev/zomboid/hordes/ZoneReserve")&&mc.name.equals("add")){InsnList h=new InsnList();h.add(new InsnNode(DUP2));h.add(call(IS,"relocate","(Ljava/lang/Object;Ljava/lang/Object;)V"));m.instructions.insertBefore(n,h);hits++;}need(hits,1,"horde spawn");count("horde replacement indoors");}
   }
   if(c.name.equals("dev/zomboid/hordes/ChunkPopulation")&&m.name.equals("spawnInChunk")){int hits=0;boolean found=false;for(AbstractInsnNode n:m.instructions.toArray()){
    if(n instanceof LdcInsnNode l&&l.cst.equals("addFreshEntity"))found=true;
    if(found&&n instanceof MethodInsnNode mc&&mc.owner.equals("dev/zomboid/hordes/ZoneReserve")&&mc.name.equals("invoke")){InsnList h=new InsnList();h.add(new VarInsnNode(ALOAD,0));h.add(call(IS,"before","([Ljava/lang/Object;Ljava/lang/Object;)[Ljava/lang/Object;"));m.instructions.insertBefore(n,h);found=false;hits++;}
   }need(hits,1,"population spawn");count("population replacement indoors");}
   if(c.name.equals("dev/zomboid/hordes/WorldTuning")&&m.name.equals("stopped")){for(AbstractInsnNode n:m.instructions.toArray())if(n.getOpcode()==RETURN)m.instructions.insertBefore(n,call(CS,"reset","()V"));count("clear world integration caches on stop");}
   if(c.name.equals("dev/zomboid/hordes/AdminNetwork")&&m.name.equals("register")){int hits=0;for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof LdcInsnNode l&&l.cst.equals("28")){l.cst="29";hits++;}need(hits,1,"protocol");count("matching control schema");}
   if(c.name.equals(AS)){
    if(m.name.equals("<clinit>")){for(AbstractInsnNode n:m.instructions.toArray())if(n.getOpcode()==RETURN){InsnList h=new InsnList();for(String k:List.of("KEYS","LABELS","HINTS")){h.add(new FieldInsnNode(GETSTATIC,AS,k,"[[Ljava/lang/String;"));h.add(new LdcInsnNode(k));h.add(call(CP,"rows","([[Ljava/lang/String;Ljava/lang/String;)[[Ljava/lang/String;"));h.add(new FieldInsnNode(PUTSTATIC,AS,k,"[[Ljava/lang/String;"));}
     h.add(new FieldInsnNode(GETSTATIC,AS,"KEYS","[[Ljava/lang/String;"));h.add(new FieldInsnNode(GETSTATIC,AS,"HINTS","[[Ljava/lang/String;"));h.add(call(HELP,"hints","([[Ljava/lang/String;[[Ljava/lang/String;)[[Ljava/lang/String;"));h.add(new FieldInsnNode(PUTSTATIC,AS,"HINTS","[[Ljava/lang/String;"));m.instructions.insertBefore(n,h);}count("melee tab indoor controls and plain-language help");}
    if(m.name.equals("init")){int tabs=0,layout=0;boolean tabLayout=false;for(AbstractInsnNode n:m.instructions.toArray()){
     if(n instanceof MethodInsnNode mc&&mc.owner.equals("dev/zomboid/hordes/SpawnPoolUi")&&mc.name.equals("tabs")){m.instructions.insert(n,call(CP,"tabs","([Ljava/lang/String;)[Ljava/lang/String;"));tabs++;tabLayout=true;}
     if(n instanceof FieldInsnNode f&&f.owner.equals(AS)&&f.name.equals("KEYS"))tabLayout=false;
     if(tabLayout&&n.getOpcode()==ICONST_4){m.instructions.set(n,new InsnNode(ICONST_5));layout++;}
    }need(tabs,1,"extra tab");need(layout,5,"five columns in each tab row");count("ten tabs layout");}
    if(m.name.equals("init")||m.name.startsWith("lambda$init$")){
     int key=-1;if(m.name.equals("init")){for(AbstractInsnNode n:m.instructions.toArray())if(n.getOpcode()==AALOAD&&n.getNext() instanceof VarInsnNode v&&v.getOpcode()==ASTORE){key=v.var;break;}}
     else if(m.desc.startsWith("(Ljava/lang/String;"))key=1;
     if(key>=0)for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof LdcInsnNode l&&(l.cst.equals("Ejecutar al aplicar")||l.cst.equals("Sin acci\u00f3n"))){InsnList h=new InsnList();h.add(new VarInsnNode(ALOAD,key));h.add(call(CP,"switchText","(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"));m.instructions.insert(n,h);}
    }
    if(m.name.equals("receive")){LabelNode old=new LabelNode();InsnList h=new InsnList();h.add(new VarInsnNode(ALOAD,0));h.add(call(CS,"accept","(Ljava/lang/String;)Z"));h.add(new JumpInsnNode(IFEQ,old));h.add(new InsnNode(RETURN));h.add(old);h.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(h);count("receive combat snapshots without opening admin panel");}
    for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode mc&&mc.owner.equals("dev/zomboid/hordes/SpawnPoolUi")&&(mc.name.equals("preset")||mc.name.equals("scroll")))mc.owner=CP;
   }
   if(c.name.equals("dev/zomboid/hordes/client/ScrollSpeed")&&m.name.equals("step")){InsnList h=new InsnList();h.add(new VarInsnNode(ALOAD,1));h.add(call(CP,"wheel","(Ljava/lang/String;)Ljava/lang/String;"));h.add(new VarInsnNode(ASTORE,1));m.instructions.insert(h);count("small cooldown wheel increments");}
  }else{
   if(c.name.equals("dev/zomboidmelee/mixin/MeleeTimingMixin")&&Set.of("zpush$cooldown","zpush$delay").contains(m.name)){int hits=0;for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode mc&&mc.owner.equals("dev/zomboidmelee/combat/CombatRules")&&Set.of("cooldown","delay").contains(mc.name)){m.instructions.insertBefore(n,new VarInsnNode(ALOAD,mc.name.equals("cooldown")?1:2));mc.owner=M;mc.name=mc.name.equals("cooldown")?"wrap":"delay";mc.desc="(IZLjava/lang/Object;)I";hits++;}need(hits,1,"melee timing");count(m.name);}
   if(c.name.equals("dev/zomboidmelee/mixin/VanillaCombatMixin")&&m.name.equals("zpush$faster")){int hits=0;for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode mc&&mc.owner.equals("java/lang/Float")&&mc.name.equals("valueOf")){InsnList h=new InsnList();h.add(new VarInsnNode(ALOAD,0));h.add(call(M,"vanilla","(FLjava/lang/Object;)F"));m.instructions.insertBefore(n,h);hits++;}need(hits,1,"vanilla delay");count("vanilla cooldowns");}
   if(c.name.equals("dev/zomboidmelee/combat/PushClient")&&m.name.equals("request")){int hits=0;for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof IntInsnNode i&&i.operand==15){m.instructions.set(n,call(M,"shove","()I"));hits++;}need(hits,1,"client shove");count("synchronized shove cooldown client");}
   if(c.name.equals("dev/zomboidmelee/combat/ShoveCombat")){int hits=0;boolean next=false;for(AbstractInsnNode n:m.instructions.toArray()){
    if(n instanceof LdcInsnNode l&&l.cst.equals("zpush_next"))next=true;
    if(next&&n instanceof LdcInsnNode l&&Long.valueOf(15).equals(l.cst)){InsnList h=new InsnList();h.add(call(M,"shove","()I"));h.add(new InsnNode(I2L));m.instructions.insertBefore(n,h);m.instructions.remove(n);hits++;next=false;}
   }if(hits>0){need(hits,1,"server shove");count("synchronized shove cooldown server");}}
  }
 }
 if(before==changes)return b;ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);ClassNode v=new ClassNode();new ClassReader(w.toByteArray()).accept(v,0);verify(v);return w.toByteArray();
 }
 public static void main(String[] args)throws Exception{Map<String,byte[]> out=new TreeMap<>();try(ZipFile z=new ZipFile(args[1])){for(ZipEntry e:Collections.list(z.entries())){byte[] b=z.getInputStream(e).readAllBytes();out.put(e.getName(),e.getName().endsWith(".class")?patch(b,args[0]):b);}}
 String prefix=args[0].equals("hordes")?"dev/zomboid/hordes/":"dev/zomboidmelee/";try(var files=Files.walk(Path.of(args[3]))){for(Path p:files.filter(p->p.toString().endsWith(".class")).toList()){String name=Path.of(args[3]).relativize(p).toString();if(name.startsWith(prefix)){byte[] b=Files.readAllBytes(p);ClassNode c=new ClassNode();new ClassReader(b).accept(c,0);verify(c);out.put(name,b);}}}
 try(ZipOutputStream z=new ZipOutputStream(Files.newOutputStream(Path.of(args[2])))){for(var e:out.entrySet()){ZipEntry n=new ZipEntry(e.getKey());n.setTime(0);z.putNextEntry(n);z.write(e.getValue());z.closeEntry();}}System.out.println("Completed "+changes+" patches.");}
}
