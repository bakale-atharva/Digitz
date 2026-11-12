package digitz;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import javax.imageio.ImageIO;

/**
 * Loads the Digitz application icon from the classpath.
 *
 * <p>The icon file lives at {@value #RESOURCE_PATH}, relative to the classpath root.
 * In this project that is {@code src/resources/icon/icon.png}, because {@code src} is the
 * source root and intelliJ copies non-Java files from it into the build output. Any image
 * format supported by {@link ImageIO} (PNG, JPEG, GIF, BMP) works.
 *
 * <p>A missing or broken icon is not treated as fatal: the app simply runs with the
 * default Java icon.
 *
 * <h2>Example</h2>
 * {@snippet :
 * AppIcon.load().ifPresent(frame::setIconImage);
 * }
 *
 * @see CalculatorWindow
 */
final class AppIcon {
    /** Where the icon file is found on the classpath. The leading {@code /} means "from the root". */
    private static final String RESOURCE_PATH = "/resources/icon/icon.png";

    // Best practice: a utility class with only static methods get a private constructor,
    // so nobody creates a useless instance of it.

    /** Not used. This class only has static methods, so it is never instantiated. */
    private AppIcon() {}

    /**
     * Reads the icon image from the classpath.
     *
     * <p> Each call reads the file again, so call it once and keep the result.
     * Problems are reported on {@link System#err}; they never stop the app.
     *
     * @return the icon, or an empty {@link Optional} if the file is missing, is not a
     *         supported image format, or it cannot be read
     */
    static Optional<Image> load() {
        // Best practice: try-with-resources closes the stream even if reading fails.
        // A Null resource is allowed here; Java simply skips closing it.
        try (InputStream stream = AppIcon.class.getResourceAsStream(RESOURCE_PATH)) {
            if (stream == null) {
                return Optional.empty();
            }
            // Best practice: Optional tells the caller "this may be missing" instead of returning null.
            return Optional.ofNullable(ImageIO.read(stream));
        } catch (IOException e) {
            System.err.println("Could not load the app icon: " + e.getMessage());
            return Optional.empty();
        }
    }
}
