package cn.kafei.mixin;
import cn.kafei.interact.Interaction;
import dev.zomboid.storage.CorpseOutline;
import dev.zomboid.storage.Reflect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(targets="net.minecraft.client.renderer.LevelRenderer",remap=false)
public abstract class InteractionOutlineMixin {
    @ModifyArgs(method="renderHitOutline",at=@At(value="INVOKE",target="Lnet/minecraft/client/renderer/LevelRenderer;renderShape(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/shapes/VoxelShape;DDDFFFF)V"))
    private void quietly$yellow(Args args) {
        if(Interaction.block()) {
            if(CorpseOutline.block(Interaction.target()) && Boolean.TRUE.equals(Reflect.call(args.get(2),"isEmpty"))) args.set(2,Reflect.call(Reflect.type("net.minecraft.world.phys.shapes.Shapes"),"box",0d,0d,0d,1d,.35d,1d));
            args.set(6,1f);args.set(7,.82f);args.set(8,.08f);args.set(9,1f);
        }
    }
}
