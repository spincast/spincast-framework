package org.spincast.plugins.response;

public class FieldMessage implements IFieldMessage {

    private final FieldMessageType messageType;
    private final String fieldName;
    private final String value;
    private final Integer valuePosition;
    private final String error;

    /**
     * Constructor
     */
    public FieldMessage(FieldMessageType messageType,
                        String fieldName,
                        String value,
                        Integer valuePosition,
                        String error) {
        this.messageType = messageType;
        this.fieldName = fieldName;
        this.value = value;
        this.valuePosition = valuePosition;
        this.error = error;
    }

    @Override
    public FieldMessageType getMessageType() {
        return this.messageType;
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public Integer getValuePosition() {
        return this.valuePosition;
    }

    @Override
    public String getMessage() {
        return this.error;
    }

}
