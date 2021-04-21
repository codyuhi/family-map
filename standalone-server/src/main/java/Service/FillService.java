/** The FillService class contains a fill method which performs the business logic for
 *  populating Person and Event information
 *
 *  Filling allows for the databases to be populated with meaningful information provided by the user or random information
 *  By filling the database with data, the application is able to offer functionality.
 *  Without data, the application does nothing useful or interesting.
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Service;

import DataAccess.Database;
import DataAccess.EventDao;
import DataAccess.PersonDao;
import DataAccess.UserDao;
import Errors.DataAccessException;
import Errors.InternalServerError;
import Errors.InvalidGenerationsError;
import Errors.InvalidUsernameError;
import Model.Event;
import Model.Person;
import Responses.Response;
import Util.JsonUtil;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.util.Random;
import java.util.UUID;

public class FillService extends Service {

    /**
     * The db variable contains a Database instance which allows for data access to the persistent SQLite database
     */
    private static Database db;
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
    public FillService() {}

    /**
     * getTotalPersons is a getter to provide the totalPersons private variable
     * @return provides the totalPersons count for the total number of Persons added in the fill operation
     */
    public static int getTotalPersons() {
        return totalPersons;
    }

    /**
     * getTotalEvents is a getter to provide the totalEvents private variable
     * @return provides the totalEvents count for the total number of Events added in the fill operation
     */
    public static int getTotalEvents() {
        return totalEvents;
    }

    /** The fill method performs the business logic for populating Person and Event information
     *  The method is called when the Fill endpoint is hit by a request from the client or other classes in the server
     *
     *  The business logic for this service is as follows:
     *   - Delete data in the database for a User if the User already has data in the database
     *   - Populate the server's database with generated data for the specified username
     *   - Repeat the above steps for the number of generations specified in the parameters
     *
     * @param generations       The generations parameter specifies the number of generations to be populated
     * @param username          The username parameter is a string which holds the username for the currently authenticated User
     * @return                  A FillResponse class member is returned,
     *                          which has the attributes that can be converted into the JSON that the client
     *                          expects from the fill operation
     *
     *                          The response body will look as follows upon successful operation:
     *                          {
     *                              "message":"Successfully added X persons and Y events to the database",
     *                              "success":"true"    // Boolean IDentifier
     *                          }
     *
     * @throws Errors.InvalidUsernameError      Throws an invalid username/password error if the provided username/password could not be found
     * @throws Errors.InvalidGenerationsError   Throws an invalid generations error if the number of generations requested is outside of an acceptable range
     * @throws Errors.InternalServerError       Throws an internal server error if the server has problems executing the operation
     *
     *                          The response body will look as follows upon a failed operation:
     *                          {
     *                              "message":"Description of the error",
     *                              "success":"false"   // Boolean IDentifier
     *                          }
     *
     */
    public static Response fill(String generations, String username) throws InvalidUsernameError, InvalidGenerationsError, InternalServerError, DataAccessException  {
//        The overwrite variable is an artifact of a troubleshooting process
//        This also will allow me to tweak the Server code based on whether I want to overwrite the original Person or not
//        I want this to be an option in the future because I would prefer to not overwrite the original Person created at registration
        boolean overwrite = true;
//        Create the Response POJO and Database variables
        Response response = new Response();
        db = new Database();
//        If the given input is invalid, validateInput will throw an error send response to the client
        validateInput(generations, username);
//        Initialize the variables that will be the roots of the fill operation to be built upon
        int birthYear = -1;
        Person person = null;
        totalPersons = 0;
        totalEvents = 0;
        try {
//            Open the database connection
            Connection conn = db.openConnection();
//            Create the PersonDao POJO
            PersonDao pDao = new PersonDao(conn);
//            Clear the Person table of all Persons that are associated with the given username
            pDao.clearPersonByUsername(username, overwrite);
            String rootPersonID = null;
            if(overwrite) {
//                If the overwrite artifact is true, I want ot overwrite the rootPerson that was originally created when the User registered
                rootPersonID = UUID.randomUUID().toString();
                person = new Person(
                        rootPersonID,
                        username,
                        getRandomFirstName("m"),
                        getRandomLastName(),
                        "m",
                        UUID.randomUUID().toString(),
                        UUID.randomUUID().toString(),
                        UUID.randomUUID().toString()
                );
//            Insert the newly created Person and increment the persons counter
                pDao.insertPerson(person);
                totalPersons++;
//                Generate a location randomly from the locations assets
//                Then fill in data for the event
                JsonObject birthLocation = JsonUtil.getJsonFile("json/locations.json")
                        .getAsJsonArray("data")
                        .get((int) (977 * Math.random()))
                        .getAsJsonObject();
                Event birth = new Event();
                birth.setEventID(UUID.randomUUID().toString());
                birth.setAssociatedUsername(username);
                birth.setLatitude(birthLocation.get("latitude").getAsLong());
                birth.setLongitude(birthLocation.get("longitude").getAsLong());
                birth.setCountry(birthLocation.get("country").toString());
                birth.setCity(birthLocation.get("city").toString());
                birth.setEventType("birth");
                birth.setYear(2020);
                birth.setPersonID(rootPersonID);
                EventDao eDao = new EventDao(conn);
//                Insert the Event to the database then increment the totalEvents counter
                eDao.insertEvent(birth);
                totalEvents++;
            } else {
//                If the overwrite boolean is false, preserve the original root Person
                rootPersonID = pDao.getRootPersonIDByUsername(username);
                person = pDao.getPerson(rootPersonID);
            }
//            Create a new EventDao POJO and clear all Events from the database that are associated with the given username
            EventDao eDao = new EventDao(conn);
            eDao.clearEventByUsername(username, rootPersonID);
            birthYear = eDao.getBirthYearByPersonID(rootPersonID);
//            Close the database
            db.closeConnection(true);
        } catch (DataAccessException e) {
//            Close the database
            db.closeConnection(false);
            System.out.println("Something went wrong while accessing data in the Fill method");
            throw new DataAccessException(e.getMessage());
        }

//        Call the recursive fillHelper and pass in the number of generations to be recursed,
//        a new root node Person for the recursion,
//        and the root node Person's birthYear to calculate all the following nodes' birth, death, and marriage years
        fillHelper(Integer.parseInt(generations), person, birthYear);
//        Set response message, reset the total counts, set the success status, and return the successful response
        response.setMessage("Successfully added " + totalPersons + " persons and " + totalEvents + " events to the database.");
        totalPersons = 0;
        totalEvents = 0;
        response.setSuccess(true);
        return response;
    }

    /**
     * fillHelper is a recursive method which uses the parent Person to populate appropriate, randomly generated data into the database
     * The number of generations that the User provided in the request denotes how many times this helper method will be called
     *
     * If the number of generations is 0, the recursion is complete and the method should be exited
     * Create and populate the father Person with randomly generated data that meets the business requirements
     * Create and populate the mother Person with randomly generated data that meets the business requirements
     *      Random locations and names are taken from the json assets
     *      Random years for events are found within an appropriate range
     *          proportionately to their spouse's age, their own death/birth, and the age of the child
     * If the Person is one of the last from the generations requirement, make sure they don't have a defined father/motherID
     *
     * @param generations contains the remaining number of recursions to iterate through
     * @param person contains the Person POJO from the child node
     * @param birthYear contains the birthYear for the person whose father and mother are currently being created
     *                  This allows for realistic dates to be randomly generated
     * @throws InternalServerError this occurs when there is a problem that occurs during business operations in this method
     * @throws DataAccessException this occurs when there is a problem during database access
     */
    public static void fillHelper(int generations, Person person, int birthYear) throws InternalServerError, DataAccessException {
//        If the number of generations is 0, the recursion is complete and the method should be exited
        if(generations <= 0) {
            System.out.println("Exit condition met!");
            return;
        }
//        Create and populate the father Person with randomly generated data that meets the business requirements
        Person father = new Person(person.getFatherID(),
                person.getAssociatedUsername(),
                getRandomFirstName("m"),
                getRandomLastName(),
                "m",
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                person.getMotherID());

//        Create the Events associated with the father Person
        Random random = new Random();
        JsonObject fatherBirthLocation = JsonUtil.getJsonFile("json/locations.json")
                .getAsJsonArray("data")
                .get((int) (977 * Math.random()))
                .getAsJsonObject();
        int fatherBirthYear = random.nextInt((birthYear - 13) - (birthYear - 50)) + (birthYear - 50);
        if((birthYear - fatherBirthYear) < 13) {
            fatherBirthYear = birthYear - 13;
        }
        if((birthYear - fatherBirthYear) > 50) {
            fatherBirthYear = birthYear - 50;
        }
        System.out.println("Birth year: " + birthYear +
                "\nFather Birth year: " + fatherBirthYear);
        Event fatherBirth = new Event();
        fatherBirth.setEventID(UUID.randomUUID().toString());
        fatherBirth.setAssociatedUsername(person.getAssociatedUsername());
        fatherBirth.setLatitude(fatherBirthLocation.get("latitude").getAsLong());
        fatherBirth.setLongitude(fatherBirthLocation.get("longitude").getAsLong());
        fatherBirth.setCountry(fatherBirthLocation.get("country").toString());
        fatherBirth.setCity(fatherBirthLocation.get("city").toString());
        fatherBirth.setEventType("birth");
        fatherBirth.setYear(fatherBirthYear);
        fatherBirth.setPersonID(father.getPersonID());
        JsonObject fatherDeathLocation = JsonUtil.getJsonFile("json/locations.json")
                .getAsJsonArray("data")
                .get((int) (977 * Math.random()))
                .getAsJsonObject();
        int fatherDeathYear = random.nextInt((120 + fatherBirthYear) - (birthYear)) + (birthYear);
        if((fatherDeathYear - fatherBirthYear) > 120) {
            fatherDeathYear = fatherBirthYear + 120;
        }
        Event fatherDeath = new Event();
        fatherDeath.setEventID(UUID.randomUUID().toString());
        fatherDeath.setAssociatedUsername(person.getAssociatedUsername());
        fatherDeath.setLatitude(fatherDeathLocation.get("latitude").getAsLong());
        fatherDeath.setLongitude(fatherDeathLocation.get("longitude").getAsLong());
        fatherDeath.setCountry(fatherBirthLocation.get("country").toString());
        fatherDeath.setCity(fatherDeathLocation.get("city").toString());
        fatherDeath.setEventType("death");
        fatherDeath.setYear(fatherDeathYear);
        fatherDeath.setPersonID(father.getPersonID());

//        Create and populate the mother Person with randomly generated data that meets the business requirements
        Person mother = new Person(person.getMotherID(),
                person.getAssociatedUsername(),
                getRandomFirstName("f"),
                getRandomLastName(),
                "f",
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                person.getFatherID());

//        Create the Events associated with the mother Person
        JsonObject motherBirthLocation = JsonUtil.getJsonFile("json/locations.json")
                .getAsJsonArray("data")
                .get((int) (977 * Math.random()))
                .getAsJsonObject();
        int motherBirthYear = random.nextInt((fatherBirthYear + 13) - (fatherBirthYear - 13)) + (fatherBirthYear - 13);
        if((birthYear - motherBirthYear) < 13) {
            motherBirthYear = birthYear - 13;
        }
        if((birthYear - motherBirthYear) > 50) {
            motherBirthYear = birthYear - 50;
        }
        System.out.println("Mother Birth year: " + motherBirthYear);
        Event motherBirth = new Event();
        motherBirth.setEventID(UUID.randomUUID().toString());
        motherBirth.setAssociatedUsername(person.getAssociatedUsername());
        motherBirth.setLatitude(motherBirthLocation.get("latitude").getAsLong());
        motherBirth.setLongitude(motherBirthLocation.get("longitude").getAsLong());
        motherBirth.setCountry(motherBirthLocation.get("country").toString());
        motherBirth.setCity(motherBirthLocation.get("city").toString());
        motherBirth.setEventType("birth");
        motherBirth.setYear(motherBirthYear);
        motherBirth.setPersonID(mother.getPersonID());
        JsonObject motherDeathLocation = JsonUtil.getJsonFile("json/locations.json")
                .getAsJsonArray("data")
                .get((int) (977 * Math.random()))
                .getAsJsonObject();
        int motherDeathYear = random.nextInt((120 + motherBirthYear) - birthYear) + birthYear;
        if((motherDeathYear - motherBirthYear) > 120) {
            motherDeathYear = motherBirthYear + 120;
        }
        Event motherDeath = new Event();
        motherDeath.setEventID(UUID.randomUUID().toString());
        motherDeath.setAssociatedUsername(person.getAssociatedUsername());
        motherDeath.setLatitude(motherDeathLocation.get("latitude").getAsLong());
        motherDeath.setLongitude(motherDeathLocation.get("longitude").getAsLong());
        motherDeath.setCountry(motherDeathLocation.get("country").toString());
        motherDeath.setCity(motherDeathLocation.get("city").toString());
        motherDeath.setEventType("death");
        motherDeath.setYear(motherDeathYear);
        motherDeath.setPersonID(mother.getPersonID());

//        Create the marriage event
        JsonObject marriageLocation = JsonUtil.getJsonFile("json/locations.json")
                .getAsJsonArray("data")
                .get((int) (977 * Math.random()))
                .getAsJsonObject();
        int marriageYear = random.nextInt((Math.min(fatherDeathYear, motherDeathYear) - (Math.max(fatherBirthYear, motherBirthYear)))) +
                (Math.max(fatherBirthYear, motherBirthYear));
        if((marriageYear - fatherBirthYear) < 13) {
            marriageYear = fatherBirthYear + 13;
        }
        if((marriageYear - motherBirthYear) < 13) {
            marriageYear = motherBirthYear + 13;
        }
        System.out.println("Marriage year: " + marriageYear);
        System.out.println("Father was " + (marriageYear - fatherBirthYear) + " years old and Mother was " +
                (marriageYear - motherBirthYear) + " years old when they got married\n");
        Event marriage = new Event();
        marriage.setEventID(UUID.randomUUID().toString());
        marriage.setAssociatedUsername(person.getAssociatedUsername());
        marriage.setLatitude(marriageLocation.get("latitude").getAsLong());
        marriage.setLongitude(marriageLocation.get("longitude").getAsLong());
        marriage.setCountry(marriageLocation.get("country").toString());
        marriage.setCity(marriageLocation.get("city").toString());
        marriage.setEventType("marriage");
        marriage.setYear(marriageYear);
        marriage.setPersonID(father.getPersonID());

//        If the Person is one of the last from the generations requirement, make sure they don't have a defined father/motherID
        if(generations == 1) {
            father.setFatherID(null);
            father.setMotherID(null);
            mother.setFatherID(null);
            mother.setMotherID(null);
        }

//        Open the connection to the database and add the newly created events to the database
        db = new Database();
        try {
//            for every successful event added, increment the event count
            Connection conn = db.openConnection();
            EventDao eDao = new EventDao(conn);
            eDao.insertEvent(fatherBirth);
            totalEvents++;
            eDao.insertEvent(fatherDeath);
            totalEvents++;
            eDao.insertEvent(marriage);
            totalEvents++;
            eDao.insertEvent(motherBirth);
            totalEvents++;
            eDao.insertEvent(motherDeath);
            totalEvents++;
            marriage.setPersonID(mother.getPersonID());
            marriage.setEventID(UUID.randomUUID().toString());
            eDao.insertEvent(marriage);
            totalEvents++;
//            After successful additions, gracefully close the database
            db.closeConnection(true);
        } catch (DataAccessException e) {
//            Failed conditions trigger a fail response to the client
            db.closeConnection(false);
            System.out.println("Failed to add an Event to the database in the FillHelper method");
            throw new DataAccessException(e.getMessage());
        }

//        Open the connection to the database and add the newly created father and mother to the database
        try {
//            Increment the counter for every successful person added
            Connection conn = db.openConnection();
            PersonDao pDao = new PersonDao(conn);
            pDao.insertPerson(father);
            totalPersons++;
            pDao.insertPerson(mother);
            totalPersons++;
            db.closeConnection(true);
//            After successful additions, gracefully close the database
        } catch (DataAccessException e) {
//            Failed conditions trigger a fail response to the client
            db.closeConnection(false);
            System.out.println("Failed to add a Person to the database in the FillHelper method");
            throw new DataAccessException(e.getMessage());
        }

//        Decrement the generations count and recursively call the helper method for the father and mother
        generations--;
        fillHelper(generations, father, fatherBirthYear);
        fillHelper(generations, mother, motherBirthYear);
    }

    /**
     * validateInput performs input validation before any changes are allowed on the database
     * This will filter any potentially dangerous or crash-causing inputs from being allowed in methods that change the database
     *
     * If the generations were not given in the URL parameters, send an error response to the client
     * If the username was not given in the URL parameters, send an error response to the client
     * If the generations value is not valid (not an int), send an error response to the client
     * If a User associated with the given username does not exist in the database, send an error response to the client
     *
     * @param generations contains the number of generations to be filled
     * @param username contains the username that the filled entities should be attached to
     * @throws InvalidGenerationsError occurs when the generation is not in the correct domain
     * @throws InvalidUsernameError occurs when the username is not attached to a valid user
     * @throws DataAccessException occurs when something went wrong while accessing the database
     */
    private static void validateInput(String generations, String username) throws InvalidGenerationsError, InvalidUsernameError, DataAccessException {
//        If the generations were not given in the URL parameters, send an error response to the client
        if(generations == null) {
            throw new InvalidGenerationsError();
        }
//        If the username was not given in the URL parameters, send an error response to the client
        if(username == null) {
            throw new InvalidUsernameError();
        }
//        If the generations value is not valid (not an int), send an error response to the client
        try {
            Integer.parseInt(generations);
        } catch (NumberFormatException e) {
            throw new InvalidGenerationsError();
        }
        try {
//            If a User associated with the given username does not exist in the database, send an error response to the client
            Connection conn = db.openConnection();
            UserDao uDao = new UserDao(conn);
            if(!uDao.usernameExists(username)) {
                System.out.println(username + " does not exist in the database");
                db.closeConnection(false);
                throw new InvalidUsernameError();
            }
            db.closeConnection(true);
        } catch (DataAccessException e) {
            System.out.println("Something went wrong while validating input in the FillService!");
            db.closeConnection(false);
            throw new DataAccessException(e.getMessage());
        }
    }
}
