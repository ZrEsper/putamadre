package dev.zomboid.reactive;
import java.util.*;
public final class Reactive {
 static final String I="dev.zomboid.hordes.ZombieIndividual";
 static final Map<Object,Traits> traitCache=new WeakHashMap<>();
 static final Map<Object,Step> steps=new WeakHashMap<>();
 static final Map<Object,Gate> noiseNext=new WeakHashMap<>();
 static boolean warned;
 static void error(Throwable e){if(!warned){warned=true;System.err.println("[Zomboid reactive AI] "+e);e.printStackTrace();}}
 static Object level(Object e){return R.call(e,"level");}static Object tag(Object e){return R.call(e,"getPersistentData");}
 static long time(Object e){return R.lng(R.call(level(e),"getGameTime"));}
 static boolean yes(Object e,String m){return R.yes(R.call(e,m));}
 static long lng(Object t,String key){return R.lng(R.call(t,"getLong",key));}
 static int kind(Object t){return ((Number)R.call(t,"getInt","zi_type")).intValue();}
 public static boolean controlled(Object mob){try{return R.yes(R.call(R.type(I),"supported",mob));}catch(Throwable e){error(e);return false;}}
 static boolean player(Object e){return R.type("net.minecraft.world.entity.player.Player").isInstance(e);}
 static boolean eligible(Object e){return e!=null&&yes(e,"isAlive")&&!yes(e,"isCreative")&&!yes(e,"isSpectator");}
 static double distance(Object a,Object b){return Math.sqrt(Math.pow(R.coord(a,"X")-R.coord(b,"X"),2)+Math.pow(R.coord(a,"Y")-R.coord(b,"Y"),2)+Math.pow(R.coord(a,"Z")-R.coord(b,"Z"),2));}
 static boolean trait(Object p,String id){
  long now=time(p);Traits cached=traitCache.get(p);if(cached!=null&&cached.level==level(p)&&now<cached.until)return cached.ids.contains(id);
  Set<String> result=new HashSet<>();
  try {Object d=R.call(p,"getData",R.get(R.type("dev.survivorcreator.SurvivorCreator"),"CHARACTER"));
   for(Object perk:(Iterable<?>)R.call(d,"perks"))result.add(String.valueOf(perk).replace("survivorcreator:",""));
   Object profession=((Optional<?>)R.call(d,"profession")).orElse(null);
   if(profession!=null){Object prof=R.call(R.type("dev.survivorcreator.data.DataRegistry"),"profession",profession);if(prof!=null)for(Object perk:(Iterable<?>)R.call(prof,"grantedPerks"))result.add(String.valueOf(perk).replace("survivorcreator:",""));}
  }catch(IllegalStateException e){Throwable cause=e;while(cause.getCause()!=null)cause=cause.getCause();if(!(cause instanceof ClassNotFoundException))error(e);}
  traitCache.put(p,new Traits(level(p),now+20,result));return result.contains(id);
 }
 static boolean slippery(Object p){return trait(p,"elusive")||trait(p,"inconspicuous")||trait(p,"lightfooted");}
 public static void profile(Object mob,Object t){
  if(((Number)R.call(t,"getInt","zpr_version")).intValue()>=1)return;
  Object random=R.call(mob,"getRandom");int k=Rules.kind(R.num(R.call(random,"nextDouble")));
  R.call(t,"putInt","zi_type",k);R.call(t,"putDouble","zi_hearing",Rules.hearing(k,R.num(R.call(random,"nextDouble"))));
  R.call(t,"putInt","zi_delay",k==0?6+((Number)R.call(random,"nextInt",10)).intValue():k==1?2+((Number)R.call(random,"nextInt",5)).intValue():((Number)R.call(random,"nextInt",4)).intValue());
  R.call(t,"putInt","zi_pace",Rules.pace(R.num(R.call(random,"nextDouble"))));R.call(t,"putInt","zpr_version",1);
  for(String key:List.of("zi_seen","zi_seen_until","zi_pending","zi_anger_until","za_heard_until"))R.call(t,"remove",key);
 }
 static Object profile(Object mob){return R.call(R.type(I),"profile",mob);}
 static void hint(Object mob,Object pos,Object source,long now,int memory){Object t=profile(mob);
  R.call(t,"putLong","zh_pos",R.call(pos,"asLong"));R.call(t,"putLong","zh_until",now+memory);
  R.call(t,"putLong","zpr_heard_at",now);
  if(source!=null)R.call(t,"putUUID","zh_player",R.call(source,"getUUID"));else R.call(t,"remove","zh_player");
 }
 public static boolean sees(Object mob,Object p){
  try {
   if(!eligible(p)||level(mob)!=level(p)||!yes(mob,"isAlive"))return false;
   Object t=profile(mob);long now=time(mob);double d=distance(mob,p);
   if(!R.yes(R.call(mob,"hasLineOfSight",p)))return false;
   int k=kind(t);double max=Rules.vision(k,yes(p,"isCrouching"),slippery(p));
   boolean tracking=R.yes(R.call(t,"hasUUID","zpr_seen_player"))&&R.call(t,"getUUID","zpr_seen_player").equals(R.call(p,"getUUID"))&&now-lng(t,"zpr_seen_at")<=20;
   if(d>max*(tracking?1.25:1))return false;
   double dx=R.coord(p,"X")-R.coord(mob,"X"),dz=R.coord(p,"Z")-R.coord(mob,"Z"),angle=Math.toRadians(R.num(R.call(mob,"getYHeadRot")));
   double cosine=d<=.01?1:(-Math.sin(angle)*dx+Math.cos(angle)*dz)/Math.max(.01,Math.hypot(dx,dz));
   if(d>2&&!tracking&&cosine<Rules.viewCos(k))return false;
   if(d>2&&!tracking){
    if(!R.yes(R.call(t,"hasUUID","zi_pending"))||!R.call(t,"getUUID","zi_pending").equals(R.call(p,"getUUID"))||now-lng(t,"zi_pending_last")>8){R.call(t,"putUUID","zi_pending",R.call(p,"getUUID"));R.call(t,"putLong","zi_notice_at",now+((Number)R.call(t,"getInt","zi_delay")).intValue());}
    R.call(t,"putLong","zi_pending_last",now);if(lng(t,"zi_notice_at")>now)return false;
   }
   R.call(t,"putUUID","zpr_seen_player",R.call(p,"getUUID"));R.call(t,"putLong","zpr_seen_at",now);
   R.call(t,"putLong","zh_pos",R.call(R.call(p,"blockPosition"),"asLong"));R.call(t,"putLong","zh_until",now+Rules.memory(k));R.call(t,"putUUID","zh_player",R.call(p,"getUUID"));return true;
  }catch(Throwable e){error(e);return false;}
 }
 public static boolean close(Object mob,Object p){return distance(mob,p)<=2&&sees(mob,p);}
 public static boolean hear(Object mob,Object pos,Object p,double radius,long until,boolean heli){
  try {if(!Double.isFinite(radius)||radius<=0)return false;if(p!=null&&(!eligible(p)||level(mob)!=level(p)))return false;
   Object t=profile(mob);double hearing=R.num(R.call(t,"getDouble","zi_hearing"));
   boolean blocked=p!=null&&!R.yes(R.call(mob,"hasLineOfSight",p));double range=Rules.hearingRange(radius,hearing,blocked);
   if(distance(mob,pos)>range)return false;
   long now=time(mob);if(lng(t,"zpr_hearing_next")>now&&radius<=R.num(R.call(t,"getDouble","zpr_radius")))return false;
   R.call(t,"putLong","zpr_hearing_next",now+(kind(t)==0?8:kind(t)==1?4:2));R.call(t,"putDouble","zpr_radius",radius);
   hint(mob,pos,p,now,Math.min(Rules.memory(kind(t)),Math.max(20,(int)(until-now))));
   R.call(t,"putBoolean","zh_heli",heli);Object look=R.call(mob,"getLookControl");R.call(look,"setLookAt",R.coord(pos,"X")+.5,R.coord(pos,"Y")+.5,R.coord(pos,"Z")+.5);
   return true;
  }catch(Throwable e){error(e);return false;}
 }
 public static double radius(String id,double original,boolean quiet){
  String s=id.toLowerCase(Locale.ROOT);if(s.contains("step")||s.contains("walk")||s.contains("rustle"))return 0; // footsteps have their own authoritative movement sampler
  if(s.contains("sneez")||s.contains("cough"))return Math.max(8,original);
  return R.num(R.call(R.type("dev.zomboid.hordes.NoiseRules"),"radius",id,original,quiet));
 }
 public static void playerTick(Object p){
  if(!player(p)||yes(level(p),"isClientSide"))return;
  long now=time(p);double x=R.coord(p,"X"),z=R.coord(p,"Z");Step old=steps.get(p);
  double moved=old==null||old.level!=level(p)?0:Math.hypot(x-old.x,z-old.z);
  Step step=new Step(level(p),x,z,old==null?0:old.next,old==null?0:old.noiseTime,old==null?null:old.pos,old==null?0:old.radius);steps.put(p,step);
  if(!eligible(p)||moved<=.003||moved>4||yes(p,"isPassenger")||R.yes(R.get(R.call(p,"getAbilities"),"flying")))return;
  if(!yes(p,"onGround")&&!yes(p,"isInWater"))return;
  if(now<step.next)return;
  boolean run=yes(p,"isSprinting"),crouch=yes(p,"isCrouching"),water=yes(p,"isInWater");step.next=now+(run?6:crouch?14:12);step.noiseTime=now;step.pos=R.call(p,"blockPosition");step.radius=Rules.steps(run,crouch,water,slippery(p),trait(p,"clumsy"));
 }
 public static void tick(Object event){
  try {Object mob=R.call(event,"getEntity");if(!controlled(mob)||yes(level(mob),"isClientSide")||!yes(mob,"isAlive"))return;
   long now=time(mob);if(Math.floorMod(now+((Number)R.call(mob,"getId")).intValue(),4)!=0)return;
   Object t=profile(mob);if(lng(t,"zc_stun")>now||lng(t,"zpush_until")>now)return;
   Object current=R.call(mob,"getTarget"),best=null;double bestDistance=Double.MAX_VALUE;
   double mobX=R.coord(mob,"X"),mobZ=R.coord(mob,"Z");Object mobLevel=level(mob);
   for(Object p:(Iterable<?>)R.call(mobLevel,"players")){
    Step nearby=steps.get(p);if(nearby!=null&&nearby.level==mobLevel&&(Math.abs(nearby.x-mobX)>40||Math.abs(nearby.z-mobZ)>40))continue;
    if(!eligible(p))continue;double d=distance(mob,p);if(d>40)continue;
    String noiseKey="zpr_noise_"+R.call(p,"getUUID");Step step=steps.get(p);if(step!=null&&step.level==level(mob)&&step.pos!=null&&now-step.noiseTime<=8&&lng(t,noiseKey)<step.noiseTime){if(hear(mob,step.pos,p,step.radius,now+Rules.memory(kind(t)),false))R.call(t,"putLong",noiseKey,step.noiseTime);}
    if(d<bestDistance&&sees(mob,p)){best=p;bestDistance=d;}
   }
   if(current!=null&&player(current)&&!sees(mob,current)){
    R.call(mob,"setTarget",(Object)null);R.call(R.call(mob,"getNavigation"),"stop");current=null;
   }
   if(best!=null&&(current==null||player(current)&&distance(mob,current)>bestDistance+2)){R.call(mob,"setTarget",best);R.call(R.call(mob,"getLookControl"),"setLookAt",best,30f,30f);}
   else if(R.call(mob,"getTarget")==null&&lng(t,"zh_until")>now){Object pos=R.call(R.type("net.minecraft.core.BlockPos"),"of",lng(t,"zh_pos"));
    // InvestigateNoiseGoal owns navigation; this only points at the remembered location.
    R.call(R.call(mob,"getLookControl"),"setLookAt",R.coord(pos,"X")+.5,R.coord(pos,"Y")+.5,R.coord(pos,"Z")+.5);
   }
  }catch(Throwable e){error(e);}
 }
 public static void sound(Object sound,Object level,double x,double y,double z,float volume){
  try {if(sound!=null)named(String.valueOf(R.call(sound,"getLocation")),level,x,y,z,volume);}catch(Throwable e){error(e);}
 }
 public static void named(String id,Object level,double x,double y,double z,float volume){emit(level,x,y,z,Rules.ambient(id,volume));}
 public static void block(Object p,Object level,Object pos){
  try {if(p!=null&&!eligible(p))return;emit(level,R.coord(pos,"X"),R.coord(pos,"Y"),R.coord(pos,"Z"),18);}catch(Throwable e){error(e);}
 }
 public static void levelEvent(int type,Object level,double x,double y,double z){
  if(type==2001)emit(level,x,y,z,18);
  else if(type==1010)emit(level,x,y,z,32);
  else if(java.util.Set.of(1037,1007,1036,1013,1011,1012,1005,1006,1008,1014).contains(type))emit(level,x,y,z,12);
 }
 static void emit(Object level,double x,double y,double z,double radius){
  try {if(yes(level,"isClientSide")||radius<=0)return;
   long now=R.lng(R.call(level,"getGameTime"));Gate gate=noiseNext.get(level);if(gate!=null&&gate.until>now&&gate.radius>=radius)return;noiseNext.put(level,new Gate(now+2,radius));
   Object pos=R.call(R.type("net.minecraft.core.BlockPos"),"containing",x,y,z),box=R.make("net.minecraft.world.phys.AABB",x-radius,y-radius,z-radius,x+radius,y+radius,z+radius);
   for(Object mob:(Iterable<?>)R.call(level,"getEntitiesOfClass",R.type("net.minecraft.world.entity.Mob"),box))if(controlled(mob)&&yes(mob,"isAlive"))hear(mob,pos,null,radius,now+180,false);
  }catch(Throwable e){error(e);}
 }
 public static double speed(int pace,Object mob){
  if(R.type("net.minecraft.world.entity.monster.Zombie").isInstance(mob))return Rules.speed(pace);
  Object attribute=R.call(mob,"getAttribute",R.get(R.type("net.minecraft.world.entity.ai.attributes.Attributes"),"MOVEMENT_SPEED"));
  return attribute==null?Rules.speed(pace):R.num(R.call(attribute,"getBaseValue"))*(pace==3?1.25:pace==2?.8:pace==1?.9:1.05);
 }
 public static float cap(float speed,int pace,boolean night,Object mob){return R.type("net.minecraft.world.entity.monster.Zombie").isInstance(mob)?Math.min(speed,(float)(Rules.speed(pace)*(night?1:.975))):speed;}
 public static void jump(Object p){
  try {if(!player(p)||yes(level(p),"isClientSide")||!eligible(p)||yes(p,"isInWater")||yes(p,"isPassenger")||R.yes(R.get(R.call(p,"getAbilities"),"flying")))return;
   R.call(tag(p),"putLong","zpr_jump",time(p));
  }catch(Throwable e){error(e);}
 }
 static void message(Object p,String text){R.call(p,"displayClientMessage",R.call(R.type("net.minecraft.network.chat.Component"),"literal",text),true);}
 public static boolean allowAttack(Object mob,Object p){
  try {if(!player(p)||yes(level(mob),"isClientSide"))return true;
   Object t=profile(mob);long now=time(mob);if(lng(t,"zc_stun")>now||lng(t,"zpush_until")>now){R.call(t,"remove","zpr_bite_ready");return false;}
   if(!eligible(p)||distance(mob,p)>2.4||!R.yes(R.call(mob,"hasLineOfSight",p))){R.call(t,"remove","zpr_bite_ready");return false;}
   if(lng(t,"zpr_attack_next")>now)return false;
   Object pt=tag(p);long ready=lng(t,"zpr_bite_ready");Object id=R.call(p,"getUUID");
   if(ready==0||!R.yes(R.call(t,"hasUUID","zpr_bite_target"))||!id.equals(R.call(t,"getUUID","zpr_bite_target"))){
    R.call(t,"putUUID","zpr_bite_target",id);R.call(t,"putLong","zpr_bite_ready",now+Rules.windup(kind(t)));
    if(lng(pt,"zpr_prompt_until")<=now){R.call(pt,"putLong","zpr_prompt_until",now+8);message(p,"¡Mordida! Salta justo antes del golpe (ESPACIO)");}return false;
   }
   if(now<ready)return false;
   R.call(t,"remove","zpr_bite_ready");R.call(t,"putLong","zpr_attack_next",now+Rules.cooldown(kind(t)));
   if(R.yes(R.call(pt,"contains","zpr_jump"))&&Rules.dodge(lng(pt,"zpr_jump"),now,lng(pt,"zpr_dodge_next"))){
    R.call(pt,"putLong","zpr_dodge_next",now+40);R.call(pt,"remove","zpr_jump");R.call(t,"putLong","zc_stun",now+8);message(p,"Mordida esquivada");return false;
   }
   return true;
  }catch(Throwable e){error(e);return true;}
 }
 static record Traits(Object level,long until,Set<String> ids){}
 static record Gate(long until,double radius){}
 static final class Step {final Object level;final double x,z;long next,noiseTime;Object pos;double radius;Step(Object l,double x,double z,long n,long t,Object p,double r){level=l;this.x=x;this.z=z;next=n;noiseTime=t;pos=p;radius=r;}}
}
