package blackoutInteractive.ema_08_.rendering.twoToThreeD;

import blackoutInteractive.ema_08_.rendering.twoToThreeD.IRenderablePngProvider;
import blackoutInteractive.ema_08_.rendering.twoToThreeD.RenderablePng;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public class RenderablePngsManager {
    private static final ConcurrentHashMap<String, RenderablePng> cache = new ConcurrentHashMap();

    public static void invalidate() {cache.clear();}
    private static String hash(ResourceLocation texture) {
        return "@" + String.valueOf(texture);
    }

    public static RenderablePng transform(String textureName, ResourceLocation texture) {
        int[] wh = RenderablePngsManager.wh(texture);
        return new RenderablePng(texture, textureName, wh[0], wh[1]);
    }

    public static RenderablePng transformOrGet(String textureName, ResourceLocation texture) {
        return cache.computeIfAbsent(RenderablePngsManager.hash(texture), hash -> RenderablePngsManager.transform(textureName, texture));
    }

    public static RenderablePng transformOrGet(IRenderablePngProvider provider) {
        return RenderablePngsManager.transformOrGet(provider.getTextureName(), provider.getTexturePng());
    }

    private static int[] wh(ResourceLocation loc) {
        try(InputStream in=Minecraft.getInstance().getResourceManager().getResource(loc).orElseThrow().open(); NativeImage img=NativeImage.read(in)) { return new int[]{img.getWidth(),img.getHeight()}; }
        catch(IOException e) { throw new IllegalArgumentException("Could not load patch texture "+loc,e); }
    }
}
