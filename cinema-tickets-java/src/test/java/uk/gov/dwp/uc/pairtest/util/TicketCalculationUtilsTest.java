package uk.gov.dwp.uc.pairtest.util;

import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TicketCalculationUtilsTest {

    @Test
    void shouldCalculateCorrectPriceForAdultTickets() {
        int amount = TicketCalculationUtils.calculateTotalAmount(
            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2)
        );

        assertEquals(50, amount); // 2 adults * £25 each
    }

    @Test
    void shouldCalculateCorrectPriceForMixedTickets() {
        int amount = TicketCalculationUtils.calculateTotalAmount(
            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
            new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
            new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
        );

        assertEquals(80, amount); // (2 adults * £25) + (2 children * £15) + (1 infant * £0)
    }

    @Test
    void shouldNotCountInfantForSeat() {
        int seats = TicketCalculationUtils.calculateTotalSeats(
            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
            new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
            new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3)
        );

        // 2 adults + 1 child = 3 seats (infants not counted)
        assertEquals(3, seats);
    }
    

    @Test
    void shouldCountCorrectSeatsWithMultipleRequestsSameType() {
        int seats = TicketCalculationUtils.calculateTotalSeats(
            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
            new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)
        );

        assertEquals(4, seats); // 1+2 adults +1 child
    }

}
