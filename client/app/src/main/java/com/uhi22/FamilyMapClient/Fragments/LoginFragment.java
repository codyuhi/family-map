/**
 * This fragment is imposed on the Main Activity screen when the app is first loaded
 * This is also loaded again when the user logs out of the app via the settings activity
 * Users can enter their login information to start a session on an existing user on this fragment
 * They can also create a new user and populate it with randomly generated data (performed on the server's side)
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.uhi22.FamilyMapClient.MainActivity;
import com.uhi22.FamilyMapClient.R;
import com.uhi22.FamilyMapClient.NetworkServices.EventService;
import com.uhi22.FamilyMapClient.NetworkServices.LoginService;
import com.uhi22.FamilyMapClient.NetworkServices.PersonService;
import com.uhi22.FamilyMapClient.NetworkServices.RegisterService;
import com.uhi22.shared.Requests.LoginRequest;
import com.uhi22.shared.Requests.RegisterRequest;
import com.uhi22.shared.Responses.AllEventsResponse;
import com.uhi22.shared.Responses.AllPersonsResponse;
import com.uhi22.shared.Responses.LoginResponse;
import com.uhi22.shared.Responses.PersonResponse;
import com.uhi22.shared.Responses.RegisterResponse;
import com.uhi22.shared.model.Event;
import com.uhi22.shared.model.Person;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Objects;

public class LoginFragment extends Fragment {

//    Declare the variables that are used in the UI
    private Button login, register;
    private EditText hostInput, portInput, usernameInput, firstNameInput, lastNameInput, emailInput, passwordInput;
    private RadioGroup gender;

//    Declare variables that will be used throughout the fragment
    private static String host, port, username, password;

    /**
     * Define the variables for the UI and set things up for functionality
     *
     * @param layoutInflater inflates the layout for the fragment
     * @param viewGroup the group for the view
     * @param bundle the bundle
     * @return the view that was loaded
     */
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
//        Define the activity layout and instance state
        View view = layoutInflater.inflate(R.layout.fragment_login, viewGroup, false);
//        Populate the UI
        hostInput = view.findViewById(R.id.host);
        hostInput.addTextChangedListener(new loginTextListener());
        portInput = view.findViewById(R.id.port);
        portInput.addTextChangedListener(new loginTextListener());
        usernameInput = view.findViewById(R.id.username);
        usernameInput.addTextChangedListener(new loginTextListener());
        passwordInput = view.findViewById(R.id.password);
        passwordInput.addTextChangedListener(new loginTextListener());
        firstNameInput = view.findViewById(R.id.firstName);
        firstNameInput.addTextChangedListener(new loginTextListener());
        lastNameInput = view.findViewById(R.id.lastName);
        lastNameInput.addTextChangedListener(new loginTextListener());
        emailInput = view.findViewById(R.id.email);
        emailInput.addTextChangedListener(new loginTextListener());
        gender = view.findViewById(R.id.genderChoices);
        login = view.findViewById(R.id.login);
        register = view.findViewById(R.id.register);
        login.setOnClickListener(new loginListener());
        register.setOnClickListener(new registerListener());

//        Define the variables that will be used throughout the activity
        host = hostInput.getText().toString();
        port = portInput.getText().toString();
        username = usernameInput.getText().toString();
        password = passwordInput.getText().toString();

//        Enable the register button if all fields are filled out
        register.setEnabled(
                host.length() > 0 &&
                        port.length() > 0 &&
                        username.length() > 0 &&
                        password.length() > 0 &&
                        firstNameInput.getText().length() > 0 &&
                        lastNameInput.getText().length() > 0 &&
                        emailInput.getText().length() > 0
        );
//        Enable the login button if the host, port, username, and password fields are filled out
        login.setEnabled(
                !host.isEmpty() &&
                        !port.isEmpty() &&
                        !username.isEmpty() &&
                        !password.isEmpty()
        );

        return view;
    }

    /**
     * This class is set up to listen for any changes in the login fields
     * If a field is changed, the afterTextChanged method will execute
     */
    private class loginTextListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        /**
         * When the user makes a change to any of the text fields,
         * this method will execute
         * It updates the values for the login variables
         * @param s denotes the field that was changed
         */
        @Override
        public void afterTextChanged(Editable s) {
//            Get the text data
            host = hostInput.getText().toString();
            port = portInput.getText().toString();
            username = usernameInput.getText().toString();
            password = passwordInput.getText().toString();

//            Change the enabled status based on which fields are filled out
            register.setEnabled(
                    host.length() > 0 &&
                    port.length() > 0 &&
                    username.length() > 0 &&
                    password.length() > 0 &&
                    firstNameInput.getText().length() > 0 &&
                    lastNameInput.getText().length() > 0 &&
                    emailInput.getText().length() > 0
            );
            login.setEnabled(
                    !host.isEmpty() &&
                    !port.isEmpty() &&
                    !username.isEmpty() &&
                    !password.isEmpty()
            );
        }
    }

    /**
     * This class is set up to listen for if the login button is pressed
     * Pressing the login button obtains the text information for the user and starts a network call for login
     */
    private class loginListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
//            Get user input data
            host = hostInput.getText().toString();
            port = portInput.getText().toString();
            username = usernameInput.getText().toString();
            password = passwordInput.getText().toString();

//            Create new LoginRequest
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUserName(username);
            loginRequest.setPassword(password);

            try {
//                Build LoginService object with the given information
                LoginService loginService = new LoginService(LoginFragment.this.getContext(),
                        host,
                        Integer.parseInt(port),
                        username,
                        password
                );

//                Perform the Http request
                LoginResponse loginResponse = new LoginResponse();
                loginResponse = loginService.executeRequest(loginRequest);
                Person rootPerson;
//                If the login request was successful, get the data that was returned from the server
                if(loginResponse.getSuccess()) {
                    PersonService personService = new PersonService(LoginFragment.this.getContext(), host, Integer.parseInt(port));
                    PersonResponse personResponse = new PersonResponse();
                    personResponse = personService.getPerson(loginResponse.getPersonID(), loginResponse.getAuthToken());
                    if(personResponse.getSuccess()) {
                        rootPerson = new Person(personResponse.getPersonID(),
                                personResponse.getAssociatedUsername(),
                                personResponse.getFirstName(),
                                personResponse.getLastName(),
                                personResponse.getGender(),
                                personResponse.getFatherID(),
                                personResponse.getMotherID(),
                                personResponse.getSpouseID());
                        Toast.makeText(LoginFragment.this.getContext(), "Welcome, " + personResponse.getFirstName() + " " + personResponse.getLastName() + "!", Toast.LENGTH_LONG).show();
                    } else {
                        throw new Exception();
                    }

//                    Get all the persons that are associated with the newly authenticated user
                    AllPersonsResponse allPersonsResponse = new AllPersonsResponse();
                    allPersonsResponse = personService.getAllPersons(loginResponse.getAuthToken());
                    ArrayList<Person> allPersons;
                    if(allPersonsResponse.getSuccess()) {
                        StringBuilder text = new StringBuilder();
                        allPersons = new ArrayList<Person>();
                        for(Person person : allPersonsResponse.getData()) {
                            text.append(person.getFirstName()).append(", ");
                            allPersons.add(person);
                        }
                    } else {
                        throw new Exception();
                    }

//                    Get all the events that are associated with the newly authenticated user
                    EventService eventService = new EventService(LoginFragment.this.getContext(), host, Integer.parseInt(port));
                    AllEventsResponse allEventsResponse = new AllEventsResponse();
                    allEventsResponse = eventService.getAllEvents(loginResponse.getAuthToken());
                    if(allEventsResponse.getSuccess()) {
                        StringBuilder text = new StringBuilder();
                        ArrayList<Event> allEvents = new ArrayList<Event>();
                        for(Event event : allEventsResponse.getData()) {
                            text.append(event.getCountry()).append(", ");
                            event.setCountry(event.getCountry().replaceAll("[\"]", ""));
                            event.setCity(event.getCity().replaceAll("[\"]", ""));
                            allEvents.add(event);
                        }
//                        Close the login fragment
                        closeFragment(loginResponse.getAuthToken(), rootPerson, allPersons, allEvents);
                    } else {
                        throw new Exception();
                    }
                } else {
//                    If anything went wrong during the login, give a proper error message
                    Toast.makeText(LoginFragment.this.getContext(), "Invalid Username/Password Combination for Username: " + username + "!", Toast.LENGTH_LONG).show();
                }
            } catch (MalformedURLException e) {
                System.out.println("MalformedURLException thrown from the LoginFragment: " + e.getMessage());
            } catch (NumberFormatException e) {
                Toast.makeText(LoginFragment.this.getContext(), "Invalid Port Number Given", Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                Toast.makeText(LoginFragment.this.getContext(), "Interrupted while performing Login operation", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(LoginFragment.this.getContext(), "Something went wrong while Logging in", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This class is set up to listen for if the register button is pressed
     * Pressing the register button obtains the text information for the user and starts a network call for registration
     */
    private class registerListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
//            Get user input data
            host = hostInput.getText().toString();
            port = portInput.getText().toString();
            username = usernameInput.getText().toString();
            password = passwordInput.getText().toString();
            String firstName = firstNameInput.getText().toString();
            String lastName = lastNameInput.getText().toString();
            String email = emailInput.getText().toString();
            String genderString = "";
            int radioButtonId = gender.indexOfChild(Objects.requireNonNull(getActivity()).findViewById(gender.getCheckedRadioButtonId()));
            if(radioButtonId == 0) {
                genderString = "m";
            } else {
                genderString = "f";
            }

//            Create new RegisterRequest
            RegisterRequest registerRequest = new RegisterRequest(username,password,email,firstName,lastName,genderString);

            try {
//                Build RegisterService object with the given information
                RegisterService registerService = new RegisterService(
                        LoginFragment.this.getContext(),
                        host,
                        Integer.parseInt(port)
                );


//                Perform the Http request
                RegisterResponse registerResponse = new RegisterResponse();
                registerResponse = registerService.executeRequest(registerRequest);
//                If the login request was successful, get the data that was returned from the server
                if(registerResponse.getSuccess()) {
                    PersonService personService = new PersonService(LoginFragment.this.getContext(), host, Integer.parseInt(port));
                    PersonResponse personResponse = new PersonResponse();
                    personResponse = personService.getPerson(registerResponse.getPersonID(), registerResponse.getAuthToken());
                    Person rootPerson;
                    if(personResponse.getSuccess()) {
                        Toast.makeText(LoginFragment.this.getContext(), "Welcome, " + personResponse.getFirstName() + " " + personResponse.getLastName() + "!", Toast.LENGTH_LONG).show();
                        rootPerson = new Person(personResponse.getPersonID(),
                                personResponse.getAssociatedUsername(),
                                personResponse.getFirstName(),
                                personResponse.getLastName(),
                                personResponse.getGender(),
                                personResponse.getFatherID(),
                                personResponse.getMotherID(),
                                personResponse.getSpouseID());
                    } else {
                        throw new Exception();
                    }

//                    Get all the persons that are associated with the newly created user
                    AllPersonsResponse allPersonsResponse = new AllPersonsResponse();
                    allPersonsResponse = personService.getAllPersons(registerResponse.getAuthToken());
                    ArrayList<Person> allPersons;
                    if(allPersonsResponse.getSuccess()) {
                        StringBuilder text = new StringBuilder();
                        allPersons = new ArrayList<Person>();
                        for(Person person : allPersonsResponse.getData()) {
                            text.append(person.getFirstName()).append(", ");
                            allPersons.add(person);
                        }
                    } else {
                        throw new Exception();
                    }

//                    Get all the events that are associated with the newly created user
                    EventService eventService = new EventService(LoginFragment.this.getContext(), host, Integer.parseInt(port));
                    AllEventsResponse allEventsResponse = eventService.getAllEvents(registerResponse.getAuthToken());
                    if(allEventsResponse.getSuccess()) {
                        StringBuilder text = new StringBuilder();
                        ArrayList<Event> allEvents = new ArrayList<Event>();
                        for(Event event : allEventsResponse.getData()) {
                            event.setCountry(event.getCountry().replaceAll("[\"]", ""));
                            event.setCity(event.getCity().replaceAll("[\"]", ""));
                            text.append(event.getCountry()).append(", ");
                            allEvents.add(event);
                        }
//                        Close the login fragment
                        closeFragment(registerResponse.getAuthToken(), rootPerson, allPersons, allEvents);
                    } else {
//                    If anything went wrong during the registration, give a proper error message
                        throw new Exception();
                    }
                } else if("Username Already Taken Error".equals(registerResponse.getMessage())) {
                    Toast.makeText(LoginFragment.this.getContext(), "Username, " + username + ", already taken. Please use a different Username", Toast.LENGTH_LONG).show();
                } else if("Internal Server Error".equals(registerResponse.getMessage())) {
                    Toast.makeText(LoginFragment.this.getContext(), "Email, " + email + ", already taken. Please use a different email", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginFragment.this.getContext(), "Something went wrong while registering " + registerResponse.getUserName() + "!", Toast.LENGTH_LONG).show();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid Port Number Given: " + e.getMessage());
            } catch (InterruptedException e) {
                Toast.makeText(LoginFragment.this.getContext(), "Interrupted while performing Register operation", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(LoginFragment.this.getContext(), "Something went wrong while registering", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This method closes the login fragment, passes data to the mainactivity and allows for the map fragment to load
     * @param authToken the user-given authToken
     * @param rootPerson the person associated with the user that just logged in/registered
     * @param allPersons all persons associated with the authenticated user
     * @param allEvents all events associated with the authenticated user
     */
    private void closeFragment(String authToken, Person rootPerson, ArrayList<Person> allPersons, ArrayList<Event> allEvents) {
//        Return the data to the main activity
        ((MainActivity) getActivity()).setData(authToken, rootPerson, allPersons, allEvents, host, port);
//        Close the login fragment
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
