package digitz;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::showWindow);
    }

    private static void showWindow() {
        JFrame window = new JFrame("Digitz");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(new JLabel("Digitz is ready", SwingConstants.CENTER));
        window.setSize(320, 180);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
