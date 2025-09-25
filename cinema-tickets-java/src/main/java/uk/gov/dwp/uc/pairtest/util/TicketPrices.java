package uk.gov.dwp.uc.pairtest.util;

/**
 * Simple constants for ticket prices to allow usage like `TicketPrices.ADULT`.
 */
public final class TicketPrices {
    public static final int ADULT = 25;
    public static final int CHILD = 15;
    public static final int INFANT = 0;

    private TicketPrices() {
        // utility
    }
}
