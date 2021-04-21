/**
 * This class allows for network functionality supporting PERSON operations
 * This class can get a single person or all persons, given a proper auth token
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient.NetworkServices;

import android.content.Context;
import android.os.AsyncTask;

import com.uhi22.shared.Responses.AllPersonsResponse;
import com.uhi22.shared.Responses.PersonResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class PersonService {

//    Declare network variables used throughout the class
    private static String host;
    private static int port;

    /**
     * Constructor to define network variables based on given data
     * @param context This is an artifact of a previous iteration I did, which is no longer relevant. I just didn't want to go in and delete it everywhere
     * @param host the given host
     * @param port the given port
     */
    public PersonService(Context context, String host, int port) {
//        Define the network variables
        PersonService.host = host;
        PersonService.port = port;
    }

    /**
     * This method creates a getPersonTask, which performs the network functionality for getting a person
     * @param personID denotes the personID which is to be retrieved from the server
     * @param authToken the authtoken for auth
     * @return Returns a PersonResponse
     * @throws ExecutionException happens when problem happens in execution
     * @throws InterruptedException happens when interrupted during network interaction
     */
    public PersonResponse getPerson(String personID, String authToken) throws ExecutionException, InterruptedException {
        GetPersonTask getPersonTask = new GetPersonTask();
        return getPersonTask.execute(personID, authToken).get();
    }

    /**
     * This method creates a getAllPersonsTask, which performs the network functionality for getting a bunch of persons
     * @param authToken the authtoken for auth
     * @return Returns an allPersonsResponse
     * @throws ExecutionException happens when problem happens in execution
     * @throws InterruptedException happens when interrupted during network interaction
     */
    public AllPersonsResponse getAllPersons(String authToken) throws ExecutionException, InterruptedException {
        GetAllPersonTask getAllPersonTask = new GetAllPersonTask();
        return getAllPersonTask.execute(authToken).get();
    }

    /**
     * Child class because I used something like this in a previous android app in the IT program
     * The child class extends an AsyncTask, allowing for background thread for network and still supports UI
     */
    private static class GetPersonTask extends AsyncTask<String, Void, PersonResponse> {

        /**
         * This method is done in a background thread, still allowing UI without a freeze-up
         * @param strings the strings passed into the background thread for network action
         * @return the PersonResponse
         */
        @Override
        protected PersonResponse doInBackground(String... strings) {
            String personID = strings[0];
            String authToken = strings[1];
            PersonResponse response = new PersonResponse();
            try {
//                Perform network operation
                ServerProxy serverProxy = new ServerProxy();
                response = serverProxy.getPerson(personID, authToken, host, port);
            } catch (IOException e) {
                System.out.println("IOException occurred while getting person data for the User: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Generic Exception occurred while getting person data for the user: " + e.getMessage());
            }
            return response;
        }
    }

    /**
     * Child class because I used something like this in a previous android app in the IT program
     * The child class extends an AsyncTask, allowing for background thread for network and still supports UI
     */
    private static class GetAllPersonTask extends AsyncTask<String, Void, AllPersonsResponse> {

        /**
         * This method is done in a background thread, still allowing UI without a freeze-up
         * @param strings the strings passed into the background thread for network action
         * @return the allPersonResponse
         */
        @Override
        protected AllPersonsResponse doInBackground(String... strings) {
            String authToken = strings[0];
            AllPersonsResponse response = new AllPersonsResponse();
            try {
//                Perform network operation
                ServerProxy serverProxy = new ServerProxy();
                response = serverProxy.getPersons(authToken, host, port);
            } catch (IOException e) {
                System.out.println("IOException occurred while getting family data: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Generic Exception occurred while getting family data: " + e.getMessage());
            }
            return response;
        }
    }
}
