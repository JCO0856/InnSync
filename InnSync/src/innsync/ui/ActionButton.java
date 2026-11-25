package innsync.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

import javax.swing.JButton;

/** Small rounded icon button (view / edit / delete) drawn with Java2D, no image files needed. */
public class ActionButton extends JButton {

    public enum Kind { VIEW, EDIT, DELETE }

    private final Kind kind;

    public ActionButton(Kind kind, String tooltip) {
        this.kind = kind;
        setToolTipText(tooltip);
        setPreferredSize(new Dimension(40, 30));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color base;
        switch (kind) {
            case VIEW: base = UiUtil.VIEW; break;
            case EDIT: base = UiUtil.EDIT; break;
            default: base = UiUtil.DELETE; break;
        }
        if (getModel().isPressed()) {
            base = base.darker();
        } else if (getModel().isRollover()) {
            base = base.brighter();
        }
        g.setColor(base);
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (kind) {
            case VIEW:
                Path2D eye = new Path2D.Double();
                eye.moveTo(cx - 10, cy);
                eye.quadTo(cx, cy - 11, cx + 10, cy);
                eye.quadTo(cx, cy + 11, cx - 10, cy);
                g.draw(eye);
                g.fillOval(cx - 3, cy - 3, 7, 7);
                break;
            case EDIT:
                g.translate(cx, cy);
                g.rotate(Math.toRadians(45));
                g.fillRect(-3, -9, 6, 12);
                g.fill(new Polygon(new int[] { -3, 3, 0 }, new int[] { 4, 4, 9 }, 3));
                g.rotate(Math.toRadians(-45));
                g.translate(-cx, -cy);
                break;
            default:
                g.drawLine(cx - 7, cy - 5, cx + 7, cy - 5);
                g.drawLine(cx - 2, cy - 8, cx + 2, cy - 8);
                g.fill(new Polygon(new int[] { cx - 5, cx + 5, cx + 4, cx - 4 },
                        new int[] { cy - 3, cy - 3, cy + 8, cy + 8 }, 4));
                g.setColor(base);
                g.setStroke(new BasicStroke(1.4f));
                g.drawLine(cx - 2, cy, cx - 2, cy + 5);
                g.drawLine(cx + 2, cy, cx + 2, cy + 5);
                break;
        }
        g.dispose();
    }
}
