package uk.gov.dwp.uc.pairtest.util;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

/**
 * Utility methods for ticket calculations extracted for reuse.
 */
public final class TicketCalculationUtils {

    private TicketCalculationUtils() {
        // utility
    }

    //
    public static int calculateTotalAmount(TicketTypeRequest... ticketTypeRequests) {
        int totalAmount = 0;

        for (TicketTypeRequest request : ticketTypeRequests) {
            switch (request.getTicketType()) {
                case ADULT:
                    totalAmount += request.getNoOfTickets() * TicketPrices.ADULT;
                    break;
                case CHILD:
                    totalAmount += request.getNoOfTickets() * TicketPrices.CHILD;
                    break;
                case INFANT:
                    totalAmount += request.getNoOfTickets() * TicketPrices.INFANT;
                    break;
            }
        }

        return totalAmount;
    }

    // Calculate total seats needed (infants do not require a seat)
    public static int calculateTotalSeats(TicketTypeRequest... ticketTypeRequests) {
        int totalSeats = 0;

        for (TicketTypeRequest request : ticketTypeRequests) {
            switch (request.getTicketType()) {
                case ADULT:
                case CHILD:
                    totalSeats += request.getNoOfTickets();
                    break;
                case INFANT:
                    // Infants don't need seats
                    break;
            }
        }

        return totalSeats;
    }
}
