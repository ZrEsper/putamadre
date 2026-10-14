package dev.zomboid.hordes;
import java.util.*;import java.lang.reflect.Array;
/** Existing admin channel transports a tagged settings snapshot without opening a screen. */
public final class CombatSync {
 static final String PREFIX="!melee29:";static final Map<Object,Integer> sent=new WeakHashMap<>();static Object server,connection;
 static volatile Map<String,Double> client=Map.of();
 public static String encode(Map<String,Double> values){StringBuilder b=new StringBuilder(PREFIX);for(String key:CombatPanel.MELEE)b.append(values.getOrDefault(key,1.)).append(',');return b.toString();}
 public static Map<String,Double> decode(String wire){if(!wire.startsWith(PREFIX))throw new IllegalArgumentException("Invalid combat settings prefix");String[] v=wire.substring(PREFIX.length()).split(",");if(v.length!=CombatPanel.MELEE.length)throw new IllegalArgumentException("Invalid combat setting count");Map<String,Double> out=new HashMap<>();for(int i=0;i<v.length;i++){double d=Double.parseDouble(v[i]);if(!Double.isFinite(d)||d<.25||d>4)throw new IllegalArgumentException("Invalid combat setting");out.put(CombatPanel.MELEE[i],d);}return Map.copyOf(out);}
 public static boolean accept(String wire){if(!wire.startsWith(PREFIX))return false;try{Map<String,Double> next=decode(wire);connection=clientConnection();client=next;}catch(RuntimeException e){System.err.println("[Hordes melee] Ignored invalid settings snapshot: "+e);}return true;}
 static Object clientConnection(){Object mc=ZoneReflection.call(ZoneReflection.type("net.minecraft.client.Minecraft"),"getInstance");return ZoneReflection.call(mc,"getConnection");}
 public static double multiplier(String key){
  Object side=ZoneReflection.call(ZoneReflection.type("net.neoforged.fml.util.thread.EffectiveSide"),"get");
  if(Boolean.TRUE.equals(ZoneReflection.call(side,"isServer")))return WorldTuning.get(key);
  return connection!=null&&connection==clientConnection()?client.getOrDefault(key,1.):1;
 }
 public static void reset(){sent.clear();server=null;client=Map.of();connection=null;IndoorSpawn.clear();}
 public static void tick(Object event){
  Object current=ZoneReflection.call(event,"getServer");long tick=((Number)ZoneReflection.call(current,"getTickCount")).longValue();if(tick%20!=0)return;
  if(server!=current){server=current;sent.clear();}
  int revision=WorldTuning.revision();Object list=ZoneReflection.call(current,"getPlayerList");
  for(Object player:(Iterable<?>)ZoneReflection.call(list,"getPlayers"))if(sent.getOrDefault(player,-1)!=revision){
   try{Object payload=ZoneReflection.type("dev.zomboid.hordes.AdminNetwork$View").getConstructor(String.class).newInstance(encode(WorldTuning.snapshot()));
    ZoneReflection.call(ZoneReflection.type("net.neoforged.neoforge.network.PacketDistributor"),"sendToPlayer",player,payload,Array.newInstance(ZoneReflection.type("net.minecraft.network.protocol.common.custom.CustomPacketPayload"),0));sent.put(player,revision);
   }catch(ReflectiveOperationException|RuntimeException e){System.err.println("[Hordes melee] Settings synchronization failed: "+e);}
  }
 }
}
