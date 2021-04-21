/** The RequestHandler class is a parent class which contains methods and variables that should be accessible to
 *  all the handler methods because their use is consistent between them
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Handlers;

import Responses.Response;
import Util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RequestHandler implements HttpHandler {

    /**
     * This is an HttpExchange object which allows for data reception/transmission over the network
     * and for the processed business logic to be written back to the client
     */
    protected HttpExchange httpExchange;
    /**
     * This variable holds the request method of the httpExchange (i.e. GET/POST)
     * This allows for easy validation of whether the method is valid or not based on the request path
     */
    protected String requestMethod;
    /**
     * This variable holds the request body of the httpExchange
     * The data included with POST requests is access here, in JSON format
     */
    protected String requestBody;
    /**
     * This variable holds the Authorization Token included in the httpExchange
     * The authToken is passed in the HTTP Authorization header
     * This allows for easy validation of whether the User should be allowed access to data resources
     * This also allows for personalization as well
     */
    protected String authToken;
    /**
     * This variable holds the first request parameter when the request URL holds significant request data
     * This is taken from the URL
     */
    protected String firstParameter;
    /**
     * This variable holds the first request parameter when the request URL holds significant request data
     * This is taken from the URL
     */
    protected String secondParameter;
    /**
     * This variable holds the request path of the httpExchange
     * This allows for easy extraction of the URL parameters and routing
     */
    protected String requestPath;
    /**
     * This variable holds the full request URI in case further breakdown is necessary to handle the request
     */
    protected String requestUri;

    /**
     * Empty constructor
     */
    public RequestHandler() {}

    /**
     * handle takes the given request data given in the httpExchange and extracts meaningful information from it
     * The information gained from the handle method is used to define the business logic and provide the client with
     * the desired data based on operations that are requested
     *
     * Pull information from the HttpExchange
     * Pull information from the requestURI
     * Define parameters included in the requestURI
     * Make the httpExchange available throughout the class
     *
     * If the response encounters an error while trying to send, it is caught and printed
     *
     * @param httpExchange represents the httpExchange that takes place as part of the client-server architecture
     */
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
//            Pull information from the HttpExchange
            firstParameter = null;
            secondParameter = null;
            requestMethod = httpExchange.getRequestMethod();
            requestBody = convertRequestBody(httpExchange.getRequestBody());
            authToken = httpExchange.getRequestHeaders().getFirst("Authorization");
            if(authToken != null) {
                System.out.println("Auth Token: " + authToken);
            }
            requestPath = httpExchange.getHttpContext().getPath();
//            Pull information from the requestURI
            if(!httpExchange.getRequestURI().toString().equals("") && !requestPath.equals("event") && !requestPath.equals("person")){
                requestUri = httpExchange.getRequestURI().toString();
            }else {
                requestUri = httpExchange.getRequestURI().toString().replaceAll(requestPath,"");
            }
//            Define parameters included in the requestURI
            String[] parameters = requestUri.trim().split("/");
            if(parameters.length > 0) {
                if("".equals(parameters[0])) {
                    parameters = Arrays.copyOfRange(parameters, 1, parameters.length);
                }
            }
//            There should be no more than 3 parameters. Providing that many shall return an error
            if(parameters.length > 3) {
                Response response = new Response();
                respond(response, HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }
            if(parameters.length >= 1) {
                firstParameter = parameters[0];
            }
            if(parameters.length >= 2) {
                secondParameter = parameters[1];
            }
            if(parameters.length == 3) {
                firstParameter = secondParameter;
                secondParameter = parameters[2];
            }
//            Make the httpExchange available throughout the class
            this.httpExchange = httpExchange;
        } catch (IOException e) {
//            If the response encounters an error while trying to send, it is caught and printed
            e.printStackTrace();
        }
    }

    /**
     * convertRequestBody takes the given inputStream and iterates through it to convert the raw data into a parsable String
     *
     * Create an inputStreamReader and bufferedReader and pass the inputStream through it
     * Use a StringBuilder for efficient conversion from raw data to Stringified data
     * Iterate through the buffered reader and build the StringBuilder
     * Close readers and convert the StringBuilder to String to be returned
     *
     * @param inputStream is an inputStream which is passed from the handle method
     *                    the inputStream contains the request body and returns it
     *                    as a string which can be deserialized into POJOs
     * @return provides a String containing the requestBody data that can be deserialized
     * @throws IOException this occurs when there is a problem with reading the inputStream
     *                     and is handled by the calling method
     */
    protected String convertRequestBody(InputStream inputStream) throws IOException {
//        Create an inputStreamReader and bufferedReader and pass the inputStream through it
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        int iteration = 0;
//        Use a StringBuilder for efficient conversion from raw data to Stringified data
        StringBuilder stringBuilder = new StringBuilder();
//        Iterate through the buffered reader and build the StringBuilder
        while((iteration = bufferedReader.read()) != -1) {
            stringBuilder.append((char) iteration);
        }
//        Close readers and convert the StringBuilder to String to be returned
        bufferedReader.close();
        inputStreamReader.close();
        System.out.println("Handling Request:\n" + stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * respond takes the given response POJO and response code and send it to the client
     *
     * Serialize the Response POJO
     * Set response headers and body onto the outputstream
     * Write, send, and close the OutputStream, which send the actual response to the client
     * @param response is a Response POJO which contains the results of business logic that took place from the request
     * @param responseCode is an integer which contains industry-wide standards for what the status of the response is
     * @throws IOException this occurs when something went wrong while writing the response
     *                     this exception is handled by the calling method
     */
    protected void respond(Response response, int responseCode) throws IOException {
        System.out.println("Sending response . . .");
//        Serialize the Response POJO
        String responseBody = JsonUtil.serialize(response);
//        Set response headers and body onto the outputstream
        this.httpExchange.sendResponseHeaders(responseCode, responseBody.getBytes().length);
        OutputStream outputStream = this.httpExchange.getResponseBody();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
//        Write, send, and close the OutputStream, which send the actual response to the client
        outputStreamWriter.write(responseBody);
        outputStreamWriter.flush();
        outputStreamWriter.close();
        outputStream.close();
        System.out.println("Response sent to client successfully!\nCode: " +
                responseCode + "\nLength: " +
                responseBody.getBytes().length + "\nBody:\n" +
                responseBody + "\n");
    }

    /**
     * defineFailure provides an easy way to build a Response POJO based off a given String message
     *
     * Create the new Response instance and set its message attribute to be the given message String
     * Set the success to false because this method is only called upon failed events
     *
     * @param message is a message which is customized based off the error that triggered it
     * @return provides a Response POJO which can be easily streamed back to the client
     */
    protected Response defineFailure(String message) {
//        Create the new Response instance and set its message attribute to be the given message String
        Response errorResponse = new Response();
        errorResponse.setMessage(message);
//        Set the success to false because this method is only called upon failed events
        errorResponse.setSuccess(false);
        return errorResponse;
    }
}
