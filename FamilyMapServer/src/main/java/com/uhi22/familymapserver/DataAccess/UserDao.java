/** The UserDao class is used as an interface between the server's Java code and POJOs and the SQLite database
 * This class is specifically used to interact with the User table
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.familymapserver.DataAccess;

import com.uhi22.familymapserver.Errors.DataAccessException;
import com.uhi22.shared.model.User;

import java.sql.*;
import java.util.ArrayList;

public class UserDao {

    /**
     *  The Connection denotes the connection to the database that was initiated by the calling method
     */
    private static Connection conn;

    /**
     * The constructor takes the Database Connection that was passed by the calling method and makes it available within this class
     * @param conn the given connection to the database instance
     */
    public UserDao(Connection conn) {
        this.conn = conn;
    }

    /**
     * getEvent provides the Event POJO that is attached to the given eventID
     * A SQL String is created which is:
     * "SELECT * FROM Users WHERE UserID = ?;"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method attaches the userID to the prepared statement and executes the query
     * If something was returned from the SQL query, fill in the information for the User POJO and return it
     * If nothing was returned from the SQL query, return null
     *
     * If any of the above failed, print the error and throw a new DataAccessException
     *
     * @param userID is the eventID that was passed by the calling method
     * @return provides the Event POJO that is attached to the given eventID
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public User getUser(String userID) throws DataAccessException {
        System.out.println("Getting the User POJO for the userID: " + userID + " . . . ");
        String sql = "SELECT * FROM Users WHERE UserID = ?;";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The method attaches the userID to the prepared statement and executes the query
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
//                If something was returned from the SQL query, fill in the information for the User POJO and return it
                User user = new User(rs.getString("userID"),
                        rs.getString("UserName"),
                        rs.getString("PassWord"),
                        rs.getString("Email"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Gender"),
                        rs.getString("PersonID"));
                rs.close();
                return user;
            }
        } catch (SQLException e) {
//            If any of the above failed, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException("Error encountered while retrieving User from database");
        }
//        If nothing was returned from the SQL query, return null
        return null;
    }

    /**
     * getUsers provides all the events that are attached to a User based on a given username
     * A SQL String is created which is:
     * "SELECT * FROM Users;"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method executes the query
     * The method iterates through the results of the query and creates a new User POJO for every
     *  User that was found to be associated with the given username
     * All associated User POJOs are added to an ArrayList
     * This ArrayList is returned to the calling method, either containing a list of Users or an empty ArrayList
     *
     * If any of the above failed, print the error and throw a new DataAccessException
     *
     * @return provides an ArrayList of User POJOs associated with the given username
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public ArrayList<User> getUsers() throws DataAccessException {
        System.out.println("Getting all Users . . . ");
        String sql = "SELECT * FROM Users;";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The method executes the query
            ResultSet rs = stmt.executeQuery();
//            All associated User POJOs are added to an ArrayList
            ArrayList<User> users = new ArrayList<User>();
//            The method iterates through the results of the query and creates a new User POJO for every User
            while(rs.next()) {
                users.add(new User(rs.getString("UserID"), rs.getString("UserName"),
                        rs.getString("PassWord"), rs.getString("Email"), rs.getString("FirstName"),
                        rs.getString("LastName"), rs.getString("Gender"), rs.getString("PersonID")));
            }
            rs.close();
//            The ArrayList is returned to the calling method, either containing a list of Users or an empty ArrayList
            return users;
        } catch (SQLException e) {
//            If any of the above failed, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException("Error encountered while getting all users");
        }
    }

    /**
     * insertUser inserts a row into the Users table containing all information related to a given user
     * Passwords are hashed before being used in this operation.
     * This is to enforce security by not storing the passwords in plaintext
     * A SQL String is created which is:
     * "INSERT INTO Users (UserID, UserName, PassWord, Email, FirstName, LastName, Gender, PersonID)
     *  VALUES (?,?,?,?,?,?,?,?);"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method attaches all User attributes to the prepared statement and executes it
     *
     * If any of the above failed, print the error and throw a new DataAccessException
     *
     * @param user is the User POJO which is to be inserted into the SQLite database
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public static void insertUser(User user) throws DataAccessException {
        System.out.println("Inserting " + user.getUserID() + " to the Users table . . . ");
        String sql = "INSERT INTO Users (UserID, UserName, PassWord, Email, FirstName, LastName, Gender, PersonID) " +
                "VALUES (?,?,?,?,?,?,?,?);";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The method attaches all User attributes to the prepared statement and executes it
            stmt.setString(1, user.getUserID());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getFirstName());
            stmt.setString(6, user.getLastName());
            stmt.setString(7, user.getGender());
            stmt.setString(8, user.getPersonID());
            stmt.executeUpdate();
        } catch (SQLException e) {
//            If any of the above failed, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting User into the database");
        }
    }

    /**
     * loginUser performs functionality to verify whether a username and password combination is correct for a single user
     * If the username and password combo is correct, it return true. Otherwise, it returns false
     * Passwords are hashed before being used in this operation
     * A SQL String is created which is:
     * "SELECT * FROM Users WHERE UserName = ? AND PassWord = ?;"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method attaches the username and password to the prepared statement and executes it
     * If something was returned from the SQL query, return true.  Else, return false
     *
     * If any of the above failed, print the error and throw a new DataAccessException
     *
     * @param username
     * @param password
     * @return
     * @throws DataAccessException
     */
    public static boolean loginUser(String username, String password) throws DataAccessException {
        System.out.println("Verifying Username/Password combination . . . \n");
        String sql = "SELECT * FROM Users WHERE UserName = ? AND PassWord = ?;";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The method attaches the username and password to the prepared statement and executes it
            stmt.setString(1,username);
            stmt.setString(2,password);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
//                If something was returned from the SQL query, return true.
                rs.close();
                return true;
            }
        } catch (SQLException e) {
//            If any of the above failed, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException("Error encountered while verifying username/password");
        }
//        If no username/password combo was found, return false
        return false;
    }

    /**
     * clearUsers clears all rows from the Users table
     * A SQL String is created which is:
     * "DELETE FROM Users;"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The prepared statement is executed
     * If an error occurs, it is printed and a DataAccessException is thrown
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public void clearUsers() throws DataAccessException {
        System.out.println("Clearing the Users table . . . ");
        String sql = "DELETE FROM Users;";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The prepared statement is executed
            stmt.executeUpdate();
        } catch (SQLException e) {
//            If an error occurs, it is printed and a DataAccessException is thrown
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing or repopulating database");
        }
    }

    /**
     * usernameExists provides info about whether a given username is associated with a User that exists in the Users table
     * A SQL String is created which is:
     * "SELECT * FROM Users WHERE UserName = ?;"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method attaches the username to the prepared statement and executes it
     * If something was returned from the SQL query, return true
     * If nothing was returned from the SQL query, return false
     *
     * If any of the above failed, print the error and throw a new DataAccessException
     *
     * @param username is the username that was passed by the calling method
     * @return provides the status of whether the username exists in the database
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public boolean usernameExists(String username) throws DataAccessException {
        System.out.println("Checking whether username: " + username + " exists in the database . . . ");
        String sql = "SELECT * FROM Users WHERE UserName = ?;";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The method attaches the username to the prepared statement and executes it
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
//                If something was returned from the SQL query, return true
                rs.close();
                System.out.println(username + " exists in the database");
                return true;
            }
        } catch (SQLException e) {
//            If any of the above failed, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException("Error encountered while retrieving User from database");
        }
//        If nothing was returned from the SQL query, return false
        System.out.println(username + " does not exist in the database");
        return false;
    }

    /**
     * getUserID gets the userID that is associated with a User that has a given username
     * A SQL String is created which is:
     * "SELECT UserID FROM Users WHERE UserName = ?;"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method attaches the username to the prepared statement and executes it
     * If something was returned from the SQL query, return the userID associated with the result
     * If nothing was returned from the SQL query, return null
     *
     * If any of the above failed, print the error and throw a new DataAccessException
     *
     * @param username is the username that was passed by the calling method
     * @return provides the userID associated with a given username
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public String getUserID(String username) throws DataAccessException {
        System.out.println("Getting userID associated with username: " + username + " . . . ");
        String sql = "SELECT UserID FROM Users WHERE UserName = ?;";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The method attaches the username to the prepared statement and executes it
            stmt.setString(1,username);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
//                If something was returned from the SQL query, return the userID associated with the result
                return rs.getString("UserID");
            }
        } catch (SQLException e) {
//            If any of the above failed, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException("Error encountered while getting the UserID");
        }
//        If nothing was returned from the SQL query, return null
        return null;
    }
}
