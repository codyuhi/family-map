/** The RequestedPersonDoesNotBelongToThisUser exception is thrown when the authenticated user is not authorized to access the person
 *  that he/she is attempting to access.  This will ultimately return a 401 response code
 *
 *  This supports principles of Confidentiality, Integrity, and Availability of data
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Errors;

public class RequestedPersonDoesNotBelongToThisUser extends Throwable {

    private String message;

    /**
     * Constructor to define the specific error message associated with this exception
     */
    public RequestedPersonDoesNotBelongToThisUser() {
        this.message = "Requested Person does not belong to this User";
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
