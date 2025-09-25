package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceImplTest {

    private TicketService ticketService;
    private TicketPaymentService paymentService;
    private SeatReservationService reservationService;

    @BeforeEach
    void setUp() {
        paymentService = mock(TicketPaymentService.class);
        reservationService = mock(SeatReservationService.class);
        ticketService = new TicketServiceImpl(paymentService, reservationService);
    }


    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        @Test
        @DisplayName("Should process valid ticket purchase successfully")
        void shouldProcessValidTicketPurchaseSuccessfully() {
            Long accountId = 123L;
            TicketTypeRequest[] requests = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
            };

            assertDoesNotThrow(() -> ticketService.purchaseTickets(accountId, requests));
            // Payment amount and seat count are validated in TicketCalculationUtils unit tests.
            // Here we verify that the orchestration calls the external services.
            verify(paymentService).makePayment(eq(accountId), anyInt());
            verify(reservationService).reserveSeat(eq(accountId), anyInt());
        }

        @Test
        @DisplayName("Should handle multiple ticket requests of the same type")
        void shouldHandleMultipleTicketRequestsOfSameType() {
            Long accountId = 123L;
            TicketTypeRequest[] requests = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)
            };

            assertDoesNotThrow(() -> ticketService.purchaseTickets(accountId, requests));
            verify(paymentService).makePayment(eq(accountId), anyInt());
            verify(reservationService).reserveSeat(eq(accountId), anyInt());
        }
    }


}