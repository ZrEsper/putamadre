import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public final class Patch implements Opcodes {
 static int changes;
 static void changed(String what){changes++;System.out.println("Patched "+what);}
 static void body(MethodNode m,InsnList code){m.instructions=code;m.tryCatchBlocks.clear();if(m.localVariables!=null)m.localVariables.clear();m.visibleLocalVariableAnnotations=null;m.invisibleLocalVariableAnnotations=null;changed(m.name);}
 static InsnList delegate(String owner,String method,String desc,boolean object){InsnList h=new InsnList();if(object)h.add(new VarInsnNode(ALOAD,0));h.add(new MethodInsnNode(INVOKESTATIC,owner,method,desc,false));h.add(new InsnNode(RETURN));return h;}
 static void require(int actual,int expected,String label){if(actual!=expected)throw new IllegalStateException(label+": "+actual+" expected "+expected);}
 static void verify(ClassNode c)throws Exception {for(MethodNode m:c.methods)if((m.access&(ACC_ABSTRACT|ACC_NATIVE))==0)new Analyzer<BasicValue>(new BasicVerifier()).analyze(c.name,m);}
 static byte[] patch(String kind,String name,byte[] bytes)throws Exception {
  ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);int before=changes;
  if(kind.equals("iso")) {
   if(c.name.equals("dev/zomboid/isotacz/Bridge")) for(MethodNode m:c.methods) {
    if(m.name.equals("aim"))body(m,delegate("dev/zomboid/isotacz/FreeAim","aim","()V",false));
    // Let the patched FOV/orthographic paths own zoom; do not cancel all external-view FOV.
    if(m.name.equals("cancelScope")){InsnList h=new InsnList();h.add(new InsnNode(RETURN));body(m,h);}
   }
   if(c.name.equals("com/cleannrooster/dungeons_iso/config/Config")) for(MethodNode m:c.methods)
    if(Set.of("isContextualTargeting","isMovementTargeting","isContextualInteract").contains(m.name)&&m.desc.equals("()Z")) {InsnList h=new InsnList();h.add(new InsnNode(ICONST_0));h.add(new InsnNode(IRETURN));body(m,h);}
   if(c.name.equals("com/cleannrooster/dungeons_iso/mixin/MinecraftClientMixin")) {
    int disabled=0;
    for(MethodNode m:c.methods)for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof FieldInsnNode f&&f.getOpcode()==GETFIELD&&f.name.equals("additionalMeleeAssistance")) {InsnList h=new InsnList();h.add(new InsnNode(POP));h.add(new InsnNode(ICONST_0));m.instructions.insertBefore(n,h);m.instructions.remove(n);disabled++;}
    require(disabled,1,"Melee assist read");changed("additional melee assist disabled");
   }
   if(c.name.equals("com/cleannrooster/dungeons_iso/api/Ortho")) {
    int hooks=0;for(MethodNode m:c.methods)for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode call&&call.owner.equals("com/cleannrooster/dungeons_iso/mod/Mod")&&call.name.equals("getZoom")) {m.instructions.insert(n,new MethodInsnNode(INVOKESTATIC,"dev/zomboid/isotacz/ScopeView","scale","(F)F",false));hooks++;}
    require(hooks,4,"Orthographic zoom calls");changed("scope-aware orthographic projection");
   }
  }
  if(kind.equals("tps")) {
   if(c.name.equals("mod/chloeprime/thirdpersonshooting/client/eventhandler/ClientGunFireEvent"))for(MethodNode m:c.methods)if(m.name.equals("onClientFire")) {InsnList h=new InsnList();h.add(new InsnNode(RETURN));body(m,h);}
   if(c.name.equals("mod/chloeprime/thirdpersonshooting/client/TpsSsPlugin"))for(MethodNode m:c.methods)if(m.name.equals("setupCameraRotation")) {
    LabelNode old=new LabelNode();InsnList h=new InsnList();h.add(new MethodInsnNode(INVOKESTATIC,"mod/chloeprime/thirdpersonshooting/client/FreeCamera","isIso","()Z",false));h.add(new JumpInsnNode(IFEQ,old));
    for(String field:List.of("deltaXRot","deltaYRot")){h.add(new VarInsnNode(ALOAD,0));h.add(new InsnNode(DCONST_0));h.add(new FieldInsnNode(PUTFIELD,c.name,field,"D"));}
    h.add(new InsnNode(RETURN));h.add(old);h.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(h);changed("isometric camera recoil isolation");
   }
   if(c.name.equals("mod/chloeprime/thirdpersonshooting/mixin/client/bugfix/FixIncompatibleClassChangeInCameraSetupEvent")) {
    int hooks=0;for(MethodNode m:c.methods)for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode call&&call.name.equals("isShoulderSurfing")&&call.desc.equals("()Z")) {m.instructions.set(n,new MethodInsnNode(INVOKESTATIC,"mod/chloeprime/thirdpersonshooting/client/FreeCamera","shoulder","(Ljava/lang/Object;)Z",false));hooks++;}
    require(hooks,2,"Recoil shoulder checks");changed("native recoil retained without rotating iso camera");
   }
   if(c.name.equals("mod/chloeprime/thirdpersonshooting/mixin/client/MixinTacAimingHandler$MixinFovEvent"))for(MethodNode m:c.methods)if(m.name.equals("redirectFov")) {
    AbstractInsnNode start=null;for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof VarInsnNode v&&v.getOpcode()==ALOAD&&v.var==2){start=n;break;}
    if(start==null)throw new IllegalStateException("No original FOV operation");
    while(m.instructions.getFirst()!=start)m.instructions.remove(m.instructions.getFirst());
    m.tryCatchBlocks.clear();if(m.localVariables!=null)m.localVariables.clear();changed("scope attachment zoom replaces constant shoulder zoom");
   }
   if(c.name.equals("mod/chloeprime/thirdpersonshooting/mixin/client/MixinTacAimingHandler"))for(MethodNode m:c.methods)if(m.name.equals("lockSpeedAtSsMode_103")) {InsnList h=new InsnList();h.add(new VarInsnNode(FLOAD,1));h.add(new InsnNode(FRETURN));body(m,h);}
  }
  if(kind.equals("tacz")) {
   if(c.name.equals("dev/zomboid/taczfeedback/ThirdPersonAimClient"))for(MethodNode m:c.methods) {
    if(m.name.equals("pick")) {int exact=0;for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode call&&call.name.equals("inflate")&&call.desc.equals("(D)Lnet/minecraft/world/phys/AABB;")&&n.getPrevious().getOpcode()==F2D&&n.getPrevious().getPrevious() instanceof MethodInsnNode radius&&radius.name.equals("getPickRadius")){m.instructions.set(n,new InsnNode(POP2));exact++;}require(exact,1,"Entity pick-radius inflation");changed("exact reticle ray without inflated entity hitboxes");}
    if(m.name.equals("prepareShot")) {for(AbstractInsnNode n:m.instructions.toArray())if(n.getOpcode()==ARETURN)m.instructions.insertBefore(n,new MethodInsnNode(INVOKESTATIC,"dev/zomboid/tacz/ShotPose","capture","([F)[F",false));changed("shot pose captures actual recoil baseline");}
    if(m.name.equals("restore"))body(m,delegate("dev/zomboid/tacz/ShotPose","restore","([F)V",true));
   }
   if(c.name.equals("dev/zomboid/taczfeedback/FeedbackMath"))for(MethodNode m:c.methods)if(m.name.equals("glare")){InsnList h=new InsnList();h.add(new VarInsnNode(DLOAD,0));h.add(new VarInsnNode(DLOAD,2));for(int i=4;i<8;i++)h.add(new VarInsnNode(ILOAD,i));h.add(new MethodInsnNode(INVOKESTATIC,"dev/zomboid/taczfeedback/GlareRules","visible",m.desc,false));h.add(new InsnNode(IRETURN));body(m,h);}
   if(c.name.equals("dev/zomboid/taczfeedback/mixin/IsoGunCameraMixin"))for(MethodNode m:c.methods)if(m.name.equals("isoScope")) {InsnList h=new InsnList();h.add(new VarInsnNode(ALOAD,0));h.add(new MethodInsnNode(INVOKESTATIC,"dev/zomboid/tacz/ScopeZoom","applyIso","(Ljava/lang/Object;)V",false));m.instructions.insert(h);changed("isometric perspective scope zoom");}
   if(c.name.equals("dev/zomboid/taczfeedback/FeedbackClient"))for(MethodNode m:c.methods) {
    if(m.name.equals("tick"))for(AbstractInsnNode n:m.instructions.toArray())if(n.getOpcode()==ICONST_4&&n.getNext().getOpcode()==IREM){m.instructions.set(n,new InsnNode(ICONST_2));changed("faster observer glare refresh");}
    if(m.name.equals("glare")) {
     int distance=-1,alignment=-1,sizes=0,alphas=0;
     for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode call&&call.owner.equals("net/minecraft/client/renderer/RenderType")&&call.name.equals("entityTranslucent")) {call.name="eyes";changed("additive fullbright scope glare");}
     for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof LdcInsnNode ldc&&Double.valueOf(.65).equals(ldc.cst)) {
      AbstractInsnNode end=n;while(end!=null&&end.getOpcode()!=D2F){if(end instanceof VarInsnNode v&&v.getOpcode()==DLOAD)distance=v.var;end=end.getNext();}
      if(end==null||distance<0)throw new IllegalStateException("Glare size shape");InsnList h=new InsnList();h.add(new VarInsnNode(DLOAD,distance));h.add(new MethodInsnNode(INVOKESTATIC,"dev/zomboid/taczfeedback/GlareRules","size","(D)F",false));m.instructions.insertBefore(n,h);
      AbstractInsnNode after=end.getNext();while(n!=after){AbstractInsnNode next=n.getNext();m.instructions.remove(n);n=next;}sizes++;
     }
     for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof LdcInsnNode ldc&&Double.valueOf(210).equals(ldc.cst)) {
      AbstractInsnNode end=n;while(end!=null&&end.getOpcode()!=D2I){if(end instanceof VarInsnNode v&&v.getOpcode()==DLOAD)alignment=v.var;end=end.getNext();}
      if(end==null||alignment<0)throw new IllegalStateException("Glare alpha shape");InsnList h=new InsnList();h.add(new VarInsnNode(DLOAD,alignment));h.add(new VarInsnNode(DLOAD,distance));h.add(new MethodInsnNode(INVOKESTATIC,"dev/zomboid/taczfeedback/GlareRules","alpha","(DD)I",false));m.instructions.insertBefore(n,h);
      AbstractInsnNode after=end.getNext();while(n!=after){AbstractInsnNode next=n.getNext();m.instructions.remove(n);n=next;}alphas++;
     }
     require(sizes,1,"Glare size");require(alphas,1,"Glare alpha");changed("larger bright glare facing observer");
    }
   }
  }
  if(changes==before)return bytes;
  ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);ClassNode check=new ClassNode();new ClassReader(w.toByteArray()).accept(check,0);verify(check);return w.toByteArray();
 }
 public static void main(String[] args)throws Exception {
  String kind=args[0];Path input=Path.of(args[1]),output=Path.of(args[2]),classes=Path.of(args[3]);Map<String,byte[]> data=new LinkedHashMap<>();
  try(ZipFile zip=new ZipFile(input.toFile())){for(ZipEntry e:Collections.list(zip.entries())){byte[] bytes=zip.getInputStream(e).readAllBytes();data.put(e.getName(),e.getName().endsWith(".class")?patch(kind,e.getName(),bytes):bytes);}}
  Set<String> included=kind.equals("iso")?Set.of("dev/zomboid/isotacz/FreeAim.class","dev/zomboid/isotacz/ScopeView.class"):kind.equals("tps")?Set.of("mod/chloeprime/thirdpersonshooting/client/FreeCamera.class"):Set.of("dev/zomboid/tacz/ScopeZoom.class","dev/zomboid/tacz/ShotPose.class","dev/zomboid/taczfeedback/GlareRules.class");
  for(String name:new TreeSet<>(included)){byte[] bytes=Files.readAllBytes(classes.resolve(name));ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);verify(c);data.put(name,bytes);}
  Files.createDirectories(output.getParent());try(ZipOutputStream zip=new ZipOutputStream(Files.newOutputStream(output))){for(var e:data.entrySet()){ZipEntry entry=new ZipEntry(e.getKey());entry.setTime(0);zip.putNextEntry(entry);zip.write(e.getValue());zip.closeEntry();}}
  System.out.println("Completed "+kind+"; "+changes+" specific patches.");
 }
}
