package com.raiiiden.warborn.common.util;
import java.util.*;
public final class NvgSupport33 {
 static final String HANDLER="com.raiiiden.warborn.common.event.HelmetBatteryTickHandler",OWN="WB_NVG33OwnEffect";static final Map<Object,Backup>backups=new WeakHashMap<>();
 record Backup(Object effect,long time){}
 static Object c(Object o,String n,Object...a){return NvgReflection.call(o,n,a);}static boolean yes(Object o){return Boolean.TRUE.equals(o);}static long time(Object p){return ((Number)c(c(p,"level"),"getGameTime")).longValue();}
 public static void tooltip(java.util.List<Object> lines){lines.add(c(NvgReflection.type("net.minecraft.network.chat.Component"),"translatable","tooltip.fracturepoint.nvg33.duration",new Object[0]));}
 public static int drain(int ignored,Object player){Object tag=c(player,"getPersistentData");int phase=Math.floorMod(((Number)c(tag,"getInt","WB_NVG33DrainPhase")).intValue(),9);c(tag,"putInt","WB_NVG33DrainPhase",(phase+1)%9);return (int)(50000L*(phase+1)/900-50000L*phase/900);}
 public static void event(Object e){player(c(e,"getEntity"));}
 @SuppressWarnings("unchecked")public static void player(Object p){if(p==null||yes(c(c(p,"level"),"isClientSide")))return;Object id=c(p,"getUUID"),tag=c(p,"getPersistentData");Map<Object,Object>active=(Map<Object,Object>)NvgReflection.get(NvgReflection.type(HANDLER),"nvgActiveMap"),energy=(Map<Object,Object>)NvgReflection.get(NvgReflection.type(HANDLER),"batteryEnergyCache");boolean on=active.containsKey(id)&&energy.get(id)instanceof Number n&&n.intValue()>0;Object holder=NvgReflection.get(NvgReflection.type("net.minecraft.world.effect.MobEffects"),"NIGHT_VISION"),effect=c(p,"getEffect",holder);
  if(on){if(!yes(c(tag,"getBoolean",OWN))){if(effect!=null)backups.put(p,new Backup(NvgReflection.make("net.minecraft.world.effect.MobEffectInstance",effect),time(p)));c(tag,"putBoolean",OWN,true);if(effect!=null)c(p,"removeEffect",holder);effect=null;}if(effect==null||((Number)c(effect,"getDuration")).intValue()!=-1)c(p,"addEffect",NvgReflection.make("net.minecraft.world.effect.MobEffectInstance",holder,-1,0,true,false,true));}
  else if(yes(c(tag,"getBoolean",OWN))){c(tag,"putBoolean",OWN,false);if(effect!=null&&((Number)c(effect,"getDuration")).intValue()==-1&&((Number)c(effect,"getAmplifier")).intValue()==0&&yes(c(effect,"isAmbient")))c(p,"removeEffect",holder);Backup b=backups.remove(p);if(b!=null){int duration=((Number)c(b.effect(),"getDuration")).intValue();if(duration>=0)duration=(int)Math.max(0,duration-(time(p)-b.time()));if(duration!=0)c(p,"addEffect",NvgReflection.make("net.minecraft.world.effect.MobEffectInstance",holder,duration,((Number)c(b.effect(),"getAmplifier")).intValue(),yes(c(b.effect(),"isAmbient")),yes(c(b.effect(),"isVisible")),yes(c(b.effect(),"showIcon"))));}}
 }
}
