import net.minecraft.nbt.CompoundTag;import net.minecraft.resources.ResourceKey;import net.minecraft.world.RandomizableContainer;import snownee.jade.api.Accessor;
public class Checks{
 static class A implements Accessor {Object target;CompoundTag tag=new CompoundTag();A(Object o){target=o;}public Object getTarget(){return target;}public CompoundTag getServerData(){return tag;}}
 static class C implements RandomizableContainer {ResourceKey key;int reads;C(boolean pending){key=pending?new ResourceKey():null;}public ResourceKey getLootTable(){reads++;return key;}}
 public static void main(String[] args)throws Exception{
  var method=Class.forName("GuardProbe").getMethod("run",Accessor.class);var field=Class.forName("GuardProbe").getField("extensions");C pending=new C(true);A a=new A(pending);method.invoke(null,a);if(!a.tag.loot||field.getInt(null)!=0||pending.reads!=1)throw new AssertionError("Pending loot reached extensions");
  A opened=new A(new C(false));method.invoke(null,opened);if(opened.tag.loot||field.getInt(null)!=1)throw new AssertionError("Opened container blocked");
  method.invoke(null,new A(new Object()));method.invoke(null,new A(null));if(field.getInt(null)!=3)throw new AssertionError("Unrelated target blocked");
  var preview=Class.forName("PendingProbe").getMethod("pending",Object.class);for(Object o:new Object[]{"crate_pharmacy","crate_military",null})if(!preview.invoke(null,o).equals("Contenido por revisar"))throw new AssertionError("Revealed category");
  System.out.println("PASS 7 bytecode behavior cases: pending before extensions, opened, unrelated, null, three hidden previews");
 }
}
