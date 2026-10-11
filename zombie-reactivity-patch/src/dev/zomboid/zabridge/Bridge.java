package dev.zomboid.zabridge;
public class Bridge {
 static Class<?> helper;static boolean missing;
 static Class<?> helper()throws Exception{if(missing)return null;if(helper==null)try{helper=Class.forName("dev.zomboid.reactive.Reactive");}catch(ClassNotFoundException e){missing=true;}return helper;}
 public static boolean controlled(Object mob){try{Class<?> h=helper();return h!=null&&(boolean)h.getMethod("controlled",Object.class).invoke(null,mob);}catch(Exception e){System.err.println("[Zombie Awareness bridge] "+e);return false;}}
 public static void sound(Object sound,Object level,double x,double y,double z,float volume){try{Class<?> h=helper();if(h!=null)h.getMethod("sound",Object.class,Object.class,double.class,double.class,double.class,float.class).invoke(null,sound,level,x,y,z,volume);}catch(Exception e){System.err.println("[Zombie Awareness bridge] "+e);}}
 public static void block(Object p,Object level,Object pos){try{Class<?> h=helper();if(h!=null)h.getMethod("block",Object.class,Object.class,Object.class).invoke(null,p,level,pos);}catch(Exception e){System.err.println("[Zombie Awareness block bridge] "+e);}}
 public static void levelEvent(int type,Object level,double x,double y,double z){try{Class<?> h=helper();if(h!=null)h.getMethod("levelEvent",int.class,Object.class,double.class,double.class,double.class).invoke(null,type,level,x,y,z);}catch(Exception e){System.err.println("[Zombie Awareness event bridge] "+e);}}
}
