/**
 * This class offers functionality for loading the sample data to the server (which sample data is used during passoff)
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient.Services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.uhi22.familymapserver.Errors.InternalServerError;
import com.uhi22.familymapserver.Util.JsonUtil;
import com.uhi22.shared.Requests.LoadRequest;
import com.uhi22.shared.model.Event;
import com.uhi22.shared.model.Person;
import com.uhi22.shared.model.User;

import java.util.ArrayList;

public class LoadSampleDataService {

    /**
     * This method builds a loadrequest with the local url for the JSON file which contains my family map sample data
     * @return the filled LoadRequest
     */
    public static LoadRequest buildLoadRequest() {
        LoadRequest loadRequest = new LoadRequest();
        JsonObject jsonObject;
        JsonArray jsonArray;
        try {
//            Grab the json data from file
           jsonObject = JsonUtil.getJsonFile("Q:\\github\\FamilyMapClient\\app\\libs\\json\\sampleData.json");
            ArrayList<User> users = new ArrayList<User>();
//            Place and deserialize the user data
            jsonArray = jsonObject.getAsJsonArray("users");
            for(JsonElement j : jsonArray) {
                users.add(JsonUtil.deserialize(j.toString(), User.class));
            }
           loadRequest.setUsers(users.toArray(new User[users.size()]));

//            Place and deserialize the person data
            ArrayList<Person> persons = new ArrayList<Person>();
            jsonArray = jsonObject.getAsJsonArray("persons");
            for(JsonElement j : jsonArray) {
                persons.add(JsonUtil.deserialize(j.toString(), Person.class));
            }
            loadRequest.setPersons(persons.toArray(new Person[persons.size()]));

//            Place and deserialize the event data
            ArrayList<Event> events = new ArrayList<Event>();
            jsonArray = jsonObject.getAsJsonArray("events");
            for(JsonElement j : jsonArray) {
                events.add(JsonUtil.deserialize(j.toString(), Event.class));
            }
            loadRequest.setEvents(events.toArray(new Event[events.size()]));
        } catch (InternalServerError internalServerError) {
            internalServerError.printStackTrace();
        }
        return loadRequest;
    }
}
