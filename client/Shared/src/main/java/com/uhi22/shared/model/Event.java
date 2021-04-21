/** Event class members represent meaningful things that happen in the lives of Persons
 *  Every Person must have atleast have 1 event in their lives
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.shared.model;

public class Event {
    private String eventID;             // Unique identifier for this event (non-empty string)
    private String associatedUsername;  // User (username) to which this event belongs
    private String personID;            // ID of person to which this event belongs
    private float latitude;             // Latitude of the event's location
    private float longitude;            // Longitude of the event's location
    private String country;             // Country in which event occurred
    private String city;                // City in which event occurred
    private String eventType;           // Type of event (birth, baptism, christening, marriage, death, etc.)
    private int year;                   // Year in which event occurred

    /**
     * Empty constructor marking the class for public use
     */
    public Event() {}

    /** Getter for the eventID value
     *
     * @return the eventID of the Event
     */
    public String getEventID() {
        return eventID;
    }

    /** Getter for the associatedUsername value
     *
     * @return the associatedUsername of the Event
     */
    public String getAssociatedUsername() {
        return associatedUsername;
    }

    /** Getter for the personID value
     *
     * @return the personID of the Event
     */
    public String getPersonID() {
        return personID;
    }

    /** Getter for the latitude value
     *
     * @return the latitude of the Event
     */
    public float getLatitude() {
        return latitude;
    }

    /** Getter for the longitude value
     *
     * @return the longitude of the Event
     */
    public float getLongitude() {
        return longitude;
    }

    /** Getter for the country value
     *
     * @return the country of the Event
     */
    public String getCountry() {
        return country;
    }

    /** Getter for the city value
     *
     * @return the city of the Event
     */
    public String getCity() {
        return city;
    }

    /** Getter for the eventType value
     *
     * @return the eventType of the Event
     */
    public String getEventType() {
        return eventType;
    }

    /** Getter for the year value
     *
     * @return the year of the Event
     */
    public int getYear() {
        return year;
    }

    /** Setter for the eventID value
     *
     * @param eventID pass in the eventID value and set the Event class's eventID to the parameter
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /** Setter for the associatedUsername value
     *
     * @param associatedUsername pass in the associatedUsername value and set the Event class's associatedUsername to the parameter
     */
    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    /** Setter for the personID value
     *
     * @param personID pass in the personID value and set the Event class's personID to the parameter
     */
    public void setPersonID(String personID) {
        this.personID = personID;
    }

    /** Setter for the latitude value
     *
     * @param latitude pass in the latitude value and set the Event class's latitude to the parameter
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /** Setter for the longitude value
     *
     * @param longitude pass in the longitude value and set the Event class's longitude to the parameter
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /** Setter for the country value
     *
     * @param country pass in the country value and set the Event class's country to the parameter
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /** Setter for the city value
     *
     * @param city pass in the city value and set the Event class's city to the parameter
     */
    public void setCity(String city) {
        this.city = city;
    }

    /** Setter for the eventType value
     *
     * @param eventType pass in the eventType value and set the Event class's eventType to the parameter
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /** Setter for the year value
     *
     * @param year pass in the year value and set the Event class's year to the parameter
     */
    public void setYear(int year) {
        this.year = year;
    }
}
