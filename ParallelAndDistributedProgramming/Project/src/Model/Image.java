package Model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Image {

    private String filename;
    private BufferedImage image;

    public Image(String filename) {
        this.filename = filename;
    }

    public void read() {

        try {
            image = ImageIO.read(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void write() {

        try {
            File output = new File("blurred" + filename);
            ImageIO.write(image, "png", output);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public Color getRGB(int index) {

        int x = index % image.getWidth();
        int y = index / image.getWidth();

        return new Color(image.getRGB(x, y));
    }

    public void setRGB(int index, int rgb) {

        int x = index % image.getWidth();
        int y = index / image.getWidth();

        image.setRGB(x, y, rgb);
    }

    public int width() {
        return image.getWidth();
    }

    public int height() {
        return image.getHeight();
    }
}
