package dev.zomboid.gore;
import java.util.*;
/** Ballistic tissue loss follows damage received, not projectile count. */
public final class Wounds18 {
 static final double[]THRESHOLD={0,0,4,12,28,44,64,92};
 static final Map<Object,double[]>CORPSES=Collections.synchronizedMap(new WeakHashMap<>());
 public static double scale(int region){return region==0?.55:region>=2&&region<6?.75:1;}
 public static int stage(double damage,int region){if(!Double.isFinite(damage)||damage<=0)return 0;int tier=1;for(int i=2;i<=7;i++)if(damage>=THRESHOLD[i]*scale(region))tier=i;return tier;}
 public static Damage13.Result result(int state,int details,int surfaces,int region,double accumulated){int tier=Math.max(Trauma12.stage(details,region),stage(accumulated,region));return new Damage13.Result(state|1<<(8+region),Trauma12.withStage(details,region,tier),surfaces|1<<(8+region));}
 public static void route(Object n,Gore.Shot shot,float damage,int kind){if(shot.bullet()==null){R.call(R.type("dev.zomboid.gore.Trauma13"),"injureMelee18",n,shot,damage,kind);return;}injure(n,shot,damage);}
 public static void injure(Object n,Gore.Shot shot,float damage){if(!Float.isFinite(damage)||damage<=0)return;int region=Ballistics11.zone(shot);if(region==1)region=0;Object tag=Gore.tag(n);String key="zg18_damage_"+region;double accumulated=Math.max(THRESHOLD[Trauma12.stage(Trauma12.details(n),region)]*scale(region),R.num(R.call(tag,"getDouble",key)))+damage;R.call(tag,"putDouble",key,accumulated);
 int hits=((Number)R.call(tag,"getInt","zg13_hits_"+region)).intValue()+1;R.call(tag,"putInt","zg13_hits_"+region,hits);
 var raw=result(Gore.state(n),Trauma12.details(n),Gore.surfaces(n),region,accumulated);long time=((Number)R.call(R.call(n,"level"),"getGameTime")).longValue();int limited=Severity23.limit(n,region,Trauma12.stage(Trauma12.details(n),region),Trauma12.stage(raw.details(),region),time);var result=new Damage13.Result(raw.state(),Trauma12.withStage(raw.details(),region,limited),raw.surfaces());Trauma12.set(n,result.details());Gore.surfaceSet(n,result.surfaces());Gore.set(n,result.state());
 int serial=((Number)R.call(tag,"getInt","zg13_serial")).intValue()+1;if(serial>10000000)serial=1;R.call(tag,"putInt","zg13_serial",serial);R.call(R.call(n,"getEntityData"),"set",Trauma13.impactAccessor,(serial<<4)|region);
 long now=((Number)R.call(R.call(n,"level"),"getGameTime")).longValue();R.call(tag,"putLong","zg13_bleedUntil",now+(stage(accumulated,region)>=3?180L:40L));Gore.push(n,shot.bullet(),shot.gun());if(stage(accumulated,region)>=2)Artery14.injure(n,shot,damage,0);
 }
 public static Damage13.Result corpse(int state,int details,int surfaces,int region,int hits,int kind,double location,Object packet,Object player,float damage){if(player!=null)return Layers15.hit(state,details,surfaces,region,hits,kind,location);if(!Float.isFinite(damage)||damage<=0)return new Damage13.Result(state,details,surfaces);double[]values=CORPSES.computeIfAbsent(packet,k->new double[8]);values[region]=Math.max(values[region],THRESHOLD[Trauma12.stage(details,region)]*scale(region))+damage;var raw=result(state,details,surfaces,region,values[region]);long now;try{now=((Number)R.call(R.get(R.call(R.type("net.minecraft.client.Minecraft"),"getInstance"),"level"),"getGameTime")).longValue();}catch(RuntimeException server){now=System.nanoTime()/50_000_000L;}int tier=Severity23.limit(packet,region,Trauma12.stage(details,region),Trauma12.stage(raw.details(),region),now);return new Damage13.Result(raw.state(),Trauma12.withStage(raw.details(),region,tier),raw.surfaces());}
}
