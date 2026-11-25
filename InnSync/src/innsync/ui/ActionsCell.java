package innsync.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.function.IntConsumer;

import javax.swing.AbstractCellEditor;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/** The "Actions" column: view / edit / delete buttons inside a table row. */
public final class ActionsCell {

    private ActionsCell() { }

    private static JPanel panel(ActionButton view, ActionButton edit, ActionButton delete) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 5));
        p.add(view);
        p.add(edit);
        p.add(delete);
        return p;
    }

    /** Draws the buttons (not clickable). */
    public static class Renderer implements TableCellRenderer {
        private final JPanel panel = panel(
                new ActionButton(ActionButton.Kind.VIEW, "View"),
                new ActionButton(ActionButton.Kind.EDIT, "Edit"),
                new ActionButton(ActionButton.Kind.DELETE, "Delete"));

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                boolean focus, int row, int column) {
            panel.setBackground(selected ? table.getSelectionBackground() : table.getBackground());
            return panel;
        }
    }

    /** Makes the buttons clickable. The row's ID is read from idColumn. */
    public static class Editor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;
        private final int idColumn;
        private int currentId = -1;

        public Editor(int idColumn, IntConsumer onView, IntConsumer onEdit, IntConsumer onDelete) {
            this.idColumn = idColumn;
            ActionButton view = new ActionButton(ActionButton.Kind.VIEW, "View");
            ActionButton edit = new ActionButton(ActionButton.Kind.EDIT, "Edit");
            ActionButton delete = new ActionButton(ActionButton.Kind.DELETE, "Delete");
            view.addActionListener(e -> fire(onView));
            edit.addActionListener(e -> fire(onEdit));
            delete.addActionListener(e -> fire(onDelete));
            panel = panel(view, edit, delete);
        }

        private void fire(IntConsumer action) {
            final int id = currentId;
            fireEditingStopped();
            SwingUtilities.invokeLater(() -> action.accept(id));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean selected,
                int row, int column) {
            currentId = ((Number) table.getValueAt(row, idColumn)).intValue();
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}
