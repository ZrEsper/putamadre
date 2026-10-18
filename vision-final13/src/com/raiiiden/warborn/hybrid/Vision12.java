package com.raiiiden.warborn.hybrid;
import com.raiiiden.warborn.common.util.NvgLink2;import com.raiiiden.warborn.common.util.NvgReflection;import java.util.*;import java.util.function.*;import java.lang.reflect.*;
/** Dedicated bounded post-chain cache: foreign GameRenderer effects are never closed or replaced. */
public final class Vision12{
 static final String REG="com.raiiiden.warborn.client.shader.ShaderRegistry";static final LinkedHashMap<String,Slot> CACHE=new LinkedHashMap<>(4,.75f,true);static Object world,owner,frozenRenderer;static boolean previousEffectActive;static Slot active;static int width,height;static long retry;static boolean warned;
 static final class Slot{Object chain;int width,height;Slot(Object c){chain=c;}}
 static Object c(Object o,String n,Object...a){return VR12.call(o,n,a);}static Object mc(){return c(VR12.type("net.minecraft.client.Minecraft"),"getInstance");}
 public static void frame(){prepare(c(VR12.type(REG),"getInstance"));}
 public static void ignoreForeignShutdown(Object renderer){}
 static void restoreForeign(){if(frozenRenderer!=null){NvgReflection.set(frozenRenderer,"effectActive",previousEffectActive);frozenRenderer=null;}}
 static void suppressForeign(Object renderer){if(frozenRenderer!=renderer){restoreForeign();previousEffectActive=Boolean.TRUE.equals(VR12.get(renderer,"effectActive"));frozenRenderer=renderer;}NvgReflection.set(renderer,"effectActive",false);}
 public static void clear(){restoreForeign();for(Slot s:CACHE.values())try{c(s.chain,"close");}catch(RuntimeException e){warn(e);}CACHE.clear();active=null;width=height=0;retry=0;}
 static void warn(RuntimeException e){if(!warned){warned=true;System.err.println("[NVG13] Cached vision pass: "+e);}}
 public static void prepare(Object registry){try{Object m=mc(),p=VR12.get(m,"player"),next=VR12.get(m,"level");if(p==null||next==null){clear();world=owner=null;NvgReflection.set(registry,"currentActiveShader",null);return;}if(world!=next||owner!=p){clear();world=next;owner=p;}Object helmet=NvgLink2.head(p);String wanted=null;Object entry=null;
 if(NvgLink2.down(helmet)){for(var e:((Map<?,?>)VR12.get(registry,"shaders")).entrySet()){Object se=e.getValue(),force=VR12.get(se,"forceEnabled");boolean enabled=Boolean.TRUE.equals(force)||!Boolean.FALSE.equals(force)&&((Predicate<Object>)VR12.get(se,"activationCondition")).test(m);if(enabled){wanted=e.getKey().toString();entry=se;break;}}}
 if(wanted==null){active=null;NvgReflection.set(registry,"currentActiveShader",null);if(!CACHE.isEmpty())clear();return;}
 Object target=c(m,"getMainRenderTarget");int w=((Number)VR12.get(target,"width")).intValue(),h=((Number)VR12.get(target,"height")).intValue();if(w<=0||h<=0)return;width=w;height=h;Slot slot=CACHE.get(wanted);if(slot==null){if(System.nanoTime()<retry){active=null;return;}Object chain=VR12.make("net.minecraft.client.renderer.PostChain",c(m,"getTextureManager"),c(m,"getResourceManager"),target,VR12.get(entry,"shaderLocation"));slot=new Slot(chain);c(chain,"resize",w,h);slot.width=w;slot.height=h;((Consumer<Object>)VR12.get(entry,"configurer")).accept(chain);CACHE.put(wanted,slot);while(CACHE.size()>2){var it=CACHE.entrySet().iterator();Slot old=it.next().getValue();it.remove();c(old.chain,"close");}}
 if(slot.width!=w||slot.height!=h){c(slot.chain,"resize",w,h);slot.width=w;slot.height=h;}active=slot;suppressForeign(VR12.get(m,"gameRenderer"));NvgReflection.set(registry,"currentActiveShader",wanted);
 }catch(RuntimeException e){active=null;retry=System.nanoTime()+1_000_000_000L;warn(e);}}
 // Level-stage rendering precedes vanilla first-person hands.
 public static void render(Object event){}
 public static void finish(Object delta){if(active==null)return;Object m=mc();if(VR12.get(m,"player")==null||VR12.get(m,"level")!=world)return;float partial=((Number)c(delta,"getGameTimeDeltaPartialTick",false)).floatValue();try(Gpu12.State saved=new Gpu12.State()){Object rs=Gpu12.rs();c(rs,"disableBlend");c(rs,"disableDepthTest");c(rs,"resetTextureMatrix");c(active.chain,"process",partial);c(c(m,"getMainRenderTarget"),"bindWrite",true);}catch(RuntimeException e){warn(e);}}
 public static void reloadEvent(Object event){Class<?> listener=VR12.type("net.minecraft.server.packs.resources.ResourceManagerReloadListener");Object proxy=Proxy.newProxyInstance(listener.getClassLoader(),new Class<?>[]{listener},(p,m,args)->{if(m.getName().equals("onResourceManagerReload")){c(mc(),"execute",(Runnable)Vision12::clear);return null;}if(m.isDefault())return InvocationHandler.invokeDefault(p,m,args);if(m.getName().equals("toString"))return "NVG12ResourceReset";if(m.getName().equals("hashCode"))return System.identityHashCode(p);if(m.getName().equals("equals"))return p==args[0];return null;});c(event,"registerReloadListener",proxy);}
}
