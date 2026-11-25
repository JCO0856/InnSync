package innsync.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/** Gives every table in the app the same flat look as the mockup. */
public final class TableStyler {

    private TableStyler() { }

    public static void style(JTable table, boolean centered, boolean grid, int rowHeight) {
        final int align = centered ? SwingConstants.CENTER : SwingConstants.LEFT;

        table.setRowHeight(rowHeight);
        table.setFont(UiUtil.font(Font.PLAIN, 13));
        table.setShowGrid(grid);
        table.setGridColor(new Color(0x8C8C8C));
        table.setBackground(UiUtil.TABLE_BG);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(0xDCE6FF));
        table.setSelectionForeground(Color.BLACK);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                    boolean focus, int row, int column) {
                super.getTableCellRendererComponent(t, value, selected, false, row, column);
                setHorizontalAlignment(align);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new HeaderRenderer(align));
        header.setPreferredSize(new Dimension(100, 36));
    }

    private static class HeaderRenderer implements TableCellRenderer {
        private final JLabel label = new JLabel();

        HeaderRenderer(int align) {
            label.setHorizontalAlignment(align);
            label.setOpaque(true);
            label.setBackground(Color.WHITE);
            label.setForeground(new Color(0x333333));
            label.setFont(UiUtil.font(Font.BOLD, 13));
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xD5D5D5)),
                    BorderFactory.createEmptyBorder(0, 8, 0, 8)));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                boolean focus, int row, int column) {
            label.setText(value == null ? "" : value.toString());
            return label;
        }
    }
}
