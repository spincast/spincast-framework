package org.spincast.demos.sum;

import org.spincast.core.exceptions.PublicExceptionDefault;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.json.JsonObject;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;

public class AppControllerDefault implements AppController {

    /**
     * Sum Route Handler
     */
    @Override
    public void sumRoute(DefaultRequestContext context) {

        String firstNbr = context.request().getFormData().getString("first");
        if (StringUtils.isBlank(firstNbr)) {
            throw new PublicExceptionDefault("The 'first' post parameter is required.",
                                             HttpStatus.SC_BAD_REQUEST);
        }
        String secondNbr = context.request().getFormData().getString("second");
        if (StringUtils.isBlank(secondNbr)) {
            throw new PublicExceptionDefault("The 'second' post parameter is required.",
                                             HttpStatus.SC_BAD_REQUEST);
        }

        String error = null;
        long sum = 0;
        do {
            int firstInt;
            try {
                firstInt = Integer.parseInt(firstNbr);
            } catch (NumberFormatException ex) {
                error = ex.getMessage();
                break;
            }

            int secondInt;
            try {
                secondInt = Integer.parseInt(secondNbr);
            } catch (NumberFormatException ex) {
                error = ex.getMessage();
                break;
            }

            sum = (long)firstInt + (long)secondInt;

            if (sum > Integer.MAX_VALUE) {
                error = "The sum overflows the maximum integer value, " + Integer.MAX_VALUE;
                break;
            }
            if (sum < Integer.MIN_VALUE) {
                error = "The sum overflows the minimum integer value, " + Integer.MIN_VALUE;
                break;
            }

        } while (false);

        JsonObject resultObj = context.json().create();
        if (error != null) {
            context.response().setStatusCode(HttpStatus.SC_BAD_REQUEST);
            resultObj.put("error", error);
        } else {
            resultObj.put("result", String.valueOf(sum));
        }

        context.response().sendJson(resultObj);
    }
}
