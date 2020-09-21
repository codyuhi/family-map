/**
 * This class holds methods which support the functionality for finding relevant search suggestions based on a user-provided string
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient.Services;

import com.uhi22.shared.model.Event;
import com.uhi22.shared.model.Person;

import java.util.ArrayList;

public class SearchService {

    public static ArrayList<Person> getSuggestedPersons(String searchString, ArrayList<Person> allPersons) {
        ArrayList<Person> suggestedPersons = new ArrayList<Person>();
//        match by firstName, lastName
        for(int i = 0; i < allPersons.size(); i++) {
            boolean found = false;
            Person person = allPersons.get(i);
            for(int j = 0; j < person.getFirstName().length(); j++) {
                if((j + searchString.length()) <= person.getFirstName().length()) {
                    if(person.getFirstName().substring(j, searchString.length() + j).toLowerCase().equals(searchString.toLowerCase())) {
                        suggestedPersons.add(person);
                        found = true;
                        break;
                    }
                }
            }
            if(found) {
                continue;
            }
            for(int j = 0; j < person.getLastName().length(); j++) {
                if((j + searchString.length()) <= person.getLastName().length()) {
                    if(person.getLastName().substring(j, searchString.length() + j).toLowerCase().equals(searchString.toLowerCase())) {
                        suggestedPersons.add(person);
                        break;
                    }
                }
            }
        }
        return suggestedPersons;
    }

    public static ArrayList<Event> getSuggestedEvents(String searchString, ArrayList<Event> allEvents, ArrayList<Person> allPersons,
                                                      ArrayList<String> allFatherSideIDs, ArrayList<String> allMotherSideIDs,
                                                      boolean fatherEnabled, boolean motherEnabled, boolean maleEnabled,
                                                      boolean femaleEnabled) {
        ArrayList<Event> suggestedEvents = new ArrayList<Event>();
//        match by city, country, eventType, year
        for(int i = 0; i < allEvents.size(); i++) {
            boolean found = false;
            Event event = allEvents.get(i);
            for(int j = 0; j < event.getCity().length(); j++) {
                if((j + searchString.length()) <= event.getCity().length()) {
                    if(event.getCity().substring(j, searchString.length() + j).toLowerCase().equals(searchString.toLowerCase())){
                        Person evaluatedPerson = null;
                        for(Person person : allPersons) {
                            if(event.getPersonID().equals(person.getPersonID())) {
                                evaluatedPerson = person;
                            }
                        }
                        if(evaluatedPerson != null && FilterService.searchFailFilter(event, evaluatedPerson, allPersons, allFatherSideIDs, allMotherSideIDs, fatherEnabled, motherEnabled, maleEnabled, femaleEnabled)) {
                            found = true;
                            break;
                        }
                        suggestedEvents.add(event);
                        found = true;
                        break;
                    }
                }
            }
            if(found) {
                continue;
            }
            for(int j = 0; j < event.getCountry().length(); j++) {
                if((j + searchString.length()) <= event.getCountry().length()) {
                    if(event.getCountry().substring(j, searchString.length() + j).toLowerCase().equals(searchString.toLowerCase())){
                        Person evaluatedPerson = null;
                        for(Person person : allPersons) {
                            if(event.getPersonID().equals(person.getPersonID())) {
                                evaluatedPerson = person;
                            }
                        }
                        if(evaluatedPerson != null && FilterService.searchFailFilter(event, evaluatedPerson, allPersons, allFatherSideIDs, allMotherSideIDs, fatherEnabled, motherEnabled, maleEnabled, femaleEnabled)) {
                            found = true;
                            break;
                        }
                        suggestedEvents.add(event);
                        found = true;
                        break;
                    }
                }
            }
            if(found) {
                continue;
            }
            for(int j = 0; j < event.getEventType().length(); j++) {
                if((j + searchString.length()) <= event.getEventType().length()) {
                    if(event.getEventType().substring(j, searchString.length() + j).toLowerCase().equals(searchString.toLowerCase())){
                        Person evaluatedPerson = null;
                        for(Person person : allPersons) {
                            if(event.getPersonID().equals(person.getPersonID())) {
                                evaluatedPerson = person;
                            }
                        }
                        if(evaluatedPerson != null && FilterService.searchFailFilter(event, evaluatedPerson, allPersons, allFatherSideIDs, allMotherSideIDs, fatherEnabled, motherEnabled, maleEnabled, femaleEnabled)) {
                            found = true;
                            break;
                        }
                        suggestedEvents.add(event);
                        found = true;
                        break;
                    }
                }
            }
            if(found) {
                continue;
            }
            for(int j = 0; j < String.valueOf(event.getYear()).length(); j++) {
                if((j + searchString.length()) <= String.valueOf(event.getYear()).length()) {
                    if(String.valueOf(event.getYear()).substring(j, searchString.length() + j).toLowerCase().equals(searchString.toLowerCase())){
                        Person evaluatedPerson = null;
                        for(Person person : allPersons) {
                            if(event.getPersonID().equals(person.getPersonID())) {
                                evaluatedPerson = person;
                            }
                        }
                        if(evaluatedPerson != null && FilterService.searchFailFilter(event, evaluatedPerson, allPersons, allFatherSideIDs, allMotherSideIDs, fatherEnabled, motherEnabled, maleEnabled, femaleEnabled)) {
                            found = true;
                            break;
                        }
                        suggestedEvents.add(event);
                        found = true;
                        break;
                    }
                }
            }
        }
        return suggestedEvents;
    }
}
