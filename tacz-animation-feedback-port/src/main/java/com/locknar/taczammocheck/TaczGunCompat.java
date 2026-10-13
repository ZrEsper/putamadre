package com.locknar.taczammocheck;

import com.locknar.taczammocheck.AmmoCheckCartridgeState;
import com.locknar.taczammocheck.LocknarConfig;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ItemCapability;

public final class TaczGunCompat {
    private static boolean resolved;
    private static Class<?> iGunClass;
    private static Method getGunIdMethod;
    private static Method getIGunOrNullMethod;
    private static Method getCurrentAmmoCountMethod;
    private static Method setCurrentAmmoCountMethod;
    private static final int MAX_GUN_INFO_CACHE_SIZE = 512;
    private static final int MAX_METHOD_CACHE_SIZE = 256;
    private static final Map<String, CachedGunInfo> GUN_INFO_CACHE;
    private static final Map<String, CachedMethod> METHOD_CACHE;
    private static final Map<String, Integer> OBSERVED_AMMO_CAPACITY;

    private TaczGunCompat() {
    }

    public static boolean isAmmoCheckDisabled(String gunId) {
        if (gunId == null) {
            return false;
        }
        String path = gunId.substring(gunId.indexOf(58) + 1).toLowerCase(Locale.ROOT).replace("_", "").replace("-", "");
        return path.equals("minigun") || path.equals("m134") || path.equals("rpg7");
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
            info = new GunInfo(gunId, kind, 0.0);
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

    public static int getCurrentAmmoCount(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return -1;
        }
        int byApi = TaczGunCompat.findCurrentAmmoByApi(stack);
        if (byApi >= 0) {
            return byApi;
        }
        return TaczGunCompat.findCurrentAmmoFromNbt(com.locknar.taczammocheck.port.Tags.read(stack));
    }

    public static AmmoCheckCartridgeState getCartridgeState(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return AmmoCheckCartridgeState.UNKNOWN;
        }
        Boolean chamberLoaded = null;
        Object gun = TaczGunCompat.getIGunObject(stack);
        if (gun != null) {
            try {
                Object object;
                Method method = TaczGunCompat.getCachedMethod(gun.getClass(), "hasBulletInBarrel", true);
                if (method != null && (object = method.invoke(gun, stack)) instanceof Boolean) {
                    Boolean value;
                    chamberLoaded = value = (Boolean)object;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return new AmmoCheckCartridgeState(TaczGunCompat.getCurrentAmmoCount(stack), chamberLoaded);
    }

    public static boolean setCurrentAmmoCount(ItemStack stack, int count) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Object iGun = TaczGunCompat.getIGunObject(stack);
        if (iGun == null) {
            return false;
        }
        int safeCount = Math.max(0, count);
        if (setCurrentAmmoCountMethod != null) {
            try {
                setCurrentAmmoCountMethod.invoke(iGun, stack, safeCount);
                return true;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        try {
            Method method = iGun.getClass().getMethod("setCurrentAmmoCount", ItemStack.class, Integer.TYPE);
            method.setAccessible(true);
            method.invoke(iGun, stack, safeCount);
            return true;
        }
        catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean setChamberLoaded(ItemStack stack, boolean loaded) {
        Object gun = TaczGunCompat.getIGunObject(stack);
        if (gun == null) {
            return false;
        }
        try {
            gun.getClass().getMethod("setBulletInBarrel", ItemStack.class, Boolean.TYPE).invoke(gun, stack, loaded);
            return true;
        }
        catch (ReflectiveOperationException e) {
            return false;
        }
    }

    public static AmmoInfo getAmmoInfo(ItemStack stack) {
        boolean hasNbtCapacity;
        GunInfo info = TaczGunCompat.getGunInfo(stack);
        if (info == null) {
            return null;
        }
        CompoundTag tag = com.locknar.taczammocheck.port.Tags.read(stack);
        int currentByApi = TaczGunCompat.findCurrentAmmoByApi(stack);
        int current = currentByApi >= 0 ? currentByApi : TaczGunCompat.findCurrentAmmoFromNbt(tag);
        Integer customCapacity = TaczGunCompat.getCustomCapacity(info.gunId());
        int apiCapacity = TaczGunCompat.findInstalledMagazineCapacity(stack);
        if (apiCapacity <= 0) {
            apiCapacity = TaczGunCompat.findAmmoCapacityByApi(stack, info.gunId());
        }
        int nbtCapacity = TaczGunCompat.findAmmoCapacityFromNbt(tag);
        if (apiCapacity > 0) {
            nbtCapacity = apiCapacity;
        }
        boolean hasExactCapacity = customCapacity != null && customCapacity > 0;
        boolean bl = hasNbtCapacity = nbtCapacity > 0;
        int capacity = hasExactCapacity ? customCapacity : (hasNbtCapacity ? nbtCapacity : TaczGunCompat.getFallbackCapacity(info));
        capacity = Math.max(1, Math.min(999, capacity));
        if (current >= 0) {
            String observedKey;
            current = Math.max(0, Math.min(999, current));
            String string = observedKey = info.gunId() == null || info.gunId().isBlank() ? "default" : info.gunId().toLowerCase(Locale.ROOT);
            if (current > 0) {
                OBSERVED_AMMO_CAPACITY.merge(observedKey, current, Math::max);
            }
            Integer observedCapacity = OBSERVED_AMMO_CAPACITY.get(observedKey);
            if (!hasExactCapacity && !hasNbtCapacity && observedCapacity != null && observedCapacity > 0) {
                capacity = observedCapacity;
            }
        } else {
            current = capacity;
        }
        current = Math.max(0, Math.min(capacity, current));
        return new AmmoInfo(current, capacity);
    }

    public static void clearCaches() {
        GUN_INFO_CACHE.clear();
        METHOD_CACHE.clear();
        OBSERVED_AMMO_CAPACITY.clear();
    }

    private static String buildGunInfoCacheKey(ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        CompoundTag tag = com.locknar.taczammocheck.port.Tags.read(stack);
        int tagHash = tag == null ? 0 : tag.hashCode();
        return String.valueOf(itemId) + "|" + tagHash + "|" + TaczGunCompat.configFingerprint();
    }

    private static int configFingerprint() {
        return 100;
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
        }
        catch (Throwable ignored) {
            method = null;
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
        String value = result.toString();
        if (TaczGunCompat.looksLikeGunId(value)) {
            return value;
        }
        return null;
    }

    private static void resolveApi() {
        String[] possibleInterfaceNames;
        if (resolved) {
            return;
        }
        resolved = true;
        for (String className : possibleInterfaceNames = new String[]{"com.tacz.guns.api.item.IGun", "com.tacz.guns.api.item.gun.IGun", "com.tacz.guns.api.item.gun.IGunItem"}) {
            try {
                Class<?> clazz = Class.forName(className, false, TaczGunCompat.class.getClassLoader());
                Method gunId = TaczGunCompat.findMethod(clazz, "getGunId", ItemStack.class);
                if (gunId == null) continue;
                iGunClass = clazz;
                getGunIdMethod = gunId;
                getCurrentAmmoCountMethod = TaczGunCompat.findMethod(clazz, "getCurrentAmmoCount", ItemStack.class);
                setCurrentAmmoCountMethod = TaczGunCompat.findMethod(clazz, "setCurrentAmmoCount", ItemStack.class, Integer.TYPE);
                getIGunOrNullMethod = TaczGunCompat.findStaticMethod(clazz, "getIGunOrNull", ItemStack.class);
                return;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        iGunClass = null;
        getGunIdMethod = null;
        getCurrentAmmoCountMethod = null;
        setCurrentAmmoCountMethod = null;
        getIGunOrNullMethod = null;
    }

    private static Method findMethod(Class<?> clazz, String name, Class<?> ... params) {
        if (clazz == null || name == null) {
            return null;
        }
        try {
            Method method = clazz.getMethod(name, params);
            method.setAccessible(true);
            return method;
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static Method findStaticMethod(Class<?> clazz, String name, Class<?> ... params) {
        Method method = TaczGunCompat.findMethod(clazz, name, params);
        if (method == null) {
            return null;
        }
        try {
            if ((method.getModifiers() & 8) == 0) {
                return null;
            }
        }
        catch (Throwable ignored) {
            return null;
        }
        return method;
    }

    private static int findCurrentAmmoByApi(ItemStack stack) {
        String[] methodNames;
        Object iGun = TaczGunCompat.getIGunObject(stack);
        if (iGun == null) {
            return -1;
        }
        if (getCurrentAmmoCountMethod != null) {
            try {
                Object value = getCurrentAmmoCountMethod.invoke(iGun, stack);
                if (value instanceof Number) {
                    Number number = (Number)value;
                    return Math.max(0, number.intValue());
                }
            }
            catch (Throwable value) {
                // empty catch block
            }
        }
        for (String name : methodNames = new String[]{"getCurrentAmmoCount", "getCurrentAmmo", "getAmmoCount", "getAmmo", "getMagazineAmmo", "getCurrentMagazineAmmo"}) {
            int value = TaczGunCompat.invokeIntMethod(iGun, name, stack, true);
            if (value >= 0) {
                return value;
            }
            value = TaczGunCompat.invokeIntMethod(iGun, name, stack, false);
            if (value < 0) continue;
            return value;
        }
        return -1;
    }

    private static int findInstalledMagazineCapacity(ItemStack stack) {
        try {
            ItemStack magazine;
            ClassLoader loader = TaczGunCompat.class.getClassLoader();
            Class<?> provider = Class.forName("com.raiiiden.taczmagazines.capability.GunMagazineProvider", false, loader);
            Object value = provider.getField("GUN_MAGAZINE").get(null);
            if (!(value instanceof ItemCapability)) {
                return -1;
            }
            ItemCapability capability = (ItemCapability)value;
            Object holder = stack.getCapability(capability);
            Object stored = TaczGunCompat.invokeNoArg(holder, "getStoredMagazine");
            if (!(stored instanceof ItemStack) || (magazine = (ItemStack)stored).isEmpty()) {
                return -1;
            }
            Class<?> item = Class.forName("com.raiiiden.taczmagazines.item.MagazineItem", false, loader);
            if (!item.isInstance(magazine.getItem())) {
                return -1;
            }
            return ((Number)item.getMethod("getMaxCapacity", ItemStack.class).invoke(null, magazine)).intValue();
        }
        catch (LinkageError | ReflectiveOperationException | RuntimeException ignored) {
            return -1;
        }
    }

    private static int findAmmoCapacityByApi(ItemStack stack, String gunId) {
        block8: {
            if (gunId == null || gunId.isBlank()) {
                return -1;
            }
            try {
                int base;
                Class<?> timelessApi = Class.forName("com.tacz.guns.api.TimelessAPI", false, TaczGunCompat.class.getClassLoader());
                Method getCommonGunIndex = timelessApi.getMethod("getCommonGunIndex", ResourceLocation.class);
                Object optional = getCommonGunIndex.invoke(null, ResourceLocation.parse(gunId));
                Object commonGunIndex = TaczGunCompat.optionalValue(optional);
                if (commonGunIndex == null) break block8;
                Object gunData = TaczGunCompat.invokeNoArg(commonGunIndex, "getGunData");
                if (gunData != null) {
                    try {
                        Class<?> utility = Class.forName("com.tacz.guns.util.AttachmentDataUtils", false, TaczGunCompat.class.getClassLoader());
                        int extended = ((Number)utility.getMethod("getAmmoCountWithAttachment", ItemStack.class, gunData.getClass()).invoke(null, stack, gunData)).intValue();
                        if (extended > 0) {
                            return extended;
                        }
                    }
                    catch (ReflectiveOperationException utility) {
                        // empty catch block
                    }
                }
                if ((base = TaczGunCompat.invokeIntNoArg(gunData, "getAmmoAmount")) > 0) {
                    return base;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return -1;
    }

    private static Object optionalValue(Object optional) {
        if (optional == null) {
            return null;
        }
        try {
            Boolean b;
            Method isPresent = optional.getClass().getMethod("isPresent", new Class[0]);
            Object present = isPresent.invoke(optional, new Object[0]);
            if (!(present instanceof Boolean) || !(b = (Boolean)present).booleanValue()) {
                return null;
            }
            Method get = optional.getClass().getMethod("get", new Class[0]);
            return get.invoke(optional, new Object[0]);
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static Object invokeNoArg(Object target, String name) {
        if (target == null || name == null) {
            return null;
        }
        try {
            Method method = target.getClass().getMethod(name, new Class[0]);
            method.setAccessible(true);
            return method.invoke(target, new Object[0]);
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static int invokeIntNoArg(Object target, String name) {
        Object value = TaczGunCompat.invokeNoArg(target, name);
        if (value instanceof Number) {
            Number number = (Number)value;
            return number.intValue();
        }
        return -1;
    }

    private static int invokeIntMethod(Object target, String methodName, ItemStack stack, boolean withStack) {
        if (target == null || methodName == null) {
            return -1;
        }
        try {
            Object value;
            Method method = TaczGunCompat.getCachedMethod(target.getClass(), methodName, withStack);
            if (method == null) {
                return -1;
            }
            Object object = value = withStack ? method.invoke(target, stack) : method.invoke(target, new Object[0]);
            if (value instanceof Number) {
                Number number = (Number)value;
                return number.intValue();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return -1;
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
        ResourceLocation location;
        if (value == null) {
            return false;
        }
        String lower = value.toLowerCase(Locale.ROOT).trim();
        if (!lower.contains(":")) {
            return false;
        }
        try {
            location = ResourceLocation.tryParse((String)lower);
        }
        catch (Throwable ignored) {
            location = null;
        }
        if (location == null || location.getNamespace().isBlank() || location.getPath().isBlank()) {
            return false;
        }
        String namespace = location.getNamespace();
        return !"minecraft".equals(namespace) && !"forge".equals(namespace);
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
        return 0.0;
    }

    private static int getFallbackCapacity(GunInfo info) {
        Integer custom = TaczGunCompat.getCustomCapacity(info.gunId());
        if (custom != null && custom > 0) {
            return custom;
        }
        return LocknarConfig.fallbackCapacityForKind(info.kind());
    }

    private static Integer getCustomCapacity(String gunId) {
        return null;
    }

    private static int findCurrentAmmoFromNbt(CompoundTag tag) {
        if (tag == null) {
            return -1;
        }
        String[] directKeys = new String[]{"GunCurrentAmmoCount", "CurrentAmmoCount", "currentAmmoCount", "current_ammo_count", "CurrentAmmo", "currentAmmo", "current_ammo", "AmmoCount", "ammoCount", "ammo_count", "Ammo", "ammo", "BulletCount", "bulletCount", "Bullets", "bullets", "MagazineAmmo", "magazineAmmo", "magazine_ammo", "RemainingAmmo", "remainingAmmo"};
        int direct = TaczGunCompat.findIntIfPresent(tag, directKeys);
        if (direct >= 0) {
            return direct;
        }
        return TaczGunCompat.scanForAmmoInt(tag, 0, true);
    }

    private static int findAmmoCapacityFromNbt(CompoundTag tag) {
        if (tag == null) {
            return -1;
        }
        String[] directKeys = new String[]{"GunAmmoCapacity", "AmmoCapacity", "ammoCapacity", "ammo_capacity", "MaxAmmo", "maxAmmo", "max_ammo", "MaxAmmoCount", "maxAmmoCount", "max_ammo_count", "MagazineCapacity", "magazineCapacity", "magazine_capacity", "MagazineSize", "magazineSize", "MagSize", "magSize", "ClipSize", "clipSize", "Capacity", "capacity"};
        int direct = TaczGunCompat.findIntIfPresent(tag, directKeys);
        if (direct > 0) {
            return direct;
        }
        return TaczGunCompat.scanForAmmoInt(tag, 0, false);
    }

    private static int findIntIfPresent(CompoundTag tag, String ... keys) {
        for (String key : keys) {
            Tag value = tag.get(key);
            if (!(value instanceof NumericTag)) continue;
            NumericTag numeric = (NumericTag)value;
            return numeric.getAsInt();
        }
        return -1;
    }

    private static int scanForAmmoInt(CompoundTag tag, int depth, boolean current) {
        if (tag == null || depth > 3) {
            return -1;
        }
        for (String key : tag.getAllKeys()) {
            CompoundTag child;
            int nested;
            Tag value = tag.get(key);
            if (value == null) continue;
            String lowerKey = key.toLowerCase(Locale.ROOT);
            if (value instanceof NumericTag) {
                NumericTag numeric = (NumericTag)value;
                if (TaczGunCompat.looksLikeAmmoNumberKey(lowerKey, current)) {
                    int number = numeric.getAsInt();
                    if (!(current ? number >= 0 : number > 0)) continue;
                    return number;
                }
            }
            if (!(value instanceof CompoundTag) || (nested = TaczGunCompat.scanForAmmoInt(child = (CompoundTag)value, depth + 1, current)) < 0) continue;
            return nested;
        }
        return -1;
    }

    private static boolean looksLikeAmmoNumberKey(String lowerKey, boolean current) {
        boolean ammoLike;
        if (lowerKey == null) {
            return false;
        }
        boolean bl = ammoLike = lowerKey.contains("ammo") || lowerKey.contains("bullet") || lowerKey.contains("magazine") || lowerKey.equals("mag") || lowerKey.startsWith("mag_") || lowerKey.endsWith("_mag") || lowerKey.contains("clip");
        if (!ammoLike) {
            return false;
        }
        if (current) {
            return !lowerKey.contains("max") && !lowerKey.contains("capacity") && !lowerKey.contains("size");
        }
        return lowerKey.contains("max") || lowerKey.contains("capacity") || lowerKey.contains("size");
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
        OBSERVED_AMMO_CAPACITY = new ConcurrentHashMap<String, Integer>();
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

    public record AmmoInfo(int current, int capacity) {
        public float ratio() {
            return this.capacity <= 0 ? 0.0f : Math.max(0.0f, Math.min(1.0f, (float)this.current / (float)this.capacity));
        }
    }

    private record CachedMethod(Method method) {
    }
}
