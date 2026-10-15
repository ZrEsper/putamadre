import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.tree.analysis.*;import java.util.*;import java.util.zip.*;import java.nio.file.*;
public class Patch implements Opcodes{
 static int hooks;static final String V="dev/zomboid/tacz/Visual17",L="dev/tactical/tacz/ScopeLimits";
 static void clear(MethodNode m){m.instructions.clear();m.tryCatchBlocks.clear();m.localVariables=null;}
 static MethodInsnNode call(String o,String n,String d){return new MethodInsnNode(INVOKESTATIC,o,n,d,false);}
 static byte[] patch(byte[] bytes,String kind)throws Exception{ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);int before=hooks;
 for(MethodNode m:c.methods){
  if(kind.equals("tacz")&&c.name.equals("com/tacz/guns/client/renderer/entity/EntityBulletRenderer")&&m.name.equals("lambda$render$0")){
   MethodInsnNode push=null,rotation=null;for(AbstractInsnNode a:m.instructions)if(a instanceof MethodInsnNode mc&&mc.owner.equals("com/mojang/blaze3d/vertex/PoseStack")){if(mc.name.equals("pushPose")){if(push!=null)throw new IllegalStateException("multiple push");push=mc;}if(mc.name.equals("mulPose")&&rotation==null)rotation=mc;}
   if(push==null||rotation==null)throw new IllegalStateException("ammo rotation pattern");AbstractInsnNode load=push.getPrevious();if(!(load instanceof VarInsnNode v))throw new IllegalStateException("pose local");int local=v.var;m.instructions.remove(load);m.instructions.remove(push);
   // First rotation expression starts with the PoseStack load, then Axis.YP.
   AbstractInsnNode first=rotation;while(first.getPrevious()!=null){first=first.getPrevious();if(first instanceof VarInsnNode receiver&&receiver.getOpcode()==ALOAD&&receiver.var==local)break;}
   if(!(first instanceof VarInsnNode))throw new IllegalStateException("rotation receiver");InsnList x=new InsnList();x.add(new VarInsnNode(ALOAD,local));x.add(new MethodInsnNode(INVOKEVIRTUAL,"com/mojang/blaze3d/vertex/PoseStack","pushPose","()V",false));m.instructions.insertBefore(first,x);hooks++;
  }
  if(kind.equals("tacz")&&c.name.equals("com/tacz/guns/client/particle/BulletHoleParticle")&&m.name.equals("<init>"))for(AbstractInsnNode a:m.instructions.toArray())if(a.getOpcode()==RETURN){InsnList x=new InsnList();x.add(new VarInsnNode(ALOAD,0));x.add(new VarInsnNode(ALOAD,10));x.add(new VarInsnNode(ALOAD,11));x.add(call(V,"apply","(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V"));m.instructions.insertBefore(a,x);hooks++;}
  if(kind.equals("tacz")&&c.name.equals("com/tacz/guns/entity/EntityKineticBullet")&&m.name.equals("readSpawnData"))for(AbstractInsnNode a:m.instructions.toArray())if(a.getOpcode()==RETURN){InsnList x=new InsnList();for(String[] pair:new String[][]{{"xRotO","getXRot"},{"yRotO","getYRot"}}){x.add(new VarInsnNode(ALOAD,0));x.add(new VarInsnNode(ALOAD,0));x.add(new MethodInsnNode(INVOKEVIRTUAL,c.name,pair[1],"()F",false));x.add(new FieldInsnNode(PUTFIELD,c.name,pair[0],"F"));}m.instructions.insertBefore(a,x);hooks++;}
  if(kind.equals("tactical")&&c.name.equals("dev/tactical/tacz/ScopeZoom")){
   if(m.name.equals("scale")){clear(m);m.instructions.add(new VarInsnNode(ALOAD,0));m.instructions.add(new VarInsnNode(ALOAD,1));m.instructions.add(call(L,"scale",m.desc));m.instructions.add(new InsnNode(ARETURN));hooks++;}
   if(m.name.equals("scroll")){clear(m);m.instructions.add(new VarInsnNode(ALOAD,0));m.instructions.add(call(L,"scroll","(Ljava/lang/Object;)V"));m.instructions.add(new InsnNode(RETURN));hooks++;}
  }
  if(kind.equals("tactical")&&c.name.equals("dev/tactical/tacz/ShotTrails")&&m.name.equals("render"))for(AbstractInsnNode a:m.instructions.toArray())if(a instanceof LdcInsnNode l&&l.cst.equals("getModelViewMatrix")){l.cst="getPoseStack";AbstractInsnNode invocation=a;while(invocation!=null&&!(invocation instanceof MethodInsnNode mc&&mc.owner.equals("dev/tactical/tacz/Reflect")&&mc.name.equals("call")))invocation=invocation.getNext();if(invocation==null)throw new IllegalStateException("stage matrix call");m.instructions.insert(invocation,call("dev/tactical/tacz/TracePose","matrix","(Ljava/lang/Object;)Ljava/lang/Object;"));hooks++;}
  if(kind.equals("tactical")&&c.name.equals("dev/tactical/tacz/ImpactMarks")){
   if(m.name.equals("configure")){clear(m);m.instructions.add(new VarInsnNode(ALOAD,0));m.instructions.add(call(V,"configure","(Ljava/lang/Object;)V"));m.instructions.add(new InsnNode(RETURN));hooks++;}
   if(m.name.equals("size")){clear(m);m.instructions.add(new VarInsnNode(ALOAD,0));m.instructions.add(call(V,"size","(Ljava/lang/Object;)F"));m.instructions.add(new InsnNode(FRETURN));hooks++;}
  }
 }
 if(before==hooks)return bytes;ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);ClassNode checked=new ClassNode();new ClassReader(w.toByteArray()).accept(checked,0);for(MethodNode m:checked.methods)if((m.access&(ACC_ABSTRACT|ACC_NATIVE))==0)new Analyzer<BasicValue>(new BasicVerifier()).analyze(checked.name,m);return w.toByteArray();}
 public static void main(String[]a)throws Exception{Map<String,byte[]>out=new TreeMap<>();try(ZipFile z=new ZipFile(a[0])){for(ZipEntry e:Collections.list(z.entries())){byte[]b=z.getInputStream(e).readAllBytes();out.put(e.getName(),e.getName().endsWith(".class")?patch(b,a[3]):b);}}
 try(var files=Files.walk(Path.of(a[2]))){for(Path p:files.filter(p->p.toString().endsWith(".class")).toList())out.put(Path.of(a[2]).relativize(p).toString(),Files.readAllBytes(p));}
 int expected=a[3].equals("tacz")?3:5;if(hooks!=expected)throw new IllegalStateException("hook count "+hooks+" expected "+expected);
 try(ZipOutputStream z=new ZipOutputStream(Files.newOutputStream(Path.of(a[1])))){for(var e:out.entrySet()){ZipEntry n=new ZipEntry(e.getKey());n.setTime(0);z.putNextEntry(n);z.write(e.getValue());z.closeEntry();}}System.out.println(a[3]+" hooks verified: "+hooks);}
}
