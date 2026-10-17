import java.util.*;import java.util.zip.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import checks.*;import net.minecraft.client.gui.GuiGraphics;
public class RenderRegression implements Opcodes{
 static int n;static void ok(boolean b,String s){n++;if(!b)throw new AssertionError(s);}static class Loader extends ClassLoader{Class<?>define(String n,byte[]b){return defineClass(n,b,0,b.length);}}
 static void check(ZipFile z,String path,boolean fixed,int serial)throws Exception{
  ClassNode c=new ClassNode();new ClassReader(z.getInputStream(z.getEntry(path)).readAllBytes()).accept(c,0);MethodNode nativeRender=c.methods.stream().filter(m->m.name.equals("render")&&m.desc.endsWith("IIF)V")).findFirst().orElseThrow();String name="PanelRender"+serial;
  ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);w.visit(V21,ACC_PUBLIC,name,null,"checks/BaseScreen",null);MethodVisitor ctor=w.visitMethod(ACC_PUBLIC,"<init>","()V",null,null);ctor.visitCode();ctor.visitVarInsn(ALOAD,0);ctor.visitMethodInsn(INVOKESPECIAL,"checks/BaseScreen","<init>","()V",false);ctor.visitInsn(RETURN);ctor.visitMaxs(1,1);ctor.visitEnd();
  // Execute the real simple/preview branch, retaining the exact ordering of
  // custom content and super.render. Substitute only the drawing endpoint.
  MethodNode extracted=new MethodNode(ACC_PUBLIC,"render",nativeRender.desc,null,null);boolean branch=false,complete=false;for(AbstractInsnNode i:nativeRender.instructions){if(!branch){if(i.getOpcode()==IFEQ)branch=true;continue;}if(i.getOpcode()<0)continue;AbstractInsnNode j=i.clone(new HashMap<>());if(j instanceof MethodInsnNode m){if(m.owner.equals("dev/zomboid/hordes/SimplePanel")){m.owner="checks/Trace";m.name="labels";}else if(m.getOpcode()==INVOKESPECIAL&&m.name.equals("render"))m.owner="checks/BaseScreen";}extracted.instructions.add(j);if(i.getOpcode()==RETURN){complete=true;break;}}
  ok(complete,"native simple branch isolated "+path);extracted.accept(w);
  for(MethodNode m:c.methods)if(m.name.equals("renderBackground")||m.name.equals("renderBlurredBackground")){ok(m.instructions.size()==1&&m.instructions.getFirst().getOpcode()==RETURN,"override contains no blur or draw side effects");m.accept(w);}
  w.visitEnd();Object screen=new Loader().define(name,w.toByteArray()).getConstructor().newInstance();Trace.reset();((BaseScreen)screen).render(new GuiGraphics(),0,0,0);ok(Trace.labelsDrawn&&Trace.widgets==1,"custom labels and widgets both render");ok(Trace.labelsBlurred!=fixed,"native branch old blur reproduced / fixed "+path);
 }
 public static void main(String[]a)throws Exception{try(ZipFile before=new ZipFile(a[0]);ZipFile after=new ZipFile(a[1])){int serial=0;for(String path:new String[]{"dev/zomboid/hordes/client/AdminScreen.class","dev/zomboid/hordes/client/LootPreviewScreen.class"}){check(before,path,false,serial++);check(after,path,true,serial++);}}System.out.println("NATIVE_ADMIN_PREVIEW_RENDER_BLUR_REGRESSION_CHECKS="+n);}
}
