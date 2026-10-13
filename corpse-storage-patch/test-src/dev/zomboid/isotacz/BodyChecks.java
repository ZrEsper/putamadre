package dev.zomboid.isotacz;
import dev.zomboid.storage.CorpseOutline;
import cn.kafei.interact.Interaction;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Block;
public class BodyChecks {
    static int checks;
    static void check(boolean b){if(!b)throw new AssertionError("Outline "+checks);checks++;}
    public static class Hit {public Object block,entity;public String getType(){return entity==null?"BLOCK":"ENTITY";}public Object getEntity(){return entity;}public Object getBlockPos(){return block;}}
    public static class Level {public Object getBlockEntity(Object pos){return null;}public State getBlockState(Object pos){return new State((Block)pos);}}
    public record State(Block block){public Block getBlock(){return block;}}
    public static void main(String[]args) {
        check(CorpseOutline.remainsId("doomsday_decoration:remains_1"));check(CorpseOutline.remainsId("zombie_remains_loot:skeletal_remains"));
        check(!CorpseOutline.remainsId("minecraft:stone"));check(!CorpseOutline.remainsId("other:remains_1"));
        Minecraft mc=Minecraft.INSTANCE;mc.player=new Object();mc.level=new Level();Hit hit=new Hit();hit.block=new Block("doomsday_decoration:remains_4");mc.hitResult=hit;
        check(Interaction.supported(hit.block));check(Interaction.block());check(Interaction.target()==hit.block);
        hit.block=new Block("zombie_remains_loot:skeletal_remains");check(Interaction.block());
        hit.block=new Block("minecraft:stone");check(!Interaction.block());
        hit.entity=new de.maxhenkel.corpse.entities.CorpseEntity();check(Interaction.entity(hit.entity));check(!Interaction.entity(new de.maxhenkel.corpse.entities.CorpseEntity()));
        mc.screen=new Object();check(!Interaction.entity(hit.entity));mc.screen=null;mc.player=null;check(Interaction.target()==null);
        System.out.println("Actual patched Quietly corpse block/entity outline targeting: "+checks+" checks");
    }
}
