import java.nio.file.*;import java.util.*;import jdk.internal.org.objectweb.asm.*;import jdk.internal.org.objectweb.asm.tree.*;
public class Patch{
 static final String H="dev/survivorcreator/hudscale/ScreenScale",HUD="dev/survivorcreator/hudscale/HudScale";
 public static void main(String[]a)throws Exception{try(var fs=Files.walk(Path.of(a[0]))){for(Path p:fs.filter(x->x.toString().endsWith("Screen.class")).toList()){
 ClassNode c=new ClassNode();new ClassReader(Files.readAllBytes(p)).accept(c,0);if(!c.superName.equals("net/minecraft/client/gui/screens/Screen"))continue;
 for(MethodNode m:new ArrayList<>(c.methods)){
 for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode v&&v.owner.equals("net/minecraft/client/gui/GuiGraphics")&&v.name.equals("enableScissor")&&v.desc.equals("(IIII)V")){v.setOpcode(Opcodes.INVOKESTATIC);v.owner=H;v.name="scissor";v.desc="(Ljava/lang/Object;IIII)V";v.itf=false;}
 if(m.name.equals("render")&&m.desc.equals("(Lnet/minecraft/client/gui/GuiGraphics;IIF)V")){
 String old="scale32Render";m.name=old;MethodNode w=new MethodNode(Opcodes.ACC_PUBLIC,"render",m.desc,null,null);InsnList i=w.instructions;
 i.add(new VarInsnNode(Opcodes.ALOAD,1));i.add(new MethodInsnNode(Opcodes.INVOKESTATIC,HUD,"begin","(Ljava/lang/Object;)Ljava/lang/Object;",false));i.add(new VarInsnNode(Opcodes.ASTORE,5));LabelNode start=new LabelNode(),end=new LabelNode(),handler=new LabelNode();i.add(start);
 i.add(new VarInsnNode(Opcodes.ALOAD,0));i.add(new VarInsnNode(Opcodes.ALOAD,1));for(int v=2;v<=3;v++){i.add(new VarInsnNode(Opcodes.ILOAD,v));i.add(new InsnNode(Opcodes.I2D));i.add(new MethodInsnNode(Opcodes.INVOKESTATIC,H,"input","(D)D",false));i.add(new InsnNode(Opcodes.D2I));}i.add(new VarInsnNode(Opcodes.FLOAD,4));i.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,c.name,old,m.desc,false));i.add(end);i.add(new VarInsnNode(Opcodes.ALOAD,5));i.add(new MethodInsnNode(Opcodes.INVOKESTATIC,HUD,"end","(Ljava/lang/Object;)V",false));i.add(new InsnNode(Opcodes.RETURN));i.add(handler);i.add(new VarInsnNode(Opcodes.ASTORE,6));i.add(new VarInsnNode(Opcodes.ALOAD,5));i.add(new MethodInsnNode(Opcodes.INVOKESTATIC,HUD,"end","(Ljava/lang/Object;)V",false));i.add(new VarInsnNode(Opcodes.ALOAD,6));i.add(new InsnNode(Opcodes.ATHROW));w.tryCatchBlocks.add(new TryCatchBlockNode(start,end,handler,null));c.methods.add(w);
 }
 if(m.name.startsWith("mouse")&&m.desc.startsWith("(DD")){InsnList i=new InsnList();for(int v:new int[]{1,3}){i.add(new VarInsnNode(Opcodes.DLOAD,v));i.add(new MethodInsnNode(Opcodes.INVOKESTATIC,H,"input","(D)D",false));i.add(new VarInsnNode(Opcodes.DSTORE,v));}m.instructions.insert(i);}
 }
 ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS){protected String getCommonSuperClass(String x,String y){return x.equals(y)?x:"java/lang/Object";}};c.accept(w);Files.write(p,w.toByteArray());System.out.println(c.name);
 }}}
}
