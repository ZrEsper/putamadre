package dev.zomboid.scavenging;
import dev.zomboid.hordes.*;import java.util.*;import java.nio.file.*;import net.minecraft.world.item.ItemStack;
public class BridgeChecks47 {
 static int checks;static void ok(boolean b,String s){checks++;if(!b)throw new AssertionError(s);}
 public static void main(String[]args)throws Exception{EnvironmentChecks47.main(args);WorldTuning.set("panel_cofres_pilas",4.);var w=new EnvironmentChecks47.World(new PanelChecks40.Server(Files.createTempDirectory("quiet47")));var p=new EnvironmentChecks47.Player(w,8,8);var b=new EnvironmentChecks47.Barrel();var t=new Search.Target(w,b.getBlockPos(),b,null,null,"barrel");Loot.bonus(t,p,4,100);ok(b.items.stream().anyMatch(x->!((ItemStack)x).isEmpty()),"native Quietly bonus uses authoritative Hordes generator");List<Object>copy=new ArrayList<>(b.items);Loot.bonus(t,p,4,100);ok(copy.equals(b.items),"native bonus cannot refill generated barrel");
 var owned=new EnvironmentChecks47.Barrel();owned.tag.putBoolean("QuietlyPlayerPlaced",true);Loot.bonus(new Search.Target(w,owned.getBlockPos(),owned,null,null,"barrel"),p,4,100);ok(owned.items.stream().allMatch(x->((ItemStack)x).isEmpty()),"native owned storage early exit preserved");
 var body=new Object();Loot.bonus(new Search.Target(w,null,null,body,null,"corpse"),p,4,100);ok(true,"native corpse bonus delegated: no second random loot system");System.out.println("QUIET_NATIVE_ENVIRONMENT_BRIDGE_CHECKS="+checks);}
}
