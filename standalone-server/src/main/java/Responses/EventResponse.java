/** The EventResponse class is a child class of the response class and is used to provide all information for an Event class member
 *  The EventResponse class is used whenever the user hits the /event/[eventID] endpoint to get the information associated with a specific Event class member
 *
 *  The EventResponse class is important because it allows for an easily parsable schema that contains
 *  expected attributes for an event request to be returned to the client
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Responses;

public class EventResponse extends Response {

    private String associatedUsername;  // the associatedUsername string is the associated username for the
    // event found in the database with a matching eventID to the request
    private String eventID;             // the eventID string is the associated eventID for the event
    // found in the database with matching eventID to the request
    private String personID;            // the personID string is the associated personID for the event
    // found in the database with matching eventID to the request
    private float latitude;             // the latitude float is the associated latitude for the event
    // found in the database with matching eventID to the request
    private float longitude;            // the longitude float is the associated longitude for the event
    // found in the database with matching eventID to the request
    private String country;             // the country string is the associated country for the event
    // found in the database with matching eventID to the request
    private String city;                // the city string is the associated city for the event
    // found in the database with matching eventID to the request
    private String eventType;           // the eventType string is the associated eventType for the event
    // found in the database with matching eventID to the request
    private int year;                   // the year int is the associated year for the event
    // found in the database with matching eventID to the request

    /**
     * Empty constructor marking the class for public use
     */
    public EventResponse() {}

    /**
     * Getter for the username
     * @return  the associatedUsername for the EventResponse
     */
    public String getAssociatedUsername() {
        return associatedUsername;
    }

    /**
     * Getter for the eventID
     * @return  the eventID for the EventResponse
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Getter for the personID
     * @return  the personID for the EventResponse
     */
    public String getPersonID() {
        return personID;
    }

    /**
     * Getter for the latitude
     * @return  the latitude for the EventResponse
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * Getter for the longitude
     * @return  the longitude for the EventResponse
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * Getter for the country
     * @return  the country for the EventResponse
     */
    public String getCountry() {
        return country;
    }

    /**
     * Getter for the city
     * @return  the city for the EventResponse
     */
    public String getCity() {
        return city;
    }

    /**
     * Getter for the eventType
     * @return  the eventType for the EventResponse
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Getter for the year
     * @return  the year for the EventResponse
     */
    public int getYear() {
        return year;
    }

    /**
     * Setter for the associatedUsername
     * @param associatedUsername    pass in the associatedUsername and set the associatedUsername to the parameter
     */
    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    /**
     * Setter for the eventID
     * @param eventID    pass in the eventID and set the eventID to the parameter
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * Setter for the personID
     * @param personID    pass in the personID and set the personID to the parameter
     */
    public void setPersonID(String personID) {
        this.personID = personID;
    }

    /**
     * Setter for the latitude
     * @param latitude    pass in the latitude and set the latitude to the parameter
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /**
     * Setter for the longitude
     * @param longitude    pass in the longitude and set the longitude to the parameter
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /**
     * Setter for the country
     * @param country    pass in the country and set the country to the parameter
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Setter for the city
     * @param city    pass in the city and set the city to the parameter
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Setter for the eventType
     * @param eventType    pass in the eventType and set the eventType to the parameter
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * Setter for the year
     * @param year    pass in the year and set the year to the parameter
     */
    public void setYear(int year) {
        this.year = year;
    }
}
