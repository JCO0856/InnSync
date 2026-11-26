package innsync.ui;

import java.awt.Color;
import java.awt.Font;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// Shared colors, fonts and formatting for the InnSync UI. 
public final class UiUtil {

    //colors (taken from the InnSync mockup) 
    public static final Color BLUE = new Color(0x0033CC);
    public static final Color DARK = new Color(0x2D2A29);
    public static final Color NAV_ACTIVE = new Color(0x6E6E6E);
    public static final Color PILL = new Color(0xC5CCFF);
    public static final Color PRIMARY = new Color(0x0047AB);
    public static final Color BORDER = new Color(0xCFCFCF);
    public static final Color LINE = new Color(0x9E9E9E);
    public static final Color TEXT_GRAY = new Color(0x6B6B6B);
    public static final Color BOX_BG = new Color(0xFAF8F8);
    public static final Color STAT_BG = new Color(0xEBEBEB);
    public static final Color TABLE_BG = new Color(0xFBFAFA);
    public static final Color VIEW = new Color(0x0B2E3F);
    public static final Color EDIT = new Color(0x3F7D0A);
    public static final Color DELETE = new Color(0xFF3B3B);

    //hotel policy: standard check-in / check-out time shown in the tables 
    public static final LocalTime CHECK_IN_TIME = LocalTime.of(11, 0);
    public static final LocalTime CHECK_OUT_TIME = LocalTime.of(11, 0);

    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("hh:mma", Locale.ENGLISH);

    private UiUtil() { }

    public static Font font(int style, int size) {
        return new Font("SansSerif", style, size);
    }

    //Formats an amount as Philippine peso, -"P 1,200.00".
    public static String money(double amount) {
        return String.format(Locale.US, "\u20B1 %,.2f", amount);
    }

    // sample - "2026-11-23, 11:00AM" 
    public static String checkInText(LocalDate d) {
        return d.format(DATE) + ", " + CHECK_IN_TIME.format(TIME);
    }

    public static String checkOutText(LocalDate d) {
        return d.format(DATE) + ", " + CHECK_OUT_TIME.format(TIME);
    }
}
