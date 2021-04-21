/**
 * This is the Search Activity that is opened when the user selects the search icon from the main activity's map fragment
 * Search functionality allows users to search through filtered Person and Event information and navigate
 * to a chosen Person or Event activity
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uhi22.FamilyMapClient.Adapters.SearchAdapter;
import com.uhi22.FamilyMapClient.R;
import com.uhi22.FamilyMapClient.NetworkServices.EventService;
import com.uhi22.FamilyMapClient.NetworkServices.PersonService;
import com.uhi22.FamilyMapClient.Services.RelativeService;
import com.uhi22.FamilyMapClient.Services.SearchService;
import com.uhi22.shared.Responses.AllEventsResponse;
import com.uhi22.shared.Responses.AllPersonsResponse;
import com.uhi22.shared.model.Event;
import com.uhi22.shared.model.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class SearchActivity extends AppCompatActivity {

//    Declare the variables that are used in the UI
    private EditText searchInput;
    private TextView basicText;
    private SearchAdapter searchAdapter;

//    Declare variables that will be used throughout the Search Activity
    private String authToken, host, port, rootId;
    private Person rootPerson;
    private boolean fatherEnabled, motherEnabled, maleEnabled, femaleEnabled;
    private ArrayList<Person> allPersons, suggestedPersons;
    private ArrayList<Event> allEvents, suggestedEvents;
    private List<String> suggestionEventIDs, suggestionPersonIDs, suggestionTitles, suggestionDescriptions, suggestionGenders;
    private ArrayList<String> allFatherSideIDs, allMotherSideIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Define the activity layout and instance state
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
//        Populate the UI
        searchInput = (EditText) findViewById(R.id.search_input);
        basicText = (TextView) findViewById(R.id.basic_text);
        RecyclerView searchSuggestions = (RecyclerView) findViewById(R.id.search_suggestions);
        searchSuggestions.addItemDecoration(new DividerItemDecoration(searchSuggestions.getContext(), DividerItemDecoration.VERTICAL));
        searchSuggestions.setItemAnimator(new DefaultItemAnimator());
        searchSuggestions.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

//        Get information for the network connection and the root User
        Intent intent = getIntent();
        authToken = intent.getStringExtra("authToken");
        host = intent.getStringExtra("host");
        port = intent.getStringExtra("port");
        rootId = intent.getStringExtra("RootID");

//        Get information regarding filter settings
        boolean lifeStoryEnabled = Objects.requireNonNull(intent.getExtras()).getBoolean("lifeStoryEnabled");
        boolean familyTreeEnabled = intent.getExtras().getBoolean("familyTreeEnabled");
        boolean spouseLineEnabled = intent.getExtras().getBoolean("spouseLineEnabled");
        fatherEnabled = intent.getExtras().getBoolean("fatherEnabled");
        motherEnabled = intent.getExtras().getBoolean("motherEnabled");
        maleEnabled = intent.getExtras().getBoolean("maleEnabled");
        femaleEnabled = intent.getExtras().getBoolean("femaleEnabled");

//        Initialize Person and Event objects and ArrayLists
        suggestionEventIDs = new ArrayList<>();
        suggestionPersonIDs = new ArrayList<>();
        suggestionTitles = new ArrayList<>();
        suggestionDescriptions = new ArrayList<>();
        suggestionGenders = new ArrayList<>();
        allPersons = new ArrayList<Person>();
        allEvents = new ArrayList<Event>();
        suggestedPersons = new ArrayList<Person>();
        suggestedEvents = new ArrayList<Event>();
        allFatherSideIDs = new ArrayList<String>();
        allMotherSideIDs = new ArrayList<String>();

//        Define the search adapter, set a listener for when the user types, and set the adapter for the list items
        searchAdapter = new SearchAdapter(this, authToken, host, port, suggestionEventIDs, suggestionPersonIDs, suggestionTitles, suggestionDescriptions, suggestionGenders,
                lifeStoryEnabled, familyTreeEnabled, spouseLineEnabled, fatherEnabled, motherEnabled, maleEnabled, femaleEnabled, rootId);
        searchInput.addTextChangedListener(new searchTextListener());
        searchSuggestions.setAdapter(searchAdapter);
        try {
//            Perform the network transactions to get data from the server
            getData();
//            Find the root user to determine paternal/maternal lineage
            rootPerson = findRoot();
//            Fill the father and mother event IDs for filtering purposes
            fillFatherMotherEventIDs();
//            After this, everything is done and the activity waits for user input
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This class is used to listen for user input
     * User input will trigger this functionality even if a single character is pressed
     */
    private class searchTextListener implements TextWatcher {

        /**
         * Antyhing in here would have executed before the text changed
         * @param s n/a
         * @param start n/a
         * @param count n/a
         * @param after n/a
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        /**
         * Anything in here would have executed during the text change
         * @param s n/a
         * @param start n/a
         * @param before n/a
         * @param count n/a
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        /**
         * This method runs after the text is changed
         * The method clears the current suggestions and reevaluates the suggestions based on the current
         * user input
         * After determining which items should be suggested, the adapter is notified to show the suggestions to the screen
         * @param s
         */
        @Override
        public void afterTextChanged(Editable s) {

//            Clear the current suggestions
            suggestionPersonIDs.clear();
            suggestionEventIDs.clear();
            suggestionTitles.clear();
            suggestionDescriptions.clear();
            suggestionGenders.clear();

//            Get the user's input
            String searchString = searchInput.getText().toString();
//            If there is nothing in the text box, clear everything out and prompt for user input
            if(searchString.equals("")) {
                basicText.setVisibility(View.VISIBLE);
                searchAdapter.notifyDataSetChanged();
            } else {
//                else if there is something in the text input, hide the prompt and find suggestions
                basicText.setVisibility(View.INVISIBLE);
                try {
//                    Find the suggestions
                    suggestedPersons = SearchService.getSuggestedPersons(searchString, allPersons);
                    suggestedEvents = SearchService.getSuggestedEvents(searchString, allEvents, allPersons, allFatherSideIDs, allMotherSideIDs, fatherEnabled, motherEnabled, maleEnabled, femaleEnabled);
//                    Add suggestion data to the adapter
                    for(Person p : suggestedPersons) {
                        addPersonToSuggestionList(p);
                    }
                    for(Event e :suggestedEvents) {
                        addEventToSuggestionList(e);
                    }
//                    update the adapter
                    searchAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
            if(person.getPersonID().equals(rootId)) {
                return person;
            }
        }
        return null;
    }

    /**
     * This method starts the network call to get information from the server for all persons and events
     * @throws ExecutionException can happen during network call
     * @throws InterruptedException can happen during network call
     * @throws Exception can happen during network call
     */
    private void getData() throws ExecutionException, InterruptedException, Exception {
//        Get all the people
        PersonService personService = new PersonService(this.getApplicationContext(), host, Integer.parseInt(port));
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

    /**
     * This method adds a given person to the suggestion list which will display the person's menu item on the screen
     * @param person this is the person who was found to match the user's input and should be added
     */
    private void addPersonToSuggestionList(Person person) {
        suggestionPersonIDs.add(person.getPersonID());
        suggestionEventIDs.add(null);
        suggestionTitles.add(person.getFirstName() + " " + person.getLastName());
        suggestionDescriptions.add("");
        suggestionGenders.add(person.getGender());
    }

    /**
     * This method adds a given event to the suggestion list which will display the event's menu item on the screen
     * @param event this is the event who was found to match the user's input and should be added
     */
    private void addEventToSuggestionList(Event event) {
        Person activePerson = getEventOwner(event.getPersonID());
        suggestionPersonIDs.add(event.getPersonID());
        suggestionEventIDs.add(event.getEventID());
        suggestionTitles.add(event.getEventType() + ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")");
        suggestionDescriptions.add(activePerson.getFirstName() + " " + activePerson.getLastName());
        suggestionGenders.add(null);
    }

    /**
     * This method is used to find the EventOwner to populate the Event card with the owner's data
     * @param eventsPersonID
     * @return
     */
    private Person getEventOwner(String eventsPersonID) {
        Person person = null;
//        Search for the event's personID and find the person based off that
        for(Person p : allPersons) {
            if(p.getPersonID().equals(eventsPersonID)) {
                person = p;
                break;
            }
        }
        return person;
    }
}
