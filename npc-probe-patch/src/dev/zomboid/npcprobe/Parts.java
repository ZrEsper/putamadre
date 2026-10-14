package dev.zomboid.npcprobe;
import java.util.*;
public final class Parts {
 public static final String[] KEYS={"head","body","leftArm","rightArm","leftLeg","rightLeg","leftFoot","rightFoot"};
 public static final String[] NAMES={"cabeza","torso","brazo_izquierdo","brazo_derecho","pierna_izquierda","pierna_derecha","pie_izquierdo","pie_derecho"};
 public static final double[] DEFAULT={6,12,6,6,8,8,4,4};
 static String max(int i){return "np15_max_"+KEYS[i];}static String hp(int i){return "np15_hp_"+KEYS[i];}
 public static void init(Object npc,Object player){Object tag=Probe.tag(npc);double[] max=DEFAULT.clone();
  try{Object model=R.call(R.type("ichttt.mods.firstaid.common.util.CommonUtils"),"getDamageModel",player);if(model!=null)for(int i=0;i<8;i++){double value=R.num(R.call(R.get(model,KEYS[i]),"getMaxHealth"));if(Double.isFinite(value)&&value>0&&value<=1000)max[i]=value;}}
  catch(RuntimeException e){/* First Aid is optional; use explicit documented defaults. */}
  for(int i=0;i<8;i++){R.call(tag,"putDouble",max(i),max[i]);R.call(tag,"putDouble",hp(i),max[i]);}R.call(tag,"putInt","np15_parts",1);
 }
 public static double[] health(Object npc){double[] h=new double[8];for(int i=0;i<8;i++)h[i]=R.num(R.call(Probe.tag(npc),"getDouble",hp(i)));return h;}
 public static double[] maxima(Object npc){double[] h=new double[8];for(int i=0;i<8;i++)h[i]=R.num(R.call(Probe.tag(npc),"getDouble",max(i)));return h;}
 public static boolean critical(double[] hp){return hp[0]<=0||hp[1]<=0;}
 public static int zone(double height,double side){if(height>=.86)return 0;if(height>=.48)return Math.abs(side)>.18?(side>=0?2:3):1;if(height>=.14)return side>=0?4:5;return side>=0?6:7;}
 public static double[] damaged(double[] hp,int part,double damage){double[] out=hp.clone();if(part>=0&&part<out.length&&Double.isFinite(damage)&&damage>0)out[part]=Math.max(0,out[part]-damage);return out;}
 public static double speed(double[] hp){return (hp[4]<=0&&hp[5]<=0?.25:hp[4]<=0||hp[5]<=0?.6:1)*(hp[6]<=0||hp[7]<=0?.8:1);}
 public static double strength(double[] hp){return hp[2]<=0&&hp[3]<=0?.25:hp[2]<=0||hp[3]<=0?.65:1;}
 static void store(Object npc,double[] h){Object tag=Probe.tag(npc);for(int i=0;i<8;i++)R.call(tag,"putDouble",hp(i),h[i]);}
 public static int aim(Object npc,Object attacker){
  try{Object eye=R.call(attacker,"getEyePosition"),view=R.call(attacker,"getViewVector",1f),end=R.call(eye,"add",R.call(view,"scale",6d));Optional<?> hit=(Optional<?>)R.call(R.call(npc,"getBoundingBox"),"clip",eye,end);if(hit.isPresent())return at(npc,hit.get());}catch(RuntimeException ignored){}
  return 1;
 }
 static int at(Object npc,Object point){double y=R.num(R.get(point,"y"))-R.coord(npc,"Y"),dx=R.num(R.get(point,"x"))-R.coord(npc,"X"),dz=R.num(R.get(point,"z"))-R.coord(npc,"Z"),yaw=Math.toRadians(R.num(R.call(npc,"getYRot")));return zone(y/Math.max(.1,R.num(R.call(npc,"getBbHeight"))),dx*Math.cos(yaw)+dz*Math.sin(yaw));}
 public static int impact(Object npc,Object source){
  Object attacker=R.call(source,"getEntity"),direct=R.call(source,"getDirectEntity");
  String id=String.valueOf(R.call(source,"getMsgId")).toLowerCase(Locale.ROOT);if(id.contains("headshot"))return 0;
  if(direct!=null&&direct!=attacker){Object point=R.call(direct,"position");return at(npc,point);}return attacker==null?1:aim(npc,attacker);
 }
 public static void post(Object event){Object npc=R.call(event,"getEntity");if(!Probe.marked(npc)||R.yes(R.call(R.call(npc,"level"),"isClientSide"))||Probe.down(npc)||R.yes(R.call(Probe.tag(npc),"getBoolean","np15_final")))return;
  double amount=R.num(R.call(event,"getNewDamage"));if(amount<=0)return;int part=impact(npc,R.call(event,"getSource"));apply(npc,part,amount);
 }
 public static void apply(Object npc,int part,double damage){store(npc,damaged(health(npc),part,damage));R.call(Probe.tag(npc),"putInt","np15_last_part",part);if(critical(health(npc)))Probe.collapse(npc);else sync(npc);}
 public static void heal(Object npc,int part,double amount){double[] h=health(npc),max=maxima(npc);h[part]=Math.min(max[part],h[part]+amount);store(npc,h);sync(npc);}
 public static void reset(Object npc){store(npc,maxima(npc));R.call(Probe.tag(npc),"putBoolean","np15_down",false);R.call(Probe.tag(npc),"putBoolean","np15_final",false);R.call(Probe.tag(npc),"remove","np15_helper");sync(npc);}
 public static void revive(Object npc){double[] h=health(npc);h[0]=Math.max(1,h[0]);h[1]=Math.max(1,h[1]);store(npc,h);R.call(Probe.tag(npc),"putBoolean","np15_down",false);R.call(Probe.tag(npc),"remove","np15_helper");sync(npc);}
 public static void sync(Object npc){
  double[] h=health(npc),m=maxima(npc);double sum=0,total=0;for(int i=0;i<8;i++){sum+=h[i];total+=m[i];}
  float life=Probe.down(npc)?1f:(float)Math.max(4,R.num(R.call(npc,"getMaxHealth"))*sum/Math.max(1,total));R.call(npc,"setHealth",life);
  R.call(npc,"setPose",R.get(R.type("net.minecraft.world.entity.Pose"),Probe.down(npc)?"SWIMMING":"STANDING"));
  attribute(npc,"MOVEMENT_SPEED","npcprobe:legs",speed(h)-1);attribute(npc,"ATTACK_DAMAGE","npcprobe:arms",strength(h)-1);attribute(npc,"ATTACK_SPEED","npcprobe:arm_speed",strength(h)-1);
  Probe.name(npc);
 }
 static void attribute(Object npc,String key,String id,double factor){Object attr=R.call(npc,"getAttribute",R.get(R.type("net.minecraft.world.entity.ai.attributes.Attributes"),key));if(attr==null)return;Object resource=R.call(R.type("net.minecraft.resources.ResourceLocation"),"parse",id),old=R.call(attr,"getModifier",resource);if(old!=null&&R.num(R.call(old,"amount"))==factor)return;if(old!=null)R.call(attr,"removeModifier",resource);if(factor!=0)R.call(attr,"addTransientModifier",R.make("net.minecraft.world.entity.ai.attributes.AttributeModifier",resource,factor,R.get(R.type("net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation"),"ADD_MULTIPLIED_TOTAL")));}
 public static String summary(Object npc){double[] h=health(npc),m=maxima(npc);StringBuilder b=new StringBuilder("Partes del NPC: ");for(int i=0;i<8;i++)b.append(NAMES[i]).append(' ').append(String.format(Locale.ROOT,"%.1f/%.1f",h[i],m[i])).append(i==7?"":" | ");return b.toString();}
}
