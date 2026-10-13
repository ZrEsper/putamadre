package me.xjqsh.lesraisinsarmor.resource;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import me.xjqsh.lesraisinsarmor.resource.data.ArmorData;
import me.xjqsh.lesraisinsarmor.util.LocUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.core.registries.BuiltInRegistries;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

public class ArmorDataManager
extends SimplePreparableReloadListener<Map<ResourceLocation, ArmorData>> {
    private static final Predicate<ResourceLocation> FILTER = rl -> rl.getPath().endsWith(".json");
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, (Object)new ResourceLocation.Serializer()).registerTypeAdapter(ArmorData.class, (Object)new ArmorData.Deserializer()).setPrettyPrinting().create();
    private static ArmorDataManager INSTANCE;
    private Map<ResourceLocation, String> networkCache;

    public static ArmorDataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ArmorDataManager();
        }
        return INSTANCE;
    }

    public Map<ResourceLocation, String> getNetworkCache() {
        return this.networkCache;
    }

    @NotNull
    public String getName() {
        return "lr_armor_data";
    }

    @NotNull
    protected Map<ResourceLocation, ArmorData> prepare(@NotNull ResourceManager manager, @NotNull ProfilerFiller pProfiler) {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        ImmutableMap.Builder cache = new ImmutableMap.Builder();
        manager.listResources("armor_data", FILTER).forEach((rl, resource) -> {
            try (InputStream reader = resource.open();){
                ResourceLocation configLocation = LocUtil.fromFile(rl, "armor_data");
                String json = IOUtils.toString((InputStream)reader, (Charset)StandardCharsets.UTF_8);
                ArmorData config = (ArmorData)GSON.fromJson(json, ArmorData.class);
                if (config != null && configLocation != null) {
                    builder.put((Object)configLocation, (Object)config);
                    cache.put((Object)configLocation, (Object)json);
                }
            }
            catch (JsonIOException | JsonSyntaxException | IOException e) {
                LesRaisinsArmor.LOGGER.warn("Failed to load armor config {}!", rl, (Object)e);
            }
        });
        this.networkCache = cache.build();
        return builder.build();
    }

    protected void apply(@NotNull Map<ResourceLocation, ArmorData> map, @NotNull ResourceManager manager, @NotNull ProfilerFiller pProfiler) {
        for (Map.Entry<ResourceLocation, ArmorData> entry : map.entrySet()) {
            for (ArmorItem.Type type : ArmorItem.Type.values()) {
                Object object;
                ResourceLocation rl = ResourceLocation.tryBuild((String)entry.getKey().getNamespace(), (String)(entry.getKey().getPath() + "_" + type.getName()));
                if (rl == null || !((object = BuiltInRegistries.ITEM.get(rl)) instanceof LrArmorItem)) continue;
                LrArmorItem item = (LrArmorItem)((Object)object);
                item.setArmorData(new ArmorDataSupplier(entry.getValue()));
            }
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void fromNetwork(Map<ResourceLocation, String> networkCache) {
        for (Map.Entry<ResourceLocation, String> entry : networkCache.entrySet()) {
            try {
                ArmorData armorData = (ArmorData)GSON.fromJson(entry.getValue(), ArmorData.class);
                if (armorData == null) continue;
                for (ArmorItem.Type type : ArmorItem.Type.values()) {
                    Object object;
                    ResourceLocation rl = ResourceLocation.tryBuild((String)entry.getKey().getNamespace(), (String)(entry.getKey().getPath() + "_" + type.getName()));
                    if (rl == null || !((object = BuiltInRegistries.ITEM.get(rl)) instanceof LrArmorItem)) continue;
                    LrArmorItem item = (LrArmorItem)((Object)object);
                    item.setArmorData(new ArmorDataSupplier(armorData));
                }
            }
            catch (JsonSyntaxException e) {
                LesRaisinsArmor.LOGGER.warn("Failed to load armor config from network {}!", (Object)entry.getKey(), (Object)e);
            }
        }
    }

    public static class ArmorDataSupplier
    implements Supplier<ArmorData> {
        private final ArmorData armorData;

        private ArmorDataSupplier(@NotNull ArmorData armorData) {
            this.armorData = armorData;
        }

        @Override
        public ArmorData get() {
            return this.armorData;
        }
    }
}
