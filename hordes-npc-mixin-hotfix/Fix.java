import java.nio.file.*;import java.util.*;import java.util.zip.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
public class Fix {
 static ClassNode read(byte[]b){ClassNode c=new ClassNode();new ClassReader(b).accept(c,0);return c;}
 static Object value(AnnotationNode a,String key){if(a.values!=null)for(int i=0;i<a.values.size();i+=2)if(a.values.get(i).equals(key))return a.values.get(i+1);return null;}
 static List<AnnotationNode> annotations(MethodNode m){List<AnnotationNode>a=new ArrayList<>();if(m.visibleAnnotations!=null)a.addAll(m.visibleAnnotations);if(m.invisibleAnnotations!=null)a.addAll(m.invisibleAnnotations);return a;}
 static Map<String,byte[]> entries(String file)throws Exception{Map<String,byte[]>r=new TreeMap<>();try(ZipFile z=new ZipFile(file)){for(ZipEntry e:Collections.list(z.entries()))r.put(e.getName(),z.getInputStream(e).readAllBytes());}return r;}
 static void patch(String input,String output)throws Exception{
  Map<String,byte[]>r=entries(input);String name="dev/zomboid/hordes/mixin/MilitaryBanditBalanceMixin.class";ClassNode c=read(r.get(name));int changed=0;
  for(MethodNode m:c.methods)for(AnnotationNode a:annotations(m))if(a.desc.endsWith("/Redirect;")&&m.name.equals("balance$military")){
   @SuppressWarnings("unchecked") List<String>methods=(List<String>)value(a,"method");if(!methods.equals(List.of("outfit(Ljava/lang/Object;Ljava/util/Random;)V")))throw new IllegalStateException("unexpected original selectors");
   methods.add("originalOutfit33(Ljava/lang/Object;Ljava/util/Random;)V");changed++;
  }
  if(changed!=1)throw new IllegalStateException("selector patch count "+changed);ClassWriter w=new ClassWriter(0);c.accept(w);byte[]after=w.toByteArray();ClassNode check=read(after);
  // Only annotations change; executable instructions must remain exactly the same.
  for(int i=0;i<c.methods.size();i++){ClassWriter beforeCode=new ClassWriter(0),afterCode=new ClassWriter(0);beforeCode.visit(c.version,c.access,c.name,null,c.superName,null);afterCode.visit(c.version,c.access,c.name,null,c.superName,null);MethodNode b=read(r.get(name)).methods.get(i),n=check.methods.get(i);b.visibleAnnotations=null;b.invisibleAnnotations=null;n.visibleAnnotations=null;n.invisibleAnnotations=null;b.accept(beforeCode);n.accept(afterCode);if(!Arrays.equals(beforeCode.toByteArray(),afterCode.toByteArray()))throw new IllegalStateException("executable changed");}
  r.put(name,after);try(ZipOutputStream z=new ZipOutputStream(Files.newOutputStream(Path.of(output)))){for(var e:r.entrySet()){ZipEntry n=new ZipEntry(e.getKey());n.setTime(0);z.putNextEntry(n);z.write(e.getValue());z.closeEntry();}}
 }
 static int compatibility(String hordes,String npc)throws Exception{
  Map<String,byte[]>h=entries(hordes),n=entries(npc);int checked=0;
  for(var e:h.entrySet())if(e.getKey().contains("/mixin/")&&e.getKey().endsWith(".class")){
   ClassNode mix=read(e.getValue());AnnotationNode ann=null;if(mix.invisibleAnnotations!=null)for(var a:mix.invisibleAnnotations)if(a.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;"))ann=a;
   if(mix.visibleAnnotations!=null)for(var a:mix.visibleAnnotations)if(a.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;"))ann=a;
   if(ann==null||!(value(ann,"targets")instanceof List<?>targets))continue;
   for(Object target:targets){byte[]bytes=n.get(target.toString().replace('.','/')+".class");if(bytes==null)continue;ClassNode type=read(bytes);
    for(MethodNode method:mix.methods)for(AnnotationNode injection:annotations(method))if(injection.desc.endsWith("/Redirect;")||injection.desc.endsWith("/Inject;")){
     List<?>selectors=(List<?>)value(injection,"method");Object atObject=value(injection,"at");List<?>ats=atObject instanceof List<?>l?l:List.of(atObject);int hits=0;
     for(MethodNode tm:type.methods)if(selectors.contains(tm.name+tm.desc)||selectors.contains(tm.name))for(Object ao:ats){AnnotationNode at=(AnnotationNode)ao;String kind=(String)value(at,"value");if("HEAD".equals(kind)){hits++;continue;}if(!"INVOKE".equals(kind))throw new IllegalStateException("unhandled injection "+kind);String invoke=(String)value(at,"target");int matches=0;for(AbstractInsnNode ins:tm.instructions)if(ins instanceof MethodInsnNode mc&&invoke.equals("L"+mc.owner+";"+mc.name+mc.desc))matches++;Object ordinal=value(at,"ordinal");hits+=ordinal instanceof Integer i?(matches>i?1:0):matches;}
     int required=value(injection,"require")instanceof Integer i?i:1;if(hits<required)throw new IllegalStateException(method.name+": "+hits+"/"+required+" injection sites");checked++;
    }
   }
  }
  if(checked!=3)throw new IllegalStateException("Expected 3 Hordes/NPC injections, got "+checked);return checked;
 }
 public static void main(String[]a)throws Exception{if(a[0].equals("patch"))patch(a[1],a[2]);else System.out.println("COMPATIBILITY_CHECKS="+compatibility(a[1],a[2]));}
}
