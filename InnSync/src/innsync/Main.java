package innsync;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import innsync.ui.MainFrame;

/** Entry point of InnSync. Run this class as a Java Application. */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // fall back to the default look and feel
        }
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
