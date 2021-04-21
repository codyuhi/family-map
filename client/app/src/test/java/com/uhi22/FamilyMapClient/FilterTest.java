/**
 * This Class is built to test functionality for the application's FILTER functions
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient;

import com.uhi22.FamilyMapClient.Services.FilterService;
import com.uhi22.FamilyMapClient.Services.LoadSampleDataService;
import com.uhi22.FamilyMapClient.Services.RelativeService;
import com.uhi22.FamilyMapClient.NetworkServices.ServerProxy;
import com.uhi22.shared.Requests.LoadRequest;
import com.uhi22.shared.Responses.AllEventsResponse;
import com.uhi22.shared.Responses.AllPersonsResponse;
import com.uhi22.shared.Responses.LoginResponse;
import com.uhi22.shared.Responses.PersonResponse;
import com.uhi22.shared.Responses.Response;
import com.uhi22.shared.model.Event;
import com.uhi22.shared.model.Person;

import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FilterTest {

//    Declare variables that are necessary through multiple methods
    private ServerProxy serverProxy;
    private String host;
    private int port;
    private ArrayList<Person> allPersons;
    private ArrayList<Event> allEvents;
    private ArrayList<String> allFatherSideIDs, allMotherSideIDs;
    private Event fatherEvent, motherEvent;

    /**
     * Constructor to initialize vital variables
     */
    public FilterTest() {
//        Establish network information
        serverProxy = new ServerProxy();
        host = "192.168.1.193";
        port = 8080;
        allPersons = new ArrayList<>();
        allEvents = new ArrayList<>();
        fatherEvent = null;
        motherEvent = null;
    }

    /**
     * This method defines the vital variables and populates the server with sample data
     */
    @Before
    public void setUp() {
        try {
//            Create a LoadRequest that will populate the server with the sample data
            LoadRequest loadRequest = LoadSampleDataService.buildLoadRequest();
            Response loadResponse = serverProxy.loadToServer(loadRequest, host, port);
//            Login with the default User to otain an authToken
            LoginResponse loginResponse = new LoginResponse();
            loginResponse = serverProxy.login(host, port, "sheila", "parker", new URL("http://" + host + ":" + port + "/user/login"), loginResponse);
//            If the load and login operations were successful, continue
            if(loadResponse.getSuccess() && loginResponse.getSuccess()) {
//                Get all the Persons and Events in the database associated with the authenticated User
                AllPersonsResponse allPersonsResponse = serverProxy.getPersons(loginResponse.getAuthToken(), host, port);
                allPersons = allPersonsResponse.getData();
                AllEventsResponse allEventsResponse = serverProxy.getEvents(loginResponse.getAuthToken(), host, port);
                allEvents = allEventsResponse.getData();
                PersonResponse personResponse = serverProxy.getPerson(loginResponse.getPersonID(), loginResponse.getAuthToken(), host, port);
                Person rootPerson = new Person(personResponse.getPersonID(), personResponse.getAssociatedUsername(),
                        personResponse.getFirstName(), personResponse.getLastName(), personResponse.getFatherID(),
                        personResponse.getMotherID(), personResponse.getSpouseID(), personResponse.getGender());
                allFatherSideIDs = RelativeService.getFatherIdEvents(rootPerson, allPersons);
                allMotherSideIDs = RelativeService.getMotherEventIDs(rootPerson, allPersons);
//                Get all the events associated with the father and mother
                for(Event e : allEvents) {
                    if(e.getPersonID().equals(rootPerson.getFatherID())) {
                        fatherEvent = e;
                    } else if(e.getPersonID().equals(rootPerson.getMotherID())) {
                        motherEvent = e;
                    }
                    if(motherEvent != null && fatherEvent != null) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method tests scenarios where the provided event should be accepted and loaded on the screen based on the filter
     */
    @Test
    public void filterPass() {
        try {
//            Sadly, these are double negatives.
//            This verifies that the motherEvents are accepted and loaded to the screen based on the applied filters
            assertFalse(FilterService.failFilter(motherEvent, allPersons, allFatherSideIDs, allMotherSideIDs, true, true, true, true));
//            This verifies that the fatherEvents are accepted and loaded to the screen based on the applied filters
            assertFalse(FilterService.failFilter(fatherEvent, allPersons, allFatherSideIDs, allMotherSideIDs, true, true, true, true));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * This method tests scenarios where the provided event should be rejected and kept from loading to the screen based on the filters applied
     */
    @Test
    public void filterFail() {
        try {
//            These verify that father events and mother events are rejected when the appropriate filters are applied, respectively
            assertTrue(FilterService.failFilter(motherEvent, allPersons, allFatherSideIDs, allMotherSideIDs, false, false, false, false));
            assertTrue(FilterService.failFilter(motherEvent, allPersons, allFatherSideIDs, allMotherSideIDs, false, false, false, false));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
