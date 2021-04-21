/** The ClearService class contains a clear method which performs the business logic for
 *  clearing out the data that exists in the database
 *
 * Clearing allows for the wiping of database contents to easily allow for fresh starts and testing
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Service;

import DataAccess.*;
import Errors.DataAccessException;
import Errors.InternalServerError;
import Responses.Response;

import java.sql.Connection;

public class ClearService {

    /**
     * The db variable contains a Database instance which allows for data access to the persistent SQLite database
     */
    private static Database db;

    /**
     * Empty constructor
     */
    public ClearService() {}

    /** The clear method performs the business logic for clearing the data that is stored in the database
     *  The method is called when the clear endpoint is hit by a request from the client or another class on the server
     *  There are no input parameters for this method because it unambiguously clears everything in the database
     *
     *  The business logic for this service is as follows:
     *   - Delete all data from the database, including user accounts, auth tokens, and generated person and event data
     *
     * @return      The results of the clear operation are returned in the expected JSON format
     *
     *              The response body will look as follows upon successful operation:
     *              {
     *                  "message":"Clear succeeded",
     *                  "success":"true"    // Boolean identifier
     *              }
     *
     * @throws Errors.InternalServerError   Throws internal server error if something
     *                                      went wrong on the server side during operation execution
     *
     *                                      The response body will look as follows upon a failed operation:
     *                                      {
     *                                          "message":"Description of message",
     *                                          "success":"false"   // Boolean identifier
     *                                      }
     */
    public static Response clear() throws InternalServerError, DataAccessException {
        System.out.println("The clear service was triggered!");
        Response response = new Response();
        db = new Database();
        try {
//            Open the database connection,
//            Create UserDao, PersonDao, AuthTokenDao, and EventDao POJOs
//            Call the clear method in each of them
//            Exit gracefully after everything is successfully cleared
            Connection conn = db.openConnection();
            UserDao uDao = new UserDao(conn);
            uDao.clearUsers();
            PersonDao pDao = new PersonDao(conn);
            pDao.clearPersons();
            AuthTokenDao aDao = new AuthTokenDao(conn);
            aDao.clearAuthTokens();
            EventDao eDao = new EventDao(conn);
            eDao.clearEvents();
            db.closeConnection(true);
        } catch (DataAccessException e) {
//            If something goes wrong while clearing the database,
//            throw an error so the response can be adjusted
            System.out.println("The clear service failed to clear the database");
            db.closeConnection(false);
            throw new InternalServerError();
        }
        response.setMessage("Clear succeeded");
        response.setSuccess(true);
        return response;
    }
}
