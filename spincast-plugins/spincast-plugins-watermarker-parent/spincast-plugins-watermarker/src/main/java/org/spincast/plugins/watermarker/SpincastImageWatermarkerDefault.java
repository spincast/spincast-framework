package org.spincast.plugins.watermarker;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.shaded.org.apache.commons.io.FilenameUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.geometry.Positions;

public class SpincastImageWatermarkerDefault implements SpincastImageWatermarker {

    private final String watermarkerId;
    private final String text;
    private final Color textColor;
    private final Color backgroundColor;
    private final Font textFont;
    private final String imageFilePath;
    private final boolean imageFileOnClasspath;
    private final Position position;
    private final Integer margin;
    private final float opacity;
    private final int percentageWidth;
    private final Integer borderWidth;
    private final Color borderColor;
    private final SpincastUtils spincastUtils;
    private final SpincastConfig spincastConfig;

    private BufferedImage watermarkOriginalImageBufferedImage;
    private File mainTempDir;
    private File watermarkImagesCacheDir;
    private Font defaultFont;

    @AssistedInject
    public SpincastImageWatermarkerDefault(@Assisted("text") @Nullable String text,
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
                                           @Assisted("borderColor") @Nullable Color borderColor,
                                           SpincastUtils spincastUtils,
                                           SpincastConfig spincastConfig) {
        if (text != null && imageFilePath != null) {
            throw new RuntimeException("Only the text OR the image can be specified, not both!");
        }
        this.text = text;
        this.imageFilePath = imageFilePath;
        this.imageFileOnClasspath = imageFileOnClasspath;

        this.watermarkerId = UUID.randomUUID().toString();

        this.textColor = textColor;
        this.textFont = textFont;
        if (percentageWidth == null) {
            percentageWidth = 50;
        }
        this.backgroundColor = backgroundColor;
        this.percentageWidth = percentageWidth;

        this.borderWidth = borderWidth;
        this.borderColor = borderColor;

        this.spincastUtils = spincastUtils;
        this.spincastConfig = spincastConfig;

        this.margin = margin;
        this.position = toThumbnailatorPosition(position, margin);

        if (opacity == null || opacity > 1) {
            opacity = 1F;
        } else if (opacity < 0) {
            opacity = 0F;
        }
        this.opacity = opacity;
    }

    protected String getWatermarkerId() {
        return this.watermarkerId;
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

    public Position getPosition() {
        return this.position;
    }

    public Integer getMargin() {
        return this.margin;
    }

    public float getOpacity() {
        return this.opacity;
    }

    public int getPercentageWidth() {
        return this.percentageWidth;
    }

    public Integer getBorderWidth() {
        return this.borderWidth;
    }

    public Color getBorderColor() {
        return this.borderColor;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }


    protected Position toThumbnailatorPosition(SpincastWatermarkPosition position, Integer margin) {

        Positions pos = Positions.valueOf(position.name());
        if (margin == null || margin <= 0) {
            return pos;
        }

        return new Position() {

            @Override
            public Point calculate(int enclosingWidth,
                                   int enclosingHeight,
                                   int width,
                                   int height,
                                   int insetLeft,
                                   int insetRight,
                                   int insetTop,
                                   int insetBottom) {

                Point point = pos.calculate(enclosingWidth,
                                            enclosingHeight,
                                            width,
                                            height,
                                            insetLeft,
                                            insetRight,
                                            insetTop,
                                            insetBottom);

                double x = point.getX();
                double y = point.getY();
                if (pos == Positions.TOP_LEFT || pos == Positions.TOP_CENTER || pos == Positions.TOP_RIGHT) {
                    y += margin;
                } else {
                    y -= margin;
                }

                if (pos == Positions.TOP_LEFT || pos == Positions.BOTTOM_LEFT) {
                    x += margin;
                } else if (pos == Positions.TOP_RIGHT || pos == Positions.BOTTOM_RIGHT) {
                    x -= margin;
                }

                point.setLocation(x, y);
                return point;
            }
        };
    }

    protected File getMainTempDir() {
        if (this.mainTempDir == null) {
            this.mainTempDir = new File(getSpincastConfig().getTempDir(), "spincastWatermarkerPlugin");
            if (!this.mainTempDir.isDirectory()) {
                boolean result = this.mainTempDir.mkdirs();
                if (!result) {
                    throw new RuntimeException("Unable to create the directory " + this.mainTempDir.getAbsolutePath());
                }
            }
        }
        return this.mainTempDir;
    }

    protected File getWatermarkImagesTempDir() {
        if (this.watermarkImagesCacheDir == null) {
            File dir = this.watermarkImagesCacheDir =
                    new File(getMainTempDir().getAbsolutePath() + "/" + getWatermarkerId() + "/watermarkImagesCache");
            if (!dir.isDirectory()) {
                boolean result = dir.mkdirs();
                if (!result) {
                    throw new RuntimeException("Unable to create the directory " + dir.getAbsolutePath());
                }
            }
        }
        return this.watermarkImagesCacheDir;
    }

    protected boolean isImageWatermark() {
        return getImageFilePath() != null;
    }

    protected Font getDefaultFont() {
        if (this.defaultFont == null) {
            Graphics g = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB).getGraphics();
            this.defaultFont = g.getFont();
            g.dispose();
        }
        return this.defaultFont;
    }

    protected BufferedImage createTextWatermarkBaseImage() {
        try {
            String text = getText();
            if (StringUtils.isBlank(text)) {
                text = getSpincastConfig().getPublicUrlBase();
            }

            Color fontColor = getTextColor();
            if (fontColor == null) {
                fontColor = Color.BLACK;
            }

            Color backgroundColor = getBackgroundColor();

            Font font = getTextFont();
            if (font == null) {
                font = getDefaultFont();
            }
            font = font.deriveFont(getFontSizeToCreateTextWatermarkBaseImage());

            //==========================================
            // Compute the width that is going to be required
            // to write the text using this font...
            //==========================================
            BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            FontMetrics fm = g.getFontMetrics(font);
            int textWidth = fm.stringWidth(text);
            g.dispose();

            BufferedImage bufferedImage =
                    new BufferedImage(textWidth + font.getSize() * 2, font.getSize() * 2, BufferedImage.TYPE_INT_ARGB);
            g = (Graphics2D)bufferedImage.getGraphics();

            //==========================================
            // Background color or transparent otherwise
            //==========================================
            if (backgroundColor != null) {
                g.setPaint(backgroundColor);
            } else {
                g.setComposite(AlphaComposite.Clear);
            }
            g.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

            g.setComposite(AlphaComposite.Src);
            g.setPaint(fontColor);
            g.setFont(font);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                               RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,
                               RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                               RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.drawString(text, font.getSize(), Math.round(font.getSize() * 1.3));
            g.dispose();

            bufferedImage = addBorder(bufferedImage);
            return bufferedImage;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected float getFontSizeToCreateTextWatermarkBaseImage() {
        return 100F;
    }

    protected BufferedImage getWatermarkOriginalImage() {
        if (this.watermarkOriginalImageBufferedImage == null) {
            try {

                if (isImageWatermark()) {
                    if (isImageFileOnClasspath()) {
                        InputStream is = getSpincastUtils().getClasspathInputStream(getImageFilePath());
                        if (is == null) {
                            throw new RuntimeException("Image not found on classpath: " + getImageFilePath());
                        }
                        try {
                            this.watermarkOriginalImageBufferedImage = ImageIO.read(is);
                        } finally {
                            SpincastStatics.closeQuietly(is);
                        }
                    } else {
                        this.watermarkOriginalImageBufferedImage = ImageIO.read(new File(getImageFilePath()));
                        if (this.watermarkOriginalImageBufferedImage == null) {
                            throw new RuntimeException("Image not found on the file system: " + getImageFilePath());
                        }
                    }
                } else {
                    this.watermarkOriginalImageBufferedImage = createTextWatermarkBaseImage();
                }
            } catch (Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }
        return this.watermarkOriginalImageBufferedImage;
    }

    protected BufferedImage getWatermarkImageForWidth(int width) {

        try {
            String imageName = "watermarkImg" + width + ".png";
            File watermarkImageFile = new File(getWatermarkImagesTempDir(), imageName);
            if (!watermarkImageFile.isFile()) {
                BufferedImage bufferedImage = Thumbnails.of(getWatermarkOriginalImage())
                                                        .width(width)
                                                        .imageType(BufferedImage.TYPE_INT_ARGB)
                                                        .outputFormat("png")
                                                        .asBufferedImage();
                bufferedImage = addBorder(bufferedImage);
                ImageIO.write(bufferedImage, "png", watermarkImageFile);
            }

            BufferedImage bufferedImageFinal = ImageIO.read(watermarkImageFile);
            return bufferedImageFinal;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected BufferedImage addBorder(BufferedImage watermarkImg) {
        if (getBorderWidth() == null || getBorderWidth() <= 0) {
            return watermarkImg;
        }

        Color borderColor = getBorderColor();
        if (borderColor == null) {
            borderColor = Color.BLACK;
        }

        BufferedImage imageWithBorder = new BufferedImage(watermarkImg.getWidth() + (2 * getBorderWidth()),
                                                          watermarkImg.getHeight() + (2 * getBorderWidth()),
                                                          watermarkImg.getType());
        Graphics2D g = (Graphics2D)imageWithBorder.getGraphics();

        g.setComposite(AlphaComposite.Src);
        g.setPaint(borderColor);
        g.fillRect(0, 0, imageWithBorder.getWidth(), imageWithBorder.getHeight());

        g.drawImage(watermarkImg, getBorderWidth(), getBorderWidth(), null);

        g.dispose();
        return imageWithBorder;
    }

    protected int getTargetWatermarkImageWidth(BufferedImage originalImage) {
        try {
            int originalImageWidth = originalImage.getWidth();

            int watermarkWidth = Math.round((getPercentageWidth() / 100F) * originalImageWidth);
            watermarkWidth = Math.min(watermarkWidth, getWatermarkOriginalImage().getWidth());

            //==========================================
            // Border to be added?
            //==========================================
            if (getBorderWidth() != null && getBorderWidth() > 0) {
                watermarkWidth = watermarkWidth - (2 * getBorderWidth());
            }

            //==========================================
            // Margin?
            //==========================================
            Integer margin = getMargin();
            if (margin != null && margin > 0) {
                if ((3 * margin) + watermarkWidth > originalImageWidth) {
                    watermarkWidth = originalImageWidth - (3 * margin);
                }
            }

            return watermarkWidth;

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public void watermark(String originalImagePath, File targetFile) {
        watermark(originalImagePath, false, targetFile);
    }

    @Override
    public void watermark(String originalImagePath, boolean onClasspath, File targetFile) {
        try {

            File fileOnFileSystem = new File(originalImagePath);
            if (!onClasspath && !fileOnFileSystem.isFile()) {
                throw new RuntimeException("Image not found on the file system: " + originalImagePath);
            }

            InputStream originalImageInputStream = onClasspath ? getSpincastUtils().getClasspathInputStream(originalImagePath)
                                                               : new FileInputStream(fileOnFileSystem);
            try {

                if (originalImageInputStream == null) {
                    throw new RuntimeException("Image not found: " + originalImagePath);
                }

                BufferedImage originalImageBufferedImage = ImageIO.read(originalImageInputStream);

                int watermarkWidth = getTargetWatermarkImageWidth(originalImageBufferedImage);
                BufferedImage watermarkImg = getWatermarkImageForWidth(watermarkWidth);

                String outputType = getOutputType(targetFile);

                //==========================================
                // "BufferedImage.TYPE_INT_ARGB" : For way
                // better thumb from gif quality...
                // @see https://github.com/coobird/thumbnailator/issues/65#issuecomment-124802293
                //
                // There is a bug with a target file without extension, so we
                // use an outputStream instead of a direct file:
                // https://github.com/coobird/thumbnailator/issues/66
                //==========================================
                FileOutputStream os = new FileOutputStream(targetFile);
                try {
                    Thumbnails.of(originalImageBufferedImage)
                              .imageType(BufferedImage.TYPE_INT_ARGB)
                              .watermark(getPosition(), watermarkImg, getOpacity())
                              .scale(1.0)
                              .outputFormat(outputType)
                              .toOutputStream(os);
                } finally {
                    SpincastStatics.closeQuietly(os);
                }
            } finally {
                SpincastStatics.closeQuietly(originalImageInputStream);
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected String getOutputType(File targetFile) {

        String extension = FilenameUtils.getExtension(targetFile.getName());
        if (!StringUtils.isBlank(extension)) {
            if (extension.equalsIgnoreCase("gif")) {
                return "gif";
            } else if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")) {
                return "jpg";
            }
        }
        return "png";
    }

}
