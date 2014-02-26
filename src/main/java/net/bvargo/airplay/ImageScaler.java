package net.bvargo.airplay;

import java.awt.image.BufferedImage;
import java.awt.Graphics;

/**
 * Scales an image.
 */
class ImageScaler {
    /**
     * Downsizes the given image to the desired size, maintaining the aspect
     * ratio. If the image is already smaller than the desired size, then
     * nothing happens, and the input image is returned.
     */
    public BufferedImage scaleImage(BufferedImage image, int width, int height) {
        int inputWidth = image.getWidth();
        int inputHeight = image.getHeight();

        // no modifications if the image is already smaller
        if(inputWidth <= width && inputHeight <= height)
            return image;

        // compute scaled width / height
        double outputAspectRatio = (double)width/height;
        double inputAspectRatio = (double)inputWidth/inputHeight;
        int scaledWidth;
        int scaledHeight;
        if(inputAspectRatio > outputAspectRatio) {
            // image is wider than desired output
            scaledWidth = width;
            scaledHeight = (int)((double)width / inputAspectRatio);
        }
        else {
            // image is taller than desired output
            scaledWidth = (int)((double)height * inputAspectRatio);
            scaledHeight = height;
        }

        // scaled the image
        BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = scaledImage.createGraphics();
        g.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        return scaledImage;
    }
}
