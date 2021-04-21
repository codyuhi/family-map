/**
 * This class tests functionality of the load handler and services supported by the server
 */

package passoff;

import Errors.InternalServerError;
import Responses.Response;
import Service.ClearService;
import Util.JsonUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class LoadTests {
    private static URL loadUrl;
    private static HttpURLConnection connection;

    @BeforeEach
    public void setUp() throws Exception {
        try {
            ClearService.clear();
            loadUrl = new URL("http://localhost:8080/load");
            connection = (HttpURLConnection) loadUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
        } catch (Exception | InternalServerError e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void tearDown() {
        connection.disconnect();
    }

    /**
     * This is the control method which shows that valid load requests work
     */
    @Test
    @DisplayName("Valid load request")
    public void validLoadRequest() {
        String requestString = "{\"users\":[{\"userName\":\"sheila\",\"password\":\"parker\",\"email\":\"sheila@parker.com\",\"firstName\":\"Sheila\",\"lastName\":\"Parker\",\"gender\":\"f\",\"personID\":\"Sheila_Parker\"}],\"persons\":[{\"firstName\":\"Sheila\",\"lastName\":\"Parker\",\"gender\":\"f\",\"personID\":\"Sheila_Parker\",\"fatherID\":\"Patrick_Spencer\",\"motherID\":\"Im_really_good_at_names\",\"associatedUsername\":\"sheila\"},{\"firstName\":\"Patrick\",\"lastName\":\"Spencer\",\"gender\":\"m\",\"personID\":\"Patrick_Spencer\",\"spouseID\":\"Im_really_good_at_names\",\"associatedUsername\":\"sheila\"},{\"firstName\":\"CS240\",\"lastName\":\"JavaRocks\",\"gender\":\"f\",\"personID\":\"Im_really_good_at_names\",\"spouseID\":\"Patrick_Spencer\",\"associatedUsername\":\"sheila\"}],\"events\":[{\"eventType\":\"started family map\",\"personID\":\"Sheila_Parker\",\"city\":\"Salt Lake City\",\"country\":\"United States\",\"latitude\":40.75,\"longitude\":-110.1167,\"year\":2016,\"eventID\":\"Sheila_Family_Map\",\"associatedUsername\":\"sheila\"},{\"eventType\":\"fixed this thing\",\"personID\":\"Patrick_Spencer\",\"city\":\"Provo\",\"country\":\"United States\",\"latitude\":40.2338,\"longitude\":-111.6585,\"year\":2017,\"eventID\":\"I_hate_formatting\",\"associatedUsername\":\"sheila\"}]}";
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bufferedWriter.write(requestString);
            bufferedWriter.close();
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder stringBuilder = new StringBuilder();
            while(scanner.hasNextLine()) {
                stringBuilder.append(scanner.next());
            }
            scanner.close();
            String response = stringBuilder.toString();
            System.out.println(response);
            assert response != null;
            Response loadResponse = JsonUtil.deserialize(response, Response.class);
            assert loadResponse.getSuccess();
            assert loadResponse.getMessage() != null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Empty request body error")
    public void emptyRequestBodyError() {
//        Provide an empty request body
        String requestString = "";
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bufferedWriter.write(requestString);
            bufferedWriter.close();
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder stringBuilder = new StringBuilder();
            while(scanner.hasNextLine()) {
                stringBuilder.append(scanner.next());
            }
            scanner.close();
            String response = stringBuilder.toString();
//            Assert that the operation failed
            assert response == null;
            assert connection.getResponseCode() == 400;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
