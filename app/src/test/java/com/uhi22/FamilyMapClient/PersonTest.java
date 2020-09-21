/**
 * This Class is built to test functionality for the application's PERSON network functions
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient;

import com.uhi22.FamilyMapClient.NetworkServices.ServerProxy;
import com.uhi22.shared.Requests.RegisterRequest;
import com.uhi22.shared.Responses.AllPersonsResponse;
import com.uhi22.shared.Responses.PersonResponse;
import com.uhi22.shared.Responses.RegisterResponse;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PersonTest {

//    Declare variables that are necessary through multiple methods
    private ServerProxy serverProxy;
    private int port;
    private String host, personID, authToken;
    private PersonResponse personResponse;
    private AllPersonsResponse allPersonsResponse;

    /**
     * Constructor to initialize vital variables
     */
    public PersonTest() {
//        Establish network information
        serverProxy = new ServerProxy();
        host = "192.168.1.193";
        port = 8080;
        personID = null;
        authToken = null;
        personResponse = new PersonResponse();
        allPersonsResponse = new AllPersonsResponse();
    }

    /**
     * This method prepares the database for the tests below to successfully have access to
     * reliable data
     * @throws IOException This happens when a URL is malformed or when data cannot be read from / written to
     * the HttpConnection
     */
    @Before
    public void setUp() throws IOException {
//        Clear the database to prepare it for the tests
        serverProxy.clear(host, port);
//        Create a RegisterRequest filled with dummy data
        RegisterRequest registerRequest = new RegisterRequest("test", "test", "test", "test", "test", "m");
//        Obtain the authToken and personID returned from the registration operation
        RegisterResponse registerResponse = serverProxy.register(registerRequest, host, port);
        authToken = registerResponse.getAuthToken();
        personID = registerResponse.getPersonID();
    }

    /**
     * This method tests scenarios where getting a single person returned successfully from the server
     */
    @Test
    public void getPersonPass() {
        try {
//            Perform the GET Request
            personResponse = serverProxy.getPerson(personID, authToken, host, port);
//            Verify that the server responded correctly to the valid HTTP Request
            assertTrue(personResponse.getSuccess());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * This method tests scenarios where there is no authToken (which will fail)
     */
    @Test
    public void getPersonFail() {
        try {
//            Perform the invalid GET request (no authToken header)
            personResponse = serverProxy.getPerson(personID, null, host, port);
//            Verify that the server returned an unsuccessful response for the invalid request
            assertFalse(personResponse.getSuccess());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * This method tests scenarios where a valid request is performed to get all Persons for an authenticated User
     */
    @Test
    public void getAllPersonPass() {
        try {
//            Perform the valid GET request for all Persons associated with the authenticated user
            allPersonsResponse = serverProxy.getPersons(authToken, host, port);
//            Verify that the server returned a successful response for the valid request
            assertTrue(allPersonsResponse.getSuccess());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * This method tests scenarios where an invalid request is performed to get all Persons associated with a User
     * (No authToken)
     */
    @Test
    public void getAllPersonsFail() {
        try {
//            Perform invalid GET request for all Persons associated with authenticated User
            allPersonsResponse = serverProxy.getPersons(null, host, port);
//            Verify that the server returned an unsuccessful response for the invalid request
            assertFalse(allPersonsResponse.getSuccess());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
