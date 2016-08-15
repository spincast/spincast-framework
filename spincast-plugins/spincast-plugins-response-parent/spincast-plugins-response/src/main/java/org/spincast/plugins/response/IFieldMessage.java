package org.spincast.plugins.response;

public interface IFieldMessage {

    public FieldMessageType getMessageType();

    public String getFieldName();

    public String getValue();

    public Integer getValuePosition();

    public String getMessage();
}
