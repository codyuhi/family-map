/**
 * This test class tests the functionality of the login handler and service offered by the server
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package passoff;

import DataAccess.Database;
import DataAccess.UserDao;
import Errors.InternalServerError;
import Model.User;
import Requests.LoginRequest;
import Responses.LoginResponse;
import Service.ClearService;
import Util.JsonUtil;
import Util.RandomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

public class LoginTests {

    private static URL loginUrl;
    private static HttpURLConnection connection;
    private User user;

    @BeforeEach
    public void setUp() throws Exception {
        Database db = new Database();
        try {
            ClearService.clear();
            user = new User("userID", "username",
                    RandomUtil.generateHash("password"), "email",
                    "firstName", "lastName", "f",
                    "personID");
            Connection conn = db.openConnection();
            UserDao uDao = new UserDao(conn);
            UserDao.insertUser(user);
            db.closeConnection(true);

        } catch (InternalServerError internalServerError) {
            db.closeConnection(false);
            internalServerError.getMessage();
        }
        loginUrl = new URL("http://localhost:8080/user/login");
        connection = (HttpURLConnection) loginUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
    }

    /**
     * this is the control method which shows that successful logins can happen
     */
    @Test
    @DisplayName("Valid login")
    public void validLogin() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserName("username");
        loginRequest.setPassword("password");

        try (DataOutputStream writer = new DataOutputStream(connection.getOutputStream())) {
            byte[] bytes = JsonUtil.serialize(loginRequest).getBytes(StandardCharsets.UTF_8);
            writer.write(bytes);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String response = null;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder result = new StringBuilder();
            String line = null;
            while((line = in.readLine()) != null) {
                result.append(line);
                result.append(System.lineSeparator());
            }
            response = result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(response);
        assert response != null;
        LoginResponse loginReponse = JsonUtil.deserialize(response, LoginResponse.class);
        assert "username".equals(loginReponse.getUserName());
        assert loginReponse.getSuccess();
    }

    /**
     * This method checks of make sure that an error response is received when the wrong username/password combo is submitted
     */
    @Test
    @DisplayName("Invalid login")
    public void invalidLogin() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserName("username");
//        Set the password to be the wrong password and verify that a failure happens
        loginRequest.setPassword("WRONG PASSWORD");

        try (DataOutputStream writer = new DataOutputStream(connection.getOutputStream())) {
            byte[] bytes = JsonUtil.serialize(loginRequest).getBytes(StandardCharsets.UTF_8);
            writer.write(bytes);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String response = null;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder result = new StringBuilder();
            String line = null;
            while((line = in.readLine()) != null) {
                result.append(line);
                result.append(System.lineSeparator());
            }
            response = result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(response);
//            Assert that the operation failed
        assert response == null;
    }
}
