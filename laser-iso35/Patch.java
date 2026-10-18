import java.nio.file.*;import java.util.*;import java.util.zip.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.tree.analysis.*;
public class Patch implements Opcodes{
 static final String ROOT="com/cleannrooster/dungeons_iso/mixin/",H="dev/zomboid/isotacz/ShotAim32";
 static byte[] hook(String name,String target,String method,boolean stat,boolean returns,boolean cancel)throws Exception{
 ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);w.visit(V21,ACC_PUBLIC|ACC_ABSTRACT,ROOT+name,null,"java/lang/Object",null);
 w.visitAnnotation("Lorg/spongepowered/asm/mixin/Pseudo;",false).visitEnd();var a=w.visitAnnotation("Lorg/spongepowered/asm/mixin/Mixin;",false);var ar=a.visitArray("targets");ar.visit(null,target);ar.visitEnd();a.visit("remap",false);a.visitEnd();
 String ci="org/spongepowered/asm/mixin/injection/callback/"+(returns?"CallbackInfoReturnable":"CallbackInfo");var m=w.visitMethod(ACC_PRIVATE|(stat?ACC_STATIC:0),"iso32$"+name.substring(name.lastIndexOf('/')+1),"(L"+ci+";)V",null,null);a=m.visitAnnotation("Lorg/spongepowered/asm/mixin/injection/Inject;",true);ar=a.visitArray("method");ar.visit(null,method);ar.visitEnd();ar=a.visitArray("at");var at=ar.visitAnnotation(null,"Lorg/spongepowered/asm/mixin/injection/At;");at.visit("value","HEAD");at.visitEnd();ar.visitEnd();a.visit("remap",false);a.visit("require",1);a.visit("cancellable",cancel);a.visitEnd();m.visitCode();if(cancel){m.visitVarInsn(ALOAD,stat?0:1);m.visitMethodInsn(INVOKESTATIC,H,"external","(Ljava/lang/Object;)V",false);}else m.visitMethodInsn(INVOKESTATIC,H,"before","()V",false);m.visitInsn(RETURN);m.visitMaxs(0,0);m.visitEnd();w.visitEnd();return w.toByteArray();}
 static void verify(byte[]b)throws Exception{ClassNode c=new ClassNode();new ClassReader(b).accept(c,0);for(var m:c.methods)if((m.access&(ACC_NATIVE|ACC_ABSTRACT))==0)new Analyzer<BasicValue>(new BasicVerifier()).analyze(c.name,m);}
 public static void main(String[]args)throws Exception{Map<String,byte[]>e=new TreeMap<>();try(var z=new ZipFile(args[0])){for(var n:Collections.list(z.entries()))e.put(n.getName(),z.getInputStream(n).readAllBytes());}
 if(args[3].equals("iso")){
 String path="dev/zomboid/isotacz/Bridge.class";ClassNode c=new ClassNode();new ClassReader(e.get(path)).accept(c,0);int count=0;
 for(var m:c.methods)if(m.name.equals("finishCamera")){for(var n:m.instructions.toArray())if(n.getOpcode()==RETURN)m.instructions.insertBefore(n,new MethodInsnNode(INVOKESTATIC,H,"before","()V",false));count++;}if(count!=1)throw new AssertionError("camera hook missing");ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);e.put(path,w.toByteArray());
 e.put(ROOT+"compat/tacz/CursorShoot32.class",hook("compat/tacz/CursorShoot32","com.tacz.guns.client.gameplay.LocalPlayerShoot","shoot()Lcom/tacz/guns/api/entity/ShootResult;",false,true,false));
 e.put(ROOT+"compat/tacz/CursorAuto32.class",hook("compat/tacz/CursorAuto32","com.tacz.guns.client.input.ShootKey","autoShoot(Lnet/neoforged/neoforge/client/event/ClientTickEvent$Post;)V",true,false,false));
 e.put(ROOT+"ShoulderFire32.class",hook("ShoulderFire32","mod.chloeprime.thirdpersonshooting.client.eventhandler.ClientGunFireEvent","onClientFire(Lcom/tacz/guns/api/event/common/GunShootEvent;)V",true,false,true));
 }
 try(var fs=Files.walk(Path.of(args[2]))){for(var p:fs.filter(p->p.toString().endsWith(".class")).toList())e.put(Path.of(args[2]).relativize(p).toString(),Files.readAllBytes(p));}
 for(var n:e.entrySet())if(n.getKey().endsWith(".class")&&(n.getKey().startsWith("dev/zomboid")||n.getKey().endsWith("32.class")))verify(n.getValue());
 try(var z=new ZipOutputStream(Files.newOutputStream(Path.of(args[1])))){for(var n:e.entrySet()){ZipEntry ze=new ZipEntry(n.getKey());ze.setTime(0);z.putNextEntry(ze);z.write(n.getValue());z.closeEntry();}}
 System.out.println("LASER_ISO_NATIVE_BYTECODE_VERIFIED "+args[3]);}
}
