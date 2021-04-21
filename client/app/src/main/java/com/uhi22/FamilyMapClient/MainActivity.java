/**
 * This is the Default Activity that opens on first load
 * The MainActivity is populated with UI through the use of Fragments
 * The two Fragments, Login and Map, populate the activity with login and map UI, respectively
 * Users will see the Login fragment if they are not currently authenticated
 * Once authentication is complete, the Login Fragment is replaced with the Map Fragment
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.uhi22.FamilyMapClient.Fragments.LoginFragment;
import com.uhi22.FamilyMapClient.Fragments.MapFragment;
import com.uhi22.shared.model.Event;
import com.uhi22.shared.model.Person;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

//    Declare the Fragment Class Objects that will be used to populate the fragments
    LoginFragment loginFragment;
    MapFragment mapFragment;

//    Declare variables that will be populated once authentication is complete
//    These variables will be used throughout the application
    private static String authToken, host, port;
//    rootPerson denotes the Person object directly associated with the authenticated User
//    aPerson denotes the Person who is currently being viewed in the info pane on the MapFragment
    private static Person rootPerson, aPerson;
//    aEvent denotes the Event that is currently selected on the MapFragment
    private static Event aEvent;
    private static ArrayList<Person> allPersons;
    private static ArrayList<Event> allEvents;

    /**
     * This method runs when the Activity is first created
     * On the Main Activity, that defines some of the variables that will be used throughout the application to default values
     * It calls the method to populate the page with the login fragment
     *
     * @param savedInstanceState is the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Define the activity layout and instance state
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Define variables that are used across the application with default values (pending login)
        authToken = "";
        host = "";
        port = "";
        rootPerson = null;
        aPerson = null;
        aEvent = null;
        allPersons = null;
        allEvents = null;

//        Populate the view with the login fragment
        loadLogin();
    }

    /**
     * This method populates the MainActivity with the LoginFragment
     * See the LoginFragment Class for more information regarding the LoginFragment
     */
    private void loadLogin() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        Define the LoginFragment class member with method call
        loginFragment = new LoginFragment();
//        Display the login fragment and commit to screen
        fragmentTransaction.replace(R.id.fragment,loginFragment);
        fragmentTransaction.commit();
    }

    /**
     * This method populates the MainActivity with the MapFragment
     * See the MapFragment Class for more information regarding the MapFragment
     */
    private void loadMap() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        Define the MapFragment class member with method call
        mapFragment = new MapFragment();
//        Display the map fragment and commit to screen
        fragmentTransaction.replace(R.id.fragment, mapFragment);
        fragmentTransaction.commit();
    }

    /**
     * This method defines the variables that are used across the application
     * with significant data which was obtained in the LoginFragment after successful authentication
     *
     * After the data is successfully set, the MapFragment is brought to the screen
     *
     * @param authToken This is the authToken used for interactions with the server
     * @param rootPerson This is the rootPerson that is associated directly with the authenticated User
     * @param allPersons This is the ArrayList of all Person class members that are associated with the authenticated User
     * @param allEvents This is the ArrayLIst of all Events that are associated with the authenticated User
     * @param host This is the designated host for the server
     * @param port This is the designated port for the server
     */
    public void setData(String authToken, Person rootPerson, ArrayList<Person> allPersons, ArrayList<Event> allEvents, String host, String port) {
//        All that is done here
        MainActivity.authToken = authToken;
        MainActivity.rootPerson = rootPerson;
        MainActivity.allPersons = allPersons;
        MainActivity.allEvents = allEvents;
        MainActivity.host = host;
        MainActivity.port = port;
//        Populate the Activity with the MapFragment view
        loadMap();
    }

    /**
     * This method clears all data that was obtained through successful authentication and returns the MainActivity to the LoginView
     */
    public void logout() {
//        Clear out the data from all the widely-used variables
        authToken = "";
        rootPerson = null;
        allPersons = null;
        allEvents = null;
        aEvent = null;
        aPerson = null;
        port = "";
        host = "";
//        Force the User back onto the LoginFragment
        loadLogin();
    }

    /**
     * Getter for the rootPerson variable
     *
     * @return the rootPerson variable
     */
    public Person getRootPerson() {
        return rootPerson;
    }

    /**
     * Getter for the allPersons variable
     *
     * @return the allPersons variable
     */
    public ArrayList<Person> getAllPersons() {
        return allPersons;
    }

    /**
     * Getter for the allEvents variable
     *
     * @return the allEvents variable
     */
    public ArrayList<Event> getAllEvents() {
        return allEvents;
    }


    /**
     * Getter for the authToken variable
     *
     * @return the authToken variable
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Getter for the aEvent variable
     *
     * @return the aEvent variable
     */
    public Event getaEvent() {
        return aEvent;
    }

    /**
     * Getter for the aPerson variable
     *
     * @return the aPerson variable
     */
    public Person getaPerson() {
        return aPerson;
    }

    /**
     * Getter for the host variable
     *
     * @return the host variable
     */
    public String getHost() {
        return host;
    }

    /**
     * Getter for the port variable
     *
     * @return the port variable
     */
    public String getPort() {
        return port;
    }

    /**
     * Setter for the aEvent variable
     *
     * @param aEvent is the desired aEvent value
     */
    public void setaEvent(Event aEvent) {
        MainActivity.aEvent = aEvent;
    }

    /**
     * Setter for the aPerson variable
     *
     * @param aPerson is the desired aPerson value
     */
    public void setaPerson(Person aPerson) {
        MainActivity.aPerson = aPerson;
    }
}
