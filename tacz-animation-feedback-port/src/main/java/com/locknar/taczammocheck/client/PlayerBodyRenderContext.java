package com.locknar.taczammocheck.client;

import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;

public final class PlayerBodyRenderContext {
    private static final ThreadLocal<Deque<Frame>> FRAMES = ThreadLocal.withInitial(ArrayDeque::new);

    private PlayerBodyRenderContext() {
    }

    public static void begin(AbstractClientPlayer player, PlayerModel<?> model) {
        FRAMES.get().push(new Frame(player, model));
    }

    public static boolean active(AbstractClientPlayer player, PlayerModel<?> model) {
        Frame frame = FRAMES.get().peek();
        return frame != null && frame.player == player && frame.model == model;
    }

    public static void end(AbstractClientPlayer player, PlayerModel<?> model) {
        Deque<Frame> frames = FRAMES.get();
        if (!PlayerBodyRenderContext.active(player, model)) {
            return;
        }
        frames.pop().restore();
        if (frames.isEmpty()) {
            FRAMES.remove();
        }
    }

    private static final class Frame {
        final AbstractClientPlayer player;
        final PlayerModel<?> model;
        final ModelPart[] parts;
        final float[][] poses;

        Frame(AbstractClientPlayer player, PlayerModel<?> model) {
            this.player = player;
            this.model = model;
            this.parts = new ModelPart[]{model.head, model.hat, model.body, model.jacket, model.leftArm, model.leftSleeve, model.rightArm, model.rightSleeve, model.leftLeg, model.leftPants, model.rightLeg, model.rightPants};
            this.poses = new float[this.parts.length][];
            for (int i = 0; i < this.parts.length; ++i) {
                ModelPart p = this.parts[i];
                this.poses[i] = new float[]{p.x, p.y, p.z, p.xRot, p.yRot, p.zRot, p.xScale, p.yScale, p.zScale};
            }
        }

        void restore() {
            for (int i = 0; i < this.parts.length; ++i) {
                ModelPart p = this.parts[i];
                float[] v = this.poses[i];
                p.x = v[0];
                p.y = v[1];
                p.z = v[2];
                p.xRot = v[3];
                p.yRot = v[4];
                p.zRot = v[5];
                p.xScale = v[6];
                p.yScale = v[7];
                p.zScale = v[8];
            }
        }
    }
}
