package dev.zomboid.scavenging;
public final class FinalLoot17{
 static boolean warned;
 public static Object prepare(Object inventory,Object stack,Object target){Search.Target t=(Search.Target)target;if(PlayerStorage.owned(t)||t.entity!=null||R.yes(R.call(stack,"isEmpty")))return stack;try{Class<?> rules=R.type("dev.zomboid.remains.LootFinish15");Object family=R.call(rules,"family",stack);if(family==null)return stack;Loot.Inv inv=(Loot.Inv)inventory;for(int i=0;i<inv.size();i++){Object s=inv.get(i);if(!R.yes(R.call(s,"isEmpty"))&&family.equals(R.call(rules,"family",s)))return R.get(R.type("net.minecraft.world.item.ItemStack"),"EMPTY");}return R.call(stack,"copyWithCount",1);}catch(IllegalStateException unavailable){return stack;}}

 public static void after(Object target,Object player){if(PlayerStorage.owned((Search.Target)target))return;Object corpse=R.get(target,"corpse");try{if(corpse!=null)R.call(R.type("dev.zomboid.remains.LootFinish15"),"normalize",corpse,R.get(target,"level"));else if(R.get(target,"be")!=null)R.call(R.type("dev.zomboid.remains.LootFinish15"),"inventory",new View(Loot.inventory((Search.Target)target)),R.get(target,"level"));}catch(IllegalStateException e){if(!warned){warned=true;System.err.println("[Quietly loot] Final loot pass unavailable: "+e);}}}
 public static final class View{final Loot.Inv inv;View(Loot.Inv i){inv=i;}public int getContainerSize(){return inv.size();}public Object getItem(int i){return inv.get(i);}public void setItem(int i,Object s){inv.set(i,s);}public void setChanged(){inv.changed();}}
}
