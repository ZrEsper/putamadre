package blackoutInteractive.ema_08_.rendering.geom;

import blackoutInteractive.ema_08_.rendering.geom.MatrixRTS;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public final class RTSMatricesCompound {
    public static final RTSMatricesCompound EMPTY = new RTSMatricesCompound(new HashMap<String, MatrixRTS>());
    public static final String key_armor_render = "ARMOR";
    public static final String key_workbench_render = "WORKBENCH";
    private final Map<String, MatrixRTS> matrices;

    public RTSMatricesCompound(Map<String, MatrixRTS> matrices) {
        matrices.entrySet().forEach(e -> {
            RTSMatricesCompound.validateKey((String)e.getKey());
            if (e.getValue() == null) {
                throw new IllegalArgumentException("Cannot map a null matrix");
            }
        });
        this.matrices = Collections.unmodifiableMap(new HashMap<String, MatrixRTS>(matrices));
    }

    private static String validateKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Invalid matrices compound key");
        }
        return key;
    }

    public boolean has(String key) {
        return this.matrices.get(RTSMatricesCompound.validateKey(key)) != null;
    }

    public MatrixRTS get(String key) {
        MatrixRTS matrix = this.matrices.get(RTSMatricesCompound.validateKey(key));
        if (matrix == null) {
            throw new IllegalArgumentException("No matrix under key " + key);
        }
        return matrix;
    }

    public void applyIfPresent(String key, PoseStack poseStack) {
        MatrixRTS matrix = this.matrices.get(RTSMatricesCompound.validateKey(key));
        if (matrix != null) {
            matrix.apply(poseStack);
        }
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof RTSMatricesCompound) {
            RTSMatricesCompound that = (RTSMatricesCompound)o;
            return this.matrices.equals(that.matrices);
        }
        return false;
    }

    public int hashCode() {
        return 17 * this.matrices.hashCode();
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{" + String.join((CharSequence)"; ", this.matrices.keySet()) + "}";
    }
}

