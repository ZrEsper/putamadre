package blackoutInteractive.ema_08_.misc;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Freezable<I, F> {
    private final Object l1;
    private final Object l2;
    private volatile I i;
    private volatile F f;
    private volatile boolean frozen = false;
    private final Function<I, F> freezer;

    protected Freezable(boolean needsPreFreezingLock, boolean needsPostFreezingLock, Function<I, F> f, Supplier<I> initialState) {
        this.l1 = needsPreFreezingLock ? new Object() : null;
        this.l2 = needsPostFreezingLock ? new Object() : null;
        this.freezer = Objects.requireNonNull(f, "A freezer function must be provided");
        this.i = Objects.requireNonNull(Objects.requireNonNull(initialState, "An initial state supplier must be provided").get(), "Initial state cannot be null");
    }

    protected final Object getPreFreezingLock() {
        if (this.l1 == null) {
            throw new IllegalStateException("This instance does not provide a pre-freezing lock");
        }
        return this.l1;
    }

    protected final Object getPostFreezingLock() {
        if (this.l2 == null) {
            throw new IllegalStateException("This instance does not provide a post-freezing lock");
        }
        return this.l2;
    }

    private static void assertLockHeld(Object l, String lockName) {
        if (l != null && !Thread.holdsLock(l)) {
            throw new IllegalStateException(String.format("Lock %s must be held in order to perform this operation", lockName));
        }
    }

    protected final I getPreFreezingData() {
        Freezable.assertLockHeld(this.l1, "pre-freezing");
        if (this.frozen) {
            throw new IllegalStateException("This instance is already frozen");
        }
        return this.i;
    }

    protected final F getPostFreezingData() {
        Freezable.assertLockHeld(this.l2, "post-freezing");
        if (!this.frozen) {
            throw new IllegalStateException("This instance is not yet frozen");
        }
        return this.f;
    }

    public final void freeze() {
        Object object = this.l1 == null ? new Object() : this.l1;
        synchronized (object) {
            Object object2 = this.l2 == null ? new Object() : this.l2;
            synchronized (object2) {
                if (this.frozen) {
                    throw new IllegalStateException("This instance is already frozen");
                }
                F data = Objects.requireNonNull(this.freezer.apply(this.i), "Final state cannot be null");
                this.f = data;
                this.i = null;
                this.frozen = true;
            }
        }
    }
}

