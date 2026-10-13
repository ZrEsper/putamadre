package blackoutInteractive.ema_08_.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class TrivialEqualityDeduper<T> {
    private final HashMap<T, Data> known = new HashMap();

    private void put0(T t) {
        this.known.put(t, new Data(t));
    }

    private Data get0(T k) {
        return this.known.get(k);
    }

    public T dedupe(T value) {
        Data d = this.get0(Objects.requireNonNull(value, "Cannot deduplicate null values"));
        if (d == null) {
            this.put0(value);
            return value;
        }
        return d.getForDeduping();
    }

    public boolean contains(T value) {
        return null != this.get0(Objects.requireNonNull(value, "Cannot check content against null values"));
    }

    public boolean containsExact(T value) {
        Data d = this.get0(Objects.requireNonNull(value, "Cannot check content against null values"));
        return d != null && d.value == value;
    }

    public boolean put(T value) {
        Data d = this.get0(Objects.requireNonNull(value, "Cannot check content against null values"));
        if (d == null) {
            this.put0(value);
            return true;
        }
        return false;
    }

    public void ensurePresentExact(T value) {
        Data d = this.get0(Objects.requireNonNull(value, "Cannot check content against null values"));
        if (d == null) {
            this.put0(value);
        } else if (d.value != value) {
            throw new IllegalStateException("Another equal instance is already present");
        }
    }

    public int size() {
        return this.known.size();
    }

    public String dump() {
        HashMap<Class, HashMap> dump = new HashMap<Class, HashMap>();
        int dedupAccesses = 0;
        for (Data entry : this.known.values()) {
            HashMap<Object,Integer> cdump = dump.computeIfAbsent(entry.value.getClass(), c -> {
                HashMap<Object, Integer> map = new HashMap<Object, Integer>();
                map.put(null, 0);
                return map;
            });
            cdump.put(entry.value, entry.dedupAccesses);
            cdump.merge(null, entry.dedupAccesses, Integer::sum);
            dedupAccesses += entry.dedupAccesses;
        }
        StringBuilder dumpb = new StringBuilder("Total size: ").append(this.known.size()).append("\nDeduplication accesses: ").append(dedupAccesses);
        dumpb.append("\nContents and deduplication accesses by concrete type:");
        for (Map.Entry entry : dump.entrySet()) {
            dumpb.append("\n - Type: ").append(((Class)entry.getKey()).getName()).append(" (objects = ").append(((HashMap)entry.getValue()).size() - 1).append(") (dedup_accesses = ").append(((HashMap)entry.getValue()).get(null)).append(")");
            for (Object rawObject : ((HashMap)entry.getValue()).entrySet()) {
                Map.Entry object=(Map.Entry)rawObject;
                Object k = object.getKey();
                if (k == null) continue;
                dumpb.append("\n    - ").append(k.toString()).append(": ").append((Integer)object.getValue());
            }
        }
        return dumpb.toString();
    }

    private class Data {
        private int dedupAccesses;
        private final T value;

        private Data(T v) {
            this.value = v;
        }

        private T getForDeduping() {
            ++this.dedupAccesses;
            return this.value;
        }
    }
}

