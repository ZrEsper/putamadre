package me.xjqsh.lesraisinsarmor.client.resource;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.client.resource.data.ArmorRenderConfig;
import me.xjqsh.lesraisinsarmor.util.LocUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

public class ArmorRenderConfigManager
implements ResourceManagerReloadListener {
    private static final Predicate<ResourceLocation> FILTER = rl -> rl.getPath().endsWith(".json");
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, (Object)new ResourceLocation.Serializer()).setPrettyPrinting().create();
    private static ArmorRenderConfigManager INSTANCE;
    private Map<ResourceLocation, ArmorRenderConfig> configMap = new HashMap<ResourceLocation, ArmorRenderConfig>();

    public static ArmorRenderConfigManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ArmorRenderConfigManager();
        }
        return INSTANCE;
    }

    public void onResourceManagerReload(@NotNull ResourceManager manager) {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        manager.listResources("armor_display", FILTER).forEach((rl, resource) -> {
            try (BufferedReader reader = resource.openAsReader();){
                ResourceLocation configLocation = LocUtil.fromFile(rl, "armor_display");
                ArmorRenderConfig config = (ArmorRenderConfig)GSON.fromJson((Reader)reader, ArmorRenderConfig.class);
                if (config != null && configLocation != null) {
                    builder.put((Object)configLocation, (Object)config);
                }
            }
            catch (JsonIOException | JsonSyntaxException | IOException e) {
                LesRaisinsArmor.LOGGER.warn("Failed to load armor display config!", e);
            }
        });
        this.configMap = builder.build();
    }

    public ArmorRenderConfig getConfig(ResourceLocation rl) {
        return this.configMap.get(rl);
    }
}
