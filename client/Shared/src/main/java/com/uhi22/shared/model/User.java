/** In order to use Family Map, one must first create a user account.
 *  The server stores information about each user account in its database
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.shared.model;

public class User {

    private String userID;
    private String userName; // Unique user name (non-empty string)
    private String password; // User's password (non-empty string)
    private String email;    // User's email address (non-empty string)
    private String firstName;// User's first name (non-empty string)
    private String lastName; // User's last name (non-empty string)
    private String gender;   // User's gender (string: "f" or "m")
    private String personID; // Unique person ID assigned to this user's generated Person object

    /**
     * Empty constructor marking the class for public use
     */
    public User(String userID, String userName, String password, String email, String firstName, String lastName, String gender, String personID) {
        this.userID = userID;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.personID= personID;
    }

    /** Getter for the userID value
     *
     * @return the userID of the user
     */
    public String getUserID() {
        return userID;
    }

    /** Getter for the username value
     *
     * @return the username of the user
     */
    public String getUsername() {
        return userName;
    }

    /** Getter for the password value
     *
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }

    /** Getter for the email value
     *
     * @return the email address of the user
     */
    public String getEmail() {
        return email;
    }

    /** Getter for the first name value
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /** Getter for the last name value
     *
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /** Getter for the gender value
     *
     * @return the gender of the user
     */
    public String getGender() {
        return gender;
    }

    /** Getter for the personID value
     *
     * @return the personID of the user
     */
    public String getPersonID() { return personID; }

    /** Setter for the userID value
     *
     * @param userID pass in the userID value and set the user class's userID to the parameter
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /** Setter for the username value
     *
     * @param username pass in the username value and set the user class's username to the parameter
     */
    public void setUsername(String username) {
        this.userName = username;
    }

    /** Setter for the password value
     *
     * @param password pass in the password value and set the user class's password to the parameter
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /** Setter for the email value
     *
     * @param email pass in the email value and set the user class's email to the parameter
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /** Setter for the first name value
     *
     * @param firstName pass in the firstname value and set the user class's firstname to the parameter
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /** Setter for the last name value
     *
     * @param lastName pass in the lastname value and set the user class's lastname to the parameter
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /** Setter for the gender value
     *
     * @param gender pass in the gender value and set the user class's gender to the parameter
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /** Setter for the personID
     *
     * @param personID pass in the personID value and set the user class's personID to the parameter
     */
    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public boolean equals(User otherUser) {
        if(!this.userID.equals(otherUser.userID)) {
            return false;
        } else if(!this.userName.equals(otherUser.userName)) {
            return false;
        } else if(!this.password.equals(otherUser.password)) {
            return false;
        } else if(!this.email.equals(otherUser.email)) {
            return false;
        } else if(!this.firstName.equals(otherUser.firstName)) {
            return false;
        } else if(!this.lastName.equals(otherUser.lastName)) {
            return false;
        } else return this.personID.equals(otherUser.personID);
    }

    public String toString() {
        return "User ID: " + this.userID +
                "\nUsername: " + this.userName +
                "\nPassword: " + this.password +
                "\nEmail: " + this.email +
                "\nFirst Name: " + this.firstName +
                "\nLast Name: " + this.lastName +
                "\nPerson ID: " + this.personID;
    }
}
