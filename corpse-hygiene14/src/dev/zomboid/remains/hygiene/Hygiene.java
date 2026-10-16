package dev.zomboid.remains.hygiene;
import dev.zomboid.remains.delivery.Reflect;import java.util.*;
public final class Hygiene{
 static final Map<Object,PlayerState> PLAYERS=new WeakHashMap<>();static boolean corpseChecked;static Class<?> corpseType;
 static final class PlayerState{Object world;Exposure exposure=new Exposure();boolean ownsNausea;}
 record Body(Object key,Object pos,double x,double y,double z,long age){}
 static Object c(Object o,String n,Object...a){return Reflect.call(o,n,a);}static Object g(Object o,String n){return Reflect.get(o,n);}static Class<?> t(String n){return Reflect.type(n);}static double d(Object v){return ((Number)v).doubleValue();}static boolean yes(Object v){return Boolean.TRUE.equals(v);}
 static Object blockPos(int x,int y,int z){return Reflect.make("net.minecraft.core.BlockPos",x,y,z);}static String blockId(Object w,Object p){return c(g(t("net.minecraft.core.registries.BuiltInRegistries"),"BLOCK"),"getKey",c(c(w,"getBlockState",p),"getBlock")).toString();}
 static List<Body> nearby(Object player,Object world,long now){List<Body> bodies=new ArrayList<>();double x=d(c(player,"getX")),y=d(c(player,"getY")),z=d(c(player,"getZ"));
  if(!corpseChecked){corpseChecked=true;try{corpseType=Class.forName("de.maxhenkel.corpse.entities.CorpseEntity");}catch(ClassNotFoundException ignored){}}
  if(corpseType!=null){Object box=c(c(player,"getBoundingBox"),"inflate",6.);for(Object entity:(List<?>)c(world,"getEntitiesOfClass",corpseType,box)){double ex=d(c(entity,"getX")),ey=d(c(entity,"getY")),ez=d(c(entity,"getZ"));if((ex-x)*(ex-x)+(ey-y)*(ey-y)+(ez-z)*(ez-z)>36||yes(c(entity,"isRemoved")))continue;Object tag=c(entity,"getPersistentData");if(!yes(c(tag,"contains","ZHygieneBorn")))c(tag,"putLong","ZHygieneBorn",now-Math.max(0,((Number)g(entity,"tickCount")).intValue()));long age=Math.max(0,now-((Number)c(tag,"getLong","ZHygieneBorn")).longValue());bodies.add(new Body(entity,c(entity,"blockPosition"),ex,ey+.25,ez,age));}}
  Object data=c(t("dev.zomboid.remains.ZombieRemainsLoot"),"data",world);Map<?,?> blocks=(Map<?,?>)g(data,"corpses");int bx=(int)Math.floor(x),by=(int)Math.floor(y),bz=(int)Math.floor(z);
  if(!blocks.isEmpty())for(int dx=-6;dx<=6;dx++)for(int dy=-6;dy<=6;dy++)for(int dz=-6;dz<=6;dz++){int px=bx+dx,py=by+dy,pz=bz+dz;double ax=px+.5-x,ay=py+.25-y,az=pz+.5-z;if(ax*ax+ay*ay+az*az>36)continue;long packed=((long)px&0x3ffffffL)<<38|((long)pz&0x3ffffffL)<<12|((long)py&0xfffL);Object corpse=blocks.get(packed);if(corpse==null)continue;Object pos=blockPos(px,py,pz);if(!yes(c(world,"hasChunkAt",pos))||!g(corpse,"block").toString().equals(blockId(world,pos)))continue;long born=((Number)g(corpse,"born")).longValue();if(born<0){Reflect.set(corpse,"born",now);c(data,"setDirty");born=now;}bodies.add(new Body(pos,pos,px+.5,py+.25,pz+.5,Math.max(0,now-born)));}
  return bodies;
 }
 static void nausea(Object player,PlayerState state,boolean active){Object effect=g(t("net.minecraft.world.effect.MobEffects"),"CONFUSION");Object current=c(player,"getEffect",effect);boolean manageable=current==null||((Number)c(current,"getAmplifier")).intValue()==0&&((Number)c(current,"getDuration")).intValue()<=120;
  if(active&&manageable){Object potion=Reflect.make("net.minecraft.world.effect.MobEffectInstance",effect,100,0,false,false,true);c(player,"addEffect",potion);state.ownsNausea=true;}else if(!active&&state.ownsNausea){if(current!=null&&manageable)c(player,"removeEffect",effect);state.ownsNausea=false;}
 }
 static void flies(Object world,Body b){if(HygieneContent.FLY!=null)c(world,"sendParticles",HygieneContent.FLY,b.x,b.y,b.z,2,.28,.16,.28,.01);}
 public static void vomit(Object player){Object world=c(player,"level"),food=c(player,"getFoodData");int amount=Math.max(0,((Number)c(food,"getFoodLevel")).intValue()-4);c(food,"setFoodLevel",amount);c(food,"setSaturation",Math.min(amount,((Number)c(food,"getSaturationLevel")).floatValue()));Hydration.drain(player);
  Object pos=c(player,"blockPosition");if(HygieneContent.SOUND!=null)c(world,"playSound",null,pos,HygieneContent.SOUND,g(t("net.minecraft.sounds.SoundSource"),"PLAYERS"),1f,1f);
  Object state=c(HygieneContent.VOMIT,"defaultBlockState"),option=Reflect.make("net.minecraft.core.particles.BlockParticleOption",g(t("net.minecraft.core.particles.ParticleTypes"),"BLOCK"),state);double x=d(c(player,"getX")),y=d(c(player,"getEyeY"))-.2,z=d(c(player,"getZ"));c(world,"sendParticles",option,x,y,z,16,.16,.08,.16,.08);
  Object look=c(player,"getLookAngle");int bx=(int)Math.floor(x+d(g(look,"x"))*.8),by=(int)Math.floor(d(c(player,"getY"))),bz=(int)Math.floor(z+d(g(look,"z"))*.8);
  for(int[] offset:new int[][]{{0,0},{1,0},{-1,0},{0,1},{0,-1}})for(int dy=0;dy>=-2;dy--){Object target=blockPos(bx+offset[0],by+dy,bz+offset[1]);Object below=c(target,"below");if(!yes(c(world,"hasChunkAt",target))||!yes(c(world,"hasChunkAt",below)))continue;Object existing=c(world,"getBlockState",target);if(!yes(c(existing,"isAir"))||!yes(c(c(world,"getBlockState",below),"isFaceSturdy",world,below,g(t("net.minecraft.core.Direction"),"UP"))))continue;c(world,"setBlock",target,state,3);return;}
 }
 public static void server(Object event){Object server=c(event,"getServer");if(((Number)c(server,"getTickCount")).intValue()%20!=0)return;
  for(Object world:(Iterable<?>)c(server,"getAllLevels")){long now=((Number)c(world,"getGameTime")).longValue();Set<Object> rendered=new HashSet<>();int flyBudget=24;
   for(Object player:(List<?>)c(world,"players")){PlayerState state=PLAYERS.computeIfAbsent(player,p->new PlayerState());if(state.world!=world){nausea(player,state,false);state.exposure=new Exposure();state.world=world;}if(!yes(c(player,"isAlive"))||yes(c(player,"isCreative"))||yes(c(player,"isSpectator"))){nausea(player,state,false);state.exposure=new Exposure();continue;}
    List<Body> bodies=nearby(player,world,now);for(Body b:bodies)if(b.age>=1200&&flyBudget>0&&rendered.add(b.key)){flies(world,b);flyBudget--;}
    Exposure.Result result=state.exposure.step(now,bodies.size());nausea(player,state,result.nausea());if(result.vomit())vomit(player);
   }
  }
 }
}
