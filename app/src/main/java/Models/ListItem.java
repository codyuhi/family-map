package Models;

public class ListItem {
    private String itemType;
    private String personId;
    private String eventId;
    private String authToken;
    private String host;
    private String port;

    private String name;
    private String gender;
    private String relationship;

    private int year;
    private String location;
    private String eventType;

    public ListItem(){
        this.itemType = null;
        this.personId = null;
        this.eventId = null;
        this.authToken = null;
        this.host = null;
        this.port = null;
        this.name = null;
        this.gender = null;
        this.relationship = null;
        this.year = -1;
        this.location = null;
        this.eventType = null;
    }

    public String getItemType() {
        return itemType;
    }

    public String getPersonId() {
        return personId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getRelationship() {
        return relationship;
    }

    public int getYear() {
        return year;
    }

    public String getLocation() {
        return location;
    }

    public String getEventType() {
        return eventType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
