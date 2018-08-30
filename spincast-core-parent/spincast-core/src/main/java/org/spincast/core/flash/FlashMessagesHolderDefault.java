package org.spincast.core.flash;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * TODO Use a Session?
 */
public class FlashMessagesHolderDefault implements FlashMessagesHolder {

    private Cache<String, FlashMessage> flashMessagesCache;

    protected Cache<String, FlashMessage> getCache() {
        if(this.flashMessagesCache == null) {

            this.flashMessagesCache =
                    CacheBuilder.newBuilder()
                                .maximumSize(getMaxCacheItems())
                                .expireAfterAccess(getCacheExpirationInSeconds(), TimeUnit.SECONDS)
                                .build();
        }

        return this.flashMessagesCache;
    }

    protected int getMaxCacheItems() {
        return 1000;
    }

    protected int getCacheExpirationInSeconds() {
        return 60;
    }

    @Override
    public String saveFlashMessage(FlashMessage flashMessage) {

        String uuid = UUID.randomUUID().toString();

        getCache().put(uuid, flashMessage);

        return uuid;
    }

    @Override
    public FlashMessage getFlashMessage(String uuid, boolean removeIt) {

        if(uuid == null) {
            return null;
        }

        FlashMessage message = getCache().getIfPresent(uuid);
        if(removeIt) {
            getCache().invalidate(uuid);
        }

        return message;
    }

}
