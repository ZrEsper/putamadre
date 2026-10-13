package com.locknar.taczammocheck.tactical;

import com.locknar.taczammocheck.LocknarConfig;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class TaczGunCompat {
    private static boolean resolved;
    private static Class<?> iGunClass;
    private static Method getGunIdMethod;
    private static Method getIGunOrNullMethod;
    private static final int MAX_GUN_INFO_CACHE_SIZE = 512;
    private static final int MAX_METHOD_CACHE_SIZE = 256;
    private static final Map<String, CachedGunInfo> GUN_INFO_CACHE;
    private static final Map<String, CachedMethod> METHOD_CACHE;

    private TaczGunCompat() {
    }

    public static GunInfo getGunInfo(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        String cacheKey = TaczGunCompat.buildGunInfoCacheKey(stack);
        CachedGunInfo cached = GUN_INFO_CACHE.get(cacheKey);
        if (cached != null) {
            return cached.info();
        }
        String gunId = TaczGunCompat.getGunIdUncached(stack);
        GunInfo info = null;
        if (gunId != null && !gunId.isBlank()) {
            GunKind kind = TaczGunCompat.classify(gunId);
            info = new GunInfo(gunId, kind, TaczGunCompat.getLengthForKind(kind, gunId));
        }
        if (GUN_INFO_CACHE.size() > 512) {
            GUN_INFO_CACHE.clear();
        }
        GUN_INFO_CACHE.put(cacheKey, new CachedGunInfo(info));
        return info;
    }

    public static boolean isTaczGun(ItemStack stack) {
        return TaczGunCompat.getGunInfo(stack) != null;
    }

    public static String getGunId(ItemStack stack) {
        GunInfo info = TaczGunCompat.getGunInfo(stack);
        return info == null ? null : info.gunId();
    }

    public static void clearCaches() {
        GUN_INFO_CACHE.clear();
        METHOD_CACHE.clear();
    }

    private static String buildGunInfoCacheKey(ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        CompoundTag tag = com.locknar.taczammocheck.port.Tags.read(stack);
        int tagHash = tag == null ? 0 : tag.hashCode();
        return String.valueOf(itemId) + "|" + tagHash + "|75";
    }

    private static String getGunIdUncached(ItemStack stack) {
        String path;
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        String byApi = TaczGunCompat.getGunIdByApi(stack);
        if (byApi != null && !byApi.isBlank()) {
            return byApi;
        }
        String byNbt = TaczGunCompat.getGunIdByNbt(stack);
        if (byNbt != null && !byNbt.isBlank()) {
            return byNbt;
        }
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemId != null && "tacz".equals(itemId.getNamespace()) && ((path = itemId.getPath().toLowerCase(Locale.ROOT)).contains("gun") || path.contains("kinetic") || path.contains("energy"))) {
            return itemId.toString();
        }
        return null;
    }

    private static String getGunIdByApi(ItemStack stack) {
        String[] methodNames;
        String value;
        String value2;
        TaczGunCompat.resolveApi();
        Object iGun = TaczGunCompat.getIGunObject(stack);
        if (iGun != null && getGunIdMethod != null && (value2 = TaczGunCompat.invokeGunIdMethod(iGun, getGunIdMethod, stack)) != null && !value2.isBlank()) {
            return value2;
        }
        Item item = stack.getItem();
        if (item == null) {
            return null;
        }
        if (iGunClass != null && getGunIdMethod != null && iGunClass.isInstance(item) && (value = TaczGunCompat.invokeGunIdMethod(item, getGunIdMethod, stack)) != null && !value.isBlank()) {
            return value;
        }
        for (String methodName : methodNames = new String[]{"getGunId", "getGunID", "getGunResourceLocation", "getGunResource", "getGunDataId", "getGunDataID"}) {
            String withStack = TaczGunCompat.invokeGunIdMethodByName(item, methodName, stack, true);
            if (withStack != null && !withStack.isBlank()) {
                return withStack;
            }
            String noArg = TaczGunCompat.invokeGunIdMethodByName(item, methodName, stack, false);
            if (noArg == null || noArg.isBlank()) continue;
            return noArg;
        }
        return null;
    }

    private static Object getIGunObject(ItemStack stack) {
        TaczGunCompat.resolveApi();
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        Item item = stack.getItem();
        if (iGunClass != null && item != null && iGunClass.isInstance(item)) {
            return item;
        }
        if (getIGunOrNullMethod != null) {
            try {
                Object value = getIGunOrNullMethod.invoke(null, stack);
                if (value != null) {
                    return value;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return null;
    }

    private static void resolveApi() {
        String[] possibleIGunNames;
        if (resolved) {
            return;
        }
        resolved = true;
        for (String className : possibleIGunNames = new String[]{"com.tacz.guns.api.item.IGun", "com.tacz.guns.api.item.gun.IGun", "com.tacz.guns.api.item.gun.AbstractGunItem"}) {
            try {
                Class<?> clazz = Class.forName(className, false, TaczGunCompat.class.getClassLoader());
                iGunClass = clazz;
                getGunIdMethod = TaczGunCompat.findMethod(clazz, "getGunId", ItemStack.class);
                if (getGunIdMethod == null) {
                    getGunIdMethod = TaczGunCompat.findMethod(clazz, "getGunID", ItemStack.class);
                }
                getIGunOrNullMethod = TaczGunCompat.findStaticMethod(clazz, "getIGunOrNull", ItemStack.class);
                break;
            }
            catch (Throwable throwable) {
            }
        }
    }

    private static Method findMethod(Class<?> clazz, String name, Class<?> ... parameters) {
        try {
            Method method = clazz.getMethod(name, parameters);
            method.setAccessible(true);
            return method;
        }
        catch (Throwable ignored) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameters);
                method.setAccessible(true);
                return method;
            }
            catch (Throwable ignored2) {
                return null;
            }
        }
    }

    private static Method findStaticMethod(Class<?> clazz, String name, Class<?> ... parameters) {
        Method method = TaczGunCompat.findMethod(clazz, name, parameters);
        return method == null ? null : method;
    }

    private static String invokeGunIdMethod(Object item, Method method, ItemStack stack) {
        try {
            Object result = method.invoke(item, stack);
            return TaczGunCompat.normalizeGunIdResult(result);
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static String invokeGunIdMethodByName(Object item, String methodName, ItemStack stack, boolean withStack) {
        try {
            Method method = TaczGunCompat.getCachedMethod(item.getClass(), methodName, withStack);
            if (method == null) {
                return null;
            }
            Object result = withStack ? method.invoke(item, stack) : method.invoke(item, new Object[0]);
            return TaczGunCompat.normalizeGunIdResult(result);
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static Method getCachedMethod(Class<?> clazz, String methodName, boolean withStack) {
        if (clazz == null || methodName == null) {
            return null;
        }
        String key = clazz.getName() + "#" + methodName + "#" + withStack;
        CachedMethod cached = METHOD_CACHE.get(key);
        if (cached != null) {
            return cached.method();
        }
        Method method = null;
        try {
            method = withStack ? clazz.getMethod(methodName, ItemStack.class) : clazz.getMethod(methodName, new Class[0]);
            method.setAccessible(true);
        }
        catch (Throwable ignored) {
            try {
                method = withStack ? clazz.getDeclaredMethod(methodName, ItemStack.class) : clazz.getDeclaredMethod(methodName, new Class[0]);
                method.setAccessible(true);
            }
            catch (Throwable ignored2) {
                method = null;
            }
        }
        if (METHOD_CACHE.size() > 256) {
            METHOD_CACHE.clear();
        }
        METHOD_CACHE.put(key, new CachedMethod(method));
        return method;
    }

    private static String normalizeGunIdResult(Object result) {
        if (result == null) {
            return null;
        }
        if (result instanceof ResourceLocation) {
            ResourceLocation location = (ResourceLocation)result;
            return location.toString();
        }
        return String.valueOf(result);
    }

    private static String getGunIdByNbt(ItemStack stack) {
        CompoundTag tag = com.locknar.taczammocheck.port.Tags.read(stack);
        if (tag == null) {
            return null;
        }
        String direct = TaczGunCompat.getStringIfPresent(tag, "GunId", "gunId", "gun_id", "GunID", "Gun", "gun", "TaczGunId", "taczGunId");
        if (direct != null) {
            return direct;
        }
        return TaczGunCompat.scanForResourceLocationString(tag, 0);
    }

    private static String getStringIfPresent(CompoundTag tag, String ... keys) {
        for (String key : keys) {
            String value;
            if (!tag.contains(key, 8) || !TaczGunCompat.looksLikeGunId(value = tag.getString(key))) continue;
            return value;
        }
        return null;
    }

    private static String scanForResourceLocationString(CompoundTag tag, int depth) {
        if (tag == null || depth > 3) {
            return null;
        }
        for (String key : tag.getAllKeys()) {
            CompoundTag child;
            String nested;
            Tag value = tag.get(key);
            if (value == null) continue;
            if (value.getId() == 8) {
                String s = tag.getString(key);
                if (!TaczGunCompat.looksLikeGunId(s)) continue;
                return s;
            }
            if (!(value instanceof CompoundTag) || (nested = TaczGunCompat.scanForResourceLocationString(child = (CompoundTag)value, depth + 1)) == null) continue;
            return nested;
        }
        return null;
    }

    private static boolean looksLikeGunId(String value) {
        if (value == null) {
            return false;
        }
        String lower = value.toLowerCase(Locale.ROOT).trim();
        return lower.contains(":") && (lower.contains("tacz") || lower.contains("gun") || lower.contains("m4") || lower.contains("glock") || lower.contains("ak") || lower.contains("mp") || lower.contains("shotgun"));
    }

    public static GunKind classify(String gunId) {
        String id;
        String string = id = gunId == null ? "" : gunId.toLowerCase(Locale.ROOT);
        if (TaczGunCompat.matchesAny(id, LocknarConfig.keywordsForKind(GunKind.PISTOL))) {
            return GunKind.PISTOL;
        }
        if (TaczGunCompat.matchesAny(id, LocknarConfig.keywordsForKind(GunKind.SMG))) {
            return GunKind.SMG;
        }
        if (TaczGunCompat.matchesAny(id, LocknarConfig.keywordsForKind(GunKind.SHOTGUN))) {
            return GunKind.SHOTGUN;
        }
        if (TaczGunCompat.matchesAny(id, LocknarConfig.keywordsForKind(GunKind.SNIPER))) {
            return GunKind.SNIPER;
        }
        return GunKind.RIFLE;
    }

    public static double getLengthForKind(GunKind kind, String gunId) {
        return LocknarConfig.lengthForKind(kind);
    }

    private static boolean matchesAny(String id, String keywords) {
        String[] list;
        if (id == null || keywords == null || keywords.isBlank()) {
            return false;
        }
        for (String keyword : list = keywords.split(",")) {
            String k = keyword.trim().toLowerCase(Locale.ROOT);
            if (k.isEmpty() || !id.contains(k)) continue;
            return true;
        }
        return false;
    }

    static {
        GUN_INFO_CACHE = new ConcurrentHashMap<String, CachedGunInfo>();
        METHOD_CACHE = new ConcurrentHashMap<String, CachedMethod>();
    }

    private record CachedGunInfo(GunInfo info) {
    }

    public record GunInfo(String gunId, GunKind kind, double length) {
    }

    public static enum GunKind {
        PISTOL,
        SMG,
        RIFLE,
        SHOTGUN,
        SNIPER;

    }

    private record CachedMethod(Method method) {
    }
}
