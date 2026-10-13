package blackoutInteractive.ema_08_.rendering.obj;

import blackoutInteractive.ema_08_.rendering.obj.ObjResourcesLocation;
import blackoutInteractive.ema_08_.rendering.obj.modelsHolders.LoadedModel;
import blackoutInteractive.ema_08_.rendering.obj.modelsHolders.ModelDeclaration;
import com.mojang.math.Transformation;
import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.obj.ObjLoader;
import net.neoforged.neoforge.client.model.obj.ObjModel;
import net.neoforged.neoforge.client.model.renderable.CompositeRenderable;
import org.jetbrains.annotations.Nullable;

public final class ObjModelsManager {
    private static final ArrayList<ModelDeclaration> declarations = new ArrayList();
    private static volatile LoadedModel[] models;

    private ObjModelsManager() {
    }

    public static int declare(ModelDeclaration declaration) {
        Objects.requireNonNull(declaration, "Cannot declare with a null declaration");
        ArrayList<ModelDeclaration> arrayList = declarations;
        synchronized (arrayList) {
            if (models != null) {
                throw new IllegalStateException("Manager is already frozen");
            }
            declarations.add(declaration);
            return declarations.size() - 1;
        }
    }

    public static void freeze() {
        ArrayList<ModelDeclaration> arrayList = declarations;
        synchronized (arrayList) {
            if (models != null) {
                throw new IllegalStateException("Manager is already frozen");
            }
            models = new LoadedModel[declarations.size()];
        }
    }

    public static void invalidate() { if(models!=null) models=new LoadedModel[declarations.size()]; }

    public static LoadedModel get(int idx) {
        LoadedModel model = models[idx];
        if (model == null) {
            ModelDeclaration declaration = declarations.get(idx);
            CompositeRenderable baked = ObjModelsManager.bake(declaration.location);
            ObjModelsManager.models[idx] = model = new LoadedModel(baked, declaration.requiresTransparency ? RenderType::entityTranslucent : RenderType::entityCutout, declaration.getMetadata());

        }
        return model;
    }

    private static CompositeRenderable bake(ObjResourcesLocation data) {
        ObjModel rawModel = ObjLoader.INSTANCE.loadModel(new ObjModel.ModelSettings(data.obj(), true, false, true, false, data.mtl().toString()));
        return rawModel.bakeRenderable((IGeometryBakingContext)new BakingContext(data.name(), data.png()));
    }

    private static class BakingContext
    implements IGeometryBakingContext {
        private final String modelName;
        private final Material texture;

        protected BakingContext(String modelName, ResourceLocation texture) {
            Objects.requireNonNull(texture);
            this.modelName = Objects.requireNonNull(modelName);
            this.texture = new Material(texture, texture);
        }

        public String getModelName() {
            return this.modelName;
        }

        public boolean hasMaterial(String name) {
            return this.modelName.equals(name);
        }

        public Material getMaterial(String name) {
            return this.hasMaterial(name) ? this.texture : null;
        }

        public boolean isGui3d() {
            return true;
        }

        public boolean useBlockLight() {
            return false;
        }

        public boolean useAmbientOcclusion() {
            return true;
        }

        public ItemTransforms getTransforms() {
            return ItemTransforms.NO_TRANSFORMS;
        }

        public Transformation getRootTransform() {
            return Transformation.identity();
        }

        @Nullable
        public ResourceLocation getRenderTypeHint() {
            return null;
        }

        public boolean isComponentVisible(String component, boolean fallback) {
            return fallback;
        }
    }
}

