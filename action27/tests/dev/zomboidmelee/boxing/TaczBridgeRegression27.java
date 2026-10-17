package dev.zomboidmelee.boxing;
public class TaczBridgeRegression27 {
 public static void main(String[]args){var mc=net.minecraft.client.Minecraft.INSTANCE;var p=new MovementChecks26.Local();p.physics.client=true;mc.player=p;mc.level=p.physics;mc.screen=null;Keys26.register(new net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent());BoxingClient16.CLOCK=()->5_000_000_000L;MoveClient25.runAt=4_950_000_000L;if(!dev.zomboid.tacz.MovementCompat20.proneKey(67,0,1))throw new AssertionError("uploaded TaCZ cannot resolve the running-slide bridge");System.out.println("SUPPLIED_TACZ19_MISSING_BRIDGE_REGRESSION_PASS");}
}
