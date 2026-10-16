package net.mcreator.selfexpression.client;
/** One model per identical native geometry, invalidated with the model resources. */
public final class SharedArmorModels {
 private static long epoch=Long.MIN_VALUE;
 private static Object[] models=new Object[5];
 public static synchronized Object get(int shape){
  if(shape<0||shape>=models.length)throw new IllegalArgumentException("Unknown Selfexpression armor geometry: "+shape);
  long now=ArmorCacheEpoch.current();
  if(now!=epoch){models=new Object[5];epoch=now;}
  Object model=models[shape];
  if(model==null){model=SharedArmorFactories.create(shape);if(model==null)throw new IllegalStateException("Missing armor geometry");models[shape]=model;}
  return model;
 }
}
