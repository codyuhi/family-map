/** The AllPersonsResponse class is a child class of the Response class and is used to provide all information for an array of Person class members
 *  The AllPersonsResponse class is used whenever the user hits the /person endpoint to get the information associated with all persons owned by the current user
 *
 *  The ALlPersonsResponse class is important because it allows for an easily parsable schema that contains
 *  expected attributes for a persons request to be returned to the client
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Responses;

import Model.Person;

import java.util.ArrayList;

public class AllPersonsResponse extends Response{

    private ArrayList<Person> data;     // the data ArrayList of Person class members will contain
    // all information for all Persons that are owned by the logged in user

    /**
     * Empty constructor marking the class for public use
     */
    public AllPersonsResponse() {}

    /**
     * Getter for the data
     * @return  the data for the AllPersonsResponse
     */
    public ArrayList<Person> getData() {
        return data;
    }

    /**
     * Setter for the data
     * @param data  pass in the data and set the data to the parameter
     */
    public void setData(ArrayList<Person> data) {
        this.data = data;
    }
}
