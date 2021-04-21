/** The InvalidGenerationsError exception is thrown when the fill request asks for an invalid number of generations
 *  to be created in the request
 *
 *  This supports principles of Confidentiality, Integrity, and Availability of data
 *
 * @author Cody Uhi
 * @version 1.0.0
 */
package Errors;

public class InvalidGenerationsError extends Throwable {

    private String message;     // Contains the message that will be included in the response body

    /**
     * Constructor to define the specific error message associated with this exception
     */
    public InvalidGenerationsError() {
        this.message = "Invalid Generations Value Provided";
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
