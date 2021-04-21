/** When a user logs into the server, the login request sent from the client to the server
 *  must include the user's username and password.  If the login succeeds, the server
 *  will generate a unique authorization token string for the user and return it to the client.
 *  Subsequent requests sent from the client to the server will include the auth token so the server
 *  can determine which user is making the request.  This allows non-login requests to be made without
 *  having to include the user's credentials, thus reucing the likelihood that a hacker will intercept them.
 *  For this scheme to work, the server will store auth tokens in its database and record which user
 *  each token belongs to.  Also to protect against the possibility that a hacker might intercept
 *  a user's auth token, it is important that each new login request generate and return a unique auth token.
 *  It should also be possible for the same user to be loggied in from multiple clients at the same time.
 *  This means that the same user to be logged in from multiple clients at the same time, which means
 *  that the same user could have multiple active auth tokens simultaneously
 *
 *  An auth token should be included in the HTTP "Authorization" request header for all requests that require an Auth token
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Model;

public class AuthorizationToken {

    private String authKey; // Randomly generated authentication token string
    private String userID; // userID associated with the auth token

    /**
     * Empty constructor marking the class for public use
     */
    public AuthorizationToken() {}

    /** Getter for the authKey value
     *
     * @return the authKey for the Auth token
     */
    public String getAuthKey() {
        return authKey;
    }

    /** Getter for the userID value
     *
     * @return the userID for the Auth token
     */
    public String getUserID() {
        return userID;
    }

    /** Setter for the authKey value
     *
     * @param authKey pass in the authKey value and set the authtoken class's authKey to the parameter
     */
    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    /** Setter for the userID value
     *
     * @param userID pass in the userID value and set the auth token class's userID to the parameter
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }
}
