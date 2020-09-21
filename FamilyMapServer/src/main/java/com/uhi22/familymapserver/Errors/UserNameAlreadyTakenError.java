/** The UserNameAlreadyTakenError exception is thrown when the register request is performed
 *  and attempts to create a new user with a username that is already taken.  This prevents
 *  from duplicates occuring in the database for user entity instances
 *
 *  This supports principles of Confidentiality, Integrity, and Availability of data
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.familymapserver.Errors;

public class UserNameAlreadyTakenError extends Throwable {

    private String message;     // Contains the message that will be included in the response body

    /**
     * Constructor to define the specific error message associated with this exception
     */
    public UserNameAlreadyTakenError() {
        this.message = "Username Already Taken";
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
