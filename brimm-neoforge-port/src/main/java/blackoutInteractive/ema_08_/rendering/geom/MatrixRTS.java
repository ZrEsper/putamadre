package blackoutInteractive.ema_08_.rendering.geom;

import blackoutInteractive.ema_08_.rendering.geom.RotQuaternionPool;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.joml.Quaternionf;

public abstract class MatrixRTS {
    public static final MatrixRTS IDENTITY = new M_ID();
    public final float trX;
    public final float trY;
    public final float trZ;
    public final float rtX;
    public final float rtY;
    public final float rtZ;
    public final float scX;
    public final float scY;
    public final float scZ;

    public static MatrixRTS getMatrix(float translateX, float translateY, float translateZ, float rotateX, float rotateY, float rotateZ, float scaleX, float scaleY, float scaleZ, boolean pooled) {
        boolean scales;
        boolean translates = translateX != 0.0f || translateY != 0.0f || translateZ != 0.0f;
        boolean rotates = rotateX != 0.0f || rotateY != 0.0f || rotateZ != 0.0f;
        boolean bl = scales = scaleX != 1.0f || scaleY != 1.0f || scaleZ != 1.0f;
        if (!(translates || rotates || scales)) {
            return IDENTITY;
        }
        if (translates && rotates && scales) {
            return new M_RTS(translateX, translateY, translateZ, rotateX, rotateY, rotateZ, scaleX, scaleY, scaleZ, pooled);
        }
        if (translates && !rotates && !scales) {
            return new M_T(translateX, translateY, translateZ);
        }
        if (rotates && !translates && !scales) {
            return new M_R(rotateX, rotateY, rotateZ, pooled);
        }
        if (scales && !translates && !rotates) {
            return new M_S(scaleX, scaleY, scaleZ);
        }
        if (!translates) {
            return new M_RS(rotateX, rotateY, rotateZ, scaleX, scaleY, scaleZ, pooled);
        }
        if (!rotates) {
            return new M_TS(translateX, translateY, translateZ, scaleX, scaleY, scaleZ);
        }
        if (!scales) {
            return new M_RT(translateX, translateY, translateZ, rotateX, rotateY, rotateZ, pooled);
        }
        throw new IllegalStateException("Could not resolve matrix [" + String.join((CharSequence)"; ", Float.toString(translateX), Float.toString(translateY), Float.toString(translateZ), Float.toString(rotateX), Float.toString(rotateY), Float.toString(rotateZ), Float.toString(scaleX), Float.toString(scaleY), Float.toString(scaleZ)) + "]");
    }

    protected MatrixRTS(float translateX, float translateY, float translateZ, float rotateX, float rotateY, float rotateZ, float scaleX, float scaleY, float scaleZ) {
        this.trX = translateX;
        this.trY = translateY;
        this.trZ = translateZ;
        this.rtX = rotateX;
        this.rtY = rotateY;
        this.rtZ = rotateZ;
        this.scX = scaleX;
        this.scY = scaleY;
        this.scZ = scaleZ;
    }

    public abstract void apply(@NonNull PoseStack var1);

    public final boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof MatrixRTS) {
            MatrixRTS that = (MatrixRTS)o;
            return this.trX == that.trX && this.trY == that.trY && this.trZ == that.trZ && this.rtX == that.rtX && this.rtY == that.rtY && this.rtZ == that.rtZ && this.scX == that.scX && this.scY == that.scY && this.scZ == that.scZ;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Float.valueOf(this.trX), Float.valueOf(this.trY), Float.valueOf(this.trZ), Float.valueOf(this.rtX), Float.valueOf(this.rtY), Float.valueOf(this.rtZ), Float.valueOf(this.scX), Float.valueOf(this.scY), Float.valueOf(this.scZ));
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());
        sb.append("{");
        sb.append("trX=").append(this.trX).append("; ");
        sb.append("trY=").append(this.trY).append("; ");
        sb.append("trZ=").append(this.trZ).append("; ");
        sb.append("rtX=").append(this.rtX).append("; ");
        sb.append("rtY=").append(this.rtY).append("; ");
        sb.append("rtZ=").append(this.rtZ).append("; ");
        sb.append("scX=").append(this.scX).append("; ");
        sb.append("scY=").append(this.scY).append("; ");
        sb.append("scZ=").append(this.scZ);
        sb.append("}");
        return sb.toString();
    }

    private static final class M_RTS
    extends M_ABS_R {
        protected M_RTS(float translateX, float translateY, float translateZ, float rotateX, float rotateY, float rotateZ, float scaleX, float scaleY, float scaleZ, boolean pooled) {
            super(translateX, translateY, translateZ, rotateX, rotateY, rotateZ, scaleX, scaleY, scaleZ, pooled);
        }

        @Override
        public void apply(@NonNull PoseStack poseStack) {
            poseStack.translate(this.trX, this.trY, this.trZ);
            poseStack.mulPose(this.rot);
            poseStack.scale(this.scX, this.scY, this.scZ);
        }
    }

    private static final class M_T
    extends MatrixRTS {
        protected M_T(float translateX, float translateY, float translateZ) {
            super(translateX, translateY, translateZ, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        }

        @Override
        public void apply(@NonNull PoseStack poseStack) {
            poseStack.translate(this.trX, this.trY, this.trZ);
        }
    }

    private static final class M_R
    extends M_ABS_R {
        protected M_R(float rotateX, float rotateY, float rotateZ, boolean pooled) {
            super(0.0f, 0.0f, 0.0f, rotateX, rotateY, rotateZ, 1.0f, 1.0f, 1.0f, pooled);
        }

        @Override
        public void apply(@NonNull PoseStack poseStack) {
            poseStack.mulPose(this.rot);
        }
    }

    private static final class M_S
    extends MatrixRTS {
        protected M_S(float scaleX, float scaleY, float scaleZ) {
            super(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, scaleX, scaleY, scaleZ);
        }

        @Override
        public void apply(@NonNull PoseStack poseStack) {
            poseStack.scale(this.scX, this.scY, this.scZ);
        }
    }

    private static final class M_RS
    extends M_ABS_R {
        protected M_RS(float rotateX, float rotateY, float rotateZ, float scaleX, float scaleY, float scaleZ, boolean pooled) {
            super(0.0f, 0.0f, 0.0f, rotateX, rotateY, rotateZ, scaleX, scaleY, scaleZ, pooled);
        }

        @Override
        public void apply(@NonNull PoseStack poseStack) {
            poseStack.mulPose(this.rot);
            poseStack.scale(this.scX, this.scY, this.scZ);
        }
    }

    private static final class M_TS
    extends MatrixRTS {
        protected M_TS(float translateX, float translateY, float translateZ, float scaleX, float scaleY, float scaleZ) {
            super(translateX, translateY, translateZ, 0.0f, 0.0f, 0.0f, scaleX, scaleY, scaleZ);
        }

        @Override
        public void apply(@NonNull PoseStack poseStack) {
            poseStack.translate(this.trX, this.trY, this.trZ);
            poseStack.scale(this.scX, this.scY, this.scZ);
        }
    }

    private static final class M_RT
    extends M_ABS_R {
        protected M_RT(float translateX, float translateY, float translateZ, float rotateX, float rotateY, float rotateZ, boolean pooled) {
            super(translateX, translateY, translateZ, rotateX, rotateY, rotateZ, 1.0f, 1.0f, 1.0f, pooled);
        }

        @Override
        public void apply(@NonNull PoseStack poseStack) {
            poseStack.translate(this.trX, this.trY, this.trZ);
            poseStack.mulPose(this.rot);
        }
    }

    private static final class M_ID
    extends MatrixRTS {
        protected M_ID() {
            super(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
            if (IDENTITY != null) {
                throw new IllegalStateException();
            }
        }

        @Override
        public void apply(@NonNull PoseStack poseStack) {
        }
    }

    private static abstract class M_ABS_R
    extends MatrixRTS {
        protected final Quaternionf rot;

        protected M_ABS_R(float trX, float trY, float trZ, float rtX, float rtY, float rtZ, float scX, float scY, float scZ, boolean pooled) {
            super(trX, trY, trZ, rtX, rtY, rtZ, scX, scY, scZ);
            this.rot = pooled ? RotQuaternionPool.retrieve(rtX, rtY, rtZ) : RotQuaternionPool.computeIndependent0(rtX, rtY, rtZ);
        }
    }
}

