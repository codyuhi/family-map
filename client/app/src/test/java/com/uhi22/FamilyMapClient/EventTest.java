/**
 * This Class is built to test functionality for the application's EVENT network functions
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient;

import com.uhi22.FamilyMapClient.NetworkServices.ServerProxy;
import com.uhi22.shared.Requests.RegisterRequest;
import com.uhi22.shared.Responses.AllEventsResponse;
import com.uhi22.shared.Responses.EventResponse;
import com.uhi22.shared.Responses.RegisterResponse;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class EventTest {

//    Declare variables that are necessary through multiple methods
    private ServerProxy serverProxy;
    private String host, eventID, authToken;
    private int port;
    private EventResponse eventResponse;
    private AllEventsResponse allEventsResponse;

    /**
     * Constructor to initialize vital variables
     */
    public EventTest() {
//        Establish network information
        serverProxy = new ServerProxy();
        host = "192.168.1.193";
        port = 8080;
        eventID = null;
        authToken = null;
        eventResponse = new EventResponse();
        allEventsResponse = new AllEventsResponse();
    }

    /**
     * This method defines the vital variables and populates the server with sample data
     */
    @Before
    public void setUp() throws IOException {
//        Clear the current server data
        serverProxy.clear(host, port);
//        Register a new user and get the authToken
        RegisterRequest registerRequest = new RegisterRequest("test", "test", "test", "test", "test", "m");
        RegisterResponse registerResponse;
        registerResponse = serverProxy.register(registerRequest, host, port);
        authToken = registerResponse.getAuthToken();
//        Get a list of all the events
        allEventsResponse = serverProxy.getEvents(authToken, host, port);
        if(allEventsResponse != null) {
//            Also obtain a single Event that can be used to test the single Event GET request
            eventID = allEventsResponse.getData().get(0).getEventID();
        } else {
            fail("Unable to get any events from the server");
        }
    }

    /**
     * This method tests scenarios where a valid request is provided to get an Event from the server
     */
    @Test
    public void getEventPass() {
        try {
//            Perform the valid GET request
            eventResponse = serverProxy.getEvent(eventID, authToken, host, port);
//            Verify that the GET request was successful
            assertTrue(eventResponse.getSuccess());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * This method tests scenarios where an invalid request is provided to get an Event from the server
     * This fails due to invalid Auth
     */
    @Test
    public void getEventFail() {
        try {
//            Perform the invalid GET request
            eventResponse = serverProxy.getEvent(eventID, null, host, port);
//            Verify that the GET request was unsuccessful
            assertFalse(eventResponse.getSuccess());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * This method tests scenarios where a valid request is provided to get all Events from the server associated with the authenticated User
     */
    @Test
    public void getAllEventsPass() {
        try {
//            Perform the invalid GET request
            allEventsResponse = serverProxy.getEvents(authToken, host, port);
//            Verify that the GET request was unsuccessful
            assertTrue(allEventsResponse.getSuccess());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * This method tests scenarios where an invalid request is provided to get all Events from the server associated with an authenticated User
     * This fails due to invalid Auth
     */
    @Test
    public void getAllEventsFail() {
        try {
//            Perform the invalid GET request
            allEventsResponse = serverProxy.getEvents(null, host, port);
//            Verify that the GET request was unsuccessful
            assertFalse(allEventsResponse.getSuccess());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
