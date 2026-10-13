package dev.zomboid.scavenging;
import dev.zomboid.storage.*;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.inventory.*;
import net.minecraft.world.entity.player.*;

public class StorageChecks {
    public static class Actor {public double getX(){return 0;}public double getY(){return 0;}public double getZ(){return 0;}}
    static int checks;
    static void check(boolean test){if(!test)throw new AssertionError("Storage check "+checks);checks++;}
    static ItemStack item(String components,int n){return new ItemStack(components,n,64);}
    static Search.Target target(ChestBlockEntity chest){chest.level.container=chest;return new Search.Target(chest.level,new Object(),chest,null,null,"test");}
    static int total(Container c){int n=0;for(int i=0;i<c.getContainerSize();i++)n+=c.getItem(i).getCount();return n;}
    static void insert(Loot.Inv inv,ItemStack stack,Search.Target target){Loot.insert(inv,stack,target,new Object());}
    public static void main(String[]args)throws Exception {
        ChestBlockEntity chest=new ChestBlockEntity();Search.Target t=target(chest);
        Loot.Inv inv=Loot.inventory(t);ChestStorage storage=(ChestStorage)inv.value;
        check(storage.getContainerSize()==27);
        insert(inv,item("apple:components-A",32),t);check(storage.getContainerSize()==27);check(storage.getItem(0).getCount()==32);
        insert(inv,item("apple:components-A",16),t);check(storage.getContainerSize()==27);check(storage.getItem(0).getCount()==48);check(chest.dirty>0);
        insert(inv,item("apple:components-B",2),t);check(storage.getItem(1).tag.equals("apple:components-B"));check(storage.getItem(0).getCount()==48);
        for(int i=0;i<27;i++)storage.setItem(i,item("full-"+i,64));
        ItemStack incoming=item("weapon:nbt",150);insert(inv,incoming,t);
        check(incoming.getCount()==150);check(storage.getContainerSize()==36);check(storage.getItem(27).getCount()==64);check(storage.getItem(29).getCount()==22);check(total(storage)==27*64+150);
        insert(inv,item("weapon:nbt",10),t);check(storage.getContainerSize()==36);check(storage.getItem(29).getCount()==32);
        ChestBlockEntity reloaded=new ChestBlockEntity();reloaded.persistent=chest.persistent;
        for(int i=0;i<27;i++)reloaded.setItem(i,chest.getItem(i).copy());
        ChestStorage restored=ChestStorage.wrap(reloaded);check(restored.getContainerSize()==36);check(total(restored)==total(storage));check(restored.getItem(27).tag.equals("weapon:nbt"));
        restored.ensure(324);restored.setItem(310,item("slot-over-255",3));
        ChestBlockEntity reload2=new ChestBlockEntity();reload2.persistent=reloaded.persistent;
        ChestStorage big=ChestStorage.wrap(reload2);check(big.getContainerSize()==324);check(big.getItem(310).getCount()==3);
        Inventory inventory=new Inventory();ChestMenu original=new ChestMenu(MenuType.GENERIC_9x3,1,inventory,reloaded,3);
        check(reloaded.opens==1);ChestPageMenu menu=(ChestPageMenu)ChestMenus.wrap(original,1,inventory);check(reloaded.opens==1);
        for(int i=45;i<54;i++)check(!menu.slots.get(i).mayPlace(item("x",1))&&!menu.slots.get(i).mayPickup(inventory.player));
        check(menu.quickMoveStack(inventory.player,53).isEmpty());int before=total(restored);
        for(ClickType type:ClickType.values())menu.clicked(49,0,type,inventory.player);
        check(total(restored)==before);check(menu.clicks==0);menu.clicked(53,0,ClickType.PICKUP,inventory.player);check(menu.clicks==0);
        ChestPages pages=new ChestPages(restored);pages.move(6);check(pages.getItem(40).tag.equals("slot-over-255"));check(pages.removeItem(40,1).getCount()==1);check(restored.getItem(310).getCount()==2);
        pages.setItem(45,item("forged-arrow",64));check(restored.getItem(315).isEmpty());pages.clearContent();check(restored.getItem(310).getCount()==2);
        reloaded.valid=false;check(!menu.stillValid(inventory.player));reloaded.valid=true;
        menu.getContainer().stopOpen(inventory.player);check(reloaded.opens==0);
        ChestBlockEntity left=new ChestBlockEntity(),right=new ChestBlockEntity();Search.Target dt=target(left);
        CompoundContainer compound=new CompoundContainer(left,right);left.level.container=compound;
        for(int i=0;i<27;i++)left.setItem(i,item("left-"+i,64));
        Loot.Inv doubleInv=Loot.inventory(dt);insert(doubleInv,item("right-free",3),dt);
        ChestStorage doubleStorage=(ChestStorage)doubleInv.value;check(doubleStorage.getContainerSize()==54);check(right.getItem(0).getCount()==3);check(!doubleStorage.expanded());
        for(int i=0;i<27;i++)right.setItem(i,item("right-"+i,64));insert(doubleInv,item("overflow-double",65),dt);
        check(doubleStorage.getContainerSize()==63);check(doubleStorage.getItem(54).getCount()==64);check(doubleStorage.getItem(55).getCount()==1);check(right.getItem(0).tag.equals("right-0"));
        check(KeepLoot.expanded(dt));check(!Loot.upgrade(new Search.Job(new Actor(),dt,null,0,0),4));
        // Deliberate block breaking visits every extra slot and leaves a saved empty inventory.
        Container drops=ChestMenus.drops(compound);check(total(drops)==54*64+65);
        for(int i=0;i<drops.getContainerSize();i++)drops.getItem(i).setCount(0);drops.setChanged();
        check(total(drops)==0);ChestBlockEntity afterBreak=new ChestBlockEntity();afterBreak.persistent=right.persistent;check(ChestStorage.wrap(afterBreak).isEmpty());
        System.out.println("Quietly native insertion, components, double chests, persistence, pages, and deliberate-break conservation: "+checks+" checks");
    }
}
