package vanadium.models;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * This implementation was sourced from FxMorrin of MemoryLeakFixes, MoreCulling, etc.
 */
public class NonBlockingThreadLocal<T> extends ThreadLocal<T>{
    private static final Map<Thread, Map<ThreadLocal<?>, Object>> STRONGLY_REFERENCED_THREADING_VALUES = Collections.synchronizedMap(new WeakHashMap<>());
    private static final ThreadLocal<WeakReference<Map<ThreadLocal<?>, Object>>>  WEAKLY_REFERENCED_THREAD_LOCALS = new ThreadLocal<>() {
        @Override
        protected WeakReference<Map<ThreadLocal<?>, Object>> initialValue() {
            Map<ThreadLocal<?>, Object> value = new WeakHashMap<>();
             STRONGLY_REFERENCED_THREADING_VALUES.put(Thread.currentThread(), value);
             return new WeakReference<>(value);
        }
    };

    @Override
    public void remove() {
        WEAKLY_REFERENCED_THREAD_LOCALS.get().get().remove(this);
    }

    @Override
    public void set(T value) {
        WEAKLY_REFERENCED_THREAD_LOCALS.get().get().put(this, value);
    }

    @Override
    public T get() {
        Map<ThreadLocal<?>, Object> threadLocalMap = WEAKLY_REFERENCED_THREAD_LOCALS.get().get();

        T value;
        if((threadLocalMap == null)
                || (value = (T) threadLocalMap.get(this)) == null
                && !threadLocalMap.containsKey(this)) {
            value = this.initialValue();
            set(value);
        }

        return value;
    }
}
