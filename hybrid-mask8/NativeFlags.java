import java.util.*;import java.util.function.*;import java.util.zip.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
public class NativeFlags implements Opcodes {
 static final String B="com/raiiiden/warborn/client/shader/ShaderRegistry",R="NativeShaderFlags8",E="NativeShaderEntry8";
 static class Loader extends ClassLoader{Class<?>load(String n,byte[]b){return defineClass(n,b,0,b.length);}}
 static String remap(String s){return s.equals(B)?R:s.equals(B+"$ShaderEntry")?E:s;}
 static void init(ClassWriter w){MethodVisitor m=w.visitMethod(ACC_PUBLIC,"<init>","()V",null,null);m.visitCode();m.visitVarInsn(ALOAD,0);m.visitMethodInsn(INVOKESPECIAL,"java/lang/Object","<init>","()V",false);m.visitInsn(RETURN);m.visitMaxs(1,1);m.visitEnd();}
 public static void main(String[]args)throws Exception{
  try(ZipFile z=new ZipFile(args[0])){
   ClassNode real=new ClassNode();new ClassReader(z.getInputStream(z.getEntry(B+".class")).readAllBytes()).accept(real,0);
   ClassWriter ew=new ClassWriter(0);ew.visit(V21,ACC_PUBLIC,E,null,"java/lang/Object",null);ew.visitField(ACC_PUBLIC,"activationCondition","Ljava/util/function/Predicate;",null,null).visitEnd();ew.visitField(ACC_PUBLIC,"forceEnabled","Ljava/lang/Boolean;",null,null).visitEnd();init(ew);ew.visitEnd();
   ClassWriter rw=new ClassWriter(0);rw.visit(V21,ACC_PUBLIC,R,null,"java/lang/Object",null);rw.visitField(ACC_PUBLIC,"shaders","Ljava/util/Map;",null,null).visitEnd();init(rw);
   for(MethodNode m:real.methods)if(m.name.equals("isShaderActive")||m.name.equals("isShaderForceEnabled")){
    for(AbstractInsnNode i:m.instructions){if(i instanceof FieldInsnNode f)f.owner=remap(f.owner);if(i instanceof TypeInsnNode t)t.desc=remap(t.desc);if(i instanceof FrameNode f){if(f.local!=null)f.local.replaceAll(v->v instanceof String s?remap(s):v);if(f.stack!=null)f.stack.replaceAll(v->v instanceof String s?remap(s):v);}}
    m.accept(rw);
   }rw.visitEnd();Loader loader=new Loader();Class<?>ec=loader.load(E,ew.toByteArray()),rc=loader.load(R,rw.toByteArray());Object entry=ec.getConstructor().newInstance(),registry=rc.getConstructor().newInstance();ec.getField("activationCondition").set(entry,(Predicate<Object>)mc->false);ec.getField("forceEnabled").set(entry,Boolean.TRUE);rc.getField("shaders").set(registry,Map.of("htvg",entry));
   if(!Boolean.FALSE.equals(rc.getMethod("isShaderActive",String.class).invoke(registry,"htvg")))throw new AssertionError("native predicate is false");
   if(!Boolean.TRUE.equals(rc.getMethod("isShaderForceEnabled",String.class).invoke(registry,"htvg")))throw new AssertionError("native manual flag is true");
   ec.getField("forceEnabled").set(entry,Boolean.FALSE);if(!Boolean.FALSE.equals(rc.getMethod("isShaderForceEnabled",String.class).invoke(registry,"htvg")))throw new AssertionError("native flag off");
   ec.getField("activationCondition").set(entry,(Predicate<Object>)mc->true);if(!Boolean.TRUE.equals(rc.getMethod("isShaderActive",String.class).invoke(registry,"htvg")))throw new AssertionError("predicate independent of false manual flag");
  }System.out.println("REAL_NATIVE_SHADER_QUERY_SEMANTICS_CHECKS=4");
 }
}
