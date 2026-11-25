package innsync.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import innsync.data.DataStore;
import innsync.model.Room;

public class RoomsPanel extends JPanel {

    private final DataStore store = DataStore.get();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[] { "Room No.", "Type", "Capacity", "Rate / Night", "Status" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public RoomsPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(14, 24, 18, 24));

        JTable table = new JTable(model);
        TableStyler.style(table, true, true, 36);
        table.setAutoCreateRowSorter(true);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UiUtil.BORDER));
        scroll.getViewport().setBackground(UiUtil.TABLE_BG);
        add(scroll, BorderLayout.CENTER);

        PrimaryButton addBtn = new PrimaryButton("Add Room", true);
        addBtn.addActionListener(e -> addRoom());
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        bar.setOpaque(false);
        bar.add(addBtn);
        add(bar, BorderLayout.SOUTH);

        store.addChangeListener(this::refresh);
        refresh();
    }

    private void refresh() {
        model.setRowCount(0);
        for (Room r : store.getRooms()) {
            model.addRow(new Object[] { r.getNumber(), r.getType(), r.getCapacity(),
                    UiUtil.money(r.getRate()), store.getRoomStatus(r) });
        }
    }

    private void addRoom() {
        JTextField number = new JTextField();
        JComboBox<String> type = new JComboBox<>(new String[] { "Single", "Double", "Deluxe", "Suite" });
        JSpinner capacity = new JSpinner(new SpinnerNumberModel(2, 1, 20, 1));
        JSpinner rate = new JSpinner(new SpinnerNumberModel(1500.0, 0.0, 1000000.0, 100.0));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Room number:"));
        form.add(number);
        form.add(new JLabel("Type:"));
        form.add(type);
        form.add(new JLabel("Capacity:"));
        form.add(capacity);
        form.add(new JLabel("Rate per night:"));
        form.add(rate);

        int result = JOptionPane.showConfirmDialog(this, form, "Add Room",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String no = number.getText().trim();
        if (no.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Room number is required.", "Missing data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Room room = new Room(no, (String) type.getSelectedItem(),
                (Integer) capacity.getValue(), (Double) rate.getValue());
        if (!store.addRoom(room)) {
            JOptionPane.showMessageDialog(this, "Room " + no + " already exists.", "Duplicate room",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
