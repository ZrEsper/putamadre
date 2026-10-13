package net.neoforged.neoforge.event.level;
import java.util.*;
public class BlockDropsEvent {
    public final Object blockEntity,level,pos;public final List<Object> drops=new ArrayList<>();
    public BlockDropsEvent(Object be,Object level,Object pos){blockEntity=be;this.level=level;this.pos=pos;}
    public Object getBlockEntity(){return blockEntity;}public Object getLevel(){return level;}public Object getPos(){return pos;}public List<Object> getDrops(){return drops;}
}
