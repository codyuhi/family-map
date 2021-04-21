/**
 * This Class is built to test functionality for the application's FAMILY RELATIONSHIP functions
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient;

import com.uhi22.FamilyMapClient.Services.LoadSampleDataService;
import com.uhi22.FamilyMapClient.Services.RelativeService;
import com.uhi22.FamilyMapClient.NetworkServices.ServerProxy;
import com.uhi22.shared.Requests.LoadRequest;
import com.uhi22.shared.Responses.AllPersonsResponse;
import com.uhi22.shared.Responses.LoginResponse;
import com.uhi22.shared.Responses.PersonResponse;
import com.uhi22.shared.Responses.Response;
import com.uhi22.shared.model.Person;

import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FamilyRelationshipsTest {

//    Declare variables that are necessary through multiple methods
    private ServerProxy serverProxy;
    private String host;
    private int port;
    private ArrayList<Person> allPersons;
    private Person rootPerson;

    /**
     * Constructor to initialize vital variables
     */
    public FamilyRelationshipsTest() {
//        Establish network information
        serverProxy = new ServerProxy();
        host = "192.168.1.193";
        port = 8080;
        allPersons = new ArrayList<>();
        rootPerson = null;
    }

//    This method defines the vital variables and populates the server with sample data
    @Before
    public void setUp() {
        try {
//            Create a LoadRequest that will populate the server with the sample data
            LoadRequest loadRequest;
            loadRequest = LoadSampleDataService.buildLoadRequest();
            Response loadResponse = serverProxy.loadToServer(loadRequest, host, port);
//            Login with the default User to obtain an authToken and gain access to the User's resources on the server
            LoginResponse loginResponse = new LoginResponse();
            loginResponse = serverProxy.login(host, port, "sheila", "parker", new URL("http://" + host + ":" + port + "/user/login"), loginResponse);
            if(loadResponse.getSuccess() && loginResponse.getSuccess()) {
//            If the loadResponse and loginResponse results are both successful, continue
//                Get all the Persons from the server
                AllPersonsResponse allPersonsResponse;
                allPersonsResponse = serverProxy.getPersons(loginResponse.getAuthToken(), host, port);
                allPersons = allPersonsResponse.getData();
                PersonResponse personResponse;
//                Get the person that is associated with the root User
                personResponse = serverProxy.getPerson(loginResponse.getPersonID(), loginResponse.getAuthToken(), host, port);
                rootPerson = new Person(personResponse.getPersonID(), personResponse.getAssociatedUsername(),
                        personResponse.getFirstName(), personResponse.getLastName(), personResponse.getGender(),
                        personResponse.getFatherID(), personResponse.getMotherID(), personResponse.getSpouseID());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method tests scenarios where the correct maternal and paternal relationships are found based on a given Person
     */
    @Test
    public void familyRelationshipsPass() {
        try {
//            Build the list of father and mother ids
            ArrayList<String> fatherIds = RelativeService.getFatherIdEvents(rootPerson, allPersons);
            ArrayList<String> motherIds = RelativeService.getMotherEventIDs(rootPerson, allPersons);
            if(rootPerson.getFatherID() != null) {
//                Verify that the person's father is included in the list of paternal ancestors
                assertTrue(fatherIds.contains(rootPerson.getFatherID()));
            }
            if(rootPerson.getMotherID() != null) {
//                Verify that the person's mother is included in the list of maternal ancestors
                assertTrue(motherIds.contains(rootPerson.getMotherID()));
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * This method tests scenarios where a provided personID is not a family member of the given person
     */
    @Test
    public void familyRelationshipsFail() {
        try {
//            Build the list of father and mother ids
            ArrayList<String> fatherIds = RelativeService.getFatherIdEvents(rootPerson, allPersons);
            ArrayList<String> motherIds = RelativeService.getMotherEventIDs(rootPerson, allPersons);
            if(rootPerson.getFatherID() != null) {
//                Verify that invalid data is not included in the list of paternal ancestors
                assertFalse(fatherIds.contains("RANDOM INCORRECT FATHER ID"));
            }
            if(rootPerson.getMotherID() != null) {
//                Verify that invalid data is not included in the list of maternal ancestors
                assertFalse(motherIds.contains("RANDOM INCORRECT MOTHER ID"));
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
