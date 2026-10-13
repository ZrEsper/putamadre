import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;

public class Patch implements Opcodes {
    public static void main(String[]args)throws Exception{
        String kind=args[0];int hooks=0,methods=0;Path out=Path.of(args[2]);Files.createDirectories(out.getParent());
        try(ZipFile z=new ZipFile(args[1]);ZipOutputStream w=new ZipOutputStream(Files.newOutputStream(out))){
            for(var entries=z.entries();entries.hasMoreElements();){
                var entry=entries.nextElement();byte[] data=z.getInputStream(entry).readAllBytes();boolean changed=false;
                if(entry.getName().endsWith(".class")){
                    ClassNode c=new ClassNode();new ClassReader(data).accept(c,0);
                    for(MethodNode m:c.methods){
                        String helper=null,owner=null;boolean argument=true;
                        if(kind.equals("tacz")){
                            owner="dev/zomboid/taczinteract/Priority";
                            if(c.name.equals("com/tacz/guns/client/event/ClientPreventGunClick")&&m.name.equals("onClickInput"))helper="allowUse";
                            if(c.name.equals("com/tacz/guns/client/input/AimKey")&&m.name.equals("onAimPress"))helper="aimEvent";
                            if(c.name.equals("com/tacz/guns/client/input/AimKey")&&m.name.equals("onAimHoldingPreInput")){helper="holdAim";argument=false;}
                        }else{
                            owner="dev/zomboid/lriso/UsePriority";
                            if(c.name.equals("me/xjqsh/lrtactical/client/ClientEventsHandler")&&m.name.equals("onClickInput"))helper="allowUse";
                            if(c.name.equals("me/xjqsh/lrtactical/client/input/AttackKeys")&&m.name.equals("onSpAttack"))helper="meleeEvent";
                        }
                        if(helper==null)continue;
                        if((m.access&ACC_STATIC)==0||Type.getReturnType(m.desc)!=Type.VOID_TYPE||Type.getArgumentTypes(m.desc).length!=1)throw new IllegalStateException(m.name+m.desc);
                        InsnList h=new InsnList();LabelNode next=new LabelNode();if(argument)h.add(new VarInsnNode(ALOAD,0));
                        h.add(new MethodInsnNode(INVOKESTATIC,owner,helper,argument?"(Ljava/lang/Object;)Z":"()Z",false));h.add(new JumpInsnNode(IFEQ,next));h.add(new InsnNode(RETURN));h.add(next);h.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(h);
                        hooks++;changed=true;
                    }
                    if(changed){
                        ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(writer);data=writer.toByteArray();ClassNode check=new ClassNode();new ClassReader(data).accept(check,0);
                        for(MethodNode m:check.methods)if(m.instructions.size()>0){new Analyzer<>(new BasicVerifier()).analyze(c.name,m);methods++;}
                        System.out.println("Patched "+c.name);
                    }
                }
                w.putNextEntry(new ZipEntry(entry.getName()));w.write(data);w.closeEntry();
            }
        }
        if(hooks!=(kind.equals("tacz")?3:2))throw new IllegalStateException("Unexpected input contract: "+hooks);
        System.out.println("Verified input guards: "+hooks+", original methods: "+methods);
    }
}
