package org.spincast.tests.validation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.json.JsonObject;
import org.spincast.core.validation.JsonObjectValidationSet;
import org.spincast.core.validation.ValidationSet;

public class SharingValidatorBetweenEntityAndJsonObjectTest extends ValidationTestBase {

    private UserValidator userValidator;

    /**
     * A typed Model/Entity.
     */
    public static class User {

        private String name;
        private Integer age;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return this.age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public static User fromJsonObject(JsonObject jsonObj) {

            if(jsonObj == null) {
                return null;
            }

            User user = new User();
            user.setName(jsonObj.getString("name"));
            user.setAge(jsonObj.getInteger("age"));

            return user;
        }
    }

    /**
     * User validator.
     */
    public class UserValidator {

        /**
         * Validates from a User object directly.
         */
        public ValidationSet validate(User user) {
            return validate(user.getName(), user.getAge());
        }

        /**
         * Validates from User informations.
         */
        public ValidationSet validate(String name,
                                       Integer age) {

            ValidationSet validation = getValidationFactory().createValidationSet();

            // Not null
            ValidationSet lastResult = validation.validationNotNull().key("name").element(name).validate();
            if(!lastResult.isValid()) {
                return validation;
            }

            // No "a"
            lastResult = validation.validationNotPattern(".*a.*").key("name").element(name).validate();

            // If the name is "Marvin", then the age can't be 42.
            if("Marvin".equalsIgnoreCase(name)) {
                lastResult = validation.validationNotEquivalent(42).key("age").element(age).validate();
            }

            return validation;
        }
    }

    protected UserValidator getUserValidator() {
        if(this.userValidator == null) {
            this.userValidator = new UserValidator();
        }
        return this.userValidator;
    }

    @Test
    public void validateEntity() throws Exception {

        User user = new User();
        user.setName("Stromgol");
        user.setAge(42);

        ValidationSet validation = getUserValidator().validate(user);
        assertEquals(0, validation.getMessages().size());

        user = new User();
        user.setName("Marvin");
        user.setAge(42);

        validation = getUserValidator().validate(user);
        assertEquals(2, validation.getMessages().size());
        assertEquals(1, validation.getMessages("name").size());
        assertEquals(1, validation.getMessages("age").size());
    }

    /**
     * Method #1 : 
     * 
     * Uses a "validate(...)" method overload method
     * of the validator that takes individual properties to validate.
     * 
     */
    @Test
    public void validateJsonObjectUsingIndividualProperties() throws Exception {

        JsonObject user = getJsonManager().create();
        user.put("name", "Stromgol");
        user.put("age", 42);

        JsonObjectValidationSet jsonObjValidation = user.validationSet();

        ValidationSet validation = getUserValidator().validate(user.getString("name"),
                                                                user.getInteger("age"));
        jsonObjValidation.mergeValidationSet(validation);

        assertEquals(0, jsonObjValidation.getMessages().size());

        user = getJsonManager().create();
        user.put("name", "Marvin");
        user.put("age", 42);

        validation = getUserValidator().validate(user.getString("name"), user.getInteger("age"));
        jsonObjValidation.mergeValidationSet(validation);

        assertEquals(2, jsonObjValidation.getMessages().size());
        assertEquals(1, jsonObjValidation.getMessages("name").size());
        assertEquals(1, jsonObjValidation.getMessages("age").size());
    }

    /**
     * Method #2 : 
     * 
     * Creates an Entity instance from the JsonObject and validate
     * this instance.
     * 
     */
    @Test
    public void validateJsonObjectByTransformingToEntity() throws Exception {

        JsonObject user = getJsonManager().create();
        user.put("name", "Stromgol");
        user.put("age", 42);

        User userInstance = User.fromJsonObject(user);

        ValidationSet validation = getUserValidator().validate(userInstance);
        assertEquals(0, validation.getMessages().size());

        user = getJsonManager().create();
        user.put("name", "Marvin");
        user.put("age", 42);

        userInstance = User.fromJsonObject(user);

        validation = getUserValidator().validate(userInstance);
        assertEquals(2, validation.getMessages().size());
        assertEquals(1, validation.getMessages("name").size());
        assertEquals(1, validation.getMessages("age").size());
    }

}
