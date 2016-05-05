package org.spincast.core.json;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.AssistedInject;

/**
 * <code>IJsonArray</code> implementation.
 */
public class JsonArray implements IJsonArray {

    protected final Logger logger = LoggerFactory.getLogger(JsonArray.class);

    private final LinkedList<Object> elements;
    private final IJsonManager jsonManager;

    /**
     * Constructor
     */
    @AssistedInject
    public JsonArray(IJsonManager jsonManager) {
        this.jsonManager = jsonManager;
        this.elements = new LinkedList<>();
    }

    protected IJsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected LinkedList<Object> getElements() {
        return this.elements;
    }

    @Override
    public List<Object> getUnderlyingList() {
        return getElements();
    }

    @Override
    public void add(Object element) {
        getElements().add(element);
    }

    @Override
    public void addFirst(Object element) {
        getElements().addFirst(element);
    }

    @Override
    public void clear() {
        getElements().clear();
    }

    @Override
    public int size() {
        return getElements().size();
    }

    @Override
    public Object get(int pos) {
        return getElements().get(pos);
    }

    @Override
    public IJsonObject getJsonObject(int pos) {
        Object valObj = getElements().get(pos);
        if(valObj == null) {
            return null;
        }
        if(valObj instanceof IJsonObject) {
            return (IJsonObject)valObj;
        }

        throw new RuntimeException("Can't convert'" + valObj + "' to a " + IJsonObject.class.getSimpleName() +
                                   " value.");
    }

    @Override
    public IJsonArray getJsonArray(int pos) {
        Object valObj = getElements().get(pos);
        if(valObj == null) {
            return null;
        }
        if(valObj instanceof IJsonArray) {
            return (IJsonArray)valObj;
        }

        throw new RuntimeException("Can't convert'" + valObj + "' to a " + IJsonArray.class.getSimpleName() +
                                   " value.");
    }

    @Override
    public String getString(int pos) {

        Object valObj = getElements().get(pos);
        if(valObj == null) {
            return null;
        }
        if(valObj instanceof String) {
            return (String)valObj;
        }

        return valObj.toString();
    }

    @Override
    public Integer getInteger(int pos) {

        Object valObj = getElements().get(pos);
        if(valObj == null) {
            return null;
        }
        if(valObj instanceof Integer) {
            return (Integer)valObj;
        }

        return Integer.parseInt(valObj.toString());
    }

    @Override
    public Long getLong(int pos) {
        Object valObj = getElements().get(pos);
        if(valObj == null) {
            return null;
        }
        if(valObj instanceof Long) {
            return (Long)valObj;
        }

        return Long.parseLong(valObj.toString());
    }

    @Override
    public Double getDouble(int pos) {
        Object valObj = getElements().get(pos);
        if(valObj == null) {
            return null;
        }
        if(valObj instanceof Double) {
            return (Double)valObj;
        }

        return Double.parseDouble(valObj.toString());
    }

    @Override
    public Boolean getBoolean(int pos) {
        Object valObj = getElements().get(pos);
        if(valObj == null) {
            return null;
        }
        if(valObj instanceof Boolean) {
            return (Boolean)valObj;
        }

        String valStr = String.valueOf(valObj);
        if("true".equalsIgnoreCase(valStr)) {
            return true;
        } else if("false".equalsIgnoreCase(valStr)) {
            return false;
        }

        throw new RuntimeException("Can't convert'" + valStr + "' to a boolean value.");
    }

    @Override
    public BigDecimal getBigDecimal(int pos) {
        Object valObj = getElements().get(pos);
        if(valObj == null) {
            return null;
        }
        if(valObj instanceof BigDecimal) {
            return (BigDecimal)valObj;
        }

        return new BigDecimal(valObj.toString());
    }

    @Override
    public Iterator<Object> iterator() {
        return getElements().iterator();
    }

    @Override
    public String toJsonString() {
        return getJsonManager().toJsonString(this);
    }

    @Override
    public String toString() {
        return toJsonString();
    }

}
