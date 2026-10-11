package dev.zomboid.scavengingjade;
public final class Bridge {
 public static boolean hide(Object accessor){try{return Boolean.TRUE.equals(Class.forName("dev.zomboid.scavenging.Search").getMethod("hidePreview",Object.class).invoke(null,accessor));}catch(ClassNotFoundException e){return false;}catch(ReflectiveOperationException e){System.err.println("[Scavenging Jade] "+e);return true;}}
}
