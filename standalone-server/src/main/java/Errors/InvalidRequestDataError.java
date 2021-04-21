/** The InvalidRequestDataError exception is thrown when the load request is performed and invalid data
 *  is passed in the request body
 *
 *  This supports principles of Confidentiality, Integrity, and Availability of data
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Errors;

public class InvalidRequestDataError extends Throwable {

    private String message;     // Contains the message that will be included in the response body

    /**
     * Constructor to define the specific error message associated with this exception
     */
    public InvalidRequestDataError() {
        this.message = "Invalid Request Data";
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
