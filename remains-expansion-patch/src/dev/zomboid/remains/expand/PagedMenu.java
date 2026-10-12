package dev.zomboid.remains.expand;
import net.minecraft.world.inventory.*;import net.minecraft.world.entity.player.*;import net.minecraft.world.item.ItemStack;
public final class PagedMenu extends ChestMenu {
 final PagedView view;final Object corpse,data,pos,level;
 public PagedMenu(int id,Inventory inventory,ExpandedStorage storage,Object corpse,Object data,Object pos,Object level){this(id,inventory,new PagedView(storage),corpse,data,pos,level);}
 private PagedMenu(int id,Inventory inventory,PagedView view,Object corpse,Object data,Object pos,Object level){super(MenuType.GENERIC_9x6,id,inventory,view,6);this.view=view;this.corpse=corpse;this.data=data;this.pos=pos;this.level=level;for(int i=45;i<54;i++){Slot old=slots.get(i);Slot button=new Slot(view,i,old.x,old.y){@Override public boolean mayPlace(ItemStack s){return false;}@Override public boolean mayPickup(Player p){return false;}};button.index=old.index;slots.set(i,button);}}
 @Override public boolean stillValid(Player p){return ExpandedBodies.valid(corpse,data,pos,level,p);}
 @Override public void clicked(int slot,int button,ClickType type,Player player){if(slot>=45&&slot<54){if(type==ClickType.PICKUP&&button==0&&(slot==45||slot==53)){view.move(slot==45?-1:1);broadcastChanges();}return;}super.clicked(slot,button,type,player);}
 @Override public ItemStack quickMoveStack(Player player,int slot){return slot>=45&&slot<54?ItemStack.EMPTY:super.quickMoveStack(player,slot);}
}
