/**
 * This class allows for network functionality supporting REGISTER operations
 * This class can register a person and retrieve an authToken
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient.NetworkServices;

import android.content.Context;
import android.os.AsyncTask;

import com.uhi22.shared.Requests.RegisterRequest;
import com.uhi22.shared.Responses.RegisterResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class RegisterService {

//    Declare network variables used throughout the class
    private static String host;
    private static int port;

    /**
     * Constructor to define network variables based on given data
     * @param context This is an artifact of a previous iteration I did, which is no longer relevant. I just didn't want to go in and delete it everywhere
     * @param host the given host
     * @param port the given port
     */
    public RegisterService(Context context, String host, int port) {
        RegisterService.host = host;
        RegisterService.port = port;
    }

    /**
     * This method creates a RegisterTask, which performs the network functionality for registering a new user
     * and getting an authtoken
     * @param request the built RegisterRequest
     * @return a RegisterResponse
     * @throws ExecutionException happens when problem happens in execution
     * @throws InterruptedException happens when interrupted during network interaction
     */
    public RegisterResponse executeRequest(RegisterRequest request) throws ExecutionException, InterruptedException {
        RegisterTask registerTask = new RegisterTask();
        return registerTask.execute(request).get();
    }

    /**
     * Child class because I used something like this in a previous android app in the IT program
     * The child class extends an AsyncTask, allowing for background thread for network and still supports UI
     */
    private static class RegisterTask extends AsyncTask<RegisterRequest, Void, RegisterResponse> {

        /**
         * This method is done in a background thread, still allowing UI without a freeze-up
         * @param registerRequests the registerRequests passed into the background thread for network action
         * @return the PersonResponse
         */
        @Override
        protected RegisterResponse doInBackground(RegisterRequest... registerRequests) {
            RegisterResponse response = new RegisterResponse();
            RegisterRequest registerRequest = registerRequests[0];
            try {
                ServerProxy serverProxy = new ServerProxy();
                response = serverProxy.register(registerRequest, host, port);
            } catch (IOException e) {
                System.out.println("IOException occurred while registering the user: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Generic Exception occurred while registering the user: " + e.getMessage());
            }
            return response;
        }
    }
}
