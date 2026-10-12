package dev.zomboid.remains.expand;
import net.minecraft.world.SimpleContainer;import net.minecraft.world.item.ItemStack;
public final class PagedView extends SimpleContainer {
 final ExpandedStorage storage;int page;private ItemStack previous,next,label;
 public PagedView(ExpandedStorage storage){super(54);this.storage=storage;update();}
 public int pages(){return Math.max(1,(storage.getContainerSize()+44)/45);}
 public void move(int delta){page=Math.max(0,Math.min(pages()-1,page+delta));update();setChanged();}
 void update(){previous=ExpandedBodies.icon("ARROW","Pagina anterior");next=ExpandedBodies.icon("ARROW","Pagina siguiente");label=ExpandedBodies.icon("PAPER","Pagina "+(page+1)+" / "+pages());}
 @Override public ItemStack getItem(int slot){if(slot<45)return storage.getItem(page*45+slot);return slot==45?previous:slot==49?label:slot==53?next:ItemStack.EMPTY;}
 @Override public void setItem(int slot,ItemStack item){if(slot>=0&&slot<45){storage.setItem(page*45+slot,item);update();}}
 @Override public ItemStack removeItem(int slot,int amount){return slot>=0&&slot<45?storage.removeItem(page*45+slot,amount):ItemStack.EMPTY;}
 @Override public ItemStack removeItemNoUpdate(int slot){return slot>=0&&slot<45?storage.removeItemNoUpdate(page*45+slot):ItemStack.EMPTY;}
 @Override public void clearContent(){/* A menu view must never clear the whole corpse. */}
 @Override public void setChanged(){super.setChanged();if(storage!=null)storage.setChanged();}
}
