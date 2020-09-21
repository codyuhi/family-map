/** The FillHandler class is used to handle requests whose URL denotes that the client
 *  wants to generate filler data for a certain number of generations off of a given username
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.familymapserver.Handlers;

import com.uhi22.familymapserver.Errors.DataAccessException;
import com.uhi22.familymapserver.Errors.InternalServerError;
import com.uhi22.familymapserver.Errors.InvalidGenerationsError;
import com.uhi22.familymapserver.Errors.InvalidUsernameError;
import com.uhi22.shared.Responses.Response;
import com.uhi22.familymapserver.Service.FillService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.HttpURLConnection;

public class FillHandler extends RequestHandler implements HttpHandler {

    /**
     * handle takes the given request data, performs business logic by calling the FillService class,
     * then sends an HTTP response to the client containing the requested Fill data
     *
     * Use the adopted handle method from the RequestHandler class to work with the request data
     * If the request method is not a POST request, then an error response is returned to the client
     * If the username is malformed, then an error response is returned to the client
     * If there is no second parameter denoting the number of generations to be filled, set the second parameter to 4 by default
     * A Response POJO is created and set to be the results of the fill method from the FillService class
     * If an internal server error or a data access exception is thrown during the service call,
     *      an error response is returned to the client
     * If the number of generations is invalid, an error response is returned to the client
     * If the username is invalid, an error response is returned to the client
     * If there were no errors, a successful response containing the appropriate FillResponse data is returned to the client
     *
     * If the response encounters an error while trying to send, it is caught and printed
     *
     * @param httpExchange represents the httpExchange that takes place as part of the client-server architecture
     */
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            System.out.println("\nCalled the FillHandler");
//            Use the adopted handle method from the RequestHandler class to work with the request data
            super.handle(httpExchange);
            if(!"POST".equals(requestMethod)) {
//                If the request method is not a POST request, then an error response is returned to the client
                respond(defineFailure("Invalid Request Method Error"), HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            } else if(firstParameter == null) {
//                If the username is malformed, then an error response is returned to the client
                respond(defineFailure("Invalid Username Error"), HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            } else if(secondParameter == null) {
//                If there is no second parameter denoting the number of generations to be filled, set the second parameter to 4 by default
                secondParameter = "4";
            }
            try {
//                A Response POJO is created and set to be the results of the fill method from the FillService class
                Response fillResponse = FillService.fill(secondParameter, firstParameter);
//                If there were no errors, a successful response containing the appropriate FillResponse data is returned to the client
                respond(fillResponse, HttpURLConnection.HTTP_OK);
            } catch (InternalServerError | DataAccessException internalServerError) {
//                If an internal server error or a data access exception is thrown during the service call,
//                an error response is returned to the client
                internalServerError.printStackTrace();
                respond(defineFailure("Internal Server Error"),HttpURLConnection.HTTP_INTERNAL_ERROR);
            } catch (InvalidGenerationsError invalidGenerationsError) {
//                If the number of generations is invalid, an error response is returned to the client
                invalidGenerationsError.printStackTrace();
                respond(defineFailure("Invalid Generations Error"), HttpURLConnection.HTTP_BAD_REQUEST);
            } catch (InvalidUsernameError invalidUsernameError) {
//                If the username is invalid, an error response is returned to the client
                invalidUsernameError.printStackTrace();
                respond(defineFailure("Invalid Username Error"), HttpURLConnection.HTTP_BAD_REQUEST);
            }
        } catch (IOException e) {
//            If the response encounters an error while trying to send, it is caught and printed
            e.printStackTrace();
        }
    }
}
