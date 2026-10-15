package dev.zomboid.tacz;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.luaj.vm2.*;

/** Only guards the standard SPAS reload cache. Other Lua errors remain visible. */
public final class Reload18 {
    private record Access(Field id, Method cache) {}
    private static final ClassValue<Access> ACCESS = new ClassValue<>() {
        protected Access computeValue(Class<?> type) {
            try {
                Field id = type.getDeclaredField("gunId");
                id.setAccessible(true);
                return new Access(id, type.getMethod("getCachedScriptData"));
            } catch (ReflectiveOperationException e) { throw new IllegalStateException("TaCZ reload API changed", e); }
        }
    };
    public static boolean valid(LuaValue cache) {
        if (cache == null || !cache.istable()) return false;
        for (String key : new String[]{"reloaded_count", "needed_count", "interrupted_time"}) {
            LuaValue value = cache.get(key);
            if (value.type() != LuaValue.TNUMBER || !Double.isFinite(value.todouble())) return false;
        }
        double loaded = cache.get("reloaded_count").todouble(), needed = cache.get("needed_count").todouble();
        return loaded >= 0 && loaded == Math.floor(loaded) && needed >= 0 && needed == Math.floor(needed)
            && cache.get("interrupted_time").todouble() >= -1 && cache.get("is_tactical").isboolean();
    }
    public static Varargs invoke(LuaValue function, Varargs args) {
        Object api = args.arg1().touserdata();
        if (api != null) {
            Access access = ACCESS.get(api.getClass());
            try {
                if ("tacz:spas_12".equals(String.valueOf(access.id().get(api)))
                        && !valid((LuaValue) access.cache().invoke(api))) {
                    // ReloadState.StateType.NOT_RELOADING is ordinal 0 in the pinned base.
                    // Cancel; never synthesize elapsed state or grant ammunition.
                    return LuaValue.varargsOf(LuaValue.valueOf(0), LuaValue.valueOf(-1));
                }
            } catch (ReflectiveOperationException e) { throw new IllegalStateException("Cannot read TaCZ reload state", e); }
        }
        return function.invoke(args);
    }
}
