/** The LoginService class contains a login method which performs the business logic for
 * logging a user in when the login endpoint is hit
 *
 * Logging in allows a user to authenticate their identity such that they can prove authorization to access resources
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.familymapserver.Service;

import com.uhi22.familymapserver.DataAccess.AuthTokenDao;
import com.uhi22.familymapserver.DataAccess.Database;
import com.uhi22.familymapserver.DataAccess.PersonDao;
import com.uhi22.familymapserver.DataAccess.UserDao;
import com.uhi22.familymapserver.Errors.DataAccessException;
import com.uhi22.familymapserver.Errors.InternalServerError;
import com.uhi22.familymapserver.Errors.RequestPropertyInvalidValue;
import com.uhi22.familymapserver.Errors.RequestPropertyMissingValue;
import com.uhi22.shared.model.AuthorizationToken;
import com.uhi22.shared.Requests.LoginRequest;
import com.uhi22.shared.Responses.LoginResponse;
import com.uhi22.familymapserver.Util.RandomUtil;

import java.sql.Connection;
import java.util.UUID;

public class LoginService {

    /**
     * The db variable contains a Database instance which allows for data access to the persistent SQLite database
     */
    private static Database db;

    /**
     * Empty constructor
     */
    public LoginService() {}

    /** The login method performs the business logic for logging a user in
     *  The method is called when the login endpoint is hit by a request from the client
     *
     *  The business logic for this service is as follows:
     *   - Login the user
     *   - Return an AuthenticationToken
     *
     * @param request   the request parameter is a member of the LoginRequest class,
     *                  and has the information that would be passed in JSON for the login operation
     *
     *                  The request Body will look as follows:
     *                  {
     *                      "userName":"susan",     // Non-empty String
     *                      "password":"mysecret"   // Non-empty String
     *                  }
     *
     * @return          A LoginResponse class member is returned,
     *                  which has the attributes that can be converted into the JSON that the client
     *                  expects from the login operation
     *
     *                  The response body will look as follows upon successful operation:
     *                  {
     *                      "authToken":"cf7a368f", // Non-empty auth token String
     *                      "userName":"susan",     // User name passed in with request
     *                      "personID":"39f9fe46",  // Non-empty String containing the Person ID of the
     *                                              // User's generated Person object
     *                      "success":"true"        // Boolean identifier
     *                  }
     *
     * @throws com.uhi22.familymapserver.Errors.RequestPropertyMissingValue   Throws missing value error if a value is missing in input
     * @throws com.uhi22.familymapserver.Errors.RequestPropertyInvalidValue   Throws invalid value error if a value is invalid
     * @throws com.uhi22.familymapserver.Errors.InternalServerError           Throws an internal server error if something went
     *                                              wrong on the server side during the operation
     *
     *                  The response body will look as follows upon a failed operation:
     *                  {
     *                      "message":"Description of the error",
     *                      "success":"false"       // Boolean identifier
     *                  }
     */
    public static LoginResponse login(LoginRequest request) throws RequestPropertyMissingValue, RequestPropertyInvalidValue, InternalServerError, DataAccessException {
//        Create the Response POJO and Database variables
        LoginResponse response = new LoginResponse();
//        If the given input is invalid, validateInput will throw an error send response to the client
        validateInput(request);
        db = new Database();

//        Initialize to comboFound to false for fail-safe default permission design
        boolean comboFound = false;
        String personID = null;
        AuthorizationToken authToken = new AuthorizationToken();
        authToken.setAuthKey(UUID.randomUUID().toString());
        authToken.setUserID(null);

        try {
//            Hash the password and compare it with the database contents
            String pWord = RandomUtil.generateHash(request.getPassword());
            Connection conn = db.openConnection();
            UserDao uDao = new UserDao(conn);
//            If the user/pass combo is found, this will be true
            comboFound = uDao.loginUser(request.getUserName(), pWord);
            authToken.setUserID(uDao.getUserID(request.getUserName()));
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
            throw new DataAccessException(e.getMessage());
        }

        if(comboFound) {
//            If the combo was found, insert a new row to the authToken table and generate an authToken for the response
            System.out.println("Correct Username/Password combination found");
            try {
                Connection conn = db.openConnection();
                PersonDao pDao = new PersonDao(conn);
                personID = pDao.getPersonIDByUserID(authToken.getUserID());
                AuthTokenDao aDao = new AuthTokenDao(conn);
                aDao.insertAuthToken(UUID.randomUUID().toString(),authToken);
                db.closeConnection(true);
            } catch (DataAccessException e) {
                db.closeConnection(false);
                throw new DataAccessException(e.getMessage());
            }
        } else {
            System.out.println("Username/Password combination not found!");
            throw new RequestPropertyInvalidValue();
        }
//        Define response values and send them back to the client
        response.setAuthToken(authToken.getAuthKey());
        response.setUserName(request.getUserName());
        response.setPersonID(personID);
        response.setSuccess(true);
        return response;
    }

    /**
     * validateInput performs input validation before any changes are allowed on the database
     * This will filter any potentially dangerous or crash-causing inputs from being allowed in methods that change the database
     *
     * If either the username or password was not included in the request body, send an error response to the client
     *
     * @param request contains the request data, which has both the username and password
     * @throws RequestPropertyMissingValue occurs if either the username or password is not included in the request
     */
    private static void validateInput(LoginRequest request) throws RequestPropertyMissingValue {
//        If either the username or password was not included in the request body, send an error response to the client
        if(request.getUserName() == null ||
        request.getPassword() == null) {
            throw new RequestPropertyMissingValue();
        }
    }
}
