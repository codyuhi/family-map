/** The FileHandler class is used to handle requests whose URL denotes that the client
 *  requests a file from the server
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.familymapserver.Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;

public class FileHandler extends RequestHandler implements HttpHandler {

    /**
     * handle takes the given request data, performs business logic to access the requested file,
     * then writes the file data to an HTTP response to the client containing the requested File data
     *
     * Use the adopted handle method from the RequestHandler class to work with the request data
     * If the request method is not a GET request, then an error response is returned to the client
     * If the given requestPath is empty, then return the index file (which is the default file for the server)
     * If a file is requested in a subdirectory of the main web directory, get the full file path
     * If none of the above conditions are met, try to access whatever unmodified requestPath that was given
     * Create a new File object with the contents of the file located at the file path that was given
     * If the file doesn't exist, return the 404 file
     * Else return the file that was requested
     *
     * If the response encounters an error while trying to send or while trying to read the file, it is caught and printed
     *
     * @param httpExchange represents the httpExchange that takes place as part of the client-server architecture
     */
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            System.out.println("\nCalled the FileHandler");
//            Use the adopted handle method from the RequestHandler class to work with the request data
            super.handle(httpExchange);
            if(!"GET".equals(requestMethod)) {
//                If the request method is not a GET request, then an error response is returned to the client
                respond(defineFailure("Invalid Method"), HttpURLConnection.HTTP_BAD_REQUEST);
            }
            String filePath = "web/";
            if(("/".equals(requestPath) && firstParameter == null) || requestPath == null) {
//                If the given requestPath is empty, then return the index file (which is the default file for the server)
                filePath += "index.html";
            } else if("css".equals(firstParameter)) {
//                If a file is requested in a subdirectory of the main web directory, get the full file path
                filePath += firstParameter + "/" + secondParameter;
            } else {
//                If none of the above conditions are met, return whatever unmodified requestPath that was given
                filePath += firstParameter;
            }
//            Create a new File object with the contents of the file located at the file path that was given
            File file = new File(filePath);
            if(!file.exists()) {
//                If the file doesn't exist, return the 404 file
                sendBackFile(new File("web/HTML/404.html"));
            } else {
//                Else return the file that was requested
                sendBackFile(file);
            }
        } catch (IOException e) {
//            If the response encounters an error while trying to send or while trying to read the file, it is caught and printed
            e.printStackTrace();
        }
    }

    /**
     * sendBackFile sends the requested file to the client or the 404 page if the resource couldn't be found
     *
     * Read the file bytes into a bytes array
     * If the 404 file is to be sent, send the response with the 404 HTTP method
     * else send the response with the 200 HTTP OK method
     * If something goes wrong with the above request, fail-safe default to sending the 404 file
     * Write the httpExchange body to an outputstream then close it (sending the response)
     *
     * @param file is the file that was provided by the calling method and will be written to the response
     * @throws IOException occurs if something goes wrong while trying to send the response.
     *                      Will be handled by the calling method
     */
    public void sendBackFile(File file) throws IOException {
//        Read the file bytes into a bytes array
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(file.toPath());
            if("web\\HTML\\404.html".equals(file.getPath())) {
//                If the 404 file is to be sent, send the response with the 404 HTTP method
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, bytes.length);
            } else {
//                else send the response with the 200 HTTP OK method
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytes.length);
            }
        } catch (IOException e) {
//            If something goes wrong with the above request, fail-safe default to sending the 404 file
            File file404 = new File("web/HTML/404.html");
            bytes = Files.readAllBytes(file404.toPath());
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, bytes.length);
        }
//        Write the httpExchange body to an outputstream then close it (sending the response)
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(bytes);
        outputStream.close();
    }
}
