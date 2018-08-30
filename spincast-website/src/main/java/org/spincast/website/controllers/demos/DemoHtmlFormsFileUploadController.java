package org.spincast.website.controllers.demos;

import java.io.File;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.flash.FlashMessage;
import org.spincast.core.flash.FlashMessageFactory;
import org.spincast.core.flash.FlashMessageLevel;
import org.spincast.core.json.JsonObject;
import org.spincast.core.request.Form;
import org.spincast.core.server.UploadedFile;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.codec.binary.Base64;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.website.exchange.AppRequestContext;

import com.google.inject.Inject;

/**
 * HTML Forms - File upload demo controller
 */
public class DemoHtmlFormsFileUploadController {

    protected final Logger logger = LoggerFactory.getLogger(DemoHtmlFormsFileUploadController.class);

    private final FlashMessageFactory flashMessageFactory;

    @Inject
    public DemoHtmlFormsFileUploadController(FlashMessageFactory flashMessageFactory) {
        this.flashMessageFactory = flashMessageFactory;
    }

    protected FlashMessageFactory getFlashMessageFactory() {
        return this.flashMessageFactory;
    }

    /**
     * File upload - GET
     */
    public void fileUpload(AppRequestContext context) {

        //==========================================
        // We validate if a Flash Message is present and indicates
        // that an image has been uploaded and then the page
        // redirected. If it's the case, we get the base64 encoded 
        // the image from the Flash Message and add it to the 
        // response model so it can be displayed!
        //==========================================
        FlashMessage flashMessage = context.request().getFlashMessage();
        if (flashMessage != null) {

            String uploadFileBase64ImageSrc = flashMessage.getVariables().getString("uploadFileBase64ImageSrc");
            if (uploadFileBase64ImageSrc == null) {
                this.logger.error("The uploadFileBase64ImageSrc was expected...");
            } else {
                context.response().getModel().set("uploadFileBase64ImageSrc", uploadFileBase64ImageSrc);
            }
        }

        sendTemplate(context);
    }

    protected void sendTemplate(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/htmlForms/fileUpload.html");
    }

    /**
     * File upload - POST
     */
    public void fileUploadSubmit(AppRequestContext context) {

        //==========================================
        // Gets the form data as a Form object,
        // and adds it back to the response's model.
        // The validation message will be added to the
        // default "validation" element of the model.
        //==========================================
        Form form = context.request().getFormWithRootKey("demoForm");
        context.response().addForm(form);

        //==========================================
        // Gets the uploaded file
        //==========================================
        UploadedFile uploadedFile = context.request().getUploadedFileFirst("demoForm.fileToUpload");
        if (uploadedFile == null) {
            form.addError("fileToUpload", "file_missing", "Please select a file to upload.");
        }

        File file = uploadedFile.getFile();

        long length = file.length();
        if (length == 0) {
            form.addError("fileToUpload", "file_empty", "The file is empty.");
        } else if ((length / 1024) > 200) {
            form.addError("fileToUpload", "file_too_big", "The file must be 200KB or less. " +
                                                          "It was " + (length / 1024) + "KB.");
        }

        //==========================================
        // We validate that the uploaded file is a
        // valide image.
        //==========================================
        if (form.isValid("fileToUpload")) {
            try {
                ImageIO.read(file).toString();
            } catch (Exception e) {
                form.addError("fileToUpload",
                              "fileToUpload_notValidImage",
                              "The file must be a valid image of type PNG, JPEG, GIF or BMP.");
            }
        }

        //==========================================
        // If the validation is not successful, we
        // redisplay the form.
        //==========================================
        if (!form.isValid()) {
            fileUpload(context);

        //==========================================@formatter:off 
        // The uploaded file is valid!
        //
        // We base64 encode the image and add it to
        // a Flash Message. We then redirect to a
        // confirmation page where the image will be
        // retrieved from the Flash Message and displayed
        // to the user...
        //==========================================@formatter:on 
        } else {

            ImageInputStream iis = null;
            JsonObject variables = context.json().create();
            try {

                iis = ImageIO.createImageInputStream(file);
                Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
                if (iter.hasNext()) {
                    String base64Str = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));
                    String formatName = iter.next().getFormatName();
                    String uploadFileBase64ImageSrc = "data:image/" + formatName + ";charset=utf-8;base64," + base64Str;
                    variables.set("uploadFileBase64ImageSrc", uploadFileBase64ImageSrc);
                }

            } catch (Exception ex) {
                this.logger.error("Error converting the image to base 64.", ex);
            } finally {
                SpincastStatics.closeQuietly(iis);
            }

            context.response().redirect(getFlashMessageFactory().create(FlashMessageLevel.SUCCESS,
                                                                        "The file was uploaded successfully!",
                                                                        variables));
        }
    }
}
