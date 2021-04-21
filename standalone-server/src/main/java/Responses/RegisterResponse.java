/** The RegisterResponse class is a child class of the Response class and is used to provide the new authToken, username, and personID for a newly created user instance
 *  The RegisterResponse class is used whenever the user hits the register endpoint to register a new user
 *
 *  The RegisterResponse class is important because it allows for an easily parsable schema that contains the
 *  expected attributes for a register request to be returned to the client
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Responses;

public class RegisterResponse extends Response {
    private String authToken;   // the authToken is the authToken that was generated randomly during the register operation
    private String userName;    // the username is the given username that was created for the newly registered user
    private String personID;    // the personID is the given personID that was generated randomly during the register operation

    /**
     * Empty constructor marking the class for public use
     */
    public RegisterResponse() {}

    /**
     * Getter for the authToken
     * @return  the authToken for the RegisterResponse
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Getter for the username
     * @return  the username for the RegisterResponse
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Getter for the personID
     * @return  the personID for the RegisterResponse
     */
    public String getPersonID() {
        return personID;
    }

    /**
     * Setter for the authToken
     * @param authToken     pass in the authToken value and set the authToken to the parameter
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * Setter for the username
     * @param userName      pass in the username value and set the username to the parameter
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Setter for the personID
     * @param personID      pass in the personID value and set the personID to the parameter
     */
    public void setPersonID(String personID) {
        this.personID = personID;
    }
}
