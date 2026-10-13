package com.locknar.taczammocheck.client;

import com.locknar.taczammocheck.client.AmmoCheckBedrockAnimationPlayer;
import com.locknar.taczammocheck.client.AmmoCheckClient;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

public final class AmmoCheckHudAnchor {
    private static float[] pending;

    private AmmoCheckHudAnchor() {
    }

    public static void capture(Object model, PoseStack renderedPose, ItemStack gun, ItemDisplayContext context) {
        if (!AmmoCheckClient.shouldApplyNativeBedrockAmmoCheck(context, gun)) {
            return;
        }
        pending = null;
        try {
            boolean p90Seat;
            Map<?, ?> parts = AmmoCheckBedrockAnimationPlayer.getModelMap(model);
            if (parts == null) {
                return;
            }
            Object target = null;
            boolean bl = p90Seat = "p90".equals(AmmoCheckClient.getActiveNativePresetName()) && parts.containsKey("p90_body");
            if (p90Seat) {
                target = parts.get("p90_body").getClass().getMethod("getModelRenderer", new Class[0]).invoke(parts.get("p90_body"), new Object[0]);
            }
            for (String name : List.of("magazine", "magzine", "mag", "Mag", "cylinder", "cy_base", "drum", "front_part", "bullet_in_mag", "chamber", "lefthand", "left_hand", "righthand")) {
                Object wrapper;
                if (target == null && ((wrapper = parts.get(name)) == null || (target = wrapper.getClass().getMethod("getModelRenderer", new Class[0]).invoke(wrapper, new Object[0])) == null)) continue;
                break;
            }
            if (target == null) {
                return;
            }
            Class<?> type = target.getClass();
            Method parent = type.getMethod("getParent", new Class[0]);
            Method transform = type.getMethod("translateAndRotateAndScale", PoseStack.class);
            ArrayList<Object> path = new ArrayList<Object>();
            Object p = parent.invoke(target, new Object[0]);
            while (p != null && path.size() < 64) {
                path.add(p);
                p = parent.invoke(p, new Object[0]);
            }
            Collections.reverse(path);
            PoseStack pose = new PoseStack();
            pose.last().pose().set((Matrix4fc)renderedPose.last().pose());
            for (Object e : path) {
                transform.invoke(e, pose);
            }
            Matrix4f projection = new Matrix4f((Matrix4fc)RenderSystem.getProjectionMatrix()).mul((Matrix4fc)RenderSystem.getModelViewMatrix());
            if (p90Seat) {
                transform.invoke(target, pose);
                Vector4f vector4f = new Matrix4f((Matrix4fc)projection).mul((Matrix4fc)pose.last().pose()).transform(new Vector4f(0.0f, -0.125f, -0.25f, 1.0f));
                if (vector4f.w > 1.0E-4f) {
                    float x = (vector4f.x / vector4f.w + 1.0f) * 0.5f;
                    float y = (1.0f - vector4f.y / vector4f.w) * 0.5f;
                    if (Float.isFinite(x) && Float.isFinite(y)) {
                        pending = new float[]{x, y};
                    }
                }
                return;
            }
            Bounds bounds = new Bounds();
            AmmoCheckHudAnchor.collect(target, pose, projection, transform, bounds, 0);
            if (bounds.count > 0) {
                pending = new float[]{bounds.right, bounds.top};
            }
        }
        catch (ReflectiveOperationException | RuntimeException exception) {
            // empty catch block
        }
    }

    public static float[] consume() {
        float[] point = pending;
        pending = null;
        return point;
    }

    private static void collect(Object part, PoseStack pose, Matrix4f projection, Method transform, Bounds bounds, int depth) throws ReflectiveOperationException {
        if (depth > 64) {
            return;
        }
        Class<?> type = part.getClass();
        if (!type.getField("visible").getBoolean(part)) {
            return;
        }
        if (Math.abs(type.getField("xScale").getFloat(part) * type.getField("yScale").getFloat(part) * type.getField("zScale").getFloat(part)) < 1.0E-6f) {
            return;
        }
        pose.pushPose();
        try {
            transform.invoke(part, pose);
            Matrix4f matrix = new Matrix4f((Matrix4fc)projection).mul((Matrix4fc)pose.last().pose());
            for (Object cube : (Iterable)type.getField("cubes").get(part)) {
                Class<?> c = cube.getClass();
                Field minX = c.getField("minX");
                Field minY = c.getField("minY");
                Field minZ = c.getField("minZ");
                Field maxX = c.getField("maxX");
                Field maxY = c.getField("maxY");
                Field maxZ = c.getField("maxZ");
                float x0 = minX.getFloat(cube) / 16.0f;
                float y0 = minY.getFloat(cube) / 16.0f;
                float z0 = minZ.getFloat(cube) / 16.0f;
                float x1 = maxX.getFloat(cube) / 16.0f;
                float y1 = maxY.getFloat(cube) / 16.0f;
                float z1 = maxZ.getFloat(cube) / 16.0f;
                for (int corner = 0; corner < 8; ++corner) {
                    Vector4f point = matrix.transform(new Vector4f((corner & 1) == 0 ? x0 : x1, (corner & 2) == 0 ? y0 : y1, (corner & 4) == 0 ? z0 : z1, 1.0f));
                    if (point.w <= 1.0E-4f) continue;
                    float x = (point.x / point.w + 1.0f) * 0.5f;
                    float y = (1.0f - point.y / point.w) * 0.5f;
                    if (!Float.isFinite(x) || !Float.isFinite(y)) continue;
                    bounds.right = Math.max(bounds.right, x);
                    bounds.top = Math.min(bounds.top, y);
                    ++bounds.count;
                }
            }
            for (Object child : (Iterable)type.getField("children").get(part)) {
                AmmoCheckHudAnchor.collect(child, pose, projection, transform, bounds, depth + 1);
            }
        }
        finally {
            pose.popPose();
        }
    }

    private static final class Bounds {
        float right = Float.NEGATIVE_INFINITY;
        float top = Float.POSITIVE_INFINITY;
        int count;

        private Bounds() {
        }
    }
}
