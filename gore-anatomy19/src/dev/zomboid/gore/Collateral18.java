package dev.zomboid.gore;
import java.util.*;
/** A projectile retains 65% damage after each distinct body crossed. */
public final class Collateral18 {
 static final Map<Object,Flight>FLIGHTS=Collections.synchronizedMap(new WeakHashMap<>());
 static final class Flight{int bodies;final Map<Integer,Double>targets=new HashMap<>();}
 public static double factor(int previousBodies){return Math.pow(.65,Math.max(0,Math.min(64,previousBodies)));}
 static double target(Object bullet,int id){Flight f=FLIGHTS.computeIfAbsent(bullet,k->new Flight());Double old=f.targets.get(id);if(old!=null)return old;double value=factor(f.bodies);if(f.targets.size()<64)f.targets.put(id,value);f.bodies=Math.min(64,f.bodies+1);return value;}
 public static void incoming(Object event){Object source=R.call(event,"getSource"),bullet=R.call(source,"getDirectEntity");if(!Bullet13.tacz(bullet)||R.yes(R.get(bullet,"explosion")))return;Object entity=R.call(event,"getEntity");if(R.yes(R.get(R.call(entity,"level"),"isClientSide")))return;float amount=(float)R.num(R.call(event,"getAmount"));if(!Float.isFinite(amount)||amount<=0)return;double multiplier=target(bullet,((Number)R.call(entity,"getId")).intValue());R.call(event,"setAmount",(float)(amount*multiplier));}
 public static float corpseDamage(Object bullet,int id){return corpseDamage(bullet,id,R.call(bullet,"position"));}
 public static float corpseDamage(Object bullet,int id,Object point){float amount=(float)R.num(R.call(bullet,"getDamage",point));if(!Float.isFinite(amount)||amount<=0)return 0;return (float)(amount*target(bullet,id));}
}
