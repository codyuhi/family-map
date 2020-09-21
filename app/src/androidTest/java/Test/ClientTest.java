package com.uhi22.FamilyMapClient.;

import android.content.Context;

import com.uhi22.FamilyMapClient.NetworkServices.RegisterService;
import com.uhi22.shared.Requests.RegisterRequest;
import com.uhi22.shared.Responses.RegisterResponse;
import com.uhi22.shared.model.User;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class ClientTest {

    private User simUser;
    private Context context;
    private String authToken;
    private String rootPersonId;

    public boolean initialize() throws ExecutionException, InterruptedException {
        RegisterRequest registerRequest = new RegisterRequest("test","test","test","test","test","test");
        RegisterService registerService = new RegisterService(null, "http://192.168.1.193", 8080);
        RegisterResponse registerResponse = registerService.executeRequest(registerRequest);
        if(registerResponse.getSuccess()) {
            authToken = registerResponse.getAuthToken();
            rootPersonId = registerResponse.getPersonID();
            return true;
        }
        return false;
    }

    @Test
    public void testRegistration() {
        try {
            assertTrue(initialize());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
