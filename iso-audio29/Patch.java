import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.tree.analysis.*;import java.nio.file.*;import java.util.*;import java.util.zip.*;
public class Patch implements Opcodes{
 static final String H="dev/zomboid/isotacz/",M="com/cleannrooster/dungeons_iso/mixin/";static int hooks;
 static void clear(MethodNode m){m.instructions.clear();m.tryCatchBlocks.clear();m.localVariables=null;}
 static MethodInsnNode call(String owner,String name,String desc){return new MethodInsnNode(INVOKESTATIC,owner,name,desc,false);}
 static byte[] patch(byte[]b)throws Exception{ClassNode c=new ClassNode();new ClassReader(b).accept(c,0);int before=hooks;
 for(MethodNode m:c.methods){
  if(c.name.equals("com/cleannrooster/dungeons_iso/util/SoundReference")&&m.name.equals("point")){clear(m);m.instructions.add(new VarInsnNode(ALOAD,0));m.instructions.add(call(H+"PlayerAudio29","point","(Ljava/lang/Object;)Ljava/lang/Object;"));m.instructions.add(new TypeInsnNode(CHECKCAST,"net/minecraft/world/phys/Vec3"));m.instructions.add(new InsnNode(ARETURN));hooks++;}
  if(c.name.equals(M+"SoundSystemMixin")&&m.name.equals("repositionListenerToPlayer")){clear(m);m.instructions.add(new VarInsnNode(ALOAD,1));for(String[] f:new String[][]{{"loaded","Z"},{"listener","Lcom/mojang/blaze3d/audio/Listener;"},{"executor","Lnet/minecraft/client/sounds/SoundEngineExecutor;"}}){m.instructions.add(new VarInsnNode(ALOAD,0));m.instructions.add(new FieldInsnNode(GETFIELD,c.name,f[0],f[1]));}m.instructions.add(new VarInsnNode(ALOAD,2));m.instructions.add(call(H+"PlayerAudio29","update","(Ljava/lang/Object;ZLjava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V"));m.instructions.add(new InsnNode(RETURN));hooks++;}
  if(c.name.equals(H+"Bridge")&&m.name.equals("mouse")){
   InsnList x=new InsnList();x.add(new VarInsnNode(LLOAD,0));for(int i=2;i<=4;i++)x.add(new VarInsnNode(ILOAD,i));x.add(new VarInsnNode(ALOAD,5));x.add(call(H+"AimInteraction29","mouse",m.desc.substring(0,m.desc.length()-1)+"Z"));LabelNode pass=new LabelNode();x.add(new JumpInsnNode(IFEQ,pass));x.add(new InsnNode(RETURN));x.add(pass);x.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(x);hooks++;
  }
 }
 if(before==hooks)return b;ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);verify(w.toByteArray());return w.toByteArray();}
 static void verify(byte[]b)throws Exception{ClassNode c=new ClassNode();new ClassReader(b).accept(c,0);for(MethodNode m:c.methods)if((m.access&(ACC_ABSTRACT|ACC_NATIVE))==0)new Analyzer<BasicValue>(new BasicVerifier()).analyze(c.name,m);}
 static byte[] mixin(){ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);String cls=M+"AimInteraction29Mixin";w.visit(V21,ACC_PUBLIC,cls,null,"java/lang/Object",null);
 AnnotationVisitor mix=w.visitAnnotation("Lorg/spongepowered/asm/mixin/Mixin;",false);AnnotationVisitor targets=mix.visitArray("targets");targets.visit(null,"net.minecraft.client.multiplayer.MultiPlayerGameMode");targets.visitEnd();mix.visitEnd();
 MethodVisitor m=w.visitMethod(ACC_PRIVATE,"iso29$guard","(Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;)V",null,null);
 AnnotationVisitor in=m.visitAnnotation("Lorg/spongepowered/asm/mixin/injection/Inject;",true),methods=in.visitArray("method");
 for(String method:new String[]{"useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;","interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;","interactAt(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/EntityHitResult;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"})methods.visit(null,method);
 methods.visitEnd();AnnotationVisitor ats=in.visitArray("at"),at=ats.visitAnnotation(null,"Lorg/spongepowered/asm/mixin/injection/At;");at.visit("value","HEAD");at.visitEnd();ats.visitEnd();in.visit("cancellable",true);in.visitEnd();m.visitCode();m.visitVarInsn(ALOAD,1);m.visitMethodInsn(INVOKESTATIC,H+"AimInteraction29","guard","(Ljava/lang/Object;)V",false);m.visitInsn(RETURN);m.visitMaxs(0,0);m.visitEnd();w.visitEnd();return w.toByteArray();}
 public static void main(String[]a)throws Exception{Map<String,byte[]>e=new TreeMap<>();try(ZipFile z=new ZipFile(a[0])){for(ZipEntry n:Collections.list(z.entries())){byte[]b=z.getInputStream(n).readAllBytes();e.put(n.getName(),n.getName().endsWith(".class")?patch(b):b);}}
 if(hooks!=3)throw new IllegalStateException("hook count "+hooks);byte[]m=mixin();verify(m);e.put(M+"AimInteraction29Mixin.class",m);
 try(var files=Files.walk(Path.of(a[2]))){for(Path p:files.filter(p->p.toString().endsWith(".class")).toList())e.put(Path.of(a[2]).relativize(p).toString(),Files.readAllBytes(p));}
 try(ZipOutputStream z=new ZipOutputStream(Files.newOutputStream(Path.of(a[1])))){for(var n:e.entrySet()){ZipEntry entry=new ZipEntry(n.getKey());entry.setTime(0);z.putNextEntry(entry);z.write(n.getValue());z.closeEntry();}}System.out.println("3 native hooks + 3 interaction method selectors verified");
 }
}
