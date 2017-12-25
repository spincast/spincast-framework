package org.spincast.core.validation;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.json.JsonManager;
import org.spincast.core.request.FormDefault;
import org.spincast.core.request.FormFactory;
import org.spincast.core.utils.ObjectConverter;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.core.xml.XmlManager;

import com.google.inject.assistedinject.AssistedInject;


public class ValidationSetSimple extends FormDefault {

    @AssistedInject
    public ValidationSetSimple(JsonManager jsonManager,
                               SpincastUtils spincastUtils,
                               ObjectConverter objectConverter,
                               SpincastDictionary spincastDictionary,
                               XmlManager xmlManager,
                               SpincastConfig spincastConfig,
                               Validators validators,
                               FormFactory formFactory,
                               ValidationFactory validationFactory) {
        super("",
              null,
              jsonManager,
              spincastUtils,
              objectConverter,
              spincastDictionary,
              xmlManager,
              spincastConfig,
              validators,
              formFactory,
              validationFactory);
    }

    @Override
    public String toString() {

        Map<String, List<ValidationMessage>> messages = getMessages();

        if (messages == null || messages.size() == 0) {
            return "[No messages]";
        }

        StringBuilder builder = new StringBuilder();
        for (Entry<String, List<ValidationMessage>> entry : messages.entrySet()) {
            builder.append(entry.getKey()).append(": ").append("\n");
            for (ValidationMessage message : entry.getValue()) {
                builder.append("\t- [").append(message.getValidationLevel()).append("] ")
                       .append(message.getCode()).append(" - ").append(message.getText()).append("\n");
            }
        }
        return builder.toString();
    }

}
