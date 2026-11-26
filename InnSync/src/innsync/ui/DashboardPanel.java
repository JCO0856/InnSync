package innsync.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import innsync.data.DataStore;
import innsync.model.Reservation;
import innsync.model.ReservationStatus;
import innsync.model.Room;

public class DashboardPanel extends JPanel {

    private final DataStore store = DataStore.get();

    private final JLabel greeting = new JLabel();
    private final JLabel totalRooms = number();
    private final JLabel availableRooms = number();
    private final JLabel occupiedRooms = number();
    private final JLabel activeReservations = number();
    private final JLabel departing = number();
    private final JLabel cancelled = number();

    private final DefaultTableModel arrivalsModel = new DefaultTableModel(
            new String[] { "Request ID", "Guest Name", "Room", "Check-In", "Nights" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(8, 44, 22, 44));

        // greeting with the thin vertical line on its left
        greeting.setFont(new Font("Georgia", Font.PLAIN, 34));
        greeting.setForeground(new Color(0xB5B5B5));
        greeting.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, UiUtil.LINE),
                BorderFactory.createEmptyBorder(8, 14, 8, 0)));
        add(greeting, BorderLayout.NORTH);

        // Rooms | Reservations | Request/Reports  +  Upcoming Arrivals
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        c.gridx = 0; c.gridy = 0; c.weightx = 0.22; c.weighty = 0;
        c.insets = new Insets(0, 0, 18, 18);
        grid.add(new SectionPanel("Rooms", roomsBody()), c);

        c.gridx = 1; c.weightx = 0.48;
        grid.add(new SectionPanel("Reservations", reservationsBody()), c);

        c.gridx = 2; c.gridy = 0; c.gridheight = 2; c.weightx = 0.30; c.weighty = 0;
        c.insets = new Insets(0, 0, 0, 0);
        grid.add(new SectionPanel("Request/Reports", requestsBody()), c);

        c.gridx = 0; c.gridy = 1; c.gridwidth = 2; c.gridheight = 1; c.weightx = 0; c.weighty = 1;
        c.insets = new Insets(0, 0, 0, 18);
        grid.add(arrivalsBody(), c);

        add(grid, BorderLayout.CENTER);

        store.addChangeListener(this::refresh);
        new Timer(60000, e -> updateGreeting()).start();
        updateGreeting();
        refresh();
    }


    private JComponent roomsBody() {
        JPanel p = new JPanel(new GridLayout(3, 1));
        p.setBackground(Color.WHITE);
        p.add(statRow("Total Rooms", totalRooms, true));
        p.add(statRow("Available Rooms", availableRooms, true));
        p.add(statRow("Occupied", occupiedRooms, false));
        return p;
    }

    private JPanel statRow(String title, JLabel value, boolean line) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
                line ? BorderFactory.createMatteBorder(0, 0, 1, 0, UiUtil.LINE)
                     : BorderFactory.createEmptyBorder(),
                BorderFactory.createEmptyBorder(8, 16, 6, 10)));
        JLabel t = new JLabel(title);
        t.setFont(UiUtil.font(Font.PLAIN, 13));
        t.setForeground(UiUtil.TEXT_GRAY);
        value.setFont(UiUtil.font(Font.BOLD, 26));
        row.add(t, BorderLayout.NORTH);
        row.add(value, BorderLayout.CENTER);
        return row;
    }

    private JComponent reservationsBody() {
        JPanel p = new JPanel(new GridLayout(1, 3, 28, 0));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));
        p.add(box("Active<br>Reservation", activeReservations));
        p.add(box("Departing<br>&nbsp;", departing));
        p.add(box("Cancelled<br>&nbsp;", cancelled));
        return p;
    }

    private JPanel box(String htmlTitle, JLabel value) {
        JPanel b = new JPanel(new BorderLayout(0, 4));
        b.setBackground(UiUtil.BOX_BG);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiUtil.BORDER),
                BorderFactory.createEmptyBorder(10, 8, 12, 8)));
        JLabel t = new JLabel("<html><div style='text-align:center'>" + htmlTitle + "</div></html>",
                SwingConstants.CENTER);
        t.setFont(UiUtil.font(Font.BOLD, 14));
        t.setForeground(UiUtil.TEXT_GRAY);
        value.setHorizontalAlignment(SwingConstants.CENTER);
        value.setFont(UiUtil.font(Font.BOLD, 30));
        b.add(t, BorderLayout.NORTH);
        b.add(value, BorderLayout.CENTER);
        return b;
    }

    private JComponent requestsBody() {
        JLabel hint = new JLabel("No requests or reports yet.", SwingConstants.CENTER);
        hint.setForeground(new Color(0xB0B0B0));
        hint.setBorder(BorderFactory.createEmptyBorder(24, 8, 8, 8));
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.add(hint, BorderLayout.NORTH);
        return p;
    }

    private JComponent arrivalsBody() {
        JTable table = new JTable(arrivalsModel);
        TableStyler.style(table, false, false, 30);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiUtil.TABLE_BG);

        TitledBorder tb = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UiUtil.BORDER), "Upcoming Arrivals");
        tb.setTitleFont(UiUtil.font(Font.PLAIN, 11));
        tb.setTitleColor(UiUtil.TEXT_GRAY);

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UiUtil.TABLE_BG);
        p.setBorder(BorderFactory.createCompoundBorder(tb, BorderFactory.createEmptyBorder(2, 6, 6, 6)));
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private static JLabel number() {
        JLabel l = new JLabel("0");
        l.setForeground(Color.BLACK);
        return l;
    }

    private void updateGreeting() {
        int hour = LocalTime.now().getHour();
        greeting.setText(hour < 12 ? "Good Morning" : hour < 18 ? "Good Afternoon" : "Good Evening");
    }

    private void refresh() {
        int available = 0;
        int occupied = 0;
        for (Room r : store.getRooms()) {
            String s = store.getRoomStatus(r);
            if (s.equals("Available")) {
                available++;
            } else if (s.equals("Occupied")) {
                occupied++;
            }
        }

        int active = 0;
        List<Reservation> arrivals = new ArrayList<>();
        for (Reservation r : store.getReservations()) {
            if (r.isActive()) {
                active++;
            }
            if (r.getStatus() == ReservationStatus.BOOKED || r.getStatus() == ReservationStatus.CONFIRMED) {
                arrivals.add(r);
            }
        }

        totalRooms.setText(String.valueOf(store.getRooms().size()));
        availableRooms.setText(String.valueOf(available));
        occupiedRooms.setText(String.valueOf(occupied));
        activeReservations.setText(String.valueOf(active));
        departing.setText(String.valueOf(store.countDeparting()));
        cancelled.setText(String.valueOf(store.countByStatus(ReservationStatus.CANCELLED)));

        arrivals.sort(Comparator.comparing(Reservation::getCheckIn));
        arrivalsModel.setRowCount(0);
        for (Reservation r : arrivals) {
            arrivalsModel.addRow(new Object[] { r.getId(), r.getGuestName(), r.getRoom(),
                    UiUtil.checkInText(r.getCheckIn()), r.getNights() });
        }
    }
}
