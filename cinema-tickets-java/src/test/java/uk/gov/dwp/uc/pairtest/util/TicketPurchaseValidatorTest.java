package uk.gov.dwp.uc.pairtest.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TicketPurchaseValidatorTest {

    @Nested
    @DisplayName("Account Validation Tests")
    class AccountValidationTests {
        @Test
        @DisplayName("Should throw exception when account ID is null")
        void shouldThrowExceptionWhenAccountIdIsNull() {
            TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> TicketPurchaseValidator.validate(null, request));
            assertTrue(exception.getMessage().contains("Invalid account ID"));
        }

        @Test
        @DisplayName("Should throw exception when account ID is zero or negative")
        void shouldThrowExceptionWhenAccountIdIsZeroOrNegative() {
            TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
            assertAll(
                () -> assertThrows(InvalidPurchaseException.class,
                    () -> TicketPurchaseValidator.validate(0L, request)),
                () -> assertThrows(InvalidPurchaseException.class,
                    () -> TicketPurchaseValidator.validate(-1L, request))
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
                () -> TicketPurchaseValidator.validate(1L, (TicketTypeRequest[]) null));
            assertTrue(exception.getMessage().contains("No tickets requested"));
        }

        @Test
        @DisplayName("Should throw exception when ticket requests array is empty")
        void shouldThrowExceptionWhenTicketRequestsIsEmpty() {
            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> TicketPurchaseValidator.validate(1L));
            assertTrue(exception.getMessage().contains("No tickets requested"));
        }

        @Test
        @DisplayName("Should throw exception when individual ticket request is null")
        void shouldThrowExceptionWhenIndividualRequestIsNull() {
            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> TicketPurchaseValidator.validate(1L, (TicketTypeRequest) null));
            assertTrue(exception.getMessage().contains("Invalid ticket request"));
        }
    }

    @Nested
    @DisplayName("Ticket Count Validation Tests")
    class TicketCountValidationTests {
        @Test
        @DisplayName("Should throw exception when total tickets exceed maximum limit")
        void shouldThrowExceptionWhenTotalTicketsExceedLimit() {
            int max = TicketingConfig.getMaxTickets();
            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> TicketPurchaseValidator.validate(1L,
                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, max + 1)));
            assertTrue(exception.getMessage().contains(String.valueOf(max)));
        }

        @ParameterizedTest(name = "Total tickets = {0}")
        @ValueSource(ints = {1, 10, 25})
        @DisplayName("Should accept valid total ticket counts")
        void shouldAcceptValidTotalTicketCounts(int ticketCount) {
            assertDoesNotThrow(() ->
                TicketPurchaseValidator.validate(1L, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, ticketCount)));
        }
    }

    @Nested
    @DisplayName("Adult Ticket Requirement Tests")
    class AdultTicketRequirementTests {
        @Test
        @DisplayName("Should throw exception when no adult ticket is purchased")
        void shouldThrowExceptionWhenNoAdultTicket() {
            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> TicketPurchaseValidator.validate(1L,
                    new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)));
            assertTrue(exception.getMessage().contains("adult ticket must be purchased"));
        }

        @Test
        @DisplayName("Should throw exception when number of infants exceeds adults")
        void shouldThrowExceptionWhenInfantsExceedAdults() {
            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> TicketPurchaseValidator.validate(1L,
                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                    new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2)));
            assertTrue(exception.getMessage().contains("Number of infants cannot exceed number of adults"));
        }
    }
}
