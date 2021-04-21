/** The RequestPropertyInvalidValue exception is thrown when a register or login request
 *  passed an invalid value in its body
 *
 *  This supports principles of Confidentiality, Integrity, and Availability of data
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Errors;

public class RequestPropertyInvalidValue extends Throwable {

    private String message;     // Contains the message that will be included in the response body

    /**
     * Constructor to define the specific error message associated with this exception
     */
    public RequestPropertyInvalidValue() {
        this.message = "Invalid Value Error";
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
