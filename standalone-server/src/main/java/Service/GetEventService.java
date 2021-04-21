/** The GetEventService class contains a getEvent method which performs the business logic
 *  for getting Event data for a single Event class member from the database
 *  The GetEventService class also contains a getAllEvents method which performs the business logic
 *  for getting Event data for all Events stored in the database for all Persons associated with the current user
 *
 *  Getting Event data from the database is important for allowing the client
 *  to gain information about specific Event class members so it can be processed client-side
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Service;

import DataAccess.AuthTokenDao;
import DataAccess.Database;
import DataAccess.EventDao;
import DataAccess.UserDao;
import Errors.*;
import Model.Event;
import Model.User;
import Responses.AllEventsResponse;
import Responses.EventResponse;

import java.sql.Connection;
import java.util.ArrayList;

public class GetEventService {

    /**
     * The db variable contains a Database instance which allows for data access to the persistent SQLite database
     */
    private static Database db;

    /**
     * Empty constructor marking the class for public use
     */
    public GetEventService() {}

    /** The getEvent method performs the business logic for getting the data associated with a specific event
     *  The method is called when the event/[eventID] endpoint is hit by a GET request from the user
     *
     *  The business logic for this service is as follows:
     *   - Return a single Event object with the specified ID
     *
     * @param eventID       The eventID parameter is a string
     *                      and has the eventID value for the Event row that
     *                      is desired to be returned from the GET operation
     *
     * @param authToken     The authToken that was included in the request header
     *
     * @return              A EventResponse class member is returned,
     *                      which has the attributes that can be converted into the JSOn that the client
     *                      expects from the get event operation
     *
     *                      The response body will look as follows upon successful operation:
     *                      {
     *                          "associatedUsername":"susan",   // Username of user account this event belongs to (non-empty string)
     *                          "eventID":"251837d7",           // Event's unique ID (non-empty string)
     *                          "personID":"7255e93e",          // ID of the person this event belongs to (non-empty string)
     *                          "latitude": 65.6833,            // Latitude of the event's location (number)
     *                          "longitude":-17.9,              // Longitude of the event's location (number)
     *                          "country":"Iceland",            // Name of the country where event occurred (non-empty string)
     *                          "city":"Akureyri",              // Name of city where event occurred (non-empty string)
     *                          "eventType":"birth",            // Type of event ("birth", "baptism", etc.) (non-empty string)
     *                          "year":1912,                    // Year the event occurred (integer)
     *                          "success":"true"                // Boolean identifier
     *                      }
     *
     * @throws InvalidAuthTokenError                    Throws invalid auth token error if the given auth token is not found in the AuthorizationToken table
     * @throws InvalidEventIDError                      Throws invalid event id error if the given event does not exist in the database
     * @throws RequestedEventDoesNotBelongToThisUser    Throws requested person does not belong to this user if the given authToken is not associated with
     *      *                                           the requested person's user
     * @throws InternalServerError                      Throws internal server error if something went wrong
     *      *                                           on the server side during the operation
     *
     *                      The response body will look as follows upon failed operation:
     *                      {
     *                          "message":"Description of error",
     *                          "success":"false"   // Boolean identifier
     *                      }
     */
    public static EventResponse getEvent(String eventID, String authToken) throws InvalidAuthTokenError,
            InvalidEventIDError, RequestedEventDoesNotBelongToThisUser, InternalServerError, DataAccessException {
        System.out.println("Getting Event with EventID: " + eventID + " . . . ");
        EventResponse response = new EventResponse();
//        If the given input is invalid, validateInput will throw an error send response to the client
        validateInput(eventID, authToken);
        Event event = null;
        try {
//            Open the database connection
            Connection conn = db.openConnection();
//            Create the EventDao POJO
            EventDao eDao = new EventDao(conn);
            event = eDao.getEvent(eventID);
//            Gracefully exit
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
            throw new DataAccessException(e.getMessage());
        }
//        Define the response based on the Dao results
        response.setAssociatedUsername(event.getAssociatedUsername());
        response.setEventID(event.getEventID());
        response.setPersonID(event.getPersonID());
        response.setLatitude(event.getLatitude());
        response.setLongitude(event.getLongitude());
        response.setCountry(event.getCountry());
        response.setCity(event.getCity());
        response.setEventType(event.getEventType());
        response.setYear(event.getYear());
        response.setSuccess(true);
        return response;
    }

    /** The getAllEvents method performs the business logic for getting the data associated with
     *  all Events that are owned by the currently logged in user
     *
     *  The business logic for this service is as follows:
     *   - Return all Events for all family members of the current user (determined by the provided authToken)
     *
     * @param authToken     the authToken parameter is a string containing the authToken passed in the request header
     *
     * @return              An AllEventsResponse class member is returned,
     *                      which has the attributes that can be converted into the JSON that the client
     *                      expects from the GET /event operation
     *
     *                      The response body will look as follows upon successful operation:
     *                      {
     *                          "data":[/* Array of Event objects *],
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
    public static AllEventsResponse getAllEvents(String authToken) throws InvalidAuthTokenError, InternalServerError, DataAccessException {
        System.out.println("Getting all Events . . . ");
        AllEventsResponse response = new AllEventsResponse();
        validateAllInput(authToken);
        ArrayList<Event> events = null;
//        Open the DB connection and get the ArrayList of all Events
        try {
            Connection conn = db.openConnection();
            AuthTokenDao aDao = new AuthTokenDao(conn);
            String userID = aDao.getUserID(authToken);
            UserDao uDao = new UserDao(conn);
            String username = uDao.getUser(userID).getUsername();
            EventDao eDao = new EventDao(conn);
            events = eDao.getEvents(username);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
            throw new InternalServerError();
        }
        response.setData(events);
        response.setSuccess(true);
        return response;
    }

    /**
     * validateInput performs input validation before any changes are allowed on the database
     * This will filter any potentially dangerous or crash-causing inputs from being allowed in methods that change the database
     *
     * If the authToken isn't in the database, send an error response to the client
     * If no User exists for the existing authToken, send an error response to the client
     * If the event doesn't exist in the database, send an error response to the client
     * If the authenticated User is not authorized to get this event, send an error response to the client
     *
     * @param eventID contains the EventID for the event that is desired in the response
     * @param authToken contains the authToken to verify the user is authorized to access the event
     * @throws InvalidEventIDError occurs when the EventID does not exist in the database
     * @throws InvalidAuthTokenError occurs when the authToken does not exist in the database
     * @throws RequestedEventDoesNotBelongToThisUser occurs when the authenticated user is not authorized for the desired event
     * @throws DataAccessException occurs when something went wrong while accessing the database
     */
    private static void validateInput(String eventID, String authToken) throws InvalidEventIDError,
            InvalidAuthTokenError, RequestedEventDoesNotBelongToThisUser, DataAccessException {
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
            EventDao eDao = new EventDao(conn);
            Event event = eDao.getEvent(eventID);
            if(event == null) {
//                If the event doesn't exist in the database, send an error response to the client
                db.closeConnection(false);
                throw new InvalidEventIDError();
            }
            if(!user.getUsername().equals(event.getAssociatedUsername())) {
//                If the authenticated User is not authorized to get this event, send an error response to the client
                db.closeConnection(false);
                throw new RequestedEventDoesNotBelongToThisUser();
            }
            db.closeConnection(true);
        } catch (DataAccessException e) {
            System.out.println("Something went wrong while validating input in the GetEventService!");
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
            System.out.println("Something went wrong while validating input in the GetAllEventsService!");
            db.closeConnection(false);
            throw new DataAccessException(e.getMessage());
        }
    }
}
