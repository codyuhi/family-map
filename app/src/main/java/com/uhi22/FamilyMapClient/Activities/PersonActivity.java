/**
 * This is the Person Activity that is opened when the user clicks on the info bar from a map fragment or when
 * selecting a Person result from the search activity
 * This Activity displays information regarding the selected person, including
 * their first name, last name, gender, family relationships, and significant life events
 * This activity can navigate to other PersonActivity instances or to Event Activity instances
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.uhi22.FamilyMapClient.Adapters.MainAdapter;
import com.uhi22.FamilyMapClient.R;
import com.uhi22.FamilyMapClient.NetworkServices.EventService;
import com.uhi22.FamilyMapClient.NetworkServices.PersonService;
import com.uhi22.FamilyMapClient.Services.FilterService;
import com.uhi22.FamilyMapClient.Services.RelativeService;
import com.uhi22.shared.Responses.AllEventsResponse;
import com.uhi22.shared.Responses.AllPersonsResponse;
import com.uhi22.shared.Responses.PersonResponse;
import com.uhi22.shared.model.Event;
import com.uhi22.shared.model.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import Models.ListItem;

public class PersonActivity extends AppCompatActivity {

//    Declare the variables that are used in the UI
    private TextView firstNameTextView, lastNameTextView, genderTextView;
    private List<String> listGroup;
    private HashMap<String, List<ListItem>> listItem;
    private MainAdapter adapter;

//    Declare variables that will be used throughout the PersonActivity
    private String authToken, activePersonID, rootId, host, port;
    private ArrayList<Event> allEvents, relatedEvents;
    private ArrayList<Person> allPersons, children;
    private ArrayList<String> allFatherSideIDs, allMotherSideIDs;
    private Person rootPerson, activePerson, father, mother, spouse;
    private boolean fatherEnabled, motherEnabled, maleEnabled, femaleEnabled;

    /**
     * This method runs when the Activity is first opened
     * On the Person Activity, that defines the variables that will be used throughout the activity through
     * pulling information from the passed intent or via network calls.
     * It also calls to build out and populate the views
     *
     * @param savedInstanceState is the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Define the activity layout and instance state
        super.onCreate(savedInstanceState);
//        Populate the UI
        setContentView(R.layout.person_activity);
        firstNameTextView = (TextView) findViewById(R.id.first_name_value);
        lastNameTextView = (TextView) findViewById(R.id.last_name_value);
        genderTextView = (TextView) findViewById(R.id.gender_value);
        ExpandableListView expandableListView = findViewById(R.id.expandable_listview);

//        Get information for the network connections and the active person/context
        Intent intent = getIntent();
        authToken = intent.getStringExtra("authToken");
        activePersonID = intent.getStringExtra("PersonID");
        rootId = intent.getStringExtra("RootID");
        host = intent.getStringExtra("host");
        port = intent.getStringExtra("port");

//        Get information regarding filter settings
        boolean lifeStoryEnabled = Objects.requireNonNull(intent.getExtras()).getBoolean("lifeStoryEnabled");
        boolean familyTreeEnabled = intent.getExtras().getBoolean("familyTreeEnabled");
        boolean spouseLineEnabled = intent.getExtras().getBoolean("spouseLineEnabled");
        fatherEnabled = intent.getExtras().getBoolean("fatherEnabled");
        motherEnabled = intent.getExtras().getBoolean("motherEnabled");
        maleEnabled = intent.getExtras().getBoolean("maleEnabled");
        femaleEnabled = intent.getExtras().getBoolean("femaleEnabled");

//        Initialize Person and Event objects and ArrayLists
        activePerson = null;
        father = null;
        mother = null;
        spouse = null;
        allPersons = new ArrayList<Person>();
        allEvents = new ArrayList<Event>();
        children = new ArrayList<Person>();
        relatedEvents = new ArrayList<Event>();
        allFatherSideIDs = new ArrayList<String>();
        allMotherSideIDs = new ArrayList<String>();

//        Create the skeleton for the expandable list view
        listGroup = new ArrayList<>();
        listItem = new HashMap<>();
        adapter = new MainAdapter(this, listGroup, listItem,
                lifeStoryEnabled, familyTreeEnabled, spouseLineEnabled,
                fatherEnabled, motherEnabled, maleEnabled, femaleEnabled, rootId);
        expandableListView.setAdapter(adapter);

        try {
//            Perform the network call to get data from the server
            getData();
//            Find the root user to determine paternal/maternal lineage
            rootPerson = findRoot();
//            Fill the father and mother event IDs for filtering purposes
            fillFatherMotherEventIDs();
//            Determine which Persons and Events are related to the viewed Person
            filterRelevantData();
//            Build out the expandable list with the relevant information
            populateExpandableList();
        } catch (Exception e) {
//            Provide user feedback if something goes wrong
            Toast.makeText(this.getApplicationContext(), "Unable to get Person Data for PersonID: " + activePersonID, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * This method populates the father and mother IDs into ArrayLists, which can be used
     * to determine ancestry, gender, and whether items should be added to the list or not based on filters
     */
    private void fillFatherMotherEventIDs() {
        allFatherSideIDs = RelativeService.getFatherIdEvents(rootPerson, allPersons);
        allMotherSideIDs = RelativeService.getMotherEventIDs(rootPerson, allPersons);
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
     * This method populates the ExpandableList with the relevant Person and Event info
     * It sorts the events in chronological order with a comparator,
     */
    private void populateExpandableList() {
//        Create list groups for all Events and Family members
        listGroup.add("Events");
        listGroup.add("Family");

//        Sort the events in chronological order
        Collections.sort(relatedEvents, new Comparator<Event>() {
           @Override public int compare(Event e1, Event e2) {return e1.getYear() - e2.getYear();}
        });

//        Iterate through the related events and skip any events that do not match the filter settings
        List<ListItem> eventItems = new ArrayList<>();
        for(Event event : relatedEvents) {
            if(FilterService.failFilter(event, allPersons, allFatherSideIDs, allMotherSideIDs, fatherEnabled, motherEnabled, maleEnabled, femaleEnabled)) {
                continue;
            }
            ListItem eItem = new ListItem();
            eItem.setItemType("event");
            eItem.setEventId(event.getEventID());
            eItem.setPersonId(event.getPersonID());
            eItem.setAuthToken(authToken);
            eItem.setHost(host);
            eItem.setPort(port);
            eItem.setLocation(event.getCity() + ", " + event.getCountry());
            eItem.setName(activePerson.getFirstName() + " " + activePerson.getLastName());
            eItem.setEventType(event.getEventType());
            eItem.setYear(event.getYear());
            eventItems.add(eItem);
        }

//        Get the father object and populate the data into the list view
        List<ListItem> familyItems = new ArrayList<>();
        if(father != null) {
            ListItem fatherItem = new ListItem();
            fatherItem.setItemType("family");
            fatherItem.setPersonId(father.getPersonID());
            fatherItem.setAuthToken(authToken);
            fatherItem.setHost(host);
            fatherItem.setPort(port);
            fatherItem.setGender("m");
            fatherItem.setName(father.getFirstName() + " " + father.getLastName());
            fatherItem.setRelationship("Father");
            familyItems.add(fatherItem);
        }

//        Get the mother object and populate the data into the list view
        if(mother != null) {
            ListItem motherItem = new ListItem();
            motherItem.setItemType("family");
            motherItem.setPersonId(mother.getPersonID());
            motherItem.setAuthToken(authToken);
            motherItem.setHost(host);
            motherItem.setPort(port);
            motherItem.setGender("f");
            motherItem.setName(mother.getFirstName() + " " + mother.getLastName());
            motherItem.setRelationship("Mother");
            familyItems.add(motherItem);
        }

//        Get the spouse object and populate the data into the list view
        if(spouse != null) {
            ListItem spouseItem = new ListItem();
            spouseItem.setItemType("family");
            spouseItem.setPersonId(spouse.getPersonID());
            spouseItem.setAuthToken(authToken);
            spouseItem.setHost(host);
            spouseItem.setPort(port);
            spouseItem.setGender(spouse.getGender());
            spouseItem.setName(spouse.getFirstName() + " " + spouse.getLastName());
            spouseItem.setRelationship("Spouse");
            familyItems.add(spouseItem);
        }

//        Iterate through all the people in the list of children and  populate the data into the list view
        for(Person child : children) {
            ListItem childItem = new ListItem();
            childItem.setItemType("family");
            childItem.setPersonId(child.getPersonID());
            childItem.setAuthToken(authToken);
            childItem.setHost(host);
            childItem.setPort(port);
            childItem.setGender(child.getGender());
            childItem.setName(child.getFirstName() + " " + child.getLastName());
            childItem.setRelationship("Child");
            familyItems.add(childItem);
        }

//        Place the data into the list group and update the adapter to display the new info
        listItem.put(listGroup.get(0), eventItems);
        listItem.put(listGroup.get(1), familyItems);
        adapter.notifyDataSetChanged();
    }

    /**
     * This method removes any data that is irrelevant to the current person
     * This means that the person activity will only show persons and events that have direct connection to them
     */
    private void filterRelevantData() {
//        call findRelatives to decouple
        findRelatives();
//        iterate through events and add an event if its person ID matches the current person
        for(Event event : allEvents) {
            if(event.getPersonID().equals(activePersonID)) {
                relatedEvents.add(event);
            }
        }
    }

    /**
     * This method adds defines which persons in the allPersons array are related to the current person and should be loaded to the screen
     */
    private void findRelatives() {
        for(Person person : allPersons) {
//            If the current person is the father or mother of the currently evaluated child, add it to the list of children
//            Then get the mother, father, and spouse based on the data that is stored on the person object
            if((person.getFatherID() != null && person.getMotherID() != null) && (person.getFatherID().equals(activePersonID) || person.getMotherID().equals(activePersonID))) {
                children.add(person);
            } else if((activePerson.getFatherID() != null) && (person.getPersonID().equals(activePerson.getFatherID()))) {
                father = person;
            } else if((activePerson.getMotherID() != null) && (person.getPersonID().equals(activePerson.getMotherID()))) {
                mother = person;
            } else if((activePerson.getSpouseID() != null) && (person.getPersonID().equals(activePerson.getSpouseID()))) {
                spouse = person;
            }
        }
    }

    /**
     * This method starts the network call to get information from the server for all persons and events
     * @throws ExecutionException can happen during network call
     * @throws InterruptedException can happen during network call
     * @throws Exception can happen during network call
     */
    private void getData() throws ExecutionException, InterruptedException, Exception {
//        Get the active person
        PersonService personService = new PersonService(this.getApplicationContext(), host, Integer.parseInt(port));
        PersonResponse personResponse = new PersonResponse();
        personResponse = personService.getPerson(activePersonID, authToken);
        if(personResponse.getSuccess()) {
            activePerson = new Person(personResponse.getPersonID(),
                    personResponse.getAssociatedUsername(),
                    personResponse.getFirstName(),
                    personResponse.getLastName(),
                    personResponse.getGender(),
                    personResponse.getFatherID(),
                    personResponse.getMotherID(),
                    personResponse.getSpouseID());
            firstNameTextView.setText(activePerson.getFirstName());
            lastNameTextView.setText(activePerson.getLastName());
            if(activePerson.getGender().equals("m")) {
                genderTextView.setText(R.string.male);
            } else {
                genderTextView.setText(R.string.female);
            }
        } else {
            throw new Exception();
        }

//        Get all the people
        AllPersonsResponse allPersonsResponse = new AllPersonsResponse();
        allPersonsResponse = personService.getAllPersons(authToken);
        if(allPersonsResponse.getSuccess()) {
            allPersons.addAll(allPersonsResponse.getData());
        } else {
            throw new Exception();
        }

//        Get all the events
        EventService eventService = new EventService(this.getApplicationContext(), host, Integer.parseInt(port));
        AllEventsResponse allEventsResponse = new AllEventsResponse();
        allEventsResponse = eventService.getAllEvents(authToken);
        if(allEventsResponse.getSuccess()) {
            allEvents.addAll(allEventsResponse.getData());
        } else {
            throw new Exception();
        }
    }
}
