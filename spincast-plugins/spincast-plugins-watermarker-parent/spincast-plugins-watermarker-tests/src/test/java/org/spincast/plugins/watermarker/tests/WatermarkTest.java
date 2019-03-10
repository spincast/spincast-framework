package org.spincast.plugins.watermarker.tests;

import java.awt.Color;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.watermarker.SpincastImageWatermarker;
import org.spincast.plugins.watermarker.SpincastWatermarkPosition;
import org.spincast.plugins.watermarker.SpincastWatermarkerFactory;
import org.spincast.plugins.watermarker.SpincastWatermarkerPlugin;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;

/**
 * Testing images is not that easy...
 * 
 * At least, we test that the code works without exception.
 * We can also look at the generated images manually.
 */
public class WatermarkTest extends NoAppTestingBase {

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastWatermarkerPlugin());
        return extraPlugins;
    }

    @Inject
    protected SpincastWatermarkerFactory spincastWatermarkerFactory;

    @Inject
    protected SpincastUtils spincastUtils;

    protected SpincastWatermarkerFactory getSpincastWatermarkerFactory() {
        return this.spincastWatermarkerFactory;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    @Test
    public void defaultTextFromClasspathImage() {

        SpincastImageWatermarker watermarker = getSpincastWatermarkerFactory().imageWatermarkerBuilder()
                                                                              .build();

        File watermarkedImage = new File(createTestingFilePath(UUID.randomUUID().toString() + ".png"));
        watermarker.watermark("/gardien-phare.jpg", true, watermarkedImage);

        System.out.println("Watermaked image: " + watermarkedImage.getAbsolutePath());
        return;
    }

    @Test
    public void defaultTextFromFileSystemImage() {

        try {
            File originalImage = new File(createTestingFilePath(UUID.randomUUID().toString() + ".png"));

            InputStream in = getSpincastUtils().getClasspathInputStream("/gardien-phare.jpg");
            FileUtils.copyInputStreamToFile(in, originalImage);

            SpincastImageWatermarker watermarker = getSpincastWatermarkerFactory().imageWatermarkerBuilder()
                                                                                  .backgroundColor(Color.PINK)
                                                                                  .build();

            File watermarkedImage = new File(createTestingFilePath(UUID.randomUUID().toString() + ".png"));
            watermarker.watermark(originalImage.getAbsolutePath(), watermarkedImage);

            System.out.println("Watermaked image: " + watermarkedImage.getAbsolutePath());
            return;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Test
    public void customTextAndSettings() {

        SpincastImageWatermarker watermarker = getSpincastWatermarkerFactory().imageWatermarkerBuilder()
                                                                              .text("Hello my friend!", Color.ORANGE)
                                                                              .backgroundColor(Color.GREEN)
                                                                              .border(10, Color.RED)
                                                                              .opacity(0.5F)
                                                                              .position(SpincastWatermarkPosition.TOP_CENTER, 20)
                                                                              .build();

        File watermarkedImage = new File(createTestingFilePath(UUID.randomUUID().toString() + ".png"));
        watermarker.watermark("/gardien-phare.jpg", true, watermarkedImage);

        System.out.println("Watermaked image: " + watermarkedImage.getAbsolutePath());
        return;
    }

    @Test
    public void outputGif() {

        SpincastImageWatermarker watermarker = getSpincastWatermarkerFactory().imageWatermarkerBuilder()
                                                                              .build();

        File watermarkedImage = new File(createTestingFilePath(UUID.randomUUID().toString() + ".gif"));
        watermarker.watermark("/gardien-phare.jpg", true, watermarkedImage);

        System.out.println("Watermaked image: " + watermarkedImage.getAbsolutePath());
        return;
    }

    @Test
    public void outputJpg() {

        SpincastImageWatermarker watermarker = getSpincastWatermarkerFactory().imageWatermarkerBuilder()
                                                                              .build();

        File watermarkedImage = new File(createTestingFilePath(UUID.randomUUID().toString() + ".jpg"));
        watermarker.watermark("/gardien-phare.jpg", true, watermarkedImage);

        System.out.println("Watermaked image: " + watermarkedImage.getAbsolutePath());
        return;
    }

    @Test
    public void outputJpeg() {

        SpincastImageWatermarker watermarker = getSpincastWatermarkerFactory().imageWatermarkerBuilder()
                                                                              .build();

        File watermarkedImage = new File(createTestingFilePath(UUID.randomUUID().toString() + ".jpeg"));
        watermarker.watermark("/gardien-phare.jpg", true, watermarkedImage);

        System.out.println("Watermaked image: " + watermarkedImage.getAbsolutePath());
        return;
    }

    @Test
    public void usingImageFromClasspath() {

        SpincastImageWatermarker watermarker = getSpincastWatermarkerFactory().imageWatermarkerBuilder()
                                                                              .image("/wtf.png", true)
                                                                              .build();

        File watermarkedImage = new File(createTestingFilePath(UUID.randomUUID().toString() + ".png"));
        watermarker.watermark("/gardien-phare.jpg", true, watermarkedImage);

        System.out.println("Watermaked image: " + watermarkedImage.getAbsolutePath());
        return;
    }

    @Test
    public void usingImageFromFileSystemAndCustomSettings() {

        try {
            File watermarkImage = new File(createTestingFilePath(UUID.randomUUID().toString() + ".png"));

            InputStream in = getSpincastUtils().getClasspathInputStream("/wtf.png");
            FileUtils.copyInputStreamToFile(in, watermarkImage);

            SpincastImageWatermarker watermarker = getSpincastWatermarkerFactory().imageWatermarkerBuilder()
                                                                                  .image(watermarkImage.getAbsolutePath())
                                                                                  .backgroundColor(Color.GREEN)
                                                                                  .border(10, Color.RED)
                                                                                  .opacity(0.5F)
                                                                                  .position(SpincastWatermarkPosition.TOP_CENTER,
                                                                                            20)
                                                                                  .build();

            File watermarkedImage = new File(createTestingFilePath(UUID.randomUUID().toString() + ".png"));
            watermarker.watermark("/gardien-phare.jpg", true, watermarkedImage);

            System.out.println("Watermaked image: " + watermarkedImage.getAbsolutePath());
            return;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

}
