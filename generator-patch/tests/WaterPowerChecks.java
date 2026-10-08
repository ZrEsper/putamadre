import java.lang.reflect.Method;
import dev.zomboid.survival.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.mcreator.doomsdaydecoration.DoomsdayDecorationMod;

public final class WaterPowerChecks {
    private static int passed;
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);passed++;}
    private static MachineEntity machine(Level level,String name,int x){BlockPos pos=new BlockPos(x,0,0);MachineEntity m=new MachineEntity(pos,new BlockState(new MachineBlock(name)));level.add(pos,m);return m;}
    private static CompoundTag save(MachineEntity m)throws Exception{CompoundTag tag=new CompoundTag();Method save=MachineEntity.class.getDeclaredMethod("saveAdditional",CompoundTag.class,HolderLookup.Provider.class);save.setAccessible(true);save.invoke(m,tag,null);return tag;}
    private static void load(MachineEntity m,CompoundTag tag)throws Exception{Method load=MachineEntity.class.getDeclaredMethod("loadAdditional",CompoundTag.class,HolderLookup.Provider.class);load.setAccessible(true);load.invoke(m,tag,null);}
    public static void main(String[] args)throws Exception {
        for(String name:new String[]{"generator","fixedgenerator","trailergenerator"}) {
            Level world=new Level();MachineEntity m=machine(world,name,0);int capacity=PowerPolicy.capacity(m);
            check(capacity==(name.equals("generator")?64000:name.equals("fixedgenerator")?256000:384000),"Generator tier capacity: "+name);
            check(m.energy.getMaxEnergyStored()==capacity,"Capability capacity matches tier: "+name);
            m.setItem(0,new ItemStack(64,1600));for(int i=0;i<capacity/80;i++){m.tick();world.advance();}
            check(m.zsGetEnergy()==capacity,"Generator fills to tier capacity: "+name);
            int burn=m.zsGetBurn();for(int i=0;i<100;i++){m.tick();world.advance();}
            check(m.zsGetBurn()==burn,"Fuel pauses at tier capacity: "+name);
            MachineEntity restored=machine(new Level(),name,0);load(restored,save(m));check(restored.zsGetEnergy()==capacity,"Save/reload preserves high-tier energy: "+name);
            ContainerData server=new GeneratorData(m);SimpleContainerData wire=new SimpleContainerData(9);
            for(int i=0;i<9;i++)wire.set(i,(short)server.get(i));GeneratorMenu ui=new GeneratorMenu(1,new Inventory(new Player()),new SimpleContainer(54),wire);
            check(ui.capacity()==capacity && ui.energy()==capacity,"Tier capacity synchronizes to client: "+name);
        }
        Level efficiencyWorld=new Level();MachineEntity g=machine(efficiencyWorld,"generator",0);g.setItem(0,new ItemStack(64,1600));g.tick();g.energy.extractEnergy(64000,false);g.tick();
        int before=g.energy.getEnergyStored();MachineEntity fridge=machine(efficiencyWorld,"fridge",1);fridge.tick();
        check(before-g.energy.getEnergyStored()==1,"Refrigerator uses 1 FE/t");
        before=g.energy.getEnergyStored();MachineEntity freezer=machine(efficiencyWorld,"freezer",2);freezer.tick();check(before-g.energy.getEnergyStored()==2,"Freezer uses 2 FE/t");
        before=g.energy.getEnergyStored();MachineEntity oven=machine(efficiencyWorld,"electricoven",3);oven.tick();check(before-g.energy.getEnergyStored()==30,"Oven preserves its existing demand");

        // Exercise both common ID spellings without adding speculative IDs to the registered type.
        DoomsdayDecorationMod.BLOCKS_BY_ID.put("water_dispenser",new DeferredBlock<>(new MachineBlock("water_dispenser")));
        check(MachineIds.registered(java.util.Set.of("fridge")).toList().contains("water_dispenser"),"Actual dispenser block added to machine registration");
        check(MachineIds.contains(java.util.Set.of("fridge"),"waterdispenser"),"Factory recognizes compact dispenser ID");
        check(MachineIds.dispenserBlock()!=null,"Craftable alias points to existing dispenser block");
        check(!MachineIds.contains(java.util.Set.of("fridge"),"random_block"),"Unrelated decoration blocks remain untouched");
        WaterItems.register(new net.neoforged.neoforge.registries.RegisterEvent());

        Level waterWorld=new Level();MachineEntity source=machine(waterWorld,"generator",0);source.setItem(0,new ItemStack(64,1600));
        MachineEntity dispenser=machine(waterWorld,"water_dispenser",1);Player player=new Player();player.setLevel(waterWorld);BlockPos position=new BlockPos(1,0,0);
        check(dispenser.zsRawWater==0 && dispenser.zsCleanWater==0,"Crafted/placed dispensers start empty");
        check(!dispenser.canPlaceItem(0,new ItemStack(1,0,"raw_beef",true)),"Dispensers must not accept food through inventory automation");
        player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(WaterItems.CONTAMINATED));WaterGameplay.dispenser(waterWorld,position,player);
        check(dispenser.zsRawWater==0 && player.getItemInHand(InteractionHand.MAIN_HAND).getCount()==1,"Bottles cannot fill dispenser; buckets are mandatory");
        player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.WATER_BUCKET));WaterGameplay.dispenser(waterWorld,position,player);
        check(dispenser.zsRawWater==4,"Water bucket adds four servings");
        check(player.getItemInHand(InteractionHand.MAIN_HAND).is(Items.BUCKET),"Filling returns an empty bucket");
        dispenser.tick();check(dispenser.zsWaterProgress==0,"Unpowered dispenser cannot filter water");
        for(int i=0;i<99;i++){source.tick();dispenser.tick();waterWorld.advance();}
        check(dispenser.zsCleanWater==0 && dispenser.zsWaterProgress==99,"Filtering requires 100 powered ticks");
        MachineEntity restoredWater=machine(new Level(),"water_dispenser",1);load(restoredWater,save(dispenser));
        check(restoredWater.zsRawWater==4 && restoredWater.zsCleanWater==0 && restoredWater.zsWaterProgress==99,"Tank contents and partial filtration survive save/reload");
        source.tick();dispenser.tick();waterWorld.advance();check(dispenser.zsCleanWater==1 && dispenser.zsRawWater==3,"100th powered tick creates filtered water");
        player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.GLASS_BOTTLE));WaterGameplay.dispenser(waterWorld,position,player);
        ItemStack coldWater=player.getItemInHand(InteractionHand.MAIN_HAND);
        check(coldWater.is(Survival.WATER.get()) && FoodClock.cold(coldWater),"Empty bottle receives filtered cold water");
        check(dispenser.zsCleanWater==0 && dispenser.zsRawWater==3,"Dispensing removes exactly one clean serving");
        ServerPlayer drinker=new ServerPlayer();drinker.setLevel(waterWorld);homeostatic.util.WaterHelper.hydrated=0;
        Survival.WATER.get().finishUsingItem(coldWater,waterWorld,drinker);check(homeostatic.util.WaterHelper.hydrated==8,"Cold filtered water hydrates by 8");
        ItemStack warmWater=new ItemStack(Survival.WATER.get());Survival.WATER.get().finishUsingItem(warmWater,waterWorld,drinker);
        check(homeostatic.util.WaterHelper.hydrated==14,"Ordinary filtered water hydrates by 6");
        player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.GLASS_BOTTLE));PlayerInteractEvent.RightClickItem collect=new PlayerInteractEvent.RightClickItem(player,InteractionHand.MAIN_HAND);
        WaterGameplay.refill(collect);check(collect.canceled && WaterGameplay.dirty(player.getItemInHand(InteractionHand.MAIN_HAND)),"Ground-source glass bottles produce contaminated water");
        check(WaterItems.CONTAMINATED.use(waterWorld,player,InteractionHand.MAIN_HAND).result==InteractionResult.FAIL,"Actual patched DrinkItem refuses contaminated water");
        player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Survival.EMPTY.get()));WaterGameplay.refill(new PlayerInteractEvent.RightClickItem(player,InteractionHand.MAIN_HAND));
        check(WaterGameplay.dirty(player.getItemInHand(InteractionHand.MAIN_HAND)),"Mod empty bottles also collect contaminated water");
        for(int i=0;i<7;i++){player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.WATER_BUCKET));WaterGameplay.dispenser(waterWorld,position,player);}
        int tank=dispenser.zsRawWater+dispenser.zsCleanWater;check(tank==31,"Tank uses servings without rounding or water duplication");
        player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.WATER_BUCKET));WaterGameplay.dispenser(waterWorld,position,player);
        check(dispenser.zsRawWater+dispenser.zsCleanWater==tank && player.getItemInHand(InteractionHand.MAIN_HAND).is(Items.WATER_BUCKET),"A full tank rejects a whole bucket without consuming it");
        check(WaterGameplay.dispenser(waterWorld,new BlockPos(0,0,0),player)==null,"Generator block interaction is retained");
        System.out.println("PASS: "+passed+" water/power assertions: tier buffers, low consumption, tank persistence, bucket-only filling, purification/cooling, hydration and contaminated source water.");
    }
}
