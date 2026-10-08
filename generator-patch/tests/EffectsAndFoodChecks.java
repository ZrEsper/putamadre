import dev.zomboid.survival.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.state.BlockState;

public final class EffectsAndFoodChecks {
    private static int passed;
    private static void check(boolean condition,String label) {
        if(!condition) throw new AssertionError(label);passed++;
    }
    private static ItemStack food(String id,double left,long last) {
        ItemStack food=new ItemStack(16,0,id,true);
        CompoundTag tag=new CompoundTag();tag.putDouble("left",left);tag.putLong("last",last);tag.putInt("rate",1);
        tag.putBoolean("frozen",false);tag.putDouble("thaw",0);tag.putInt("chill",0);tag.putInt("cold",0);
        setClock(food,tag);return food;
    }
    private static void setClock(ItemStack food,CompoundTag tag) {
        CompoundTag root=food.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();
        root.put(FoodClock.KEY,tag);food.set(DataComponents.CUSTOM_DATA,CustomData.of(root));
    }
    public static void main(String[] args) {
        ServerLevel level=new ServerLevel();BlockPos pos=new BlockPos(0,0,0);
        MachineEntity generator=new MachineEntity(pos,new BlockState(new MachineBlock("generator")));
        level.add(pos,generator);generator.setItem(0,new ItemStack(64,1600));
        for(int i=0;i<100;i++){generator.tick();level.advance();}
        check(level.sounds==5 && level.lastSound==GeneratorEffects.RUNNING,"Only custom engine sound, one event per second");
        check(level.particles==20,"Exhaust smoke every half-second during generation");
        for(int i=100;i<800;i++){generator.tick();level.advance();}
        int soundCount=level.sounds,particleCount=level.particles;
        for(int i=0;i<200;i++){generator.tick();level.advance();}
        check(level.sounds==soundCount && level.particles==particleCount,"Full-buffer standby must not emit sound or exhaust");
        generator.energy.extractEnergy(64000,false);
        for(int i=0;i<20;i++){generator.tick();level.advance();}
        check(level.sounds==soundCount+1 && level.particles==particleCount+4,"Effects must resume on demand");
        ServerLevel emptyLevel=new ServerLevel();MachineEntity empty=new MachineEntity(pos,new BlockState(new MachineBlock("generator")));
        emptyLevel.add(pos,empty);for(int i=0;i<100;i++){empty.tick();emptyLevel.advance();}
        check(emptyLevel.sounds==0 && emptyLevel.particles==0,"Unfueled generators must remain quiet");

        ItemStack first=food("raw_beef",29900,100),second=food("raw_beef",29880,120);
        check(!ItemStack.isSameItemSameComponents(first,second),"Regression fixture: one second creates different food components");
        check(FoodStacking.mergeable(first,second),"One-second split stacks must merge");
        check(ItemStack.isSameItemSameComponents(first,second),"Both stacks must use the same conservative food clock");
        check(FoodClock.data(first).getDouble("left")==29880,"Elapsed time must be accounted for at a common timestamp");
        check(first.getCount()==16 && second.getCount()==16,"Compatibility check must not change item counts");

        ItemStack tenA=food("raw_beef",29000,1000),tenB=food("raw_beef",28800,1000);
        check(FoodStacking.mergeable(tenA,tenB),"Exactly ten seconds of freshness difference must merge");
        check(FoodClock.data(tenA).getDouble("left")==28800,"Use the oldest freshness, never extend expiry");
        ItemStack overA=food("raw_beef",29000,1000),overB=food("raw_beef",28799,1000);
        check(!FoodStacking.mergeable(overA,overB),"More than ten seconds must not merge");
        check(FoodClock.data(overA).getDouble("left")==29000,"Rejected comparisons must preserve food metadata");
        check(!FoodStacking.mergeable(food("raw_beef",29000,1000),food("raw_pork",29000,1000)),"Different meat types must remain distinct");
        ItemStack namedA=food("raw_beef",29000,1000),namedB=food("raw_beef",28999,1000);
        namedB.set(DataComponents.CUSTOM_NAME,net.minecraft.network.chat.Component.literal("Special meat"));
        check(!FoodStacking.mergeable(namedA,namedB),"Other item components must remain strict");
        ItemStack customA=food("raw_beef",29000,1000),customB=food("raw_beef",28999,1000);
        CompoundTag root=customB.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();root.putString("other_mod","value");customB.set(DataComponents.CUSTOM_DATA,CustomData.of(root));
        check(!FoodStacking.mergeable(customA,customB),"Other mods' custom NBT must remain strict");
        ItemStack frozenA=food("raw_beef",29000,1000),frozenB=food("raw_beef",28999,1000);
        CompoundTag frozen=FoodClock.data(frozenB);frozen.putBoolean("frozen",true);frozen.putDouble("thaw",1200);setClock(frozenB,frozen);
        check(!FoodStacking.mergeable(frozenA,frozenB),"Frozen meat must not merge with unfrozen meat");
        ItemStack coldA=food("raw_beef",29000,1000),coldB=food("raw_beef",28999,1000);
        CompoundTag cold=FoodClock.data(coldB);cold.putInt("cold",1200);setClock(coldB,cold);
        check(!FoodStacking.mergeable(coldA,coldB),"Cold and room-temperature foods must remain distinct");
        check(!FoodStacking.mergeable(food("raw_beef",0,1000),food("raw_beef",100,1000)),"Expired food must not be revived through mixing");
        check(!FoodStacking.mergeable(food("raw_beef",Double.NaN,1000),food("raw_beef",100,1000)),"Invalid expiry data must not merge");
        ItemStack plain=new ItemStack(16,0,"raw_beef",true),newlyTracked=food("raw_beef",29980,20);
        check(FoodStacking.mergeable(plain,newlyTracked),"Fresh untagged food must merge within the tolerance");
        double previous=FoodClock.data(first).getDouble("left");
        for(int i=0;i<100;i++) {
            ItemStack split=food("raw_beef",previous-20,120);
            check(FoodStacking.mergeable(first,split),"Repeated split/merge remains compatible");
            double left=FoodClock.data(first).getDouble("left");check(left<=previous,"Repeated merging must never refresh food");previous=left;
        }
        check(!FoodStacking.mergeable(new ItemStack(16,0,"stone",false),new ItemStack(16,0,"dirt",false)),"Non-food item comparison must remain strict");
        System.out.println("PASS: "+passed+" effects/food assertions: engine sound and exhaust while generating, 10-second boundary, conservative expiry, metadata isolation and repeated merges.");
    }
}
