/** Person class members represent humans who have events in their lives and have relationships with other Persons
 *  A user must be associated with atleast one person
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Model;

public class Person {
    private String personID;            // Unique identifier for this person (non-empty string)
    private String associatedUsername;  // User (username) to which this person belongs
    private String firstName;           // Person's first name (non-empty string)
    private String lastName;            // Person's last name (non-empty string)
    private String gender;              // Person's gender (string: "f" or "m")
    private String fatherID;            // Person ID of person's father (possibly null)
    private String motherID;            // Person ID of person's mother (possibly null)
    private String spouseID;            // Person ID of person's spouse (possibly null)

    /**
     * Empty constructor marking the class for public use
     */
    public Person(String personID, String associatedUsername, String firstName,
                    String lastName, String gender, String fatherID,
                    String motherID, String spouseID) {
        this.personID = personID;
        this.associatedUsername = associatedUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
    }

    /** Getter for the personID value
     *
     * @return the personID of the Person
     */
    public String getPersonID() {
        return personID;
    }

    /** Getter for the associatedUsername value
     *
     * @return the associatedUsername of the Person
     */
    public String getAssociatedUsername() {
        return associatedUsername;
    }

    /** Getter for the firstName value
     *
     * @return the firstName of the Person
     */
    public String getFirstName() {
        return firstName;
    }

    /** Getter for the lastName value
     *
     * @return the lastName of the Person
     */
    public String getLastName() {
        return lastName;
    }

    /** Getter for the gender value
     *
     * @return the gender of the Person
     */
    public String getGender() {
        return gender;
    }

    /** Getter for the fatherID value
     *
     * @return the fatherID of the Person
     */
    public String getFatherID() {
        return fatherID;
    }

    /** Getter for the motherID value
     *
     * @return the motherID of the Person
     */
    public String getMotherID() {
        return motherID;
    }

    /** Getter for the spouseID value
     *
     * @return the spouseID of the Person
     */
    public String getSpouseID() {
        return spouseID;
    }

    /** Setter for the personID value
     *
     * @param personID pass in the personID value and set the Person class's personID to the parameter
     */
    public void setPersonID(String personID) {
        this.personID = personID;
    }

    /** Setter for the associatedUsername value
     *
     * @param associatedUsername pass in the associatedUsername value and set the Person class's associatedUsername to the parameter
     */
    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    /** Setter for the firstName value
     *
     * @param firstName pass in the firstName value and set the Person class's firstName to the parameter
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /** Setter for the lastName value
     *
     * @param lastName pass in the lastName value and set the Person class's lastName to the parameter
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /** Setter for the gender value
     *
     * @param gender pass in the gender value and set the Person class's gender to the parameter
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /** Setter for the fatherID value
     *
     * @param fatherID pass in the fatherID value and set the Person class's fatherID to the parameter
     */
    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }

    /** Setter for the motherID value
     *
     * @param motherID pass in the motherID value and set the Person class's motherID to the parameter
     */
    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    /** Setter for the spouseID value
     *
     * @param spouseID pass in the spouseID value and set the Person class's spouseID to the parameter
     */
    public void setSpouseID(String spouseID) {
        this.spouseID = spouseID;
    }

    public boolean equals(Person otherPerson) {
        if(!this.personID.equals(otherPerson.personID)) {
            return false;
        } else if(!this.associatedUsername.equals(otherPerson.associatedUsername)) {
            return false;
        } else if(!this.firstName.equals(otherPerson.firstName)) {
            return false;
        } else if(!this.lastName.equals(otherPerson.lastName)) {
            return false;
        } else if(!this.gender.equals(otherPerson.gender)) {
            return false;
        } else if(!this.fatherID.equals(otherPerson.fatherID)) {
            return false;
        } else if(!this.motherID.equals(otherPerson.motherID)) {
            return false;
        } else return this.spouseID.equals(otherPerson.spouseID);
    }

    public String toString() {
        return "Person ID: " + this.personID +
                "\nUsername: " + this.associatedUsername +
                "\nFirst Name: " + this.firstName +
                "\nLast Name: " + this.lastName +
                "\nGender: " + this.gender +
                "\nFather ID: " + this.fatherID +
                "\nMother ID: " + this.motherID +
                "\nSpouse ID: " +this.spouseID;
    }
}
