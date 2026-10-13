package blackoutInteractive.ema_08_.rendering.geom;

import java.util.concurrent.ConcurrentHashMap;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class RotQuaternionPool {
    private static final ConcurrentHashMap<Long, Quaternionf> CACHE_POOL = new ConcurrentHashMap();

    private static float roundf(float f) {
        return (float)Math.round(f * 10000.0f) / 10000.0f;
    }

    private static long hash(float rrotX, float rrotY, float rrotZ) {
        int hx = Float.floatToIntBits(rrotX);
        int hy = Float.floatToIntBits(rrotY);
        int hz = Float.floatToIntBits(rrotZ);
        long hash = 17L;
        hash = hash * 31L + (long)hx;
        hash = hash * 31L + (long)hy;
        hash = hash * 31L + (long)hz;
        return hash;
    }

    public static boolean isCached(float rotX, float rotY, float rotZ) {
        return CACHE_POOL.get(RotQuaternionPool.hash(RotQuaternionPool.roundf(rotX), RotQuaternionPool.roundf(rotY), RotQuaternionPool.roundf(rotZ))) != null;
    }

    protected static Quaternionf retrieve(float rotX, float rotY, float rotZ) {
        float rrotX = RotQuaternionPool.roundf(rotX);
        float rrotY = RotQuaternionPool.roundf(rotY);
        float rrotZ = RotQuaternionPool.roundf(rotZ);
        return CACHE_POOL.computeIfAbsent(RotQuaternionPool.hash(rrotX, rrotY, rrotZ), key -> RotQuaternionPool.computeIndependent0(rrotX, rrotY, rrotZ));
    }

    protected static Quaternionf computeIndependent0(float rotX, float rotY, float rotZ) {
        return new Quaternionf((Quaternionfc)new Quaternionf().rotationZ((float)Math.toRadians(rotZ))).mul((Quaternionfc)new Quaternionf().rotationX((float)Math.toRadians(rotX))).mul((Quaternionfc)new Quaternionf().rotationY((float)Math.toRadians(rotY)));
    }

    public static void freeAll() {
        CACHE_POOL.clear();
    }
}

