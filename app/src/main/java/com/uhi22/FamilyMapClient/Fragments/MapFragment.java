/**
 * This fragment is imposed on the main activity and the event activity screens
 * It shows a view of the world map with markers placed to denote significant events which are filtered based on user preference in settings
 * The fragment also allows for lines to be drawn between different map markers when pressed
 * When the user presses a marker, the person who owns that event has his/her information loaded to an information bar
 * at the bottom of the display
 * Selecting the info bar begins a person activity
 * The fragment has a search option and a settings option, which are both custom actionmenu items
 * When clicking either of these menu items, a new activity is opened for search or settings
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uhi22.FamilyMapClient.Activities.EventActivity;
import com.uhi22.FamilyMapClient.Activities.PersonActivity;
import com.uhi22.FamilyMapClient.Activities.SearchActivity;
import com.uhi22.FamilyMapClient.Activities.SettingsActivity;
import com.uhi22.FamilyMapClient.MainActivity;
import com.uhi22.FamilyMapClient.R;
import com.uhi22.FamilyMapClient.Services.ChronologicalService;
import com.uhi22.FamilyMapClient.Services.FilterService;
import com.uhi22.FamilyMapClient.Services.RelativeService;
import com.uhi22.shared.model.Event;
import com.uhi22.shared.model.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class MapFragment extends Fragment implements OnMapReadyCallback {

//    Declare the variables that are used in the UI
    private TextView title, description;
    private ImageView imageView;
    private ConstraintLayout infoLayout;
    private GoogleMap mMap;

//    Declare variables that will be used throughout the fragment
    private ArrayList<Person> allPersons;
    private ArrayList<Event> allEvents;
    private ArrayList<String> recognizedEventTypes;
    private ArrayList<Polyline> allPolylines;
    private ArrayList<String> allFatherSideIDs, allMotherSideIDs;
    private Person rootPerson, activePerson;
    private Event activeEvent;
    private String authToken, host, port;
    private int colorIterator;
    private boolean lifeStoryEnabled, familyTreeEnabled, spouseLineEnabled, fatherEnabled, motherEnabled, maleEnabled, femaleEnabled;

//    This array of float items allows for colors to be iterated through and new colors assigned to new events
    private Float[] colors = new Float[] {
            BitmapDescriptorFactory.HUE_AZURE,
            BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_RED,
            BitmapDescriptorFactory.HUE_ROSE,
            BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_YELLOW
    };

    /**
     * Define the variables for the UI and set things up for functionality
     *
     * @param savedInstanceState the instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
//        Define the activity layout and instance state
        super.onCreate(savedInstanceState);

//        Restrict all filters by default
        lifeStoryEnabled = true;
        familyTreeEnabled = true;
        spouseLineEnabled = true;
        fatherEnabled = true;
        motherEnabled = true;
        maleEnabled = true;
        femaleEnabled = true;

//        Start the color iterator at 0 and initialize ArrayLists that will be used throughout the fragment
        colorIterator = 0;
        allPolylines = new ArrayList<Polyline>();
        allFatherSideIDs = new ArrayList<String>();
        allMotherSideIDs = new ArrayList<String>();
        recognizedEventTypes = new ArrayList<>();

//        If the parent activity is a MainActivity, get the data that was passed as a result of the user login
//        If the parent activity is an EventActivity, get the data that was passed as a result of a new EventActivity being started
        if(getActivity() instanceof MainActivity) {
            setHasOptionsMenu(true);
            activePerson = ((MainActivity) getActivity()).getaPerson();
            activeEvent = ((MainActivity) getActivity()).getaEvent();
            host = ((MainActivity) getActivity()).getHost();
            port = ((MainActivity) getActivity()).getPort();
            allPersons = ((MainActivity) getActivity()).getAllPersons();
            allEvents = ((MainActivity) getActivity()).getAllEvents();
            rootPerson = ((MainActivity) getActivity()).getRootPerson();
            authToken = ((MainActivity) getActivity()).getAuthToken();
        } else {
            activePerson = ((EventActivity) getActivity()).getaPerson();
            activeEvent = ((EventActivity) getActivity()).getaEvent();
            host = ((EventActivity) getActivity()).getHost();
            port = ((EventActivity) getActivity()).getPort();
            allPersons = ((EventActivity) getActivity()).getAllPersons();
            allEvents = ((EventActivity) getActivity()).getAllEvents();
            rootPerson = ((EventActivity) getActivity()).getRootPerson();
            authToken = ((EventActivity) getActivity()).getAuthToken();
            lifeStoryEnabled = ((EventActivity) getActivity()).getLifeStoryEnabled();
            familyTreeEnabled = ((EventActivity) getActivity()).getFamilyTreeEnabled();
            spouseLineEnabled = ((EventActivity) getActivity()).getSpouseLineEnabled();
            fatherEnabled = ((EventActivity) getActivity()).getFatherEnabled();
            motherEnabled = ((EventActivity) getActivity()).getMotherEnabled();
            maleEnabled = ((EventActivity) getActivity()).getMaleEnabled();
            femaleEnabled = ((EventActivity) getActivity()).getFemaleEnabled();
        }
        try {
//            Fill the father and mother event IDs for filtering purposes
            fillFatherMotherEventIDs();
        } catch (Exception e) {
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
     * This method is used to ensure that the custom-made search and settings icons are included in the action bar
     * @param menu the action bar
     * @param inflater the inflater which will inflate the layout
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        Define the layout and inflate
        inflater.inflate(R.menu.map_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * This method executes when an action bar item is selected
     * It opens a search activity when the search option is selected and a settings activity when the
     * settings option is selected
     * @param item This denotes which option was clicked
     * @return true/false
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        If this is a mainactivity, switch statement will take the user to the settings activity
//        if the settings option was clicked and will take to the search activity
//        if the search option was clicked
        if(getActivity() instanceof MainActivity) {
            switch(item.getItemId()) {
                case R.id.settings_icon:
                    ((MainActivity) getActivity()).setaEvent(activeEvent);
                    ((MainActivity) getActivity()).setaPerson(activePerson);
                    Intent settingsIntent = new Intent(((MainActivity) getActivity()), SettingsActivity.class);
                    settingsIntent.putExtra("lifeStoryEnabled", lifeStoryEnabled);
                    settingsIntent.putExtra("familyTreeEnabled", familyTreeEnabled);
                    settingsIntent.putExtra("spouseLineEnabled", spouseLineEnabled);
                    settingsIntent.putExtra("fatherEnabled", fatherEnabled);
                    settingsIntent.putExtra("motherEnabled", motherEnabled);
                    settingsIntent.putExtra("maleEnabled", maleEnabled);
                    settingsIntent.putExtra("femaleEnabled", femaleEnabled);
                    startActivityForResult(settingsIntent, 1);
                    return true;
                case R.id.search_icon:
                    ((MainActivity) getActivity()).setaEvent(activeEvent);
                    ((MainActivity) getActivity()).setaPerson(activePerson);
                    Intent searchIntent = new Intent(((MainActivity) getActivity()), SearchActivity.class);
                    searchIntent.putExtra("authToken", authToken);
                    searchIntent.putExtra("host", host);
                    searchIntent.putExtra("port", port);
                    searchIntent.putExtra("lifeStoryEnabled", lifeStoryEnabled);
                    searchIntent.putExtra("familyTreeEnabled", familyTreeEnabled);
                    searchIntent.putExtra("spouseLineEnabled", spouseLineEnabled);
                    searchIntent.putExtra("fatherEnabled", fatherEnabled);
                    searchIntent.putExtra("motherEnabled", motherEnabled);
                    searchIntent.putExtra("maleEnabled", maleEnabled);
                    searchIntent.putExtra("femaleEnabled", femaleEnabled);
                    searchIntent.putExtra("RootID", rootPerson.getPersonID());
                    startActivity(searchIntent);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        return true;
    }

    /**
     * This method handles responses from the settings activity.
     * When the settings activity returns from its intent, it included data.  This data
     * is used to power the filter functionality.
     * This method takes that data and makes the changes made in the settings activity available in the
     * map fragment
     * @param requestCode n/a
     * @param resultCode the resultCode received from the intent
     * @param data the data that was passed in the intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        If this was a return from the settings activity with changed data, update filters
        if(resultCode == RESULT_OK) {
            if(Objects.equals(data.getStringExtra("settingChange"), "logout")) {
                ((MainActivity) Objects.requireNonNull(getActivity())).logout();
            } else if(data.getStringExtra("settingChange").equals("regular")) {
                lifeStoryEnabled = data.getExtras().getBoolean("lifeStoryEnabled");
                familyTreeEnabled = data.getExtras().getBoolean("familyTreeEnabled");
                spouseLineEnabled = data.getExtras().getBoolean("spouseLineEnabled");
                fatherEnabled = data.getExtras().getBoolean("fatherEnabled");
                motherEnabled = data.getExtras().getBoolean("motherEnabled");
                maleEnabled = data.getExtras().getBoolean("maleEnabled");
                femaleEnabled = data.getExtras().getBoolean("femaleEnabled");
                mMap.clear();
                setPins();
//                These if statements remove lines and markers if the previously selected event
//                has now been filtered out since the settings intent
                if(allPolylines != null) {
                    for(Polyline p : allPolylines) {
                        p.remove();
                    }
                }
                try {
                    if(activeEvent != null && FilterService.failFilter(activeEvent, allPersons, allFatherSideIDs, allMotherSideIDs, fatherEnabled, motherEnabled, maleEnabled, femaleEnabled)) {
                        activePerson = null;
                        activeEvent = null;
                    }
//                    Reinitialze the map fragment's info and lines
                    populateInfo();
                    drawLines();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This method runs when the fragment's view is being created
     * It populates the UI and inflates teh layout
     * @param layoutInflater the layoutInflater ot inflate the fragment
     * @param viewGroup viewgroup
     * @param bundle n/a
     * @return the inflated/defined display layout
     */
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
//        Populate the UI
        View view = layoutInflater.inflate(R.layout.fragment_map, viewGroup, false);
        title = (TextView) view.findViewById(R.id.title);
        description = (TextView) view.findViewById(R.id.description);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        infoLayout = (ConstraintLayout) view.findViewById(R.id.info_bar);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        return view;
    }

    /**
     * This method begins functionality to populate the map once the Google Map API object is ready
     * @param googleMap the GoogleMap object
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
//        Define the googleMap object
        mMap = googleMap;
//        Set listeners for markers
        mMap.setOnMarkerClickListener(this::markerClicked);
//        If this is the main activity, default to the center of the map
        if(getActivity() instanceof MainActivity) {
            if(activeEvent == null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(0,0)));
            }
        } else {
//            if this is the event activity, move the camera to the selected event
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(activeEvent.getLatitude(), activeEvent.getLongitude())));
//            Draw lines and populate info for the selected event
            drawLines();
            populateInfo();
        }
//        place the pins for non-filtered events
        setPins();
    }

    /**
     * This method is called when a marker is clicked
     * It puts lines on the page for the selected event,
     * moves the camera to the clicked marker location
     * and populates the info bar with the event owner's information
     * @param marker the clicked marker
     * @return whether or not there is an issue with the marker
     */
    private boolean markerClicked(Marker marker) {
//        Don't do anything if the marker is completely null
        if(marker == null) {
            return false;
        }
//        If there are polylines, remove them all
        if(allPolylines != null) {
            for(Polyline p : allPolylines) {
                p.remove();
            }
        }
//        Find the person who owns the clicked event
        for(Person person : allPersons) {
            if(person.getPersonID().equals(marker.getSnippet().split(" ")[1])) {
                activePerson = person;
                break;
            }
        }
//        Find the event that corresponds with the clicked marker
        for(Event event : allEvents) {
            if(event.getEventID().equals(marker.getSnippet().split(" ")[0])) {
                activeEvent = event;
                break;
            }
        }
//        move the camera to the clicked event
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude)));
        if(activePerson == null || activeEvent == null) {
            return false;
        }
//        update the info bar based on the event owner's information
        populateInfo();
        return true;
    }

    /**
     * This method populates the info bar with information concerning the active person
     */
    @SuppressLint("ClickableViewAccessibility")
    private void populateInfo() {

//        If there is no event owner (or selected event), revert the info bar to the default view
        if(activePerson == null) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.android));
            title.setText(R.string.default_info_title);
            description.setText("");
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(0,0)));
            mMap.clear();
            setPins();
            return;
        }

//        update the gender image based on the person's gender
        if("m".equals(activePerson.getGender())) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.man));
        } else {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.woman));
        }
//        Update the title and description for the active person
        title.setText((activePerson.getFirstName() + " " + activePerson.getLastName()));
        description.setText((activeEvent.getEventType() + ": " + activeEvent.getCity() + ", " + activeEvent.getCountry() + " (" + activeEvent.getYear() + ")"));
        drawLines();
//        Create a listener to start a person activity if the infobar is clicked
        infoLayout.setOnTouchListener((v, event) -> {
            try {
                Intent personIntent;
                if(getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).setaEvent(activeEvent);
                    ((MainActivity) getActivity()).setaPerson(activePerson);
                    personIntent = new Intent(((MainActivity) getActivity()), PersonActivity.class);
                } else {
                    ((EventActivity) getActivity()).setaEvent(activeEvent);
                    ((EventActivity) getActivity()).setaPerson(activePerson);
                    personIntent = new Intent(((EventActivity) getActivity()), PersonActivity.class);
                }
                personIntent.putExtra("authToken", authToken);
                personIntent.putExtra("PersonID", activePerson.getPersonID());
                personIntent.putExtra("host", host);
                personIntent.putExtra("port", port);
                personIntent.putExtra("lifeStoryEnabled", lifeStoryEnabled);
                personIntent.putExtra("familyTreeEnabled", familyTreeEnabled);
                personIntent.putExtra("spouseLineEnabled", spouseLineEnabled);
                personIntent.putExtra("fatherEnabled", fatherEnabled);
                personIntent.putExtra("motherEnabled", motherEnabled);
                personIntent.putExtra("maleEnabled", maleEnabled);
                personIntent.putExtra("femaleEnabled", femaleEnabled);
                personIntent.putExtra("RootID", rootPerson.getPersonID());
                startActivity(personIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    /**
     * This method places the pins for non-filtered events on the map view
     */
    private void setPins() {
//        only place the pin if the event passes the filtering check
        for(Event event: allEvents) {
            if(FilterService.failFilter(event, allPersons, allFatherSideIDs, allMotherSideIDs, fatherEnabled, motherEnabled, maleEnabled, femaleEnabled)) {
                continue;
            }
//            Get the event's latlong and place the marker at that location
            LatLng latLng = new LatLng(event.getLatitude(), event.getLongitude());
            MarkerOptions m = new MarkerOptions();
            m
                    .position(latLng)
                    .title(event.getEventType())
                    .snippet(event.getEventID() + " " + event.getPersonID())
                    .icon(BitmapDescriptorFactory.defaultMarker(determineColor(event.getEventType())));
            mMap.addMarker(m);
        }
    }

    /**
     * This method supports the functionality to change the event pin color,
     * yet allow for consistency between event types
     * @param eventType this is the event type to be evaluated
     * @return the color that was selected based on how many other events/colors are in use
     */
    private float determineColor(String eventType) {
//        If the event type has already been given a color, use that color
        if(recognizedEventTypes.contains(eventType)) {
            return colors[recognizedEventTypes.indexOf(eventType) % colors.length];
        }
//        If not already recognized, add this event type and give it a new color
        recognizedEventTypes.add(eventType);
        return colors[colorIterator++ % colors.length];
    }

    /**
     * This method allows for line types to only be drawn if the filter is disabled for them
     */
    private void drawLines() {
//        if there are no active people, then nothing has been clicked.  Don't draw lines
        if(activePerson == null) {
            return;
        }
//        only draw lines for events that meet filter requirements
        if(lifeStoryEnabled) {
            drawLifeStory();
        }
        if(spouseLineEnabled) {
            drawSpouse();
        }
        if(familyTreeEnabled) {
            drawFamilyTree(activePerson, activeEvent, 25);
        }
    }

    /**
     * This method draws a line between the two events
     * color and thickness matter per requirements
     *
     * @param start the starting event
     * @param end the ending event
     * @param color the line color
     * @param thickness the thickness of the line
     */
    private void drawLine(Event start, Event end, int color, float thickness) {
//        Build line and add it
        Polyline p = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(start.getLatitude(), start.getLongitude()),
                        new LatLng(end.getLatitude(),end.getLongitude()))
                .color(color)
                .width(thickness));
        allPolylines.add(p);
    }

    /**
     * This method draws the life story lines by sorting them and then jumping between adjacently-timed events
     */
    private void drawLifeStory() {
        ArrayList<Event> lifeStory = new ArrayList<Event>();
//        Get the events that relate to the selected event's owner
        for(Event event : allEvents) {
            if(event.getPersonID().equals(activePerson.getPersonID())) {
                lifeStory.add(event);
            }
        }
//        Sort by year
        Collections.sort(lifeStory, new Comparator<Event>() {
           @Override public int compare(Event e1, Event e2) {
               return e1.getYear() - e2.getYear();
           }
        });
//        Draw the line
        for(int i = 0; i < lifeStory.size() - 1; i++) {
            drawLine(lifeStory.get(i), lifeStory.get(i + 1), Color.BLUE, 15);
        }
    }

    /**
     * This method draws the line for the selected event's owner's spouse's birth
     */
    private void drawSpouse() {
        Event spouseBirth = null;
//        Find the spouse's birth
        for(Event event : allEvents) {
            if(FilterService.failFilter(event, allPersons, allFatherSideIDs, allMotherSideIDs, fatherEnabled, motherEnabled, maleEnabled, femaleEnabled)) {
                continue;
            }
            if(event.getPersonID().equals(activePerson.getSpouseID()) && event.getEventType().equals("birth")) {
                spouseBirth = event;
                break;
            }
        }
//        if the spouse's birth doesn't exist, don't draw a line
        if(spouseBirth == null) {
            return;
        }
//        else draw a line
        drawLine(activeEvent, spouseBirth, Color.RED, 15);
    }

    /**
     * This is a method that recursively goes through every findable generation for a given person and draws a line
     * @param person the person to be evaluated
     * @param event the event that needs to have a line drawn for
     * @param depth symbol for how many layers of recursion have been traversed
     */
    private void drawFamilyTree(Person person, Event event, int depth) {
//        Lessen the width based on the depth
        depth -= 5;
        int width = depth;
        if(width < 1) {
            width = 1;
        }
//        get the father's events for the evaluated person
        ArrayList<Event> fatherEvents = new ArrayList<Event>();
        for(Event e : allEvents) {
            if(FilterService.failFilter(e, allPersons, allFatherSideIDs, allMotherSideIDs, fatherEnabled, motherEnabled, maleEnabled, femaleEnabled)) {
                continue;
            }
            if(e.getPersonID().equals(person.getFatherID())) {
                fatherEvents.add(e);
            }
        }
        if(fatherEvents.size() != 0) {
            drawLine(event, ChronologicalService.getEarliestEvent(fatherEvents), Color.BLACK, width);
        }
//        Get the mother's events for the evaluated person
        ArrayList<Event> motherEvents = new ArrayList<Event>();
        for(Event e : allEvents) {
            if(FilterService.failFilter(e, allPersons, allFatherSideIDs, allMotherSideIDs, fatherEnabled, motherEnabled, maleEnabled, femaleEnabled)) {
                continue;
            }
            if(e.getPersonID().equals(person.getMotherID())) {
                motherEvents.add(e);
            }
        }
        if(motherEvents.size() != 0) {
            drawLine(event, ChronologicalService.getEarliestEvent(motherEvents), Color.BLACK, width);
        }

//        Get the POJO for the father and mother
        Person father = null;
        Person mother = null;
        for(Person p : allPersons) {
            if(p.getPersonID().equals(person.getFatherID())) {
                father = p;
            } else if(p.getPersonID().equals(person.getMotherID())) {
                mother = p;
            }
            if(father != null && mother != null) {
                break;
            }
        }
//        implement recursion
//        Base case is found when the evaluated person does not have a father or mother to descend to
        if(father != null && maleEnabled) {
            drawFamilyTree(father, ChronologicalService.getEarliestEvent(fatherEvents), width);
        }
        if(mother != null && femaleEnabled) {
            drawFamilyTree(mother, ChronologicalService.getEarliestEvent(motherEvents), width);
        }
    }
}
