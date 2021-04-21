/**
 * This class has several methods in it which allow for network interfacing with the server
 * Supported operations include registration, login, get person, get all persons, get event, get all events,
 * clear, and load
 *
 * This class can be used with junit for tests, as opposed to doing the asynctasks directly with the network functionality
 *
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient.NetworkServices;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.uhi22.familymapserver.Util.JsonUtil;
import com.uhi22.shared.Requests.LoadRequest;
import com.uhi22.shared.Requests.RegisterRequest;
import com.uhi22.shared.Responses.AllEventsResponse;
import com.uhi22.shared.Responses.AllPersonsResponse;
import com.uhi22.shared.Responses.EventResponse;
import com.uhi22.shared.Responses.LoginResponse;
import com.uhi22.shared.Responses.PersonResponse;
import com.uhi22.shared.Responses.RegisterResponse;
import com.uhi22.shared.Responses.Response;
import com.uhi22.shared.model.Event;
import com.uhi22.shared.model.Person;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ServerProxy {

    /**
     * This method performs network functionality to support the REGISTER operation
     * host and port data is provided to reach the server
     * Registration information is included for a new user to be created and random data generated
     *
     * @param registerRequest the given RegisterRequest
     * @param host  the given host
     * @param port the given port
     * @return a RegisterResponse
     * @throws IOException happens if the URL being built is invalid
     */
    public RegisterResponse register(RegisterRequest registerRequest, String host, int port) throws IOException {
//        build the Http Request
        RegisterResponse response = new RegisterResponse();
        URL registerURL = new URL("http://" + host + ":" + port + "/user/register");
        HttpURLConnection connection = (HttpURLConnection) registerURL.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
        connection.setDoInput(true);
        connection.setDoOutput(true);
//        Write to outputstream on the connection
        DataOutputStream write = new DataOutputStream(connection.getOutputStream());
        write.writeBytes("{\"userName\":\"" + registerRequest.getUserName() +
                "\",\"password\":\"" + registerRequest.getPassword() +
                "\",\"email\":\"" + registerRequest.getEmail() +
                "\",\"firstName\":\"" + registerRequest.getFirstName() +
                "\",\"lastName\":\"" + registerRequest.getLastName() +
                "\",\"gender\":\"" + registerRequest.getGender() +
                "\"}");
        write.flush();
        write.close();
//        Close connection and read the response
        InputStreamReader read;
        try {
            read = new InputStreamReader(connection.getInputStream());
        } catch (Exception e) {
            read = new InputStreamReader(connection.getErrorStream());
        }
        BufferedReader br = new BufferedReader(read);
        String text = "";
        String JSONResponse = "";
        while((text = br.readLine()) != null) {
            JSONResponse += text;
        }
//        Get the data for successful registration
        JsonObject jsonObject = new JsonParser().parse(JSONResponse).getAsJsonObject();
        if(jsonObject.get("success").getAsBoolean()) {
            response.setAuthToken(jsonObject.get("authToken").getAsString());
            response.setUserName(jsonObject.get("userName").getAsString());
            response.setPersonID(jsonObject.get("personID").getAsString());
            response.setSuccess(jsonObject.get("success").getAsBoolean());
            return response;
        } else {
//            failed registration arrives here
            response.setSuccess(false);
            response.setMessage(jsonObject.get("message").getAsString());
        }
        return response;
    }

    /**
     * This method performs network functionality to support the LOGIN operation
     * host and port data is provided to reach the server
     * Login information is included for an existing user to be logged into the server and receive authToken
     *
     * @param host the given host
     * @param port the given port
     * @param username the given username
     * @param password the given password
     * @param loginUrl the given url
     * @param loginResponse the given response
     * @return a LoginResponse
     * @throws IOException happens if the URL being built is invalid
     */
    public LoginResponse login(String host, int port, String username, String password, URL loginUrl, LoginResponse loginResponse) throws IOException {
//        build the Http Request
        LoginResponse response = new LoginResponse();
        HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoInput(true);
        connection.setDoOutput(true);
//        Write to outputstream on the connection
        DataOutputStream write = new DataOutputStream(connection.getOutputStream());
        write.writeBytes("{\"userName\":\"" + username + "\", \"password\": \"" + password + "\"}");
        write.flush();
        write.close();
//        Close connection and read the response
        InputStreamReader read;
        try {
            read = new InputStreamReader(connection.getInputStream());
        } catch (Exception e) {
            read = new InputStreamReader(connection.getErrorStream());
        }
        BufferedReader br = new BufferedReader(read);
        String text = "";
        StringBuilder JSONResponse = new StringBuilder();
        while((text = br.readLine()) != null) {
            JSONResponse.append(text);
        }
//        Get the data for successful registration
        JsonObject jsonObject = new JsonParser().parse(JSONResponse.toString()).getAsJsonObject();
        if(jsonObject.get("success").getAsBoolean()) {
            String authToken = jsonObject.get("authToken").getAsString();
            String personID = jsonObject.get("personID").getAsString();
            boolean success = jsonObject.get("success").getAsBoolean();
            loginResponse.setMessage("");
            loginResponse.setAuthToken(authToken);
            loginResponse.setPersonID(personID);
            loginResponse.setUserName(username);
            loginResponse.setSuccess(success);
            response = loginResponse;
            return response;
        }
//            failed operation arrives here
        response.setSuccess(false);
        response.setMessage("Error logging in");
        return response;
    }

    /**
     * This method performs network functionality to support PERSON operations
     * host and port data is provided to reach the server
     * authToken and personID are included to get the right person and verify permission
     *
     * @param personID the given personID
     * @param authToken the given authToken
     * @param host the given host
     * @param port the given port
     * @return a PersonResponse
     * @throws IOException happens if the URL being built is invalid
     */
    public PersonResponse  getPerson(String personID, String authToken, String host, int port) throws IOException {
//        build the Http Request
        PersonResponse response = new PersonResponse();
        URL getPersonUrl = new URL("http://" + host + ":" + port + "/person/" + personID);
        HttpURLConnection connection = (HttpURLConnection) getPersonUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", authToken);
//        Close connection and read the response
        InputStreamReader read;
        try {
            read = new InputStreamReader(connection.getInputStream());
        } catch (Exception e) {
            read = new InputStreamReader(connection.getErrorStream());
        }
        BufferedReader br = new BufferedReader(read);
        String text = "";
        String JSONResponse = "";
        while((text = br.readLine()) != null) {
            JSONResponse += text;
        }
//        Get the data for successful operation
        JsonObject jsonObject = new JsonParser().parse(JSONResponse).getAsJsonObject();
        if(jsonObject.get("success").getAsBoolean()) {
            response.setAssociatedUsername(jsonObject.get("associatedUsername").getAsString());
            response.setPersonID(jsonObject.get("personID").getAsString());
            response.setFirstName(jsonObject.get("firstName").getAsString());
            response.setLastName(jsonObject.get("lastName").getAsString());
            response.setGender(jsonObject.get("gender").getAsString());
            if(jsonObject.get("fatherID") != null) {
                response.setFatherID(jsonObject.get("fatherID").getAsString());
            } else {
                response.setFatherID(null);
            }
            if(jsonObject.get("motherID") != null) {
                response.setMotherID(jsonObject.get("motherID").getAsString());
            } else {
                response.setMotherID(null);
            }
            if(jsonObject.get("spouseID") != null) {
                response.setSpouseID(jsonObject.get("spouseID").getAsString());
            } else {
                response.setSpouseID(null);
            }
            response.setSuccess(jsonObject.get("success").getAsBoolean());
            return response;
        }
//            failed operation arrives here
        response.setSuccess(false);
        response.setMessage("Error getting Person info for personID: " + personID);
        return response;
    }

    /**
     * This method performs network functionality to support PERSON operations
     * host and port data is provided to reach the server
     * authToken is included to verify permission
     *
     * @param authToken the given authToken
     * @param host the given host
     * @param port the given port
     * @return a PersonResponse
     * @throws IOException happens if the URL being built is invalid
     */
    public AllPersonsResponse getPersons(String authToken, String host, int port) throws IOException {
//        build the Http Request
        AllPersonsResponse response = new AllPersonsResponse();
        URL getPersonUrl = new URL("http://" + host + ":" + port + "/person");
        HttpURLConnection connection = (HttpURLConnection) getPersonUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", authToken);
//        Close connection and read the response
        InputStreamReader read;
        try {
            read = new InputStreamReader(connection.getInputStream());
        } catch (Exception e) {
            read = new InputStreamReader(connection.getErrorStream());
        }
        BufferedReader br = new BufferedReader(read);
        String text = "";
        String JSONResponse = "";
        while((text = br.readLine()) != null) {
            JSONResponse += text;
        }
//        Get the data for successful operation
        JsonObject jsonObject = new JsonParser().parse(JSONResponse).getAsJsonObject();
        Boolean success = jsonObject.get("success").getAsBoolean();
        if(success) {
            ArrayList<Person> data = new ArrayList<Person>();
            JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
            for(JsonElement person : jsonArray) {
                String object = JsonUtil.serialize(person.getAsJsonObject());
                data.add(JsonUtil.deserialize(object, Person.class));
            }
            response.setSuccess(success);
            response.setData(data);
            return response;
        }
//            failed operation arrives here
        response.setSuccess(false);
        response.setMessage("Error getting Family info");
        return response;
    }

    /**
     * This method performs network functionality to support EVENT operations
     * host and port data is provided to reach the server
     * eventID and authToken are given to get the right event and verify permission
     *
     * @param eventID the given eventID
     * @param authToken the given authToken
     * @param host the given host
     * @param port the given port
     * @return a EventResponse
     * @throws IOException happens if the URL being built is invalid
     */
    public EventResponse getEvent(String eventID, String authToken, String host, int port) throws IOException {
//        build the Http Request
        EventResponse response = new EventResponse();
        URL getEventUrl = new URL("http://" + host + ":" + port + "/event/" + eventID);
        HttpURLConnection connection = (HttpURLConnection) getEventUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", authToken);
//        Close connection and read the response
        InputStreamReader read;
        try {
            read = new InputStreamReader(connection.getInputStream());
        } catch (Exception e) {
            read = new InputStreamReader(connection.getErrorStream());
        }
        BufferedReader br = new BufferedReader(read);
        String text = "";
        String JSONResponse = "";
        while((text = br.readLine()) != null) {
            JSONResponse += text;
        }
//        Get the data for successful operation
        JsonObject jsonObject = new JsonParser().parse(JSONResponse).getAsJsonObject();
        if(jsonObject.get("success").getAsBoolean()) {
            response.setAssociatedUsername(jsonObject.get("associatedUsername").getAsString());
            response.setEventID(jsonObject.get("eventID").getAsString());
            response.setPersonID(jsonObject.get("personID").getAsString());
            response.setLatitude(jsonObject.get("latitude").getAsFloat());
            response.setLongitude(jsonObject.get("longitude").getAsFloat());
            response.setCountry(jsonObject.get("country").getAsString());
            response.setCity(jsonObject.get("city").getAsString());
            response.setEventType(jsonObject.get("eventType").getAsString());
            response.setYear(jsonObject.get("year").getAsInt());
            response.setSuccess(jsonObject.get("success").getAsBoolean());
            return response;
        }
//            failed operation arrives here
        response.setSuccess(false);
        response.setMessage("Error getting Event info for EventID: " + eventID);
        return response;
    }

    /**
     * This method performs network functionality to support EVENT operations
     * host and port data is provided to reach the server
     * authToken are given to verify permission
     *
     * @param authToken the given authToken
     * @param host the given host
     * @param port the given port
     * @return a AllEventsResponse
     * @throws IOException happens if the URL being built is invalid
     */
    public AllEventsResponse getEvents(String authToken, String host, int port) throws IOException {
//        build the Http Request
        AllEventsResponse response = new AllEventsResponse();
        URL getEventUrl = new URL("http://" + host + ":" + port + "/event");
        HttpURLConnection connection = (HttpURLConnection) getEventUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", authToken);
//        Close connection and read the response
        InputStreamReader read;
        try {
            read = new InputStreamReader(connection.getInputStream());
        } catch (Exception e) {
            read = new InputStreamReader(connection.getErrorStream());
        }
        BufferedReader br = new BufferedReader(read);
        String text = "";
        String JSONResponse = "";
        while((text = br.readLine()) != null) {
            JSONResponse += text;
        }
//        Get the data for successful operation
        JsonObject jsonObject = new JsonParser().parse(JSONResponse).getAsJsonObject();
        if(jsonObject.get("success").getAsBoolean()) {
            ArrayList<Event> data = new ArrayList<Event>();
            JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
            for(JsonElement event : jsonArray) {
                String object = JsonUtil.serialize(event.getAsJsonObject());
                data.add(JsonUtil.deserialize(object, Event.class));
            }
            response.setSuccess(jsonObject.get("success").getAsBoolean());
            response.setData(data);
            return response;
        }
//            failed operation arrives here
        response.setSuccess(false);
        response.setMessage("Error getting all Event info");
        return response;
    }


    /**
     * This method performs network functionality to support EVENT operations
     * host and port data is provided to reach the server
     * authToken are given to verify permission
     *
     * @param host the given host
     * @param port the given port
     * @return a Response
     * @throws IOException happens if the URL being built is invalid
     */
    public Response clear(String host, int port) throws IOException {
//        build the Http Request
        Response response = new Response();
        URL clearUrl = new URL("http://" + host + ":" + port + "/clear");
        HttpURLConnection connection = (HttpURLConnection) clearUrl.openConnection();
        connection.setRequestMethod("POST");
//        Close connection and read the response
        InputStreamReader read;
        try {
            read = new InputStreamReader(connection.getInputStream());
        } catch (Exception e) {
            read = new InputStreamReader(connection.getErrorStream());
        }
        BufferedReader br = new BufferedReader(read);
        String text = "";
        String JSONResponse = "";
        while((text = br.readLine()) != null) {
            JSONResponse += text;
        }
//        Get the data for successful or failed operation
        JsonObject jsonObject = new JsonParser().parse(JSONResponse).getAsJsonObject();
        response.setSuccess(jsonObject.get("success").getAsBoolean());
        response.setMessage(jsonObject.get("message").getAsString());
        return response;
    }


    /**
     * This method performs network functionality to support the LOGIN operation
     * host and port data is provided to reach the server
     * Login information is included for an existing user to be logged into the server and receive authToken
     *
     * @param host the given host
     * @param port the given port
     * @return a Response
     * @throws IOException happens if the URL being built is invalid
     */
    public Response loadToServer(LoadRequest loadRequest, String host, int port) throws IOException {
//        build the Http Request
        Response response = new Response();
        HttpURLConnection connection = (HttpURLConnection) new URL("http://" + host + ":" + port + "/load").openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoInput(true);
        connection.setDoOutput(true);
//        Write to outputstream on the connection
        DataOutputStream write = new DataOutputStream(connection.getOutputStream());
        write.writeBytes(JsonUtil.serialize(loadRequest));
        write.flush();
        write.close();
//        Close connection and read the response
        InputStreamReader read;
        try {
            read = new InputStreamReader(connection.getInputStream());
        } catch (Exception e) {
            read = new InputStreamReader(connection.getErrorStream());
        }
        BufferedReader br = new BufferedReader(read);
        String text = "";
        String JSONResponse = "";
        while((text = br.readLine()) != null) {
            JSONResponse += text;
        }
//        Get the data for successful/failed operation
        JsonObject jsonObject = new JsonParser().parse(JSONResponse).getAsJsonObject();
        response.setSuccess(jsonObject.get("success").getAsBoolean());
        response.setMessage(jsonObject.get("message").getAsString());
        return response;
    }
}
