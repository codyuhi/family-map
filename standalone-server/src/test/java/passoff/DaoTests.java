package passoff;

import DataAccess.Database;
import DataAccess.PersonDao;
import DataAccess.UserDao;
import Errors.DataAccessException;
import Model.Person;
import Model.User;
import Util.RandomUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DaoTests {

    private Database db;
    private User user;
    private Person person;

    @BeforeEach
    public void setUp() throws Exception {
        System.out.println("Entered setUp");
        db = new Database();
        user = new User(RandomUtil.generateRandomString(),"username","password",
                "email", "firstName", "lastName",
                "f", "personID");
        person = new Person(RandomUtil.generateRandomString(), "username", "firstName",
                "lastName", "f", RandomUtil.generateRandomString(),
                RandomUtil.generateRandomString(), RandomUtil.generateRandomString());
        db.openConnection();
        db.createTables();
        db.closeConnection(true);

    }

    @AfterEach
    public void tearDown() throws Exception {
        System.out.println("Entered tearDown");
        db.openConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    /**
     * User test 1
     */
    @Test
    public void insertUserPass() throws Exception {
        System.out.println("Testing whether a User can be inserted to the User table . . . ");
        User compareTest = null;
        try {
            Connection conn = db.openConnection();
            UserDao uDao = new UserDao(conn);
            uDao.insertUser(user);
            compareTest = uDao.getUser(user.getUserID());

            db.closeConnection(true);
        } catch(DataAccessException e) {
            System.out.println("Error: " + e.getMessage());
            db.closeConnection(false);
        }
        System.out.println("Checking whether the User was added to the User table and could be retrieved again . . . ");
        assertNotNull(compareTest);
        System.out.println("That worked!\n");
        System.out.println("Checking whether the User that was inserted to the User table changed . . . ");
        assertTrue(user.equals(compareTest));
        System.out.println("The User was not altered! Passed.");
    }

    /**
     * User test 2
     */
    @Test
    public void insertDuplicateUserFail() throws Exception {
        System.out.println("Testing whether the User table rejects a User with a duplicate userID . . . ");
        boolean passed = true;
        try {
            Connection conn = db.openConnection();
            UserDao uDao = new UserDao(conn);
            uDao.insertUser(user);
            uDao.insertUser(user);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
            passed = false;
        }
        System.out.println("Checking whether the User table rejected the invalid User input . . . ");
        assertFalse(passed);
        System.out.println("That worked!");

        User compareTest = user;
        try {
            Connection conn = db.openConnection();
            UserDao uDao = new UserDao(conn);
            compareTest = uDao.getUser(user.getUserID());
            db.closeConnection(true);
        } catch(DataAccessException e) {
            db.closeConnection(false);
        }
        System.out.println("Checking whether the User table rejected the whole database operation above . . . ");
        assertNull(compareTest);
        System.out.println("The User table rejected the whole operation!  Passed.");
    }

    /**
     * User test 3
     */
    @Test
    public void clearUserPass() throws Exception {
        System.out.println("Testing whether the User table can be cleared using the clear method in the UserDao class . . . ");
        ArrayList<User> users = new ArrayList<User>();
        try {
            Connection conn = db.openConnection();
            UserDao uDao = new UserDao(conn);

            for(int i = 0; i < 100; i++) {
                user.setUserID(RandomUtil.generateRandomString());
                user.setPersonID(RandomUtil.generateRandomString());
                user.setEmail("email" + i);
                user.setUsername("username" + i);
                uDao.insertUser(user);
            }
            users = uDao.getUsers();
            System.out.println("There are " + users.size() + " Users in the database after adding a bunch");
            System.out.println("Checking whether the User table has some data in it . . . ");
            assertNotEquals(0, users.size());
            System.out.println("The User table has some data in it!");

            uDao.clearUsers();
            users = uDao.getUsers();
            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
        }
        System.out.println("After clearing the Users table, there are now " + users.size() + " Users in the table.");
        System.out.println("Checking whether the User table has no more data in it . . . ");
        assertEquals(0, users.size());
        System.out.println("There is no more data in the User table!  Passed.");
    }

    /**
     * User test 4
     */
    @Test
    public void getUserPass() throws Exception {
        System.out.println("Testing whether different Users can be retrieved from the User table . . . ");
        User compareTest1 = null;
        User compareTest2 = null;
        User compareTest3 = null;
        try {
            Connection conn = db.openConnection();
            UserDao uDao = new UserDao(conn);

            user.setUserID(RandomUtil.generateRandomString());
            user.setPersonID(RandomUtil.generateRandomString());
            user.setEmail("email1");
            user.setUsername("username1");
            compareTest1 = user;
            uDao.insertUser(user);
            System.out.println("Added a new User to the User table");

            user.setUserID(RandomUtil.generateRandomString());
            user.setPersonID(RandomUtil.generateRandomString());
            user.setEmail("email2");
            user.setUsername("username2");
            compareTest2 = user;
            uDao.insertUser(user);
            System.out.println("Added a new User to the User table");

            user.setUserID(RandomUtil.generateRandomString());
            user.setPersonID(RandomUtil.generateRandomString());
            user.setEmail("email3");
            user.setUsername("username3");
            compareTest3 = user;
            uDao.insertUser(user);
            System.out.println("Added a new User to the User table");

            System.out.println("Checking whether we can retrieve the first added User . . . ");
            assertNotNull(uDao.getUser(compareTest1.getUserID()));
            assertTrue(uDao.getUser(compareTest1.getUserID()).equals(compareTest1));
            System.out.println("The first added user was added successfully to the User table and not altered!");

            System.out.println("Checking whether we can retrieve the second added User . . . ");
            assertNotNull(uDao.getUser(compareTest2.getUserID()));
            assertTrue(uDao.getUser(compareTest2.getUserID()).equals(compareTest1));
            System.out.println("The second added user was added successfully to the User table and not altered!");

            System.out.println("Checking whether we can retrieve the third added User . . . ");
            assertNotNull(uDao.getUser(compareTest3.getUserID()));
            assertTrue(uDao.getUser(compareTest3.getUserID()).equals(compareTest1));
            System.out.println("The third added user was added successfully to the User table and not altered!  Passed.");

            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
        }
    }

    /**
     * User test 5
     */
    @Test
    public void getUserDoesntExist() throws Exception {
        System.out.println("Testing whether a reliable result is returned when a getUser request has no matching User in the User table . . .");
        User compareTest = null;
        try {
            Connection conn = db.openConnection();
            UserDao uDao = new UserDao(conn);
            compareTest = uDao.getUser(user.getUserID());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        System.out.println("Checking whether the retrieval request returned NULL . . .");
        assertNull(compareTest);
        System.out.println("The retrieval request returned NULL! Passed.");
    }

    /**
     * User test 6
     */
    @Test
    public void getAllUsersPass() throws Exception {
        System.out.println("Testing whether the getAllUsers method returns an ArrayList of all Users in the User table . . . ");
        ArrayList<User> users = new ArrayList<User>();
        try {
            Connection conn = db.openConnection();
            UserDao uDao = new UserDao(conn);
            for(int i = 0; i < 300; i++) {
                user.setUserID(RandomUtil.generateRandomString());
                user.setPersonID(RandomUtil.generateRandomString());
                user.setEmail("email" + i);
                user.setUsername("username" + i);
                uDao.insertUser(user);
            }
            users = uDao.getUsers();
            System.out.println("Added " + users.size() + " Users to the User table");
            System.out.println("Checking if all the Users still exist in the User table . . . ");
            for(User u : users) {
                System.out.println("Testing " + u.getUsername());
                assertNotNull(uDao.getUser(u.getUserID()));
            }
            System.out.println("All of the Users still exist in the User table! Passed.");
            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
        }
    }

    /**
     * User test 7
     */
    @Test
    public void getAllUsersEmpty() throws Exception {
        System.out.println("Testing whether the getAllUsers method gracefully returns an empty ArrayList when the User table is empty . . .");
        ArrayList<User> users = new ArrayList<User>();
        users.add(user);
        try {
            Connection conn = db.openConnection();
            UserDao uDao = new UserDao(conn);
            System.out.println("Clearing any existing Users from the Users table . . .");
            uDao.clearUsers();
            users = uDao.getUsers();
            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
        }
        System.out.println("Checking if the getAllUsers method gracefully returned an empty ArrayList with an empty User table . . . ");
        assertTrue(users.isEmpty());
        System.out.println("An empty ArrayList was returned!  Passed.");
    }

    /**
     * User test 8
     * @throws Exception    in case the assertion fails or a dataaccess exception is thrown
     */
    @Test
    public void insertInvalidGender() throws Exception {
        System.out.println("Testing whether the User table rejects an insert request to add a User with an invalid gender string . . . ");
        user.setGender("Attack Helicopter");
        boolean passed = true;
        try {
            Connection conn = db.openConnection();
            UserDao uDao = new UserDao(conn);
            System.out.println("Trying to add a User to the User table with the gender 'Attack Helicopter'");
            uDao.insertUser(user);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
            passed = false;
        }
        System.out.println("Checking if an exception was caught from the above insert operation . . . ");
        assertFalse(passed);
        System.out.println("The above insert operation gracefully failed!");

        User compareTest = user;
        ArrayList<User> users = new ArrayList<User>();
        try {
            Connection conn = db.openConnection();
            UserDao uDao = new UserDao(conn);
            compareTest = uDao.getUser(user.getUserID());
            users = uDao.getUsers();
            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
        }
        System.out.println("Checking to make sure that nothing unusual was added to the User table from the above operation . . . ");
        assertNull(compareTest);
        assertTrue(users.isEmpty());
        System.out.println("Passed.");
    }

    /**
     * Person test 1
     */
    @Test
    public void insertPersonPass() throws Exception {
        System.out.println("Testing whether a Person can be inserted to the Person table . . . ");
        Person compareTest = null;
        try {
            Connection conn = db.openConnection();
            PersonDao pDao = new PersonDao(conn);
            pDao.insertPerson(person);
            compareTest = pDao.getPerson(person.getPersonID());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
        }
        System.out.println("Checking whether the Person was added to the Person table and could be retrieved again . . . ");
        assertNotNull(compareTest);
        System.out.println("That worked!\n");
        System.out.println("Checking whether the Person that was inserted to the Person table changed . . . ");
        assertTrue(person.equals(compareTest));
        System.out.println("The Person was not altered! Passed.");
    }

    /**
     * Person test 2
     */
    @Test
    public void insertDuplicatePersonFail() throws Exception {
        System.out.println("Testing whether the Person table rejects a Person with a duplicate personID . . . ");
        boolean passed = true;
        try {
            Connection conn = db.openConnection();
            PersonDao pDao = new PersonDao(conn);
            pDao.insertPerson(person);
            pDao.insertPerson(person);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
            passed = false;
        }
        System.out.println("Checking whether the Person table rejected the invalid Person input . . . ");
        assertFalse(passed);
        System.out.println("That worked!");

        Person compareTest = person;
        try {
            Connection conn = db.openConnection();
            PersonDao pDao = new PersonDao(conn);
            compareTest = pDao.getPerson(person.getPersonID());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        System.out.println("Checking whether the Person table rejected the whole database operation above . . . ");
        assertNull(compareTest);
        System.out.println("The Person table rejected the whole operation!  Passed.");
    }

    /**
     * Person test 3
     */
    @Test
    public void clearPersonPass() throws Exception {
        System.out.println("Testing whether the Person table can be cleared using the clear method in the PersonDao class . . . ");
        ArrayList<Person> persons = new ArrayList<Person>();
        try {
            Connection conn = db.openConnection();
            PersonDao pDao = new PersonDao(conn);

            for(int i = 0; i < 100; i++) {
                person.setPersonID(RandomUtil.generateRandomString());
                person.setFatherID(RandomUtil.generateRandomString());
                person.setMotherID(RandomUtil.generateRandomString());
                person.setSpouseID(RandomUtil.generateRandomString());
                pDao.insertPerson(person);
//                System.out.println(person.toString());
            }
            persons = pDao.getPersons("username");
            System.out.println("There are " + persons.size() + " Persons in the database after adding a bunch");
            System.out.println("Checking whether the Person table has some data in it . . . ");
            assertNotEquals(0, persons.size());
            System.out.println("The Person table has some data in it!");
            pDao.clearPersons();
            persons = pDao.getPersons(person.getAssociatedUsername());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
        }
        System.out.println("After clearing the Persons table, there are now " + persons.size() + " Persons in the table.");
        System.out.println("Checking whether the Persons table has no more data in it . . . ");
        assertEquals(0, persons.size());
        System.out.println("There is no more data in the Person table!  Passed.");
    }

    /**
     * Person test 4
     */
    @Test
    public void getPersonPass() throws Exception {
        System.out.println("Testing whether different Persons can be retrieved from the Person table . . . ");
        Person compareTest1 = null;
        Person compareTest2 = null;
        Person compareTest3 = null;
        try {
            Connection conn = db.openConnection();
            PersonDao pDao = new PersonDao(conn);

            person.setPersonID(RandomUtil.generateRandomString());
            person.setFatherID(RandomUtil.generateRandomString());
            person.setMotherID(RandomUtil.generateRandomString());
            person.setSpouseID(RandomUtil.generateRandomString());
            person.setAssociatedUsername("username1");
            compareTest1 = person;
            pDao.insertPerson(person);
            System.out.println("Added a new Person to the Person table");

            person.setPersonID(RandomUtil.generateRandomString());
            person.setFatherID(RandomUtil.generateRandomString());
            person.setMotherID(RandomUtil.generateRandomString());
            person.setSpouseID(RandomUtil.generateRandomString());
            person.setAssociatedUsername("username2");
            compareTest2 = person;
            pDao.insertPerson(person);
            System.out.println("Added a new Person to the Person table");

            person.setPersonID(RandomUtil.generateRandomString());
            person.setFatherID(RandomUtil.generateRandomString());
            person.setMotherID(RandomUtil.generateRandomString());
            person.setSpouseID(RandomUtil.generateRandomString());
            person.setAssociatedUsername("username3");
            compareTest3 = person;
            pDao.insertPerson(person);
            System.out.println("Added a new Person to the Person table");

            System.out.println("Checking whether we can retrieve the first added Person . . . ");
            assertNotNull(pDao.getPerson(compareTest1.getPersonID()));
            assertTrue(pDao.getPerson(compareTest1.getPersonID()).equals(compareTest1));
            System.out.println("The first added Person was added successfully to the Person table and not altered!");

            System.out.println("Checking whether we can retrieve the second added Person . . . ");
            assertNotNull(pDao.getPerson(compareTest2.getPersonID()));
            assertTrue(pDao.getPerson(compareTest2.getPersonID()).equals(compareTest2));
            System.out.println("The second added Person was added successfully to the Person table and not altered!");
            
            System.out.println("Checking whether we can retrieve the third added Person . . . ");
            assertNotNull(pDao.getPerson(compareTest3.getPersonID()));
            assertTrue(pDao.getPerson(compareTest3.getPersonID()).equals(compareTest3));
            System.out.println("The third added Person was added successfully to the Person table and not altered!");

            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
        }
    }

    /**
     * Person test 5
     */
    @Test
    public void getPersonDoesntExist() throws Exception {
        System.out.println("Testing whether a reliable result is returned when a getPerson request has no matching Person in the User table . . .");
        Person compareTest = null;
        try {
            Connection conn = db.openConnection();
            PersonDao pDao = new PersonDao(conn);
            compareTest = pDao.getPerson(person.getPersonID());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        System.out.println("Checking whether the retrieval request returned NULL . . .");
        assertNull(compareTest);
        System.out.println("The retrieval request returned NULL! Passed.");
    }

    /**
     * Person test 6
     */
    @Test
    public void getAllPersonsPass() throws Exception {
        System.out.println("Testing whether the getAllPersons method returns an ArrayList of all Persons in the Person table . . . ");
        ArrayList<Person> persons = new ArrayList<Person>();
        try {
            Connection conn = db.openConnection();
            PersonDao pDao = new PersonDao(conn);
            for(int i = 0; i < 300; i++) {
                person.setPersonID(RandomUtil.generateRandomString());
                person.setFatherID(RandomUtil.generateRandomString());
                person.setMotherID(RandomUtil.generateRandomString());
                person.setSpouseID(RandomUtil.generateRandomString());
                person.setAssociatedUsername("username" + i);
                pDao.insertPerson(person);
            }
            persons = pDao.getPersons("username");
            System.out.println("Added " + persons.size() + " Persons to the Person table");
            System.out.println("Checking if all the Persons still exist in the Person table . . . ");
            for(Person p : persons) {
                System.out.println("Testing " + p.getAssociatedUsername());
                assertNotNull(pDao.getPerson(p.getPersonID()));
            }
            System.out.println("All of the Persons still exist in the Person table! Passed.");
            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
        }
    }

    /**
     * Person test 7
     */
    @Test
    public void getAllPersonsEmpty() throws Exception {
        System.out.println("Testing whether the getAllPersons method gracefully returns an empty ArrayList when the Person table is empty . . .");
        ArrayList<Person> persons = new ArrayList<Person>();
        persons.add(person);
        try {
            Connection conn = db.openConnection();
            PersonDao pDao = new PersonDao(conn);
            System.out.println("Clearing any existing Persons from the Persons table . . .");
            pDao.clearPersons();
            persons = pDao.getPersons("username");
            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
        }
        System.out.println("Checking if the getAllPersons method gracefully returned an empty ArrayList with an empty Persons table . . . ");
        assertTrue(persons.isEmpty());
        System.out.println("An empty ArrayList was returned!  Passed.");
    }

    @Test
    public void insertInvalidGenderPerson() throws Exception {
        System.out.println("Testing whether the Person table rejects an insert request to add a Person with an invalid gender");
        person.setGender("Attack Helicopter");
        boolean passed = true;
        try {
            Connection conn = db.openConnection();
            PersonDao pDao = new PersonDao(conn);
            System.out.println("Trying to add a Person to the Person table with the gender 'Attack Helicopter'");
            pDao.insertPerson(person);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
            passed = false;
        }
        System.out.println("Checking if an exception was caught from the above insert operation . . .");
        assertFalse(passed);
        System.out.println("The above insert operation gracefully failed!");

        Person compareTest = person;
        ArrayList<Person> persons = new ArrayList<Person>();
        try {
            Connection conn = db.openConnection();
            PersonDao pDao = new PersonDao(conn);
            compareTest = pDao.getPerson(person.getPersonID());
            persons = pDao.getPersons(person.getAssociatedUsername());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
        }
        System.out.println("Checking to make sure that nothing unusual was added to the Person table from the above operation . . .");
        assertNull(compareTest);
        assertTrue(persons.isEmpty());
        System.out.println("Passed.");
    }
}
