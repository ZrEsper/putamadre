package dev.zomboid.mts;
import java.io.*;import java.nio.charset.StandardCharsets;import java.util.*;
public final class WorkshopLanguage {
 private static final Map<String,String[]> TEXT=new LinkedHashMap<>();private static long next;private static String language="es_es";
 static {try(var in=WorkshopLanguage.class.getResourceAsStream("/dev/zomboid/mts/workshop-translations.tsv")){if(in!=null)try(var reader=new BufferedReader(new InputStreamReader(in,StandardCharsets.UTF_8))){reader.lines().forEach(l->{String[] v=l.split("\t",-1);if(v.length==3)TEXT.put(v[0],new String[]{v[1],v[2]});});}}catch(IOException ignored){}}
 private static String locale(){long now=System.nanoTime();if(now>=next){next=now+1_000_000_000L;try{Class<?> c=Class.forName("net.minecraft.client.Minecraft");Object mc=c.getMethod("getInstance").invoke(null);Object options=c.getField("options").get(mc);language=Objects.toString(options.getClass().getField("language").get(options),"es_es");}catch(ReflectiveOperationException|LinkageError ignored){language="es_es";}}return language;}
 public static String text(String source){return translate(source,locale());}
 public static String translate(String source,String locale){if(source==null||locale.startsWith("es"))return source;int i=locale.startsWith("de")?1:locale.startsWith("en")?0:-1;if(i<0)return source;String[] v=TEXT.get(source);return v==null?source:v[i];}
 public static Object displayed(String field,Object value){if(value instanceof String s&&(field.equals("text")||field.equals("label")||field.equals("displayText")))return text(s);return value;}
}
