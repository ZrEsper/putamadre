package dev.zomboid.decor.needs.client;
import dev.zomboid.decor.needs.*;import dev.zomboid.decor.needs.Checks.*;
public class ClientChecks{
 static int checks;static void ok(boolean b,String why){checks++;if(!b)throw new AssertionError(why);}
 public static class Keys{public net.minecraft.client.KeyMapping mapping;public void register(net.minecraft.client.KeyMapping k){mapping=k;}}
 public static class Input{public boolean shiftKeyDown,jumping=true;}
 public record Movement(Input input){public Input getInput(){return input;}}
 public static class Context{public int tasks;public java.util.concurrent.CompletableFuture<Void> enqueueWork(Runnable run){tasks++;run.run();return java.util.concurrent.CompletableFuture.completedFuture(null);}}
 public static class Registrar{public net.neoforged.neoforge.network.handling.IPayloadHandler handler;public Object playToClient(Object type,Object codec,net.neoforged.neoforge.network.handling.IPayloadHandler handler){this.handler=handler;return this;}}
 public static class Registration{public Registrar r=new Registrar();public Object registrar(String version){return r;}}
 public static void main(String[] args){World w=new World();Player p=new Player(w);net.minecraft.client.Minecraft mc=net.minecraft.client.Minecraft.INSTANCE;mc.player=p;mc.level=w;Keys keys=new Keys();NeedsClient.keys(keys);ok(keys.mapping.key==88,"X default registered");keys.mapping.pending=1;NeedsClient.tick(null);ok(p.connection.commands.isEmpty(),"X ignored when no need");
 Registration registration=new Registration();Network.register(registration);Context context=new Context();registration.r.handler.handle(new NeedsPayload(1,0),context);ok(context.tasks==1,"network state applied on client work queue");keys.mapping.pending=1;NeedsClient.tick(null);ok(p.connection.commands.equals(java.util.List.of("necesidades aqui")),"X sends only self needs command");NeedsClient.tick(null);ok(p.connection.commands.size()==1,"no automatic repeated command");
 registration.r.handler.handle(new NeedsPayload(2,200),context);Input input=new Input();NeedsClient.input(new Movement(input));ok(input.shiftKeyDown&&!input.jumping,"defecation forces crouch input and blocks jump");NeedsClient.tick(null);ok(p.pose==net.minecraft.world.entity.Pose.CROUCHING,"local forced pose set");keys.mapping.pending=1;NeedsClient.tick(null);ok(p.connection.commands.size()==1,"X ignored during action");for(int i=0;i<220;i++){if(i%40==0)NeedsClient.state(2,20);NeedsClient.tick(null);}ok(p.pose==net.minecraft.world.entity.Pose.CROUCHING,"server heartbeat keeps crouch with slower TPS instead of ending on local ten-second timer");
 registration.r.handler.handle(new NeedsPayload(0,0),context);NeedsClient.tick(null);ok(p.pose==null,"completion releases local forced pose");
 NeedsClient.state(1,0);mc.screen=new Object();keys.mapping.pending=1;NeedsClient.tick(null);ok(p.connection.commands.size()==1,"key does not run through menus");mc.screen=null;mc.player=null;NeedsClient.tick(null);ok(NeedsClient.kind==0&&NeedsClient.ticks==0,"disconnect clears old state");
 System.out.println("NEEDS_CLIENT_NETWORK_CHECKS="+checks);
 }
}
