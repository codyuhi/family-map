/** The server class is the main class from which all functionality will originate
 * The server accepts the following command line arguments:
 *  - Port number on which the server will accept client connections.  This value is an integer in the range 1-65535 EX: 8080
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Server;

import Handlers.*;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;

import static java.lang.Integer.parseInt;

public class Server {

    /**
     * This is the HttpServer that the whole program will run off of
     */
    private static HttpServer server;

    /** The main function is the function that will run upon invocation of the server
     * The main function will call handlers based on the type of requests provided, which will call services, which will call DAOs
     * The main function will also pass any string command line input to the handlers
     * As a void method, it does not return any value, it simply performs functionality
     *
     * This program can be run using "java -cp target/classes Server.Server <port_number>" on the cmd line while in the FamilyMapServer directory
     * First the method checks to see if the user input contains the port number
     *  if not, the server program returns and requires the user to try again
     * The port number is parsed as an int and if the port number is outside the valid range of between 1-65535, feedback is provided and the program exits
     * The method checks to see if the server has all the necessary resources to run the program
     *  if not, the server program returns
     * If all the above conditions are met, this method calls the Server.run method to start the server/webAPI functionality.
     *
     * @param args the port number on which the server will accept client connections
     */
    public static void main(String[] args) {
//        Run this using java -cp target/classes Server.Server <port_number>
//        Use the above command from the FamilyMapServer directory

        System.out.println("Server command executed\n");
        int port = 0;
        if(args.length != 1) {
            System.out.println("Invalid input given. Please give input in this format: 'java server <port_number>'");
            System.out.println("\nExiting program");
            return;
        }
        port = parseInt(args[0]);
//        if the port number is outside of the range 1-65535, exit the program
        if(port > 65535 || port < 1) {
            System.out.println("Invalid port number given. Please specify a port number between 1 and 65535");
            return;
        }
//        else if the server is missing any of the resources it needs (i.e. database), exit the program
        else if(!hasAllResources()) {
            System.out.println("Unable to access all server resources.  Please verify that all required resources are setup properly");
            return;
        }
        System.out.println("All necessary resources are accessible");
//        else if the port number is inside the range, call Server.run to start the server on that port
        run(port);
    }

    /** The run method actually starts the server for interaction with the client using the given port number
     *  It creates a handler for GET requests and POST requests (the two types of requests given in the specs)
     *  It is called by the main function once the server determines that all necessary resources are present for the server to run
     *
     *  The method creates an HttpServer on the given port and network interface (wlan1 from my laptop)
     *  The method creates contexts for all the supported endpoints.
     *  These contexts will handle requests that are directed at those endpoints as designated by the request URL.
     *  When the contexts are created, start the server and listen for traffic
     *
     *  If any of the above fails, don't start the server and print the error
     *
     * @param port  the port number on which the server will accept client connections
     */
    private static void run(int port) {
        System.out.println("Running server . . . \n");
//        Create an http server using this device's IP address and listening for requests on the given port
        try {
//            The method creates an HttpServer on the given port and network interface (wlan1 from my laptop)
            server = HttpServer.create(new InetSocketAddress(port), 10);
            NetworkInterface networkInterface = NetworkInterface.getByName("wlan1");

//            The method creates contexts for all the supported endpoints.
//            These contexts will handle requests that are directed at those endpoints as designated by the request URL.
            server.createContext("/user/register", new RegisterHandler());
            server.createContext("/user/login", new LoginHandler());
            server.createContext("/clear", new ClearHandler());
            server.createContext("/fill", new FillHandler());
            server.createContext("/load", new LoadHandler());
            server.createContext("/person/", new PersonHandler());
            server.createContext("/person", new AllPersonsHandler());
            server.createContext("/event/", new EventHandler());
            server.createContext("/event", new AllEventsHandler());
            server.createContext("/", new FileHandler());
            System.out.println("Server running on http:/" + networkInterface.getInetAddresses().nextElement() + "/" + String.valueOf(port));

//            When the contexts are created, start the server and listen for traffic
            server.start();

        } catch(Exception e) {
//            If any of the above fails, don't start the server and print the error
            e.printStackTrace();
        }
    }

    /**
     * The hasAllResources method validates that all necessary resources exist and are readable by the server before trying to start the server
     * If the web directory, the json directory, or the db directory do not exist, this will return false
     * @return
     */
    private static boolean hasAllResources() {
        File web = new File("web");
        File json = new File("json");
        File db = new File("db");
        return web.exists() && json.exists() && db.exists();
    }
}
