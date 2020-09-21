/**
 * This class allows for network functionality supporting EVENT operations
 * This class can get a single event or all events, given a proper auth token
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient.NetworkServices;

import android.content.Context;
import android.os.AsyncTask;

import com.uhi22.shared.Responses.AllEventsResponse;
import com.uhi22.shared.Responses.EventResponse;

import java.util.concurrent.ExecutionException;

public class EventService {

//    Declare network variables used throughout the class
    private static String host;
    private static int port;

    /**
     * Constructor to define network variables based on given data
     * @param context This is an artifact of a previous iteration I did, which is no longer relevant. I just didn't want to go in and delete it everywhere
     * @param host the given host
     * @param port the given port
     */
    public EventService(Context context, String host, int port) {
        EventService.host = host;
        EventService.port = port;
    }

    /**
     * This method creates a getEventTask, which performs the network functionality for getting a event
     * @param eventID denotes the eventID which is to be retrieved from the server
     * @param authToken the authtoken for auth
     * @return Returns a EventResponse
     * @throws ExecutionException happens when problem happens in execution
     * @throws InterruptedException happens when interrupted during network interaction
     */
    public EventResponse getEvent(String eventID, String authToken) throws ExecutionException, InterruptedException {
        GetEventTask getEventTask = new GetEventTask();
        return getEventTask.execute(eventID, authToken).get();
    }

    /**
     * This method creates a getEventTask, which performs the network functionality for getting all events
     * @param authToken the authtoken for auth
     * @return Returns an allEventsResponse
     * @throws ExecutionException happens when problem happens in execution
     * @throws InterruptedException happens when interrupted during network interaction
     */
    public AllEventsResponse getAllEvents(String authToken) throws ExecutionException, InterruptedException {
        GetAllEventsTask getAllEventsTask = new GetAllEventsTask();
        return getAllEventsTask.execute(authToken).get();
    }

    /**
     * Child class because I used something like this in a previous android app in the IT program
     * The child class extends an AsyncTask, allowing for background thread for network and still supports UI
     */
    private static class GetEventTask extends AsyncTask<String, Void, EventResponse> {

        /**
         * This method is done in a background thread, still allowing UI without a freeze-up
         * @param strings the strings passed into the background thread for network action
         * @return the PersonResponse
         */
        @Override
        protected EventResponse doInBackground(String... strings) {
            String eventID = strings[0];
            String authToken = strings[1];
            EventResponse response = new EventResponse();
            try {
//                Perform network operation
                ServerProxy serverProxy = new ServerProxy();
                response = serverProxy.getEvent(eventID, authToken, host, port);
            } catch (Exception e) {
                System.out.println("Generic Exception occurred while getting event data for the given event");
            }
            return response;
        }
    }

    /**
     * Child class because I used something like this in a previous android app in the IT program
     * The child class extends an AsyncTask, allowing for background thread for network and still supports UI
     */
    private static class GetAllEventsTask extends AsyncTask<String, Void, AllEventsResponse> {

        /**
         * This method is done in a background thread, still allowing UI without a freeze-up
         * @param strings the strings passed into the background thread for network action
         * @return the PersonResponse
         */
        @Override
        protected AllEventsResponse doInBackground(String... strings) {
            String authToken = strings[0];
            AllEventsResponse response = new AllEventsResponse();
            try {
//                Perform network operation
                ServerProxy serverProxy = new ServerProxy();
                response = serverProxy.getEvents(authToken, host, port);
            } catch (Exception e) {
                System.out.println("Generic Exception occurred while getting event data for the given event");
            }
            return response;
        }
    }
}
