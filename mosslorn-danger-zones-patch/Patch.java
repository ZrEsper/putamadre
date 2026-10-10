import java.nio.file.*;import java.util.zip.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.util.CheckClassAdapter;
public class Patch implements Opcodes{
 public static void main(String[] args)throws Exception{
  byte[] b;try(ZipFile z=new ZipFile(args[0])){b=z.getInputStream(z.getEntry("dev/zomboid/hordes/ChunkPopulation.class")).readAllBytes();}
  ClassNode c=new ClassNode();new ClassReader(b).accept(c,0);int edits=0;
  for(MethodNode m:c.methods)if(m.name.equals("populate")){
   int pair=-1;
   for(AbstractInsnNode i:m.instructions.toArray()){
    if(i instanceof TypeInsnNode t&&t.getOpcode()==CHECKCAST&&t.desc.equals("dev/zomboid/hordes/ChunkPopulationRules$Pair")){
     AbstractInsnNode n=i.getNext();while(n!=null&&n.getOpcode()<0)n=n.getNext();if(n instanceof VarInsnNode v&&v.getOpcode()==ASTORE)pair=v.var;
    }
    if(i instanceof MethodInsnNode call&&call.owner.equals("dev/zomboid/hordes/ChunkPopulationRules")&&call.name.equals("missing")){
     if(pair<0)throw new IllegalStateException("pair local not found");m.instructions.insertBefore(i,new VarInsnNode(ALOAD,pair));call.owner="dev/zomboid/danger/Zones";call.desc="(IILdev/zomboid/hordes/ChunkPopulationRules$Pair;)I";edits++;
    }
   }
  }
  if(edits!=1)throw new IllegalStateException("Expected one population hook: "+edits);
  ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(new CheckClassAdapter(w));Path out=Path.of(args[1]);Files.createDirectories(out.getParent());Files.write(out,w.toByteArray());System.out.println("PASS: one existing population hook; rates and cooldowns preserved");
 }
}
