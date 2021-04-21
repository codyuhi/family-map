/**
 * This activity populates the screen with a MapFragment much like the MainActivity,
 * but without the Options that the MainActivity has in the action bar
 * This activity can be reached by searching for an event and selecting from the SearchActivity
 * or by selecting an Event from the PersonActivity
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.uhi22.FamilyMapClient.Fragments.MapFragment;
import com.uhi22.FamilyMapClient.R;
import com.uhi22.FamilyMapClient.NetworkServices.EventService;
import com.uhi22.FamilyMapClient.NetworkServices.PersonService;
import com.uhi22.shared.Responses.AllEventsResponse;
import com.uhi22.shared.Responses.AllPersonsResponse;
import com.uhi22.shared.Responses.EventResponse;
import com.uhi22.shared.Responses.PersonResponse;
import com.uhi22.shared.model.Event;
import com.uhi22.shared.model.Person;

import java.util.ArrayList;

public class EventActivity extends AppCompatActivity {

    //    Declare variables that will be used throughout the activity
    private static String authToken, host, port, activeEventID, activePersonID, rootId;
    private static Person rootPerson, aPerson;
    private static Event aEvent;
    private static ArrayList<Person> allPersons;
    private static ArrayList<Event> allEvents;
//    Declare the filter settings
    private boolean lifeStoryEnabled, familyTreeEnabled, spouseLineEnabled, fatherEnabled, motherEnabled, maleEnabled, femaleEnabled;

    /**
     * This method runs when the Activity is first created
     * On the Event Activity, this defines the variables that will be used throughout the other methods in the activity
     * The method defines these variables by pulling the information provided in the Intent that started the activity
     * The method also performs a network call to the server to obtain information about the Persons and Events on the server
     * After this is all done, the map fragment will be loaded to the screen
     *
     * @param savedInstanceState is the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Define the activity layout and instance state
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);

//        Define variables that are used throughout the activity based on information that was provided in the passed intent
        Intent intent = getIntent();
        authToken = intent.getStringExtra("authToken");
        activeEventID = intent.getStringExtra("EventID");
        activePersonID = intent.getStringExtra("PersonID");
        lifeStoryEnabled = intent.getExtras().getBoolean("lifeStoryEnabled");
        familyTreeEnabled = intent.getExtras().getBoolean("familyTreeEnabled");
        spouseLineEnabled = intent.getExtras().getBoolean("spouseLineEnabled");
        fatherEnabled = intent.getExtras().getBoolean("fatherEnabled");
        motherEnabled = intent.getExtras().getBoolean("motherEnabled");
        maleEnabled = intent.getExtras().getBoolean("maleEnabled");
        femaleEnabled = intent.getExtras().getBoolean("femaleEnabled");
        rootId = intent.getStringExtra("RootID");
        host = intent.getStringExtra("host");
        port = intent.getStringExtra("port");

//        Define the allPersons and allEvents lists from a network call
        allPersons = new ArrayList<Person>();
        allEvents = new ArrayList<Event>();
        try {
            obtainData();
//            define the root user's person, found from the list of persons returned from the server
            rootPerson = findRoot();
//            Load the MapFragment to the layout
            loadMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to iterate through the list of Persons returned by the server and determine which Person is the root user's associated person
     * @return The root user's associated person
     */
    private Person findRoot() {
        for(Person person : allPersons) {
//            Person is determined by comparing the rootUser's personID with the evaluated person object
            if(person.getPersonID().equals(rootId)) {
                return person;
            }
        }
        return null;
    }

    /**
     * This method populates the MainActivity with the MapFragment
     * See the MapFragment Class for more information regarding the MapFragment
     */
    private void loadMap() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        Define the MapFragment class member with method call
        MapFragment mapFragment = new MapFragment();
//        Display the map fragment and commit to screen
        fragmentTransaction.replace(R.id.fragment, mapFragment);
        fragmentTransaction.commit();
    }

    /**
     * This method starts the network call which will ultimately populate the allPersons and allEvents ArrayLists
     * @throws Exception happens if there is an incorrectly defined Person object detected in the list of returned Person Objects
     */
    private void obtainData() throws Exception {
//        Create new PersonService which will interact with the server
        PersonService personService = new PersonService(this.getApplicationContext(), host, Integer.parseInt(port));
//        Perform the GET request the person associated with the authenticated User
        PersonResponse personResponse = personService.getPerson(activePersonID, authToken);
//        If the response was successful, populate the aPerson class object
        if(personResponse.getSuccess()) {
            aPerson = new Person(personResponse.getPersonID(),
                    personResponse.getAssociatedUsername(),
                    personResponse.getFirstName(),
                    personResponse.getLastName(),
                    personResponse.getGender(),
                    personResponse.getFatherID(),
                    personResponse.getMotherID(),
                    personResponse.getSpouseID());
        } else {
//            If the Person is incorrectly defined for whatever reason, throw exception and exit
            throw new Exception();
        }

//        Get all the Person objects from the database
        AllPersonsResponse allPersonsResponse = personService.getAllPersons(authToken);
//        If the request was successful, add all those Person Objects to the ArrayList
        if(allPersonsResponse.getSuccess()) {
            try {
                allPersons.addAll(allPersonsResponse.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new Exception();
        }

//        Perform the same functionality given above, but for an Event object and ArrayList
        EventService eventService = new EventService(this.getApplicationContext(), host, Integer.parseInt(port));
        EventResponse eventResponse = eventService.getEvent(activeEventID, authToken);
        if(eventResponse.getSuccess()) {
            aEvent = new Event();
            aEvent.setEventID(eventResponse.getEventID());
            aEvent.setAssociatedUsername(eventResponse.getAssociatedUsername());
            aEvent.setPersonID(eventResponse.getPersonID());
            aEvent.setLatitude(eventResponse.getLatitude());
            aEvent.setLongitude(eventResponse.getLongitude());
            aEvent.setCountry(eventResponse.getCountry());
            aEvent.setCity(eventResponse.getCity());
            aEvent.setEventType(eventResponse.getEventType());
            aEvent.setYear(eventResponse.getYear());
        }

        AllEventsResponse allEventsResponse = eventService.getAllEvents(authToken);
        if(allEventsResponse.getSuccess()) {
            allEvents.addAll(allEventsResponse.getData());
        } else {
            throw new Exception();
        }
    }

    /**
     * Setter for the aEvent variable
     * @param aEvent is the given aEvent variable to be set
     */
    public void setaEvent(Event aEvent) {
        EventActivity.aEvent = aEvent;
    }

    /**
     * Setter for the aPerson variable
     * @param aPerson is the given aPerson variable to be set
     */
    public void setaPerson(Person aPerson) {
        EventActivity.aPerson = aPerson;
    }

    /**
     * Getter for the rootPerson variable
     * @return returns the rootPerson
     */
    public Person getRootPerson() {
        return rootPerson;
    }

    /**
     * Getter for the allPersons variable
     * @return returns the allPersons
     */
    public ArrayList<Person> getAllPersons() {
        return allPersons;
    }

    /**
     * Getter for the allEvents variable
     * @return returns the allEvents
     */
    public ArrayList<Event> getAllEvents() {
        return allEvents;
    }

    /**
     * Getter for the authToken variable
     * @return returns the authToken
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Getter for the aEvent variable
     * @return returns the aEvent
     */
    public Event getaEvent() {
        return aEvent;
    }

    /**
     * Getter for the aPerson variable
     * @return returns the aPerson
     */
    public Person getaPerson() {
        return aPerson;
    }

    /**
     * Getter for the host variable
     * @return returns the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Getter for the port variable
     * @return returns the port
     */
    public String getPort() {
        return port;
    }

    /**
     * Getter for the lifeStoryEnabled variable
     * @return returns the lifeStoryEnabled
     */
    public boolean getLifeStoryEnabled() {
        return lifeStoryEnabled;
    }

    /**
     * Getter for the familyTreeEnabled variable
     * @return returns the familyTreeEnabled
     */
    public boolean getFamilyTreeEnabled() {
        return familyTreeEnabled;
    }

    /**
     * Getter for the spouseLineEnabled variable
     * @return returns the spouseLineEnabled
     */
    public boolean getSpouseLineEnabled() {
        return spouseLineEnabled;
    }

    /**
     * Getter for the fatherEnabled variable
     * @return returns the fatherEnabled
     */
    public boolean getFatherEnabled() {
        return fatherEnabled;
    }

    /**
     * Getter for the motherEnabled variable
     * @return returns the motherEnabled
     */
    public boolean getMotherEnabled() {
        return motherEnabled;
    }

    /**
     * Getter for the maleEnabled variable
     * @return returns the maleEnabled
     */
    public boolean getMaleEnabled() {
        return maleEnabled;
    }

    /**
     * Getter for the femaleEnabled variable
     * @return returns the femaleEnabled
     */
    public boolean getFemaleEnabled() {
        return femaleEnabled;
    }
}
