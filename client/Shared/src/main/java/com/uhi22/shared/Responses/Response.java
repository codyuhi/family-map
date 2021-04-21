/** The Response class is a parent class to several types of responses that can be returned
 *  as a result of operations that are triggered when the client hits an endpoint on the server with a request
 *
 *  Response classes are important to define based on the different operations that the server supports.
 *  The client expects different schemas for data based on operation, so modularizing the types of responses
 *  allows for organization in how requests are responded to
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.shared.Responses;

public class Response {

    private String message;     // This string holds the message of the response for if
    // the operation has a message associated with the result of the operation
    private boolean success;    // This boolean denotes whether the operation was successful or not

    /**
     * Empty constructor marking the class for public use
     */
    public Response() {}

    /**
     * Getter for the message
     * @return  the message for the Response
     */
    public String getMessage() {
        return message;
    }

    /**
     * Getter for the success status
     * @return  the success status
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     * Setter for the message
     * @param message   pass in the message value and set the message to the parameter
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Setter for the success status
     * @param success   pass in the success value and set the success status to the parameter
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
