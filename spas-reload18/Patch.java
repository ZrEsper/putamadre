import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
import java.nio.file.*;
public class Patch implements Opcodes {
 public static void main(String[] args) throws Exception {
  ClassNode c=new ClassNode(); new ClassReader(Files.readAllBytes(Path.of(args[0]))).accept(c,0);
  int hooks=0;
  for(MethodNode m:c.methods) if(m.name.equals("lambda$tickReload$12"))
   for(AbstractInsnNode n:m.instructions.toArray()) if(n instanceof MethodInsnNode call && call.name.equals("invoke") && call.desc.equals("(Lorg/luaj/vm2/Varargs;)Lorg/luaj/vm2/Varargs;")) {
    if(!call.owner.startsWith("org/luaj/vm2/"))throw new IllegalStateException(call.owner);
    m.instructions.set(call,new MethodInsnNode(INVOKESTATIC,"dev/zomboid/tacz/Reload18","invoke","(Lorg/luaj/vm2/LuaValue;Lorg/luaj/vm2/Varargs;)Lorg/luaj/vm2/Varargs;",false)); hooks++;
   }
  if(hooks!=1)throw new IllegalStateException("Expected one reload invocation, got "+hooks);
  ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);
  ClassNode checked=new ClassNode();new ClassReader(w.toByteArray()).accept(checked,0);
  for(MethodNode m:checked.methods) if((m.access&(ACC_ABSTRACT|ACC_NATIVE))==0)new Analyzer<BasicValue>(new BasicVerifier()).analyze(c.name,m);
  Files.write(Path.of(args[1]),w.toByteArray());System.out.println("Reload hook: 1; all original methods passed ASM BasicVerifier");
 }
}
