package org.spincast.plugins.jdbc.utils;

import java.util.ArrayList;
import java.util.List;

public class ItemsAndTotalCountDefault<T> implements ItemsAndTotalCount<T> {

    private final List<T> items;
    private final long totalCount;

    /**
     * Empty result.
     */
    public ItemsAndTotalCountDefault() {
        this.items = new ArrayList<T>();
        this.totalCount = 0;
    }

    public ItemsAndTotalCountDefault(List<T> items, long totalCount) {
        this.items = items;
        this.totalCount = totalCount;
    }

    @Override
    public List<T> getItems() {
        return this.items;
    }

    @Override
    public long getTotalCount() {
        return this.totalCount;
    }

}
