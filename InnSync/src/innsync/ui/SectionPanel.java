package innsync.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** A white box with a bold title and a line under it (Rooms / Reservations / Request-Reports). */
public class SectionPanel extends JPanel {

    public SectionPanel(String title, JComponent body) {
        super(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(UiUtil.BORDER));

        JLabel header = new JLabel(title);
        header.setFont(UiUtil.font(Font.BOLD, 18));
        header.setForeground(UiUtil.TEXT_GRAY);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, UiUtil.LINE),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        add(header, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
    }
}
