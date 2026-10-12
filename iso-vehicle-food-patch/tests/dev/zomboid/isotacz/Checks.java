package dev.zomboid.isotacz;
import net.minecraft.world.InteractionHand;
public class Checks {
 static int n;static void check(boolean b){n++;if(!b)throw new AssertionError("case "+n);}
 public static class Stack {boolean empty=true;String anim="NONE";public boolean isEmpty(){return empty;}public String getUseAnimation(){return anim;}}
 public static class Player {boolean passenger;Stack main=new Stack(),off=new Stack();public boolean isPassenger(){return passenger;}public Object getItemInHand(InteractionHand h){return h==InteractionHand.MAIN_HAND?main:off;}}
 public static class Client {public Player player=new Player();}
 public static void main(String[] args){Client c=new Client();Bridge.client=c;check(!NativeActions.nativeInput());c.player.passenger=true;check(NativeActions.riding());check(NativeActions.nativeInput());c.player.passenger=false;c.player.main.empty=false;c.player.main.anim="EAT";check(NativeActions.nativeInput());check(NativeActions.food(c));Bridge.mouseClaimed=true;Bridge.rightAim=true;NativeActions.nativeInput();check(!Bridge.mouseClaimed&&!Bridge.rightAim);c.player.main.anim="DRINK";check(NativeActions.food(c));c.player.main.anim="NONE";check(!NativeActions.nativeInput());c.player.off.empty=false;c.player.off.anim="DRINK";check(NativeActions.nativeInput());c.player.off.empty=true;check(!NativeActions.nativeInput());c.player=null;check(!NativeActions.nativeInput());System.out.println("Native action checks: "+n);}
}
