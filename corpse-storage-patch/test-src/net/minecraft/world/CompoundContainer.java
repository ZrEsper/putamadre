package net.minecraft.world;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
public class CompoundContainer extends SimpleContainer {
    public final Container container1,container2;
    public CompoundContainer(Container a,Container b){super(0);container1=a;container2=b;}
    public int getContainerSize(){return container1.getContainerSize()+container2.getContainerSize();}
    public ItemStack getItem(int i){return i<container1.getContainerSize()?container1.getItem(i):container2.getItem(i-container1.getContainerSize());}
    public void setItem(int i,ItemStack s){if(i<container1.getContainerSize())container1.setItem(i,s);else container2.setItem(i-container1.getContainerSize(),s);}
    public boolean stillValid(Player p){return container1.stillValid(p)&&container2.stillValid(p);}
    public void startOpen(Player p){container1.startOpen(p);container2.startOpen(p);}
    public void stopOpen(Player p){container1.stopOpen(p);container2.stopOpen(p);}
}
