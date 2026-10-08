package dev.zomboid.survival;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class WaterGameplay {
    public static final int TANK = 32; // Eight buckets, four bottle servings each.
    public static boolean dirty(ItemStack stack) { return stack.is(WaterItems.CONTAMINATED); }
    public static InteractionResultHolder<ItemStack> guardDrink(Level level, Player player, InteractionHand hand) {
        ItemStack stack=player.getItemInHand(hand);
        if (!dirty(stack)) return null;
        if (!level.isClientSide) player.displayClientMessage(Component.literal("Agua contaminada: hiérvela en un horno o estufa antes de beber."), true);
        return InteractionResultHolder.fail(stack);
    }
    private static void exchange(Player player, InteractionHand hand, ItemStack input, ItemStack output) {
        if (!player.getAbilities().instabuild) input.shrink(1);
        if (input.isEmpty()) player.setItemInHand(hand,output);
        else if (!player.getInventory().add(output)) player.drop(output,false);
    }
    /** Replaces the old source-water refill path, including vanilla glass bottles. */
    public static void refill(PlayerInteractEvent.RightClickItem event) {
        ItemStack held=event.getItemStack();
        if (!held.is(Survival.EMPTY.get()) && !held.is(Items.GLASS_BOTTLE)) return;
        Player player=event.getEntity();
        Vec3 from=player.getEyePosition(), to=from.add(player.getLookAngle().scale(5.0));
        BlockHitResult hit=player.level().clip(new ClipContext(from,to,ClipContext.Block.OUTLINE,ClipContext.Fluid.SOURCE_ONLY,player));
        if (hit.getType()!=HitResult.Type.BLOCK || !player.level().getFluidState(hit.getBlockPos()).is(FluidTags.WATER)) return;
        if (!player.level().isClientSide) exchange(player,event.getHand(),held,new ItemStack(WaterItems.CONTAMINATED));
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide));
    }
    /** Returns null for ordinary machines so their existing interaction is retained. */
    public static InteractionResult dispenser(Level level, BlockPos position, Player player) {
        if (!(level.getBlockEntity(position) instanceof MachineEntity machine) || !MachineIds.dispenser(machine.id())) return null;
        if (level.isClientSide) return InteractionResult.sidedSuccess(true);
        InteractionHand hand=InteractionHand.MAIN_HAND;
        ItemStack held=player.getItemInHand(hand);
        if (held.is(Items.WATER_BUCKET)) {
            if (machine.zsRawWater+machine.zsCleanWater+4>TANK) {
                player.displayClientMessage(Component.literal("Dispensador lleno: capacidad de 8 cubetas."),true);
            } else {
                machine.zsRawWater+=4;
                exchange(player,hand,held,new ItemStack(Items.BUCKET));
                player.displayClientMessage(Component.literal("Agua cargada. El dispensador necesita electricidad para filtrarla y enfriarla."),true);
                machine.setChanged();
            }
        } else if (held.is(Survival.EMPTY.get()) || held.is(Items.GLASS_BOTTLE)) {
            if (machine.zsCleanWater<=0) {
                player.displayClientMessage(Component.literal(machine.zsRawWater>0 ? "Filtrando agua: necesita electricidad y 5 s por botella." : "Sin agua: llena el dispensador con una cubeta de agua."),true);
            } else {
                ItemStack water=new ItemStack(Survival.WATER.get());
                if (machine.zsPowered()) {
                    CompoundTag clock=new CompoundTag();clock.putLong("last",level.getGameTime());clock.putInt("rate",1);clock.putInt("cold",1200);
                    FoodClock.save(water,clock);
                }
                machine.zsCleanWater--;
                exchange(player,hand,held,water);machine.setChanged();
                player.displayClientMessage(Component.literal(machine.zsPowered() ? "Agua filtrada y fría: +8 hidratación." : "Agua filtrada: +6 hidratación. Sin electricidad no se enfría."),true);
            }
        } else {
            player.displayClientMessage(Component.literal("Dispensador: "+machine.zsCleanWater+" botellas filtradas, "+machine.zsRawWater+" pendientes. Llenar con cubeta; sacar con botella vacia."),true);
        }
        return InteractionResult.sidedSuccess(false);
    }
    public static void tick(MachineEntity machine) {
        if (!MachineIds.dispenser(machine.id())) return;
        if (machine.zsPowered() && machine.zsRawWater>0) {
            if (++machine.zsWaterProgress>=100) {
                machine.zsRawWater--;machine.zsCleanWater++;machine.zsWaterProgress=0;machine.setChanged();
            }
        } else if (machine.zsRawWater==0) machine.zsWaterProgress=0;
    }
    public static void save(MachineEntity machine,CompoundTag tag) {
        tag.putInt("WaterRaw",machine.zsRawWater);tag.putInt("WaterClean",machine.zsCleanWater);tag.putInt("WaterFilterTicks",machine.zsWaterProgress);
    }
    public static void load(MachineEntity machine,CompoundTag tag) {
        machine.zsCleanWater=Math.max(0,Math.min(TANK,tag.getInt("WaterClean")));
        machine.zsRawWater=Math.max(0,Math.min(TANK-machine.zsCleanWater,tag.getInt("WaterRaw")));
        machine.zsWaterProgress=Math.max(0,Math.min(99,tag.getInt("WaterFilterTicks")));
    }
}
