package com.locknar.taczammocheck.tactical.network;

import com.locknar.taczammocheck.tactical.LocknarDoorKickMod;
import java.lang.reflect.Method;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public final class ClientPacketDispatch {
    private static final String ENDPOINT = "com.locknar.taczammocheck.tactical.client.ClientPacketEndpoint";

    private ClientPacketDispatch() {
    }

    public static void dispatch(String methodName, Object packet) {
        if (FMLEnvironment.dist != Dist.CLIENT || methodName == null || packet == null) {
            return;
        }
        try {
            Class<?> endpoint = Class.forName(ENDPOINT, true, ClientPacketDispatch.class.getClassLoader());
            Method method = endpoint.getMethod(methodName, Object.class);
            method.invoke(null, packet);
        }
        catch (Throwable throwable) {
            LocknarDoorKickMod.LOGGER.error("Failed to dispatch client packet {}", (Object)methodName, (Object)throwable);
        }
    }
}
