package mod.cdv.tnpc.tactical;
public final class Link31 {
 static final ThreadLocal<Object> SOURCE=new ThreadLocal<>();
 public static void begin(Object o){SOURCE.set(o);}
 public static void end(){SOURCE.remove();}
 public static Object call(Object receiver,String method,Object[] args)throws Exception{Object result=Social.c(receiver,method,args);if(method.equals("addFreshEntity")&&Boolean.TRUE.equals(result)&&SOURCE.get()!=null)try{Social.c(Social.t("dev.zomboid.gore.Linked45"),"attach",args[0],SOURCE.get());}catch(RuntimeException e){if(!(e.getCause()instanceof ClassNotFoundException))throw e;}return result;}
}
