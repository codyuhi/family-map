/**
 * This test class tests the functionality of the fill handler and service offered by the server
 */

package passoff;

import DataAccess.Database;
import DataAccess.UserDao;
import Errors.InternalServerError;
import Model.User;
import Responses.Response;
import Service.ClearService;
import Util.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.sql.Connection;

public class FillTests {
    private static String url;
    private static HttpURLConnection connection;
    private User user;

    @BeforeEach
    public void setUp() throws Exception {
        Database db = new Database();
        try {
            ClearService.clear();
            url = "http://localhost:8080/fill/";
            user = new User("userID",
                    "username",
                    "password",
                    "email",
                    "firstName",
                    "lastName",
                    "m",
                    "personID");
            Connection conn = db.openConnection();
            UserDao uDao = new UserDao(conn);
            uDao.insertUser(user);
            db.closeConnection(true);
        } catch (Exception | InternalServerError e) {
            db.closeConnection(false);
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Valid fill request")
    public void validFill() throws ProtocolException {
        try {
            URL fillUrl = new URL(url + "username/4");
            connection = (HttpURLConnection) fillUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            String response = null;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder result = new StringBuilder();
                String line = null;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                    result.append(System.lineSeparator());
                }
                response = result.toString();
                System.out.println(response);
                assert response != null;
                Response fillResponse = JsonUtil.deserialize(response,Response.class);
                assert "Successfully added 31 persons and 91 events to the database.".equals(fillResponse.getMessage());
                assert fillResponse.getSuccess();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Failed fill test (invalid generation)")
    public void invalidFill() {
        try {
//            Set the fill generation value to be invalid
            URL fillUrl = new URL(url + "username/INVALIDFILL");
            connection = (HttpURLConnection) fillUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            String response = null;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder result = new StringBuilder();
                String line = null;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                    result.append(System.lineSeparator());
                }
                response = result.toString();
                System.out.println(response);
                assert response == null;
                assert connection.getResponseCode() == 400;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}