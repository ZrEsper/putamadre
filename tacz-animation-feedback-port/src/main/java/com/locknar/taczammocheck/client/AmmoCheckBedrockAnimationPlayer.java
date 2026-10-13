package com.locknar.taczammocheck.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.locknar.taczammocheck.AmmoCheckCartridgeState;
import com.locknar.taczammocheck.ConfigMigration;
import com.locknar.taczammocheck.TaczGunCompat;
import com.locknar.taczammocheck.client.AmmoCheckClient;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public final class AmmoCheckBedrockAnimationPlayer {
    private static final List<String> BUILTIN_PRESETS = List.of("aa12", "ai_awp", "ak47", "g36k", "glock_17", "hk416d", "hk_g3", "hk_mp5a5", "m16a1", "m16a4", "m1911", "m320", "m4a1", "m700", "m9a4", "p320", "p90", "scar_h", "scar_l", "timeless50", "ump45", "aug_a3s", "ddm4_pdw", "mk556", "sr25", "cr300", "m870", "m1014", "qbz_191", "aug", "b93r", "cz75", "db_long", "db_short", "deagle", "deagle_golden", "fn_evolys", "fn_fal", "hk_mk23", "kar98", "lonetrail", "m107", "m249", "m95", "mk14", "qbz_95", "rhino357", "rpk", "sks_tactical", "spas_12", "spr15hb", "springfield1873", "taurus500", "taurus943", "type_81", "uzi", "vector45");
    private static final String DEFAULT_ANIMATION = "ammo_check";
    private static final Map<String, AnimationTemplate> TEMPLATE_CACHE = new ConcurrentHashMap<String, AnimationTemplate>();
    private static final Map<Class<?>, Method> GET_MODEL_RENDERER_METHOD_CACHE = new ConcurrentHashMap();
    private static final Map<Class<?>, Field> MODEL_MAP_FIELD_CACHE = new ConcurrentHashMap();
    private static final Map<Class<?>, PartFields> PART_FIELD_CACHE = new ConcurrentHashMap();

    private AmmoCheckBedrockAnimationPlayer() {
    }

    public static String selectPreset(String gunId, TaczGunCompat.GunKind kind) {
        if (TaczGunCompat.isAmmoCheckDisabled(gunId)) {
            return null;
        }
        String normalizedGunId = AmmoCheckBedrockAnimationPlayer.normalize(gunId);
        if (normalizedGunId.isBlank()) {
            return null;
        }
        String explicitAlias = AmmoCheckBedrockAnimationPlayer.explicitPresetAlias(normalizedGunId);
        if (explicitAlias != null) {
            return explicitAlias;
        }
        String best = null;
        int bestScore = 0;
        for (String preset : BUILTIN_PRESETS) {
            String normalizedPreset = AmmoCheckBedrockAnimationPlayer.normalize(preset);
            int score = AmmoCheckBedrockAnimationPlayer.scorePresetMatch(normalizedGunId, normalizedPreset);
            if (score <= bestScore) continue;
            bestScore = score;
            best = preset;
        }
        return bestScore > 0 ? best : null;
    }

    private static String explicitPresetAlias(String normalizedGunId) {
        if (normalizedGunId == null || normalizedGunId.isBlank()) {
            return null;
        }
        if (normalizedGunId.equals("qbz191") || normalizedGunId.contains("qbz191") || normalizedGunId.equals("qbz_191") || normalizedGunId.contains("qbz_191")) {
            return "qbz_191";
        }
        if (normalizedGunId.equals("mcxvirtus") || normalizedGunId.contains("mcxvirtus")) {
            return "mk556";
        }
        if (normalizedGunId.equals("cr300") || normalizedGunId.contains("cr300") || normalizedGunId.equals("haenelcr300") || normalizedGunId.contains("haenelcr300")) {
            return "cr300";
        }
        if (normalizedGunId.equals("camgmk18") || normalizedGunId.contains("camgmk18") || normalizedGunId.equals("g95a1") || normalizedGunId.contains("g95a1")) {
            return "ddm4_pdw";
        }
        if (normalizedGunId.equals("mk18ltl") || normalizedGunId.contains("mk18ltl")) {
            return "m4a1";
        }
        if (normalizedGunId.equals("staccato2011p") || normalizedGunId.contains("staccato2011p")) {
            return "timeless50";
        }
        if (normalizedGunId.equals("benellim4beanbag") || normalizedGunId.contains("benellim4beanbag")) {
            return "m1014";
        }
        if (normalizedGunId.equals("breachingshotgun") || normalizedGunId.contains("breachingshotgun") || normalizedGunId.equals("beanbaggun") || normalizedGunId.contains("beanbaggun")) {
            return "m870";
        }
        return null;
    }

    private static int scorePresetMatch(String normalizedGunId, String normalizedPreset) {
        if (normalizedGunId == null || normalizedPreset == null || normalizedGunId.isBlank() || normalizedPreset.isBlank()) {
            return 0;
        }
        if (normalizedGunId.equals(normalizedPreset)) {
            return 10000 + normalizedPreset.length();
        }
        if (normalizedGunId.endsWith(normalizedPreset)) {
            return 9000 + normalizedPreset.length();
        }
        if (normalizedGunId.contains(normalizedPreset)) {
            return 8000 + normalizedPreset.length();
        }
        if (normalizedPreset.contains(normalizedGunId)) {
            return 5000 + normalizedGunId.length();
        }
        return 0;
    }

    public static void preload(String presetName) {
        AmmoCheckBedrockAnimationPlayer.preload(presetName, DEFAULT_ANIMATION);
    }

    public static void preload(String presetName, String animationName) {
        if (presetName == null || presetName.isBlank() || animationName == null || animationName.isBlank()) {
            return;
        }
        AmmoCheckBedrockAnimationPlayer.getTemplate(presetName, animationName);
    }

    public static boolean hasAnimation(String presetName, String animationName) {
        return presetName != null && !presetName.isBlank() && animationName != null && !animationName.isBlank() && AmmoCheckBedrockAnimationPlayer.getTemplate(presetName, animationName) != null;
    }

    public static String selectOrdinaryAnimation(String presetName, AmmoCheckCartridgeState state) {
        AnimationTemplate normal;
        AnimationTemplate animationTemplate = normal = presetName == null ? null : AmmoCheckBedrockAnimationPlayer.getTemplate(presetName, DEFAULT_ANIMATION);
        if (normal != null && state.shouldHide(normal.options.emptySource, 1) && AmmoCheckBedrockAnimationPlayer.hasAnimation(presetName, "ammo_check_empty")) {
            return "ammo_check_empty";
        }
        return DEFAULT_ANIMATION;
    }

    public static long getEntryBlendMs(String presetName, String animationName, long fallback) {
        AnimationTemplate template = AmmoCheckBedrockAnimationPlayer.getTemplate(presetName, animationName);
        return template != null && template.options.entryBlendMs >= 0L ? template.options.entryBlendMs : fallback;
    }

    public static boolean usesAuthoredSounds(String presetName, String animationName) {
        AnimationTemplate template = AmmoCheckBedrockAnimationPlayer.getTemplate(presetName, animationName);
        return template != null && template.options.authoredSounds;
    }

    public static long getPresetDurationMs(String presetName, long fallbackMs) {
        return AmmoCheckBedrockAnimationPlayer.getPresetDurationMs(presetName, DEFAULT_ANIMATION, fallbackMs);
    }

    public static long getPresetDurationMs(String presetName, String animationName, long fallbackMs) {
        if (presetName == null || presetName.isBlank() || animationName == null || animationName.isBlank()) {
            return Math.max(1L, fallbackMs);
        }
        AnimationTemplate template = AmmoCheckBedrockAnimationPlayer.getTemplate(presetName, animationName);
        if (template == null || template.lengthSeconds <= 0.0f) {
            return Math.max(1L, fallbackMs);
        }
        return Math.max(1L, (long)Math.round(template.lengthSeconds * 1000.0f));
    }

    public static List<AnimationSoundCue> getSoundCuesBetween(String presetName, String animationName, float fromExclusiveSeconds, float toInclusiveSeconds) {
        if (presetName == null || presetName.isBlank() || animationName == null || animationName.isBlank() || toInclusiveSeconds < 0.0f || toInclusiveSeconds < fromExclusiveSeconds) {
            return List.of();
        }
        AnimationTemplate template = AmmoCheckBedrockAnimationPlayer.getTemplate(presetName, animationName);
        if (template == null || template.soundCues == null || template.soundCues.isEmpty()) {
            return List.of();
        }
        ArrayList<AnimationSoundCue> out = new ArrayList<AnimationSoundCue>();
        for (AnimationSoundCue cue : template.soundCues) {
            if (!(cue.timeSeconds > fromExclusiveSeconds) || !(cue.timeSeconds <= toInclusiveSeconds)) continue;
            out.add(cue);
        }
        return out;
    }

    public static boolean apply(Object bedrockGunModel, String presetName, float timeSeconds, float blendWeight, String gunId) {
        return AmmoCheckBedrockAnimationPlayer.apply(bedrockGunModel, presetName, DEFAULT_ANIMATION, timeSeconds, blendWeight, gunId);
    }

    public static boolean apply(Object bedrockGunModel, String presetName, String animationName, float timeSeconds, float blendWeight, String gunId) {
        if (bedrockGunModel == null || presetName == null || presetName.isBlank() || animationName == null || animationName.isBlank()) {
            return false;
        }
        AnimationTemplate template = AmmoCheckBedrockAnimationPlayer.getTemplate(presetName, animationName);
        if (template == null) {
            return false;
        }
        Map<?, ?> modelMap = AmmoCheckBedrockAnimationPlayer.getModelMap(bedrockGunModel);
        if (modelMap == null || modelMap.isEmpty()) {
            return false;
        }
        return AmmoCheckBedrockAnimationPlayer.applyTemplate(modelMap, template, animationName, timeSeconds, blendWeight, null);
    }

    public static boolean applyBranchTransition(Object bedrockGunModel, String presetName, String sourceAnimationName, float sourceTimeSeconds, String targetAnimationName, float targetTimeSeconds, float targetBlendWeight, float baseWeight, String gunId) {
        if (bedrockGunModel == null || presetName == null || presetName.isBlank() || sourceAnimationName == null || sourceAnimationName.isBlank() || targetAnimationName == null || targetAnimationName.isBlank()) {
            return false;
        }
        AnimationTemplate source = AmmoCheckBedrockAnimationPlayer.getTemplate(presetName, sourceAnimationName);
        AnimationTemplate target = AmmoCheckBedrockAnimationPlayer.getTemplate(presetName, targetAnimationName);
        if (source == null || target == null) {
            return false;
        }
        Map<?, ?> modelMap = AmmoCheckBedrockAnimationPlayer.getModelMap(bedrockGunModel);
        if (modelMap == null || modelMap.isEmpty()) {
            return false;
        }
        float alpha = AmmoCheckBedrockAnimationPlayer.clamp01(targetBlendWeight);
        float base = AmmoCheckBedrockAnimationPlayer.clamp01(baseWeight);
        HashSet<String> sharedBones = new HashSet<String>(source.bones.keySet());
        sharedBones.retainAll(target.bones.keySet());
        HashSet<String> sourceOnlyBones = new HashSet<String>(source.bones.keySet());
        sourceOnlyBones.removeAll(target.bones.keySet());
        boolean applied = false;
        if (!sharedBones.isEmpty()) {
            applied |= AmmoCheckBedrockAnimationPlayer.applyTemplate(modelMap, source, sourceAnimationName, sourceTimeSeconds, base, sharedBones);
        }
        if (!sourceOnlyBones.isEmpty()) {
            applied |= AmmoCheckBedrockAnimationPlayer.applyTemplate(modelMap, source, sourceAnimationName, sourceTimeSeconds, base * (1.0f - alpha), sourceOnlyBones);
        }
        return applied |= AmmoCheckBedrockAnimationPlayer.applyTemplate(modelMap, target, targetAnimationName, targetTimeSeconds, base * alpha, null);
    }

    private static boolean applyTemplate(Map<?, ?> modelMap, AnimationTemplate template, String animationName, float timeSeconds, float blendWeight, Set<String> boneFilter) {
        float t = Math.max(0.0f, Math.min(template.lengthSeconds, timeSeconds));
        float weight = AmmoCheckBedrockAnimationPlayer.clamp01(blendWeight);
        boolean appliedAny = false;
        boolean inspectAnimation = DEFAULT_ANIMATION.equals(animationName) || "ammo_check_empty".equals(animationName);
        boolean hideBullet = inspectAnimation && !template.options.exactBones && AmmoCheckClient.shouldHideBulletDuringAmmoCheck();
        for (Map.Entry<String, BoneChannels> entry : template.bones.entrySet()) {
            float[] v;
            Object part;
            if (boneFilter != null && !boneFilter.contains(entry.getKey())) continue;
            boolean bulletBone = AmmoCheckBedrockAnimationPlayer.isBulletLike(entry.getKey());
            Object wrapper = template.options.exactBones ? modelMap.get(entry.getKey()) : AmmoCheckBedrockAnimationPlayer.resolveWrapper(modelMap, entry.getKey());
            if (wrapper == null || (part = AmmoCheckBedrockAnimationPlayer.getModelRenderer(wrapper)) == null) continue;
            if (bulletBone && inspectAnimation && !template.options.exactBones) {
                AmmoCheckBedrockAnimationPlayer.setScale(part, hideBullet ? 0.0f : 1.0f, hideBullet ? 0.0f : 1.0f, hideBullet ? 0.0f : 1.0f, weight);
                appliedAny = true;
                continue;
            }
            BoneChannels channels = entry.getValue();
            if (channels.position != null) {
                v = channels.position.sample(t);
                AmmoCheckBedrockAnimationPlayer.setPosition(part, v[0] / 16.0f, -v[1] / 16.0f, v[2] / 16.0f, weight);
                appliedAny = true;
            }
            if (channels.rotation != null) {
                float[] deg = channels.rotation.sample(t);
                Quaternionf quaternion = AmmoCheckBedrockAnimationPlayer.toQuaternion((float)Math.toRadians(deg[0]), (float)Math.toRadians(deg[1]), (float)Math.toRadians(deg[2]));
                AmmoCheckBedrockAnimationPlayer.setQuaternion(part, quaternion, weight);
                appliedAny = true;
            }
            if (channels.scale == null) continue;
            v = channels.scale.sample(t);
            AmmoCheckBedrockAnimationPlayer.setScale(part, v[0], v[1], v[2], weight);
            appliedAny = true;
        }
        if (template.options.exactBones) {
            AmmoCheckCartridgeState state = AmmoCheckClient.getCartridgeState();
            for (Map.Entry<String, CartridgeRule> entry : template.options.cartridges.entrySet()) {
                Object part;
                if (boneFilter != null && !boneFilter.contains(entry.getKey())) continue;
                CartridgeRule rule = entry.getValue();
                Object wrapper = modelMap.get(entry.getKey());
                if (wrapper == null || !state.shouldHide(rule.source, rule.minimumAmmo) || (part = AmmoCheckBedrockAnimationPlayer.getModelRenderer(wrapper)) == null) continue;
                float maskWeight = animationName.startsWith("ammo_check_reload_") ? 1.0f : weight;
                AmmoCheckBedrockAnimationPlayer.setScale(part, 0.0f, 0.0f, 0.0f, maskWeight);
                appliedAny = true;
            }
        } else if (inspectAnimation && boneFilter == null) {
            appliedAny |= AmmoCheckBedrockAnimationPlayer.setAllBulletBonesVisible(modelMap, !hideBullet);
        }
        return appliedAny;
    }

    public static Boolean inspectionCartridgeVisibility(String bone) {
        if (!AmmoCheckClient.isActive() || !AmmoCheckClient.isOrdinaryAmmoCheck()) {
            return null;
        }
        AnimationTemplate template = AmmoCheckBedrockAnimationPlayer.getTemplate(AmmoCheckClient.getActiveNativePresetName(), AmmoCheckClient.getActiveAnimationName());
        if (template == null || !template.options.exactBones) {
            return null;
        }
        CartridgeRule rule = template.options.cartridges.get(bone);
        if (rule == null) {
            return null;
        }
        if (rule.source.equals("retained_case")) {
            return true;
        }
        AmmoCheckCartridgeState state = AmmoCheckClient.getCartridgeState();
        if (rule.source.equals("magazine") && state.magazineAmmo() < 0 || rule.source.equals("chamber") && state.chamberLoaded() == null) {
            return null;
        }
        return state.shouldHide(rule.source, rule.minimumAmmo) ? Boolean.FALSE : null;
    }

    private static AnimationTemplate getTemplate(String presetName, String animationName) {
        String cacheKey = presetName + "::" + animationName;
        return TEMPLATE_CACHE.computeIfAbsent(cacheKey, ignored -> AmmoCheckBedrockAnimationPlayer.loadTemplate(presetName, animationName));
    }

    private static AnimationTemplate loadTemplate(String presetName, String animationName) {
        JsonObject root = AmmoCheckBedrockAnimationPlayer.loadJsonFromConfig(presetName);
        if (!AmmoCheckBedrockAnimationPlayer.containsAnimation(root, animationName)) {
            root = AmmoCheckBedrockAnimationPlayer.loadJsonFromResource(presetName);
        }
        if (root == null) {
            return null;
        }
        try {
            JsonObject soundsJson;
            JsonObject animations = root.getAsJsonObject("animations");
            if (animations == null || !animations.has(animationName)) {
                return null;
            }
            JsonObject animation = animations.getAsJsonObject(animationName);
            ClipOptions options = AmmoCheckBedrockAnimationPlayer.parseClipOptions(animation);
            float length = animation.has("animation_length") ? animation.get("animation_length").getAsFloat() : 2.55f;
            HashMap<String, BoneChannels> bones = new HashMap<String, BoneChannels>();
            ArrayList<AnimationSoundCue> soundCues = new ArrayList<AnimationSoundCue>();
            JsonObject bonesJson = animation.getAsJsonObject("bones");
            if (bonesJson != null) {
                for (Map.Entry entry : bonesJson.entrySet()) {
                    Channel scale;
                    if (!((JsonElement)entry.getValue()).isJsonObject()) continue;
                    JsonObject bone = ((JsonElement)entry.getValue()).getAsJsonObject();
                    Channel position = bone.has("position") ? AmmoCheckBedrockAnimationPlayer.parseChannel(bone.get("position"), options.exactBones) : null;
                    Channel rotation = bone.has("rotation") ? AmmoCheckBedrockAnimationPlayer.parseChannel(bone.get("rotation"), options.exactBones) : null;
                    Channel channel = scale = bone.has("scale") ? AmmoCheckBedrockAnimationPlayer.parseChannel(bone.get("scale"), options.exactBones) : null;
                    if (position == null && rotation == null && scale == null) continue;
                    bones.put((String)entry.getKey(), new BoneChannels(position, rotation, scale));
                }
            }
            if ((soundsJson = animation.getAsJsonObject("sound_effects")) != null) {
                for (Map.Entry soundEntry : soundsJson.entrySet()) {
                    String effect;
                    JsonObject cueObject;
                    if (!AmmoCheckBedrockAnimationPlayer.isNumeric((String)soundEntry.getKey()) || soundEntry.getValue() == null || !((JsonElement)soundEntry.getValue()).isJsonObject() || !(cueObject = ((JsonElement)soundEntry.getValue()).getAsJsonObject()).has("effect") || !cueObject.get("effect").isJsonPrimitive() || (effect = cueObject.get("effect").getAsString()) == null || effect.isBlank()) continue;
                    soundCues.add(new AnimationSoundCue(Float.parseFloat((String)soundEntry.getKey()), effect));
                }
                soundCues.sort(Comparator.comparingDouble(AnimationSoundCue::timeSeconds));
            }
            return new AnimationTemplate(length, bones, soundCues, options);
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static boolean containsAnimation(JsonObject root, String animationName) {
        if (root == null || animationName == null || animationName.isBlank()) {
            return false;
        }
        try {
            JsonObject animations = root.getAsJsonObject("animations");
            return animations != null && animations.has(animationName);
        }
        catch (Throwable ignored) {
            return false;
        }
    }

    private static ClipOptions parseClipOptions(JsonObject animation) {
        if (!animation.has("locknar_clip")) {
            return animation.has("locknar_authored_sounds") && animation.get("locknar_authored_sounds").getAsBoolean() ? new ClipOptions(false, true, -1L, "", Map.of()) : ClipOptions.LEGACY;
        }
        JsonObject json = animation.getAsJsonObject("locknar_clip");
        HashMap<String, CartridgeRule> cartridges = new HashMap<String, CartridgeRule>();
        if (json.has("cartridge_bones")) {
            for (Map.Entry entry : json.getAsJsonObject("cartridge_bones").entrySet()) {
                JsonObject rule = ((JsonElement)entry.getValue()).getAsJsonObject();
                String source = rule.get("source").getAsString();
                if (!"magazine".equals(source) && !"chamber".equals(source)) continue;
                cartridges.put((String)entry.getKey(), new CartridgeRule(source, rule.has("minimum_ammo") ? Math.max(1, rule.get("minimum_ammo").getAsInt()) : 1));
            }
        }
        return new ClipOptions(true, json.has("authored_sounds") && json.get("authored_sounds").getAsBoolean(), json.has("entry_blend_ms") ? Math.max(0L, Math.min(1000L, json.get("entry_blend_ms").getAsLong())) : -1L, json.has("empty_source") ? json.get("empty_source").getAsString() : "", Map.copyOf(cartridges));
    }

    private static JsonObject loadJsonFromConfig(String presetName) {
        Path path=ConfigMigration.animationOverride("ammo_check_animations/presets/"+presetName+".animation.json");
        if(!Files.isRegularFile(path))return null;
        try(BufferedReader reader=Files.newBufferedReader(path, StandardCharsets.UTF_8)){return JsonParser.parseReader(reader).getAsJsonObject();}catch(Exception ignored){return null;}
    }

    private static JsonObject loadJsonFromResource(String presetName) {
        if (presetName == null || presetName.isBlank()) {
            return null;
        }
        String safePreset = presetName.toLowerCase(Locale.ROOT).replace("\\", "_").replace("/", "_");
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath("locknar_doorkick", "ammo_check_animations/presets/" + safePreset + ".animation.json");
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc == null || mc.getResourceManager() == null) {
                return null;
            }
            Optional resource = mc.getResourceManager().getResource(location);
            if (resource.isEmpty()) {
                return null;
            }
            try (InputStream in = ((Resource)resource.get()).open();){
                JsonObject jsonObject;
                try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);){
                    jsonObject = JsonParser.parseReader((Reader)reader).getAsJsonObject();
                }
                return jsonObject;
            }
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static Channel parseChannel(JsonElement element, boolean preservePrePost) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        if (element.isJsonArray() || element.isJsonPrimitive()) {
            return Channel.constant(AmmoCheckBedrockAnimationPlayer.readVec3(element));
        }
        if (!element.isJsonObject()) {
            return null;
        }
        JsonObject object = element.getAsJsonObject();
        ArrayList<Keyframe> frames = new ArrayList<Keyframe>();
        boolean allKeysAreTimes = true;
        for (String key : object.keySet()) {
            if (AmmoCheckBedrockAnimationPlayer.isNumeric(key)) continue;
            allKeysAreTimes = false;
            break;
        }
        if (!allKeysAreTimes) {
            return null;
        }
        for (Map.Entry entry : object.entrySet()) {
            float[] value;
            float time = Float.parseFloat((String)entry.getKey());
            JsonElement frameElement = (JsonElement)entry.getValue();
            LerpMode lerp = LerpMode.LINEAR;
            float[] incoming = null;
            if (frameElement != null && frameElement.isJsonObject()) {
                String mode;
                JsonObject frameObject = frameElement.getAsJsonObject();
                if (frameObject.has("lerp_mode") && "catmullrom".equalsIgnoreCase(mode = frameObject.get("lerp_mode").getAsString())) {
                    lerp = LerpMode.CATMULLROM;
                }
                value = frameObject.has("post") ? AmmoCheckBedrockAnimationPlayer.readVec3(frameObject.get("post")) : (frameObject.has("pre") ? AmmoCheckBedrockAnimationPlayer.readVec3(frameObject.get("pre")) : new float[]{0.0f, 0.0f, 0.0f});
                if (preservePrePost && frameObject.has("pre")) {
                    incoming = AmmoCheckBedrockAnimationPlayer.readVec3(frameObject.get("pre"));
                }
            } else {
                value = AmmoCheckBedrockAnimationPlayer.readVec3(frameElement);
            }
            frames.add(new Keyframe(time, value, incoming == null ? value : incoming, lerp));
        }
        frames.sort(Comparator.comparingDouble(f -> f.time));
        if (frames.isEmpty()) {
            return null;
        }
        return new Channel(frames);
    }

    private static float[] readVec3(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return new float[]{0.0f, 0.0f, 0.0f};
        }
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            float v = element.getAsFloat();
            return new float[]{v, v, v};
        }
        if (element.isJsonArray()) {
            float[] out = new float[]{0.0f, 0.0f, 0.0f};
            for (int i = 0; i < Math.min(3, element.getAsJsonArray().size()); ++i) {
                out[i] = element.getAsJsonArray().get(i).getAsFloat();
            }
            return out;
        }
        return new float[]{0.0f, 0.0f, 0.0f};
    }

    private static boolean isNumeric(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        try {
            Float.parseFloat(text);
            return true;
        }
        catch (NumberFormatException ignored) {
            return false;
        }
    }

    static Map<?, ?> getModelMap(Object bedrockGunModel) {
        try {
            Map map;
            Field field = MODEL_MAP_FIELD_CACHE.computeIfAbsent(bedrockGunModel.getClass(), cls -> AmmoCheckBedrockAnimationPlayer.findField(cls, "modelMap"));
            if (field == null) {
                return null;
            }
            Object value = field.get(bedrockGunModel);
            return value instanceof Map ? (map = (Map)value) : null;
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static Object resolveWrapper(Map<?, ?> modelMap, String templateBoneName) {
        String[] aliases;
        if (modelMap == null || templateBoneName == null) {
            return null;
        }
        Object exact = modelMap.get(templateBoneName);
        if (exact != null) {
            return exact;
        }
        for (String alias : aliases = AmmoCheckBedrockAnimationPlayer.aliasesFor(templateBoneName)) {
            Object value = modelMap.get(alias);
            if (value == null) continue;
            return value;
        }
        String wanted = AmmoCheckBedrockAnimationPlayer.normalize(templateBoneName);
        Object best = null;
        int bestScore = 0;
        for (Object keyObj : modelMap.keySet()) {
            String key;
            String normalized;
            int score;
            if (!(keyObj instanceof String) || (score = AmmoCheckBedrockAnimationPlayer.fuzzyScore(wanted, normalized = AmmoCheckBedrockAnimationPlayer.normalize(key = (String)keyObj), templateBoneName)) <= bestScore) continue;
            bestScore = score;
            best = modelMap.get(keyObj);
        }
        return bestScore >= 80 ? best : null;
    }

    private static String[] aliasesFor(String boneName) {
        String[] stringArray;
        String b = boneName == null ? "" : boneName.toLowerCase(Locale.ROOT);
        String n = AmmoCheckBedrockAnimationPlayer.normalize(boneName);
        if (AmmoCheckBedrockAnimationPlayer.isMagazineHandLike(n)) {
            return new String[]{"mag_and_lh", "mag_and_left_hand", "mag_and_lefthand", "mag_and_hand", "magazine_and_lh", "magazine_and_left_hand", "magazine_and_lefthand", "magazine_and_hand", "mag_lh", "mag_lefthand", "mag_hand", "magazine_hand", "magazine", "mag", "magazine_main", "mag_main", "clip", "pmag", "stanag"};
        }
        if (AmmoCheckBedrockAnimationPlayer.isGunRightHandLike(n)) {
            return new String[]{"gun_and_righthand", "gun_and_right_hand", "gun_and_rh", "gun_and_hand", "weapon_and_righthand", "weapon_and_right_hand", "righthand", "righthand_pos", "right_hand", "right_hand_pos", "rightarm", "right_arm", "hand_right"};
        }
        switch (b) {
            case "lefthand": {
                String[] stringArray2 = new String[6];
                stringArray2[0] = "lefthand_pos";
                stringArray2[1] = "left_hand";
                stringArray2[2] = "left_hand_pos";
                stringArray2[3] = "leftarm";
                stringArray2[4] = "left_arm";
                stringArray = stringArray2;
                stringArray2[5] = "hand_left";
                break;
            }
            case "righthand": {
                String[] stringArray3 = new String[6];
                stringArray3[0] = "righthand_pos";
                stringArray3[1] = "right_hand";
                stringArray3[2] = "right_hand_pos";
                stringArray3[3] = "rightarm";
                stringArray3[4] = "right_arm";
                stringArray = stringArray3;
                stringArray3[5] = "hand_right";
                break;
            }
            case "mag_release": {
                String[] stringArray4 = new String[5];
                stringArray4[0] = "magrelease";
                stringArray4[1] = "magazine_release";
                stringArray4[2] = "magazine_release_button";
                stringArray4[3] = "mag_button";
                stringArray = stringArray4;
                stringArray4[4] = "button_mag_release";
                break;
            }
            case "bullet": {
                String[] stringArray5 = new String[4];
                stringArray5[0] = "bullets";
                stringArray5[1] = "bullet_model";
                stringArray5[2] = "cartridge";
                stringArray = stringArray5;
                stringArray5[3] = "round";
                break;
            }
            case "root": {
                String[] stringArray6 = new String[5];
                stringArray6[0] = "gun";
                stringArray6[1] = "main";
                stringArray6[2] = "body";
                stringArray6[3] = "weapon";
                stringArray = stringArray6;
                stringArray6[4] = "all";
                break;
            }
            default: {
                stringArray = new String[]{};
            }
        }
        return stringArray;
    }

    private static int fuzzyScore(String wanted, String actual, String originalTemplateBone) {
        if (wanted.isEmpty() || actual.isEmpty()) {
            return 0;
        }
        if (actual.equals(wanted)) {
            return 100;
        }
        if (actual.contains(wanted) || wanted.contains(actual)) {
            return 90;
        }
        String o = originalTemplateBone == null ? "" : originalTemplateBone.toLowerCase(Locale.ROOT);
        String originalNormalized = AmmoCheckBedrockAnimationPlayer.normalize(originalTemplateBone);
        if (AmmoCheckBedrockAnimationPlayer.isMagazineHandLike(originalNormalized)) {
            if (AmmoCheckBedrockAnimationPlayer.isMagazineHandLike(actual)) {
                return 94;
            }
            if (actual.contains("magazine") || actual.equals("mag") || actual.contains("mag")) {
                return 88;
            }
            if (actual.contains("clip")) {
                return 84;
            }
        }
        if (AmmoCheckBedrockAnimationPlayer.isGunRightHandLike(originalNormalized) && AmmoCheckBedrockAnimationPlayer.isGunRightHandLike(actual)) {
            return 92;
        }
        if ("lefthand".equals(o) && actual.contains("left") && actual.contains("hand")) {
            return 88;
        }
        if ("righthand".equals(o) && actual.contains("right") && actual.contains("hand")) {
            return 88;
        }
        if ("mag_release".equals(o) && actual.contains("mag") && (actual.contains("release") || actual.contains("button") || actual.contains("catch"))) {
            return 88;
        }
        if (AmmoCheckBedrockAnimationPlayer.isBulletLike(o) && AmmoCheckBedrockAnimationPlayer.isBulletLike(actual)) {
            return 86;
        }
        return 0;
    }

    private static boolean isMagazineHandLike(String normalized) {
        if (normalized == null || normalized.isBlank()) {
            return false;
        }
        boolean hasMag = normalized.contains("mag") || normalized.contains("magazine") || normalized.contains("clip") || normalized.contains("pmag") || normalized.contains("stanag");
        boolean hasHand = normalized.contains("hand") || normalized.contains("lefthand") || normalized.contains("lh") || normalized.contains("left");
        return hasMag && hasHand;
    }

    private static boolean isGunRightHandLike(String normalized) {
        if (normalized == null || normalized.isBlank()) {
            return false;
        }
        boolean hasGun = normalized.contains("gun") || normalized.contains("weapon");
        boolean hasHand = normalized.contains("hand") || normalized.contains("righthand") || normalized.contains("rh") || normalized.contains("right");
        return hasGun && hasHand;
    }

    private static boolean isBulletLike(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String raw = value.toLowerCase(Locale.ROOT);
        String n = AmmoCheckBedrockAnimationPlayer.normalize(value);
        if (n.isBlank()) {
            return false;
        }
        if (n.contains("mag") || n.contains("magazine") || n.contains("clip") || n.contains("pmag") || n.contains("stanag") || n.contains("hand") || n.contains("lefthand") || n.contains("righthand") || n.endsWith("lh") || n.endsWith("rh") || n.contains("gun") || n.contains("weapon") || n.contains("root")) {
            return false;
        }
        if (n.equals("bullet") || n.equals("bullets") || n.equals("bulletmodel") || n.equals("bulletmesh") || n.equals("cartridge") || n.equals("cartridges") || n.equals("round") || n.equals("rounds") || n.equals("ammo") || n.equals("ammoround") || n.equals("ammomodel")) {
            return true;
        }
        String digitsOnlySuffix = n.replaceFirst("^(bullet|bullets|cartridge|cartridges|round|rounds|ammo)", "");
        if (!digitsOnlySuffix.equals(n) && digitsOnlySuffix.matches("\\d*")) {
            return true;
        }
        return raw.matches("(?i)^(bullet|bullets|cartridge|cartridges|round|rounds|ammo)[_\\- ]?\\d*$");
    }

    private static boolean setAllBulletBonesVisible(Map<?, ?> modelMap, boolean visible) {
        boolean changed = false;
        if (modelMap == null || modelMap.isEmpty()) {
            return false;
        }
        float scale = visible ? 1.0f : 0.0f;
        for (Map.Entry<?, ?> entry : modelMap.entrySet()) {
            Object part;
            String key;
            Object obj = entry.getKey();
            if (!(obj instanceof String) || !AmmoCheckBedrockAnimationPlayer.isBulletLike(key = (String)obj) || (part = AmmoCheckBedrockAnimationPlayer.getModelRenderer(entry.getValue())) == null) continue;
            AmmoCheckBedrockAnimationPlayer.setScale(part, scale, scale, scale, 1.0f);
            changed = true;
        }
        return changed;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String lower = value.toLowerCase(Locale.ROOT);
        int namespace = lower.indexOf(58);
        if (namespace >= 0 && namespace + 1 < lower.length()) {
            lower = lower.substring(namespace + 1);
        }
        return lower.replace("_", "").replace("-", "").replace(".", "").replace(" ", "").replace("/", "").replace("\\", "");
    }

    private static Object getModelRenderer(Object wrapper) {
        try {
            Method method = GET_MODEL_RENDERER_METHOD_CACHE.computeIfAbsent(wrapper.getClass(), cls -> {
                try {
                    Method m = cls.getMethod("getModelRenderer", new Class[0]);
                    m.setAccessible(true);
                    return m;
                }
                catch (Throwable ignored) {
                    return null;
                }
            });
            return method == null ? null : method.invoke(wrapper, new Object[0]);
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static void setPosition(Object part, float x, float y, float z, float weight) {
        PartFields fields = AmmoCheckBedrockAnimationPlayer.fields(part);
        if (fields == null) {
            return;
        }
        float w = AmmoCheckBedrockAnimationPlayer.clamp01(weight);
        try {
            float baseX = fields.offsetX.getFloat(part);
            float baseY = fields.offsetY.getFloat(part);
            float baseZ = fields.offsetZ.getFloat(part);
            fields.offsetX.setFloat(part, AmmoCheckBedrockAnimationPlayer.lerp(baseX, x, w));
            fields.offsetY.setFloat(part, AmmoCheckBedrockAnimationPlayer.lerp(baseY, y, w));
            fields.offsetZ.setFloat(part, AmmoCheckBedrockAnimationPlayer.lerp(baseZ, z, w));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void setQuaternion(Object part, Quaternionf quaternion, float weight) {
        PartFields fields = AmmoCheckBedrockAnimationPlayer.fields(part);
        if (fields == null) {
            return;
        }
        float w = AmmoCheckBedrockAnimationPlayer.clamp01(weight);
        try {
            Object value = fields.additionalQuaternion.get(part);
            if (value instanceof Quaternionf) {
                Quaternionf q = (Quaternionf)value;
                Quaternionf blended = new Quaternionf((Quaternionfc)q);
                blended.slerp((Quaternionfc)quaternion, w);
                q.set((Quaternionfc)blended);
            } else {
                Quaternionf blended = new Quaternionf();
                blended.slerp((Quaternionfc)quaternion, w);
                fields.additionalQuaternion.set(part, blended);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void setScale(Object part, float x, float y, float z, float weight) {
        PartFields fields = AmmoCheckBedrockAnimationPlayer.fields(part);
        if (fields == null) {
            return;
        }
        float w = AmmoCheckBedrockAnimationPlayer.clamp01(weight);
        try {
            float baseX = fields.xScale.getFloat(part);
            float baseY = fields.yScale.getFloat(part);
            float baseZ = fields.zScale.getFloat(part);
            fields.xScale.setFloat(part, AmmoCheckBedrockAnimationPlayer.lerp(baseX, x, w));
            fields.yScale.setFloat(part, AmmoCheckBedrockAnimationPlayer.lerp(baseY, y, w));
            fields.zScale.setFloat(part, AmmoCheckBedrockAnimationPlayer.lerp(baseZ, z, w));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static float lerp(float from, float to, float weight) {
        return from + (to - from) * AmmoCheckBedrockAnimationPlayer.clamp01(weight);
    }

    private static float clamp01(float value) {
        if (value < 0.0f) {
            return 0.0f;
        }
        if (value > 1.0f) {
            return 1.0f;
        }
        return value;
    }

    private static PartFields fields(Object part) {
        if (part == null) {
            return null;
        }
        return PART_FIELD_CACHE.computeIfAbsent(part.getClass(), cls -> {
            try {
                Field offsetX = AmmoCheckBedrockAnimationPlayer.findField(cls, "offsetX");
                Field offsetY = AmmoCheckBedrockAnimationPlayer.findField(cls, "offsetY");
                Field offsetZ = AmmoCheckBedrockAnimationPlayer.findField(cls, "offsetZ");
                Field additionalQuaternion = AmmoCheckBedrockAnimationPlayer.findField(cls, "additionalQuaternion");
                Field xScale = AmmoCheckBedrockAnimationPlayer.findField(cls, "xScale");
                Field yScale = AmmoCheckBedrockAnimationPlayer.findField(cls, "yScale");
                Field zScale = AmmoCheckBedrockAnimationPlayer.findField(cls, "zScale");
                if (offsetX == null || offsetY == null || offsetZ == null || additionalQuaternion == null || xScale == null || yScale == null || zScale == null) {
                    return null;
                }
                offsetX.setAccessible(true);
                offsetY.setAccessible(true);
                offsetZ.setAccessible(true);
                additionalQuaternion.setAccessible(true);
                xScale.setAccessible(true);
                yScale.setAccessible(true);
                zScale.setAccessible(true);
                return new PartFields(offsetX, offsetY, offsetZ, additionalQuaternion, xScale, yScale, zScale);
            }
            catch (Throwable ignored) {
                return null;
            }
        });
    }

    private static Field findField(Class<?> type, String name) {
        for (Class<?> current = type; current != null && current != Object.class; current = current.getSuperclass()) {
            try {
                Field field = current.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            }
            catch (Throwable ignored) {
                continue;
            }
        }
        return null;
    }

    private static Quaternionf toQuaternion(float pitch, float yaw, float roll) {
        double cy = Math.cos((double)roll * 0.5);
        double sy = Math.sin((double)roll * 0.5);
        double cp = Math.cos((double)yaw * 0.5);
        double sp = Math.sin((double)yaw * 0.5);
        double cr = Math.cos((double)pitch * 0.5);
        double sr = Math.sin((double)pitch * 0.5);
        return new Quaternionf((float)(cy * cp * sr - sy * sp * cr), (float)(sy * cp * sr + cy * sp * cr), (float)(sy * cp * cr - cy * sp * sr), (float)(cy * cp * cr + sy * sp * sr));
    }

    private static float catmull(float p0, float p1, float p2, float p3, float alpha) {
        float v0 = (p2 - p0) * 0.5f;
        float v1 = (p3 - p1) * 0.5f;
        float t2 = alpha * alpha;
        float t3 = alpha * t2;
        float h1 = 2.0f * t3 - 3.0f * t2 + 1.0f;
        float h2 = -2.0f * t3 + 3.0f * t2;
        float h3 = t3 - 2.0f * t2 + alpha;
        float h4 = t3 - t2;
        return h1 * p1 + h2 * p2 + h3 * v0 + h4 * v1;
    }

    private record AnimationTemplate(float lengthSeconds, Map<String, BoneChannels> bones, List<AnimationSoundCue> soundCues, ClipOptions options) {
    }

    private record ClipOptions(boolean exactBones, boolean authoredSounds, long entryBlendMs, String emptySource, Map<String, CartridgeRule> cartridges) {
        private static final ClipOptions LEGACY = new ClipOptions(false, false, -1L, "", Map.of());
    }

    public record AnimationSoundCue(float timeSeconds, String effect) {
    }

    private record BoneChannels(Channel position, Channel rotation, Channel scale) {
    }

    private static final class Channel {
        private final List<Keyframe> frames;

        private Channel(List<Keyframe> frames) {
            this.frames = frames;
        }

        static Channel constant(float[] value) {
            ArrayList<Keyframe> list = new ArrayList<Keyframe>();
            list.add(new Keyframe(0.0f, value, value, LerpMode.LINEAR));
            return new Channel(list);
        }

        float[] sample(float time) {
            int i;
            if (this.frames.isEmpty()) {
                return new float[]{0.0f, 0.0f, 0.0f};
            }
            if (time < this.frames.get((int)0).time) {
                return (float[])this.frames.get((int)0).incoming.clone();
            }
            if (this.frames.size() == 1 || time == this.frames.get((int)0).time) {
                return (float[])this.frames.get((int)0).value.clone();
            }
            int last = this.frames.size() - 1;
            if (time >= this.frames.get((int)last).time) {
                return (float[])this.frames.get((int)last).value.clone();
            }
            for (i = 0; i < last; ++i) {
                if (time == this.frames.get((int)(i + 1)).time) {
                    return (float[])this.frames.get((int)(i + 1)).value.clone();
                }
                if (time >= this.frames.get((int)i).time && time <= this.frames.get((int)(i + 1)).time) break;
            }
            Keyframe from = this.frames.get(i);
            Keyframe to = this.frames.get(i + 1);
            float span = Math.max(1.0E-4f, to.time - from.time);
            float alpha = Math.max(0.0f, Math.min(1.0f, (time - from.time) / span));
            float[] out = new float[]{0.0f, 0.0f, 0.0f};
            if (from.lerp == LerpMode.CATMULLROM && this.frames.size() >= 3) {
                Keyframe p0 = this.frames.get(Math.max(0, i - 1));
                Keyframe p3 = this.frames.get(Math.min(last, i + 2));
                for (int axis = 0; axis < 3; ++axis) {
                    out[axis] = AmmoCheckBedrockAnimationPlayer.catmull(p0.value[axis], from.value[axis], to.incoming[axis], p3.incoming[axis], alpha);
                }
            } else {
                for (int axis = 0; axis < 3; ++axis) {
                    out[axis] = from.value[axis] + (to.incoming[axis] - from.value[axis]) * alpha;
                }
            }
            return out;
        }
    }

    private record CartridgeRule(String source, int minimumAmmo) {
    }

    private static enum LerpMode {
        LINEAR,
        CATMULLROM;

    }

    private record Keyframe(float time, float[] value, float[] incoming, LerpMode lerp) {
    }

    private record PartFields(Field offsetX, Field offsetY, Field offsetZ, Field additionalQuaternion, Field xScale, Field yScale, Field zScale) {
    }
}
