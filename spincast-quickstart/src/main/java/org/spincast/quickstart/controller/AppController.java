package org.spincast.quickstart.controller;

import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConstants.RequestScopedVariables;
import org.spincast.core.exceptions.NotFoundException;
import org.spincast.core.exceptions.PublicException;
import org.spincast.core.exceptions.PublicExceptionDefault;
import org.spincast.core.json.JsonObject;
import org.spincast.quickstart.config.AppConfig;
import org.spincast.quickstart.exchange.AppRequestContext;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;

/**
 * The application controller.
 */
public class AppController {

    protected final Logger logger = LoggerFactory.getLogger(AppController.class);

    private final AppConfig appConfig;

    @Inject
    public AppController(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

    /**
     * Adds some common elements to the response's model.
     */
    protected void addCommonModelElements(AppRequestContext context) {
        context.response().getModel().put("appName", getAppConfig().getAppName());
        context.response().getModel().put("serverPort", getAppConfig().getHttpServerPort());
    }

    /**
     * Index page handler
     */
    public void index(AppRequestContext context) {

        addCommonModelElements(context);
        context.response().sendTemplateHtml("/templates/index.html");
    }

    /**
     * Movie info handler
     */
    public void movieInfo(AppRequestContext context) {

        JsonObject movieInfo = null;

        //==========================================
        // Do we have a movie to show info for?
        //==========================================
        String movieName = context.request().getQueryStringParamFirst("name");
        if (!StringUtils.isBlank(movieName)) {

            try {

                // We get the movie info from www.omdbapi.com
                String url = "https://www.omdbapi.com/?t=" + URLEncoder.encode(movieName, "UTF-8") + "&y=&plot=short&r=json";

                movieInfo = context.httpClient().GET(url).send().getContentAsJsonObject();

                // There is no 404 returned. To know if a movie was found,
                // the "Response" element must be true.
                boolean responseOk = movieInfo.getBoolean("Response", false);
                if (!responseOk) {
                    throw new NotFoundException("This movie was not found!");
                }

                // Default image if none is provided
                String poster = movieInfo.getString("Poster", null);
                if (StringUtils.isBlank(poster) || (!poster.startsWith("http://") && !poster.startsWith("https://"))) {
                    movieInfo.put("Poster", "/public/images/noPoster.jpg");
                }

                // Rating may be "N/A"
                if (!movieInfo.isCanBeConvertedToDouble("imdbRating")) {
                    movieInfo.put("imdbRating", "");
                }

            } catch (NotFoundException ex) {
                throw ex;
            } catch (Exception ex) {
                this.logger.error(ex.getMessage());
                throw new PublicExceptionDefault("Unable to get the movie information... " +
                                                 "Maybe omdbapi.com's is down or you don't have Internet connection?");
            }
        }

        context.response().getModel().put("movieInfo", movieInfo);
        context.response().getModel().put("movieName", movieName);
        addCommonModelElements(context);

        context.response().sendTemplateHtml("/templates/movie.html");
    }

    /**
     * "Exception example" handler
     */
    public void exceptionExample(AppRequestContext context) {

        throw new RuntimeException("This simulates an exception in the application!");
    }

    /**
     * Route Handler to manage 404
     */
    public void notFound(AppRequestContext context) {

        String notFoundMessage = "Page not found";

        //==========================================
        // Is there a custom "Not Found" message
        // to display?
        //==========================================
        String specificNotFoundMessage =
                context.variables().getAsString(RequestScopedVariables.NOT_FOUND_PUBLIC_MESSAGE);

        if (specificNotFoundMessage != null) {
            notFoundMessage = specificNotFoundMessage;
        }

        //==========================================
        // We return the response in the
        // appropriated format.
        //==========================================
        if (context.request().isJsonShouldBeReturn()) {
            JsonObject errorObj = context.json().create();
            errorObj.put("message", notFoundMessage);
            context.response().sendJson(errorObj);
        } else {
            context.response().getModel().put("notFoundMessage", notFoundMessage);
            addCommonModelElements(context);
            context.response().sendTemplateHtml("/templates/notFound.html");
        }
    }

    /**
     * Route Handler to manage exceptions
     */
    public void exception(AppRequestContext context) {

        String errorMessage = "An error occured! Please try again later...";

        //==========================================
        // We have access to the exception that was threw :
        //==========================================
        Throwable originalException =
                context.variables().get(RequestScopedVariables.EXCEPTION, Throwable.class);

        this.logger.error("An exception occured : " + originalException);

        //==========================================
        // If the exception that was threw is an instance of 
        // PublicException, it means we should display its
        // message to the user.
        //==========================================
        if (originalException != null && originalException instanceof PublicException) {
            errorMessage = originalException.getMessage();
        }

        //==========================================
        // We return the response in the
        // appropriated format.
        //==========================================
        if (context.request().isJsonShouldBeReturn()) {
            JsonObject errorObj = context.json().create();
            errorObj.put("error", errorMessage);
            context.response().sendJson(errorObj);
        } else {
            context.response().getModel().put("errorMessage", errorMessage);
            addCommonModelElements(context);
            context.response().sendTemplateHtml("/templates/exception.html");
        }
    }
}
