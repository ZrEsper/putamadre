package dev.zomboid.storage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.CompoundContainer;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

public class EventChecks {
    static int count;static void check(boolean test){if(!test)throw new AssertionError("Storage events "+count);count++;}
    public static void main(String[]args){
        ServerPlayer player=new ServerPlayer();ChestBlockEntity chest=new ChestBlockEntity();
        ChestMenu nativeMenu=new ChestMenu(MenuType.GENERIC_9x3,1,player.inventory,chest,3);player.containerMenu=nativeMenu;
        StorageEvents.open(new PlayerContainerEvent.Open(player,nativeMenu));check(player.containerMenu==nativeMenu);check(player.closes==0);check(chest.opens==1);
        ChestStorage storage=ChestStorage.wrap(chest);storage.ensure(36);storage.setItem(27,new ItemStack("overflow:components",7,64));
        StorageEvents.open(new PlayerContainerEvent.Open(player,nativeMenu));check(player.containerMenu instanceof ChestPageMenu);check(player.closes==1);check(player.opens==1);check(chest.opens==1);
        check(((ChestPageMenu)player.containerMenu).rows==6);check(player.title.toString().contains("Custom chest"));
        StorageEvents.open(new PlayerContainerEvent.Open(player,player.containerMenu));check(player.closes==1);check(player.opens==1);
        StorageEvents.open(new PlayerContainerEvent.Open(player,nativeMenu));check(player.closes==1);
        player.closeContainer();check(chest.opens==0);
        ChestBlockEntity right=new ChestBlockEntity();ChestStorage rightStorage=ChestStorage.wrap(right);rightStorage.ensure(36);rightStorage.setItem(27,new ItemStack("double-overflow",1,64));
        CompoundContainer doubleChest=new CompoundContainer(chest,right);nativeMenu=new ChestMenu(MenuType.GENERIC_9x6,3,player.inventory,doubleChest,6);player.containerMenu=nativeMenu;
        StorageEvents.open(new PlayerContainerEvent.Open(player,nativeMenu));check(player.containerMenu instanceof ChestPageMenu);check(player.title.toString().contains("container.chestDouble"));check(chest.opens==1&&right.opens==1);
        player.closeContainer();check(chest.opens==0&&right.opens==0);
        BlockDropsEvent broken=new BlockDropsEvent(chest,chest.level,new BlockPos(-2,5,7));StorageEvents.broken(broken);check(broken.drops.size()==1);
        net.minecraft.world.entity.item.ItemEntity drop=(net.minecraft.world.entity.item.ItemEntity)broken.drops.get(0);check(drop.stack.getCount()==7);check(drop.stack.tag.equals("overflow:components"));check(drop.x==-1.5&&drop.y==5.5&&drop.z==7.5);
        check(storage.getItem(27).isEmpty());check(rightStorage.getItem(27).getCount()==1);StorageEvents.broken(broken);check(broken.drops.size()==1);
        ChestBlockEntity reloaded=new ChestBlockEntity();reloaded.persistent=chest.persistent;check(ChestStorage.wrap(reloaded).getItem(27).isEmpty());
        BlockDropsEvent missing=new BlockDropsEvent(null,chest.level,new BlockPos(0,0,0));StorageEvents.broken(missing);check(missing.drops.isEmpty());
        System.out.println("Actual NeoForge event adapters: reopening, recursion guards, opener balance, titles, overflow block drops and persistence: "+count+" checks (API doubles)");
    }
}
