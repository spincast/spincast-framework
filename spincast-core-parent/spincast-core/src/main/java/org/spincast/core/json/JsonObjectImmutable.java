package org.spincast.core.json;

import java.util.Map;

import org.spincast.core.utils.ISpincastUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * This classe wraps a immutable Map : it is used to
 * have an instance of IJsonObject that can be validated
 * as immutable (instanceof IJsonObjectImmutable).
 */
public class JsonObjectImmutable extends JsonObject implements IJsonObjectImmutable {

    private Map<String, Object> immutableMap;

    /**
     * Constructor
     */
    @AssistedInject
    public JsonObjectImmutable(IJsonManager jsonManager,
                               ISpincastUtils spincastUtils,
                               @Assisted Map<String, Object> immutableMap) {
        super(jsonManager, spincastUtils);
        this.immutableMap = immutableMap;
    }

    @Override
    protected IJsonObject putAsIs(String key, Object value) {
        throw new RuntimeException("This object is immutable");
    }

    @Override
    public IJsonObject removeAll() {
        throw new RuntimeException("This object is immutable");
    }

    @Override
    protected Map<String, Object> getMap() {
        return this.immutableMap;
    }

}
