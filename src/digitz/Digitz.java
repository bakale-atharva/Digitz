/**
 * @name Digitz
 * @package atharvabakale.calculator
 * @file Main.java
 * @author Atharva Bakale
 * @email bakaleatharva13@gmail.com
 * @link https://github.com/bakale-atharva
 */
package digitz;

import java.awt.GraphicsEnvironment;

public class Digitz {
    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Digitz requires a graphical desktop environment to run.");
            return;
        }

        try {
            UI uiCal = new UI();
            uiCal.init();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
