package org.spincast.website.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.json.IJsonObject;
import org.spincast.website.exchange.IAppRequestContext;

/**
 * Form validation demo controller
 */
public class DemoFormValidationController {

    protected final Logger logger = LoggerFactory.getLogger(DemoFormValidationController.class);

    /**
     * Index
     */
    public void index(IAppRequestContext context) {
        sendTemplate(context);
    }

    /**
     * Submit
     */
    public void submit(IAppRequestContext context) {

        IJsonObject formDatas = context.request().getFormDatas();
        System.out.println(formDatas);

        //List<String> cars = context.request().getFormData("car");

        /*
        String email = context.request().getFormDataFirst("email");
        if(StringUtils.isBlank(email)) {
            context.response().addErrorField("email", "The email can't be empty");
        }
        email = email.trim();
        if(!EmailValidator.getInstance().isValid(email)) {
            context.response().addErrorField("email", "The email is not valid");
        }
        
        List<String> tags = context.request().getFormData("tag");
        tag : do {
            if(tags.size() < 3) {
                context.response().addErrorField("tag", "Please enter the three tags");
                break tag;
            }
            for(String tag : tags) {
                if(StringUtils.isBlank(tag)) {
                    context.response().addErrorField("tag", "Please enter the three tags");
                    break tag;
                }
            }
        
            if("Nope".equalsIgnoreCase(tags.get(0))) {
                context.response().addErrorField("tag", 0, "The first tag can't be 'nope'!");
                break tag;
            }
        } while(false);
        
        String password = context.request().getFormDataFirst("password");
        validatePassword(context, password);
        
        String passwordAgain = context.request().getFormDataFirst("passwordAgain");
        
        if(password != null && !password.equals(passwordAgain)) {
            context.response().addErrorField("passwordAgain", "The passwords must match");
        }
        
        String color = context.request().getFormDataFirst("color");
        if(color == null) {
            context.response().addErrorField("color", "The favorite color is required");
        } else if("green".equals(color)) {
            context.response().addErrorField("color", "\"green\" is not valid in this demo!");
        }
        
        List<String> foods = context.request().getFormData("food");
        if(foods.size() == 0) {
            context.response().addErrorField("food", "The favorite food is required");
        } else if(foods.size() == 1 && "italian".equals(foods.get(0))) {
            context.response().addErrorField("food", "\"italian\" can't be selected alone!");
        }
        
        String info = context.request().getFormDataFirst("info");
        if(info != null && info.length() > 512) {
            context.response().addErrorField("info", "Maximum 512 characters. Currently : " + info.length() + " characters.");
        }
        
        sendTemplate(context);
        */
    }

    protected void validatePassword(IAppRequestContext context, String password) {

        /*
        // Not empty
        if(StringUtils.isBlank(password)) {
            context.response().addErrorField("password", "Can't be empty");
            return;
        }
        
        // No space or tabs
        if(password.contains(" ") || password.contains("\t")) {
            context.response().addErrorField("password", "The password can't contain space or tab characters");
        }
        
        if(password.length() < 5) {
            context.response().addErrorField("password", "The password must contain at least 5 characters");
        }
        */
    }

    protected void sendTemplate(IAppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/formValidation.html");
    }

}
