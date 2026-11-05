/**
 * @name Digitz
 * @package atharvabakale.calculator
 * @file UI.java
 * @author Atharva Bakale
 * @link https://github.com/bakale-atharva
 */

package digitz;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;

import java.awt.Image;
import javax.swing.ImageIcon;
import java.io.*;

public class UI implements ActionListener {
    private final JFrame frame;

    private final JPanel panel;
    private final JPanel panelSub1;
    private final JPanel panelSub2;
    private final JPanel panelSub3;
    private final JPanel panelSub4;
    private final JPanel panelSub5;
    private final JPanel panelSub6;
    private final JPanel panelSub7;
    private final JPanel panelSub8;

    private final JTextArea text;

    private final JButton but[], butAdd, butMinus, butMultiply, butDivide,
            butEqual, butCancel, butSquareRoot, butSquare, butOneDividedBy,
            butCos, butSin, butTan, butxpowerofy, butlog, butrate, butabs, butBinary, butln;
    private final Calculator calc;

    private final String[] buttonValue = {"0", "1", "2", "3", "4", "5", "6",
            "7", "8", "9"};

    private final Font font;
    private final Font textFont;
    private ImageIcon image;
    private BufferedImageCustom imageReturn;

    public UI() throws IOException {
        frame = new JFrame("Digitz");

        imageReturn = new BufferedImageCustom();
        image = new ImageIcon(imageReturn.imageReturn());
    }

    public void init() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
