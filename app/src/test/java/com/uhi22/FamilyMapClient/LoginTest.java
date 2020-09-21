/**
 * This Class is built to test functionality for the application's LOGIN network function
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient;

import com.uhi22.FamilyMapClient.NetworkServices.ServerProxy;
import com.uhi22.shared.Requests.LoginRequest;
import com.uhi22.shared.Responses.LoginResponse;

import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LoginTest {

//    Declare variables that are necessary through multiple methods
    private ServerProxy serverProxy;
    private String host, username, password;
    private int port;
    private URL loginUrl;
    private LoginResponse loginResponse;
    private LoginRequest loginRequest;

    /**
     * Constuctor to initialize vital variables
     */
    public LoginTest() {
//        Establish network information
        serverProxy = new ServerProxy();
        host = "192.168.1.193";
        port = 8080;
        username = "test";
        password = "test";
        try {
//            Build a URL based on the network information
            loginUrl = new URL("http://" + host + ":" + port + "/user/login/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        loginResponse = new LoginResponse();
        loginRequest = new LoginRequest();
    }

    /**
     * This method tests scenarios where the user should be able to login successfully
     */
    @Test
    public void loginPass() {
        try {
//            Create a register test class member and register for a new user
            RegisterTest registerTest = new RegisterTest();
            registerTest.registerPass();
//            Login with the newly created user
            loginResponse = serverProxy.login(host, port, username, password, loginUrl, loginResponse);
//            Verify that the login request was successful
            assertTrue(loginResponse.getSuccess());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * This method tests scenarios where the user should not be allowed access to the system
     * This is done by providing false information that should fail
     */
    @Test
    public void loginFail() {
        try {
//            Create invalid login credentials
            username = "FAIL";
            password = "FAIL";
//            Set the failed credentials in the login request
            loginRequest.setUserName("FAIL");
            loginRequest.setPassword("FAIL");
//            Perform the invalid login operation
            loginResponse = serverProxy.login(host, port, username, password, loginUrl, loginResponse);
//            Verify that the invalid login attempt was unsuccessful
            assertFalse(loginResponse.getSuccess());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
