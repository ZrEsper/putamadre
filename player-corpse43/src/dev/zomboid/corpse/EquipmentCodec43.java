package dev.zomboid.corpse;
import java.lang.reflect.*;import java.util.*;
/** Corpse's Equipment is positional: empty slots must survive serialization. */
public final class EquipmentCodec43 {
 static Object call(Object o,String name,Object...a)throws Exception{for(Method m:o.getClass().getMethods()){if(!m.getName().equals(name)||m.getParameterCount()!=a.length)continue;boolean fit=true;for(int i=0;i<a.length;i++)if(a[i]!=null&&!m.getParameterTypes()[i].isInstance(a[i]))fit=false;if(fit)return m.invoke(o,a);}throw new NoSuchMethodException(name);}
 public static boolean write(Object provider,Object tag,String name,Object stacks){if(!name.equals("Equipment"))return false;try{Object list=Class.forName("net.minecraft.nbt.ListTag").getConstructor().newInstance();for(Object stack:(List<?>)stacks){Object entry=Boolean.TRUE.equals(call(stack,"isEmpty"))?Class.forName("net.minecraft.nbt.CompoundTag").getConstructor().newInstance():call(stack,"save",provider);call(list,"add",entry);}call(tag,"put",name,list);return true;}catch(Exception e){throw new IllegalStateException("Cannot preserve Corpse equipment slots",e);}}
}
