package org.spincast.plugins.jdbc.utils;

import java.util.List;

public class ItemsAndTotalCountDefault<T> implements ItemsAndTotalCount<T> {

    private final List<T> items;
    private final long total;

    public ItemsAndTotalCountDefault(List<T> items, long total) {
        this.items = items;
        this.total = total;
    }

    @Override
    public List<T> getItems() {
        return this.items;
    }

    @Override
    public long getTotal() {
        return this.total;
    }

}
