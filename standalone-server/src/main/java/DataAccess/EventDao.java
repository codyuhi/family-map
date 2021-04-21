/** The EventDao class is used as an interface between the server's Java code and POJOs and the SQLite database
 *  This class is specifically used to interact with the Events table
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package DataAccess;

import Errors.DataAccessException;
import Model.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EventDao {

    /**
     *  The Connection denotes the connection to the database that was initiated by the calling method
     */
    private static Connection conn;

    /**
     * The constructor takes the Database Connection that was passed by the calling method and makes it available within this class
     * @param conn the given connection to the database instance
     */
    public EventDao(Connection conn) {
        EventDao.conn = conn;
    }

    /**
     * getEvent provides the Event POJO that is attached to the given eventID
     * A SQL String is created which is:
     * "SELECT * FROM Events WHERE EventID = ?;"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method attaches the eventID to the prepared statement and executes the query
     * If something was returned from the SQL query, fill in the information for the Event POJO and return it
     * If nothing was returned from the SQL query, return null
     *
     * If any of the above failed, print the error and throw a new DataAccessException
     *
     * @param eventID is the eventID that was passed by the calling method
     * @return provides the Event POJO that is attached to the given eventID
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public Event getEvent(String eventID) throws DataAccessException {
        System.out.println("Getting the Event POJO for the EventID: " + eventID + " . . . ");
        String sql = "SELECT * FROM Events WHERE EventID = ?;";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The method attaches the eventID to the prepared statement and executes the query
            stmt.setString(1,eventID);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
//                If something was returned from the SQL query, fill in the information for the Event POJO and return it
                Event event = new Event();
                event.setEventID(rs.getString("EventID"));
                event.setAssociatedUsername(rs.getString("AssociatedUserName"));
                event.setPersonID(rs.getString("PersonID"));
                event.setLatitude(rs.getFloat("Latitude"));
                event.setLongitude(rs.getFloat("Longitude"));
                event.setCountry(rs.getString("country"));
                event.setCity(rs.getString("city"));
                event.setEventType(rs.getString("EventType"));
                event.setYear(rs.getInt("year"));
                rs.close();
                return event;
            }
        } catch (SQLException e) {
//            If any of the above failed, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException("Error encountered while getting event");
        }
//        If nothing was returned from the SQL query, return null
        return null;
    }

    /**
     * getEvents provides all the events that are attached to a User based on a given username
     * A SQL String is created which is:
     * "SELECT * FROM Events WHERE AssociatedUsername = ?;"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method attaches the username to the prepared statement and executes the query
     * The method iterates through the results of the query and creates a new Event POJO for every
     *  Event that was found to be associated with the given username
     * All associated Event POJOs are added to an ArrayList
     * This ArrayList is returned to the calling method, either containing a list of Events or an empty ArrayList
     *
     * If any of the above failed, print the error and throw a new DataAccessException
     *
     * @param username is the username that was passed by the calling method
     * @return provides an ArrayList of Event POJOs associated with the given username
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public ArrayList<Event> getEvents(String username) throws DataAccessException {
        System.out.println("Getting all events associated with " + username + " . . . ");
        String sql = "SELECT * FROM Events WHERE AssociatedUsername = ?;";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The method attaches the username to the prepared statement and executes the query
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
//            All associated Event POJOs are added to an ArrayList
            ArrayList<Event> events = new ArrayList<Event>();
//            The method iterates through the results of the query and creates a new Event POJO for every
//            Event that was found to be associated with the given username
            while(rs.next()) {
                Event event = new Event();
                event.setEventID(rs.getString("EventID"));
                event.setAssociatedUsername(rs.getString("AssociatedUserName"));
                event.setPersonID(rs.getString("PersonID"));
                event.setLatitude(rs.getFloat("Latitude"));
                event.setLongitude(rs.getFloat("Longitude"));
                event.setCountry(rs.getString("country"));
                event.setCity(rs.getString("city"));
                event.setEventType(rs.getString("EventType"));
                event.setYear(rs.getInt("year"));
                events.add(event);
            }
            rs.close();
//            The ArrayList is returned to the calling method, either containing a list of Events or an empty ArrayList
            return events;
        } catch (SQLException e) {
//            If any of the above failed, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * insertEvent inserts a row into the Events table containing all information related to a given event
     * A SQL String is created which is:
     * "INSERT INTO Events (EventID, PersonID, AssociatedUserName, Latitude, Longitude, Country, City, EventType, Year)
     *  VALUES (?,?,?,?,?,?,?,?,?);"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method attaches all Event attributes to the prepared statement and executes it
     *
     * If any of the above failed, print the error and throw a new DataAccessException
     *
     * @param event is the Event POJO which is to be inserted into the SQLite database
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public void insertEvent(Event event) throws DataAccessException {
        System.out.println("Inserting " + event.getEventID() + " to the Events table . . . ");
        String sql = "INSERT INTO Events (EventID, PersonID, AssociatedUserName, Latitude, Longitude, Country, City, EventType, Year) " +
                "VALUES (?,?,?,?,?,?,?,?,?);";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The method attaches all Event attributes to the prepared statement and executes it
            stmt.setString(1,event.getEventID());
            stmt.setString(2,event.getPersonID());
            stmt.setString(3, event.getAssociatedUsername());
            stmt.setFloat(4, event.getLatitude());
            stmt.setFloat(5,event.getLongitude());
            stmt.setString(6,event.getCountry());
            stmt.setString(7,event.getCity());
            stmt.setString(8,event.getEventType());
            stmt.setInt(9,event.getYear());
            stmt.executeUpdate();
        } catch (SQLException e) {
//            If any of the above failed, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting Event into the database");
        }
    }

    /**
     * getBirthYearByPersonID gets the year from the Events row that for a birth event
     * that is associated with a given personID
     * A SQL String is created which is:
     * "SELECT * FROM Events WHERE PersonID = ? AND EventType = \"birth\";"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method attaches the personID to the prepared statement and executes it
     * If something was returned from the SQL query, return the year associated with the Event
     * If nothing was returned from the SQL query, default to the current year
     *      I chose this year because the most likely person to not have an established birth year is the Person
     *      that is generated when the User first registers.
     *      The FillService-populated Persons should all have birth Events
     *
     * If any of the above failed, print the error and throw a new DataAccessException
     *
     * @param personID is the personID that was passed by the calling method
     * @return provides the birth year that is associated with the given personID
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public int getBirthYearByPersonID(String personID) throws DataAccessException {
        System.out.println("Getting the birth year for personID: " + personID + " . . . ");
        String sql = "SELECT * FROM Events WHERE PersonID = ? AND EventType = \"birth\";";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The method attaches the personID to the prepared statement and executes it
            stmt.setString(1,personID);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
//                If something was returned from the SQL query, return the year associated with the Event
                return rs.getInt("Year");
            }
        } catch (SQLException e) {
//            If any of the above failed, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException("Error encountered while getting the birth year by person id");
        }
//        If nothing was returned from the SQL query, default to the current year
//        I chose this year because the most likely person to not have an established birth year is the Person
//        that is generated when the User first registers.  The FillService-populated Persons should all have birth Events
        return 2020;
    }

    /**
     * clearEvents clears all rows from the Events table
     * A SQL String is created which is:
     * "DELETE FROM Events;"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The prepared statement is executed
     * If an error occurs, it is printed and a DataAccessException is thrown
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public void clearEvents() throws DataAccessException {
        System.out.println("Clearing the Events table . . . ");
        String sql = "DELETE FROM Events;";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The prepared statement is executed
            stmt.executeUpdate();
        } catch (SQLException e) {
//            If an error occurs, it is printed and a DataAccessException is thrown
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing Events table");
        }
    }

    /**
     * clearEventByUsername clears all rows from the Events table whose Event is associated with a given username
     * A SQL String is created which is:
     * "DELETE FROM Events WHERE AssociatedUserName = ? AND PersonID != ?;"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method attaches the username and personID to the prepared statement and executes it
     *
     * If an error occurs, it is printed and a DataAccessException is thrown
     *
     * @param username is the username that was passed by the calling method
     * @param personID is the personID that was passed by the calling method
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public void clearEventByUsername(String username, String personID) throws DataAccessException {
        System.out.println("Clearing the Events table of all rows associated with the username: " + username + " . . . ");
        String sql = "DELETE FROM Events WHERE AssociatedUserName = ? AND PersonID != ?;";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The method attaches the username and personID to the prepared statement and executes it
            stmt.setString(1,username);
            stmt.setString(2,personID);
            stmt.executeUpdate();
        } catch (SQLException e) {
//            If an error occurs, it is printed and a DataAccessException is thrown
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing Events table of " + username + "'s entries");
        }
    }
}
