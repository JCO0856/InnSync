package innsync.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;

import innsync.data.DataStore;
import innsync.model.Reservation;
import innsync.model.ReservationStatus;
import innsync.model.Room;

/** Used for both "New Reservation" (editing == null) and "Edit Reservation". */
public class ReservationDialog extends JDialog {

    private final DataStore store = DataStore.get();
    private final Reservation editing;

    private final JTextField nameField = new JTextField(20);
    private final JTextField contactField = new JTextField(20);
    private final JSpinner guestsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
    private final JSpinner inSpinner;
    private final JSpinner outSpinner;
    private final JComboBox<Room> roomCombo = new JComboBox<>();
    private final JComboBox<ReservationStatus> statusCombo = new JComboBox<>(ReservationStatus.values());
    private final JLabel totalLabel = new JLabel("-");

    public ReservationDialog(Window owner, Reservation editing) {
        super(owner, editing == null ? "New Reservation" : "Edit Reservation",
                ModalityType.APPLICATION_MODAL);
        this.editing = editing;

        LocalDate today = LocalDate.now();
        inSpinner = dateSpinner(editing == null ? today : editing.getCheckIn());
        outSpinner = dateSpinner(editing == null ? today.plusDays(1) : editing.getCheckOut());

        if (editing != null) {
            nameField.setText(editing.getGuestName());
            contactField.setText(editing.getContact());
            guestsSpinner.setValue(editing.getGuests());
            statusCombo.setSelectedItem(editing.getStatus());
            roomCombo.setModel(new DefaultComboBoxModel<>(new Room[] { editing.getRoom() }));
        }

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 18, 8, 18));
        int row = 0;
        addRow(form, row++, "Guest name:", nameField);
        addRow(form, row++, "Contact no.:", contactField);
        addRow(form, row++, "Guests:", guestsSpinner);
        addRow(form, row++, "Check-in:", inSpinner);
        addRow(form, row++, "Check-out:", outSpinner);
        addRow(form, row++, "Room:", roomCombo);
        addRow(form, row++, "Total:", totalLabel);
        if (editing != null) {
            addRow(form, row++, "Status:", statusCombo);
        }

        PrimaryButton save = new PrimaryButton("Save", false);
        JButton cancel = new JButton("Cancel");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        buttons.add(cancel);
        buttons.add(save);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(save);

        guestsSpinner.addChangeListener(e -> refreshRooms());
        inSpinner.addChangeListener(e -> refreshRooms());
        outSpinner.addChangeListener(e -> refreshRooms());
        roomCombo.addActionListener(e -> updateTotal());
        save.addActionListener(e -> save());
        cancel.addActionListener(e -> dispose());

        refreshRooms();
        pack();
        setMinimumSize(new Dimension(400, getHeight()));
        setLocationRelativeTo(owner);
    }

    private void addRow(JPanel p, int row, String label, JComponent field) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row;
        c.insets = new Insets(5, 4, 5, 4);
        c.anchor = GridBagConstraints.WEST;

        JLabel l = new JLabel(label);
        l.setFont(l.getFont().deriveFont(Font.BOLD));
        c.gridx = 0;
        p.add(l, c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        p.add(field, c);
    }

    private static JSpinner dateSpinner(LocalDate d) {
        Date date = Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
        JSpinner s = new JSpinner(new SpinnerDateModel(date, null, null, Calendar.DAY_OF_MONTH));
        s.setEditor(new JSpinner.DateEditor(s, "yyyy-MM-dd"));
        return s;
    }

    private static LocalDate toLocalDate(JSpinner s) {
        return ((Date) s.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /** Lists only the rooms that are free for the chosen dates and big enough for the guests. */
    private void refreshRooms() {
        LocalDate in = toLocalDate(inSpinner);
        LocalDate out = toLocalDate(outSpinner);
        int guests = (Integer) guestsSpinner.getValue();
        Room keep = (Room) roomCombo.getSelectedItem();

        DefaultComboBoxModel<Room> m = new DefaultComboBoxModel<>();
        if (out.isAfter(in)) {
            for (Room r : store.getAvailableRooms(in, out, guests, editing)) {
                m.addElement(r);
            }
        }
        roomCombo.setModel(m);
        if (keep != null && m.getIndexOf(keep) >= 0) {
            roomCombo.setSelectedItem(keep);
        }
        updateTotal();
    }

    private void updateTotal() {
        LocalDate in = toLocalDate(inSpinner);
        LocalDate out = toLocalDate(outSpinner);
        Room room = (Room) roomCombo.getSelectedItem();

        if (!out.isAfter(in)) {
            totalLabel.setText("Check-out must be after check-in");
        } else if (room == null) {
            totalLabel.setText("No room available for these dates");
        } else {
            long nights = ChronoUnit.DAYS.between(in, out);
            totalLabel.setText(UiUtil.money(room.getRate() * nights) + "  (" + nights + " night"
                    + (nights > 1 ? "s" : "") + ")");
        }
    }

    private void save() {
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        LocalDate in = toLocalDate(inSpinner);
        LocalDate out = toLocalDate(outSpinner);
        Room room = (Room) roomCombo.getSelectedItem();
        int guests = (Integer) guestsSpinner.getValue();

        if (name.isEmpty()) {
            error("Please enter the guest name.");
        } else if (editing == null && in.isBefore(LocalDate.now())) {
            error("Check-in date cannot be in the past.");
        } else if (!out.isAfter(in)) {
            error("Check-out must be after check-in.");
        } else if (room == null) {
            error("Please choose an available room.");
        } else if (editing == null) {
            store.addReservation(name, contact, room, in, out, guests);
            dispose();
        } else {
            ReservationStatus status = (ReservationStatus) statusCombo.getSelectedItem();
            if (store.updateReservation(editing, name, contact, room, in, out, guests, status)) {
                dispose();
            } else {
                error("That room is already taken for these dates.");
            }
        }
    }

    private void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Cannot save", JOptionPane.WARNING_MESSAGE);
    }
}
