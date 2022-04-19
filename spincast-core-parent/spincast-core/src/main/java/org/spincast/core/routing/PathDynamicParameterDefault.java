package org.spincast.core.routing;

import javax.annotation.Nullable;

import com.google.inject.assistedinject.AssistedInject;

public class PathDynamicParameterDefault implements PathDynamicParameter {

    @AssistedInject
    public PathDynamicParameterDefault(int position,
                                       String name,
                                       @Nullable String pattern,
                                       @Nullable String dictionaryKey) {

    }
}
