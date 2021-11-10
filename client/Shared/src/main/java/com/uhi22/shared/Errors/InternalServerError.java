/** The InternalServerError exception is thrown when something does not execute properly on the server
 *  while responding to a request given by the user.  This helps obscure exactly what went wrong to prevent reverse engineering.
 *  It also allows the user to know that the problem with their request is not from the client action, but from something that
 *  the server did not handle properly
 *
 *  This supports principles of Confidentiality, Integrity, and Availability of data
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.shared.Errors;

public class InternalServerError extends Throwable {

    private String message;     // Contains the message that will be included in the response body

    /**
     * Constructor to define the specific error message associated with this exception
     */
    public InternalServerError() {
        this.message = "Internal Server Error";
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
