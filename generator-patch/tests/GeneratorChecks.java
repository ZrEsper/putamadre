import java.lang.reflect.Method;
import dev.zomboid.survival.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** Executes the actual patched MachineEntity bytecode against lightweight API test doubles. */
public final class GeneratorChecks {
    private static int passed;
    private static void check(boolean condition, String description) {
        if (!condition) throw new AssertionError(description);
        passed++;
    }
    private static MachineEntity machine(Level level, String id, int x, int y, int z) {
        BlockPos position = new BlockPos(x,y,z);
        MachineEntity m = new MachineEntity(position, new BlockState(new MachineBlock(id)));
        level.add(position,m); return m;
    }
    private static int fuelCount(MachineEntity m) {
        int n=0;for(int i=0;i<54;i++) n+=m.getItem(i).getCount();return n;
    }
    private static void drain(MachineEntity generator, Level level) {
        generator.tick();generator.energy.extractEnergy(64000,false);level.advance();
    }
    private static CompoundTag save(MachineEntity m) throws Exception {
        CompoundTag tag=new CompoundTag(); Method method=MachineEntity.class.getDeclaredMethod("saveAdditional",CompoundTag.class,HolderLookup.Provider.class);
        method.setAccessible(true);method.invoke(m,tag,null);return tag;
    }
    private static void load(MachineEntity m, CompoundTag tag) throws Exception {
        Method method=MachineEntity.class.getDeclaredMethod("loadAdditional",CompoundTag.class,HolderLookup.Provider.class);
        method.setAccessible(true);method.invoke(m,tag,null);
    }
    private static AbstractContainerMenu menu(MachineEntity m,Inventory inventory) throws Exception {
        Method method=MachineEntity.class.getDeclaredMethod("createMenu",int.class,Inventory.class);
        method.setAccessible(true);return (AbstractContainerMenu)method.invoke(m,1,inventory);
    }
    public static void main(String[] args) throws Exception {
        Level level=new Level();MachineEntity generator=machine(level,"generator",0,0,0);
        generator.setItem(0,new ItemStack(64,1600));
        long output=0;
        for(int tick=0;tick<36000;tick++) {
            generator.tick(); int energy=generator.energy.extractEnergy(64000,false); output+=energy;
            check(energy==80,"Coal must produce exactly 80 FE at active tick "+tick);level.advance();
        }
        check(fuelCount(generator)==0 && generator.zsGetBurn()==0,"64 coal must end at 36000 ticks");
        check(output==2880000,"Thirty active minutes must produce 2880000 FE");
        drain(generator,level);check(generator.energy.getEnergyStored()==0,"No additional tick of fuel");

        Level restartLevel=new Level();MachineEntity before=machine(restartLevel,"fixedgenerator",0,0,0);
        before.setItem(0,new ItemStack(64,1600));
        for(int i=0;i<12345;i++)drain(before,restartLevel);
        CompoundTag snapshot=save(before);
        MachineEntity after=machine(new Level(),"fixedgenerator",0,0,0);load(after,snapshot);
        int remaining=0;Level afterLevel=new Level();after.setLevel(afterLevel);
        while(fuelCount(after)>0 || after.zsGetBurn()>0){drain(after,afterLevel);remaining++;if(remaining>36000)throw new AssertionError("Fuel did not expire");}
        check(remaining==36000-12345,"Fuel timing and fractional carry must survive save/reload");

        MachineEntity paused=machine(new Level(),"trailergenerator",0,0,0);
        paused.setItem(0,new ItemStack(64,1600));for(int i=0;i<PowerPolicy.capacity(paused)/80;i++)paused.tick();
        check(paused.zsGetEnergy()==PowerPolicy.capacity(paused),"Buffer must fill without losing generated energy");
        int burn=paused.zsGetBurn(), count=fuelCount(paused);
        for(int i=0;i<5000;i++)paused.tick();
        check(paused.zsGetBurn()==burn && fuelCount(paused)==count,"Full energy buffer must pause fuel consumption");
        paused.energy.extractEnergy(80,false);paused.tick();
        check(paused.zsGetBurn()==burn-1 && paused.zsGetEnergy()==PowerPolicy.capacity(paused),"Production must resume after demand");

        Level rangeLevel=new Level();MachineEntity rangeGenerator=machine(rangeLevel,"generator",0,0,0);
        rangeGenerator.setItem(0,new ItemStack(64,1600));rangeGenerator.tick();
        MachineEntity edge=machine(rangeLevel,"fridge",25,0,0);edge.tick();
        check(edge.status().startsWith("Con electricidad"),"Power must reach exactly 25 blocks");
        MachineEntity outside=machine(rangeLevel,"fridge",26,0,0);outside.tick();
        check(outside.status().startsWith("Sin electricidad"),"Power must not reach 26 blocks");
        MachineEntity diagonal=machine(rangeLevel,"freezer",15,20,0);rangeGenerator.tick();diagonal.tick();
        check(diagonal.status().startsWith("Con electricidad"),"25-block diagonal must work");
        MachineEntity diagonalOutside=machine(rangeLevel,"freezer",18,18,0);diagonalOutside.tick();
        check(diagonalOutside.status().startsWith("Sin electricidad"),"Euclidean distance must not be a 25-block cube");
        check(outside.status().contains("25 bloques"),"Status text must show updated range");
        rangeGenerator.setRemoved();rangeGenerator.tick();edge.tick();
        check(edge.status().startsWith("Sin electricidad"),"Removed generators must stop supplying devices");

        Level uiLevel=new Level();MachineEntity uiGenerator=machine(uiLevel,"generator",0,0,0);
        Inventory inventory=new Inventory(new Player());
        GeneratorMenu ui=(GeneratorMenu)menu(uiGenerator,inventory);
        check(ui.slots.size()==90,"All 54 saved fuel slots and 36 inventory slots must be exposed");
        check(menu(machine(uiLevel,"freezer",3,0,0),inventory) instanceof ChestMenu,"Only generator menus must change");
        ItemStack nonFuel=new ItemStack(5,0);
        check(!ui.slots.get(0).mayPlace(nonFuel),"Fuel inlet must reject non-fuel");
        check(!ui.slots.get(53).mayPlace(nonFuel),"Reserve slots must reject non-fuel");
        inventory.setItem(9,new ItemStack(64,1600));
        check(!ui.quickMoveStack(inventory.player,54).isEmpty() && fuelCount(uiGenerator)==64,"Shift-click must insert fuel");
        inventory.setItem(10,nonFuel);
        check(ui.quickMoveStack(inventory.player,55).isEmpty() && inventory.getItem(10).getCount()==5,"Shift-click must preserve rejected items");
        check(!ui.quickMoveStack(inventory.player,0).isEmpty() && fuelCount(uiGenerator)==0,"Shift-click must retrieve fuel");
        uiGenerator.setItem(53,new ItemStack(64,1600));
        check(ui.remainingTicks()==36000,"Autonomy must count all reserve slots");
        check(!ui.quickMoveStack(inventory.player,53).isEmpty(),"Existing last-slot items must remain retrievable");

        GeneratorData serverData=new GeneratorData(paused);SimpleContainerData wire=new SimpleContainerData(9);
        for(int i=0;i<9;i++)wire.set(i,(short)serverData.get(i));
        GeneratorMenu client=new GeneratorMenu(1,inventory,new SimpleContainer(54),wire);
        check(client.energy()==PowerPolicy.capacity(paused),"Signed-short synchronization must preserve generator capacity");
        paused.zsSetBurn(70312);paused.zsFuelTotal=70312;
        for(int i=0;i<9;i++)wire.set(i,(short)serverData.get(i));
        check(client.burn()==70312 && client.fuelTotal()==70312,"Long-burning fuels must synchronize beyond 16 bits");
        GuiGraphics graphics=new GuiGraphics();new GeneratorScreen(client,inventory,Component.literal("Generador")).render(graphics,0,0,0);
        check(graphics.fills>100 && graphics.labels.contains("GENERADOR"),"Generator panel must render its machinery and slots");
        check(graphics.labels.contains("Alcance: 25 bloques"),"Generator panel must render the range");

        CompoundTag old=new CompoundTag();old.putInt("FuelTicks",1600);old.putInt("Energy",1200);
        MachineEntity legacy=machine(new Level(),"generator",0,0,0);load(legacy,old);
        check(legacy.zsGetBurn()==562 && legacy.zsGetEnergy()==1200,"Legacy burning generators must migrate without losing energy");
        CompoundTag migrated=save(legacy);load(legacy,migrated);
        check(legacy.zsGetBurn()==562,"Fuel migration must run only once");
        System.out.println("PASS: "+passed+" assertions: exact fuel duration, pause/resume, NBT persistence/migration, spherical range, generator menu, transfers, sync and render smoke check.");
    }
}
