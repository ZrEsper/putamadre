package dev.zomboid.hordes;
public final class MutantPool {
 static Object c(Object o,String n,Object...a){return ZoneReflection.call(o,n,a);}
 public static Object call(Object object,String method,Object[]args){
  if(method.equals("attributes")&&dev.zomboid.rarehorde.Mutants.supported(dev.zomboid.rarehorde.Mutants.id(args[0])))return null;
  Object original=c(object,method,args);if(!method.equals("pick"))return original;
  double[]w=ApocalypseSettings.weights(WorldTuning.snapshot(),"mutante_pool_",ApocalypseSettings.MUTANTS);if(java.util.Arrays.stream(w).sum()<=0)return original;
  Object level=args[0],pos=args[1],random=ZoneReflection.get(level,"random");if(((Number)c(random,"nextInt",1000000)).intValue()>=WorldTuning.mutantThreshold())return original;
  int n=0;double x=((Number)c(pos,"getX")).doubleValue(),z=((Number)c(pos,"getZ")).doubleValue();
  for(Object e:(Iterable<?>)c(level,"getAllEntities")){if(!Boolean.TRUE.equals(c(e,"isAlive"))||!dev.zomboid.rarehorde.Mutants.supported(dev.zomboid.rarehorde.Mutants.id(e)))continue;double dx=((Number)c(e,"getX")).doubleValue()-x,dz=((Number)c(e,"getZ")).doubleValue()-z;if(dx*dx+dz*dz<=16384&&++n>=WorldTuning.mutantCap())return original;}
  // Exclude missing entity IDs BEFORE the draw, so unavailable mutants cannot dilute the pool.
  Object registry=ZoneReflection.get(ZoneReflection.type("net.minecraft.core.registries.BuiltInRegistries"),"ENTITY_TYPE");Object[]keys=new Object[w.length];for(int i=0;i<w.length;i++){keys[i]=c(ZoneReflection.type("net.minecraft.resources.ResourceLocation"),"parse","mutantszombies:"+ApocalypseSettings.MUTANTS[i]);if(!Boolean.TRUE.equals(c(registry,"containsKey",keys[i])))w[i]=0;}
  int i=ApocalypseSettings.weighted(w,((Number)c(random,"nextDouble")).doubleValue());return i<0?original:c(registry,"get",keys[i]);
 }
}
