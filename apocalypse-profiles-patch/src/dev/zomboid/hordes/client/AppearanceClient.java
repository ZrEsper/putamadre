package dev.zomboid.hordes.client;
import java.util.*;import java.lang.reflect.*;import java.security.*;import java.io.*;import dev.zomboid.hordes.*;
public final class AppearanceClient {
 static Object previous;static long last;static final Set<UUID>sent=new HashSet<>();static boolean unavailable;
 static Object c(Object o,String m,Object...a){return ZoneReflection.call(o,m,a);}
 public static void tick(Object event){if(unavailable)return;try{
  Object mc=c(ZoneReflection.type("net.minecraft.client.Minecraft"),"getInstance"),level=ZoneReflection.get(mc,"level"),player=ZoneReflection.get(mc,"player");if(level==null||player==null){sent.clear();previous=null;return;}if(previous!=level){previous=level;sent.clear();last=-20;}long time=((Number)c(level,"getGameTime")).longValue();if(time-last<20)return;last=time;if(sent.size()>4096)sent.clear();
  Class<?>api;try{api=Class.forName("traben.entity_texture_features.ETFApi");}catch(ClassNotFoundException e){unavailable=true;return;}
  int reports=0;for(Object entity:(Iterable<?>)c(level,"entitiesForRendering")){
   String type=String.valueOf(c(c(entity,"getType"),"getDescriptionId"));if(!type.contains("zombie")&&!type.contains("husk")&&!type.contains("drowned"))continue;UUID id=(UUID)c(entity,"getUUID");if(sent.contains(id)||((Number)c(player,"distanceToSqr",entity)).doubleValue()>4096||!Boolean.TRUE.equals(c(player,"hasLineOfSight",entity)))continue;
   Object renderer=c(c(mc,"getEntityRenderDispatcher"),"getRenderer",entity),base=c(renderer,"getTextureLocation",entity);Object texture=c(api,"getCurrentETFVariantTextureOfEntity",entity,base);String key=String.valueOf(texture);String map=AppearanceSync.known(key);sent.add(id);
   Optional<?>resource=(Optional<?>)c(c(mc,"getResourceManager"),"getResource",texture);if(resource.isEmpty())continue;String digest;try(InputStream in=(InputStream)c(resource.get(),"open")){digest=HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(in.readAllBytes()));}
   Object payload=ZoneReflection.type("dev.zomboid.hordes.AdminNetwork$Apply").getConstructor(String.class).newInstance(AppearanceSync.PREFIX+id+"|"+key+"|"+digest);Object empty=Array.newInstance(ZoneReflection.type("net.minecraft.network.protocol.common.custom.CustomPacketPayload"),0);c(ZoneReflection.type("net.neoforged.neoforge.network.PacketDistributor"),"sendToServer",payload,empty);if(++reports>=4)break;
  }
 }catch(Exception e){unavailable=true;System.getLogger("Apocalypse31").log(System.Logger.Level.WARNING,"Lectura ETF no disponible: se mantiene reconocimiento por equipo/profesion y pool",e);}}
}
