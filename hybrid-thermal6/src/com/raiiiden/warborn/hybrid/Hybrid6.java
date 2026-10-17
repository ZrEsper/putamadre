package com.raiiiden.warborn.hybrid;
import java.util.*;import java.util.function.Supplier;import com.raiiiden.warborn.common.util.NvgReflection;
public final class Hybrid6 {
 public static final String MODE="thermal_hybrid",SHADER="htvg";public static Object ITEM;
 public static Object c(Object o,String n,Object...a){return NvgReflection.call(o,n,a);}public static Class<?>t(String s){return NvgReflection.type(s);}
 public static Set<String> extend(Set<String>old,String value){Set<String>s=new HashSet<>(old);s.add(value);return Collections.unmodifiableSet(s);}
 public static void register(Object items){ITEM=c(items,"register","hybrid_thermal_goggles",(Supplier<Object>)()->NvgReflection.make("com.raiiiden.warborn.common.item.GogglesItem",MODE));}
 public static void creative(Object out){c(out,"accept",c(ITEM,"get"));}
 public static void drain(Map<String,Integer>rates){rates.put(MODE,5);}
}
