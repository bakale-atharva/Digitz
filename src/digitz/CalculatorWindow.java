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
    private static final String TITLE = "Digitz";

    private static final Font DISPLAY_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 34);
    private static final Font HISTORY_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private static final Font KEY_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 18);
    private static final Font SCIENTIFIC_KEY_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 15);

    private static final int GAP = 6;
    private static final Dimension KEY_SIZE = new Dimension(72, 52);

    private final CalculatorEngine engine = new CalculatorEngine();
    private final JFrame frame = new JFrame(TITLE);
    private final JLabel historyLabel = new JLabel(" ", SwingConstants.RIGHT);
    private final JLabel displayLabel = new JLabel("0", SwingConstants.RIGHT);

    /**
     * Builds the window and all its keys, installs the keyboard shortcuts and sets the
     * icon. The window is not visible yet; call {@link #show()} for that.
     *
     * <p>Must be called on the Swing Event Dispatch Thread.
     */
    public CalculatorWindow() {
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

    /**
     * Sizes the window to fit its contents, centres it on the screen and makes it visible.
     *
     * <p>Must be called on the Swing Event Dispatch Thread. Calling it again on a visible
     * window re-centres it.
     */
    public void show() {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createDisplayPanel() {
        historyLabel.setFont(HISTORY_FONT);
        historyLabel.setForeground(Color.GRAY);
        displayLabel.setFont(DISPLAY_FONT);

        JPanel panel = new JPanel(new BorderLayout(0, 2));
        panel.add(historyLabel, BorderLayout.NORTH);
        panel.add(displayLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createKeypad() {
        JPanel scientific = new JPanel(new GridLayout(2, 5, GAP, GAP));
        for (UnaryOp op : new UnaryOp[] {
                UnaryOp.SIN, UnaryOp.COS, UnaryOp.TAN, UnaryOp.LOG10, UnaryOp.LN,
                UnaryOp.SQUARE, UnaryOp.SQUARE_ROOT, UnaryOp.RECIPROCAL, UnaryOp.ABSOLUTE}) {
            scientific.add(createKey(op.label(), SCIENTIFIC_KEY_FONT, () -> engine.unary(op));
        }
        scientific.add(createKey("x", SCIENTIFIC_KEY_FONT, () -> engine.binary(BinaryOp.POWER)));

        JPanel basic = new JPanel(new GridLayout(5, 4, GAP, GAP));
        basic.add(createKey("C", KEY_FONT, engine::clear));
        basic.add(createKey("⌫", KEY_FONT, engine::backspace));
        basic.add(createKey(UnaryOp.NEGATE.label(), KEY_FONT, () -> engine.unary(UnaryOp.NEGATE)));
        basic.add(createOperatorKey(BinaryOp.DIVIDE));

        addDigitRow(basic, 7, 8, 9);
        basic.add(createOperatorKey(BinaryOp.MULTIPLY));
        addDigitRow(basic, 4, 5, 6);
        basic.add(createOperatorKey(BinaryOp.SUBTRACT));
        addDigitRow(basic, 1, 2, 3);
        basic.add(createOperatorKey(BinaryOp.ADD));

        basic.add(createKey(UnaryOp.PERCENT.label(), KEY_FONT, () -> engine.unary(UnaryOp.PERCENT)));
        basic.add(createKey("0", KEY_FONT, () -> engine.digit(0)));
        basic.add(createKey(".", KEY_FONT, engine::decimalPoint));
        basic.add(createKey("=", KEY_FONT, engine::equalsKey));

        JPanel keypad = new JPanel(new BorderLayout(0, GAP * 2));
        keypad.add(scientific, BorderLayout.NORTH);
        keypad.add(basic, BorderLayout.CENTER);
        return keypad;
    }

    private void addDigitRow(JPanel panel, int... digits) {
        for (int digit : digits) {
            panel.add(createKey(String.valueOf(digit), KEY_FONT, () -> engine.digit(digit)));
        }
    }

    private JButton createOperatorKey(BinaryOp op) {
        return createKey(op.symbol(), KEY_FONT, () -> engine.binary(op));
    }

    private JButton createKey(String text, Font font, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setPreferredSize(KEY_SIZE);
        // Unfocusable, so typed keys always reach the window shortcuts after a click.
        button.setFocusable(false);
        button.addActionListener(_ -> perform(action));
        return button;
    }

    // Key bindings work whichever component has focus, unline a KeyListener.
    private void installKeyboardShortcuts(JRootPane root) {
        for (int digit = 0; digit <= 9; digit++) {
            int value = digit;
            bindTyped(root, Character.forDigit(digit, 10), () -> engine.digit(value));
        }
        bindTyped(root, '.', engine::decimalPoint);
        bindTyped(root, ',', engine::decimalPoint);
        bindTyped(root, '+', () -> engine.binary(BinaryOp.ADD));
    }

    private void bindTyped(JRootPane root, char key, Runnable action) {
        bind(root, KeyStroke.getKeyStroke(key), "typed" + key, action);
    }

    private void bind(JRootPane root, KeyStroke stroke, String name, Runnable action) {
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, name);
        root.getActionMap().put(name, new EngineAction(() -> perform(action)));
    }

    private void perform(Runnable action) {
        action.run();
        refresh();
    }

    private void refresh() {
        displayLabel.setText(engine.displayText());
        String history = engine.historyText();
        // A single space keeps the label's height, so the layout does not jump when it is empty.
        historyLabel.setText(history.isEmpty() ? " " : history);
    }

    private void applyIcon(Image image) {
        frame.setIconImage(image);
        // macOS takes the Dock icon from the Taskbar API, not from the window.
        if (Taskbar.isTaskbarSupported()
                && Taskbar.getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE)) {
            Taskbar.getTaskbar().setIconImage(image);
        }
    }

    private static final class EngineAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private final transient Runnable task;

        EngineAction(Runnable task) {
            this.task = task;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            task.run();
        }
    }
}
