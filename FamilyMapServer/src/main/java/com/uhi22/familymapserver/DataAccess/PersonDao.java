/** The PersonDao class is used as an interface between the server's Java code and POJOs and the SQLite database
 *  This class is specifically used to interact with the Person table
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.familymapserver.DataAccess;

import com.uhi22.familymapserver.Errors.DataAccessException;
import com.uhi22.shared.model.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PersonDao {

    /**
     *  The Connection denotes the connection to the database that was initiated by the calling method
     */
    private static Connection conn;

    /**
     * The constructor takes the Database Connection that was passed by the calling method and makes it available within this class
     * @param conn the given connection to the database instance
     */
    public PersonDao(Connection conn) {
        PersonDao.conn = conn;
    }

    /**
     * getPerson provides the Person POJO that is attached to the given personID
     * A SQL String is created which is:
     * "SELECT * FROM Persons WHERE PersonID = ?;"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method attaches the personID to the prepared statement and executes the query
     * If something was returned from the SQL query, fill in the information for the Person POJO and return it
     * If nothing was returned from the SQL query, return null
     *
     * If any of the above failed, print the error and throw a new DataAccessException
     *
     * @param personID is the personID that was passed by the calling method
     * @return provides the Person POJO that is attached to the given personID
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public Person getPerson(String personID) throws DataAccessException {
        System.out.println("Getting the Person POJO for the personID: " + personID + " . . . ");
        String sql = "SELECT * FROM Persons WHERE PersonID = ?;";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The method attaches the personID to the prepared statement and executes the query
            stmt.setString(1,personID);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
//                If something was returned from the SQL query, fill in the information for the Person POJO and return it
                Person person = new Person(rs.getString("PersonID"),
                            rs.getString("AssociatedUserName"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("Gender"),
                            rs.getString("FatherID"),
                            rs.getString("MotherID"),
                            rs.getString("SpouseID"));
                rs.close();
                return person;
            }
        } catch (SQLException e) {
//            If any of the above failed, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException("Error encountered while retrieving Person from database");
        }
//        If nothing was returned from the SQL query, return null
        return null;
    }

    /**
     * getPersons provides all persons that are attached to a User based on a given username
     * A SQL String is created which is:
     * "SELECT * FROM Persons WHERE AssociatedUserName = ?;"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method attaches the username to the prepared statement and executes the query
     * The method iterates through the results of the query and creates a new Person POJO for every
     *  Person that was found to be associated with the given username
     * All associated Person POJOs are added to an ArrayList
     * This ArrayList is returned to the calling method, either containing a list of Events or an empty ArrayList
     *
     * If any of the above failed, print the error and throw a new DataAccessException
     *
     * @param username is the username that was passed by the calling method
     * @return provides an ArrayList of Person POJOs associated with the given username
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public ArrayList<Person> getPersons(String username) throws DataAccessException{
        System.out.println("Getting all persons associated with " + username + " . . . ");
        String sql = "SELECT * FROM Persons WHERE AssociatedUserName = ?;";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
//            The method attaches the username to the prepared statement and executes the query
            stmt.setString(1,username);
            ResultSet rs = stmt.executeQuery();
//            All associated Person POJOs are added to an ArrayList
            ArrayList<Person> persons = new ArrayList<Person>();
//            The method iterates through the results of the query and creates a new Person POJO for every
//            Person that was found to be associated with the given username
            while(rs.next()) {
                persons.add(new Person(rs.getString("PersonID"),
                        rs.getString("AssociatedUserName"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Gender"),
                        rs.getString("FatherID"),
                        rs.getString("MotherID"),
                        rs.getString("SpouseID")));
            }
            rs.close();
//            The ArrayList is returned to the calling method, either containing a list of Events or an empty ArrayList
            return persons;
        } catch (SQLException e) {
//            If any of the above failed, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException("Error encountered while getting all Persons from the database");
        }
    }

    /**
     * insertPerson inserts a row into the Persons table containing all information related to a given person
     * A SQL String is created which is:
     * "INSERT INTO Persons (PersonID, AssociatedUserName, FirstName, LastName, Gender, FatherID, MotherID, SpouseID)
     *  VALUES (?,?,?,?,?,?,?,?);"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method attaches all Person attributes to the prepared statement and executes it
     *
     * If any of the above failed, print the error and throw a new DataAccessException
     *
     * @param person is the Person POJO which is to be inserted into the SQLite database
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public static void insertPerson(Person person) throws DataAccessException {
        System.out.println("Inserting " + person.getPersonID() + " to the Persons table . . . ");
        String sql = "INSERT INTO Persons (PersonID, AssociatedUserName, FirstName, LastName, Gender, FatherID, MotherID, SpouseID) " +
                "VALUES (?,?,?,?,?,?,?,?);";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The method attaches all Person attributes to the prepared statement and executes it
            stmt.setString(1, person.getPersonID());
            stmt.setString(2, person.getAssociatedUsername());
            stmt.setString(3, person.getFirstName());
            stmt.setString(4, person.getLastName());
            stmt.setString(5, person.getGender());
            stmt.setString(6, person.getFatherID());
            stmt.setString(7, person.getMotherID());
            stmt.setString(8, person.getSpouseID());
            stmt.executeUpdate();
        } catch (SQLException e) {
//            If any of the above failed, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * clearPersons clears all rows from the Persons table
     * A SQL String is created which is:
     * "DELETE FROM Persons"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The prepared statement is executed
     * If an error occurs, it is printed and a DataAccessException is thrown
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public void clearPersons() throws DataAccessException {
        System.out.println("Clearing the Persons table . . . ");
        String sql = "DELETE FROM Persons;";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The prepared statement is executed
            stmt.executeUpdate();
        } catch (SQLException e) {
//            If an error occurs, it is printed and a DataAccessException is thrown
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing Persons table");
        }
    }

    /**
     * clearPersonByUsername clears all rows from the Persons table whose Person is associated with a given username
     * A SQL String is created which is:
     * "DELETE FROM Persons WHERE AssociatedUserName = ? AND AssociatedUserID IS null;"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method attaches the username and overwrite status to the prepared statement and executes it
     * Overwrite status is an artifact of the troubleshooting process and may not be entirely necessary for the final product
     * The method attaches the username to the prepared statement and executes it
     * If an error occurs, it is printed and a DataAccessException is thrown
     * @param username is the username that was passed by the calling method
     * @param overwrite denotes whether the method should also overwrite the root Person
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public void clearPersonByUsername(String username, boolean overwrite) throws DataAccessException {
        System.out.println("Clearing the Persons table of all rows associated with username: " + username + " . . . ");
        String sql = "DELETE FROM Persons WHERE AssociatedUserName = ? AND AssociatedUserID IS null;";
        if(overwrite) {
            sql = "DELETE FROM Persons WHERE AssociatedUserName = ?;";
        }
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The method attaches the username and personID to the prepared statement and executes it
            stmt.setString(1,username);
            stmt.executeUpdate();
        } catch (SQLException e) {
//            If an error occurs, it is printed and a DataAccessException is thrown
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing Persons table of " + username + "'s entries");
        }
    }

    /**
     * getPersonIDByUserID gets the personID for a Person that is associated with a given userID
     * A SQL String is created which is:
     * "SELECT * FROM Persons WHERE AssociatedUserID = ?;"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method attaches the userID to the prepared statement and executes it
     * If something was returned from the SQL query, return the personID associated with the result
     * If nothing was returned from the SQL query, return null
     *
     * If any of the above failed, print the error and throw a new DataAccessException
     *
     * @param userID is the personID that was passed by the calling method
     * @return provides the personID that is associated to the given userID
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public String getPersonIDByUserID(String userID) throws DataAccessException {
        System.out.println("Getting the personID associated with userID: " + userID + " . . . ");
        String sql = "SELECT * FROM Persons WHERE AssociatedUserID = ?;";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The method attaches the personID to the prepared statement and executes it
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
//                If something was returned from the SQL query, return the personID associated with the result
                return rs.getString("PersonID");
            }
        } catch (SQLException e) {
//            If any of the above failed, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException("Error encountered while getting Person ID by the User ID");
        }
//        If nothing was returned from the SQL query, return null
        return null;
    }

    /**
     * getRootPersonIDByUsername gets the personID for the original Person that was generated when the User
     * was initially registered with the system
     * This method is an artifact of a troubleshooting process that may not be necessary in the final product
     * A SQL String is created which is:
     * "SELECT * FROM Persons WHERE AssociatedUserName = ? AND AssociatedUserID IS NOT null;"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method attaches the username to the prepared statement and executes it
     * If something was returned from the SQL query, return the personID associated with the result
     * If nothing was returned from the SQL query, return null
     *
     * If any of the above failed, print the error and throw a new DataAccessException
     *
     * @param username is the username that was passed by the calling method
     * @return provides the personID for the original Person that was generated when the User initially registered
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public String getRootPersonIDByUsername(String username) throws DataAccessException {
        System.out.println("Getting the root personID associated with username: " + username + " . . . ");
        String sql = "SELECT * FROM Persons WHERE AssociatedUserName = ? AND AssociatedUserID IS NOT null;";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The method attaches the username to the prepared statement and executes it
            stmt.setString(1,username);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
//                If something was returned from the SQL query, return the personID associated with the result
                return rs.getString("PersonID");
            }
        } catch (SQLException e) {
//            If any of the above failed, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException("Error encountered while getting the Person ID by the Username");
        }
//        If nothing was returned from the SQL query, return null
        return null;
    }

    /**
     * attachUserID puts the userID for a User associated with a given username onto the row
     * containing the Person data associated with the given username
     * This is done to identify the root Person associated with the User at registration
     * This is an artifact of a troubleshooting process that may not be necessary in the final product
     * A SQL String is created which is:
     * "UPDATE Persons SET AssociatedUserID = ? WHERE AssociatedUserName = ?;"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method attaches the userID and username to the prepared statement and executes it
     *
     * If an error occurs, it is printed and a DataAccessException is thrown
     *
     * @param userID
     * @param username
     * @throws DataAccessException
     */
    public void attachUserID(String userID, String username) throws DataAccessException {
        System.out.println("Attaching userID: " + userID + " to username: " +
                username + " in the Persons table . . . ");
        String sql = "UPDATE Persons " +
                "SET AssociatedUserID = ? WHERE AssociatedUserName = ?;";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The method attaches the userID and username to the prepared statement and executes it
            stmt.setString(1,userID);
            stmt.setString(2,username);
            stmt.executeUpdate();
        } catch (SQLException e) {
//            If an error occurs, it is printed and a DataAccessException is thrown
            e.printStackTrace();
            throw new DataAccessException("Error encountered while setting the Associated User ID");
        }
    }
}
