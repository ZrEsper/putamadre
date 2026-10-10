package cn.kafei.interact;
import java.util.*;import net.minecraft.world.*;import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
public class Checks{
 public static class Stack{String item;Stack(String i){item=i;}public boolean isEmpty(){return item==null;}public String getItem(){return item;}}
 public static class Cooldowns{Set<String> active=new HashSet<>();public boolean isOnCooldown(String item){return active.contains(item);}}
 public static class Inventory{Map<Integer,Stack> stacks=new HashMap<>();public Stack getItem(int n){return stacks.getOrDefault(n,new Stack(null));}}
 public static class Player{Cooldowns c=new Cooldowns();Inventory inv=new Inventory();public Cooldowns getCooldowns(){return c;}public Inventory getInventory(){return inv;}public Stack getMainHandItem(){return inv.getItem(0);}public Stack getOffhandItem(){return inv.getItem(40);}}
 public static class Slot{Stack stack;Slot(String s){stack=new Stack(s);}public Stack getItem(){return stack;}}
 public static class Menu{public List<Slot> slots=List.of(new Slot("stone"),new Slot("bread"));Stack cursor=new Stack(null);public Stack getCarried(){return cursor;}}
 public static class Listener{public Player player=new Player();}public static class Packet{String action;Packet(String a){action=a;}public String getAction(){return action;}}
 public static class Box extends BaseContainerBlockEntity implements RandomizableContainer{Object loot;int reads;public Object getLootTable(){return loot;}public int getContainerSize(){return 2;}public Stack getItem(int i){reads++;return new Stack(i==0?"firstaid:bandage":"minecraft:bread");}}
 static int checks;static void check(boolean b){checks++;if(!b)throw new AssertionError("case "+checks);}
 public static void main(String[] args){Player p=new Player();Menu m=new Menu();
  check(!TransferGuard.blocked(m,0,0,"PICKUP",p));p.c.active.add("stone");
  for(String a:List.of("PICKUP","QUICK_MOVE","SWAP","THROW","PICKUP_ALL","QUICK_CRAFT"))check(TransferGuard.blocked(m,0,0,a,p));
  check(!TransferGuard.blocked(m,1,0,"PICKUP",p));check(!TransferGuard.blocked(m,0,0,"CLONE",p));
  m.cursor=new Stack("stone");check(TransferGuard.blocked(m,1,0,"PICKUP",p));check(TransferGuard.blocked(m,-999,0,"PICKUP",p));check(TransferGuard.blocked(m,1,0,"QUICK_CRAFT",p));check(!TransferGuard.blocked(m,1,0,"QUICK_MOVE",p));
  m.cursor=new Stack(null);p.inv.stacks.put(40,new Stack("stone"));check(TransferGuard.blocked(m,1,40,"SWAP",p));
  p.inv.stacks.put(0,new Stack("stone"));check(TransferGuard.hand(p));p.c.active.clear();check(!TransferGuard.hand(p));check(!TransferGuard.blocked(m,0,0,"PICKUP",p));
  Listener l=new Listener();l.player.inv.stacks.put(40,new Stack("stone"));l.player.c.active.add("stone");check(TransferGuard.swap(l,new Packet("SWAP_ITEM_WITH_OFFHAND")));check(!TransferGuard.swap(l,new Packet("START_DESTROY_BLOCK")));
  check(Interaction.category("firstaid:bandage").equals("Medicina"));check(Interaction.category("minecraft:bread").equals("Alimentos / bebidas"));
  Box b=new Box();b.loot=new Object();check(Interaction.preview(b,p).equals("Contenido por revisar"));check(b.reads==0);b.loot="mosslorn:chests/crate_pharmacy";check(Interaction.preview(b,p).equals("Posibles suministros médicos")&&b.reads==0);
  b.loot=null;check(Interaction.preview(b,p).equals("Medicina · Alimentos / bebidas"));check(b.reads==2);
  b.lockKey=new Object();int reads=b.reads;check(Interaction.preview(b,p).equals("Contenido protegido"));check(b.reads==reads);
  check(Interaction.supported(new net.minecraft.world.level.block.DoorBlock()));check(Interaction.action(new net.minecraft.world.level.block.DoorBlock()).equals("Abrir / cerrar"));
  System.out.println("PASS "+checks+" real helper cases: click variants, cursor, offhand, drop, expiry, preview, ungenerated loot and access locks");
 }
}