package dev.zomboid.isotacz;
public final class AimInteraction29 {
 public static boolean blocked(){Object mc=Bridge.mc(),p=R.get(mc,"player");return p!=null&&R.get(mc,"screen")==null&&Bridge.gun(p)&&CameraLock.aiming();}
 public static void guard(Object ci){if(blocked())R.call(ci,"setReturnValue",R.get(R.type("net.minecraft.world.InteractionResult"),"PASS"));}
 public static boolean mouse(long window,int button,int action,int mods,Object ci){
  Object mc=Bridge.mc(),p=R.get(mc,"player");
  if(!Bridge.active()||p==null||R.get(mc,"screen")!=null||R.get(mc,"level")==null||!Bridge.gun(p)||button!=1||window!=((Number)R.call(R.call(mc,"getWindow"),"getWindow")).longValue())return false;
  // Release always reaches the existing Bridge path to clear rightAim/mouseClaimed.
  if(action!=1)return false;
  if(!blocked()&&R.yes(R.call(p,"isShiftKeyDown")))return false;
  Bridge.setAim(true);Bridge.keepPerspective();R.call(ci,"cancel");return true;
 }
}
