/** The GetPersonService class contains a getPerson method which performs the business logic
 *  for getting Person data for a single Person class member from the database
 *  The GetPersonService class also contains a getAllPersons method which performs the business logic
 *  for getting Person data for all Persons stored in the database related to the current user
 *
 *  Getting Person data from the database is important for allowing the client
 *  to gain information about specific Person class members so it can be processed client-side
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.familymapserver.Service;

import com.uhi22.familymapserver.DataAccess.AuthTokenDao;
import com.uhi22.familymapserver.DataAccess.Database;
import com.uhi22.familymapserver.DataAccess.PersonDao;
import com.uhi22.familymapserver.DataAccess.UserDao;
import com.uhi22.familymapserver.Errors.*;
import com.uhi22.shared.model.Person;
import com.uhi22.shared.model.User;
import com.uhi22.shared.Responses.AllPersonsResponse;
import com.uhi22.shared.Responses.PersonResponse;

import java.sql.Connection;
import java.util.ArrayList;

public class GetPersonService {

    /**
     * The db variable contains a Database instance which allows for data access to the persistent SQLite database
     */
    private static Database db;

    /**
     * Empty constructor marking the class for public use
     */
    public GetPersonService() {}

    /** The getPerson method performs the business logic for getting the data associated with a specific person
     *  The method is called when the person/[personID] endpoint is hit by a GET request from the user
     *
     *  The business logic for this service is as follows:
     *   - Return a single Person object with the specified ID
     *
     * @param personID  The personID parameter is a string
     *                  and has the personID value for the Person row that
     *                  is desired to be returned from the GET operation
     *
     * @param authToken The authToken that was included in the request header
     *
     * @return          A PersonResponse class member is returned,
     *                  which has the attributes that can be converted into the JSON that the client
     *                  expects from the get person operation
     *
     *                  The response body will look as follows upon successful operation:
     *                  {
     *                      "associatedUsername":"susan",   // Name of user account this person belongs to
     *                      "personID":"7255e93e",          // Person's unique ID
     *                      "firstName":"Stuart",           // Person's first name
     *                      "lastName":"Klocke",            // Person's last name
     *                      "gender":"m",                   // Person's gender
     *                      "fatherID":"7255e93e",          // ID of person's father [OPTIONAL, can be missing]
     *                      "motherID":"d3gz214j",          // ID of person's mother [OPTIONAL, can be missing]
     *                      "spouseID":"f42126c8",          // ID of person's spouse [OPTIONAL, can be missing]
     *                      "success":"true"                // Boolean identifier
     *                  }
     *
     * @throws InvalidAuthTokenError                    Throws invalid auth token error if the given authToken is not in the AuthorizationTokens table
     * @throws InvalidPersonIDError                     Throws invalid person id error if the given person id does not exist in the database
     * @throws RequestedPersonDoesNotBelongToThisUser   Throws requested person does not belong to this user if the given authToken is not associated with
     *                                                  the requested person's user
     * @throws InternalServerError                      Throws internal server error if something went wrong
     *                                                  on the server side during the operation
     *
     *                  The response body will look as follows upon failed operation:
     *                  {
     *                      "message":"Description of the error",
     *                      "success":"false"   // Boolean identifier
     *                  }
     */
    public static PersonResponse getPerson(String personID, String authToken) throws InvalidAuthTokenError,
            InvalidPersonIDError, RequestedPersonDoesNotBelongToThisUser, InternalServerError, DataAccessException {
        System.out.println("Getting Person with PersonID: " + personID + "\n");
        PersonResponse response = new PersonResponse();
//        If the given input is invalid, validateInput will throw an error send response to the client
        validateInput(personID, authToken);
        Person person = null;
        try {
//            Open the database connection
            Connection conn = db.openConnection();
//            Create the PersonDao POJO
            PersonDao pDao = new PersonDao(conn);
            person = pDao.getPerson(personID);
//            Gracefully exit
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
            throw new DataAccessException(e.getMessage());
        }
//        Define the response based on the Dao results
        response.setAssociatedUsername(person.getAssociatedUsername());
        response.setPersonID(person.getPersonID());
        response.setFirstName(person.getFirstName());
        response.setLastName(person.getLastName());
        response.setGender(person.getGender());
        response.setFatherID(person.getFatherID());
        response.setMotherID(person.getMotherID());
        response.setSpouseID(person.getSpouseID());
        response.setSuccess(true);
        return response;
    }

    /** The getAllPersons method performs the business logic for getting the data associated with
     *  all Persons that are owned by the currently logged in user
     *
     *  The business logic for this service is as follows:
     *   - Return all family members of the current user (determined by the provided authToken)
     *
     * @param authToken     the authToken parameter is a string containing the authToken passed in the request header
     *
     * @return              A AllPersonsResponse class member is returned,
     *                      which has the attributes that can be converted into the JSON that the client
     *                      expects from the GET /person operation
     *
     *                      The response body will look as follows upon successful operation:
     *                      {
     *                          "data":[/* Array of Person objects *],
     *                          "success":"true"    // Boolean identifier
     *                      }
     *
     * @throws InvalidAuthTokenError    Throws invalid auth token error if the given auth token can't be found in the AuthorizationTokens table
     * @throws InternalServerError      Throws internal server error if something went wrong
     *                                  on the server side during the operation
     *
     *                      The response body will look as follows upon failed operation:
     *                      {
     *                          "message":"Description of the error",
     *                          "success":"false"   // Boolean identifier
     *                      }
     */
    public static AllPersonsResponse getAllPersons(String authToken) throws InvalidAuthTokenError, InternalServerError, DataAccessException {
        System.out.println("Getting all Persons . . . ");
        AllPersonsResponse response = new AllPersonsResponse();
        validateAllInput(authToken);
        ArrayList<Person> persons = null;
//        Open the DB connection and get the ArrayList of all Events
        try {
            Connection conn = db.openConnection();
            AuthTokenDao aDao = new AuthTokenDao(conn);
            String userID = aDao.getUserID(authToken);
            UserDao uDao = new UserDao(conn);
            String username = uDao.getUser(userID).getUsername();
            PersonDao pDao = new PersonDao(conn);
            persons = pDao.getPersons(username);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
            throw new InternalServerError();
        }
        response.setData(persons);
        response.setSuccess(true);
        return response;
    }

    /**
     * validateInput performs input validation before any changes are allowed on the database
     * This will filter any potentially dangerous or crash-causing inputs from being allowed in methods that change the database
     *
     * If the authToken isn't in the database, send an error response to the client
     * If no User exists for the existing authToken, send an error response to the client
     * If the person doesn't exist in the database, send an error response to the client
     * If the authenticated User is not authorized to get this person, send an error response to the client
     *
     * @param personID contains the personID for the desired person
     * @param authToken contains the authToken to verify that the user is authorized to access the person
     * @throws InvalidPersonIDError occurs if the personID is not associated with a Person in the database
     * @throws InvalidAuthTokenError occurs if the authToken is not in the authToken table
     * @throws RequestedPersonDoesNotBelongToThisUser occurs if the user is not authorized to view the person
     * @throws DataAccessException occurs if something goes wrong while accessing the database
     */
    private static void validateInput(String personID, String authToken) throws InvalidPersonIDError,
            InvalidAuthTokenError, RequestedPersonDoesNotBelongToThisUser, DataAccessException {
        try {
            db = new Database();
            Connection conn = db.openConnection();
            AuthTokenDao aDao = new AuthTokenDao(conn);
            String userID = aDao.getUserID(authToken);
            if(userID == null) {
//                If the authToken isn't in the database, send an error response to the client
                db.closeConnection(false);
                throw new InvalidAuthTokenError();
            }
            UserDao uDao = new UserDao(conn);
            User user = uDao.getUser(userID);
            if(user == null) {
//                If no User exists for the existing authToken, send an error response to the client
                db.closeConnection(false);
                throw new InvalidAuthTokenError();
            }
            PersonDao pDao = new PersonDao(conn);
            Person person = pDao.getPerson(personID);
            if(person == null) {
//                If the person doesn't exist in the database, send an error response to the client
                db.closeConnection(false);
                throw new InvalidPersonIDError();
            }
            if(!user.getUsername().equals(person.getAssociatedUsername())) {
//                If the authenticated User is not authorized to get this person, send an error response to the client
                db.closeConnection(false);
                throw new RequestedPersonDoesNotBelongToThisUser();
            }
            db.closeConnection(true);
        } catch (DataAccessException e) {
            System.out.println("Something went wrong while validating input in the GetPersonService!");
            db.closeConnection(false);
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * validateAllInput performs input validation before any changes are allowed on the database
     * This will filter any potentially dangerous or crash-causing inputs from being allowed in methods that change the database
     *
     * If the authToken isn't in the database, send an error response to the client
     *
     * @param authToken
     * @throws InvalidAuthTokenError
     * @throws DataAccessException
     */
    private static void validateAllInput(String authToken) throws InvalidAuthTokenError, DataAccessException {
        db = new Database();
        try {
            Connection conn = db.openConnection();
            AuthTokenDao aDao = new AuthTokenDao(conn);
            if(aDao.getUserID(authToken) == null) {
//                If the authToken isn't in the database, send an error response to the client
                db.closeConnection(false);
                throw new InvalidAuthTokenError();
            }
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
            throw new DataAccessException(e.getMessage());
        }
    }
}
