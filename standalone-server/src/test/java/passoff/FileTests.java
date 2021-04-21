package passoff;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.*;
import java.nio.Buffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileTests {

    private String urlString;

    @BeforeEach
    public void setUp() {
        try {
            urlString = "http://localhost:8080/";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("valid Index file gotten")
    public void validFile() {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            String response = null;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder result = new StringBuilder();
                String line = null;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                    result.append(System.lineSeparator());
                }
                response = result.toString();
                assert response != null;
                assert connection.getResponseCode() == 200;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        @Test
    @DisplayName("Invalid file path")
    public void invalidFile() throws IOException {
            String response = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urlString+"INVALIDFILEPATH");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder result = new StringBuilder();
                    String line = null;
                    while ((line = in.readLine()) != null) {
                        result.append(line);
                        result.append(System.lineSeparator());
                    }
                    response = result.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert response == null;
            assert connection.getResponseCode() == 404;
    }
}
