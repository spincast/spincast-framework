package org.spincast.tests.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.json.JsonObject;
import org.spincast.core.validation.ValidationSet;

public class ValidationKeysScopingTest extends ValidationTestBase {

    @Test
    public void validationKeyNotParsedAsJsonPath() throws Exception {

        ValidationSet validationSet = getValidationFactory().createValidationSet();
        validationSet.addError("aaa.bbb[2].", "ERR_CODE", "some message");

        JsonObject validationSetAsJsonObject = validationSet.convertToJsonObject();
        assertTrue(validationSetAsJsonObject.isElementExistsNoKeyParsing("aaa.bbb[2]."));
    }

    @Test
    public void validationSetMergedNoPrefix() throws Exception {

        ValidationSet validationSet = getValidationFactory().createValidationSet();
        validationSet.addError("aaa.bbb[2].", "ERR_CODE", "some message");

        ValidationSet validationSet2 = getValidationFactory().createValidationSet();
        validationSet2.mergeValidationSet(validationSet);

        JsonObject validationSetAsJsonObject = validationSet2.convertToJsonObject();
        assertTrue(validationSetAsJsonObject.isElementExistsNoKeyParsing("aaa.bbb[2]."));
    }

    @Test
    public void validationSetMergedWithPrefix() throws Exception {

        ValidationSet validationSet = getValidationFactory().createValidationSet();
        validationSet.addError("aaa.bbb[2].", "ERR_CODE", "some message");

        ValidationSet validationSet2 = getValidationFactory().createValidationSet();
        validationSet2.mergeValidationSet("some.prefix-", validationSet);

        JsonObject validationSetAsJsonObject = validationSet2.convertToJsonObject();
        assertFalse(validationSetAsJsonObject.isElementExistsNoKeyParsing("aaa.bbb[2]."));
        assertTrue(validationSetAsJsonObject.isElementExistsNoKeyParsing("some.prefix-aaa.bbb[2]."));
    }

    @Test
    public void validationPrefixDirectlyAndWhenAdded() throws Exception {

        ValidationSet validationSet = getValidationFactory().createValidationSet();
        validationSet.addError("aaa.bbb[2].", "ERR_CODE", "some message");

        validationSet.prefixValidationKeys("some.prefix-");
        JsonObject validationSetAsJsonObject = validationSet.convertToJsonObject();
        assertFalse(validationSetAsJsonObject.isElementExistsNoKeyParsing("aaa.bbb[2]."));
        assertTrue(validationSetAsJsonObject.isElementExistsNoKeyParsing("some.prefix-aaa.bbb[2]."));

        ValidationSet validationSet2 = getValidationFactory().createValidationSet();
        validationSet2.mergeValidationSet("..[]..", validationSet);

        validationSetAsJsonObject = validationSet2.convertToJsonObject();
        assertFalse(validationSetAsJsonObject.isElementExistsNoKeyParsing("aaa.bbb[2]."));
        assertFalse(validationSetAsJsonObject.isElementExistsNoKeyParsing("some.prefix-aaa.bbb[2]."));
        assertTrue(validationSetAsJsonObject.isElementExistsNoKeyParsing("..[]..some.prefix-aaa.bbb[2]."));

    }

}
