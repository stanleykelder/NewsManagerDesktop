/**
 * 
 */
package application.utils.exceptions;

/**
 * @author agonzalez
 *
 */
@SuppressWarnings("serial")
public class ErrorMalFormedNews extends Exception {

	/**
	 * 
	 */
	public ErrorMalFormedNews() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public ErrorMalFormedNews(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public ErrorMalFormedNews(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ErrorMalFormedNews(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ErrorMalFormedNews(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
