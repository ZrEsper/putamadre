package blackoutInteractive.ema_08_.rendering.obj;

import blackoutInteractive.ema_08_.rendering.obj.ObjModelsManager;
import blackoutInteractive.ema_08_.rendering.obj.modelsHolders.ModelDeclaration;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;

public final class SideSafeModelDeclarator {
    private SideSafeModelDeclarator() {
    }

    public static int decleare(ModelDeclaration declaration) {
        if (FMLEnvironment.dist.isClient()) {
            return SideSafeModelDeclarator.declare0(declaration);
        }
        return -1;
    }

    @OnlyIn(value=Dist.CLIENT)
    private static int declare0(ModelDeclaration declaration) {
        return ObjModelsManager.declare(declaration);
    }
}

