package uk.gov.dwp.uc.pairtest.util;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

// Utility class for validating ticket purchase requests
public final class TicketPurchaseValidator {
    private TicketPurchaseValidator() {}

    public static void validate(Long accountId, TicketTypeRequest... ticketTypeRequests) {
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

        int maxTickets = TicketingConfig.getMaxTickets();

        // Validate against maximum ticket limit
        if (totalTickets > maxTickets) {
            throw new InvalidPurchaseException(MessageProvider.getMessage("error.max.tickets", maxTickets));
        }

        // Ensure at least one adult ticket is purchased
        if (adultCount == 0) {
            throw new InvalidPurchaseException(MessageProvider.getMessage("error.adult.required"));
        }

        // Ensure infants do not exceed adult tickets
        if (infantCount > adultCount) {
            throw new InvalidPurchaseException(MessageProvider.getMessage("error.infant.exceeds.adult"));
        }
    }
}
