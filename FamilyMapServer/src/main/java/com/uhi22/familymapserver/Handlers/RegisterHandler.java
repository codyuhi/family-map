/** The RegisterHandler class is used to handle requests whose URL denotes that the client
 *  desires to add another User and its associated data to the database for the application
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.familymapserver.Handlers;

import com.uhi22.familymapserver.Errors.*;
import com.uhi22.shared.Requests.RegisterRequest;
import com.uhi22.shared.Responses.Response;
import com.uhi22.familymapserver.Util.JsonUtil;
import com.uhi22.familymapserver.Service.RegisterService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.HttpURLConnection;

public class RegisterHandler extends RequestHandler implements HttpHandler {

    /**
     * handle takes the given request data, performs business logic by calling the RegisterService class,
     * then send an HTTP response to the client containing the requested LoginResponse data (which has an AuthKey)
     * and the newly created User's data
     *
     * Use the adopted handle method from the RequestHandler class to work with the request data
     * If the request method is not a POST request, then an error response is returned to the client
     * If the request has no data, then an error response is returned to the client
     * A Response POJO is created and set to be the results of the login method from the LoginService class
     * If there were no errors, a successful response containing the appropriate LoginResponse data is returned to the client
     * If there is missing data in the request body, then an error response is returned to the client
     * If the request body contains an invalid value, then an error response is returned to the client
     * If the submitted username is already taken, then an error response is returned to the client
     * If an internal server error or a data access exception is thrown during the service call,
     * an error response is returned to the client
     *
     * If the response encounters an error while trying to send, it is caught and printed
     *
     * @param httpExchange represents the httpExchange that takes place as part of the client-server architecture
     */
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            System.out.println("\nCalled the RegisterHandler");
//            Use the adopted handle method from the RequestHandler class to work with the request data
            super.handle(httpExchange);
            RegisterRequest registerRequest = JsonUtil.deserialize(requestBody, RegisterRequest.class);
            if(!"POST".equals(requestMethod)) {
//                If the request method is not a POST request, then an error response is returned to the client
                respond(defineFailure("Invalid Request Method Error"), HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            } else if(registerRequest == null) {
//                If the request has no data, then an error response is returned to the client
                respond(defineFailure("Empty Request Body Error"),HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }
            try {
//                A Response POJO is created and set to be the results of the load method from the LoadService class
                Response registerResponse = RegisterService.register(registerRequest);
//                If there were no errors, a successful response containing the appropriate LoginResponse data is returned to the client
                respond(registerResponse, HttpURLConnection.HTTP_OK);
            } catch (RequestPropertyMissingValue requestPropertyMissingValue) {
//                If there is missing data in the request body, then an error response is returned to the client
                requestPropertyMissingValue.printStackTrace();
                respond(defineFailure("Request Property Missing Value Error"), HttpURLConnection.HTTP_BAD_REQUEST);
            } catch (RequestPropertyInvalidValue requestPropertyInvalidValue) {
//                If the request body contains an invalid value, then an error response is returned to the client
                requestPropertyInvalidValue.printStackTrace();
                respond(defineFailure("Request Property Invalid Value Error"), HttpURLConnection.HTTP_BAD_REQUEST);
            } catch (UserNameAlreadyTakenError userNameAlreadyTakenError) {
//                If the submitted username is already taken, then an error response is returned to the client
                userNameAlreadyTakenError.printStackTrace();
                respond(defineFailure("Username Already Taken Error"), HttpURLConnection.HTTP_BAD_REQUEST);
            } catch (InternalServerError | DataAccessException internalServerError) {
//                If an internal server error or a data access exception is thrown during the service call,
//                an error response is returned to the client
                internalServerError.printStackTrace();
                respond(defineFailure("Internal Server Error"), HttpURLConnection.HTTP_INTERNAL_ERROR);
            }
        } catch (IOException e) {
//            If the response encounters an error while trying to send, it is caught and printed
            e.printStackTrace();
        }
    }
}
