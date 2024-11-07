package com.affinitylabs.utilities;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheManager;
import io.quarkus.cache.CaffeineCache;

import java.time.Duration;
import java.util.Optional;

@Singleton
public class CacheConfigManager {

    private final CacheManager cacheManager;

    public CacheConfigManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setExpireAfterAccess(String cacheName, Duration duration) {
        Optional<Cache> cache = cacheManager.getCache(cacheName);
        if (cache.isPresent()) {
            cache.get().as(CaffeineCache.class).setExpireAfterAccess(duration);
        }
    }

    public void setExpireAfterWrite(String cacheName, Duration duration) {
        Optional<Cache> cache = cacheManager.getCache(cacheName);
        if (cache.isPresent()) {
            cache.get().as(CaffeineCache.class).setExpireAfterWrite(duration);
        }
    }

    public void setMaximumSize(String cacheName, long maximumSize) {
        Optional<Cache> cache = cacheManager.getCache(cacheName);
        if (cache.isPresent()) {
            cache.get().as(CaffeineCache.class).setMaximumSize(maximumSize);
        }
    }
}