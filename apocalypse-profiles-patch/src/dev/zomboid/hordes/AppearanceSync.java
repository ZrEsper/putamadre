package dev.zomboid.hordes;
import java.util.*;import java.io.*;import java.nio.file.*;
public final class AppearanceSync {
 public static final String PREFIX="!appearance31:";static final Properties defaults=new Properties();static final Map<Object,long[]>rate=new WeakHashMap<>();
 static {try(InputStream in=AppearanceSync.class.getResourceAsStream("/apocalypse31/skin-profiles.properties")){if(in!=null)defaults.load(new InputStreamReader(in,java.nio.charset.StandardCharsets.UTF_8));}catch(IOException e){throw new ExceptionInInitializerError(e);}}
 static Object c(Object o,String m,Object...a){return ZoneReflection.call(o,m,a);}
 public static String known(String texture){return defaults.getProperty(texture);}
 public static boolean server(Object payload,Object context){String msg=String.valueOf(c(payload,"json"));if(!msg.startsWith(PREFIX))return false;
  try{if(msg.length()>512||!WorldTuning.flag("profesion_por_aspecto_todos")||!WorldTuning.flag("profesion_cadaveres_todos"))return true;
   Object player=c(context,"player"),level=c(player,"level");long tick=((Number)c(level,"getGameTime")).longValue();long[]r=rate.computeIfAbsent(player,p->new long[]{tick,0});if(tick-r[0]>=20||tick<r[0]){r[0]=tick;r[1]=0;}if(++r[1]>8)return true;
   String[]s=msg.substring(PREFIX.length()).split("\\|",-1);if(s.length!=3)return true;UUID id=UUID.fromString(s[0]);Object entity=c(level,"getEntity",id);if(entity==null||!Boolean.TRUE.equals(c(entity,"isAlive"))||((Number)c(player,"distanceToSqr",entity)).doubleValue()>4096||!Boolean.TRUE.equals(c(player,"hasLineOfSight",entity)))return true;
   String type=String.valueOf(c(c(entity,"getType"),"getDescriptionId"));if(!type.contains("zombie")&&!type.contains("husk")&&!type.contains("drowned"))return true;
   Object tag=c(entity,"getPersistentData");if(Boolean.TRUE.equals(c(tag,"getBoolean","zh31_skin_known")))return true;
   int recognized=CorpseProfessions.recognize(entity);String existing=String.valueOf(c(tag,"getString",CorpseProfessions.ROLE));if(recognized>=0&&!existing.equals(ProfessionCatalog.IDS[recognized]))return true;
   String map=CorpseProfessions.settings().getProperty("texture."+s[1],known(s[1]));if(map==null)return true;String[]entry=map.split("\\|",-1);if(entry.length!=2||!entry[1].equals(s[2])||CorpseProfessions.index(entry[0])<0)return true;
   // Armor or a real profession still takes priority over a client texture hint.
   if(!CorpseProfessions.armor(entity).isEmpty())return true;
   c(tag,"putString",CorpseProfessions.ROLE,entry[0]);c(tag,"putString","zh31_skin",s[1]);c(tag,"putBoolean","zh31_skin_known",true);
  }catch(RuntimeException e){CorpseProfessions.warn("skin report",e);}return true;
 }
}
