/** The InvalidPersonIDError exception is thrown when the person/[personID] request passes a personID
 *  that does not exist in the database
 *
 *  This supports principles of Confidentiality, Integrity, and Availability of data
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Errors;

public class InvalidPersonIDError extends Throwable {

    private String message;     // Contains the message that will be included in the response body

    /**
     * Constructor to define the specific error message associated with this exception
     */
    public InvalidPersonIDError() {
        this.message = "Invalid Person ID";
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
