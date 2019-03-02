package org.spincast.plugins.watermarker;

import java.awt.Color;
import java.awt.Font;
import java.io.File;

public interface SpincastImageWatermarkerBuilder {

    /**
     * The text to use as the watermark. Calling this will
     * remove any image set using {@link #image(File)}.
     * <p>
     * By default, this text is the root URL of the
     * application (for example: "<em>https://www.example.com</em>").
     */
    public SpincastImageWatermarkerBuilder text(String text);

    public SpincastImageWatermarkerBuilder text(String text, Font font);

    public SpincastImageWatermarkerBuilder text(String text, Color color);

    public SpincastImageWatermarkerBuilder text(String text, Color color, Font font);

    public SpincastImageWatermarkerBuilder text(Color color);

    public SpincastImageWatermarkerBuilder text(Color color, Font font);

    public SpincastImageWatermarkerBuilder text(Font font);

    /**
     * The background color to use for the watermark. This
     * is only useful when using a <code>text</code> watermark.
     * <p>
     * Default to white.
     * <p>
     * You can set this to <code>null</code> to get a
     * transparent background!
     */
    public SpincastImageWatermarkerBuilder backgroundColor(Color color);

    /**
     * The image on the file system to use as the watermark. 
     * <p>
     * Calling this will remove any text set using {@link #text(String).
     * <p>
     * Empty by default.
     * 
     * @throws an exception if the specified file doesn't exist
     * or is not a valid image.
     */
    public SpincastImageWatermarkerBuilder image(String imageFilePath);

    /**
     * The image to use as the watermark. 
     * <p>
     * Calling this will remove any text set using {@link #text(String).
     * <p>
     * Empty by default.
     * 
     * @param onClasspath if <code>true</code>, the <code>imageFilePath</code>
     * will be considered as a classpath path. Otherwise, the image will
     * be taken from the file system.
     * 
     * @throws an exception if the specified file doesn't exist
     * or is not a valid image.
     */
    public SpincastImageWatermarkerBuilder image(String imageFilePath, boolean onClasspath);

    /**
     * The position where the watermark will be created
     * on the image. No margin.
     * <p>
     * Defaults to {@Link SpincastWatermarkPosition#BOTTOM_RIGHT}.
     */
    public SpincastImageWatermarkerBuilder position(SpincastWatermarkPosition position);

    /**
     * The position where the watermark will be created
     * on the image.
     * <p>
     * Defaults to {@Link SpincastWatermarkPosition#BOTTOM_RIGHT}.
     * 
     * @param The margin (in pixels) between the watermark and the edge of the
     * image.
     */
    public SpincastImageWatermarkerBuilder position(SpincastWatermarkPosition position, int margin);

    /**
     * The opacity of the watermark.
     * <p>
     * Between 0.0 and 1.0.
     * <p>
     * Defaults to 1.0, no transparency.
     */
    public SpincastImageWatermarkerBuilder opacity(float opacity);

    /**
     * The width of the watermark. Must be an 
     * integer between 1 and 100.
     * <p>
     * Defaults to 50.
     */
    public SpincastImageWatermarkerBuilder widthPercent(int percentageWidth);

    /**
     * The border to add around the watermark.
     * <p>
     * Set the <code>widthInPixels</code> to 0 to prevent any border
     * to be added.
     * <p>
     * Default to a 5 pixels black border.
     * 
     * @param color Can be <code>null</code>: will be black then.
     */
    public SpincastImageWatermarkerBuilder border(int widthInPixels, Color color);

    /**
     * Creates the actual {@link SpincastImageWatermarker}.
     */
    public SpincastImageWatermarker build();
}
