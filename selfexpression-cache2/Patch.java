import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.tree.analysis.*;import java.util.*;import java.util.zip.*;import java.nio.file.*;import java.security.*;
public class Patch implements Opcodes{
 static final String BASE="net/mcreator/selfexpression/client/",MODEL="net/minecraft/client/model/HumanoidModel";static Map<String,Integer> shapes=new LinkedHashMap<>();static List<List<AbstractInsnNode>> expressions=new ArrayList<>();static int methods,bakes;
 static MethodNode factory(String name,List<AbstractInsnNode> code){MethodNode m=new MethodNode(ACC_PUBLIC|ACC_STATIC,name,"()Ljava/lang/Object;",null,null);for(AbstractInsnNode n:code)m.instructions.add(n.clone(new HashMap<>()));m.instructions.add(new InsnNode(ARETURN));return m;}
 static byte[] serialize(ClassNode c){ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);return w.toByteArray();}
 static byte[] normalized(List<AbstractInsnNode> code){ClassNode c=new ClassNode();c.version=V21;c.access=ACC_PUBLIC;c.name=BASE+"ShapeFingerprint";c.superName="java/lang/Object";c.methods.add(factory("create",code));return serialize(c);}
 static byte[] patch(byte[] bytes)throws Exception{ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);boolean changed=false;
  for(MethodNode m:c.methods)if(m.name.equals("getHumanoidArmorModel")){
   List<AbstractInsnNode> code=new ArrayList<>();AbstractInsnNode first=null,last=null;
   for(AbstractInsnNode n:m.instructions.toArray()){
    if(first==null&&n instanceof TypeInsnNode t&&t.getOpcode()==NEW&&t.desc.equals(MODEL))first=n;
    if(first!=null){if(n.getOpcode()>=0){if(n instanceof VarInsnNode||n instanceof JumpInsnNode||n instanceof InvokeDynamicInsnNode)throw new IllegalStateException("Stateful factory "+c.name);code.add(n);}if(n instanceof MethodInsnNode x&&x.owner.equals(MODEL)&&x.name.equals("<init>")){last=n;break;}}
   }
   if(first==null||last==null)throw new IllegalStateException("Missing native construction "+c.name);
   String key=HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(normalized(code)));Integer id=shapes.get(key);if(id==null){id=shapes.size();shapes.put(key,id);expressions.add(code);}
   for(AbstractInsnNode n:code)if(n instanceof MethodInsnNode x&&x.name.equals("bakeLayer"))bakes++;
   InsnList replacement=new InsnList();replacement.add(new LdcInsnNode(id));replacement.add(new MethodInsnNode(INVOKESTATIC,BASE+"SharedArmorModels","get","(I)Ljava/lang/Object;",false));replacement.add(new TypeInsnNode(CHECKCAST,MODEL));m.instructions.insertBefore(first,replacement);
   AbstractInsnNode stop=last.getNext();for(AbstractInsnNode n=first;n!=stop;){AbstractInsnNode next=n.getNext();m.instructions.remove(n);n=next;}
   methods++;changed=true;
  }
  return changed?serialize(c):bytes;
 }
 static byte[] factories(){ClassNode c=new ClassNode();c.version=V21;c.access=ACC_PUBLIC|ACC_FINAL;c.name=BASE+"SharedArmorFactories";c.superName="java/lang/Object";
  for(int i=0;i<expressions.size();i++)c.methods.add(factory("create"+i,expressions.get(i)));
  MethodNode dispatch=new MethodNode(ACC_PUBLIC|ACC_STATIC,"create","(I)Ljava/lang/Object;",null,null);dispatch.instructions.add(new VarInsnNode(ILOAD,0));LabelNode bad=new LabelNode();LabelNode[] cases=new LabelNode[expressions.size()];for(int i=0;i<cases.length;i++)cases[i]=new LabelNode();dispatch.instructions.add(new TableSwitchInsnNode(0,cases.length-1,bad,cases));
  for(int i=0;i<cases.length;i++){dispatch.instructions.add(cases[i]);dispatch.instructions.add(new FrameNode(F_SAME,0,null,0,null));dispatch.instructions.add(new MethodInsnNode(INVOKESTATIC,c.name,"create"+i,"()Ljava/lang/Object;",false));dispatch.instructions.add(new InsnNode(ARETURN));}
  dispatch.instructions.add(bad);dispatch.instructions.add(new FrameNode(F_SAME,0,null,0,null));dispatch.instructions.add(new TypeInsnNode(NEW,"java/lang/IllegalArgumentException"));dispatch.instructions.add(new InsnNode(DUP));dispatch.instructions.add(new LdcInsnNode("Unknown armor shape"));dispatch.instructions.add(new MethodInsnNode(INVOKESPECIAL,"java/lang/IllegalArgumentException","<init>","(Ljava/lang/String;)V",false));dispatch.instructions.add(new InsnNode(ATHROW));c.methods.add(dispatch);return serialize(c);
 }
 static void verify(byte[] b)throws Exception{ClassNode c=new ClassNode();new ClassReader(b).accept(c,0);for(MethodNode m:c.methods)if((m.access&(ACC_ABSTRACT|ACC_NATIVE))==0)new Analyzer<BasicValue>(new BasicVerifier()).analyze(c.name,m);}
 public static void main(String[] args)throws Exception{Map<String,byte[]> entries=new TreeMap<>();try(ZipFile z=new ZipFile(args[0])){for(ZipEntry e:Collections.list(z.entries())){byte[] b=z.getInputStream(e).readAllBytes();entries.put(e.getName(),e.getName().startsWith("net/mcreator/selfexpression/item/")&&e.getName().endsWith(".class")?patch(b):b);}}
  if(methods!=876||shapes.size()!=5)throw new IllegalStateException("Unexpected shapes/methods "+shapes.size()+"/"+methods);
  try(var paths=Files.walk(Path.of(args[2]))){for(Path p:paths.filter(p->p.toString().endsWith(".class")).toList())entries.put(Path.of(args[2]).relativize(p).toString(),Files.readAllBytes(p));}
  entries.put(BASE+"SharedArmorFactories.class",factories());
  try(ZipOutputStream z=new ZipOutputStream(Files.newOutputStream(Path.of(args[1])))){for(var e:entries.entrySet()){if(e.getKey().endsWith(".class")&&(e.getKey().contains("/item/")||e.getKey().contains("SharedArmor")))verify(e.getValue());ZipEntry n=new ZipEntry(e.getKey());n.setTime(0);z.putNextEntry(n);z.write(e.getValue());z.closeEntry();}}
  System.out.println("Native armor methods="+methods+", unique geometries="+shapes.size()+", previous bakes per full collection="+bakes);for(int i=0;i<expressions.size();i++){int count=0;for(var n:expressions.get(i))if(n instanceof MethodInsnNode x&&x.name.equals("bakeLayer"))count++;System.out.println("Geometry "+i+": "+count+" layer bakes per reload");}
 }
}
