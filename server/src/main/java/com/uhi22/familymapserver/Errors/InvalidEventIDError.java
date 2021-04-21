/** The InvalidEventIDError exception is thrown when the event/[eventID] request passes an eventID
 *  that does not exist in the Event table in the database
 *
 *  This supports principles of Confidentiality, Integrity, and Availability of data
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.familymapserver.Errors;

public class InvalidEventIDError extends Throwable {

    private String message;     // Contains the message that will be included in the response body

    /**
     * Constructor to define the specific error message associated with this exception
     */
    public InvalidEventIDError() {
        this.message = "Invalid Event ID";
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
