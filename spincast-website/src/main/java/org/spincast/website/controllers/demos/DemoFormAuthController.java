package org.spincast.website.controllers.demos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Form authentication/Session demo controller
 */
public class DemoFormAuthController {

    protected final Logger logger = LoggerFactory.getLogger(DemoFormAuthController.class);

    /**
     * Index
     */
    /*
    public void index(AppRequestContext context) {
        sendTemplate(context, null);
    }
    */

    /**
     * Login
     */
    /*
    public void login(AppRequestContext context) {
    
        String email = context.request().getFormDataFirst("email");
        String password = context.request().getFormDataFirst("password");
    
        Map<String, Object> params = new HashMap<String, Object>();
    
        if(StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
        }
    
        sendTemplate(context, params);
    }
    */

    /**
     * Register
     */
    /*
    public void register(AppRequestContext context) {
        sendTemplate(context, null);
    }
    
    protected void sendTemplate(AppRequestContext context, Map<String, Object> params) {
        context.response().sendTemplateHtml("/templates/demos/formAuth.html", params);
    }
    */

}
