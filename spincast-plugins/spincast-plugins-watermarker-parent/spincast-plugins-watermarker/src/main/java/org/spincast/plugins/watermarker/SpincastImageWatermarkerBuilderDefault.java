package org.spincast.plugins.watermarker;

import java.awt.Color;
import java.awt.Font;

import com.google.inject.Inject;

public class SpincastImageWatermarkerBuilderDefault implements SpincastImageWatermarkerBuilder {

    private final SpincastWatermarkerFactory spincastWatermarkerFactory;

    private String text;
    private Color textColor = Color.BLACK;
    private Color backgroundColor = Color.WHITE;
    private Font textFont = null;
    private String imageFilePath;
    private boolean imageFileOnClasspath = false;
    private SpincastWatermarkPosition position = SpincastWatermarkPosition.BOTTOM_RIGHT;
    private int margin = 10;
    private float opacity = 1F;
    private int percentageWidth = 50;
    private int borderWidth = 2;
    private Color borderColor = Color.BLACK;

    @Inject
    public SpincastImageWatermarkerBuilderDefault(SpincastWatermarkerFactory spincastWatermarkerFactory) {
        this.spincastWatermarkerFactory = spincastWatermarkerFactory;
    }

    protected SpincastWatermarkerFactory getSpincastWatermarkerFactory() {
        return this.spincastWatermarkerFactory;
    }

    public String getText() {
        return this.text;
    }

    public Color getTextColor() {
        return this.textColor;
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public Font getTextFont() {
        return this.textFont;
    }

    public String getImageFilePath() {
        return this.imageFilePath;
    }

    public boolean isImageFileOnClasspath() {
        return this.imageFileOnClasspath;
    }

    public SpincastWatermarkPosition getPosition() {
        return this.position;
    }

    public int getMargin() {
        return this.margin;
    }

    public float getOpacity() {
        return this.opacity;
    }

    public int getPercentageWidth() {
        return this.percentageWidth;
    }

    public int getBorderWidth() {
        return this.borderWidth;
    }

    public Color getBorderColor() {
        return this.borderColor;
    }

    @Override
    public SpincastImageWatermarkerBuilder text(String text) {
        this.text = text;
        this.imageFilePath = null;
        this.imageFileOnClasspath = false;
        return this;
    }

    @Override
    public SpincastImageWatermarkerBuilder text(String text, Font font) {
        text(text);
        this.textFont = font;
        return this;
    }

    @Override
    public SpincastImageWatermarkerBuilder text(String text, Color color) {
        text(text);
        this.textColor = color;
        return this;
    }

    @Override
    public SpincastImageWatermarkerBuilder text(String text, Color color, Font font) {
        text(text, color);
        this.textFont = font;
        return this;
    }

    @Override
    public SpincastImageWatermarkerBuilder text(Color color) {
        text((String)null);
        this.textColor = color;
        return this;
    }

    @Override
    public SpincastImageWatermarkerBuilder text(Color color, Font font) {
        text(color);
        this.textFont = font;
        return this;
    }

    @Override
    public SpincastImageWatermarkerBuilder text(Font font) {
        text((String)null);
        this.textFont = font;
        return this;
    }

    @Override
    public SpincastImageWatermarkerBuilder backgroundColor(Color color) {
        this.backgroundColor = color;
        return this;
    }

    @Override
    public SpincastImageWatermarkerBuilder image(String imageFilePath) {
        this.text = null;
        this.imageFilePath = imageFilePath;
        this.imageFileOnClasspath = false;
        return this;
    }

    @Override
    public SpincastImageWatermarkerBuilder image(String imageFilePath, boolean onClasspath) {
        image(imageFilePath);
        this.imageFileOnClasspath = onClasspath;
        return this;
    }

    @Override
    public SpincastImageWatermarkerBuilder position(SpincastWatermarkPosition position) {
        this.position = position != null ? position : SpincastWatermarkPosition.BOTTOM_RIGHT;
        return this;
    }

    @Override
    public SpincastImageWatermarkerBuilder position(SpincastWatermarkPosition position, int margin) {
        position(position);
        this.margin = margin;
        return this;
    }

    @Override
    public SpincastImageWatermarkerBuilder opacity(float opacity) {
        this.opacity = opacity;
        return this;
    }

    @Override
    public SpincastImageWatermarkerBuilder widthPercent(int percentageWidth) {
        if (percentageWidth < 1 || percentageWidth > 100) {
            throw new RuntimeException("Invalid width '" + percentageWidth + "'. Must be between 1 and 100.");
        }

        this.percentageWidth = percentageWidth;
        return this;
    }

    @Override
    public SpincastImageWatermarkerBuilder border(int width, Color color) {
        if (color == null) {
            color = Color.BLACK;
        }
        if (width < 0) {
            width = 0;
        }
        this.borderWidth = width;
        this.borderColor = color;
        return this;
    }

    @Override
    public SpincastImageWatermarker build() {
        SpincastImageWatermarker watermarker = getSpincastWatermarkerFactory().createImageWatermarker(getText(),
                                                                                                      getTextColor(),
                                                                                                      getBackgroundColor(),
                                                                                                      getTextFont(),
                                                                                                      getImageFilePath(),
                                                                                                      isImageFileOnClasspath(),
                                                                                                      getPosition(),
                                                                                                      getMargin(),
                                                                                                      getOpacity(),
                                                                                                      getPercentageWidth(),
                                                                                                      getBorderWidth(),
                                                                                                      getBorderColor());
        return watermarker;
    }


}
