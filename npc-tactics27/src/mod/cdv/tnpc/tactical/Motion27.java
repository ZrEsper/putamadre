package mod.cdv.tnpc.tactical;
import java.util.*;
public final class Motion27 {
 static final Map<Object,Motion> MOTIONS=new WeakHashMap<>();static final class Motion {long slide,end,hit,service;int action;}
 static Object c(Object o,String m,Object...a)throws Exception{return Reflect.call(o,m,a);}
 public static void packet(Object packet){try{int key=((Number)c(packet,"animation")).intValue();if(key<4||key>9)return;Object world=Reflect.field(c(Reflect.type("net.minecraft.client.Minecraft"),"getInstance"),"level");if(world==null)return;Object n=c(world,"getEntity",((Number)c(packet,"id")).intValue());if(n==null)return;Motion m=MOTIONS.computeIfAbsent(n,k->new Motion());long now=System.nanoTime();if(key==4){m.slide=now;m.end=now+650000000L;}else if(key==5)m.end=Math.min(m.end,now+150000000L);else if(key==6)m.hit=now;else{m.action=key;m.service=now;}}catch(Exception e){Client26.warn("motion27 packet",e);}}
 static double weight(long now,long start,long end){if(start==0||now>=end)return 0;return Math.min(Tactical27.smooth((now-start)/150000000d),Tactical27.smooth((end-now)/150000000d));}
 static void blend(Object part,String field,float target,double amount)throws Exception{float old=((Number)Reflect.field(part,field)).floatValue();TeamBrain.set(part,field,(float)(old+(target-old)*amount));}
 public static void model(Object model,Object npc){try{Motion m=MOTIONS.get(npc);if(m==null)return;long now=System.nanoTime();double slide=weight(now,m.slide,m.end),hit=m.hit==0?0:Math.max(0,1-(now-m.hit)/350000000d),service=weight(now,m.service,m.service+(m.action==9?3000000000L:2000000000L));Object body=Reflect.field(model,"body"),head=Reflect.field(model,"head"),left=Reflect.field(model,"leftLeg"),right=Reflect.field(model,"rightLeg");if(slide>0){blend(body,"xRot",-.35f,slide);blend(body,"zRot",-.20f,slide);blend(head,"xRot",.12f,slide);blend(left,"xRot",-1.18f,slide);blend(left,"zRot",-.14f,slide);blend(right,"xRot",.48f,slide);blend(right,"zRot",.18f,slide);}
 if(hit>0){float pulse=(float)(Math.sin(hit*Math.PI)*.17);TeamBrain.set(body,"xRot",((Number)Reflect.field(body,"xRot")).floatValue()+pulse);TeamBrain.set(body,"zRot",((Number)Reflect.field(body,"zRot")).floatValue()+pulse*((Social.uuid(npc).getLeastSignificantBits()&1)==0?1:-1));}
 if(service>0){Object arm=Reflect.field(model,"leftArm");blend(arm,"xRot",m.action==9?-1.0f:-1.65f,service);blend(arm,"yRot",.35f,service);blend(arm,"zRot",-.15f,service);}
 for(String[]p:new String[][]{{"jacket","body"},{"hat","head"},{"leftPants","leftLeg"},{"rightPants","rightLeg"},{"leftSleeve","leftArm"},{"rightSleeve","rightArm"}})c(Reflect.field(model,p[0]),"copyFrom",Reflect.field(model,p[1]));
 if(slide==0&&hit==0&&service==0)MOTIONS.remove(npc);
 }catch(Exception e){Client26.warn("motion27 model",e);}}
}
