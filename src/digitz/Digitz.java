package digitz;

import java.awt.GraphicsEnvironment;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * The entry point that starts the Digitz calculator.
 *
 * <p>Startup happens in three steps:
 * <ol>
 *     <li>Check that a graphical desktop is available. If not (for example on a server
 *         or in some CI systems), print a message and exit with code {@code 1}.</li>
 *     <li>Switch Swing to the operating system's own look-and-feel, so buttons look
 *         native on macOS, Windows and Linux.</li>
 *     <li>Create and show a {@link CalculatorWindow}.</li>
 * </ol>
 * Steps 2 and 3 run on the Swing Event Dispatch Thread, as Swing requires.
 *
 * <h2>Running It</h2>
 * {@snippet lang="shell" :
 * javac -d out src/digitz/*.java
 * java -cp out:src digitz.Digitz
 * }
 * <p>On Windows, use {@code out;src} instead of {@code out:src}. The {@code src} entry
 * is there so the icon in {@code src/resources} can be found.
 *
 * @author Atharva Bakale
 * @see CalculatorWindow
 * @see <a href="https://github.com/bakale-atharva">github.com/bakale-atharva</a>
 */

public final class Digitz {
    /** Not used. This class only holds {@link #main(String[])}, so it is never instantiated. */
    private Digitz() {}

    /**
     * Starts the calculator.
     *
     * <p>This method returns almost immediately. The window keeps running on the Swing
     * Event Dispatch Thread, and the program ends when the user closes the window.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("Digitz need a graphical desktop to run.");
            System.exit(1);
        }

        // Best Practices: Swing is not thread-safe. Every Swing component must be created
        // and changed on the Event Dispatch Thread (EDT), and invokeLater puts the work there.
        SwingUtilities.invokeLater(() -> {
            useSystemLookAndFeel();
            new CalculatorWindow().show();
        });
    }

    /**
     * Switches Swing to the operating system's look-and-feel, so the window and buttons
     * look native: Aqua on macOS, the Windows theme on Windows, and GTK on Linux where available.
     *
     * <p>If the switch fails, a message is printed to {@link System#err} and Swing's
     * built-in cross-platform look is used instead. The app still works normally.
     * Must be called before any windows are created.
     */
    private static void useSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
            // Best practices: catch only the exceptions that can actually happen here,
            // and fall back gracefully instead of crashing.
            System.err.println("Using the default look and feel: " + e.getMessage());
        }
    }
}
