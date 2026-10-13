package dev.zomboid.wardrobebudget;
import java.util.*;
public final class WardrobeCache {
 private static Object roots,models;private static final Map<Object,Object> cache=new IdentityHashMap<>();
 public static Object get(Object extension,Object entity,Object stack,Object slot,Object original){try{Object mc=Lookup.call(Class.forName("net.minecraft.client.Minecraft"),"getInstance");Object set=Lookup.call(mc,"getEntityModels");return getForRoots(extension,set,Lookup.get(set,"roots"),entity,stack,slot,original);}catch(ClassNotFoundException e){throw new IllegalStateException(e);}}
 public static Object getForRoots(Object extension,Object set,Object r,Object entity,Object stack,Object slot,Object original){if(models!=set||roots!=r){models=set;roots=r;cache.clear();}return cache.computeIfAbsent(extension,k->Lookup.call(k,"zomboid$makeModel",entity,stack,slot,original));}
}
