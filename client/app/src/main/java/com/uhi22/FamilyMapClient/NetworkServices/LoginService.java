/**
 * This class allows for network functionality supporting LOGIN operations
 * This class can login a person and retrieve an authToken
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient.NetworkServices;

import android.content.Context;
import android.os.AsyncTask;

import com.uhi22.shared.Requests.LoginRequest;
import com.uhi22.shared.Responses.LoginResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class LoginService {

//    Declare network variables used throughout the class
    private static String host, username, password;
    private static int port;
    private static URL loginUrl;
    private static LoginResponse loginResponse;

    /**
     * Constructor to define network variables and build the loginRequest
     * @param context This is an artifact of a previous iteration I did, which is no longer relevant. I just didn't want to go in and delete it everywhere
     * @param host the given host
     * @param port the given port
     * @param username the given username
     * @param password the given password
     * @throws MalformedURLException happens if the URL is not the right format for an HTTP connection
     */
    public LoginService(Context context, String host, int port, String username, String password) throws MalformedURLException {
        LoginService.host = host;
        LoginService.port = port;
        LoginService.username = username;
        LoginService.password = password;
        loginUrl = new URL("http://" + host + ":" + port + "/user/login/");
        loginResponse = new LoginResponse();
        loginResponse.setSuccess(false);
        loginResponse.setMessage("Unable to set login response");
    }

    /**
     * This method creates a loginTask, which performs the network functionality for logging into the server
     * and getting an authtoken
     * @param request the built loginRequest
     * @return a LoginResponse
     * @throws ExecutionException happens when problem happens in execution
     * @throws InterruptedException happens when interrupted during network interaction
     */
    public LoginResponse executeRequest(LoginRequest request) throws ExecutionException, InterruptedException {
        LoginTask loginTask = new LoginTask();
        return loginTask.execute(request).get();
    }

    /**
     * Child class because I used something like this in a previous android app in the IT program
     * The child class extends an AsyncTask, allowing for background thread for network and still supports UI
     */
    private static class LoginTask extends AsyncTask<LoginRequest, Void, LoginResponse> {

        /**
         * This method is done in a background thread, still allowing UI without a freeze-up
         * @param loginRequests the loginrequest passed into the background thread for network action
         * @return the PersonResponse
         */
        @Override
        protected LoginResponse doInBackground(LoginRequest... loginRequests) {

            LoginResponse response = new LoginResponse();
            try {
                ServerProxy serverProxy = new ServerProxy();
                response = serverProxy.login(host, port, username, password, loginUrl, loginResponse);
            } catch (IOException e) {
                System.out.println("IOException occurred while logging in the user: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Generic Exception occurred while logging in the user: " + e.getMessage());
            }
            return response;
        }
    }
}
