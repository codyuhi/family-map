/**
 * @author Cody Uhi
 * @version 1.0.0
 */

package Responses;

public class LoginResponse extends Response {

    private String authToken;   // the authToken is the authToken that is randomly generated from the login operation
    private String userName;    // the username is the username that has been successfully authenticated from the login operation
    private String personID;    // the personID refers to the Person that is associated with the newly logged in user

    /**
     * Empty constructor marking the class for public use
     */
    public LoginResponse() {}

    /**
     * Getter for the authToken
     * @return  the authToken for the LoginResponse
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Getter for the username
     * @return  the username for the LoginResponse
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Getter for the personID
     * @return  the personID for the LoginResponse
     */
    public String getPersonID() {
        return personID;
    }

    /**
     * Setter for the authToken
     * @param authToken pass in the authToken value and set the authToken to the parameter
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * Setter for the username
     * @param userName  pass in the username and set the username to the parameter
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Setter for the personID
     * @param personID  pass in the personID and set the personID to the parameter
     */
    public void setPersonID(String personID) {
        this.personID = personID;
    }
}
