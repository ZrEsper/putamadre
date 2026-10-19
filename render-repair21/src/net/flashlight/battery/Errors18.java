package net.flashlight.battery;
import java.util.function.IntSupplier;
/** GL errors belong to the context, not to the mod reading the flag later. */
public final class Errors18 {
 static boolean warned;
 public static void incoming(String stage,IntSupplier error){int first=error.getAsInt();if(first==0)return;for(int i=0;i<15&&error.getAsInt()!=0;i++){}if(!warned){warned=true;System.err.println("[Flashlight18] Pre-existing OpenGL error before "+stage+": "+first+"; not disabling the light for an earlier render operation");}}
 public static void check(String stage,IntSupplier error){int code=error.getAsInt();if(code!=0)throw new IllegalStateException("Flashlight "+stage+" generated OpenGL error: "+code);}
}
