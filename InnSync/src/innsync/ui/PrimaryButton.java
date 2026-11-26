package innsync.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

//ito yung blue button na may "+"sign
public class PrimaryButton extends JButton {

    public PrimaryButton(String text, boolean withPlus) {
        super(text);
        setForeground(Color.WHITE);
        setFont(UiUtil.font(Font.BOLD, 15));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        setIconTextGap(10);
        if (withPlus) {
            setIcon(new PlusIcon(22));
        }
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color c = UiUtil.PRIMARY;
        if (getModel().isPressed()) {
            c = c.darker();
        } else if (getModel().isRollover()) {
            c = new Color(0x1A5BC4);
        }
        g.setColor(c);
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
        g.dispose();
        super.paintComponent(g0);
    }

    private static class PlusIcon implements Icon {
        private final int size;

        PlusIcon(int size) { this.size = size; }

        @Override
        public void paintIcon(Component c, Graphics g0, int x, int y) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawOval(x + 1, y + 1, size - 3, size - 3);
            int m = size / 2;
            g.drawLine(x + m, y + 6, x + m, y + size - 6);
            g.drawLine(x + 6, y + m, x + size - 6, y + m);
            g.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }
}
