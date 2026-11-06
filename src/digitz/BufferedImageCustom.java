package digitz;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * Returns the application icon when present, otherwise falls back to an empty image.
 */
public class BufferedImageCustom {
    public Image imageReturn() throws IOException {
        InputStream stream = getClass().getResourceAsStream("/resources/icon/icon.png");
        if (stream == null) {
            return new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        }

        try (InputStream input = stream) {
            BufferedImage image = ImageIO.read(input);
            if (image != null) {
                return image;
            }
        }

        return new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    }
}
