/**
 * This class contains all the tests associated with the Person handler on the server
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package passoff;

import DataAccess.AuthTokenDao;
import DataAccess.Database;
import DataAccess.PersonDao;
import DataAccess.UserDao;
import Errors.InternalServerError;
import Model.AuthorizationToken;
import Model.Person;
import Model.User;
import Responses.PersonResponse;
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
import java.net.URLConnection;
import java.sql.Connection;

public class PersonTests {

    private static String personUrl;
    private static Person person;
    private static Person person2;
    private static User user;

    @BeforeEach
    public void setUp() throws Exception {
        try {
            ClearService.clear();
        } catch (InternalServerError internalServerError) {
            internalServerError.printStackTrace();
        }
        personUrl = "http://localhost:8080/person/";
        person = new Person("personID",
                "username",
                "firstName",
                "lastName",
                "m",
                "fatherID",
                "motherID",
                "spouseID");
        person2 = new Person("personID2",
                "username2",
                "firstName",
                "lastName",
                "m",
                "fatherID2",
                "motherID2",
                "spouseID2");
        user = new User("userID",
                "username",
                "password",
                "email",
                "firstName",
                "lastName",
                "m",
                "personID");
        AuthorizationToken token = new AuthorizationToken();
        token.setAuthKey("key");
        token.setUserID("userID");
        Database db = new Database();
        try {
            Connection conn = db.openConnection();
            PersonDao pDao = new PersonDao(conn);
            PersonDao.insertPerson(person);
            PersonDao.insertPerson(person2);
            UserDao uDao = new UserDao(conn);
            UserDao.insertUser(user);
            AuthTokenDao aDao = new AuthTokenDao(conn);
            AuthTokenDao.insertAuthToken("tokenID",token);
            db.closeConnection(true);
        } catch (Exception e) {
            db.closeConnection(false);
            e.printStackTrace();
        }
    }

    /**
     * this test validates that a person can be successfully obtained through the http request
     */
    @Test
    @DisplayName("Get single person request")
    public void getPerson() {
        try {
            URL url = new URL(personUrl + "personID");
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Authorization", "key");
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
            PersonResponse personResponse = JsonUtil.deserialize(response, PersonResponse.class);
            assert personResponse.getPersonID().equals("personID");
            assert personResponse.getAssociatedUsername().equals("username");
            assert personResponse.getFirstName().equals("firstName");
            assert personResponse.getLastName().equals("lastName");
            assert personResponse.getGender().equals("m");
            assert personResponse.getFatherID().equals("fatherID");
            assert personResponse.getMotherID().equals("motherID");
            assert personResponse.getSpouseID().equals("spouseID");
            assert personResponse.getSuccess();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This test verifies that the server returns a bad response when the method is incorrect (POST)
     */
    @Test
    @DisplayName("Get single person bad method")
    public void getPersonBadMethod() {
        try {
            URL url = new URL(personUrl + "personID");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            Change the request method
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "key");
            String response = null;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder result = new StringBuilder();
                String line = null;
                while((line = in.readLine()) != null) {
                    result.append(line);
                    result.append(System.lineSeparator());
                }
                response = result.toString();
//            Assert that the operation failed
                assert response == null;
                assert connection.getResponseCode() == 400;
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This test verifies that users with bad authTokens are unable to get persons from the database
     */
    @Test
    @DisplayName("Get single person bad auth token")
    public void getPersonBadAuth() {
        try {
            URL url = new URL(personUrl + "personID");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
//            Provide a bad authToken
            connection.setRequestProperty("Authorization", "BADKEy");
            String response = null;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder result = new StringBuilder();
                String line = null;
                while((line = in.readLine()) != null) {
                    result.append(line);
                    result.append(System.lineSeparator());
                }
                response = result.toString();
                System.out.println(response);
//            Assert that the operation failed
                assert response == null;
                assert connection.getResponseCode() == 400;
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This test verifies that an error is thrown when the personID doesn't exist in the database
     */
    @Test
    @DisplayName("Get single person bad personID")
    public void getPersonBadID() {
        try {
//            Change the personID to be one that does not exist in the database
            URL url = new URL(personUrl + "BADPERSONID");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "key");
            String response = null;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder result = new StringBuilder();
                String line = null;
                while((line = in.readLine()) != null) {
                    result.append(line);
                    result.append(System.lineSeparator());
                }
                response = result.toString();
                System.out.println(response);
//            Assert that the operation failed
                assert response == null;
                assert connection.getResponseCode() == 400;
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * this verifies that the program doesn't allow users to access persons that they don't own
     */
    @Test
    @DisplayName("Get single person doesn't own")
    public void getPersonNotOwner() {
        try {

//            Change the personID to be one that does not belong to the user
            URL url = new URL(personUrl + "personID2");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "key");
            String response = null;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder result = new StringBuilder();
                String line = null;
                while((line = in.readLine()) != null) {
                    result.append(line);
                    result.append(System.lineSeparator());
                }
                response = result.toString();
                System.out.println(response);
//            Assert that the operation failed
                assert response == null;
                assert connection.getResponseCode() == 400;
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("All Persons success")
    public void allPersonsSuccess() throws IOException {
        String response = null;
        HttpURLConnection connection = null;
        try {
            personUrl = "http://localhost:8080/person";
            URL url = new URL(personUrl);
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
    @DisplayName("All Persons failure (bad auth)")
    public void allPersonsFailure() throws IOException {
        String response = null;
        HttpURLConnection connection = null;
        try {
            personUrl = "http://localhost:8080/person";
            URL url = new URL(personUrl);
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
