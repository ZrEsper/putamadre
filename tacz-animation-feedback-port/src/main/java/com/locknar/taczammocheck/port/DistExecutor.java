package com.locknar.taczammocheck.port;
import net.neoforged.api.distmarker.Dist;import net.neoforged.fml.loading.FMLEnvironment;import java.util.function.Supplier;
public class DistExecutor {public static void unsafeRunWhenOn(Dist dist,Supplier<Runnable> r){if(FMLEnvironment.dist==dist)r.get().run();}public interface SafeRunnable extends Runnable{} public static void safeRunWhenOn(Dist dist,Supplier<SafeRunnable> r){if(FMLEnvironment.dist==dist)r.get().run();}}
