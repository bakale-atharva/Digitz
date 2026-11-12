package digitz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Taskbar;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * The Digitz window: a history line, the main display and the keypad.
 *
 * <p>This class only deals with what the user sees and presses. Every button and
 * key forwards to {@link CalculatorEngine}, then {@link #refresh()} copies the
 * engine's state back onto the screen.</p>
 */
public final class CalculatorWindow {
    private static final String TITLE = "Digitz";

    // Best practice: logical font names (SANS_SERIF) exist on every platform; "Arial" may not.
    private static final Font DISPLAY_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 34);
    private static final Font HISTORY_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private static final Font KEY_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 18);
    private static final Font SCIENTIFIC_KEY_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 15);

    private static final int GAP = 6;
    private static final Dimension KEY_SIZE = new Dimension(72, 52);

    public CalculatorWindow() {}

    public void show() {}
}
