package checks;
import dev.zomboid.isotacz.*;import net.minecraft.client.Minecraft;import net.minecraft.world.InteractionHand;
public class Checks{
 static int count;static void ok(boolean b,String reason){count++;if(!b)throw new AssertionError(reason);}
 public static class Item{public String id;public Item(String id){this.id=id;}}
 public static class Consumable extends Item implements me.xjqsh.lrtactical.api.item.IConsumable{public Consumable(String data){super("lrtactical:consumable");}}
 public static class Stack{public Item item;public Stack(Item i){item=i;}public boolean isEmpty(){return item==null;}public Item getItem(){return item;}public String getUseAnimation(){return "NONE";}}
 public static class Model{public boolean criticalConditionActive;}
 public static class Player{public boolean gun,using;public Stack main=new Stack(new Item("minecraft:stick")),off=new Stack(null),use=new Stack(null);public Model model=new Model();public boolean isPassenger(){return false;}public boolean isUsingItem(){return using;}public Stack getUseItem(){return use;}public Stack getMainHandItem(){return main;}public Stack getOffhandItem(){return off;}public Stack getItemInHand(InteractionHand h){return h==InteractionHand.MAIN_HAND?main:off;}}
 public static class Key{public boolean physical;public boolean isDown(){return NativeActions.nativeInput()?physical:com.cleannrooster.dungeons_iso.mod.Mod.using;}}
 public static class Options{public Key keyUse=new Key();}
 public static void main(String[]args){Minecraft mc=Minecraft.INSTANCE;Player p=mc.player;
  for(String id:new String[]{"firstaid:defibrillator","firstaid:bandage","firstaid:plaster","firstaid:zombie_cure","some:medical_pack"}){p.main=new Stack(new Item(id));mc.options.keyUse.physical=true;ok(NativeActions.nativeInput(),"medical uses native input before key lookup");ok(mc.options.keyUse.isDown(),"self defib sees physical held key");ok(MedicalInput30.medical(),"treatment suppresses camera recenter");ok(MedicalInput30.skipMovement(),"skip click to move steering");ok(mc.location==null&&mc.original==null,"old click-to-move path cleared");mc.options.keyUse.physical=false;ok(!mc.options.keyUse.isDown()&&!MedicalInput30.medical(),"release preserved");}
  for(int i=0;i<8;i++){p.main=new Stack(new Consumable("generic_"+i));p.use=p.main;p.using=true;ok(MedicalInput30.held()&&MedicalInput30.medical(),"recognize actual LR consumable interface despite generic registry ID");ok(NativeActions.nativeInput(),"LR held use not converted to navigation");p.using=false;ok(!MedicalInput30.medical(),"no leftover camera suppression");ok(!MedicalInput30.skipMovement(),"normal isometric walking returns when treatment ends");}
  p.main=new Stack(new Item("tacz:modern_kinetic_gun"));p.gun=true;p.off=new Stack(new Item("firstaid:defibrillator"));p.model.criticalConditionActive=false;ok(!MedicalInput30.held(),"offhand medicine does not steal normal gun aim");p.model.criticalConditionActive=true;ok(MedicalInput30.held(),"downed player can use offhand defib despite main gun");mc.options.keyUse.physical=true;ok(mc.options.keyUse.isDown(),"offhand self defib receives key");mc.options.keyUse.physical=false;p.using=true;p.use=p.off;p.model.criticalConditionActive=false;ok(MedicalInput30.held(),"actual offhand treatment wins over gun");p.using=false;p.gun=false;p.off=new Stack(null);
  p.main=new Stack(new Item("minecraft:stick"));ok(!MedicalInput30.held(),"normal items unaffected");p.main=new Stack(new Consumable("ai2"));Bridge.iso=false;ok(!MedicalInput30.held(),"other cameras unaffected");Bridge.iso=true;mc.player=null;ok(!MedicalInput30.held()&&!MedicalInput30.medical(),"disconnect safe");
  System.out.println("MEDICAL30_CHECKS="+count);
 }
}
