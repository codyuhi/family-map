/** The LoadService class contains a load method which performs the business logic
 *  for loading given user, person, and event data into the database
 *
 *  Loading allows for data to be loaded into the database in bulk to support easy testing
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.familymapserver.Service;

import com.uhi22.familymapserver.DataAccess.Database;
import com.uhi22.familymapserver.DataAccess.EventDao;
import com.uhi22.familymapserver.DataAccess.PersonDao;
import com.uhi22.familymapserver.DataAccess.UserDao;
import com.uhi22.familymapserver.Errors.DataAccessException;
import com.uhi22.familymapserver.Errors.InternalServerError;
import com.uhi22.familymapserver.Errors.InvalidRequestDataError;
import com.uhi22.shared.model.Event;
import com.uhi22.shared.model.Person;
import com.uhi22.shared.model.User;
import com.uhi22.shared.Requests.LoadRequest;
import com.uhi22.shared.Responses.Response;
import com.uhi22.familymapserver.Util.RandomUtil;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.UUID;

public class LoadService {

    /**
     * The db variable contains a Database instance which allows for data access to the persistent SQLite database
     */
    private static Database db;
    /**
     * totalUsers contains a count of the total number of Users added to the database during the fill operation
     */
    private static int totalUsers;
    /**
     * totalPersons contains a count of the total number of Persons added to the database during the fill operation
     */
    private static int totalPersons;
    /**
     * totalEvents contains a count of the total number of Events added to the database during the fill operation
     */
    private static int totalEvents;

    /**
     * Empty constructor
     */
    public LoadService() {}

    /** The load method performs the business logic for loading bulk data into the database
     *  The method is called when the load endpoint is hit by a request from the client
     *
     *  The business logic for this service is as follows:
     *   - Clear all data from the database
     *   - Load the posted user data into the database
     *   - Load the posted person data into the database
     *   - Load the posted event data into the database
     *
     * @param request   the request parameter is a member of the LoadRequest class,
     *                  and has the information that would be passed in JSON for the load operation
     *
     *                  The request body will look as follows:
     *                  {
     *                      "users": [/* Array of User objects *],
     *                      "persons": [/* Array of Person objects *],
     *                      "events": [/* Array of Event objects *]
     *                  }
     *
     * @return          A Response class member is returned,
     *                  which has the attributes that can be converted into JSON that the client
     *                  expects from the load operation
     *
     *                  {
     *                      "message": "Successfully added X users, Y persons, and Z events to the database.",
     *                      "success":"true"        // Boolean identifier
     *                  }
     *
     * @throws com.uhi22.familymapserver.Errors.InvalidRequestDataError       Throws invalid request data error if the request data is invalid
     * @throws com.uhi22.familymapserver.Errors.InternalServerError           Throws internal server error if something went wrong while performing the operation
     *
     *                  The response body will look as follows upon failed operation:
     *                  {
     *                      "message":"Description of the error",
     *                      "success":"false"       // Boolean identifier
     *                  }
     */
    public static Response load(LoadRequest request) throws InvalidRequestDataError, InternalServerError, DataAccessException {
//       Create the Response POJO and Database variables
        Response response = new Response();
        db = new Database();
//        If the given input is invalid, validateInput will throw an error send response to the client
        validateInput(request);

        try {
            System.out.println("Clearing the database");
            ClearService.clear();
            System.out.println("Database successfully cleared!");
            Connection conn = db.openConnection();
            UserDao uDao = new UserDao(conn);
//            Iterate through the users in the request and add them into the database
            for(User user : request.getUsers()) {
                System.out.println(user);
                user.setPassword(RandomUtil.generateHash(user.getPassword()));
                uDao.insertUser(user);
                totalUsers++;
            }
//            Iterate through the persons in the request and add them into the database
            PersonDao pDao = new PersonDao(conn);
            for(Person person : request.getPersons()) {
                pDao.insertPerson(person);
                totalPersons++;
            }
//            Iterate through the events in the request and add them into the database
            EventDao eDao = new EventDao(conn);
            for(Event event : request.getEvents()) {
                eDao.insertEvent(event);
                totalEvents++;
            }
            db.closeConnection(true);
        } catch(DataAccessException e) {
            db.closeConnection(false);
            throw new DataAccessException(e.getMessage());
        }

//        Set the response message and success values, then reset the counters
        response.setMessage("Successfully added " + totalUsers + " users, " +
                totalPersons + " persons, and " + totalEvents + " events to the database.");
        totalPersons = 0;
        totalEvents = 0;
        totalUsers = 0;
        response.setSuccess(true);
        return response;
    }

    /**
     * validateInput performs input validation before any changes are allowed on the database
     * This will filter any potentially dangerous or crash-causing inputs from being allowed in methods that change the database
     *
     * If any of the Users have duplicates or invalid values from the request, send an error response to the client
     * If any of the Persons have duplicates or invalid values from the request, send an error response to the client
     * If any of the Events have duplicates or invalid values from the request, send an error response to the client
     *
     * @param request provides the LoadRequest data provided by the client
     * @throws InvalidRequestDataError occurs if the request data had an invalid value
     */
    private static void validateInput(LoadRequest request) throws InvalidRequestDataError {
        try {
//            If any of the Users have duplicates or invalid values from the request, send an error response to the client
            User[] users = request.getUsers();
            ArrayList<String> redundantUserChecker = new ArrayList<String>();
            ArrayList<String> redundantPersonChecker = new ArrayList<String>();
            for(User user : users) {
                assert !redundantUserChecker.contains(user.getUserID()) && !redundantPersonChecker.contains(user.getPersonID());
                redundantUserChecker.add(user.getUserID());
                redundantPersonChecker.add(user.getPersonID());
                if(user.getUserID() == null) {
                    user.setUserID(UUID.randomUUID().toString());
                }
                assert user.getUsername() != null && !user.getUsername().equals("");
                assert user.getPassword() != null && !user.getPassword().equals("");
                assert user.getEmail() != null && !user.getEmail().equals("");
                assert user.getFirstName() != null && !user.getFirstName().equals("");
                assert user.getLastName() != null && !user.getLastName().equals("");
                assert user.getGender().equals("m") || user.getGender().equals("f");
                assert user.getPersonID() != null && !user.getPersonID().equals("");
            }
//            If any of the Persons have duplicates or invalid values from the request, send an error response to the client
            Person[] persons = request.getPersons();
            redundantPersonChecker.clear();
            for(Person person : persons) {
                assert !redundantPersonChecker.contains(person.getPersonID());
                redundantPersonChecker.add(person.getPersonID());
                if(person.getPersonID() == null || "".equals(person.getPersonID())) {
                    person.setPersonID(UUID.randomUUID().toString());
                }
                assert person.getFirstName() != null && !person.getFirstName().equals("");
                assert person.getLastName() != null && !person.getLastName().equals("");
                assert person.getGender().equals("m") || person.getGender().equals("f");
            }
//            If any of the Events have duplicates or invalid values from the request, send an error response to the client
            Event[] events = request.getEvents();
            redundantPersonChecker.clear();
            for(Event event : events) {
                assert !redundantPersonChecker.contains(event.getEventID());
                redundantPersonChecker.add(event.getEventID());
                if(event.getEventID() == null || "".equals(event.getEventID())) {
                    event.setEventID(UUID.randomUUID().toString());
                }
                assert event.getPersonID() != null && !event.getPersonID().equals("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidRequestDataError();
        }
    }
}
