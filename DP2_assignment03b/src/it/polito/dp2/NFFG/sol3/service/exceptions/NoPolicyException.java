package it.polito.dp2.NFFG.sol3.service.exceptions;

public class NoPolicyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoPolicyException() {
	}

	public NoPolicyException(String message) {
		super(message);
	}

	public NoPolicyException(Throwable cause) {
		super(cause);
	}

	public NoPolicyException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoPolicyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
