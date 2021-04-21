/** The LoadRequest class contains information that was sent by the client in the request body
 *  It is used in the LoadService class's load method to provide the necessary data
 *  for the load operation to successfully be executed
 * @author Cody Uhi
 * @version 1.0.0
 */

package Requests;

import Model.Event;
import Model.Person;
import Model.User;

public class LoadRequest {

    private User[] users;       // an array of User class members
    private Person[] persons;   // an array of Person class members
    private Event[] events;     // an array of Event class members

    /**
     * Empty constructor marking the class for public use
     */
    public LoadRequest() {}

    /**
     * Getter for the users array
     * @return  the users array for the LoadRequest
     */
    public User[] getUsers() {
        return users;
    }

    /**
     * Getter for the persons array
     * @return  the persons array for the LoadRequest
     */
    public Person[] getPersons() {
        return persons;
    }

    /**
     * Getter for the events array
     * @return  the events array for the LoadRequest
     */
    public Event[] getEvents() {
        return events;
    }

    /** Setter for the users array
     *
     * @param users pass in the users value and set the LoadRequest class's users array to the parameter
     */
    public void setUsers(User[] users) {
        this.users = users;
    }

    /** Setter for the persons array
     *
     * @param persons pass in the persons value and set the LoadRequest class's persons array to the parameter
     */
    public void setPersons(Person[] persons) {
        this.persons = persons;
    }

    /** Setter for the events array
     *
     * @param events pass in the events value and set the LoadRequest class's events array to the parameter
     */
    public void setEvents(Event[] events) {
        this.events = events;
    }
}
