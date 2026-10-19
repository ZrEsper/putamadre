import java.nio.file.*;import java.util.*;import java.util.zip.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
public class Patch implements Opcodes {
 public static void main(String[]a)throws Exception{Map<String,byte[]> e=new TreeMap<>();try(ZipFile z=new ZipFile(a[0])){for(var n:Collections.list(z.entries()))e.put(n.getName(),z.getInputStream(n).readAllBytes());}int hits=0;
 for(String name:List.of("EntityRenderCaptureHandler","ClientMobTextureCache","ClientRagdollRenderer")){String path="com/raiiiden/ragdollified/client/"+name+".class";ClassNode c=new ClassNode();new ClassReader(e.get(path)).accept(c,0);
 for(MethodNode m:c.methods){
 if(name.equals("EntityRenderCaptureHandler"))for(var n:m.instructions.toArray())if(n instanceof MethodInsnNode i&&i.owner.endsWith("MobModelCompatibility")&&i.name.equals("capture")){i.owner="com/raiiiden/ragdollified/client/repair/Capture21";i.name="model";i.desc="(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V";hits++;}
 if(name.equals("ClientMobTextureCache")&&m.name.equals("cleanup")){m.instructions.clear();m.tryCatchBlocks.clear();m.localVariables=null;m.instructions.add(new FieldInsnNode(GETSTATIC,c.name,"TEXTURE_CACHE","Ljava/util/Map;"));m.instructions.add(new MethodInsnNode(INVOKESTATIC,"com/raiiiden/ragdollified/client/repair/Capture21","trim","(Ljava/util/Map;)V",false));m.instructions.add(new InsnNode(RETURN));hits++;}
 if(name.equals("ClientRagdollRenderer")&&m.name.equals("getBaseMobTexture")){int count=0;for(var n:m.instructions.toArray())if(n instanceof MethodInsnNode i&&i.name.equals("setCachedTexture")){count++;if(count==2){m.instructions.set(i,new InsnNode(POP2));hits++;}}if(count!=2)throw new AssertionError("texture cache call count "+count);}
 }
 ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);e.put(path,w.toByteArray());}
 if(hits!=3)throw new AssertionError(hits);
 e.put("com/raiiiden/ragdollified/client/repair/Capture21.class",Files.readAllBytes(Path.of(a[1])));
 try(ZipOutputStream z=new ZipOutputStream(Files.newOutputStream(Path.of(a[2])))){for(var row:e.entrySet()){var n=new ZipEntry(row.getKey());n.setTime(0);z.putNextEntry(n);z.write(row.getValue());z.closeEntry();}}System.out.println("RAGDOLL_HUMANOID_MODEL_CAPTURE_AND_TEXTURE_CACHE_PATCHES="+hits);
 }
}
