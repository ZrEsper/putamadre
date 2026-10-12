package dev.zomboid.remains.expand;
import java.util.*;import net.minecraft.world.SimpleContainer;import net.minecraft.world.item.ItemStack;
public final class ExpandedStorage extends SimpleContainer {
 private final List<ItemStack> content=new ArrayList<>();private final Object data;
 public ExpandedStorage(Object original,Object data){super(0);this.data=data;int count=((Number)FastReflection.call(original,"getContainerSize")).intValue();ensure(count);for(int i=0;i<count;i++)content.set(i,((ItemStack)FastReflection.call(original,"getItem",i)).copy());}
 public void ensure(int needed){if(needed<0)throw new IllegalArgumentException("negative corpse capacity");int target=((needed+8)/9)*9;while(content.size()<target)content.add(ItemStack.EMPTY);}
 @Override public int getContainerSize(){return content.size();}
 @Override public ItemStack getItem(int slot){return slot>=0&&slot<content.size()?content.get(slot):ItemStack.EMPTY;}
 @Override public boolean isEmpty(){for(ItemStack s:content)if(!s.isEmpty())return false;return true;}
 @Override public void setItem(int slot,ItemStack stack){if(slot<0)return;if(stack.isEmpty()&&slot>=content.size())return;ensure(slot+1);content.set(slot,stack);setChanged();}
 @Override public ItemStack removeItem(int slot,int amount){ItemStack s=getItem(slot);if(s.isEmpty())return ItemStack.EMPTY;ItemStack result=s.split(amount);if(s.isEmpty())content.set(slot,ItemStack.EMPTY);if(!result.isEmpty())setChanged();return result;}
 @Override public ItemStack removeItemNoUpdate(int slot){ItemStack s=getItem(slot);if(slot>=0&&slot<content.size())content.set(slot,ItemStack.EMPTY);return s;}
 @Override public void clearContent(){Collections.fill(content,ItemStack.EMPTY);setChanged();}
 @Override public void setChanged(){super.setChanged();if(data!=null)FastReflection.call(data,"setDirty");}
 // Receives a COPY of the ground stack and consumes only what was stored.
 public void absorb(ItemStack stack){for(ItemStack existing:content){if(stack.isEmpty())break;if(!existing.isEmpty()&&ItemStack.isSameItemSameComponents(existing,stack)){int amount=Math.min(stack.getCount(),Math.min(getMaxStackSize(),existing.getMaxStackSize())-existing.getCount());if(amount>0){existing.grow(amount);stack.shrink(amount);setChanged();}}}
 for(int i=0;!stack.isEmpty();i++){if(i==content.size())ensure(content.size()+9);if(!content.get(i).isEmpty())continue;int amount=Math.min(stack.getCount(),Math.min(getMaxStackSize(),stack.getMaxStackSize()));if(amount<=0)return;ItemStack copy=stack.copy();copy.setCount(amount);content.set(i,copy);stack.shrink(amount);setChanged();}}
}
