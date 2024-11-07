package com.affinitylabs.utilities;

import com.affinitylabs.models.middleware.auth.ClientLoginResponse;
import io.quarkus.cache.CacheManager;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CaffeineCache;

import java.sql.Time;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class CacheKeysService {

    @CacheName("accountLeft")
    Cache accountsCache;

    @CacheName("paginatedAccounts")
    Cache paginatedCache;

    @CacheName("customer-token")
    Cache customerTokenCache;

    private final CacheManager cacheManager;

    public CacheKeysService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public CompletableFuture<Map<String, String>> getAccountLeft(Object key)  {
        return accountsCache.as(CaffeineCache.class).getIfPresent(key);

    }
    public CompletableFuture<Map<String, String>> getPaginatedAccounts(Object key)  {
        return paginatedCache.as(CaffeineCache.class).getIfPresent(key);
    }

    public CompletableFuture<ClientLoginResponse> getCustomerToken(Object key)  {
        return customerTokenCache.as(CaffeineCache.class).getIfPresent(key);
    }

    public void setExpireAfterAccess(String cacheName, Duration duration) {
        Optional<Cache> cache = cacheManager.getCache(cacheName);
        cache.ifPresent(value -> value.as(CaffeineCache.class).setExpireAfterAccess(duration));
    }

    public void initializeCacheLeft(String key, Map<String, String> value) {
        accountsCache.as(CaffeineCache.class).put(key, CompletableFuture.completedFuture(value));
    }

    public void initializeCacheAccount(String key, Map<String, String> value) {
        paginatedCache.as(CaffeineCache.class).put(key, CompletableFuture.completedFuture(value));
    }
}