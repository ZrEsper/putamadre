/*
 * Decompiled with CFR 0.152.
 */
package dev.zomboidmelee.boxing;

import dev.zomboidmelee.boxing.Combat18;
import dev.zomboidmelee.boxing.R;
import dev.zomboidmelee.boxing.Stomp18;
import dev.zomboidmelee.combat.PanelCooldown;

public final class Boxing16 {
    public static boolean empty(Object object) {
        return object != null && R.yes(R.call(R.call(object, "getMainHandItem", new Object[0]), "isEmpty", new Object[0])) && R.yes(R.call(R.call(object, "getOffhandItem", new Object[0]), "isEmpty", new Object[0]));
    }

    public static float cooldown(float f, Object object) {
        return Boxing16.empty(object) ? PanelCooldown.vanilla(8.0f, object) : PanelCooldown.vanilla(f, object);
    }

    public static void hit(Object object) {
        long l;
        Object object2 = R.call(object, "getEntity", new Object[0]);
        if (R.yes(R.call(R.call(object2, "level", new Object[0]), "isClientSide", new Object[0])) || R.num(R.call(object, "getNewDamage", new Object[0])) <= 0.0) {
            return;
        }
        Object object3 = R.call(object, "getSource", new Object[0]);
        Object object4 = R.call(object3, "getEntity", new Object[0]);
        if (!(object4 != null && R.type("net.minecraft.server.level.ServerPlayer").isInstance(object4) && object4 == R.call(object3, "getDirectEntity", new Object[0]) && "player".equals(R.call(object3, "getMsgId", new Object[0])) && Boxing16.empty(object4))) {
            return;
        }
        if (!R.yes(R.call(R.type("dev.zomboidmelee.combat.ShoveCombat"), "zombie", object2))) {
            return;
        }
        Object object5 = R.call(object2, "getPersistentData", new Object[0]);
        long l2 = R.lng(R.call(R.call(object2, "level", new Object[0]), "getGameTime", new Object[0]));
        if (l2 >= (l = R.lng(R.call(object5, "getLong", "boxing19_stun_ready"))) && !Stomp18.down(object2)) {
            int n = Combat18.HEAVY.get() != false ? 7 : 3;
            long l3 = R.lng(R.call(object5, "getLong", "zpush_until"));
            R.call(object5, "putLong", "zpush_until", Math.max(l3, l2 + (long)n));
            R.call(object5, "putLong", "boxing19_stun_ready", l2 + 30L);
            R.call(R.call(object2, "getNavigation", new Object[0]), "stop", new Object[0]);
        }
        Stomp18.recoil(object2, object4);
    }
}
