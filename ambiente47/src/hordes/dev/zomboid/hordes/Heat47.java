package dev.zomboid.hordes;
import java.util.*;
/** Server authority; clients only receive a small read-only snapshot. */
public final class Heat47 {
 static final Map<Object,State> STATES=new WeakHashMap<>();
 static Object c(Object o,String n,Object...a){return ZoneReflection.call(o,n,a);}static long now(Object p){return ((Number)c(c(p,"level"),"getGameTime")).longValue();}
 static State state(Object p){return STATES.computeIfAbsent(p,k->new State());}public static double value(Object p){return state(p).heat;}
 public static double cooled(double heat,long elapsed){return Math.max(0,Math.min(100,heat-Math.max(0,elapsed)/20.));}
 static boolean eligible(Object p){try{return p!=null&&Population47.eligible(p)&&!Boolean.TRUE.equals(c(c(p,"level"),"isClientSide"));}catch(RuntimeException notPlayer){return false;}}
 static void add(Object p,double amount,String key,long gate){if(!eligible(p)||!WorldTuning.flag(AmbientSettings47.HEAT))return;State s=state(p);long t=now(p);if(t<s.gates.getOrDefault(key,Long.MIN_VALUE))return;s.gates.put(key,t+gate);s.heat=Math.min(100,s.heat+amount);s.position=c(p,"blockPosition");s.noiseAt=t;}
 public static void gun(Object p,double radius){if(!Double.isFinite(radius)||radius<=0)return;boolean quiet=Boolean.TRUE.equals(c(ZoneReflection.type("dev.zomboid.reactive.NoisePolicy"),"quietShot",p,radius));add(p,quiet?2:8,"gun",2);}
 public static void action(Object p){add(p,1.5,"action",10);}
 public static void explosion(Object event){Object explosion=c(event,"getExplosion"),owner=c(explosion,"getIndirectSourceEntity");add(owner,20,"explosion",20);}

 public static void tick(Object p){if(!eligible(p))return;long t=now(p);State s=state(p);if(s.tick!=Long.MIN_VALUE&&t-s.tick<20&&t>=s.tick)return;long elapsed=s.tick==Long.MIN_VALUE?0:Math.max(0,t-s.tick);s.heat=cooled(s.heat,elapsed);s.tick=t;if(!WorldTuning.flag(AmbientSettings47.HEAT))s.heat=0;
  double x=Population47.coord(p,"X"),z=Population47.coord(p,"Z"),distance=Math.hypot(x-s.x,z-s.z);if(s.known&&distance>.1&&distance<12&&!Boolean.TRUE.equals(c(p,"isPassenger"))&&!Boolean.TRUE.equals(c(p,"isCrouching")))add(p,Boolean.TRUE.equals(c(p,"isSprinting"))?2:.25,"steps",20);s.x=x;s.z=z;s.known=true;
  if(s.heat>=10&&s.position!=null&&t-s.noiseAt<=100&&t>=s.alert){s.alert=t+100;c(HordeEvents.class,"alert",c(p,"level"),s.position,p,Math.min(128,16+s.heat),t+200,false,32);}
  if(t>=s.packet){s.packet=t+40;Object level=c(p,"level");int count=Population47.count(Population47.view(level),p);String json="{\"heat47\":"+(int)Math.ceil(s.heat)+",\"count47\":"+count+",\"limit47\":"+AmbientSettings47.total()+"}";c(ZoneReflection.type("net.neoforged.neoforge.network.PacketDistributor"),"sendToPlayer",p,new AdminNetwork.View(json),java.lang.reflect.Array.newInstance(ZoneReflection.type("net.minecraft.network.protocol.common.custom.CustomPacketPayload"),0));}
 }
 public static void server(Object event){Object server=c(event,"getServer"),world=c(server,"overworld");if(((Number)c(world,"getGameTime")).longValue()%20!=0)return;for(Object p:(List<?>)c(c(server,"getPlayerList"),"getPlayers"))tick(p);}
 static final class State{double heat,x,z;long tick=Long.MIN_VALUE,noiseAt,alert,packet;boolean known;Object position;final Map<String,Long>gates=new HashMap<>();}
}
