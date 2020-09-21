/** The Database class is used to provide access to the SQLite database which houses all persistent data for the application
 *  Its primary objective is to support the functionality of database access by the following:
 *      Open connections with the database
 *      Define database access/resources
 *      Close connections with the database
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.familymapserver.DataAccess;

import com.uhi22.familymapserver.Errors.DataAccessException;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    /**
     * The connection denotes the connection to the SQLite database
     */
    private Connection conn;

    /**
     * openConnection opens and returns a connection to the database that is found in the SQLite file
     *
     * The database file's path is provided and a connection is assigned to it
     * The auto-commit property of the connection is set to false to support fail-safe defaults design principles
     *
     * If any of the above failed, print the error and throw a new DataAccessException
     *
     * If all the above worked properly, the newly established connection is returned to the calling method
     *
     * @return the successfully established connection to the database
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public Connection openConnection() throws DataAccessException {
        System.out.println("Opening a new connection to the SQLite database . . . ");
        try {
//            The database file's path is provided and a connection is assigned to it
            final String CONNECTION_URL = "jdbc:sqlite:db/familymap.sqlite";
            conn = DriverManager.getConnection(CONNECTION_URL);
//            The auto-commit property of the connection is set to false to support fail-safe defaults design principles
            conn.setAutoCommit(false);

        } catch (SQLException e) {
//            If any of the above failed, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException("Unable to Open Database Connection");
        }
//        If all the above worked properly, the newly established connection is returned to the calling method
        return conn;
    }

    /**
     * getConnection is a getter method which provides the private connection for this class
     *
     * If the connection has not yet been defined, open a new connection to the database and return that
     *
     * @return the connection to the database for this class instance
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public Connection getConnection() throws DataAccessException {
        System.out.println("Getting connection to the database . . . ");
        if(conn == null) {
//            If the connection has not yet been defined, open a new connection to the database and return that
            return openConnection();
        } else {
            return conn;
        }
    }

    /**
     * closeConnection closes the connection to the database and either opts to commit or rollback
     * any changes made depending on whether a desired outcome occurred during database operations
     *
     * If the commit is desired, commit changes.  If not, roll the changes back
     * Close the connection to the database and nullify the connection variable
     *
     * If anything failed above, print the error and throw a new DataAccessException
     *
     * @param commit is a boolean variable which denotes whether the connection should commit or rollback changes
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public void closeConnection(boolean commit) throws DataAccessException {
        System.out.println("Closing connection to the database with a " + commit + " commit value . . . ");
        try {
//            If the commit is desired, commit changes.  If not, roll the changes back
            if(commit) {
                conn.commit();
            } else {
                conn.rollback();
            }
//            Close the connection to the database and nullify the connection variable
            conn.close();
            conn = null;
        } catch (SQLException e) {
//            If anything failed above, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException("Unable to Close Connection");
        }
    }

    /**
     * createTables creates the initial tables in the SQLite database and defines table fields
     *
     * Create a Users table, an AuthorizationTokens table, a Persons table, and an Events table
     * Execute the update on the SQLite database
     *
     * If anything failed above, print the error and throw a new DataAccessException
     *
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public void createTables() throws DataAccessException {
        System.out.println("Creating tables for the SQLite database . . . ");
        try (Statement stmt = conn.createStatement()) {
//            Create a Users table, an AuthorizationTokens table, a Persons table, and an Events table
            String sql = "CREATE TABLE IF NOT EXISTS Users" +
                    "(" +
                    "UserID TEXT PRIMARY KEY UNIQUE, " +
                    "UserName TEXT NOT NULL UNIQUE, " +
                    "PassWord TEXT NOT NULL, " +
                    "Email TEXT NOT NULL UNIQUE, " +
                    "FirstName TEXT NOT NULL, " +
                    "LastName TEXT NOT NULL, " +
                    "Gender TEXT CHECK(Gender IN ('f','m')) NOT NULL," +
                    "PersonID TEXT NOT NULL UNIQUE);" +

                    "CREATE TABLE IF NOT EXISTS AuthorizationTokens" +
                    "(" +
                    "TokenID TEXT PRIMARY KEY UNIQUE, " +
                    "AuthKey TEXT NOT NULL UNIQUE, " +
                    "UserID TEXT NOT NULL);" +

                    "CREATE TABLE IF NOT EXISTS Persons" +
                    "(" +
                    "PersonID TEXT PRIMARY KEY UNIQUE, " +
                    "AssociatedUserName TEXT, " +
                    "FirstName TEXT NOT NULL, " +
                    "LastName TEXT NOT NULL, " +
                    "Gender TEXT CHECK(Gender IN ('f', 'm')) NOT NULL, " +
                    "FatherID TEXT, " +
                    "MotherID TEXT, " +
                    "SpouseID TEXT);" +

                    "CREATE TABLE IF NOT EXISTS Events" +
                    "(" +
                    "EventID TEXT PRIMARY KEY UNIQUE, " +
                    "AssociatedUserName TEXT, " +
                    "Latitude NUMBER, " +
                    "Longitude NUMBER, " +
                    "Country TEXT, " +
                    "City TEXT, " +
                    "EventType TEXT, " +
                    "Year INTEGER);";
//            Execute the update on the SQLite database
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
//            If anything failed above, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException("SQL Error encountered while creating tables");
        }
    }

    /**
     * clearTables clears all contents from all the tables in the SQLite database.  This maintains the tables' existence,
     * but all rows that were contained in them are deleted
     *
     * Delete all from the Users table, the Persons table, the Events table, and the AuthorizationTokens table
     * Execute the update on the SQLite database
     *
     * If anything failed above, print the error and throw a new DataAccessException
     *
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public void clearTables() throws DataAccessException {
        try(Statement stmt = conn.createStatement()) {
//            Delete all from the Users table, the Persons table, the Events table, and the AuthorizationTokens table
            String sql = "DELETE FROM Users; " +
                    "DELETE FROM Persons; " +
                    "DELETE FROM Events; " +
                    "DELETE FROM AuthorizationTokens; ";
//            Execute the update on the SQLite database
            stmt.executeUpdate(sql);
        } catch(SQLException e) {
//            If anything failed above, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException("SQL Error occured while clearing tables");
        }
    }
}
