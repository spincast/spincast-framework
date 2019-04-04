package org.spincast.core.json;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.exceptions.CantConvertException;
import org.spincast.core.json.JsonObjectDefault.IFirstElementGetter;
import org.spincast.core.utils.ObjectConverter;
import org.spincast.core.utils.SpincastUtils;

/**
 * Base class for both JsonObject and JsonArray.
 */
public abstract class JsonObjectArrayBase implements JsonObjectOrArray {

    protected static final Logger logger = LoggerFactory.getLogger(JsonObjectArrayBase.class);

    public static class JsonPathCachingItem {

        public boolean exists;
        public Object element;
    }

    private final JsonManager jsonManager;
    private final SpincastUtils spincastUtils;
    private final ObjectConverter objectConverter;
    private final boolean mutable;
    protected final Object defaultElementValidator = new Object();
    private Map<String, JsonPathCachingItem> jsonPathCachingMap;
    private ElementTransformer trimTransformer;

    /**
     * Constructor
     */
    public JsonObjectArrayBase(boolean mutable,
                               JsonManager jsonManager,
                               SpincastUtils spincastUtils,
                               ObjectConverter objectConverter) {
        this.mutable = mutable;
        this.jsonManager = jsonManager;
        this.spincastUtils = spincastUtils;
        this.objectConverter = objectConverter;
    }

    @Override
    public boolean isMutable() {
        return this.mutable;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected ObjectConverter getObjectConverter() {
        return this.objectConverter;
    }

    protected Object getdefaultElementValidator() {
        return this.defaultElementValidator;
    }

    protected Map<String, JsonPathCachingItem> getJsonPathCachingMap() {
        if (this.jsonPathCachingMap == null) {
            this.jsonPathCachingMap = new HashMap<>();
        }
        return this.jsonPathCachingMap;
    }

    @Override
    public Object getObject(String jsonPath) {
        return getObject(jsonPath, false, null, true);
    }

    @Override
    public Object getObject(String jsonPath, Object defaultElement) {
        return getObject(jsonPath, true, defaultElement, true);
    }

    public Object getObjectNoKeyParsing(String jsonPath) {
        return getObject(jsonPath, false, null, false);
    }

    public Object getObjectNoKeyParsing(String jsonPath, Object defaultElement) {
        return getObject(jsonPath, true, defaultElement, false);
    }

    protected Object getObject(String jsonPath, boolean hasdefaultElement, Object defaultElement, boolean parseJsonPath) {
        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, parseJsonPath);
        return val;
    }

    @Override
    public JsonObject getJsonObject(String jsonPath) {
        return getJsonObject(jsonPath, false, null);
    }

    @Override
    public JsonObject getJsonObjectOrEmpty(String jsonPath) {
        return getJsonObjectOrEmpty(jsonPath, false);
    }

    @Override
    public JsonObject getJsonObjectOrEmpty(String jsonPath, boolean addIfDoesntExist) {

        JsonObject obj = getJsonObject(jsonPath);
        if (obj == null) {
            obj = getJsonManager().create();
            if (addIfDoesntExist) {
                set(jsonPath, obj);
            }
        }

        return obj;
    }

    @Override
    public JsonObject getJsonObject(String jsonPath, JsonObject defaultElement) {
        return getJsonObject(jsonPath, true, defaultElement);
    }

    protected JsonObject getJsonObject(String jsonPath, boolean hasdefaultElement, JsonObject defaultElement) {

        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, true);
        return getJsonObjectFromObject(val);
    }

    public JsonObject getJsonObjectNoKeyParsing(String jsonPath) {
        return getJsonObject(jsonPath, false, null, false);
    }

    public JsonObject getJsonObjectNoKeyParsing(String jsonPath, JsonObject defaultElement) {
        return getJsonObject(jsonPath, true, defaultElement, false);
    }

    public JsonObject getJsonObjectOrEmptyNoKeyParsing(String jsonPath) {

        JsonObject defaultObj = getJsonManager().create();
        JsonObject obj = getJsonObject(jsonPath, true, defaultObj, false);
        if (obj == null) {
            obj = defaultObj;
        }

        return obj;
    }

    protected JsonObject getJsonObject(String jsonPath, boolean hasdefaultElement, JsonObject defaultElement,
                                       boolean parseJsonPath) {

        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, parseJsonPath);
        return getJsonObjectFromObject(val);
    }

    protected JsonObject getJsonObjectFromObject(Object object) {

        return getObjectConverter().convertToJsonObject(object);
    }

    @Override
    public JsonArray getJsonArray(String jsonPath) {
        return getJsonArray(jsonPath, false, null, true);
    }

    @Override
    public JsonArray getJsonArrayOrEmpty(String jsonPath) {
        return getJsonArrayOrEmpty(jsonPath, false);
    }

    @Override
    public JsonArray getJsonArrayOrEmpty(String jsonPath, boolean addIfDoesntExist) {

        JsonArray array = getJsonArray(jsonPath);
        if (array == null) {
            array = getJsonManager().createArray();
            if (addIfDoesntExist) {
                set(jsonPath, array);
            }
        }

        return array;
    }

    @Override
    public JsonArray getJsonArray(String jsonPath, JsonArray defaultElement) {
        return getJsonArray(jsonPath, true, defaultElement, true);
    }

    public JsonArray getJsonArrayNoKeyParsing(String jsonPath) {
        return getJsonArray(jsonPath, false, null, false);
    }

    public JsonArray getJsonArrayNoKeyParsing(String jsonPath, JsonArray defaultElement) {
        return getJsonArray(jsonPath, true, defaultElement, false);
    }

    public JsonArray getJsonArrayOrEmptyNoKeyParsing(String jsonPath) {

        JsonArray defaultArray = getJsonManager().createArray();
        JsonArray array = getJsonArray(jsonPath, true, getJsonManager().createArray(), false);
        if (array == null) {
            array = defaultArray;
        }

        return array;
    }

    protected JsonArray getJsonArray(String jsonPath, boolean hasdefaultElement, JsonArray defaultElement,
                                     boolean parseJsonPath) {

        Objects.requireNonNull(jsonPath, "The jsonPath can't be NULL");

        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, parseJsonPath);
        return getJsonArrayFromObject(val);
    }

    protected JsonArray getJsonArrayFromObject(Object object) {
        return getObjectConverter().convertToJsonArray(object);
    }

    @Override
    public String getString(String jsonPath) {
        return getString(jsonPath, false, null);
    }

    @Override
    public String getString(String jsonPath, String defaultElement) {
        return getString(jsonPath, true, defaultElement);
    }

    protected String getString(String jsonPath, boolean hasdefaultElement, String defaultElement) {
        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, true);
        return getStringFromObject(val);
    }

    public String getStringNoKeyParsing(String jsonPath) {
        return getString(jsonPath, false, null, false);
    }

    public String getStringNoKeyParsing(String jsonPath, String defaultElement) {
        return getString(jsonPath, true, defaultElement, false);
    }

    protected String getString(String jsonPath, boolean hasdefaultElement, String defaultElement, boolean parseJsonPath) {
        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, parseJsonPath);
        return getStringFromObject(val);
    }

    protected String getStringFromObject(Object object) {
        return getObjectConverter().convertToString(object);
    }

    @Override
    public Integer getInteger(String jsonPath) {
        return getIntegerElement(jsonPath, false, null);
    }

    @Override
    public Integer getInteger(String jsonPath, Integer defaultElement) {
        return getIntegerElement(jsonPath, true, defaultElement);
    }

    protected Integer getIntegerElement(String jsonPath, boolean hasdefaultElement, Integer defaultElement) {

        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, true);
        return getIntegerFromObject(val);
    }

    public Integer getIntegerNoKeyParsing(String jsonPath) {
        return getInteger(jsonPath, false, null, false);
    }

    public Integer getIntegerNoKeyParsing(String jsonPath, Integer defaultElement) {
        return getInteger(jsonPath, true, defaultElement, false);
    }

    protected Integer getInteger(String jsonPath, boolean hasdefaultElement, Integer defaultElement, boolean parseJsonPath) {

        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, parseJsonPath);
        return getIntegerFromObject(val);
    }

    protected Integer getIntegerFromObject(Object object) {
        return getObjectConverter().convertToInteger(object);
    }

    @Override
    public Long getLong(String jsonPath) {
        return getLongElement(jsonPath, false, null);
    }

    @Override
    public Long getLong(String jsonPath, Long defaultElement) {
        return getLongElement(jsonPath, true, defaultElement);
    }

    protected Long getLongElement(String jsonPath, boolean hasdefaultElement, Long defaultElement) {
        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, true);
        return getLongFromObject(val);
    }

    public Long getLongNoKeyParsing(String jsonPath) {
        return getLong(jsonPath, false, null, false);
    }

    public Long getLongNoKeyParsing(String jsonPath, Long defaultElement) {
        return getLong(jsonPath, true, defaultElement, false);
    }

    protected Long getLong(String jsonPath, boolean hasdefaultElement, Long defaultElement, boolean parseJsonPath) {
        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, parseJsonPath);
        return getLongFromObject(val);
    }

    protected Long getLongFromObject(Object object) {
        return getObjectConverter().convertToLong(object);
    }

    @Override
    public Float getFloat(String jsonPath) {
        return getFloatElement(jsonPath, false, null);
    }

    @Override
    public Float getFloat(String jsonPath, Float defaultElement) {
        return getFloatElement(jsonPath, true, defaultElement);
    }

    protected Float getFloatElement(String jsonPath, boolean hasdefaultElement, Float defaultElement) {

        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, true);
        return getFloatFromObject(val);
    }

    public Float getFloatNoKeyParsing(String jsonPath) {
        return getFloat(jsonPath, false, null, false);
    }

    public Float getFloatNoKeyParsing(String jsonPath, Float defaultElement) {
        return getFloat(jsonPath, true, defaultElement, false);
    }

    protected Float getFloat(String jsonPath, boolean hasdefaultElement, Float defaultElement, boolean parseJsonPath) {
        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, parseJsonPath);
        return getFloatFromObject(val);
    }

    protected Float getFloatFromObject(Object object) {
        return getObjectConverter().convertToFloat(object);
    }

    @Override
    public Double getDouble(String jsonPath) {
        return getDoubleElement(jsonPath, false, null);
    }

    @Override
    public Double getDouble(String jsonPath, Double defaultElement) {
        return getDoubleElement(jsonPath, true, defaultElement);
    }

    protected Double getDoubleElement(String jsonPath, boolean hasdefaultElement, Double defaultElement) {

        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, true);
        return getDoubleFromObject(val);
    }

    public Double getDoubleNoKeyParsing(String jsonPath) {
        return getDouble(jsonPath, false, null, false);
    }

    public Double getDoubleNoKeyParsing(String jsonPath, Double defaultElement) {
        return getDouble(jsonPath, true, defaultElement, false);
    }

    protected Double getDouble(String jsonPath, boolean hasdefaultElement, Double defaultElement, boolean parseJsonPath) {
        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, parseJsonPath);
        return getDoubleFromObject(val);
    }

    protected Double getDoubleFromObject(Object object) {
        return getObjectConverter().convertToDouble(object);
    }

    @Override
    public Boolean getBoolean(String jsonPath) {
        return getBooleanElement(jsonPath, false, null);
    }

    @Override
    public Boolean getBoolean(String jsonPath, Boolean defaultElement) {
        return getBooleanElement(jsonPath, true, defaultElement);
    }

    protected Boolean getBooleanElement(String jsonPath, boolean hasdefaultElement, Boolean defaultElement) {

        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, true);
        return getBooleanFromObject(val);
    }

    public Boolean getBooleanNoKeyParsing(String jsonPath) {
        return getBoolean(jsonPath, false, null, false);
    }

    public Boolean getBooleanNoKeyParsing(String jsonPath, Boolean defaultElement) {
        return getBoolean(jsonPath, true, defaultElement, false);
    }

    protected Boolean getBoolean(String jsonPath, boolean hasdefaultElement, Boolean defaultElement, boolean parseJsonPath) {
        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, parseJsonPath);
        return getBooleanFromObject(val);
    }

    protected Boolean getBooleanFromObject(Object object) {
        return getObjectConverter().convertToBoolean(object);
    }

    @Override
    public BigDecimal getBigDecimal(String jsonPath) {
        return getBigDecimalElement(jsonPath, false, null);
    }

    @Override
    public BigDecimal getBigDecimal(String jsonPath, BigDecimal defaultElement) {
        return getBigDecimalElement(jsonPath, true, defaultElement);
    }

    protected BigDecimal getBigDecimalElement(String jsonPath, boolean hasdefaultElement, BigDecimal defaultElement) {
        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, true);
        return getBigDecimalFromObject(val);
    }

    public BigDecimal getBigDecimalNoKeyParsing(String jsonPath) {
        return getBigDecimal(jsonPath, false, null, false);
    }

    public BigDecimal getBigDecimalNoKeyParsing(String jsonPath, BigDecimal defaultElement) {
        return getBigDecimal(jsonPath, true, defaultElement, false);
    }

    protected BigDecimal getBigDecimal(String jsonPath, boolean hasdefaultElement, BigDecimal defaultElement,
                                       boolean parseJsonPath) {
        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, parseJsonPath);
        return getBigDecimalFromObject(val);
    }

    protected BigDecimal getBigDecimalFromObject(Object object) {
        return getObjectConverter().convertToBigDecimal(object);
    }

    @Override
    public byte[] getBytesFromBase64String(String jsonPath) {
        return getBytesFromBase64StringElement(jsonPath, false, null);
    }

    @Override
    public byte[] getBytesFromBase64String(String jsonPath, byte[] defaultElement) {
        return getBytesFromBase64StringElement(jsonPath, true, defaultElement);
    }

    protected byte[] getBytesFromBase64StringElement(final String jsonPath, boolean hasdefaultElement, byte[] defaultElement) {
        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, true);
        return getBytesFromBase64StringValueFromObject(val);
    }

    public byte[] getBytesFromBase64StringNoKeyParsing(String jsonPath) {
        return getBytesFromBase64String(jsonPath, false, null, false);
    }

    public byte[] getBytesFromBase64StringNoKeyParsing(String jsonPath, byte[] defaultElement) {
        return getBytesFromBase64String(jsonPath, true, defaultElement, false);
    }

    protected byte[] getBytesFromBase64String(String jsonPath, boolean hasdefaultElement, byte[] defaultElement,
                                              boolean parseJsonPath) {
        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, parseJsonPath);
        return getBytesFromBase64StringValueFromObject(val);
    }

    protected byte[] getBytesFromBase64StringValueFromObject(Object object) {
        return getObjectConverter().convertBase64StringToByteArray(object);
    }

    @Override
    public Date getDate(String jsonPath) {
        return getDateElement(jsonPath, false, null);
    }

    @Override
    public Date getDate(String jsonPath, Date defaultElement) {
        return getDateElement(jsonPath, true, defaultElement);
    }

    protected Date getDateElement(final String jsonPath, boolean hasdefaultElement, Date defaultElement) {
        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, true);
        return getDateFromObject(val);
    }

    protected Date getDateFromObject(Object object) {
        return getObjectConverter().convertToDateFromJsonDateFormat(object);
    }

    public Date getDateNoKeyParsing(String jsonPath) {
        return getDate(jsonPath, false, null, false);
    }

    public Date getDateNoKeyParsing(String jsonPath, Date defaultElement) {
        return getDate(jsonPath, true, defaultElement, false);
    }

    protected Date getDate(String jsonPath, boolean hasdefaultElement, Date defaultElement, boolean parseJsonPath) {
        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, parseJsonPath);
        return getDateFromObject(val);
    }

    @Override
    public Instant getInstant(String jsonPath) throws CantConvertException {
        return getInstantElement(jsonPath, false, null);
    }

    @Override
    public Instant getInstant(String jsonPath, Instant defaultElement) throws CantConvertException {
        return getInstantElement(jsonPath, true, defaultElement);
    }

    protected Instant getInstantElement(final String jsonPath, boolean hasdefaultElement, Instant defaultElement) {
        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, true);
        return getInstantFromObject(val);
    }

    protected Instant getInstantFromObject(Object object) {
        return getObjectConverter().convertToInstantFromJsonDateFormat(object);
    }

    public Instant getInstantNoKeyParsing(String jsonPath) {
        return getInstant(jsonPath, false, null, false);
    }

    public Instant getInstantNoKeyParsing(String jsonPath, Instant defaultElement) {
        return getInstant(jsonPath, true, defaultElement, false);
    }

    protected Instant getInstant(String jsonPath, boolean hasdefaultElement, Instant defaultElement, boolean parseJsonPath) {
        Object val = getElement(jsonPath, hasdefaultElement, defaultElement, parseJsonPath);
        return getInstantFromObject(val);
    }

    protected <T> T getArrayFirst(String jsonPath,
                                  boolean parseJsonPath,
                                  boolean hasdefaultElement,
                                  T defaultElement,
                                  IFirstElementGetter<T> firstElementGetter) {

        JsonArray array;
        if (parseJsonPath) {
            array = getJsonArray(jsonPath, null);
        } else {
            array = getJsonArrayNoKeyParsing(jsonPath, null);
        }

        if (array == null) {
            if (hasdefaultElement) {
                return defaultElement;
            }
            return null;
        }

        return firstElementGetter.get(array, hasdefaultElement, defaultElement);
    }

    @Override
    public String getArrayFirstString(String key) {
        return getArrayFirstString(key, true, false, null);
    }

    @Override
    public String getArrayFirstString(String key, String defaultElement) {
        return getArrayFirstString(key, true, true, defaultElement);
    }

    public String getArrayFirstStringNoKeyParsing(String key) {
        return getArrayFirstString(key, false, false, null);
    }

    public String getArrayFirstStringNoKeyParsing(String key, String defaultElement) {
        return getArrayFirstString(key, false, true, defaultElement);
    }

    protected String getArrayFirstString(String key, boolean parseJsonPath, boolean hasdefaultElement, String defaultElement) {

        return getArrayFirst(key, parseJsonPath, hasdefaultElement, defaultElement, new IFirstElementGetter<String>() {

            @Override
            public String get(JsonArray array,
                              boolean hasdefaultElement,
                              String defaultElement) {
                if (hasdefaultElement) {
                    return array.getString(0, defaultElement);
                } else {
                    return array.getString(0);
                }
            }
        });
    }

    @Override
    public String toJsonString() {
        return toJsonString(false);
    }

    @Override
    public String toJsonString(boolean pretty) {
        return getJsonManager().toJsonString(this, pretty);
    }

    @Override
    public String toString() {
        return toJsonString();
    }

    @Override
    public JsonObject getArrayFirstJsonObject(String key) {
        return getArrayFirstJsonObject(key, true, false, null);
    }

    @Override
    public JsonObject getArrayFirstJsonObject(String key, JsonObject defaultElement) {
        return getArrayFirstJsonObject(key, true, true, defaultElement);
    }

    public JsonObject getArrayFirstJsonObjectNoKeyParsing(String key) {
        return getArrayFirstJsonObject(key, false, false, null);
    }

    public JsonObject getArrayFirstJsonObjectNoKeyParsing(String key, JsonObject defaultElement) {
        return getArrayFirstJsonObject(key, false, true, defaultElement);
    }

    protected JsonObject getArrayFirstJsonObject(String key, boolean parseJsonPath, boolean hasdefaultElement,
                                                 JsonObject defaultElement) {

        return getArrayFirst(key, parseJsonPath, hasdefaultElement, defaultElement, new IFirstElementGetter<JsonObject>() {

            @Override
            public JsonObject get(JsonArray array,
                                  boolean hasdefaultElement,
                                  JsonObject defaultElement) {
                if (hasdefaultElement) {
                    return array.getJsonObject(0, defaultElement);
                } else {
                    return array.getJsonObject(0);
                }
            }
        });
    }

    @Override
    public JsonArray getArrayFirstJsonArray(String key) {
        return getArrayFirstJsonArray(key, true, false, null);
    }

    @Override
    public JsonArray getArrayFirstJsonArray(String key, JsonArray defaultElement) {
        return getArrayFirstJsonArray(key, true, true, defaultElement);
    }

    public JsonArray getArrayFirstJsonArrayNoKeyParsing(String key) {
        return getArrayFirstJsonArray(key, false, false, null);
    }

    public JsonArray getArrayFirstJsonArrayNoKeyParsing(String key, JsonArray defaultElement) {
        return getArrayFirstJsonArray(key, false, true, defaultElement);
    }

    protected JsonArray getArrayFirstJsonArray(String key, boolean parseJsonPath, boolean hasdefaultElement,
                                               JsonArray defaultElement) {

        return getArrayFirst(key, parseJsonPath, hasdefaultElement, defaultElement, new IFirstElementGetter<JsonArray>() {

            @Override
            public JsonArray get(JsonArray array,
                                 boolean hasdefaultElement,
                                 JsonArray defaultElement) {
                if (hasdefaultElement) {
                    return array.getJsonArray(0, defaultElement);
                } else {
                    return array.getJsonArray(0);
                }
            }
        });
    }

    @Override
    public Integer getArrayFirstInteger(String key) {
        return getArrayFirstInteger(key, true, false, null);
    }

    @Override
    public Integer getArrayFirstInteger(String key, Integer defaultElement) {
        return getArrayFirstInteger(key, true, true, defaultElement);
    }

    public Integer getArrayFirstIntegerNoKeyParsing(String key) {
        return getArrayFirstInteger(key, false, false, null);
    }

    public Integer getArrayFirstIntegerNoKeyParsing(String key, Integer defaultElement) {
        return getArrayFirstInteger(key, false, true, defaultElement);
    }

    protected Integer getArrayFirstInteger(String key, boolean parseJsonPath, boolean hasdefaultElement, Integer defaultElement) {

        return getArrayFirst(key, parseJsonPath, hasdefaultElement, defaultElement, new IFirstElementGetter<Integer>() {

            @Override
            public Integer get(JsonArray array,
                               boolean hasdefaultElement,
                               Integer defaultElement) {
                if (hasdefaultElement) {
                    return array.getInteger(0, defaultElement);
                } else {
                    return array.getInteger(0);
                }
            }
        });
    }

    @Override
    public Long getArrayFirstLong(String key) {
        return getArrayFirstLong(key, true, false, null);
    }

    @Override
    public Long getArrayFirstLong(String key, Long defaultElement) {
        return getArrayFirstLong(key, true, true, defaultElement);
    }

    public Long getArrayFirstLongNoKeyParsing(String key) {
        return getArrayFirstLong(key, false, false, null);
    }

    public Long getArrayFirstLongNoKeyParsing(String key, Long defaultElement) {
        return getArrayFirstLong(key, false, true, defaultElement);
    }

    protected Long getArrayFirstLong(String key, boolean parseJsonPath, boolean hasdefaultElement, Long defaultElement) {

        return getArrayFirst(key, parseJsonPath, hasdefaultElement, defaultElement, new IFirstElementGetter<Long>() {

            @Override
            public Long get(JsonArray array,
                            boolean hasdefaultElement,
                            Long defaultElement) {
                if (hasdefaultElement) {
                    return array.getLong(0, defaultElement);
                } else {
                    return array.getLong(0);
                }
            }
        });
    }

    @Override
    public Double getArrayFirstDouble(String key) {
        return getArrayFirstDouble(key, true, false, null);
    }

    @Override
    public Double getArrayFirstDouble(String key, Double defaultElement) {
        return getArrayFirstDouble(key, true, true, defaultElement);
    }

    public Double getArrayFirstDoubleNoKeyParsing(String key) {
        return getArrayFirstDouble(key, false, false, null);
    }

    public Double getArrayFirstDoubleNoKeyParsing(String key, Double defaultElement) {
        return getArrayFirstDouble(key, false, true, defaultElement);
    }

    protected Double getArrayFirstDouble(String key, boolean parseJsonPath, boolean hasdefaultElement, Double defaultElement) {

        return getArrayFirst(key, parseJsonPath, hasdefaultElement, defaultElement, new IFirstElementGetter<Double>() {

            @Override
            public Double get(JsonArray array,
                              boolean hasdefaultElement,
                              Double defaultElement) {
                if (hasdefaultElement) {
                    return array.getDouble(0, defaultElement);
                } else {
                    return array.getDouble(0);
                }
            }
        });
    }

    @Override
    public Float getArrayFirstFloat(String key) {
        return getArrayFirstFloat(key, true, false, null);
    }

    @Override
    public Float getArrayFirstFloat(String key, Float defaultElement) {
        return getArrayFirstFloat(key, true, true, defaultElement);
    }

    public Float getArrayFirstFloatNoKeyParsing(String key) {
        return getArrayFirstFloat(key, false, false, null);
    }

    public Float getArrayFirstFloatNoKeyParsing(String key, Float defaultElement) {
        return getArrayFirstFloat(key, false, true, defaultElement);
    }

    protected Float getArrayFirstFloat(String key, boolean parseJsonPath, boolean hasdefaultElement, Float defaultElement) {

        return getArrayFirst(key, parseJsonPath, hasdefaultElement, defaultElement, new IFirstElementGetter<Float>() {

            @Override
            public Float get(JsonArray array,
                             boolean hasdefaultElement,
                             Float defaultElement) {
                if (hasdefaultElement) {
                    return array.getFloat(0, defaultElement);
                } else {
                    return array.getFloat(0);
                }
            }
        });
    }

    @Override
    public Boolean getArrayFirstBoolean(String key) {
        return getArrayFirstBoolean(key, true, false, null);
    }

    @Override
    public Boolean getArrayFirstBoolean(String key, Boolean defaultElement) {
        return getArrayFirstBoolean(key, true, true, defaultElement);
    }

    public Boolean getArrayFirstBooleanNoKeyParsing(String key) {
        return getArrayFirstBoolean(key, false, false, null);
    }

    public Boolean getArrayFirstBooleanNoKeyParsing(String key, Boolean defaultElement) {
        return getArrayFirstBoolean(key, false, true, defaultElement);
    }

    protected Boolean getArrayFirstBoolean(String key, boolean parseJsonPath, boolean hasdefaultElement, Boolean defaultElement) {

        return getArrayFirst(key, parseJsonPath, hasdefaultElement, defaultElement, new IFirstElementGetter<Boolean>() {

            @Override
            public Boolean get(JsonArray array,
                               boolean hasdefaultElement,
                               Boolean defaultElement) {
                if (hasdefaultElement) {
                    return array.getBoolean(0, defaultElement);
                } else {
                    return array.getBoolean(0);
                }
            }
        });
    }

    @Override
    public BigDecimal getArrayFirstBigDecimal(String key) {
        return getArrayFirstBigDecimal(key, true, false, null);
    }

    @Override
    public BigDecimal getArrayFirstBigDecimal(String key, BigDecimal defaultElement) {
        return getArrayFirstBigDecimal(key, true, true, defaultElement);
    }

    public BigDecimal getArrayFirstBigDecimalNoKeyParsing(String key) {
        return getArrayFirstBigDecimal(key, false, false, null);
    }

    public BigDecimal getArrayFirstBigDecimalNoKeyParsing(String key, BigDecimal defaultElement) {
        return getArrayFirstBigDecimal(key, false, true, defaultElement);
    }

    protected BigDecimal getArrayFirstBigDecimal(String key, boolean parseJsonPath, boolean hasdefaultElement,
                                                 BigDecimal defaultElement) {

        return getArrayFirst(key, parseJsonPath, hasdefaultElement, defaultElement, new IFirstElementGetter<BigDecimal>() {

            @Override
            public BigDecimal get(JsonArray array,
                                  boolean hasdefaultElement,
                                  BigDecimal defaultElement) {
                if (hasdefaultElement) {
                    return array.getBigDecimal(0, defaultElement);
                } else {
                    return array.getBigDecimal(0);
                }
            }
        });
    }

    @Override
    public byte[] getArrayFirstBytesFromBase64String(String key) {
        return getArrayFirstBytesFromBase64String(key, true, false, null);
    }

    @Override
    public byte[] getArrayFirstBytesFromBase64String(String key, byte[] defaultElement) {
        return getArrayFirstBytesFromBase64String(key, true, true, defaultElement);
    }

    public byte[] getArrayFirstBytesFromBase64StringNoKeyParsing(String key) {
        return getArrayFirstBytesFromBase64String(key, false, false, null);
    }

    public byte[] getArrayFirstBytesFromBase64StringNoKeyParsing(String key, byte[] defaultElement) {
        return getArrayFirstBytesFromBase64String(key, false, true, defaultElement);
    }

    protected byte[] getArrayFirstBytesFromBase64String(String key, boolean parseJsonPath, boolean hasdefaultElement,
                                                        byte[] defaultElement) {

        return getArrayFirst(key, parseJsonPath, hasdefaultElement, defaultElement, new IFirstElementGetter<byte[]>() {

            @Override
            public byte[] get(JsonArray array,
                              boolean hasdefaultElement,
                              byte[] defaultElement) {
                if (hasdefaultElement) {
                    return array.getBytesFromBase64String(0, defaultElement);
                } else {
                    return array.getBytesFromBase64String(0);
                }
            }
        });
    }

    @Override
    public Date getArrayFirstDate(String key) {
        return getArrayFirstDate(key, true, false, null);
    }

    @Override
    public Date getArrayFirstDate(String key, Date defaultElement) {
        return getArrayFirstDate(key, true, true, defaultElement);
    }

    public Date getArrayFirstDateNoKeyParsing(String key) {
        return getArrayFirstDate(key, false, false, null);
    }

    public Date getArrayFirstDateNoKeyParsing(String key, Date defaultElement) {
        return getArrayFirstDate(key, false, true, defaultElement);
    }

    protected Date getArrayFirstDate(String jsonPath, boolean parseJsonPath, boolean hasdefaultElement, Date defaultElement) {

        return getArrayFirst(jsonPath, parseJsonPath, hasdefaultElement, defaultElement, new IFirstElementGetter<Date>() {

            @Override
            public Date get(JsonArray array,
                            boolean hasDefaultElement,
                            Date defaultElement) {
                if (hasDefaultElement) {
                    return array.getDate(0, defaultElement);
                } else {
                    return array.getDate(0);
                }
            }
        });
    }

    @Override
    public Instant getArrayFirstInstant(String key) {
        return getArrayFirstInstant(key, true, false, null);
    }

    @Override
    public Instant getArrayFirstInstant(String key, Instant defaultElement) {
        return getArrayFirstInstant(key, true, true, defaultElement);
    }

    public Instant getArrayFirstInstantNoKeyParsing(String key) {
        return getArrayFirstInstant(key, false, false, null);
    }

    public Instant getArrayFirstInstantNoKeyParsing(String key, Instant defaultElement) {
        return getArrayFirstInstant(key, false, true, defaultElement);
    }

    protected Instant getArrayFirstInstant(String jsonPath, boolean parseJsonPath, boolean hasdefaultElement,
                                           Instant defaultElement) {

        return getArrayFirst(jsonPath, parseJsonPath, hasdefaultElement, defaultElement, new IFirstElementGetter<Instant>() {

            @Override
            public Instant get(JsonArray array,
                               boolean hasDefaultElement,
                               Instant defaultElement) {
                if (hasDefaultElement) {
                    return array.getInstant(0, defaultElement);
                } else {
                    return array.getInstant(0);
                }
            }
        });
    }

    @Override
    public boolean isCanBeConvertedToString(String jsonPath) {
        return isCanBeConvertedToString(jsonPath, true);
    }

    public boolean isCanBeConvertedToStringNoKeyParsing(String jsonPath) {
        return isCanBeConvertedToString(jsonPath, false);
    }

    protected boolean isCanBeConvertedToString(String jsonPath, boolean parseKey) {

        Object object = getElement(jsonPath, true, getdefaultElementValidator(), parseKey);
        // reference equality ok here
        return object != getdefaultElementValidator() && getObjectConverter().isCanBeConvertedToString(object);
    }

    @Override
    public boolean isCanBeConvertedToInteger(String jsonPath) {
        return isCanBeConvertedToInteger(jsonPath, true);
    }

    public boolean isCanBeConvertedToIntegerNoKeyParsing(String jsonPath) {
        return isCanBeConvertedToInteger(jsonPath, false);
    }

    protected boolean isCanBeConvertedToInteger(String jsonPath, boolean parseKey) {

        Object object = getElement(jsonPath, true, getdefaultElementValidator(), parseKey);
        // reference equality ok here
        return object != getdefaultElementValidator() && getObjectConverter().isCanBeConvertedToInteger(object);
    }

    @Override
    public boolean isCanBeConvertedToLong(String jsonPath) {
        return isCanBeConvertedToLong(jsonPath, true);
    }

    public boolean isCanBeConvertedToLongNoKeyParsing(String jsonPath) {
        return isCanBeConvertedToLong(jsonPath, false);
    }

    protected boolean isCanBeConvertedToLong(String jsonPath, boolean parseKey) {

        Object object = getElement(jsonPath, true, getdefaultElementValidator(), parseKey);
        // reference equality ok here
        return object != getdefaultElementValidator() && getObjectConverter().isCanBeConvertedToLong(object);
    }

    @Override
    public boolean isCanBeConvertedToFloat(String jsonPath) {
        return isCanBeConvertedToFloat(jsonPath, true);
    }

    public boolean isCanBeConvertedToFloatNoKeyParsing(String jsonPath) {
        return isCanBeConvertedToFloat(jsonPath, false);
    }

    protected boolean isCanBeConvertedToFloat(String jsonPath, boolean parseKey) {

        Object object = getElement(jsonPath, true, getdefaultElementValidator(), parseKey);
        // reference equality ok here
        return object != getdefaultElementValidator() && getObjectConverter().isCanBeConvertedToFloat(object);
    }

    @Override
    public boolean isCanBeConvertedToDouble(String jsonPath) {
        return isCanBeConvertedToDouble(jsonPath, true);
    }

    public boolean isCanBeConvertedToDoubleNoKeyParsing(String jsonPath) {
        return isCanBeConvertedToDouble(jsonPath, false);
    }

    protected boolean isCanBeConvertedToDouble(String jsonPath, boolean parseKey) {

        Object object = getElement(jsonPath, true, getdefaultElementValidator(), parseKey);
        // reference equality ok here
        return object != getdefaultElementValidator() && getObjectConverter().isCanBeConvertedToDouble(object);
    }

    @Override
    public boolean isCanBeConvertedToBoolean(String jsonPath) {
        return isCanBeConvertedToBoolean(jsonPath, true);
    }

    public boolean isCanBeConvertedToBooleanNoKeyParsing(String jsonPath) {
        return isCanBeConvertedToBoolean(jsonPath, false);
    }

    protected boolean isCanBeConvertedToBoolean(String jsonPath, boolean parseKey) {

        Object object = getElement(jsonPath, true, getdefaultElementValidator(), parseKey);
        // reference equality ok here
        return object != getdefaultElementValidator() && getObjectConverter().isCanBeConvertedToBoolean(object);
    }

    @Override
    public boolean isCanBeConvertedToBigDecimal(String jsonPath) {
        return isCanBeConvertedToBigDecimal(jsonPath, true);
    }

    public boolean isCanBeConvertedToBigDecimalNoKeyParsing(String jsonPath) {
        return isCanBeConvertedToBigDecimal(jsonPath, false);
    }

    protected boolean isCanBeConvertedToBigDecimal(String jsonPath, boolean parseKey) {

        Object object = getElement(jsonPath, true, getdefaultElementValidator(), parseKey);
        // reference equality ok here
        return object != getdefaultElementValidator() && getObjectConverter().isCanBeConvertedToBigDecimal(object);
    }

    @Override
    public boolean isCanBeConvertedToByteArray(String jsonPath) {
        return isCanBeConvertedToByteArray(jsonPath, true);
    }

    public boolean isCanBeConvertedToByteArrayNoKeyParsing(String jsonPath) {
        return isCanBeConvertedToByteArray(jsonPath, false);
    }

    protected boolean isCanBeConvertedToByteArray(String jsonPath, boolean parseKey) {

        Object object = getElement(jsonPath, true, getdefaultElementValidator(), parseKey);
        // reference equality ok here
        return object != getdefaultElementValidator() && getObjectConverter().isCanBeConvertedToByteArray(object);
    }

    @Override
    public boolean isCanBeConvertedToDate(String jsonPath) {
        return isCanBeConvertedToDate(jsonPath, true);
    }

    public boolean isCanBeConvertedToDateNoKeyParsing(String jsonPath) {
        return isCanBeConvertedToDate(jsonPath, false);
    }

    protected boolean isCanBeConvertedToDate(String jsonPath, boolean parseKey) {

        Object object = getElement(jsonPath, true, getdefaultElementValidator(), parseKey);
        // reference equality ok here
        return object != getdefaultElementValidator() && getObjectConverter().isCanBeConvertedToDateFromJsonDateFormat(object);
    }

    @Override
    public boolean isCanBeConvertedToJsonObject(String jsonPath) {
        return isCanBeConvertedToJsonObject(jsonPath, true);
    }

    public boolean isCanBeConvertedToJsonObjectNoKeyParsing(String jsonPath) {
        return isCanBeConvertedToJsonObject(jsonPath, false);
    }

    protected boolean isCanBeConvertedToJsonObject(String jsonPath, boolean parseKey) {

        Object object = getElement(jsonPath, true, getdefaultElementValidator(), parseKey);
        // reference equality ok here
        return object != getdefaultElementValidator() && getObjectConverter().isCanBeConvertedToJsonObject(object);
    }

    @Override
    public boolean isCanBeConvertedToJsonArray(String jsonPath) {
        return isCanBeConvertedToJsonArray(jsonPath, true);
    }

    public boolean isCanBeConvertedToJsonArrayNoKeyParsing(String jsonPath) {
        return isCanBeConvertedToJsonArray(jsonPath, false);
    }

    protected boolean isCanBeConvertedToJsonArray(String jsonPath, boolean parseKey) {

        Object object = getElement(jsonPath, true, getdefaultElementValidator(), parseKey);
        // reference equality ok here
        return object != getdefaultElementValidator() && getObjectConverter().isCanBeConvertedToJsonArray(object);
    }

    @Override
    public boolean isOfTypeString(String jsonPath) {
        return isOfTypeString(jsonPath, true);
    }

    public boolean isOfTypeStringNoKeyParsing(String jsonPath) {
        return isOfTypeString(jsonPath, false);
    }

    public boolean isOfTypeString(String jsonPath, boolean parseKey) {
        Object val = getElement(jsonPath, false, null, parseKey);
        return val == null || val instanceof String;
    }

    @Override
    public boolean isOfTypeInteger(String jsonPath) {
        return isOfTypeInteger(jsonPath, true);
    }

    public boolean isOfTypeIntegerNoKeyParsing(String jsonPath) {
        return isOfTypeInteger(jsonPath, false);
    }

    public boolean isOfTypeInteger(String jsonPath, boolean parseKey) {
        Object val = getElement(jsonPath, false, null, parseKey);
        return val == null || val instanceof Integer;
    }

    @Override
    public boolean isOfTypeLong(String jsonPath) {
        return isOfTypeLong(jsonPath, true);
    }

    public boolean isOfTypeLongNoKeyParsing(String jsonPath) {
        return isOfTypeLong(jsonPath, false);
    }

    public boolean isOfTypeLong(String jsonPath, boolean parseKey) {
        Object val = getElement(jsonPath, false, null, parseKey);
        return val == null || val instanceof Long;
    }

    @Override
    public boolean isOfTypeFloat(String jsonPath) {
        return isOfTypeFloat(jsonPath, true);
    }

    public boolean isOfTypeFloatNoKeyParsing(String jsonPath) {
        return isOfTypeFloat(jsonPath, false);
    }

    public boolean isOfTypeFloat(String jsonPath, boolean parseKey) {
        Object val = getElement(jsonPath, false, null, parseKey);
        return val == null || val instanceof Float;
    }

    @Override
    public boolean isOfTypeDouble(String jsonPath) {
        return isOfTypeDouble(jsonPath, true);
    }

    public boolean isOfTypeDoubleNoKeyParsing(String jsonPath) {
        return isOfTypeDouble(jsonPath, false);
    }

    public boolean isOfTypeDouble(String jsonPath, boolean parseKey) {
        Object val = getElement(jsonPath, false, null, parseKey);
        return val == null || val instanceof Double;
    }

    @Override
    public boolean isOfTypeBoolean(String jsonPath) {
        return isOfTypeBoolean(jsonPath, true);
    }

    public boolean isOfTypeBooleanNoKeyParsing(String jsonPath) {
        return isOfTypeBoolean(jsonPath, false);
    }

    public boolean isOfTypeBoolean(String jsonPath, boolean parseKey) {
        Object val = getElement(jsonPath, false, null, parseKey);
        return val == null || val instanceof Boolean;
    }

    @Override
    public boolean isOfTypeBigDecimal(String jsonPath) {
        return isOfTypeeBigDecimal(jsonPath, true);
    }

    public boolean isOfTypeBigDecimalNoKeyParsing(String jsonPath) {
        return isOfTypeeBigDecimal(jsonPath, false);
    }

    public boolean isOfTypeeBigDecimal(String jsonPath, boolean parseKey) {
        Object val = getElement(jsonPath, false, null, parseKey);
        return val == null || val instanceof BigDecimal;
    }

    @Override
    public boolean isOfTypeByteArray(String jsonPath, boolean acceptBase64StringToo) {
        return isOfTypeByteArray(jsonPath, acceptBase64StringToo, true);
    }

    public boolean isOfTypeByteArrayNoKeyParsing(String jsonPath, boolean acceptBase64StringToo) {
        return isOfTypeByteArray(jsonPath, acceptBase64StringToo, false);
    }

    public boolean isOfTypeByteArray(String jsonPath, boolean acceptBase64StringToo, boolean parseKey) {

        Object val = getElement(jsonPath, false, null, parseKey);

        if (val == null || val instanceof byte[]) {
            return true;
        }

        if (!acceptBase64StringToo || !(val instanceof String)) {
            return false;
        }

        //==========================================
        // Valid base 64 String?
        //==========================================
        String valStr = (String)val;
        try {
            getBytesFromBase64StringValueFromObject(valStr);
            return true;
        } catch (CantConvertException ex) {
            return false;
        }
    }

    @Override
    public boolean isOfTypeDate(String jsonPath) {
        return isOfTypeDate(jsonPath, true);
    }

    public boolean isOfTypeDateNoKeyParsing(String jsonPath) {
        return isOfTypeDate(jsonPath, false);
    }

    public boolean isOfTypeDate(String jsonPath, boolean parseKey) {
        Object val = getElement(jsonPath, false, null, parseKey);
        return val == null || val instanceof Date;
    }

    @Override
    public boolean isOfTypeJsonObject(String jsonPath) {
        return isOfTypeJsonObject(jsonPath, true);
    }

    public boolean isOfTypeJsonObjectNoKeyParsing(String jsonPath) {
        return isOfTypeJsonObject(jsonPath, false);
    }

    public boolean isOfTypeJsonObject(String jsonPath, boolean parseKey) {
        Object val = getElement(jsonPath, false, null, parseKey);
        return val == null || val instanceof JsonObject;
    }

    @Override
    public boolean isOfTypeJsonArray(String jsonPath) {
        return isOfTypeJsonArray(jsonPath, true);
    }

    public boolean isOfTypeJsonArrayNoKeyParsing(String jsonPath) {
        return isOfTypeJsonArray(jsonPath, false);
    }

    public boolean isOfTypeJsonArray(String jsonPath, boolean parseKey) {
        Object val = getElement(jsonPath, false, null, parseKey);
        return val == null || val instanceof JsonArray;
    }

    @Override
    public boolean isNull(String jsonPath) {
        return validateIsNull(jsonPath, true);
    }

    public boolean isNullNoKeyParsing(String jsonPath) {
        return validateIsNull(jsonPath, false);
    }

    public boolean validateIsNull(String jsonPath, boolean parseKey) {
        Object val = getElement(jsonPath, true, "", parseKey);
        return val == null;
    }

    @Override
    public boolean contains(String jsonPath) {

        //==========================================
        // This will use the cache if the current object is
        // immutable.
        //==========================================
        Object object = getElement(jsonPath, true, getdefaultElementValidator(), true);

        // reference equality ok here
        if (object == getdefaultElementValidator()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Gets the element at this key/jsonPath/index.
     * If the current object is immutable, the target element
     * may be cached.
     */
    protected Object getElement(String key,
                                boolean hasdefaultElement,
                                Object defaultElement,
                                boolean parseJsonPath) {

        if (!parseJsonPath) {
            return getElementNoKeyParsing(key, hasdefaultElement, defaultElement);
        } else {

            //==========================================
            // If the object is immutable, we may have the
            // element of the specified JsonPath in cache.
            //==========================================
            if (!isMutable()) {

                JsonPathCachingItem jsonPathCachingItem = getJsonPathCachingMap().get(key);
                if (jsonPathCachingItem != null) {
                    if (jsonPathCachingItem.exists) {
                        return jsonPathCachingItem.element;
                    } else {
                        return defaultElement;
                    }
                }
            }

            Object element;
            if (this instanceof JsonObject) {
                element = getJsonManager().getElementAtJsonPath((JsonObject)this, key, getdefaultElementValidator());
            } else if (this instanceof JsonArray) {
                element = getJsonManager().getElementAtJsonPath((JsonArray)this, key, getdefaultElementValidator());
            } else {
                throw new RuntimeException("Type not managed here : " + this.getClass().getName());
            }

            Object elementToReturn = element;
            boolean jsonPathElementExists = true;
            // reference equality ok here
            if (element == getdefaultElementValidator()) {
                jsonPathElementExists = false;
                if (hasdefaultElement) {
                    elementToReturn = defaultElement;
                } else {
                    elementToReturn = null;
                }
            }

            if (!isMutable()) {
                JsonPathCachingItem jsonPathCachingItem = new JsonPathCachingItem();
                jsonPathCachingItem.exists = jsonPathElementExists;
                jsonPathCachingItem.element = element;
                getJsonPathCachingMap().put(key, jsonPathCachingItem);
            }

            return elementToReturn;
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        if (!isMutable()) {
            return clone(false);
        } else {
            return clone(true);
        }
    }

    protected ElementTransformer getTrimTransformer() {

        if (this.trimTransformer == null) {
            this.trimTransformer = new ElementTransformer() {

                @Override
                public Object transform(Object obj) {

                    if (obj == null) {
                        return null;
                    }
                    if (!(obj instanceof String)) {
                        return obj;
                    }

                    return ((String)obj).trim();
                }
            };
        }

        return this.trimTransformer;
    }

    @Override
    public void trim(String jsonPath) {
        transform(jsonPath, getTrimTransformer());
    }

    @Override
    public void trimAll() {
        trimAll(true);
    }

    @Override
    public void trimAll(boolean recursive) {
        transformAll(getTrimTransformer(), recursive);
    }

    @Override
    public void transform(String jsonPath, ElementTransformer transformer) {
        Object obj = getObject(jsonPath);
        obj = transformer.transform(obj);
        set(jsonPath, obj);
    }

    @Override
    public void transformAll(ElementTransformer transformer) {
        transformAll(transformer, true);
    }

    @Override
    public JsonObjectOrArray set(String jsonPath, Object element) {
        return put(jsonPath, element, false, true);
    }

    @Override
    public JsonObjectOrArray set(String jsonPath, Object element, boolean clone) {
        return put(jsonPath, element, clone, true);
    }

    protected JsonObjectOrArray put(String jsonPath, Object element, boolean clone, boolean parseJsonPath) {

        if (!isMutable()) {
            throw new RuntimeException("This object is immutable");
        }

        Objects.requireNonNull(jsonPath, "jsonPath key can't be NULL");

        if (element != null) {

            //==========================================
            // Can the object convert itself to a 
            // JsonObject or JsonArray?
            //==========================================
            boolean newObject = false;
            if (element instanceof ToJsonObjectConvertible) {
                newObject = true;
                element = ((ToJsonObjectConvertible)element).convertToJsonObject();
            } else if (element instanceof ToJsonArrayConvertible) {
                newObject = true;
                element = ((ToJsonArrayConvertible)element).convertToJsonArray();
            }

            //==========================================
            // Do we have to make a clone?
            // We always do if the element is a JsonObject or a JsonArray and
            // its mutability is not the same as the current object.
            //==========================================
            if (element instanceof JsonObjectOrArray) {
                if (!newObject && (clone || ((JsonObjectOrArray)element).isMutable() != isMutable())) {
                    element = ((JsonObjectOrArray)element).clone(isMutable());
                }
            } else {
                element = getJsonManager().convertToNativeType(element);
            }
        }

        if (parseJsonPath) {
            // Already cloned if required.
            getJsonManager().putElementAtJsonPath(this, jsonPath, element, false);
        } else {
            putAsIs(jsonPath, element);
        }

        return this;
    }

    protected abstract JsonObjectOrArray putAsIs(String key, Object element);

    @Override
    public abstract void transformAll(ElementTransformer transformer, boolean recursive);

    /**
     * Clone the object.
     * 
     * @param mutable if <code>true</code> the resulting
     * object and all its children will be mutable, otherwise
     * they will all be immutable.
     */
    @Override
    public abstract JsonObjectOrArray clone(boolean mutable);

    /**
     * Gets the element at this key/index, without
     * key parsing.
     */
    protected abstract Object getElementNoKeyParsing(String key,
                                                     boolean hasdefaultElement,
                                                     Object defaultElement);

}
