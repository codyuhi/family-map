/** The RegisterService class contains a register method which performs the business logic
 * for registering a user when the register endpoint is hit
 *
 * Registration allows for session management, authentication, authorization, and personalization to take place
 * Registration creates an entry in the database for users to be able to identify themselves to the server
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.familymapserver.Service;

import com.uhi22.familymapserver.DataAccess.*;
import com.uhi22.familymapserver.Errors.*;
import com.uhi22.shared.model.AuthorizationToken;
import com.uhi22.shared.model.Event;
import com.uhi22.shared.model.Person;
import com.uhi22.shared.model.User;
import com.uhi22.shared.Requests.RegisterRequest;
import com.uhi22.shared.Responses.RegisterResponse;
import com.uhi22.familymapserver.Util.JsonUtil;
import com.uhi22.familymapserver.Util.RandomUtil;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.util.UUID;

public class RegisterService extends Service {

    /**
     * The db variable contains a Database instance which allows for data access to the persistent SQLite database
     */
    private static Database db;

    /**
     * Empty constructor
     */
    public RegisterService() {}

    /** The register method performs the business logic for registering a user
     *  The method is called when the register endpoint is hit by a request from the client
     *
     *  The business logic for this service is as follows:
     *   - Create a new user account
     *   - Generate 4 generations of ancestor data for the new user
     *   - Login the user
     *   - Return an AuthenticationToken
     *
     * @param request   the request parameter is a member of the RegisterRequest class,
     *                  and has the information that would be passed in JSON for the register operation
     *
     *                  The request Body will look as follows:
     *                  {
     *                      "userName":"susan",         // Non-empty String
     *                      "password":"mysecret",      // Non-empty String
     *                      "email":"susan@gmail.com",  // Non-empty String
     *                      "firstName":"Susan",        // Non-empty String
     *                      "lastName":"Ellis",         // Non-empty String
     *                      "gender":"f"                // 'f' or 'm'
     *                  }
     *
     * @return          A RegisterResponse class member is returned,
     *                  which has the attributes that can be converted into the JSON that the client
     *                  expects from the register operation
     *
     *                  The response body will look as follows upon successful operation:
     *                  {
     *                      "authToken":"cf7a368f",     // Non-empty Auth Token String
     *                      "userName":"susan",         // User name passed in with request
     *                      "personID":"39f9fe46",      // Non-empty String containing the personID
     *                                                  // of the user's generated Person object
     *                      "success":"true"            // Boolean identifier
     *                  }
     *
     * @throws com.uhi22.familymapserver.Errors.RequestPropertyMissingValue       Throws missing value error if a value is missing in input
     * @throws com.uhi22.familymapserver.Errors.RequestPropertyInvalidValue       Throws invalid value error if a value is invalid
     * @throws com.uhi22.familymapserver.Errors.UserNameAlreadyTakenError         Throws username already taken error if
     *                                                  the given username is already in use
     * @throws com.uhi22.familymapserver.Errors.InternalServerError               Throws an internal server error if something went
     *                                                  wrong on the server side during the operation
     *
     *                  The response body will look as follows upon failed operation:
     *                  {
     *                      "message":"Description of the error",
     *                      "success":"false"           // Boolean identifier
     *                  }
     */
    public static RegisterResponse register(RegisterRequest request) throws RequestPropertyMissingValue, RequestPropertyInvalidValue, UserNameAlreadyTakenError, InternalServerError, DataAccessException {
//        Create the Response POJO and Database variables
        System.out.println("Entered the register method");
        RegisterResponse response = new RegisterResponse();
        db = new Database();
//        If the given input is invalid, validateInput will throw an error send response to the client
        validateInput(request);
//        Create a new User for the register result
        String newUserID = UUID.randomUUID().toString();
        String newPersonID = UUID.randomUUID().toString();
        User user = new User(newUserID,
                request.getUserName(),
                RandomUtil.generateHash(request.getPassword()),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getGender(),
                newPersonID);
        try {
//            Insert the new User into the database
            Connection conn = db.openConnection();
            UserDao uDao = new UserDao(conn);
            uDao.insertUser(user);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
            throw new DataAccessException(e.getMessage());
        }

//        Create a new Person for the register result
        Person person = new Person(newPersonID,
                request.getUserName(),
                request.getFirstName(),
                request.getLastName(),
                request.getGender(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
//        The birth location and date is randomly generated from assets that were provided
        JsonObject birthLocation = JsonUtil.getJsonFile("json/locations.json")
                .getAsJsonArray("data")
                .get((int) (977 * Math.random()))
                .getAsJsonObject();
        Event birth = new Event();
        birth.setEventID(UUID.randomUUID().toString());
        birth.setAssociatedUsername(request.getUserName());
        birth.setLatitude(birthLocation.get("latitude").getAsLong());
        birth.setLongitude(birthLocation.get("longitude").getAsLong());
        birth.setCountry(birthLocation.get("country").toString());
        birth.setCity(birthLocation.get("city").toString());
        birth.setEventType("birth");
        birth.setYear(2020);
        birth.setPersonID(newPersonID);
        try {
//            Insert the new Person into the database
            Connection conn = db.openConnection();
            PersonDao pDao = new PersonDao(conn);
            pDao.insertPerson(person);
            pDao.attachUserID(newUserID, request.getUserName());
            EventDao eDao = new EventDao(conn);
            eDao.insertEvent(birth);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
            throw new DataAccessException(e.getMessage());
        }
//        Call the FillService to fill the database with more Persons going 4 generations back by default
        FillService.fillHelper(4, person, 2020);
        System.out.println("Created " +
                FillService.getTotalPersons() +
                " persons and " +
                FillService.getTotalEvents() +
                " events of ancestors for the new user");

//        Create an authToken and add it to the database
        AuthorizationToken authToken = new AuthorizationToken();
        authToken.setUserID(newUserID);
        authToken.setAuthKey(UUID.randomUUID().toString());
        try {
            Connection conn = db.openConnection();
            AuthTokenDao aDao = new AuthTokenDao(conn);
            response.setAuthToken(aDao.insertAuthToken(UUID.randomUUID().toString(), authToken));
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
            throw new DataAccessException(e.getMessage());
        }
//        Define response values and send them back to the client
        response.setPersonID(newPersonID);
        response.setUserName(request.getUserName());
        response.setSuccess(true);
        return response;
    }

    /**
     * validateInput performs input validation before any changes are allowed on the database
     * This will filter any potentially dangerous or crash-causing inputs from being allowed in methods that change the database
     *
     * If any of the request values is null, send an error response to the client
     * If the gender value is not an "m" or an "f" send an error response to the client
     * If the username is already taken in the database, send an error response to the client
     *
     * @param request
     * @throws RequestPropertyMissingValue
     * @throws RequestPropertyInvalidValue
     * @throws UserNameAlreadyTakenError
     * @throws DataAccessException
     */
    public static void validateInput(RegisterRequest request) throws RequestPropertyMissingValue, RequestPropertyInvalidValue, UserNameAlreadyTakenError, DataAccessException {
//        If any of the request values is null, send an error response to the client
        if(request.getUserName() == null ||
        request.getPassword() == null ||
        request.getEmail() == null ||
        request.getFirstName() == null ||
        request.getLastName() == null ||
        request.getGender() == null) {
            throw new RequestPropertyMissingValue();
        }
//        If the gender value is not an "m" or an "f" send an error response to the client
        if(!"m".equals(request.getGender()) && !"f".equals(request.getGender())) {
            throw new RequestPropertyInvalidValue();
        }
        try {
//            If the username is already taken in the database, send an error response to the client
            Connection conn = db.openConnection();
            UserDao uDao = new UserDao(conn);
            if(uDao.usernameExists(request.getUserName())) {
                System.out.println(request.getUserName() + " is a username that has already been taken");
                db.closeConnection(false);
                throw new UserNameAlreadyTakenError();
            }
            db.closeConnection(true);
        } catch (DataAccessException e) {
            System.out.println("Error: " + e.getMessage());
            db.closeConnection(false);
            throw new DataAccessException(e.getMessage());
        }
    }
}
