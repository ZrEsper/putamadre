package dev.zomboidmelee.boxing;
import java.util.*;import java.lang.reflect.Array;
/** Fall direction is authoritative, saved on the zombie and broadcast to current/late observers. */
public final class Fall24{
 public record Direction(double yaw,boolean back){}
 static final Map<Object,Direction>CLIENT=new WeakHashMap<>();
 static boolean server(Object v){return !R.yes(R.call(R.call(v,"level"),"isClientSide"));}
 static double wrap(double a){return ((a+180)%360+360)%360-180;}
 public static int encode(double yaw,boolean back){int q=((int)Math.round((((yaw%360)+360)%360)*256/360))&255;return 1000+q*2+(back?1:0);}
 public static Direction decode(int action){if(action<1000||action>1511)return null;int n=action-1000;return new Direction((n/2)*360d/256,(n&1)!=0);}
 public static void record(Object v,Object attacker){if(!Stomp18.temporary(v)||!server(v))return;Object data=R.call(v,"getPersistentData");long now=Combat18.time(v);if(R.lng(R.call(data,"getLong","boxing18_down_until"))!=now+180||R.lng(R.call(data,"getLong","boxing24_recorded"))==now+1)return;double dx=R.num(R.call(v,"getX"))-R.num(R.call(attacker,"getX")),dz=R.num(R.call(v,"getZ"))-R.num(R.call(attacker,"getZ"));if(Math.hypot(dx,dz)<.001){Object look=R.call(attacker,"getLookAngle");dx=R.num(R.get(look,"x"));dz=R.num(R.get(look,"z"));}double yaw=Math.toDegrees(Math.atan2(-dx,dz)),body=Math.toRadians(R.num(R.get(v,"yBodyRot")));boolean back=dx*-Math.sin(body)+dz*Math.cos(body)<0;int action=encode(yaw,back);R.call(data,"putLong","boxing24_fall_code",(long)action+1);R.call(data,"putLong","boxing24_recorded",now+1);Object packet=R.make("dev.zomboidmelee.boxing.Heavy18",((Number)R.call(v,"getId")).intValue(),action);R.call(R.type("net.neoforged.neoforge.network.PacketDistributor"),"sendToPlayersTrackingEntity",v,packet,Array.newInstance(R.type("net.minecraft.network.protocol.common.custom.CustomPacketPayload"),0));}
 public static void tracking(Object event){Object p=R.call(event,"getEntity"),v=R.call(event,"getTarget");if(!R.type("net.minecraft.server.level.ServerPlayer").isInstance(p)||!server(p)||!Stomp18.temporary(v))return;int action=(int)R.lng(R.call(R.call(v,"getPersistentData"),"getLong","boxing24_fall_code"))-1;if(decode(action)==null)return;Object packet=R.make("dev.zomboidmelee.boxing.Heavy18",((Number)R.call(v,"getId")).intValue(),action);R.call(R.type("net.neoforged.neoforge.network.PacketDistributor"),"sendToPlayer",p,packet,Array.newInstance(R.type("net.minecraft.network.protocol.common.custom.CustomPacketPayload"),0));}
 public static void receive(Object v,int action){Direction d=decode(action);if(d!=null&&Stomp18.supported(v))CLIENT.put(v,d);}
 public static Direction direction(Object v){if(server(v))return decode((int)R.lng(R.call(R.call(v,"getPersistentData"),"getLong","boxing24_fall_code"))-1);return CLIENT.get(v);}
 public static double tilt(Object v){Direction d=direction(v);return d==null||d.back?1:-1;}
 public static double turn(Object v){Direction d=direction(v);if(d==null)return 0;return wrap(R.num(R.get(v,"yBodyRot"))-(d.yaw+(d.back?180:0)));}
 public static Object head(Object v){double scale=Stomp18.fallenScale(v);Direction d=Stomp18.temporary(v)?direction(v):null;double yaw=Math.toRadians(d==null?R.num(R.get(v,"yBodyRot")):d.yaw);double x=d==null?Math.sin(yaw):-Math.sin(yaw),z=d==null?-Math.cos(yaw):Math.cos(yaw);return R.call(R.call(v,"position"),"add",x*.95*scale,.35*scale,z*.95*scale);}
}
