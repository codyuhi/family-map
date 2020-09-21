/** The ClearHandler class is used to handle requests whose URL denotes that the client
 *  wishes to clear all existing data stored in the SQLite database
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.familymapserver.Handlers;

import com.uhi22.familymapserver.Errors.DataAccessException;
import com.uhi22.familymapserver.Errors.InternalServerError;
import com.uhi22.shared.Responses.Response;
import com.uhi22.familymapserver.Service.ClearService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.HttpURLConnection;

public class ClearHandler extends RequestHandler implements HttpHandler {

    /**
     * handle takes the given request data, performs business logic by calling the ClearService class,
     * then sends an HTTP response to the client containing the requested clear data
     *
     * Use the adopted handle method from the RequestHandler class to work with the request data
     * If the request method is not a POST request, then an error response is returned to the client
     * A Response POJO is created and set to be the results of the clear method from the ClearService class
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
            System.out.println("\nCalled the ClearHandler");
//            Use the adopted handle method from the RequestHandler class to work with the request data
            super.handle(httpExchange);
            if(!"POST".equals(requestMethod)) {
//                If the request method is not a POST request, then an error response is returned to the client
                respond(defineFailure("Invalid Request Method Error"), HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }
            try {
//                A Response POJO is created and set to be the results of the clear method from the ClearService class
                Response clearResponse = ClearService.clear();
//                If there were no errors, a successful response containing the appropriate ClearResponse data is returned to the client
                respond(clearResponse, HttpURLConnection.HTTP_OK);
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
