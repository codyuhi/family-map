/**
 * This Class is built to test functionality for the application's SEARCH function
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient;

import com.uhi22.FamilyMapClient.Services.LoadSampleDataService;
import com.uhi22.FamilyMapClient.Services.RelativeService;
import com.uhi22.FamilyMapClient.Services.SearchService;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class SearchTest {

//    Declare variables that are necessary through multiple methods
    private ServerProxy serverProxy;
    private String host;
    private int port;
    private ArrayList<String> allFatherSideIDs, allMotherSideIDs;
    private ArrayList<Person> allPersons;
    private ArrayList<Event> allEvents;

    /**
     * Constructor to initialize vital variables
     */
    public SearchTest() {
//        Establish network information
        serverProxy = new ServerProxy();
        host = "192.168.1.193";
        port = 8080;
        allPersons = new ArrayList<>();
        allEvents = new ArrayList<>();
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
//            Login with the default User to obtain an authToken and gain access to the User's resources on the server
            LoginResponse loginResponse = new LoginResponse();
            loginResponse = serverProxy.login(host, port, "sheila", "parker", new URL("http://" + host + ":" + port + "/user/login"), loginResponse);
//            If the loadResponse and loginResponse results are both successful, continue
            if(loadResponse.getSuccess() && loginResponse.getSuccess()) {
//                Get all the Persons from the server
                AllPersonsResponse allPersonsResponse = serverProxy.getPersons(loginResponse.getAuthToken(), host, port);
                allPersons = allPersonsResponse.getData();
//                Get all the Events from the server
                AllEventsResponse allEventsResponse = serverProxy.getEvents(loginResponse.getAuthToken(), host, port);
                allEvents = allEventsResponse.getData();
//                Get the person that is associated with the root User
                PersonResponse personResponse = serverProxy.getPerson(loginResponse.getPersonID(), loginResponse.getAuthToken(), host, port);
                Person rootPerson = new Person(personResponse.getPersonID(), personResponse.getAssociatedUsername(),
                        personResponse.getFirstName(), personResponse.getLastName(), personResponse.getFatherID(),
                        personResponse.getMotherID(), personResponse.getSpouseID(), personResponse.getGender());
//                Get the IDs for people on the father's side and the mother's side of the root User's family
                allFatherSideIDs = RelativeService.getFatherIdEvents(rootPerson, allPersons);
                allMotherSideIDs = RelativeService.getMotherEventIDs(rootPerson, allPersons);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method tests scenarios where the search should produce an expected result
     */
    @Test
    public void searchPass() {
        try {
//            Test to see if searching "Sheila" produces the search result, "Sheila" in the search view
            assertEquals("Sheila", SearchService.getSuggestedPersons("Sheila", allPersons).get(0).getFirstName());
//            Test to see if searching "birth" produces the search result, "birth" in the search view
            assertEquals("birth", SearchService.getSuggestedEvents("birth", allEvents, allPersons, allFatherSideIDs,
                    allMotherSideIDs, true, true, true, true).get(0).getEventType());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * This method tests scenarios where the search is compared with an unexpected result
     * This will make sure that irrelevant data is not crowded with relevant results
     */
    @Test
    public void searchFail() {
        try {
//            Test to see if searching "Sheila" does not populate with irrelevant data
            assertFalse(getIds("person", SearchService.getSuggestedPersons("Sheila", allPersons), null).contains("IMPOSSIBLE ID"));
//            Test to see if searching "birth" does not populate with irrelevant data
            assertFalse(getIds("event", null, SearchService.getSuggestedEvents("birth", allEvents, allPersons, allFatherSideIDs,
                    allMotherSideIDs, true, true, true, true)).contains("IMPOSSIBLE ID"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * This is a helper method which is used to test whether the given irrelevant String exists in the search suggestion results
     *
     * @param type This String denotes whether the method is going to evaluate a group of persons or events
     * @param suggestedPersons This ArrayList of Person objects contains the result of the Person search suggestion evaluation
     * @param suggestedEvents This ArrayList of Event objects contains the result of the Event search suggestion evaluation
     * @return the result that is returned is an ArrayList of Strings that contains the IDs for all the objects in the given ArrayList, based on the type of request
     */
    private ArrayList<String> getIds(String type, ArrayList<Person> suggestedPersons, ArrayList<Event> suggestedEvents) {
        ArrayList<String> result = new ArrayList<String>();
        if(type.equals("person")) {
            for(Person p : suggestedPersons) {
                result.add(p.getPersonID());
            }
            return result;
        }

        for(Event e : suggestedEvents) {
            result.add(e.getEventID());
        }
        return result;
    }
}
