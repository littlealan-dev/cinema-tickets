package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
// Utility class for fetching i18n messages
import uk.gov.dwp.uc.pairtest.util.MessageProvider;
import uk.gov.dwp.uc.pairtest.util.TicketCalculationUtils;
import uk.gov.dwp.uc.pairtest.util.TicketPurchaseValidator;


public class TicketServiceImpl implements TicketService {

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
        TicketPurchaseValidator.validate(accountId, ticketTypeRequests);

        // Calculate total amount and seats to reserve
        int totalAmount = TicketCalculationUtils.calculateTotalAmount(ticketTypeRequests);
        int totalSeats = TicketCalculationUtils.calculateTotalSeats(ticketTypeRequests);

        // Make payment and reserve seats
        ticketPaymentService.makePayment(accountId, totalAmount);
        seatReservationService.reserveSeat(accountId, totalSeats);
    }

}
