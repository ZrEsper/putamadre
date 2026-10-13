package dev.zomboid.scavenging;
import dev.zomboid.storage.ChestStorage;
import net.minecraft.world.Container;

public final class KeepLoot {
    static Container nativeInventory(Search.Target target) {
        if (Search.is(target.be,"net.minecraft.world.level.block.entity.ChestBlockEntity")) {
            Object state=R.call(target.level,"getBlockState",target.pos);
            Object container=R.call(R.type("net.minecraft.world.level.block.ChestBlock"),"getContainer",R.call(state,"getBlock"),state,target.level,target.pos,true);
            if(container!=null) return (Container)container;
        }
        return (Container)target.be;
    }
    public static Loot.Inv inventory(Search.Target target) {
        if (target.be != null && (Search.is(target.be,"net.minecraft.world.level.block.entity.ChestBlockEntity") || Search.is(target.be,"net.minecraft.world.level.block.entity.BarrelBlockEntity"))) return new Loot.Inv(ChestStorage.wrap(nativeInventory(target)),0);
        return Loot.inventoryOriginal14(target);
    }
    public static void insert(Loot.Inv inventory, Object stack, Search.Target target, Object player) {
        Object remaining = inventory.add(stack);
        // Inv.add mutates existing stacks as well as slots; always dirty the native storage.
        inventory.changed();
        if (R.yes(R.call(remaining,"isEmpty"))) return;
        if (inventory.value instanceof ChestStorage storage) {
            while (!R.yes(R.call(remaining,"isEmpty"))) {
                storage.ensure(storage.getContainerSize()+9);
                remaining=inventory.add(remaining);
            }
        } else if (target.corpse != null) {
            R.call(inventory.value,"absorb",remaining);
        } else if (inventory.kind == 1) {
            while (!R.yes(R.call(remaining,"isEmpty"))) {
                int before=inventory.size();
                Loot.grow(target.be,before+9);
                Loot.Inv grown=Loot.inventory(target);
                Object next=grown.add(remaining);
                if (((Number)R.call(next,"getCount")).intValue() >= ((Number)R.call(remaining,"getCount")).intValue()) throw new IllegalStateException("Expanded storage rejected loot; no world drop was spawned");
                remaining=next;
            }
        } else throw new IllegalStateException("Unsupported fixed inventory; no world drop was spawned");
        if (!R.yes(R.call(remaining,"isEmpty"))) throw new IllegalStateException("Corpse storage rejected loot");
        inventory.changed();
    }
    public static void reopen(Search.Target target, Object player) {
        if (expanded(target)) {
            R.call(player,"closeContainer");
            Object point=R.call(R.type("net.minecraft.world.phys.Vec3"),"atCenterOf",target.pos);
            Object hit=R.make("net.minecraft.world.phys.BlockHitResult",point,R.get(R.type("net.minecraft.core.Direction"),"UP"),target.pos,false);
            Search.open(new Search.Job(player,target,hit,0,Search.skill(player)));
        }
    }
    public static boolean expanded(Search.Target target) {
        return target.be != null && (Search.is(target.be,"net.minecraft.world.level.block.entity.ChestBlockEntity") || Search.is(target.be,"net.minecraft.world.level.block.entity.BarrelBlockEntity")) && ChestStorage.wrap(nativeInventory(target)).expanded();
    }
}
