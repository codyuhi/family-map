/** The AuthTokenDao class is used as an interface between the Java-enabled server and the database which holds the persistent data for the application.
 *  Its primary objective is to support the functionality of Authentication Tokens which allow for session management between the client and server
 *  Session management allows for Confidentiality, Integrity, and Availability for privileged data such that users must:
 *      Authenticate with the server (prove they are who they say they are)
 *      Be Authorized to interact with the database for the data requested based on their authenticated identity
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.familymapserver.DataAccess;

import com.uhi22.familymapserver.Errors.DataAccessException;
import com.uhi22.shared.model.AuthorizationToken;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthTokenDao {

    /**
     * The Connection denotes the connection to the database that was initiated by the calling method
     */
    private static Connection conn;

    /**
     * The constructor takes the Database Connection that was passed by the calling method and makes it available within this class
     * @param conn the given connection to the database instance
     */
    public AuthTokenDao(Connection conn) {
        AuthTokenDao.conn = conn;
    }

    /**
     * getUserID provides the UserID that is attached to the given authToken
     * A SQL String is created which is:
     * "SELECT * FROM AuthorizationTokens WHERE AuthKey = ?;"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method attaches the UserID to the prepared statement and executes the query
     * If something was returned from the SQL query, return the UserID that was found
     * If nothing was returned in the SQL query, return null
     *
     * If any of the above failed, print the error and throw a new DataAccessException
     *
     * @param authToken is the AuthorizationToken that was passed by the calling method
     * @return provides the UserID that is attached to the given authToken
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public String getUserID(String authToken) throws DataAccessException {
        System.out.println("Getting userID from authToken: " + authToken + " . . . ");
        String sql = "SELECT * FROM AuthorizationTokens WHERE AuthKey = ?;";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The method attaches the UserID to the prepared statement and executes the query
            stmt.setString(1, authToken);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
//                If something was returned from the SQL query, return the UserID that was found
                return rs.getString("UserID");
            }
        } catch (SQLException e) {
//            If any of the above failed, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException("Error encountered while getting the User ID from the Auth Token");
        }
//        If nothing was returned in the SQL query, return null
        return null;
    }

    /**
     * insertAuthToken inserts a row into the AuthorizationTokens table containing the UserID and the authToken
     * A SQL String is created which is:
     * "INSERT INTO AuthorizationTokens (TokenID, AuthKey, UserID) VALUES (?,?,?);"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The method attaches the tokenID, authKey, and userID to the prepared statement and executes it
     *
     * If any of the above failed, print the error and throw a new DataAccessException
     *
     * @param id is the TokenID that was passed by the calling method
     * @param token is the authToken that was passed by the calling method
     * @return provides the authToken's AuthKey back to the calling method
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public static String insertAuthToken(String id, AuthorizationToken token) throws DataAccessException{
        System.out.println("Inserting " + id + " to the AuthorizationTokens table . . . ");
        String sql = "INSERT INTO AuthorizationTokens (TokenID, AuthKey, UserID) " +
                "VALUES (?,?,?);";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The method attaches the tokenID, authKey, and userID to the prepared statement and executes it
            stmt.setString(1, id);
            stmt.setString(2,token.getAuthKey());
            stmt.setString(3,token.getUserID());
            stmt.executeUpdate();
        } catch (SQLException e) {
//            If any of the above failed, print the error and throw a new DataAccessException
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting AuthToken into the database");
        }
//        Return the originally provided authKey
        return token.getAuthKey();
    }

    /**
     * clearAuthTokens clears all rows from the Authorization table
     * A SQL String is created which is:
     * "DELETE FROM AuthorizationTokens;"
     * This SQL String is used to create a prepared statement to prevent SQL Injection
     * The prepared statement is executed
     * If an error occurs, it is printed and a DataAccessException is thrown
     * @throws DataAccessException handles SQLExceptions that can take place during interaction with the SQLite DB
     */
    public void clearAuthTokens() throws DataAccessException {
        System.out.println("Clearing the authTokens table . . . ");
        String sql = "DELETE FROM AuthorizationTokens;";
//        This SQL String is used to create a prepared statement to prevent SQL Injection
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
//            The prepared statement is executed
            stmt.executeUpdate();
        } catch (SQLException e) {
//            If an error occurs, it is printed and a DataAccessException is thrown
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing AuthToken table");
        }
    }
}
