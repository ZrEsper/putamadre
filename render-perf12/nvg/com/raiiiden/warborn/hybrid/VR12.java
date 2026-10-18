package com.raiiiden.warborn.hybrid;
import com.raiiiden.warborn.common.util.NvgReflection;import java.util.*;
public final class VR12{static final Map<String,Class<?>> TYPES=new java.util.concurrent.ConcurrentHashMap<>();public static Class<?>type(String n){return TYPES.computeIfAbsent(n,NvgReflection::type);}public static Object call(Object o,String n,Object...a){return NvgReflection.call(o,n,a);}public static Object get(Object o,String n){return NvgReflection.get(o,n);}public static Object make(String n,Object...a){return NvgReflection.make(n,a);}}
