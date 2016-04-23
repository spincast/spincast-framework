package org.spincast.core.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Some often used <code>Content-Types</code>.
 */
public enum ContentTypeDefaults {

    TEXT("text/plain"),
    JSON("application/json", "application/javascript"),
    XML("application/xml", "text/xml"),
    HTML("text/html", "application/xhtml+xml"),
    BINARY("application/octet-stream");

    private static Set<String> allVariations;

    private final String mainVariation;
    private final Set<String> variations;

    private ContentTypeDefaults(String... variations) {
        this.mainVariation = variations[0];
        this.variations = new HashSet<String>(Arrays.asList(variations));
    }

    public String getMainVariation() {
        return this.mainVariation;
    }

    public String getMainVariationWithUtf8Charset() {
        return this.getMainVariation() + "; charset=utf-8";
    }

    public Set<String> getVariations() {
        return this.variations;
    }

    public static ContentTypeDefaults fromString(String contentTypeString) {
        if(contentTypeString == null) {
            return null;
        }
        contentTypeString = contentTypeString.toLowerCase();
        for(ContentTypeDefaults contentType : ContentTypeDefaults.values()) {
            if(contentType.getVariations().contains(contentTypeString)) {
                return contentType;
            }
        }
        return null;
    }

    public static Set<String> getAllContentTypesVariations() {

        if(allVariations == null) {
            allVariations = new HashSet<String>();
            for(ContentTypeDefaults contentType : ContentTypeDefaults.values()) {
                allVariations.addAll(contentType.getVariations());
            }
        }
        return allVariations;
    }

}
