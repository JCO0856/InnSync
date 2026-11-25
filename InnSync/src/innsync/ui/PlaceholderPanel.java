package innsync.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Stand-in page for sections you have not built yet (Record, Accounting, Calendar, Settings). */
public class PlaceholderPanel extends JPanel {

    public PlaceholderPanel(String title) {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("Georgia", Font.PLAIN, 32));
        t.setForeground(new Color(0xB5B5B5));

        JLabel s = new JLabel("This section is coming soon.", SwingConstants.CENTER);
        s.setForeground(UiUtil.TEXT_GRAY);

        JPanel box = new JPanel(new GridLayout(2, 1, 0, 8));
        box.setOpaque(false);
        box.add(t);
        box.add(s);
        add(box);
    }
}
