/** The FillHandler class is used to handle requests whose URL denotes that the client
 *  wants to populate the database with given data for Persons, Users, or Events
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Handlers;

import Errors.DataAccessException;
import Errors.InternalServerError;
import Errors.InvalidRequestDataError;
import Requests.LoadRequest;
import Responses.Response;
import Service.LoadService;
import Util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.HttpURLConnection;

public class LoadHandler extends RequestHandler implements HttpHandler {

    /**
     * handle takes the given request data, performs business logic by calling the FillService class,
     * then sends an HTTP response to the client containing the requested Fill data
     *
     * Use the adopted handle method from the RequestHandler class to work with the request data
     * Access the passed request data by deserializing it into a LoadRequest POJO
     * If the request method is not a POST request, then an error response is returned to the client
     * If the request has no data, then an error response is returned to the client
     * A Response POJO is created and set to be the results of the load method from the LoadService class
     * If an internal server error or a data access exception is thrown during the service call,
     *      an error response is returned to the client
     * If there were no errors, a successful response containing the appropriate LoadResponse data is returned to the client
     *
     * If the response encounters an error while trying to send, it is caught and printed
     *
     * @param httpExchange represents the httpExchange that takes place as part of the client-server architecture
     */
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            System.out.println("\nCalled the LoadHandler");
//            Use the adopted handle method from the RequestHandler class to work with the request data
            super.handle(httpExchange);
//            Access the passed request data by deserializing it into a LoadRequest POJO
            LoadRequest loadRequest = JsonUtil.deserialize(requestBody, LoadRequest.class);
            if(!"POST".equals(requestMethod)) {
//                If the request method is not a POST request, then an error response is returned to the client
                respond(defineFailure("Invalid Request Method Error"), HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            } else if(loadRequest == null) {
//                If the request has no data, then an error response is returned to the client
                respond(defineFailure("Empty Request Body Error"), HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }
            try {
//                A Response POJO is created and set to be the results of the load method from the LoadService class
                Response loadResponse = LoadService.load(loadRequest);
//                If there were no errors, a successful response containing the appropriate LoadResponse data is returned to the client
                respond(loadResponse, HttpURLConnection.HTTP_OK);
            } catch (InvalidRequestDataError invalidRequestDataError) {
                invalidRequestDataError.printStackTrace();
                respond(defineFailure("Invalid Request Data Error"), HttpURLConnection.HTTP_BAD_REQUEST);
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
