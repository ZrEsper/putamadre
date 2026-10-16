package dev.zomboid.integration.care;
import java.util.*;
/** Adds stable enum IDs and treatment flags; leaves original server validation and timers intact. */
public final class InjuryCare20 {
 static Object c(Object o,String n,Object...a){return Medical.call(o,n,a);}public static Object rows(Object rows,Object player){Object[] parts=Medical.PARTS;for(Object row:(Iterable<?>)rows){int i=((Number)c(c(row,"get","index"),"getAsInt")).intValue();int modes=0;for(int mode=1;mode<=4;mode++)if(Boolean.TRUE.equals(c(Medical.class,"needed",player,i,mode)))modes|=1<<mode;c(row,"addProperty","part",((Enum<?>)parts[i]).name());c(row,"addProperty","modes",modes);String details=(String)c(c(row,"get","details"),"getAsString");Object part=c(Medical.class,"part",c(Medical.class,"model",player),i);boolean injured=modes!=0||((Number)Medical.get(part,"currentHealth")).doubleValue()<((Number)c(part,"getMaxHealth")).doubleValue()-.001||!details.equals("Sin lesiones activas")&&!details.replace("Vendaje aplicado; ","").trim().isEmpty();c(row,"addProperty","injured",injured);}return rows;}
}
