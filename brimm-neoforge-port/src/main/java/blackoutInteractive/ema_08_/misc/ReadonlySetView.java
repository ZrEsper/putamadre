package blackoutInteractive.ema_08_.misc;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public interface ReadonlySetView<T>
extends Iterable<T> {
    public boolean contains(T var1);

    public static <T> ReadonlySetView<T> of(final T o) {
        return new ReadonlySetView<T>(){

            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>(){
                    boolean done;

                    @Override
                    public boolean hasNext() {
                        return !this.done;
                    }

                    @Override
                    public T next() {
                        if (this.done) {
                            throw new NoSuchElementException();
                        }
                        this.done = true;
                        return o;
                    }
                };
            }

            @Override
            public boolean contains(T obj) {
                return Objects.equals(o, obj);
            }
        };
    }

    public static <T> ReadonlySetView<T> of(final Set<T> set) {
        Objects.requireNonNull(set, "set");
        return new ReadonlySetView<T>(){

            @Override
            public Iterator<T> iterator() {
                final Iterator<T> iter0 = set.iterator();
                return new Iterator<T>(){

                    @Override
                    public boolean hasNext() {
                        return iter0.hasNext();
                    }

                    @Override
                    public T next() {
                        return iter0.next();
                    }
                };
            }

            @Override
            public boolean contains(T obj) {
                return set.contains(obj);
            }
        };
    }
}

