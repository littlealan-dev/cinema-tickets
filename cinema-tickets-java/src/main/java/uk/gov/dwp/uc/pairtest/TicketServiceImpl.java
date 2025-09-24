package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
// Utility class for fetching i18n messages
import uk.gov.dwp.uc.pairtest.util.MessageProvider;

public class TicketServiceImpl implements TicketService {
    private static final int MAX_TICKETS = 25;
    private static final int ADULT_TICKET_PRICE = 25;
    private static final int CHILD_TICKET_PRICE = 15;
    private static final int INFANT_TICKET_PRICE = 0;

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl() {
        this.ticketPaymentService = new TicketPaymentServiceImpl();
        this.seatReservationService = new SeatReservationServiceImpl();
    }

    // Constructor for testing with mock services
    TicketServiceImpl(TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        validatePurchaseRequest(accountId, ticketTypeRequests);

        // Calculate total amount and seats to reserve
        int totalAmount = calculateTotalAmount(ticketTypeRequests);
        int totalSeats = calculateTotalSeats(ticketTypeRequests);

        // Make payment and reserve seats
        ticketPaymentService.makePayment(accountId, totalAmount);
        seatReservationService.reserveSeat(accountId, totalSeats);
    }

    // Helper methods for validation and calculations
    private void validatePurchaseRequest(Long accountId, TicketTypeRequest... ticketTypeRequests) {
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException(MessageProvider.getMessage("error.invalid.accountId"));
        }

        if (ticketTypeRequests == null || ticketTypeRequests.length == 0) {
            throw new InvalidPurchaseException(MessageProvider.getMessage("error.no.tickets"));
        }

        int totalTickets = 0;
        int adultCount = 0;
        int infantCount = 0;

        for (TicketTypeRequest request : ticketTypeRequests) {
            if (request == null || request.getNoOfTickets() <= 0) {
                throw new InvalidPurchaseException(MessageProvider.getMessage("error.invalid.ticket.request"));
            }

            totalTickets += request.getNoOfTickets();
            
            switch (request.getTicketType()) {
                case ADULT:
                    adultCount += request.getNoOfTickets();
                    break;
                case CHILD:
                    break;
                case INFANT:
                    infantCount += request.getNoOfTickets();
                    break;
            }
        }

        if (totalTickets > MAX_TICKETS) {
            throw new InvalidPurchaseException(MessageProvider.getMessage("error.max.tickets", MAX_TICKETS));
        }

        if (adultCount == 0) {
            throw new InvalidPurchaseException(MessageProvider.getMessage("error.adult.required"));
        }

        if (infantCount > adultCount) {
            throw new InvalidPurchaseException(MessageProvider.getMessage("error.infant.exceeds.adult"));
        }
    }

    // Calculate total amount based on ticket types
    private int calculateTotalAmount(TicketTypeRequest... ticketTypeRequests) {
        int totalAmount = 0;
        
        for (TicketTypeRequest request : ticketTypeRequests) {
            switch (request.getTicketType()) {
                case ADULT:
                    totalAmount += request.getNoOfTickets() * ADULT_TICKET_PRICE;
                    break;
                case CHILD:
                    totalAmount += request.getNoOfTickets() * CHILD_TICKET_PRICE;
                    break;
                case INFANT:
                    totalAmount += request.getNoOfTickets() * INFANT_TICKET_PRICE;
                    break;
            }
        }

        return totalAmount;
    }

    // Calculate total seats to reserve (infants do not require a seat)
    private int calculateTotalSeats(TicketTypeRequest... ticketTypeRequests) {
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
