package digitz;

import java.awt.GraphicsEnvironment;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Starts the Digitz Calculator.
 *
 * @author Atharva Bakale
 * @see <a href="https://github.com/bakale-atharva">github.com/bakale-atharva</a>
 */

public final class Digitz {
    private Digitz() {}

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

    /** Makes the buttons and window look native on macOS, Windows and Linux. */
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
