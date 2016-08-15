package org.spincast.core.json;

import java.util.List;

import org.spincast.core.utils.ISpincastUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * This classe wraps a immutable List : it is used to
 * have an instance of IJsonArray that can be validated
 * as immutable (instanceof IJsonArrayImmutable).
 */
public class JsonArrayImmutable extends JsonArray implements IJsonArrayImmutable {

    private List<Object> immutableElements;

    /**
     * Constructor
     */
    @AssistedInject
    public JsonArrayImmutable(IJsonManager jsonManager,
                              ISpincastUtils spincastUtils,
                              @Assisted List<Object> immutableElements) {
        super(jsonManager, spincastUtils);
        this.immutableElements = immutableElements;
    }

    @Override
    public IJsonArray addAsIs(Object element) {
        throw new RuntimeException("This array is immutable");
    }

    @Override
    public IJsonArray clear() {
        throw new RuntimeException("This array is immutable");
    }

    @Override
    protected List<Object> getElements() {
        return this.immutableElements;
    }

}
