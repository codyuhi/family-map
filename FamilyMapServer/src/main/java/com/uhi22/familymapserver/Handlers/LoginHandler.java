/** The LoginHandler class is used to handle requests whose URL denotes that the client
 *  wants to login to the server and receive an authToken for session functionality
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.familymapserver.Handlers;

import com.uhi22.familymapserver.Errors.DataAccessException;
import com.uhi22.familymapserver.Errors.InternalServerError;
import com.uhi22.familymapserver.Errors.RequestPropertyInvalidValue;
import com.uhi22.familymapserver.Errors.RequestPropertyMissingValue;
import com.uhi22.shared.Requests.LoginRequest;
import com.uhi22.shared.Responses.Response;
import com.uhi22.familymapserver.Service.LoginService;
import com.uhi22.familymapserver.Util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.HttpURLConnection;

public class LoginHandler extends RequestHandler implements HttpHandler {

    /**
     * handle takes the given request data, performs business logic by calling the LoginService class,
     * then send an HTTP response to the client containing the requested LoginResponse data (which has an AuthKey)
     *
     * Use the adopted handle method from the RequestHandler class to work with the request data
     * If the request method is not a POST request, then an error response is returned to the client
     * If the request has no data, then an error response is returned to the client
     * A Response POJO is created and set to be the results of the load method from the LoadService class
     * If an internal server error or a data access exception is thrown during the service call,
     * an error response is returned to the client
     * If there is missing data in the request body, then an error response is returned to the client
     * If the username/password combo does not exist, then an error response is returned to the client
     * If there were no errors, a successful response containing the appropriate LoadResponse data is returned to the client
     *
     * If the response encounters an error while trying to send, it is caught and printed
     *
     * @param httpExchange represents the httpExchange that takes place as part of the client-server architecture
     */
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            System.out.println("\nCalled the LoginHandler");
//            Use the adopted handle method from the RequestHandler class to work with the request data
            super.handle(httpExchange);
            LoginRequest loginRequest = JsonUtil.deserialize(requestBody, LoginRequest.class);
            if(!"POST".equals(requestMethod)) {
//                If the request method is not a POST request, then an error response is returned to the client
                respond(defineFailure("Invalid Request Method"), HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            } else if(loginRequest == null) {
//                If the request has no data, then an error response is returned to the client
                respond(defineFailure("Empty Request Body"), HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }
            try {
//                A Response POJO is created and set to be the results of the login method from the LoginService class
                Response loginResponse = LoginService.login(loginRequest);
//                If there were no errors, a successful response containing the appropriate LoginResponse data is returned to the client
                respond(loginResponse, HttpURLConnection.HTTP_OK);
            } catch (InternalServerError | DataAccessException internalServerError) {
//                If an internal server error or a data access exception is thrown during the service call,
//                an error response is returned to the client
                internalServerError.printStackTrace();
                respond(defineFailure("Internal Server Error"), HttpURLConnection.HTTP_INTERNAL_ERROR);
            } catch (RequestPropertyMissingValue requestPropertyMissingValue) {
                requestPropertyMissingValue.printStackTrace();
//                If there is missing data in the request body, then an error response is returned to the client
                respond(defineFailure("Request Property Missing Value Error"), HttpURLConnection.HTTP_BAD_REQUEST);
            } catch (RequestPropertyInvalidValue requestPropertyInvalidValue) {
//                If the username/password combo does not exist, then an error response is returned to the client
                requestPropertyInvalidValue.printStackTrace();
                respond(defineFailure("Username/Password Combination Not Found Error"), HttpURLConnection.HTTP_BAD_REQUEST);
            }
        } catch (IOException e) {
//            If the response encounters an error while trying to send, it is caught and printed
            e.printStackTrace();
        }
    }
}
