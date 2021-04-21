package passoff;

import DataAccess.*;
import Errors.InternalServerError;
import Model.AuthorizationToken;
import Model.Event;
import Model.Person;
import Model.User;
import Responses.AllEventsResponse;
import Responses.EventResponse;
import Service.ClearService;
import Util.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;

public class EventTests {

    private String eventUrl;
    private static Event event;
    private static User user;

    @BeforeEach
    public void setUp() throws Exception {
        try {
            ClearService.clear();
        } catch (InternalServerError internalServerError) {
            internalServerError.printStackTrace();
        }
        eventUrl = "http://localhost:8080/event/";
        user = new User("userID",
                "username",
                "password",
                "email",
                "firstName",
                "lastName",
                "m",
                "personID");
        event = new Event();
        event.setEventID("eventID");
        event.setPersonID("personID");
        event.setAssociatedUsername("username");
        AuthorizationToken token = new AuthorizationToken();
        token.setAuthKey("key");
        token.setUserID("userID");
        Database db = new Database();
        try {
            Connection conn = db.openConnection();
            UserDao uDao = new UserDao(conn);
            UserDao.insertUser(user);
            EventDao eDao = new EventDao(conn);
            eDao.insertEvent(event);
            AuthTokenDao aDao = new AuthTokenDao(conn);
            AuthTokenDao.insertAuthToken("tokenID",token);
            db.closeConnection(true);
        } catch (Exception e) {
            db.closeConnection(false);
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Get valid single event")
    public void getValidEvent() throws IOException {

        String response = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(eventUrl + "eventID");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "key");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder result = new StringBuilder();
                String line = null;
                while((line = in.readLine()) != null) {
                    result.append(line);
                    result.append(System.lineSeparator());
                }
                response = result.toString();
                System.out.println(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert response != null;
        assert connection.getResponseCode() == 200;
        EventResponse eventResponse = JsonUtil.deserialize(response, EventResponse.class);
        assert eventResponse.getSuccess();
        assert eventResponse.getEventID().equals("eventID");
        assert eventResponse.getAssociatedUsername().equals("username");
        assert eventResponse.getPersonID().equals("personID");
    }

    /**
     * This test verifies that users with bad authTokens are unable to get events from the database
     */
    @Test
    @DisplayName("Get single event bad auth token")
    public void getEventBadAuth() throws IOException {
        String response = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(eventUrl + "eventID");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
//            Provide a bad authToken
            connection.setRequestProperty("Authorization", "BADKEy");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder result = new StringBuilder();
                String line = null;
                while((line = in.readLine()) != null) {
                    result.append(line);
                    result.append(System.lineSeparator());
                }
                response = result.toString();
                System.out.println(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
//            Assert that the operation failed
        assert response == null;
        assert connection.getResponseCode() == 400;
    }

    @Test
    @DisplayName("All Events success")
    public void allEventsSuccess() throws IOException {
        String response = null;
        HttpURLConnection connection = null;
        try {
            eventUrl = "http://localhost:8080/event";
            URL url = new URL(eventUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "key");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder result = new StringBuilder();
                String line = null;
                while((line = in.readLine()) != null) {
                    result.append(line);
                    result.append(System.lineSeparator());
                }
                response = result.toString();
                System.out.println(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert response != null;
        assert connection.getResponseCode() == 200;
    }

    @Test
    @DisplayName("All Events failure (bad auth)")
    public void allEventsFailure() throws IOException {
        String response = null;
        HttpURLConnection connection = null;
        try {
            eventUrl = "http://localhost:8080/event";
            URL url = new URL(eventUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "BADKEY");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder result = new StringBuilder();
                String line = null;
                while((line = in.readLine()) != null) {
                    result.append(line);
                    result.append(System.lineSeparator());
                }
                response = result.toString();
                System.out.println(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert response == null;
        assert connection.getResponseCode() == 400;
    }
}
