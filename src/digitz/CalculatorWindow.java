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
 * <h2>Layout</h2>
 * <pre>
 * ┌──────────────────────────────┐
 * │                     12 + 3 = │  history line (grey)
 * │                           15 │  main display
 * ├──────────────────────────────┤
 * │ sin  cos  tan  log  ln       │  scientific keys
 * │ x²   √    1/x  |x|  xʸ       │
 * ├──────────────────────────────┤
 * │ C    ⌫    ±    ÷             │  basic keys
 * │ 7    8    9    ×             │
 * │ 4    5    6    −             │
 * │ 1    2    3    +             │
 * │ %    0    .    =             │
 * └──────────────────────────────┘
 * </pre>
 *
 * <h2>How It Works</h2>
 * <p>This class only deals with what the user sees and presses; it contains no math.
 * Every button and keyboard shortcut calls one method on a {@link CalculatorEngine},
 * then {@link #refresh()} copies the engine's display and history text back onto the
 * screen. To change how the calculator <em>behaves</em>, edit the engine; to change how
 * it <em>looks</em>, edit this class.
 *
 * <h2>Keyboard Shortcuts</h2>
 * <table class="striped">
 *  <caption>Keys the window responds to</caption>
 *  <thead><tr><th scope="col">Keys</th><th scope="col">Actions</th></tr></thead>
 *  <tbody>
 *    <tr><td>{@code 0}-{@code 9}</td><td>Type a digit</td></tr>
 *    <tr><td>{@code .} or {@code ,}</td><td>Decimal point</td></tr>
 *    <tr><td>{@code + - * / ^}</td><td>Add, subtract, multiply, divide, power</td></td></tr>
 *    <tr><td>{@code %}</td><td>Percent</td></tr>
 *    <tr><td>{@code Enter} or {@code =}</td><td>Calculate</td></tr>
 *    <tr><td>{@code Backspace}</td><td>Delete the last character</td></tr>
 *    <tr><td>{@code Esc} or {@code Delete}</td><td>Clear everything</td></tr>
 *  </tbody>
 * </table>
 *
 * <h2>Threading</h2>
 * <p>Like all Swing code, this class must be created and used only on the Event
 * Dispatch Thread. {@link Digitz#main(String[])} takes care of that.
 *
 * <h2>Example</h2>
 * {@snippet :
 * SwingUtilities.invokeLater(() -> new CalculatorWindow().show());
 * }
 *
 * @see CalculatorEngine
 * @see Digitz
 */
public final class CalculatorWindow {
    /** The text in the window's title bar */
    private static final String TITLE = "Digitz";

    // Best practice: logical font names (SANS_SERIF) exist on every platform; "Arial" may not.

    /** Font of the main display: large and bold so the current number stands out. */
    private static final Font DISPLAY_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 34);

    /** Font of the grey history line above the display. */
    private static final Font HISTORY_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);

    /** Font of the digit, operator and control keys. */
    private static final Font KEY_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 18);

    /** Font of the scientific keys, slightly smaller because their initials are longer. */
    private static final Font SCIENTIFIC_KEY_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 15);

    /** Space in pixels between neighboring keys. Sections are separated by twice this. */
    private static final int GAP = 6;

    /** Preferred size of every key. {@link #show()} sizes the window around these. */
    private static final Dimension KEY_SIZE = new Dimension(72, 52);

    /** Does all the calculating. The window only forwards key presses to it. */
//    private final CalculatorEngine = new CalculatorEngine();

    // Best practices: Composition. The class *has* a JFrame instead of *being* one (extends JFrame),
    // so it exposes only the method it really needs

    /** The top-level window. Kept private so callers can only use {@link #show()}. */
    private final JFrame frame = new JFrame(TITLE);

    /**
     * The grey line above the display that describes the last step. It holds a single
     * space when empty, so it's height does not change. See {@link #refresh()}.
     */
    private final JLabel historyLabel = new JLabel(" ", SwingConstants.RIGHT);

    /** The main display showing the number being typed or the latest result. */
    private final JLabel displayLabel = new JLabel("0", SwingConstants.RIGHT);

    /**
     * Builds the window and all its keys, installs the keyboard shortcuts and sets the
     * icon. The window is not visible yet; call {@link #show()} for that.
     *
     * <p>Must be called on the Swing Event Dispatch Thread.
     */
    public CalculatorWindow() {
        // Best practices: DISPOSE_ON_CLOSE frees the window and lets the JVM exit on its own
        // once no windows are left, instead of killing the whole program with EXIT_ON_CLOSE.
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        JPanel content = new JPanel(new BorderLayout(GAP, GAP * 2));
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        content.add(createDisplayPanel(), BorderLayout.NORTH);
        content.add(createKeypad(), BorderLayout.CENTER);
        frame.setContentPane(content);

        installKeyboardShortcuts(frame.getRootPane());
        AppIcon.load().ifPresent(this::applyIcon);
        refresh();
    }

    public void show() {}

    /** Copies the engine's display text and history text onto the labels. */
    private void refresh() {
        displayLabel.setText(engine.displayText());
        String history = engine.historyText();
        // A single space keeps the label's height, so the layout does not jump when it is empty.
        historyLabel.setText(history.isEmpty() ? " " : history);
    }
}
