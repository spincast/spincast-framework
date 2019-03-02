package org.spincast.plugins.watermarker;

import java.io.File;

public interface SpincastImageWatermarker {

    /**
     * Watermark an image.
     * <p>
     * The format of the watermaked image is determined by
     * the extension of the <code>targetFile</code> (case-insensitive):
     * <p>
     * <ul>
     *   <li>
     *     .jpg or .jpeg => JPEG
     *   </li>
     *   <li>
     *     .gif => GIF
     *   </li>
     *   <li>
     *     .png => PNG
     *   </li>
     * </ul>
     * <p>
     * If the target file doesn't have any extension, PNG is used.
     * 
     * @param originalImage The image to watermark, on the file system.
     * 
     * @param targetFile The file where the watermarked version will
     * be saved. If this file already exists, it is overwritten.
     */
    public void watermark(String originalImagePath, File targetFile);

    /**
     * Watermark an image.
     * <p>
     * The format of the watermaked image is determined by
     * the extension of the <code>targetFile</code> (case-insensitive):
     * <p>
     * <ul>
     *   <li>
     *     .jpg or .jpeg => JPEG
     *   </li>
     *   <li>
     *     .gif => GIF
     *   </li>
     *   <li>
     *     .png => PNG
     *   </li>
     * </ul>
     * <p>
     * If the target file doesn't have any extension, PNG is used.
     * @param originalImage The image to watermark.
     * 
     * @param onClasspath If <code>true</code>, the original image
     * will be taken from the classpath. Otherwise its path must
     * be a path on the file system.
     * 
     * @param targetFile The file where the watermarked version will
     * be saved. If this file already exists, it is overwritten.
     */
    public void watermark(String originalImagePath, boolean onClasspath, File targetFile);


}
