/**
 * This Class is built to test functionality for the application's CHRONOLOGICAL functions
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient;

import com.uhi22.FamilyMapClient.Services.ChronologicalService;
import com.uhi22.FamilyMapClient.Services.LoadSampleDataService;
import com.uhi22.FamilyMapClient.NetworkServices.ServerProxy;
import com.uhi22.shared.Requests.LoadRequest;
import com.uhi22.shared.Responses.AllEventsResponse;
import com.uhi22.shared.Responses.LoginResponse;
import com.uhi22.shared.Responses.Response;
import com.uhi22.shared.model.Event;

import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

public class ChronologicalTest {

//    Declare variables that are necessary through multiple methods
    private ServerProxy serverProxy;
    private String host;
    private int port;
    private ArrayList<Event> allEvents;

    public ChronologicalTest() {
//        Establish network information
        serverProxy = new ServerProxy();
        host = "192.168.1.193";
        port = 8080;
        allEvents = new ArrayList<>();
    }

    /**
     * This method defines the vital variables and populates the server with sample data
     */
    @Before
    public void setUp() {
        try {
//            Create a LoadRequest that will populate the server with the sample data
            LoadRequest loadRequest;
            loadRequest = LoadSampleDataService.buildLoadRequest();
//            Login with the default User to obtain an authToken and gain access to the User's resources on the server
            Response loadResponse = serverProxy.loadToServer(loadRequest, host, port);
            LoginResponse loginResponse = new LoginResponse();
            loginResponse = serverProxy.login(host, port, "sheila", "parker", new URL("http://" + host + ":" + port + "/user/login"), loginResponse);
//            If the loadResponse and loginResponse results are both successful, continue
            if(loadResponse.getSuccess() && loginResponse.getSuccess()) {
//                Get all the Events from the server
                AllEventsResponse allEventsResponse;
                allEventsResponse = serverProxy.getEvents(loginResponse.getAuthToken(), host, port);
                allEvents = allEventsResponse.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method tests to see if the capability to find a desired event based on its chronological order exists in the ChronologicalService
     */
    @Test
    public void chronologicalPass() {
        try {
//            Verify that the called method finds the earliest event
            assertEquals("BYU_graduation", ChronologicalService.getEarliestEvent(allEvents).getEventID());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * This method tests to see that the ChronologicalService does not accept just any Event as the first event
     */
    @Test
    public void chronologicalFail() {
        try {
//            Pass an EventID which should not be recognized as the first chronological event
//            Verify that the eventID is confirmed not to be the first chron. event
            assertNotEquals("Rodham_Marriage", ChronologicalService.getEarliestEvent(allEvents).getEventID());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
