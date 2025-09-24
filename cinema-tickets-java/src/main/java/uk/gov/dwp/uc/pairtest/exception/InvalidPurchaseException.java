package uk.gov.dwp.uc.pairtest.exception;

/**
 * Exception thrown when an invalid ticket purchase request is made.
 */
public class InvalidPurchaseException extends RuntimeException {

	/**
	 * Constructs a new InvalidPurchaseException with {@code null} as its
	 * detail message.
	 */
	public InvalidPurchaseException() {
		super();
	}

	/**
	 * Constructs a new InvalidPurchaseException with the specified detail message.
	 *
	 * @param message the detail message (which is saved for later retrieval
	 *                by the {@link #getMessage()} method)
	 */
	public InvalidPurchaseException(String message) {
		super(message);
	}

	/**
	 * Constructs a new InvalidPurchaseException with the specified detail message and cause.
	 *
	 * @param message the detail message (which is saved for later retrieval
	 *                by the {@link #getMessage()} method)
	 * @param cause   the cause (which is saved for later retrieval by the
	 *                {@link #getCause()} method)
	 */
	public InvalidPurchaseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new InvalidPurchaseException with the specified cause.
	 *
	 * @param cause the cause (which is saved for later retrieval by the
	 *              {@link #getCause()} method)
	 */
	public InvalidPurchaseException(Throwable cause) {
		super(cause);
	}
}
