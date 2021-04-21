/**
 * This test class tests the functionality of the Register handler and service offered by the server
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package passoff;

import Errors.InternalServerError;
import Model.User;
import Responses.RegisterResponse;
import Service.ClearService;
import Util.JsonUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RegisterRequestTests {

    /**
     * registerUrl contains the url for the register endpoint
     */
    private static URL registerUrl;
    /**
     * the connection static variable contains the connection to the server for all the requests to be used with
     */
    private static HttpURLConnection connection;

    /**
     * setup initializes everything for the tests to work properly
     *
     * Clear the databases so everything tests fresh
     * set the registerURL
     * set the request method to POST for the test
     * Set the request content-type property to application/json
     * Set do output to true
     *
     * @throws Exception
     */
    @BeforeEach
    public void setUp() throws Exception {
        try {
            ClearService.clear();
        } catch (InternalServerError internalServerError) {
            internalServerError.printStackTrace();
        }
        registerUrl = new URL("http://localhost:8080/user/register");
        connection = (HttpURLConnection) registerUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
    }

    /**
     * tearDown closes everything up after the test is finished with its logic
     *
     * disconnect and reconnect after every test
     * @throws Exception
     */
    @AfterEach
    public void tearDown() throws Exception {
        connection.disconnect();
    }

    /**
     * This is the control that verifies that a User can successfully register
     *
     * Assert that all the RegisterResponse attributes are populated and the username is correct
     */
    @Test
    @DisplayName("Valid Registration Request")
    public void validRegister() {
        try (DataOutputStream writer = new DataOutputStream(connection.getOutputStream())) {
            User validUser = new User(
                    "userID",
                    "username",
                    "password",
                    "email@email.com",
                    "firstName",
                    "lastName",
                    "m",
                    "personID"
            );
            byte[] bytes = JsonUtil.serialize(validUser).getBytes(StandardCharsets.UTF_8);
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
//        Assert that all the RegisterResponse attributes are populated and the username is correct
        assert response != null;
        RegisterResponse responseObject = JsonUtil.deserialize(response, RegisterResponse.class);
        assert responseObject.getAuthToken() != null;
        assert responseObject.getUserName() != null;
        assert responseObject.getPersonID() != null;
        assert "username".equals(responseObject.getUserName());
        assert responseObject.getSuccess();
    }

    /**
     * This test asserts that when a duplicate user is attempted to be added to the database,
     * the server fails to do it and returns a 400 error
     * @throws Exception
     */
    @Test
    @DisplayName("Invalid Registration Request (Duplicate)")
    public void invalidRegisterDuplicate() throws Exception {
//        Register the user successfully the first time
        validRegister();
        try {
            registerUrl = new URL("http://localhost:8080/user/register");
            connection = (HttpURLConnection) registerUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Register the same user again
        try (DataOutputStream writer = new DataOutputStream(connection.getOutputStream())) {
            User validUser = new User(
                    "userID",
                    "username",
                    "password",
                    "email@email.com",
                    "firstName",
                    "lastName",
                    "m",
                    "personID"
            );
            byte[] bytes = JsonUtil.serialize(validUser).getBytes(StandardCharsets.UTF_8);
            writer.write(bytes);
            writer.flush();
            assert connection.getErrorStream() == null;
            assert 400 == connection.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This test asserts that if an invalid gender is given (i.e. "ATTACK HELICOPTER"), the server will not register the given user
     */
    @Test
    @DisplayName("Invalid Registration Request (Invalid Gender)")
    public void invalidRegisterGender() {
//        See that the gender is set to the value "ATTACK HELICOPTER"
        try (DataOutputStream writer = new DataOutputStream(connection.getOutputStream())) {
            User validUser = new User(
                    "userID",
                    "username",
                    "password",
                    "email@email.com",
                    "firstName",
                    "lastName",
                    "ATTACK HELICOPTER",
                    "personID"
            );
            byte[] bytes = JsonUtil.serialize(validUser).getBytes(StandardCharsets.UTF_8);
            writer.write(bytes);
            writer.flush();
//            Assert that the operation failed
            assert connection.getErrorStream() == null;
            assert 400 == connection.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This test asserts that the operation fails when the incorrect request method is given (i.e. GET)
     */
    @Test
    @DisplayName("Invalid Registration Request (Incorrect Request Method)")
    public void invalidRegisterMethod() {
        HttpURLConnection connection2 = null;
        try {
            connection2 = (HttpURLConnection) registerUrl.openConnection();
//            Set the request method to GET
            connection2.setRequestMethod("GET");
            DataOutputStream writer = new DataOutputStream(connection2.getOutputStream());
            writer.flush();
            writer.close();
//            Assert that the operation failed
            assert connection2.getErrorStream() == null;
            assert 400 == connection2.getResponseCode();
        } catch (Exception e) {
            connection2.disconnect();
            e.printStackTrace();
        }
    }

    /**
     * This test asserts that an empty request body causes the operation to fail
     */
    @Test
    @DisplayName("Invalid Registration Request (Empty Request Body)")
    public void invalidRegisterBody() {
        try {
            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
//            Set the request body to be an empty string
            byte[] bytes = "".getBytes(StandardCharsets.UTF_8);
            writer.write(bytes);
            writer.flush();
//            Assert that the operation failed
            assert connection.getErrorStream() == null;
            assert 400 == connection.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This test asserts that a the operation fails when required fields are left empty in the request body
     */
    @Test
    @DisplayName("Invalid Registration Request (Missing Request Value)")
    public void invalidRegisterValue() {
        try {
            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
//            Set the required fields to null for the request
            User validUser = new User(
                    null,
                    null,
                    null,
                    null,
                    "firstName",
                    "lastName",
                    null,
                    null
            );
            byte[] bytes = JsonUtil.serialize(validUser).getBytes(StandardCharsets.UTF_8);
            writer.write(bytes);
            writer.flush();
//            Assert that the operation failed
            assert connection.getErrorStream() == null;
            assert 400 == connection.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
