package org.spincast.plugins.jdbc.utils;

import java.util.List;

public interface ItemsAndTotalCount<T> {

    public List<T> getItems();

    public long getTotalCount();

}
