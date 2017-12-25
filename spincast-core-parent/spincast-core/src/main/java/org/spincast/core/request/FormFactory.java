package org.spincast.core.request;

import org.spincast.core.json.JsonObject;

public interface FormFactory {

    public Form createForm(String formName, JsonObject formData);

}
