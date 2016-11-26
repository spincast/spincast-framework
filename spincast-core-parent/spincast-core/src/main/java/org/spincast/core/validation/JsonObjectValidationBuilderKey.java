package org.spincast.core.validation;

public interface JsonObjectValidationBuilderKey extends ValidationBuilderKey {

    /**
     * The key for the validation. This key won't be parsed as
     * a <code>JsonPath</code> and required you to pass the
     * element(s) to validate right after.
     */
    @Override
    public ValidationBuilderTarget key(String validationKey);

    /**
     * Validates the element at the specified
     * <code>JsonPath</code>.
     */
    public ValidationBuilderCore jsonPath(String jsonPath);

    /**
     * An <code>JsonArray</code> is expected at the
     * given <code>JsonPath</code> and each of its elements 
     * will be validated.
     * <p>
     * Each validation key will be the <code>JsonPath</code>
     * followed by "[X]", where "X" is the position of the validated
     * element in the array.
     */
    public ValidationBuilderArray jsonPathAll(String jsonPathToArray);

    /**
     * An <code>JsonArray</code> is expected at the
     * given <code>JsonPath</code> and each of its elements 
     * will be validated.
     * <p>
     * Each validation key will be the specified <code>validationKey</code>
     * followed by "[X]", where "X" is the position of the validated
     * element in the array.
     * 
     * @param validationKey the validation key to use instead of the 
     * <code>JsonPath</code>.
     */
    public ValidationBuilderArray jsonPathAll(String jsonPathToArray, String validationKey);

}
