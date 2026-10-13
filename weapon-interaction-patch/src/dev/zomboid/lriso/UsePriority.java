package dev.zomboid.lriso;
public final class UsePriority {
    static boolean call(String method,Object event){
        try{return R.yes(R.call(R.type("dev.zomboid.taczinteract.Priority"),method,event));}
        catch(IllegalStateException failure){Throwable cause=failure;while(cause.getCause()!=null)cause=cause.getCause();if(cause instanceof ClassNotFoundException)return false;throw failure;}
    }
    public static boolean allowUse(Object event){return call("allowUse",event);}
    public static boolean meleeEvent(Object event){return call("meleeEvent",event);}
}
