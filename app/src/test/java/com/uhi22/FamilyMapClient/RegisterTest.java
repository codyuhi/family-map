/**
 * This Class is built to test functionality for the application's REGISTER network function
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient;

import com.uhi22.FamilyMapClient.NetworkServices.ServerProxy;
import com.uhi22.shared.Requests.RegisterRequest;
import com.uhi22.shared.Responses.RegisterResponse;

import org.junit.Test;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class RegisterTest {

//    Declare variables that are necessary through multiple methods
    private ServerProxy serverProxy;
    private String host;
    private int port;
    private RegisterRequest registerRequest;
    private RegisterResponse registerResponse;

    /**
     * Constructor to initialize vital variables
     */
    public RegisterTest() {
//        Establish network information
        host = "192.168.1.193";
        port = 8080;
        serverProxy = new ServerProxy();
        registerRequest = new RegisterRequest("test", "test", "test", "test", "test", "m");
        registerResponse = new RegisterResponse();
    }

    /**
     * This method tests a basic scenario where a new user is registered in the application
     */
    @Test
    public void registerPass() {
        try {
//            Clear the server from any existing data
            serverProxy.clear(host, port);
//            Perform the registration request
            registerResponse = serverProxy.register(registerRequest, host, port);
//            Verify that the registration response was successful
            assertTrue(registerResponse.getSuccess());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * This method tests a scenario where a duplicate user is attempted
     * to be added to the database from the application
     */
    @Test
    public void registerFailDuplicate() {
        try {
//            Perform the registration command to add a new user
            registerResponse = serverProxy.register(registerRequest, host, port);
//            Duplicate the user, which should not allow the second user to be added
            registerResponse = serverProxy.register(registerRequest, host, port);
//            Verify that the registration response was unsuccessful for duplicate user
            assertFalse(registerResponse.getSuccess());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
