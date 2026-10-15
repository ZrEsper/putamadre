package dev.zomboid.remains;
import java.lang.reflect.*;
public final class CorpseMeta31{
 static Object c(Object o,String n,Object...a){try{for(Method m:o.getClass().getMethods())if(m.getName().equals(n)&&m.getParameterCount()==a.length)return m.invoke(o,a);throw new NoSuchMethodException(n);}catch(Exception e){throw new IllegalStateException(e);}}
 public static void write(Object corpse,Object tag){try{Object role=corpse.getClass().getField("profession31").get(corpse);if(role!=null)c(tag,"putString","Profession31",role);}catch(Exception e){throw new IllegalStateException(e);}}
 public static void read(Object corpse,Object tag){try{corpse.getClass().getField("profession31").set(corpse,c(tag,"getString","Profession31"));}catch(Exception e){throw new IllegalStateException(e);}}
 public static String title(String original,Object corpse){try{String role=(String)corpse.getClass().getField("profession31").get(corpse);if(role==null||role.isBlank())return original;try{Class<?>catalog=Class.forName("dev.zomboid.hordes.ProfessionCatalog");String[]ids=(String[])catalog.getField("IDS").get(null),labels=(String[])catalog.getField("LABELS").get(null);for(int i=0;i<ids.length;i++)if(ids[i].equals(role))return original+" - "+labels[i];}catch(ClassNotFoundException ignored){}return original+" - "+role;}catch(Exception e){throw new IllegalStateException(e);}}
}
