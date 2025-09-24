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
    @DisplayName("Account Validation Tests")
    class AccountValidationTests {
        @Test
        @DisplayName("Should throw exception when account ID is null")
        void shouldThrowExceptionWhenAccountIdIsNull() {
            TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(null, request));
            assertTrue(exception.getMessage().contains("Invalid account ID"));
        }

        @Test
        @DisplayName("Should throw exception when account ID is zero or negative")
        void shouldThrowExceptionWhenAccountIdIsZeroOrNegative() {
            TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
            assertAll(
                () -> assertThrows(InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(0L, request)),
                () -> assertThrows(InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(-1L, request))
            );
        }
    }

    @Nested
    @DisplayName("Ticket Request Validation Tests")
    class TicketRequestValidationTests {
        @Test
        @DisplayName("Should throw exception when ticket requests array is null")
        void shouldThrowExceptionWhenTicketRequestsIsNull() {
            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, (TicketTypeRequest[]) null));
            assertTrue(exception.getMessage().contains("No tickets requested"));
        }

        @Test
        @DisplayName("Should throw exception when ticket requests array is empty")
        void shouldThrowExceptionWhenTicketRequestsIsEmpty() {
            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L));
            assertTrue(exception.getMessage().contains("No tickets requested"));
        }

        @Test
        @DisplayName("Should throw exception when individual ticket request is null")
        void shouldThrowExceptionWhenIndividualRequestIsNull() {
            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, (TicketTypeRequest) null));
            assertTrue(exception.getMessage().contains("Invalid ticket request"));
        }
    }

    @Nested
    @DisplayName("Ticket Count Validation Tests")
    class TicketCountValidationTests {
        @Test
        @DisplayName("Should throw exception when total tickets exceed maximum limit")
        void shouldThrowExceptionWhenTotalTicketsExceedLimit() {
            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, 
                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26)));
            assertTrue(exception.getMessage().contains("Cannot purchase more than 25 tickets"));
        }

        @ParameterizedTest(name = "Total tickets = {0}")
        @ValueSource(ints = {1, 10, 25})
        @DisplayName("Should accept valid total ticket counts")
        void shouldAcceptValidTotalTicketCounts(int ticketCount) {
            assertDoesNotThrow(() -> 
                ticketService.purchaseTickets(1L, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, ticketCount)));
        }
    }

    @Nested
    @DisplayName("Adult Ticket Requirement Tests")
    class AdultTicketRequirementTests {
        @Test
        @DisplayName("Should throw exception when no adult ticket is purchased")
        void shouldThrowExceptionWhenNoAdultTicket() {
            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L,
                    new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)));
            assertTrue(exception.getMessage().contains("adult ticket must be purchased"));
        }

        @Test
        @DisplayName("Should throw exception when number of infants exceeds adults")
        void shouldThrowExceptionWhenInfantsExceedAdults() {
            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L,
                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                    new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2)));
            assertTrue(exception.getMessage().contains("Number of infants cannot exceed number of adults"));
        }
    }

    @Nested
    @DisplayName("Price Calculation Tests")
    class PriceCalculationTests {
        @Test
        @DisplayName("Should calculate correct price for adult tickets only")
        void shouldCalculateCorrectPriceForAdultTickets() {
            ticketService.purchaseTickets(1L, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2));
            verify(paymentService).makePayment(1L, 50); // 2 * £25 = £50
        }

        @Test
        @DisplayName("Should calculate correct price for mixed tickets")
        void shouldCalculateCorrectPriceForMixedTickets() {
            ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));
            verify(paymentService).makePayment(1L, 80); // (2 * £25) + (2 * £15) + (1 * £0) = £80
        }

        @Test
        @DisplayName("Should not charge for infant tickets")
        void shouldNotChargeForInfantTickets() {
            ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));
            verify(paymentService).makePayment(1L, 25); // 1 * £25 = £25
        }
    }

    @Nested
    @DisplayName("Seat Reservation Tests")
    class SeatReservationTests {
        @Test
        @DisplayName("Should reserve seats for adults and children only")
        void shouldReserveSeatsForAdultsAndChildrenOnly() {
            ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2));
            verify(reservationService).reserveSeat(1L, 3); // 2 adults + 1 child = 3 seats
        }

        @Test
        @DisplayName("Should not reserve seats for infants")
        void shouldNotReserveSeatsForInfants() {
            ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));
            verify(reservationService).reserveSeat(1L, 1); // 1 adult only = 1 seat
        }

        @Test
        @DisplayName("Should reserve correct number of seats for multiple ticket types")
        void shouldReserveCorrectNumberOfSeatsForMultipleTicketTypes() {
            ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2));
            verify(reservationService).reserveSeat(1L, 5); // 3 adults + 2 children = 5 seats
        }
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
            verify(paymentService).makePayment(accountId, 80); // (2 * £25) + (2 * £15) = £80
            verify(reservationService).reserveSeat(accountId, 4); // 2 adults + 2 children = 4 seats
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
            verify(paymentService).makePayment(accountId, 80); // (2 * £25) + (2 * £15) = £80
            verify(reservationService).reserveSeat(accountId, 4); // 2 adults + 2 children = 4 seats
        }
    }


}