/** The RequestedEventDoesNotBelongToThisUser exception is thrown when the authenticated user is not authorized to access the event
 *  that he/she is attempting to access.  This will ultimately return a 401 response code
 *
 *  This supports principles of Confidentiality, Integrity, and Availability of data
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Errors;

public class RequestedEventDoesNotBelongToThisUser extends Throwable {

    private String message;     // Contains the message that will be included in the response body

    /**
     * Constructor to define the specific error message associated with this exception
     */
    public RequestedEventDoesNotBelongToThisUser() {
        this.message = "Requested Event does not belong to this User";
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
