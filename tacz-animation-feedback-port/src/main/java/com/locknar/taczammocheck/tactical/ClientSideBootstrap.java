package com.locknar.taczammocheck.tactical;

import java.lang.reflect.Method;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;

public final class ClientSideBootstrap {
    private static final String CLIENT_INIT = "com.locknar.taczammocheck.tactical.client.ClientInit";

    private ClientSideBootstrap() {
    }

    public static void initModBus(IEventBus modEventBus) {
        ClientSideBootstrap.invoke("initModBus", new Class[]{IEventBus.class}, new Object[]{modEventBus});
    }

    public static void initForgeBus() {
        ClientSideBootstrap.invoke("init", new Class[0], new Object[0]);
    }

    private static void invoke(String methodName, Class<?>[] parameterTypes, Object[] args) {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }
        try {
            Class<?> clazz = Class.forName(CLIENT_INIT, true, ClientSideBootstrap.class.getClassLoader());
            Method method = clazz.getMethod(methodName, parameterTypes);
            method.invoke(null, args);
        }
        catch (Throwable throwable) {
            throw new IllegalStateException("Failed to initialize client side of locknar_doorkick", throwable);
        }
    }
}
