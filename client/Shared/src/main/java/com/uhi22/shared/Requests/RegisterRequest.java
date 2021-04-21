/** The RegisterRequest class contains information that was sent by the client in the request body
 *  It is used in the RegisterService class's register method to provide the necessary data
 *  for the register operation to successfully be executed
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.shared.Requests;

public class RegisterRequest {

    private String userName;    // Unique user name (non-empty string)
    private String password;    // User's password (non-empty string)
    private String email;       // User's email address (non-empty string)
    private String firstName;   // User's first name (non-empty string)
    private String lastName;    // User's last name (non-empty string)
    private String gender;      // User's gender (string: "f" or "m")

    /**
     * Empty constructor marking the class for public use
     */
    public RegisterRequest(String userName, String password, String email, String firstName, String lastName, String gender) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    /** Getter for the username value
     *
     * @return the username of the RegisterRequest
     */
    public String getUserName() {
        return userName;
    }

    /** Getter for the password value
     *
     * @return the password of the RegisterRequest
     */
    public String getPassword() {
        return password;
    }

    /** Getter for the email value
     *
     * @return the email address of the RegisterRequest
     */
    public String getEmail() {
        return email;
    }

    /** Getter for the first name value
     *
     * @return the first name of the RegisterRequest
     */
    public String getFirstName() {
        return firstName;
    }

    /** Getter for the last name value
     *
     * @return the last name of the RegisterRequest
     */
    public String getLastName() {
        return lastName;
    }

    /** Getter for the gender value
     *
     * @return the gender of the RegisterRequest
     */
    public String getGender() {
        return gender;
    }

    /** Setter for the username value
     *
     * @param userName pass in the username value and set the user class's username to the parameter
     */
    public void setUserName(String userName) {
        this.userName = userName;
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
}
