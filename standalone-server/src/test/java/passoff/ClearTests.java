/**
 * This class contains tests to verify clear functionality over the network
 */

package passoff;

import DataAccess.Database;
import Errors.InternalServerError;
import Model.Person;
import Service.ClearService;
import Service.FillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;

public class ClearTests {

    private static URL url;
    private static HttpURLConnection connection;

    @BeforeEach
    public void setUp() throws Exception {
        Database db = new Database();
        try {
            ClearService.clear();
            Connection conn = db.openConnection();
            Person person = new Person("personID","username",
                    "first","last",
                    "f","father","mother","spouse");
            FillService.fillHelper(4, person, 2020);
            db.closeConnection(true);
            url = new URL("http://localhost:8080/clear");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
        } catch (Exception | InternalServerError e) {
            db.closeConnection(false);
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Successful clear")
    public void validClear() {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @throws IOException
     */
    @Test
    @DisplayName("invalid clear (bad method)")
    public void invalidClear() throws IOException {
        String response = null;
        connection.setRequestMethod("GET");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder result = new StringBuilder();
            String line = null;
            while ((line = in.readLine()) != null) {
                result.append(line);
                result.append(System.lineSeparator());
            }
            response = result.toString();
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert response == null;
        assert connection.getResponseCode() == 400;
    }
}
