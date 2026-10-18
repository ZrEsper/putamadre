package dev.zomboid.isotacz;
/** Keep the cursor-owned player aim separate from external shoulder-camera firing hooks. */
public final class ShotAim32 {
 public static boolean owns(){Object mc=Bridge.mc(),p=R.get(mc,"player");return Bridge.active()&&p!=null&&R.get(mc,"level")!=null&&R.get(mc,"screen")==null&&Bridge.gun(p)&&!AimStability.medical();}
 public static void before(){if(owns())Bridge.aim();}
 public static void external(Object callback){if(owns())R.call(callback,"cancel");}
}
