/**
 * This class offers functionality for finding whether a given event passes filter requirements or not
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient.Services;

import com.uhi22.shared.model.Event;
import com.uhi22.shared.model.Person;

import java.util.ArrayList;

public class FilterService {

    /**
     * This boolean method takes in an event, all persons, ids for paternal and maternal sides, and filter settings
     * Those things are combined to determine whether the item passes the filtering requirements
     * @param event the given event
     * @param allPersons the list of all persons
     * @param allFatherSideIDs the list of all the paternal person ids
     * @param allMotherSideIDs the list of all the maternal person ids
     * @param fatherEnabled filter boolean
     * @param motherEnabled filter boolean
     * @param maleEnabled filter boolean
     * @param femaleEnabled filter boolean
     * @return true if the filter was failed
     */
    public static boolean failFilter(Event event, ArrayList<Person> allPersons, ArrayList<String> allFatherSideIDs,
                                     ArrayList<String> allMotherSideIDs, boolean fatherEnabled, boolean motherEnabled,
                                     boolean maleEnabled, boolean femaleEnabled) {
//        Check if filter requirements are failed
        if(!fatherEnabled) {
            for(String s : allFatherSideIDs) {
                if(event.getPersonID().equals(s)) {
                    return true;
                }
            }
        }
        if(!motherEnabled) {
            for(String s : allMotherSideIDs) {
                if(event.getPersonID().equals(s)) {
                    return true;
                }
            }
        }
        Person evaluatedPerson;
        for(Person person : allPersons) {
            if(event.getPersonID().equals(person.getPersonID())) {
                evaluatedPerson = person;
                if(!maleEnabled && evaluatedPerson.getGender().equals("m")) {
                    return true;
                } else if(!femaleEnabled && evaluatedPerson.getGender().equals("f")) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    /**
     * This method takes an additional parameter to evaluate against an active person
     * The purpose of this method is the same though: determine whether or not the given data passes filtering requirements
     * @param event the given event
     * @param activePerson the given person
     * @param allPersons the list of all persons
     * @param allFatherSideIDs the list of all the paternal person ids
     * @param allMotherSideIDs the list of all the maternal person ids
     * @param fatherEnabled filter boolean
     * @param motherEnabled filter boolean
     * @param maleEnabled filter boolean
     * @param femaleEnabled filter boolean
     * @return true if the filter was failed
     */
    public static boolean searchFailFilter(Event event, Person activePerson, ArrayList<Person> allPersons,
                                           ArrayList<String> allFatherSideIDs, ArrayList<String> allMotherSideIDs,
                                           boolean fatherEnabled, boolean motherEnabled, boolean maleEnabled,
                                           boolean femaleEnabled) {
        if(activePerson != null) {
            if(!fatherEnabled) {
                for(String s : allFatherSideIDs) {
                    if(event.getPersonID().equals(s)) {
                        return true;
                    }
                }
            }
            if(!motherEnabled) {
                for(String s : allMotherSideIDs) {
                    if(event.getPersonID().equals(s)) {
                        return true;
                    }
                }
            }
        }
        Person evaluatedPerson;
        for(Person person : allPersons) {
            if(event.getPersonID().equals(person.getPersonID())) {
                evaluatedPerson = person;
                if(!maleEnabled && evaluatedPerson.getGender().equals("m")) {
                    return true;
                } else if(!femaleEnabled && evaluatedPerson.getGender().equals("f")) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
}
