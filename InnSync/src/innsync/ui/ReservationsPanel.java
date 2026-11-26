package innsync.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import innsync.data.DataStore;
import innsync.model.Reservation;
import innsync.model.ReservationStatus;

public class ReservationsPanel extends JPanel {

    private static final int STATUS_COL = 8;
    private static final int ACTIONS_COL = 9;

    private final DataStore store = DataStore.get();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[] { "ID", "Guest", "Contact", "Room", "Check-In", "Check-Out",
                    "Nights", "Total", "Status", "Actions" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == ACTIONS_COL;
        }
    };
    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

    private final JTextField search = new JTextField();
    private final JComboBox<Object> statusFilter = new JComboBox<>();

    private final JLabel booked = new JLabel("0");
    private final JLabel confirmed = new JLabel("0");
    private final JLabel cancelled = new JLabel("0");
    private final JLabel checkedIn = new JLabel("0");
    private final JLabel checkedOut = new JLabel("0");

    public ReservationsPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(14, 24, 18, 24));

        add(buildFilterBar(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        store.addChangeListener(this::refresh);
        refresh();
    }

    // layout

    private JPanel buildFilterBar() {
        statusFilter.addItem("All");
        for (ReservationStatus s : ReservationStatus.values()) {
            statusFilter.addItem(s);
        }
        search.setPreferredSize(new Dimension(380, 36));
        statusFilter.setPreferredSize(new Dimension(200, 36));

        JLabel l1 = new JLabel("Search:");
        JLabel l2 = new JLabel("Status:");
        l1.setFont(UiUtil.font(Font.PLAIN, 16));
        l2.setFont(UiUtil.font(Font.PLAIN, 16));

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bar.setOpaque(false);
        bar.add(l1);
        bar.add(search);
        bar.add(Box(24));
        bar.add(l2);
        bar.add(statusFilter);

        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });
        statusFilter.addActionListener(e -> applyFilter());
        return bar;
    }

    private static JPanel Box(int width) {
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(width, 1));
        return spacer;
    }

    private JScrollPane buildTable() {
        TableStyler.style(table, true, true, 40);
        table.setRowSorter(sorter);
        sorter.setSortable(ACTIONS_COL, false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        int[] widths = { 60, 130, 120, 110, 150, 150, 70, 110, 100, 150 };
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        TableColumn actions = table.getColumnModel().getColumn(ACTIONS_COL);
        actions.setCellRenderer(new ActionsCell.Renderer());
        actions.setCellEditor(new ActionsCell.Editor(0,
                this::viewReservation, this::editReservation, this::deleteReservation));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UiUtil.BORDER));
        scroll.getViewport().setBackground(UiUtil.TABLE_BG);
        return scroll;
    }

    private JPanel buildFooter() {
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        stats.setOpaque(false);
        stats.add(statBox(booked, "Booked"));
        stats.add(statBox(confirmed, "Confirmed"));
        stats.add(statBox(cancelled, "Cancelled"));
        stats.add(statBox(checkedIn, "Checked In"));
        stats.add(statBox(checkedOut, "Checked Out"));

        PrimaryButton newBtn = new PrimaryButton("New Reservation", true);
        newBtn.addActionListener(e -> openDialog(null));
        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        right.add(newBtn, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(stats, BorderLayout.WEST);
        footer.add(right, BorderLayout.EAST);
        return footer;
    }

    private JPanel statBox(JLabel value, String title) {
        value.setFont(UiUtil.font(Font.BOLD, 26));
        value.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(UiUtil.font(Font.PLAIN, 13));
        t.setForeground(new Color(0x444444));

        JPanel b = new JPanel(new BorderLayout());
        b.setBackground(UiUtil.STAT_BG);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiUtil.BORDER),
                BorderFactory.createEmptyBorder(6, 6, 8, 6)));
        b.setPreferredSize(new Dimension(130, 72));
        b.add(value, BorderLayout.CENTER);
        b.add(t, BorderLayout.SOUTH);
        return b;
    }

    //data

    private void refresh() {
        if (table.isEditing()) {
            table.getCellEditor().cancelCellEditing();
        }
        model.setRowCount(0);
        for (Reservation r : store.getReservations()) {
            model.addRow(new Object[] { r.getId(), r.getGuestName(), r.getContact(), r.getRoom(),
                    UiUtil.checkInText(r.getCheckIn()), UiUtil.checkOutText(r.getCheckOut()),
                    r.getNights(), UiUtil.money(r.getTotal()), r.getStatus(), "" });
        }
        booked.setText(String.valueOf(store.countByStatus(ReservationStatus.BOOKED)));
        confirmed.setText(String.valueOf(store.countByStatus(ReservationStatus.CONFIRMED)));
        cancelled.setText(String.valueOf(store.countByStatus(ReservationStatus.CANCELLED)));
        checkedIn.setText(String.valueOf(store.countByStatus(ReservationStatus.CHECKED_IN)));
        checkedOut.setText(String.valueOf(store.countByStatus(ReservationStatus.CHECKED_OUT)));
    }

    private void applyFilter() {
        final String text = search.getText().trim().toLowerCase();
        final Object status = statusFilter.getSelectedItem();
        sorter.setRowFilter(new RowFilter<Object, Object>() {
            @Override
            public boolean include(Entry<? extends Object, ? extends Object> entry) {
                if (status instanceof ReservationStatus
                        && !entry.getValue(STATUS_COL).equals(status)) {
                    return false;
                }
                if (text.isEmpty()) {
                    return true;
                }
                for (int i = 0; i < ACTIONS_COL; i++) {
                    if (String.valueOf(entry.getValue(i)).toLowerCase().contains(text)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    //row actions

    private void openDialog(Reservation toEdit) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        new ReservationDialog(owner, toEdit).setVisible(true);
    }

    private void viewReservation(int id) {
        Reservation r = store.findReservation(id);
        if (r == null) {
            return;
        }
        JPanel p = new JPanel(new GridBagLayout());
        int row = 0;
        row = detail(p, row, "Reservation ID", String.valueOf(r.getId()));
        row = detail(p, row, "Guest", r.getGuestName());
        row = detail(p, row, "Contact", r.getContact().isEmpty() ? "-" : r.getContact());
        row = detail(p, row, "Guests", String.valueOf(r.getGuests()));
        row = detail(p, row, "Room", r.getRoom() + "  (" + UiUtil.money(r.getRoom().getRate()) + " / night)");
        row = detail(p, row, "Check-In", UiUtil.checkInText(r.getCheckIn()));
        row = detail(p, row, "Check-Out", UiUtil.checkOutText(r.getCheckOut()));
        row = detail(p, row, "Nights", String.valueOf(r.getNights()));
        row = detail(p, row, "Total", UiUtil.money(r.getTotal()));
        detail(p, row, "Status", r.getStatus().toString());
        JOptionPane.showMessageDialog(this, p, "Reservation #" + id, JOptionPane.PLAIN_MESSAGE);
    }

    private int detail(JPanel p, int row, String label, String value) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(3, 4, 3, 16);

        JLabel l = new JLabel(label + ":");
        l.setFont(UiUtil.font(Font.BOLD, 13));
        c.gridx = 0;
        p.add(l, c);

        c.gridx = 1;
        p.add(new JLabel(value), c);
        return row + 1;
    }

    private void editReservation(int id) {
        Reservation r = store.findReservation(id);
        if (r != null) {
            openDialog(r);
        }
    }

    private void deleteReservation(int id) {
        Reservation r = store.findReservation(id);
        if (r == null) {
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "Delete reservation #" + id + " for " + r.getGuestName() + "?\nThis cannot be undone.",
                "Delete Reservation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) {
            store.deleteReservation(r);
        }
    }
}
