package innsync.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.Timer;

/** Blue logo block + dark navigation bar + date/time + Administrator pill. */
public class HeaderPanel extends JPanel {

    private static final int STRIP = 10;       // thin blue line at the very top
    private static final int LOGO_W = 216;     // width of the blue logo block (top edge)
    private static final int SLANT = 38;       // how far the logo block's edge leans

    private final Map<String, NavButton> buttons = new LinkedHashMap<>();
    private final JLabel clock = new JLabel();

    public HeaderPanel(String[] pages, Consumer<String> onNavigate) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(100, 108));

        JPanel logoSpace = new JPanel();
        logoSpace.setOpaque(false);
        logoSpace.setPreferredSize(new Dimension(LOGO_W + 14, 10));
        add(logoSpace, BorderLayout.WEST);

        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);

        // top-right: date/time + Administrator pill
        clock.setForeground(Color.WHITE);
        clock.setFont(UiUtil.font(Font.PLAIN, 12));
        JPanel info = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        info.setOpaque(false);
        info.setBorder(BorderFactory.createEmptyBorder(STRIP + 8, 0, 0, 12));
        info.add(clock);
        info.add(new Pill("Administrator"));
        right.add(info, BorderLayout.NORTH);

        // bottom: navigation tabs
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        nav.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        for (String name : pages) {
            NavButton b = new NavButton(name);
            b.addActionListener(e -> onNavigate.accept(name));
            group.add(b);
            nav.add(b);
            buttons.put(name, b);
        }
        right.add(nav, BorderLayout.SOUTH);
        add(right, BorderLayout.CENTER);

        updateClock();
        new Timer(15000, e -> updateClock()).start();
    }

    /** Highlights the tab with this name. */
    public void select(String name) {
        NavButton b = buttons.get(name);
        if (b != null) {
            b.setSelected(true);
        }
    }

    private void updateClock() {
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("EEE, MMM d", Locale.ENGLISH));
        String time = now.format(DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH));
        clock.setText("<html><div style='text-align:right'>" + date + "<br>" + time + "</div></html>");
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();

        g.setColor(UiUtil.DARK);
        g.fillRect(0, 0, w, h);

        g.setColor(UiUtil.BLUE);
        g.fillRect(0, 0, w, STRIP);
        g.fillPolygon(new int[] { 0, LOGO_W, LOGO_W - SLANT, 0 },
                new int[] { STRIP, STRIP, h, h }, 4);

        g.setColor(Color.WHITE);
        g.setFont(UiUtil.font(Font.BOLD | Font.ITALIC, 32));
        FontMetrics fm = g.getFontMetrics();
        int y = STRIP + (h - STRIP + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString("InnSync", 26, y);
        g.dispose();
    }

    // ------------------------------------------------------------------

    private static class NavButton extends JToggleButton {
        NavButton(String text) {
            super(text.toUpperCase());
            setFont(UiUtil.font(Font.BOLD, 13));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            return new Dimension(fm.stringWidth(getText()) + 40, 38);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            if (isSelected()) {
                g.setColor(UiUtil.NAV_ACTIVE);
                g.fillRect(0, 0, getWidth(), getHeight());
            } else if (getModel().isRollover()) {
                g.setColor(new Color(255, 255, 255, 35));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            g.setFont(getFont());
            g.setColor(Color.WHITE);
            FontMetrics fm = g.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g.drawString(getText(), x, y);
            g.dispose();
        }
    }

    private static class Pill extends JComponent {
        private final String text;

        Pill(String text) {
            this.text = text;
            setPreferredSize(new Dimension(138, 32));
            setFont(UiUtil.font(Font.PLAIN, 14));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(UiUtil.PILL);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 32, 32);
            g.setColor(new Color(0x55556F));
            g.setFont(getFont());
            FontMetrics fm = g.getFontMetrics();
            g.drawString(text, (getWidth() - fm.stringWidth(text)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g.dispose();
        }
    }
}
