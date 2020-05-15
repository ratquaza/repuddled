package org.baito.API.image;

import java.awt.image.BufferedImage;

public class ImageBuilder {
    private BufferedImage img;

    public ImageBuilder(BufferedImage base) {
        img = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    public ImageBuilder(int width, int height) {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public ImageBuilder draw(BufferedImage add, int x, ImageOffset xOffset, int y, ImageOffset yOffset) {
        img = ImageUtils.draw(img, add, x, xOffset, y, yOffset);
        return this;
    }

    public BufferedImage render() {
        return img;
    }

}
