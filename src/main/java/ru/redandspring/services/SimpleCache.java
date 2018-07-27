package ru.redandspring.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class SimpleCache<T> {

    private static final Map<String, SimpleCache<?>> CACHES = new HashMap<>();

    private static final long CACHE_TIME_MILLIS = 20_000_000;

    private volatile long start = System.currentTimeMillis();
    private final Map<String, T> cacheMap = new HashMap<>();
    private final String cacheName;

    private SimpleCache(final String cacheName) {
        Objects.requireNonNull(cacheName);

        this.cacheName = cacheName;
    }

    @SuppressWarnings("unchecked")
    public static synchronized <T> SimpleCache<T> getInstance(final String cacheName) {
        Objects.requireNonNull(cacheName);

        return (SimpleCache<T>) CACHES.computeIfAbsent(cacheName, SimpleCache::new);
    }

    public T get(final long key, final IProducer<T, ?> supplier) throws ServiceException {
        return get(String.valueOf(key), supplier);
    }

    public synchronized T get(final String key, final IProducer<T, ?> producer) throws ServiceException {
        clearCache();
        final String fullKey = cacheName + key;

        T value = cacheMap.get(fullKey); // NOSONAR

        if (value == null) {
            value = producer.get();

            if (value == null) {
                throw new ServiceException("Invalid produce cache value");
            }
            cacheMap.put(fullKey, value);
        }
        return value;
    }

    public void clear() {
        cacheMap.clear();
    }

    private void clearCache() {
        if (System.currentTimeMillis() - start > CACHE_TIME_MILLIS) {
            start = System.currentTimeMillis();
            this.clear();
        }
    }

    @FunctionalInterface
    public interface IProducer<T, E extends ServiceException> {
        T get() throws E;
    }
}


