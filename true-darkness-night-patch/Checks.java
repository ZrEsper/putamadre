import java.net.*;import java.nio.file.*;
public class Checks {
 public static void main(String[] a)throws Exception {
  int cases=0;
  try(URLClassLoader before=new URLClassLoader(new URL[]{Path.of(a[0]).toUri().toURL()},null);URLClassLoader after=new URLClassLoader(new URL[]{Path.of(a[1]).toUri().toURL()},null)){
   String name="com.hyrrx.hardcoretruedarkness.client.ClientDarknessState$RenderState";Class<?> original=before.loadClass(name),patched=after.loadClass(name);
   for(boolean night:new boolean[]{false,true})for(boolean cave:new boolean[]{false,true})for(float intensity:new float[]{0f,.2f,.5f,1f}){
    Object old=original.getConstructor(boolean.class,float.class,boolean.class,boolean.class,boolean.class).newInstance(true,intensity,night,cave,true);
    Object now=patched.getConstructor(boolean.class,float.class,boolean.class,boolean.class,boolean.class).newInstance(true,intensity,night,cave,true);
    float expected=(float)original.getMethod("intensity").invoke(old)*(night&&!cave?.82f:1f),actual=(float)patched.getMethod("intensity").invoke(now);
    if(Math.abs(actual-expected)>.000001f)throw new AssertionError("wrong intensity "+night+" / "+cave);
    for(String field:new String[]{"enabled","night","cave","hasSkyLight"})if(!original.getMethod(field).invoke(old).equals(patched.getMethod(field).invoke(now)))throw new AssertionError(field);
    cases++;
   }
   Object disabled=patched.getMethod("disabled").invoke(null);if((boolean)patched.getMethod("enabled").invoke(disabled)||(float)patched.getMethod("intensity").invoke(disabled)!=0f)throw new AssertionError("disabled");
  }
  System.out.println(cases+" actual patched-record cases passed; disabled state preserved.");
 }
}
