import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import java.util.*;import java.util.zip.*;
public class Check{
 static Map<String,ClassNode>classes(String path)throws Exception{Map<String,ClassNode>r=new HashMap<>();try(ZipFile z=new ZipFile(path)){for(ZipEntry e:Collections.list(z.entries()))if(e.getName().endsWith(".class")){ClassNode c=new ClassNode();new ClassReader(z.getInputStream(e).readAllBytes()).accept(c,0);r.put(c.name,c);}}return r;}
 static Object value(AnnotationNode a,String key){for(int i=0;a.values!=null&&i<a.values.size();i+=2)if(a.values.get(i).equals(key))return a.values.get(i+1);return null;}
 static List<AnnotationNode>anns(List<AnnotationNode>a,List<AnnotationNode>b){List<AnnotationNode>r=new ArrayList<>();if(a!=null)r.addAll(a);if(b!=null)r.addAll(b);return r;}
 static void require(boolean ok,String s){if(!ok)throw new IllegalStateException(s);}
 public static void main(String[]args)throws Exception{var integration=classes(args[0]);var addon=classes(args[1]);Set<String>verified=new TreeSet<>();int targets=0,shadows=0;
  for(ClassNode c:integration.values()){
   for(MethodNode m:c.methods)for(AbstractInsnNode i:m.instructions){if(i instanceof MethodInsnNode call&&call.owner.startsWith("dev/zomboid/mutants/")){ClassNode owner=addon.get(call.owner);require(owner!=null,"missing class "+call.owner);MethodNode found=owner.methods.stream().filter(x->x.name.equals(call.name)&&x.desc.equals(call.desc)).findFirst().orElseThrow();require((found.access&Opcodes.ACC_PUBLIC)!=0,"method not public "+call.name);require(((found.access&Opcodes.ACC_STATIC)!=0)==(call.getOpcode()==Opcodes.INVOKESTATIC),"static mismatch "+call.name);verified.add(call.owner+"."+call.name+call.desc);}if(i instanceof FieldInsnNode field&&field.owner.startsWith("dev/zomboid/mutants/")){require(addon.containsKey(field.owner),"missing owner");require(addon.get(field.owner).fields.stream().anyMatch(f->f.name.equals(field.name)&&f.desc.equals(field.desc)),"missing field "+field.name);verified.add(field.owner+"."+field.name+field.desc);}}
   for(AnnotationNode mix:anns(c.visibleAnnotations,c.invisibleAnnotations))if(mix.desc.endsWith("/Mixin;")&&value(mix,"targets")instanceof List<?>list)for(Object raw:list){ClassNode target=addon.get(raw.toString().replace('.','/'));if(target==null)continue;
    for(MethodNode method:c.methods)for(AnnotationNode ann:anns(method.visibleAnnotations,method.invisibleAnnotations)){
     if(ann.desc.endsWith("/Inject;")){for(Object selector:(List<?>)value(ann,"method"))require(target.methods.stream().anyMatch(m->(m.name+m.desc).equals(selector)),"missing injector target "+selector);targets++;}
     if(ann.desc.endsWith("/Shadow;")){require(target.methods.stream().anyMatch(m->m.name.equals(method.name)&&m.desc.equals(method.desc)),"missing shadow method "+method.name);shadows++;}}
    for(FieldNode f:c.fields)for(AnnotationNode ann:anns(f.visibleAnnotations,f.invisibleAnnotations))if(ann.desc.endsWith("/Shadow;")){require(target.fields.stream().anyMatch(x->x.name.equals(f.name)&&x.desc.equals(f.desc)),"missing shadow field "+f.name);shadows++;}
   }
  }
  require(targets==2&&shadows==3&&verified.size()>=4,"unexpected coverage");System.out.println("PUBLIC_API_REFERENCES="+verified.size()+" MIXIN_TARGETS="+targets+" SHADOWS="+shadows);for(String s:verified)System.out.println(s);
 }
}
