package innsync.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JPanel;

/** The whole window content: header with navigation on top, the selected page below. */
public class AppPanel extends JPanel {

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final HeaderPanel header;

    public AppPanel() {
        super(new BorderLayout());

        String[] pages = { "Dashboard", "Reservations", "Rooms", "Record", "Accounting",
                "Calendar", "Settings" };

        content.add(new DashboardPanel(), "Dashboard");
        content.add(new ReservationsPanel(), "Reservations");
        content.add(new RoomsPanel(), "Rooms");
        content.add(new PlaceholderPanel("Record"), "Record");
        content.add(new PlaceholderPanel("Accounting"), "Accounting");
        content.add(new PlaceholderPanel("Calendar"), "Calendar");
        content.add(new PlaceholderPanel("Settings"), "Settings");

        header = new HeaderPanel(pages, this::showPage);
        add(header, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);

        showPage("Dashboard");
    }

    public void showPage(String name) {
        cards.show(content, name);
        header.select(name);
    }
}
