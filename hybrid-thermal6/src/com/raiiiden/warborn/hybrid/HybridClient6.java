package com.raiiiden.warborn.hybrid;
import java.util.*;import java.lang.reflect.*;import java.util.function.*;
public final class HybridClient6 {
 static Object whiteType;static boolean registered;
 static Object c(Object o,String n,Object...a){return Hybrid6.c(o,n,a);}static Class<?>t(String n){return Hybrid6.t(n);}
 public static boolean active(){return Boolean.TRUE.equals(c(c(t("com.raiiiden.warborn.client.shader.ShaderRegistry"),"getInstance"),"isShaderActive",Hybrid6.SHADER));}
 public static void register(){Object registry=c(t("com.raiiiden.warborn.client.shader.ShaderRegistry"),"getInstance");Object id=c(t("net.minecraft.resources.ResourceLocation"),"fromNamespaceAndPath","fracturepoint","shaders/post/hybrid-thermal-vision.json");c(registry,"registerShader",Hybrid6.SHADER,id,(Predicate<Object>)mc->false,(Consumer<Object>)effect->{});registered=true;}
 /** Replace only standard living-entity mesh buffers; text, lines and special formats stay native. */
 public static Object buffers(Object source){if(Proxy.isProxyClass(source.getClass())&&Proxy.getInvocationHandler(source) instanceof MeshSource)return source;Class<?>iface=t("net.minecraft.client.renderer.MultiBufferSource");return Proxy.newProxyInstance(iface.getClassLoader(),new Class[]{iface},new MeshSource(source));}
 static final class MeshSource implements InvocationHandler{final Object source;final Map<Object,Object>wrapped=new IdentityHashMap<>();MeshSource(Object s){source=s;}public Object invoke(Object proxy,Method method,Object[]args)throws Throwable{if(!method.getName().equals("getBuffer"))return method.invoke(source,args);Object type=args[0];Object expected=com.raiiiden.warborn.common.util.NvgReflection.get(t("com.mojang.blaze3d.vertex.DefaultVertexFormat"),"NEW_ENTITY");if(c(type,"format")!=expected)return c(source,"getBuffer",type);if(whiteType==null){Object texture=c(t("net.minecraft.resources.ResourceLocation"),"fromNamespaceAndPath","fracturepoint","textures/misc/hybrid-white.png");whiteType=c(t("net.minecraft.client.renderer.RenderType"),"entityCutoutNoCull",texture);}Object consumer=c(source,"getBuffer",whiteType);return wrapped.computeIfAbsent(consumer,HybridClient6::marker);}}
 static Object marker(Object delegate){Class<?>iface=t("com.mojang.blaze3d.vertex.VertexConsumer");return Proxy.newProxyInstance(iface.getClassLoader(),new Class[]{iface},(proxy,m,a)->{Object result;switch(m.getName()){
 case "setColor":int alpha=a.length==4?((Number)a[3]).intValue():((Number)a[0]).intValue()>>>24;result=c(delegate,"setColor",239,1,137,alpha);break;
 case "setUv1":result=c(delegate,"setUv1",0,10);break;
 case "setUv2":result=c(delegate,"setUv2",240,240);break;
 default:result=m.invoke(delegate,a);break;
 }return iface.isAssignableFrom(m.getReturnType())?proxy:result;});}
}
