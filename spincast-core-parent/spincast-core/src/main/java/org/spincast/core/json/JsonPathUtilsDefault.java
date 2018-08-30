package org.spincast.core.json;

import java.util.Objects;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exceptions.CantConvertException;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;

public class JsonPathUtilsDefault implements JsonPathUtils {

    protected static enum JsonPathProcessingType {
        PUT,
        GET,
        REMOVE,
        EXISTS
    }

    private final SpincastConfig spincastConfig;
    private final JsonManager jsonManager;

    /**
     * Constructor
     */
    @Inject
    public JsonPathUtilsDefault(SpincastConfig spincastConfig,
                                JsonManager jsonManager) {
        this.spincastConfig = spincastConfig;
        this.jsonManager = jsonManager;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected int getJsonPathArrayIndexMax() {
        return getSpincastConfig().getJsonPathArrayIndexMax();
    }

    protected int getKeyMaxLengthWhenConvertingMapToJsonObject() {
        return getSpincastConfig().getKeyMaxLengthWhenConvertingMapToJsonObject();
    }

    @Override
    public Object getElementAtJsonPath(JsonObject obj, String jsonPath) {
        return selectValueUsingJsonPath(obj, jsonPath, false, null);
    }

    @Override
    public Object getElementAtJsonPath(JsonObject obj, String jsonPath, Object defaultElement) {
        return selectValueUsingJsonPath(obj, jsonPath, true, defaultElement);
    }

    protected Object selectValueUsingJsonPath(JsonObject obj, String jsonPath, boolean hasDefaultValue, Object defaultElement) {
        return processJsonPath(obj, jsonPath, JsonPathProcessingType.GET, null, hasDefaultValue, defaultElement);
    }

    @Override
    public Object getElementAtJsonPath(JsonArray array, String jsonPath) {
        return selectValueUsingJsonPath(array, jsonPath, false, null);
    }

    @Override
    public Object getElementAtJsonPath(JsonArray array, String jsonPath, Object defaultElement) {
        return selectValueUsingJsonPath(array, jsonPath, true, defaultElement);
    }

    protected Object selectValueUsingJsonPath(JsonArray array, String jsonPath, boolean hasDefaultValue, Object defaultElement) {

        Objects.requireNonNull(array, "The array can't be NULL");

        JsonObject root = getJsonManager().create();
        root.setNoKeyParsing("array", array, false);

        jsonPath = "array" + jsonPath;

        Object result = selectValueUsingJsonPath(root, jsonPath, hasDefaultValue, defaultElement);

        return result;
    }

    @Override
    public void putElementAtJsonPath(JsonObjectOrArray root, String jsonPath, Object valueToAdd) {
        Objects.requireNonNull(root, "The root can't be NULL");

        JsonObject rootObj = null;
        if (root instanceof JsonObject) {
            rootObj = (JsonObject)root;
        } else if (root instanceof JsonArray) {

            if (jsonPath == null || !jsonPath.startsWith("[")) {
                throw new RuntimeException("The JsonPath from a JsonArray must " +
                                           "start with the position of an element of the array inside brackets. " +
                                           "For example : \"[2]\".");
            }
            jsonPath = "array" + jsonPath;

            rootObj = getJsonManager().create();
            rootObj.setNoKeyParsing("array", (JsonArray)root, false);

        } else {
            throw new RuntimeException("Not managed here : " + root);
        }

        processJsonPath(rootObj, jsonPath, JsonPathProcessingType.PUT, valueToAdd, false, null);
    }

    @Override
    public void removeElementAtJsonPath(JsonObject root, String jsonPath) {
        if (root == null) {
            return;
        }

        processJsonPath(root, jsonPath, JsonPathProcessingType.REMOVE, null, false, null);
    }

    @Override
    public void removeElementAtJsonPath(JsonArray array, String jsonPath) {

        if (array == null) {
            return;
        }

        JsonObject root = getJsonManager().create();
        root.setNoKeyParsing("a", array, false);

        jsonPath = "a" + jsonPath;

        removeElementAtJsonPath(root, jsonPath);
    }

    @Override
    public boolean isElementExists(JsonObject root, String jsonPath) {

        if (root == null || jsonPath == null) {
            return false;
        }

        Object result = processJsonPath(root, jsonPath, JsonPathProcessingType.EXISTS, null, false, null);
        if (!(result instanceof Boolean)) {
            throw new RuntimeException("Expecting a Boolean here! Got : " + result);
        }
        return (Boolean)result;
    }

    @Override
    public boolean isElementExists(JsonArray array, String jsonPath) {

        if (array == null || jsonPath == null) {
            return false;
        }

        JsonObject root = getJsonManager().create();
        root.setNoKeyParsing("a", array, false);

        jsonPath = "a" + jsonPath;

        return isElementExists(root, jsonPath);
    }

    protected Object processJsonPath(JsonObject root,
                                     String jsonPath,
                                     JsonPathProcessingType jsonPathProcessingType,
                                     Object valueToPut,
                                     boolean hasDefaultValue,
                                     Object defaultElement) {

        Objects.requireNonNull(root, "The root object can't be NULL");

        if (jsonPath == null) {
            if (hasDefaultValue) {
                return defaultElement;
            } else {
                return null;
            }
        }

        JsonObject currentObj = root;
        JsonArray currentArray = null;
        int currentArrayIndex = 0;
        String token;

        //==========================================
        // Simple JsonPath, without any special character. We
        // don't need to parse it.
        //==========================================
        if (!StringUtils.containsAny(jsonPath, ".[]")) {
            token = jsonPath;

        //==========================================@formatter:off 
        // We need to parse the JsonPath
        //==========================================@formatter:on
        } else {

            if (jsonPath.length() > getKeyMaxLengthWhenConvertingMapToJsonObject()) {
                throw new RuntimeException("A JsonPath is too long to be parsed. This JsonPath starts with : " +
                                           jsonPath.substring(0, Math.min(30, (jsonPath.length() - 1))));
            }

            StringBuilder tokenBuilder = new StringBuilder();

            //==========================================
            // We allow the JsonPath to start with a "."
            // even if it's not required.
            //==========================================
            if (jsonPath.startsWith(".")) {
                jsonPath = jsonPath.substring(1);
            }

            //==========================================
            // Initial validations
            //==========================================
            if (jsonPath.startsWith(".") || jsonPath.endsWith(".")) {
                throw new RuntimeException("JsonPath parsing error on character '.'. The JsonPath can't start or end with the " +
                                           "'.' character. JsonPath : " + jsonPath);
            }
            if (jsonPath.endsWith("[")) {
                throw new RuntimeException("JsonPath parsing error on character '['. The JsonPath can't end with the " +
                                           "'[' character. JsonPath : " + jsonPath);
            }
            if (jsonPath.startsWith("]")) {
                throw new RuntimeException("JsonPath parsing error on character '['. The JsonPath can't start with the " +
                                           "']' character. JsonPath : " + jsonPath);
            }

            //==========================================
            // We parse the JsonPath, one character at the time.
            //==========================================
            boolean isInBrackets = false;
            boolean isInQuotes = false;
            boolean isInDoubleQuotes = false;
            char ch = '\0';
            char previousChar = '\0';
            for (int i = 0; i < jsonPath.length(); i++) {

                previousChar = ch;
                ch = jsonPath.charAt(i);

                //==========================================
                // The '.' character
                //==========================================
                if (ch == '.') {

                    //==========================================
                    // In quotes, this character has no
                    // special meaning.
                    //==========================================
                    if (isInQuotes || isInDoubleQuotes) {
                        tokenBuilder.append(ch);
                        continue;
                    }

                    if (isInBrackets) {
                        throw new RuntimeException("JsonPath parsing error on character '.'. A dot is not valid inside brackets but " +
                                                   "outside quotes. JsonPath : " + jsonPath);
                    }

                    if (currentObj != null) {

                        //==========================================
                        // Empty token : invalid name for an object
                        //==========================================
                        token = tokenBuilder.toString();
                        if ("".equals(token)) {
                            throw new RuntimeException("JsonPath parsing error on character '.'. A token is empty. JsonPath : " +
                                                       jsonPath);
                        }
                        tokenBuilder = new StringBuilder();

                        //==========================================
                        // Creates a new object on the current object, 
                        // if this object doesn't already exist.
                        //==========================================
                        JsonObject obj;
                        try {
                            obj = currentObj.getJsonObjectNoKeyParsing(token);
                        } catch (CantConvertException ex) {
                            throw new RuntimeException("JsonPath parsing error on character '.'. " +
                                                       "The key '" + token + "' already exists, but the associated value is " +
                                                       "not of type JsonObject as expected here. JsonPath : " +
                                                       jsonPath);
                        }

                        if (obj == null) {

                            if (jsonPathProcessingType == JsonPathProcessingType.GET) {
                                if (hasDefaultValue) {
                                    return defaultElement;
                                } else {
                                    return null;
                                }
                            } else if (jsonPathProcessingType == JsonPathProcessingType.EXISTS) {
                                return false;
                            } else {
                                obj = getJsonManager().create();
                                currentObj.set(token, obj);
                            }
                        }

                        //==========================================
                        // This object is now our current object.
                        //==========================================
                        currentObj = obj;
                        currentArray = null;

                    } else {

                        //==========================================
                        // Creates a new object on the current array, 
                        // if this object doesn't already exist.
                        //==========================================
                        JsonObject obj;
                        try {
                            obj = currentArray.getJsonObject(currentArrayIndex);
                        } catch (CantConvertException ex) {
                            throw new RuntimeException("JsonPath parsing error on character '.'. " +
                                                       "The element of the array at index '" + currentArrayIndex +
                                                       "' already exists, but is " +
                                                       "not of type JsonObject as expected here. JsonPath : " +
                                                       jsonPath);
                        }

                        if (obj == null) {

                            if (jsonPathProcessingType == JsonPathProcessingType.GET) {
                                if (hasDefaultValue) {
                                    return defaultElement;
                                } else {
                                    return null;
                                }
                            } else if (jsonPathProcessingType == JsonPathProcessingType.EXISTS) {
                                return false;
                            } else {
                                obj = getJsonManager().create();
                                currentArray.set(currentArrayIndex, obj);
                            }
                        }

                        currentObj = obj;
                        currentArray = null;
                    }

                //==========================================@formatter:off 
                // The '[' character
                //==========================================@formatter:on
                } else if (ch == '[') {

                    //==========================================
                    // In quotes, this character has no
                    // special meaning.
                    //==========================================
                    if (isInQuotes || isInDoubleQuotes) {
                        tokenBuilder.append(ch);
                        continue;
                    }

                    if (isInBrackets) {
                        throw new RuntimeException("JsonPath parsing error on character '['.The '[' is not valid inside " +
                                                   "already started brackets. JsonPath : " + jsonPath);
                    }

                    token = tokenBuilder.toString();
                    tokenBuilder = new StringBuilder();

                    //==========================================
                    // If what's inside the bracket is a String key, then
                    // we create an object.
                    // If it's an integer index, we create an array.
                    //==========================================
                    char nextChar = jsonPath.charAt(i + 1);
                    if (nextChar == '"' || nextChar == '\'') {

                        JsonObject obj = null;
                        if (currentObj != null) {

                            //==========================================
                            // If the JsonPath starts with a
                            // quoted key (for example : "['some key']" ), then 
                            // we do not create a new object since it already exists
                            // (its the root object).
                            //
                            // Also, we only create a new object if it doesn't
                            // already exist.
                            //==========================================
                            if (i != 0) {
                                try {
                                    obj = currentObj.getJsonObject(token);
                                } catch (CantConvertException ex) {
                                    throw new RuntimeException("JsonPath parsing error on character '['. " +
                                                               "The key '" + token +
                                                               "' already exists, but the associated value is " +
                                                               "not of type JsonObject, as expected here. JsonPath : " +
                                                               jsonPath);
                                }

                                if (obj == null) {

                                    if (jsonPathProcessingType == JsonPathProcessingType.GET) {
                                        if (hasDefaultValue) {
                                            return defaultElement;
                                        } else {
                                            return null;
                                        }
                                    } else if (jsonPathProcessingType == JsonPathProcessingType.EXISTS) {
                                        return false;
                                    } else {
                                        obj = getJsonManager().create();
                                        currentObj.set(token, obj);
                                    }
                                }
                                currentObj = obj;
                                currentArray = null;
                            }

                        } else {

                            //==========================================
                            // Creates a new object on the current array, 
                            // if this object doesn't already exist.
                            //==========================================
                            try {
                                obj = currentArray.getJsonObject(currentArrayIndex);
                            } catch (CantConvertException ex) {
                                throw new RuntimeException("JsonPath parsing error on character '['. " +
                                                           "The index '" + currentArrayIndex +
                                                           "' points to an already existing element, but " +
                                                           "not of type JsonObject. JsonPath : " +
                                                           jsonPath);
                            }

                            if (obj == null) {

                                if (jsonPathProcessingType == JsonPathProcessingType.GET) {
                                    if (hasDefaultValue) {
                                        return defaultElement;
                                    } else {
                                        return null;
                                    }
                                } else if (jsonPathProcessingType == JsonPathProcessingType.EXISTS) {
                                    return false;
                                } else {
                                    obj = getJsonManager().create();
                                    currentArray.set(currentArrayIndex, obj);
                                }
                            }

                            currentObj = obj;
                            currentArray = null;
                        }

                    //==========================================@formatter:off 
                    // We are dealing with an integer index,
                    // we have to create an array if requried.
                    //==========================================@formatter:on
                    } else {

                        JsonArray array;
                        if (currentObj != null) {

                            //==========================================
                            // The root object is not an array so the
                            // JsonPath Key can't start with something
                            // like "[0]"
                            //==========================================
                            if (i == 0) {
                                throw new RuntimeException("JsonPath parsing error on character '['. The root object is not " +
                                                           "an array. JsonPath : " +
                                                           jsonPath);
                            }

                            //==========================================
                            // The index can't be empty
                            //==========================================
                            if ("".equals(token)) {
                                throw new RuntimeException("JsonPath parsing error on character '['. A token is empty . JsonPath : " +
                                                           jsonPath);
                            }

                            //==========================================
                            // Creates a new array on the current object, 
                            // if this array doesn't already exist.
                            //==========================================
                            try {
                                array = currentObj.getJsonArrayNoKeyParsing(token);
                            } catch (CantConvertException ex) {
                                throw new RuntimeException("JsonPath parsing error on character '['. " +
                                                           "The key '" + token +
                                                           "' points to an already existing element, but " +
                                                           "not of type JsonArray, as expected here. " +
                                                           "JsonPath : " + jsonPath);
                            }

                            if (array == null) {

                                if (jsonPathProcessingType == JsonPathProcessingType.GET) {
                                    if (hasDefaultValue) {
                                        return defaultElement;
                                    } else {
                                        return null;
                                    }
                                } else if (jsonPathProcessingType == JsonPathProcessingType.EXISTS) {
                                    return false;
                                } else {
                                    array = getJsonManager().createArray();
                                    currentObj.set(token, array);
                                }
                            }

                        } else {

                            //==========================================
                            // Creates a new array on the current array, 
                            // if this array doesn't already exist.
                            //==========================================
                            try {
                                array = currentArray.getJsonArray(currentArrayIndex);
                            } catch (CantConvertException ex) {
                                throw new RuntimeException("JsonPath parsing error on character '['. " +
                                                           "The index '" + currentArrayIndex +
                                                           "' points to an already existing element, but " +
                                                           "not of type JsonArray. JsonPath : " +
                                                           jsonPath);
                            }

                            if (array == null) {

                                if (jsonPathProcessingType == JsonPathProcessingType.GET) {
                                    if (hasDefaultValue) {
                                        return defaultElement;
                                    } else {
                                        return null;
                                    }
                                } else if (jsonPathProcessingType == JsonPathProcessingType.EXISTS) {
                                    return false;
                                } else {
                                    array = getJsonManager().createArray();
                                    currentArray.set(currentArrayIndex, array);
                                }
                            }
                        }

                        currentObj = null;
                        currentArray = array;
                    }

                    isInBrackets = true;

                //==========================================@formatter:off 
                // The ']' character
                //==========================================@formatter:on
                } else if (ch == ']') {

                    //==========================================
                    // In quotes, this  character has no
                    // special meaning.
                    //==========================================
                    if (isInQuotes || isInDoubleQuotes) {
                        tokenBuilder.append(ch);
                        continue;
                    }

                    if (!isInBrackets) {
                        throw new RuntimeException("JsonPath parsing error on character ']'. No start bracket found. JsonPath : " +
                                                   jsonPath);
                    }

                    //==========================================
                    // Next char must be ".", "[" or the end 
                    // of the JsonPath.
                    //==========================================
                    if (i < (jsonPath.length() - 1)) {
                        char nextChar = jsonPath.charAt(i + 1);
                        if (nextChar != '.' && nextChar != '[') {
                            throw new RuntimeException("JsonPath parsing error on character ']'. The character following a ']', if any, must " +
                                                       "be '.' or '['. Here, the following character is '" + nextChar +
                                                       "'. JsonPath : " + jsonPath);
                        }
                    }

                    //==========================================
                    // Empty token : invalid name for an index/key
                    //==========================================
                    token = tokenBuilder.toString();
                    if ("".equals(token)) {
                        throw new RuntimeException("JsonPath parsing error on character ']'. A key or an index was expected. JsonPath : " +
                                                   jsonPath);
                    }

                    //==========================================
                    // What's inside the brackets is a key
                    //==========================================
                    if ((token.startsWith("\"") && token.endsWith("\"")) || (token.startsWith("'") && token.endsWith("'"))) {
                        String key = token.substring(1, token.length() - 1);
                        if ("".equals(key)) {
                            throw new RuntimeException("JsonPath parsing error on character ']'. A key can't be empty. JsonPath : " +
                                                       jsonPath);
                        }
                        //==========================================
                        // This becomes our token :
                        //==========================================
                        tokenBuilder = new StringBuilder(key);

                    //==========================================@formatter:off 
                    // What's inside the beckets is an index
                    //==========================================@formatter:on
                    } else {

                        Integer index = null;
                        try {
                            index = Integer.parseInt(token);
                        } catch (NumberFormatException ex) {
                            throw new RuntimeException("JsonPath parsing error on character ']'. The index '" + token +
                                                       "' is not a valid integer. " +
                                                       "You have to use quotes or double-quotes for an object key, or a valid integer for an array index. JsonPath : " +
                                                       jsonPath);
                        }
                        if (index < 0) {
                            throw new RuntimeException("JsonPath parsing error on character ']'. The index of an array can't " +
                                                       "be less than 0. JsonPath : " + jsonPath);

                        } else if (index > getJsonPathArrayIndexMax()) {
                            throw new RuntimeException("The index of an array is currently configured to have a " +
                                                       "maximum value of " + getJsonPathArrayIndexMax() + ". JsonPath : " +
                                                       jsonPath);
                        }

                        if (currentArray == null) {
                            throw new RuntimeException("JsonPath parsing error on character ']'. Expecting a non-null array here. JsonPath : " +
                                                       jsonPath);
                        }

                        //==========================================
                        // We keep the current array index.
                        //==========================================
                        currentArrayIndex = index;

                        //==========================================
                        // Reset the token builder
                        //==========================================
                        tokenBuilder = new StringBuilder();
                    }

                    isInBrackets = false;

                //==========================================@formatter:off 
                // The '"' character
                //==========================================@formatter:on
                } else if (ch == '"') {

                    //==========================================
                    // In single quotes, when not inside brackets, or
                    // when preceding by a "\", the '"' character has no
                    // special meaning.
                    //==========================================
                    if (isInBrackets && !isInQuotes && previousChar != '\\') {

                        //==========================================
                        // Otherwise, it starts or ends a key.
                        //==========================================
                        isInDoubleQuotes = !isInDoubleQuotes;
                    }

                    tokenBuilder.append(ch);

                //==========================================@formatter:off 
                // The "'" character
                //==========================================@formatter:on
                } else if (ch == '\'') {

                    //==========================================
                    // In double quotes, when not inside brackets, or
                    // when preceding by a "\", the "'" character has no
                    // special meaning.
                    //==========================================
                    if (isInBrackets && !isInDoubleQuotes && previousChar != '\\') {

                        //==========================================
                        // Otherwise, it starts or ends a key.
                        //==========================================
                        isInQuotes = !isInQuotes;
                    }
                    tokenBuilder.append(ch);

                //========================================== @formatter:off 
                // Any other character!
                //========================================== @formatter:on
                } else {

                    if (isInQuotes || isInDoubleQuotes) {

                        //==========================================
                        // Only the special characters and "\" can be
                        // escaped by a "\".
                        //==========================================
                        if (previousChar == '\\') {

                            if (ch != '\\') {
                                throw new RuntimeException("JsonPath parsing error on the '" + ch + "' character. " +
                                                           "This character can't be escaped. If you want to use a '\'  inside a " +
                                                           "name, you need to escape it : \"\\\\\". JsonPath : " +
                                                           jsonPath);
                            } else {
                                tokenBuilder.append(ch);
                                ch = '\0';
                                continue;
                            }
                        }

                        //==========================================
                        // We do not add the '\' character, except if it's
                        // doubled (it's already added above!).
                        //==========================================
                        if (ch == '\\') {
                            continue;
                        }

                    } else if (isInBrackets) {

                        if (!(ch >= '0' && ch <= '9')) {
                            throw new RuntimeException("JsonPath parsing error on the '" + ch + "' character. " +
                                                       "Invalid character in the index, expecting a digit, got " +
                                                       "'" + ch + "'. JsonPath : " +
                                                       jsonPath);
                        }

                    } else {

                        //==========================================
                        // Validates the char to be used a an object 
                        // property name without quotes.
                        //==========================================
                        if (ch == '.' || ch == '[' || ch == ']') {
                            throw new RuntimeException("The characters '.', '[' and ']' are not valid inside a object " +
                                                       "name or a property name, if this " +
                                                       "name is not in quotes. In JsonPath : " + jsonPath);
                        }
                    }

                    tokenBuilder.append(ch);
                }
            }

            //==========================================
            // We reached the end of the JsonPath...
            //==========================================

            if (isInBrackets) {
                throw new RuntimeException("JsonPath parsing error on the last character. Some brackets were not " +
                                           "closed properly. JsonPath : " + jsonPath);
            }

            token = tokenBuilder.toString();
        }

        if (currentObj != null) {

            if (jsonPathProcessingType == JsonPathProcessingType.PUT) {

                //==========================================
                // Cloning must habe been done before this method.
                // No more parsing of the key.
                //==========================================
                currentObj.setNoKeyParsing(token, valueToPut, false);

                return null;

            } else if (jsonPathProcessingType == JsonPathProcessingType.GET) {

                if (hasDefaultValue) {
                    return currentObj.getObjectNoKeyParsing(token, defaultElement);
                } else {
                    return currentObj.getObjectNoKeyParsing(token);
                }

            } else if (jsonPathProcessingType == JsonPathProcessingType.EXISTS) {
                return currentObj.isElementExistsNoKeyParsing(token);
            } else if (jsonPathProcessingType == JsonPathProcessingType.REMOVE) {
                currentObj.removeNoKeyParsing(token);
                return null;
            } else {
                throw new RuntimeException("Unmanaged JsonPathProcessingType here : " + jsonPathProcessingType);
            }

        } else if (currentArray != null) {

            if (jsonPathProcessingType == JsonPathProcessingType.PUT) {

                //==========================================
                // Cloning must have been done before this method
                //==========================================
                currentArray.set(currentArrayIndex, valueToPut, false);

                return null;

            } else if (jsonPathProcessingType == JsonPathProcessingType.GET) {

                if (hasDefaultValue) {
                    return currentArray.getObject(currentArrayIndex, defaultElement);
                } else {
                    return currentArray.getObject(currentArrayIndex);
                }

            } else if (jsonPathProcessingType == JsonPathProcessingType.EXISTS) {
                return currentArray.isElementExists(currentArrayIndex);
            } else if (jsonPathProcessingType == JsonPathProcessingType.REMOVE) {
                currentArray.remove(currentArrayIndex);
                return null;
            } else {
                throw new RuntimeException("Unmanaged JsonPathProcessingType here : " + jsonPathProcessingType);
            }
        } else {
            throw new RuntimeException("Not supposed. Invalid parsing of JsonPath : " + jsonPath);
        }
    }

}
