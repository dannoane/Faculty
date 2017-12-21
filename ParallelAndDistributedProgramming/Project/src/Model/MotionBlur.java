package Model;

import java.awt.*;

public class MotionBlur {

    private static final int filterWidth = 9;
    private static final int filterHeight = 9;
    private static final int[][] filter = {
            {1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 1}
    };

    private static float factor = 1.0f / 9.0f;
    private static float bias = 0.0f;

    public static double getFactor() {
        return factor;
    }

    public static void setFactor(float factor) {
        MotionBlur.factor = factor;
    }

    public static double getBias() {
        return bias;
    }

    public static void setBias(float bias) {
        MotionBlur.bias = bias;
    }

    public static void apply(int x, int y, int size, Image image) {

        float red, green, blue;

        for (int indexX = x; (indexX < x + size) && (indexX < image.width()); ++indexX) {
            for (int indexY = y; (indexY < y + size) && (indexY < image.height()); ++indexY) {
                red = green = blue = 0.0f;

                for (int filterY = 0; filterY < filterHeight; ++filterY) {
                    for (int filterX = 0; filterX < filterWidth; ++filterX) {
                        int imageX = (indexX - filterWidth / 2 + filterX + image.width()) % image.width();
                        int imageY = (indexY - filterHeight / 2 + filterY + image.height()) % image.height();

                        red += image.getRGB(imageY * image.width() + imageX).getRed() * filter[filterY][filterX];
                        green += image.getRGB(imageY * image.width() + imageX).getGreen() * filter[filterY][filterX];
                        blue += image.getRGB(imageY * image.width() + imageX).getBlue() * filter[filterY][filterX];
                    }
                }

                red = Math.min(Math.max((int) (factor * red) + bias, 0), 255);
                green = Math.min(Math.max((int) (factor * green) + bias, 0), 255);
                blue = Math.min(Math.max((int) (factor * blue) + bias, 0), 255);

                Color color = new Color((int) red, (int) green, (int) blue);
                image.setRGB(indexY * image.width() + indexX, color.getRGB());
            }
        }
    }
}
