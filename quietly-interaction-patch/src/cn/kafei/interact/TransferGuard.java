package cn.kafei.interact;
public final class TransferGuard{
 static boolean cooling(Object player,Object stack){return stack!=null&&!(Boolean)R.call(stack,"isEmpty")&&(Boolean)R.call(R.call(player,"getCooldowns"),"isOnCooldown",R.call(stack,"getItem"));}
 public static boolean blocked(Object menu,int slot,int button,Object click,Object player){
  if(player.getClass().getName().contains("FakePlayer")||"CLONE".equals(click.toString()))return false;
  java.util.List<?> slots=(java.util.List<?>)R.field(menu,"slots");Object carried=R.call(menu,"getCarried");String action=click.toString();
  if(slot>=0&&slot<slots.size()&&cooling(player,R.call(slots.get(slot),"getItem")))return true;
  if((action.equals("PICKUP")||action.equals("PICKUP_ALL")||action.equals("QUICK_CRAFT")||slot<0)&&cooling(player,carried))return true;
  if(action.equals("SWAP")&&button>=0&&(button<9||button==40)&&cooling(player,R.call(R.call(player,"getInventory"),"getItem",button)))return true;
  return false;
 }
 public static boolean hand(Object player){return cooling(player,R.call(player,"getMainHandItem"));}
 public static boolean swap(Object listener,Object packet){if(!R.call(packet,"getAction").toString().equals("SWAP_ITEM_WITH_OFFHAND"))return false;Object player=R.field(listener,"player");return hand(player)||cooling(player,R.call(player,"getOffhandItem"));}
}
