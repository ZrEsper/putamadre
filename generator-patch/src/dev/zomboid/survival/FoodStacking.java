package dev.zomboid.survival;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/** Used only at merge sites: synchronization and general item equality remain strict. */
public final class FoodStacking {
    private static final double TOLERANCE = 200; // 10 seconds at 20 TPS
    private static final String[] CLOCK_FIELDS = {"left", "last", "rate", "frozen", "thaw", "chill", "cold"};

    private static void withoutClock(ItemStack stack) {
        CompoundTag root = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        root.remove(FoodClock.KEY);
        if (root.isEmpty()) stack.remove(DataComponents.CUSTOM_DATA);
        else stack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
    }
    private static Freshness.State state(CompoundTag tag, long now) {
        Freshness.State previous = new Freshness.State(tag.contains("left") ? tag.getDouble("left") : FoodClock.LIFE,
                tag.contains("last") ? tag.getLong("last") : now,
                Math.max(1, tag.getInt("rate")), tag.getBoolean("frozen"), tag.getDouble("thaw"),
                tag.getInt("chill"), tag.getInt("cold"));
        return Freshness.advance(previous, now, previous.rate(), false, true);
    }
    public static boolean mergeable(ItemStack first, ItemStack second) {
        if (ItemStack.isSameItemSameComponents(first, second)) return true;
        if (first.isEmpty() || second.isEmpty() || !ItemStack.isSameItem(first, second)
                || !FoodClock.perishable(first) || !FoodClock.perishable(second)) return false;
        ItemStack a = first.copy(), b = second.copy();
        withoutClock(a); withoutClock(b);
        if (!ItemStack.isSameItemSameComponents(a, b)) return false;
        CompoundTag ta = FoodClock.data(first), tb = FoodClock.data(second);
        CompoundTag extraA = ta.copy(), extraB = tb.copy();
        for (String field : CLOCK_FIELDS) { extraA.remove(field); extraB.remove(field); }
        if (!extraA.equals(extraB)) return false;
        long now = Math.max(ta.getLong("last"), tb.getLong("last"));
        Freshness.State sa = state(ta, now), sb = state(tb, now);
        if (!Double.isFinite(sa.left()) || !Double.isFinite(sb.left()) || sa.left() <= 0 || sb.left() <= 0
                || Math.abs(sa.left() - sb.left()) > TOLERANCE || sa.frozen() != sb.frozen()
                || (sa.cold() > 0) != (sb.cold() > 0) || Math.abs(sa.cold() - sb.cold()) > TOLERANCE
                || Math.abs(sa.thaw() - sb.thaw()) > TOLERANCE || Math.abs(sa.chill() - sb.chill()) > TOLERANCE) return false;
        // Use the older/worse state on both stacks. Repeated split/merge cannot renew food.
        CompoundTag common = ta.copy();
        common.putDouble("left", Math.min(sa.left(), sb.left()));
        common.putLong("last", now);
        common.putInt("rate", Math.min(sa.rate(), sb.rate()));
        common.putBoolean("frozen", sa.frozen());
        common.putDouble("thaw", Math.min(sa.thaw(), sb.thaw()));
        common.putInt("chill", Math.min(sa.chill(), sb.chill()));
        common.putInt("cold", Math.min(sa.cold(), sb.cold()));
        FoodClock.save(first, common.copy()); FoodClock.save(second, common.copy());
        return true;
    }
}
