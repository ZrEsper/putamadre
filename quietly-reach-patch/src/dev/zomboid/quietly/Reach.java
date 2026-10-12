package dev.zomboid.quietly;
import dev.zomboid.scavenging.R;
/** Keep client targeting and server interaction validation on the same minimum reach. */
public final class Reach {
 public static double minimum(double value){return Math.max(5d,value);}
 public static void extend(Object callback){double old=((Number)R.call(callback,"getReturnValue")).doubleValue();if(old<5)R.call(callback,"setReturnValue",Double.valueOf(minimum(old)));}
}
