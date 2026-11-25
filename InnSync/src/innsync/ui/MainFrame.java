package innsync.ui;

import java.awt.Dimension;

import javax.swing.JFrame;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("InnSync - Hotel Reservation System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(new AppPanel());
        setSize(1180, 720);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null);
    }
}
