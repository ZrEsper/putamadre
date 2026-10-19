package dev.zomboid.firstaid;
import java.util.*;
/** Bounded, server-authored local surface marks carried by First Aid's existing NBT sync. */
public final class Wounds45 {
 public static final int LIMIT=24, STRIDE=5, DURATION=12000;
 static final ThreadLocal<Object> POINT=new ThreadLocal<>();
 public static void point(Object p){if(p==null)POINT.remove();else POINT.set(p);}
 static Object c(Object o,String n,Object...a){return R.call(o,n,a);}
 static int number(Object o,String n){return ((Number)R.get(o,n)).intValue();}
 static boolean flag(Object o,String n){return R.yes(R.get(o,n));}
 public static Object[] wounds(Object model){return (Object[])R.get(c(R.type("ichttt.mods.firstaid.survival.ClinicalData"),"state",model),"wounds");}
 public static int[] sanitize(int[] a){if(a==null||a.length>LIMIT*STRIDE||a.length%STRIDE!=0)return new int[0];for(int i=0;i<a.length;i+=STRIDE)if(a[i]<0||a[i]>255||a[i+1]<0||a[i+1]>255||a[i+2]<0||a[i+2]>255||a[i+3]<1||a[i+3]>3||a[i+4]<0||a[i+4]>DURATION)return new int[0];return a;}
 public static int[] add(int[] old,double x,double y,double z,int kind){old=sanitize(old);int keep=Math.min(old.length,(LIMIT-1)*STRIDE),a[]=Arrays.copyOfRange(old,old.length-keep,old.length+STRIDE);int i=keep;a[i]=q(x);a[i+1]=q(y);a[i+2]=q(z);a[i+3]=kind;a[i+4]=DURATION;return a;}
 static int q(double x){return (int)Math.round(Math.max(0,Math.min(1,Double.isFinite(x)?x:.5))*255);}
 public static int[] marks(Object wound){return sanitize((int[])R.get(wound,"marks45"));}
 public static boolean treated(Object w){return flag(w,"bandaged")&&!flag(w,"dirty")&&!flag(w,"infected")&&!flag(w,"inflamed44")&&(number(w,"bullets")==0||number(w,"lodgedOutcome")==1);}
 public static void tick(Object w){int[] old=marks(w);if(old.length==0)return;if(flag(w,"recovered")&&!flag(w,"gunshot")&&!flag(w,"cut")&&!flag(w,"bitten")&&number(w,"bullets")==0){R.set(w,"marks45",new int[0]);return;}if(!treated(w))return;int target=(int)Math.ceil(DURATION*Math.min(1,Math.max(0,number(w,"healingLeft"))/(double)Math.max(1,number(w,"markHealingStart45"))));int[] a=old.clone();for(int i=0;i<a.length;i+=STRIDE)a[i+4]=Math.max(1,Math.min(a[i+4],target));R.set(w,"marks45",a);}

 public static void write(Object model,Object tag){Object root=c(tag,"getCompound","zomboidClinical");Object[] w=wounds(model);for(int i=0;i<8;i++){Object part=c(root,"getCompound","part"+i);c(part,"putIntArray","marks45",marks(w[i]));c(part,"putInt","markHealingStart45",number(w[i],"markHealingStart45"));}}
 public static void read(Object model,Object tag){Object root=c(tag,"getCompound","zomboidClinical");Object[] w=wounds(model);for(int i=0;i<8;i++){Object p=c(root,"getCompound","part"+i);R.set(w[i],"marks45",sanitize((int[])c(p,"getIntArray","marks45")));R.set(w[i],"markHealingStart45",Math.max(1,Math.min(DURATION,((Number)c(p,"getInt","markHealingStart45")).intValue())));}}
 public static Object model(Object p){return c(R.type("ichttt.mods.firstaid.common.util.CommonUtils"),"getDamageModel",p);}
 static double n(Object o,String s){return ((Number)c(o,s)).doubleValue();}
 public static double[] local(Object player,Object point,int part){
  try{Object enumPart=R.type("ichttt.mods.firstaid.api.enums.EnumPlayerPart").getEnumConstants()[part],slot=c(R.type("ru.ranazy.tacz_firstaid_compat.compat.firstaid.BodypartHitbox$BodypartSlot"),"fromPart",enumPart),box=c(R.type("ru.ranazy.tacz_firstaid_compat.compat.firstaid.BodypartHitbox"),"getHitbox",slot),v=Coordinates45.local(point,player);double[] result=new double[3];String[]fields={"x","y","z"},ends={"X","Y","Z"};for(int j=0;j<3;j++){double min=((Number)R.get(box,"min"+ends[j])).doubleValue(),max=((Number)R.get(box,"max"+ends[j])).doubleValue();result[j]=Math.max(0,Math.min(1,(((Number)R.get(v,fields[j])).doubleValue()-min)/(max-min)));}result[1]=1-result[1];result[2]=1-result[2];return result;}catch(IllegalStateException ex){if(!(ex.getCause()instanceof ClassNotFoundException))throw ex;}
  double dx=n(point,"x")-n(player,"getX"),dy=n(point,"y")-n(player,"getY"),dz=n(point,"z")-n(player,"getZ");
  double yaw=Math.toRadians(((Number)R.get(player,part==0?"yHeadRot":"yBodyRot")).doubleValue()),co=Math.cos(yaw),si=Math.sin(yaw);
  double x=(dx*co+dz*si)*16,y=dy*16,z=(dx*si-dz*co)*16;
  double x0=-4,x1=4,y0=12,y1=24,z0=-2,z1=2;
  switch(part){case 0:y0=24;y1=32;z0=-4;z1=4;break;case 1:x0=4;x1=8;y0=12;y1=24;break;case 5:x0=-8;x1=-4;y0=12;y1=24;break;case 2:case 3:x0=0;x1=4;y0=part==3?0:3;y1=part==3?3:12;break;case 6:case 7:x0=-4;x1=0;y0=part==7?0:3;y1=part==7?3:12;break;}
  return new double[]{Math.max(0,Math.min(1,(x-x0)/(x1-x0))),Math.max(0,Math.min(1,(y1-y)/(y1-y0))),Math.max(0,Math.min(1,(z-z0)/(z1-z0)))};
 }
 static Object impact(Object source){if(POINT.get()!=null)return POINT.get();Object direct=c(source,"getDirectEntity");if(direct==null)return null;return c(direct,"position");}
 public static void bullet(Object model,Object player,Object source,Object part){if(part==null||!R.type("net.minecraft.server.level.ServerPlayer").isInstance(player))return;int i=((Enum<?>)part).ordinal();Object w=wounds(model)[i];Object p=impact(source);double[] xyz=p==null?new double[]{.5,.5,0}:local(player,p,i);R.set(w,"marks45",add(marks(w),xyz[0],xyz[1],xyz[2],1));R.set(w,"markHealingStart45",Math.max(1,number(w,"healingLeft")));c(model,"scheduleResync");}
 public static void bite(Object model,Object player,Object source,Object before){if(!R.type("net.minecraft.server.level.ServerPlayer").isInstance(player)||!R.yes(c(R.type("ichttt.mods.firstaid.compat.lrtactical.TreatmentV33"),"zombieSource",source)))return;Object[] w=wounds(model),parts=R.type("ichttt.mods.firstaid.api.enums.EnumPlayerPart").getEnumConstants();for(int i=0;i<8;i++){double damage=((Number)R.get(c(before,"getFromEnum",parts[i]),"currentHealth")).doubleValue()-((Number)R.get(c(model,"getFromEnum",parts[i]),"currentHealth")).doubleValue();if(damage<=0||!flag(w[i],"bitten"))continue;Object attacker=c(source,"getEntity");Object p=attacker==null?null:c(attacker,"getEyePosition");double[] xyz=p==null?new double[]{.5,.5,0}:local(player,p,i);R.set(w[i],"marks45",add(marks(w[i]),xyz[0],xyz[1],xyz[2],2));R.set(w[i],"markHealingStart45",Math.max(1,number(w[i],"healingLeft")));}c(model,"scheduleResync");}
 /** Flat snapshot: clinical index, local xyz, kind, remaining duration, dressing flags. */
 public static int[] snapshot(Object player){Object m=model(player);if(m==null)return new int[0];ArrayList<Integer>a=new ArrayList<>();Object[] w=wounds(m);for(int i=0;i<8;i++){int[] marks=marks(w[i]);int f=(flag(w[i],"bandaged")?1:0)|(flag(w[i],"dirty")?2:0);for(int k=0;k<marks.length;k+=STRIDE){a.add(i);for(int j=0;j<STRIDE;j++)a.add(marks[k+j]);a.add(f);}}return a.stream().mapToInt(Integer::intValue).toArray();}
}
