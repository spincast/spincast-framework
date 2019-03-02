package org.spincast.plugins.watermarker;

import java.awt.Color;
import java.awt.Font;

import javax.annotation.Nullable;

import com.google.inject.assistedinject.Assisted;

public interface SpincastWatermarkerFactory {

    /**
     * Starts the creation of a {@link SpincastImageWatermarker}.
     */
    public SpincastImageWatermarkerBuilder imageWatermarkerBuilder();

    /**
     * Creates a {@link SpincastImageWatermarker} directly. 
     * <p>
     * Note that you can use {@link #imageWatermarkerBuilder()} for
     * an easier way to build it.
     */
    public SpincastImageWatermarker createImageWatermarker(@Assisted("text") @Nullable String text,
                                                           @Assisted("textColor") @Nullable Color textColor,
                                                           @Assisted("backgroundColor") @Nullable Color backgroundColor,
                                                           @Assisted("textFont") @Nullable Font textFont,
                                                           @Assisted("imageFilePath") @Nullable String imageFilePath,
                                                           @Assisted("imageFileOnClasspath") boolean imageFileOnClasspath,
                                                           @Assisted("position") @Nullable SpincastWatermarkPosition position,
                                                           @Assisted("margin") @Nullable Integer margin,
                                                           @Assisted("opacity") @Nullable Float opacity,
                                                           @Assisted("percentageWidth") @Nullable Integer percentageWidth,
                                                           @Assisted("borderWidth") @Nullable Integer borderWidth,
                                                           @Assisted("borderColor") @Nullable Color borderColor);


}
