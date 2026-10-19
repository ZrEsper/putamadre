package net.flashlight.battery;

import java.lang.reflect.*;
import java.util.*;

/** Scoped terrain capture: keep the pack and its meshes, but use Sodium's texture-only program.
 * No VanillaRenderingPipeline constructor: it would change global vertex formats and rebuild chunks.
 * All fields are restored even when terrain drawing throws. Optional when Iris is not installed. */
public final class Iris12 implements AutoCloseable {
 private boolean shaderPack;public boolean shaderPack(){return shaderPack;}
 private final List<Object[]> saved=new ArrayList<>();
 private static Field field(Class<?> type,String name) throws NoSuchFieldException {
  for(Class<?> c=type;c!=null;c=c.getSuperclass())try{Field f=c.getDeclaredField(name);f.setAccessible(true);return f;}catch(NoSuchFieldException absent){}
  throw new NoSuchFieldException(type.getName()+"."+name);
 }
 private void change(Field field,Object owner,Object value)throws ReflectiveOperationException{
  Object old=field.get(owner);saved.add(new Object[]{field,owner,old});field.set(owner,value);
 }
 public static Iris12 enter(Object renderer){
  Iris12 scope=new Iris12();
  Class<?> iris;
  try{iris=Class.forName("net.irisshaders.iris.Iris");}catch(ClassNotFoundException absent){return scope;}
  try{
   Class<?> api=Class.forName("net.irisshaders.iris.pipeline.WorldRenderingPipeline");
   Object manager=iris.getMethod("getPipelineManager").invoke(null);
   Field current=field(manager.getClass(),"pipeline");Object original=current.get(manager);
   if(original==null||original.getClass().getSimpleName().equals("VanillaRenderingPipeline"))return scope;
   scope.shaderPack=true;
   Object capture=Proxy.newProxyInstance(api.getClassLoader(),new Class<?>[]{api},(proxy,method,args)->{
    if(method.getName().equals("setPhase")||method.getName().equals("setOverridePhase"))return null;
    if(method.getName().equals("getSodiumPrograms")||method.getName().equals("onSetShaderTexture"))return null;
    if(method.getName().equals("equals"))return proxy==args[0];
    if(method.getName().equals("hashCode"))return System.identityHashCode(proxy);
    try{return method.invoke(original,args);}catch(InvocationTargetException cause){throw cause.getCause();}
   });
   // LevelRenderer owns a second pipeline reference. Do not let its layer hooks mutate pack phase/debug groups.
   Field level=field(renderer.getClass(),"pipeline");
   if(!api.isAssignableFrom(level.getType()))throw new IllegalStateException("Unsupported Iris LevelRenderer pipeline field");
   scope.change(current,manager,capture);scope.change(level,renderer,capture);
   Class<?> blend=Class.forName("net.irisshaders.iris.gl.blending.BlendModeStorage");
   for(String name:new String[]{"originalBlendEnable","originalBlend","blendLocked"}){
    Field f=field(blend,name);Object old=f.get(null);scope.saved.add(new Object[]{f,null,old});
    if(name.equals("blendLocked"))f.setBoolean(null,false);
   }
   return scope;
  }catch(ReflectiveOperationException|RuntimeException failure){scope.close();throw new IllegalStateException("Cannot isolate Iris terrain capture",failure);}
 }
 public void close(){
  RuntimeException failure=null;
  for(int i=saved.size()-1;i>=0;i--){Object[] s=saved.get(i);try{((Field)s[0]).set(s[1],s[2]);}catch(ReflectiveOperationException e){failure=new IllegalStateException("Cannot restore Iris state",e);}}
  saved.clear();if(failure!=null)throw failure;
 }
}
