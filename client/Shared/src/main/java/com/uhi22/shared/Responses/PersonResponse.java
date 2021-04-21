/** The PersonResponse class is a child class of the Response class and is used to provide all information for a Person class member
 *  The PersonResponse class is used whenever the user hits the /person/[personID] endpoint to get the information associated with a specific Person class member
 *
 *  The PersonResponse class is important because it allows for an easily parsable schema that contains
 *  expected attributes for a person request to be returned to the client
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.shared.Responses;

public class PersonResponse extends Response {

    private String associatedUsername;  // the associatedUsername string is the associated username for the
    // person found in the database with a matching personId to the request
    private String personID;            // the personID string is the associated personID for the person
    // found in the database with matching personId to the request
    private String firstName;           // the firstName string is the associated first name for the person
    // found in the database with matching personId to the request
    private String lastName;            // the lastName string is the associated last name for the person
    // found in the database with matching personId to the request
    private String gender;              // the gender string is the associated gender for the person
    // found in the database with matching personId to the request
    private String fatherID;            // the fatherID string is the associated father id for the person
    // found in the database with matching personId to the request
    private String motherID;            // the motherID string is the associated mother id for the person
    // found in the database with matching personId to the request
    private String spouseID;            // the spouseID string is the associated spouse id for the person
    // found in the database with matching personId to the request

    /**
     * Empty constructor marking the class for public use
     */
    public PersonResponse() {}

    /**
     * Getter for the associatedUsername
     * @return  the associatedUsername for the PersonResponse
     */
    public String getAssociatedUsername() {
        return associatedUsername;
    }

    /**
     * Getter for the personID
     * @return  the personID for the PersonResponse
     */
    public String getPersonID() {
        return personID;
    }

    /**
     * Getter for the firstName
     * @return  the firstName for the PersonResponse
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Getter for the lastName
     * @return  the lastName for the PersonResponse
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Getter for the gender
     * @return  the gender for the PersonResponse
     */
    public String getGender() {
        return gender;
    }

    /**
     * Getter for the fatherID
     * @return  the fatherID for the PersonResponse
     */
    public String getFatherID() {
        return fatherID;
    }

    /**
     * Getter for the motherID
     * @return  the motherID for the PersonResponse
     */
    public String getMotherID() {
        return motherID;
    }

    /**
     * Getter for the spouseID
     * @return  the spouseID for the personResponse
     */
    public String getSpouseID() {
        return spouseID;
    }

    /**
     * Setter for the associatedUsername
     * @param associatedUsername    pass in the associatedUsername and set the associatedUsername to the parameter
     */
    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    /**
     * Setter for the personID
     * @param personID  pass in the personID and set the personId to the parameter
     */
    public void setPersonID(String personID) {
        this.personID = personID;
    }

    /**
     * Setter for the firstName
     * @param firstName pass in the firstName and set the firstName to the parameter
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Setter for the lastName
     * @param lastName  pass in the lastName and set the lastName to the parameter
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Setter for the gender
     * @param gender    pass in the gender and set the gender to the parameter
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Setter for the fatherID
     * @param fatherID  pass in the fatherID and set the fatherID to the parameter
     */
    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }

    /**
     * Setter for the motherID
     * @param motherID  pass in the motherID and set the motherID to the parameter
     */
    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    /**
     * Setter for the spouseID
     * @param spouseID  pass in the spouseID and set the spouseID to the parameter
     */
    public void setSpouseID(String spouseID) {
        this.spouseID = spouseID;
    }
}
