/** The LoginRequest class contains information that was sent by the client in the request body
 *  It is used in the LoginService class's login method to provide the necessary data
 *  for the login operation to successfully be executed
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Requests;

public class LoginRequest {

    private String userName;    // Unique user name (non-empty string)
    private String password;    // User's password (non-empty string)

    /**
     * Empty constructor marking the class for public use
     */
    public LoginRequest() {}

    /** Getter for the username value
     *
     * @return the username of the LoginRequest
     */
    public String getUserName() {
        return userName;
    }

    /** Getter for the password value
     *
     * @return the password of the LoginRequest
     */
    public String getPassword() {
        return password;
    }

    /** Setter for the username value
     *
     * @param userName pass in the username value and set the user class's username to the parameter
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /** Setter for the password value
     *
     * @param password pass in the password value and set the user class's password to the parameter
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
