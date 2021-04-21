/** The AllEventsResponse class is a child class of the Response class and is used to provide all information
 *  for all events that are associated with all persons that are owned by the current user
 *  The AllEventsResponse class is used whenever the user hits the /event endpoint to get the information
 *  for all events that are associated with all persons that are owned by the current user
 *
 *  The AllEventsResponse is important because it allows for an easily parsable schema that contains
 *  expected attributes for a persons request to be returned to the client
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Responses;

import Model.Event;
import Model.Person;

import java.util.ArrayList;

public class AllEventsResponse extends Response{

    private ArrayList<Event> data;      // the data ArrayList of Person class members will contain
    // all information for all Events associated with all
    // Persons that are owned by the logged in user

    /**
     * Empty constructor marking the class for public use
     */
    public AllEventsResponse() {}

    /**
     * Getter for the data
     * @return  the data for the AllPersonsResponse
     */
    public ArrayList<Event> getData() {
        return data;
    }

    /**
     * Setter for the data
     * @param data  pass in the data and set the data to the parameter
     */
    public void setData(ArrayList<Event> data) {
        this.data = data;
    }
}
