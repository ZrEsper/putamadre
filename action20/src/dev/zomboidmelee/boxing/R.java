/*
 * Decompiled with CFR 0.152.
 */
package dev.zomboidmelee.boxing;

import dev.zomboidmelee.boxing.FastFields;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class R {
    private static final Map<String, Method> METHODS = new ConcurrentHashMap<String, Method>();

    public static Class<?> type(String string) {
        return FastFields.type(string);
    }

    public static Object call(Object object2, String string, Object ... objectArray) {
        try {
            Class<?> clazz = object2 instanceof Class ? (Class<?>)object2 : object2.getClass();
            String string2 = clazz.getName() + "#" + string + Arrays.toString(Arrays.stream(objectArray).map(object -> object == null ? null : object.getClass()).toArray());
            Method method = METHODS.get(string2);
            if (method == null) {
                ArrayList<Method> arrayList = new ArrayList<Method>(Arrays.asList(clazz.getMethods()));
                for (Class<?> clazz2 = clazz; clazz2 != null; clazz2 = clazz2.getSuperclass()) {
                    arrayList.addAll(Arrays.asList(clazz2.getDeclaredMethods()));
                }
                block4: for (Method method2 : arrayList) {
                    if (!method2.getName().equals(string) || method2.getParameterCount() != objectArray.length) continue;
                    Class<?>[] classArray = method2.getParameterTypes();
                    for (int i = 0; i < objectArray.length; ++i) {
                        Class<?> clazz3 = classArray[i];
                        if (clazz3.isPrimitive()) {
                            clazz3 = R.box(clazz3);
                        }
                        if (objectArray[i] == null ? classArray[i].isPrimitive() : !clazz3.isInstance(objectArray[i])) continue block4;
                    }
                    method = method2;
                    method.setAccessible(true);
                    METHODS.put(string2, method);
                    break;
                }
                if (method == null) {
                    throw new NoSuchMethodException(string2);
                }
            }
            return method.invoke(object2 instanceof Class ? null : object2, objectArray);
        }
        catch (InvocationTargetException invocationTargetException) {
            throw new IllegalStateException(string, invocationTargetException.getCause());
        }
        catch (Exception exception) {
            throw new IllegalStateException(string, exception);
        }
    }

    private static Class<?> box(Class<?> clazz) {
        if (clazz == Integer.TYPE) {
            return Integer.class;
        }
        if (clazz == Long.TYPE) {
            return Long.class;
        }
        if (clazz == Float.TYPE) {
            return Float.class;
        }
        if (clazz == Double.TYPE) {
            return Double.class;
        }
        if (clazz == Boolean.TYPE) {
            return Boolean.class;
        }
        return clazz;
    }

    public static Object get(Object object, String string) {
        return FastFields.get(object, string);
    }

    public static Object make(String string, Object ... objectArray) {
        try {
            for (Constructor<?> constructor : R.type(string).getDeclaredConstructors()) {
                if (constructor.getParameterCount() != objectArray.length) continue;
                Class<?>[] classArray = constructor.getParameterTypes();
                boolean bl = true;
                for (int i = 0; i < objectArray.length; ++i) {
                    if (!(objectArray[i] == null ? classArray[i].isPrimitive() : !R.box(classArray[i]).isInstance(objectArray[i]))) continue;
                    bl = false;
                }
                if (!bl) continue;
                constructor.setAccessible(true);
                return constructor.newInstance(objectArray);
            }
            throw new NoSuchMethodException(string);
        }
        catch (Exception exception) {
            throw new IllegalStateException(string, exception);
        }
    }

    public static void set(Object object, String string, Object object2) {
        FastFields.set(object, string, object2);
    }

    public static double num(Object object) {
        return ((Number)object).doubleValue();
    }

    public static long lng(Object object) {
        return ((Number)object).longValue();
    }

    public static boolean yes(Object object) {
        return Boolean.TRUE.equals(object);
    }

    public static double coord(Object object, String string) {
        return R.num(R.call(object, "get" + string, new Object[0]));
    }

    public static void warn(String string, Throwable throwable) {
        System.err.println("[iso-tacz] " + string + ": " + String.valueOf(throwable));
        throwable.printStackTrace(System.err);
    }
}
