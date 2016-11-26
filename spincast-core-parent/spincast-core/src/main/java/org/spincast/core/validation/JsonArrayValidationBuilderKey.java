package org.spincast.core.validation;

public interface JsonArrayValidationBuilderKey extends JsonObjectValidationBuilderKey {

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
    @Override
    public ValidationBuilderCore jsonPath(String jsonPath);

    /**
     * An <code>JsonArray</code> is expected at the
     * given <code>JsonPath</code> and each of its elements 
     * will be validated.
     * <p>
     * Each validation key will be the jsonPath
     * followed by "[X]", where "X" is the position of the validated
     * element in the array.
     */
    @Override
    public ValidationBuilderArray jsonPathAll(String jsonPathToArray);

    /**
     * Validates the element at the specified
     * index.
     */
    public ValidationBuilderCore index(int index);

    /**
     * An <code>JsonArray</code> is expected at the
     * given index and each of its elements 
     * will be validated.
     * <p>
     * Each validation key will be "[X]", where "X" is the position 
     * of the validated element in the array.
     */
    public ValidationBuilderArray indexAll(int index);

}
