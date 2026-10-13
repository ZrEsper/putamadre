package blackoutInteractive.brimmArmors.common.workbench;

import blackoutInteractive.brimmArmors.common.workbench.Craft;
import blackoutInteractive.brimmArmors.common.workbench.CraftBuilder;
import blackoutInteractive.brimmArmors.common.workbench.CraftSection;
import blackoutInteractive.ema_08_.misc.Freezable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;

public final class CraftsManager
extends Freezable<Map<CraftSection, ArrayList<CraftBuilder>>, CraftsManager.BuiltState> {
    private static final List<CraftSection> orderedSections = List.of(CraftSection.PLATES, CraftSection.HELMETS, CraftSection.CHESTPLATES, CraftSection.LEGGINGS, CraftSection.BOOTS, CraftSection.PATCHES);
    private static final CraftsManager INSTANCE = new CraftsManager();

    private CraftsManager() {
        super(true, false, pfd -> {
            HashMap mutableBuilt = new HashMap();
            for (CraftSection section : orderedSections) {
                ArrayList<CraftBuilder> crafts = (ArrayList)pfd.get((Object)section);
                mutableBuilt.put(section, crafts.stream().map(CraftBuilder::build).collect(Collectors.toUnmodifiableList()));
            }
            HashMap<CraftSection, CraftsSectionAccessor> accessors = new HashMap<CraftSection, CraftsSectionAccessor>();
            Map<CraftSection, List<Craft>> built = Collections.unmodifiableMap(mutableBuilt);
            for (CraftSection section : built.keySet()) {
                accessors.put(section, new CraftsSectionAccessor(section, built.get((Object)section)));
            }
            pfd.clear();
            return new BuiltState(built, accessors);
        }, () -> {
            HashMap map = new HashMap();
            for (CraftSection section : orderedSections) {
                map.put(section, new ArrayList());
            }
            return map;
        });
    }

    public static void register(CraftBuilder builder, CraftSection section) {
        Object object = INSTANCE.getPreFreezingLock();
        synchronized (object) {
            Objects.requireNonNull((ArrayList)((Map)INSTANCE.getPreFreezingData()).get((Object)Objects.requireNonNull(section)), "Missing section list: " + String.valueOf((Object)section)).add(Objects.requireNonNull(builder));
        }
    }

    public static void buildAll() {
        INSTANCE.freeze();
    }

    public static Craft byId(ResourceLocation id) {
        for (List<Craft> crafts : ((BuiltState)CraftsManager.INSTANCE.getPostFreezingData()).built.values()) {
            Craft match = crafts.stream().filter(c -> c.id().equals((Object)id)).findFirst().orElse(null);
            if (match == null) continue;
            return match;
        }
        return null;
    }

    public static CraftSection firstSection() {
        INSTANCE.getPostFreezingData();
        return orderedSections.get(0);
    }

    public static CraftSection lastSection() {
        INSTANCE.getPostFreezingData();
        return orderedSections.get(orderedSections.size() - 1);
    }

    public static CraftSection nextSection(CraftSection current) {
        INSTANCE.getPostFreezingData();
        int idx = orderedSections.indexOf((Object)current);
        if (idx == -1) {
            throw new IllegalArgumentException("Invalid section");
        }
        return idx == orderedSections.size() - 1 ? null : orderedSections.get(idx + 1);
    }

    public static CraftSection prevSection(CraftSection current) {
        INSTANCE.getPostFreezingData();
        int idx = orderedSections.indexOf((Object)current);
        if (idx == -1) {
            throw new IllegalArgumentException("Invalid section");
        }
        return idx == 0 ? null : orderedSections.get(idx - 1);
    }

    public static CraftsSectionAccessor accessor(CraftSection section) {
        return Objects.requireNonNull(((BuiltState)CraftsManager.INSTANCE.getPostFreezingData()).accessors.get((Object)section), "Invalid section");
    }

    protected record BuiltState(Map<CraftSection, List<Craft>> built, Map<CraftSection, CraftsSectionAccessor> accessors) {
    }

    public static final class CraftsSectionAccessor {
        private final CraftSection section;
        private final List<Craft> cached;

        private CraftsSectionAccessor(CraftSection section, List<Craft> crafts) {
            this.section = section;
            this.cached = Objects.requireNonNull(crafts, "No data to access for " + String.valueOf((Object)section));
        }

        public Craft next(Craft craft) {
            int idx = this.cached.indexOf(craft);
            if (idx == -1) {
                throw new IllegalArgumentException("Unregistered craft");
            }
            return idx == this.cached.size() - 1 ? null : this.cached.get(idx + 1);
        }

        public Craft prev(Craft craft) {
            int idx = this.cached.indexOf(craft);
            if (idx == -1) {
                throw new IllegalArgumentException("Unregistered craft");
            }
            return idx == 0 ? null : this.cached.get(idx - 1);
        }

        public Craft first() {
            return this.cached.isEmpty() ? null : this.cached.get(0);
        }

        public Craft last() {
            return this.cached.isEmpty() ? null : this.cached.get(this.cached.size() - 1);
        }

        public CraftSection section() {
            return this.section;
        }
    }
}

