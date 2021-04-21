/** The InvalidAuthTokenError exception is thrown when the user attempts to access a resource and is passing an invalid auth
 *  token as a request header.  This may be used to redirect the client to the login page instead of fulfilling the request
 *
 *  This supports principles of Confidentiality, Integrity, and Availability of data
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Errors;

public class InvalidAuthTokenError extends Throwable {

    private String message;     // Contains the message that will be included in the response body

    /**
     * Constructor to define the specific error message associated with this exception
     */
    public InvalidAuthTokenError() {
        this.message = "Invalid Auth Token";
    }

    /**
     * Getter for the message
     * @return  the message for this specific exception
     */
    @Override
    public String getMessage() {
        return message;
    }
}
