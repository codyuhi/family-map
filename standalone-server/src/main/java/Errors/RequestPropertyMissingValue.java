/** The RequestPropertyMissingValue exception is thrown when the request body is missing a value that
 *  is necessary to respond to the request
 *
 *  This supports principles of Confidentiality, Integrity, and Availability of data
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Errors;

public class RequestPropertyMissingValue extends Throwable {

    private String message;     // Contains the message that will be included in the response body

    /**
     * Constructor to define the specific error message associated with this exception
     */
    public RequestPropertyMissingValue() {
        this.message = "Missing Value Error";
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
