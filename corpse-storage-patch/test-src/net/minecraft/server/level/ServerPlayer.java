package net.minecraft.server.level;
import net.minecraft.world.MenuProvider;import net.minecraft.world.entity.player.*;import net.minecraft.world.inventory.*;import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
public class ServerPlayer extends Player {
    public AbstractContainerMenu containerMenu=new AbstractContainerMenu();public final Inventory inventory=new Inventory();public int opens,closes;public Object title;
    public ServerPlayer(){inventory.player=this;}
    public void closeContainer(){closes++;if(containerMenu instanceof ChestMenu chest)chest.getContainer().stopOpen(this);containerMenu=new AbstractContainerMenu();}
    public void openMenu(MenuProvider provider){if(++opens>8)throw new AssertionError("Recursive open");containerMenu=provider.createMenu(opens,inventory,this);if(provider instanceof net.minecraft.world.SimpleMenuProvider simple)title=simple.title;dev.zomboid.storage.StorageEvents.open(new PlayerContainerEvent.Open(this,containerMenu));}
}
