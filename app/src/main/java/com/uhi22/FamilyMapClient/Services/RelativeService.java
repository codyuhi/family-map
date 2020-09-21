/**
 * This class offers functionality for getting information that is vital to determining family relationships
 * and ownership over events
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient.Services;

import com.uhi22.shared.model.Person;

import java.util.ArrayList;

public class RelativeService {

    /**
     * This method gets the paternal events for the paternal ancestors of the root user
     * @param rootPerson the root user's person POJO
     * @param allPersons all persons being evaluated
     * @return all the paternal ancestor personIDs
     */
    public static ArrayList<String> getFatherIdEvents(Person rootPerson, ArrayList<Person> allPersons) {
        Person father;
        ArrayList<String> allFatherSideIDs = new ArrayList<String>();
        if(rootPerson.getFatherID() != null) {
            allFatherSideIDs.add(rootPerson.getFatherID());
            for(Person person : allPersons) {
                if(person.getPersonID().equals(rootPerson.getFatherID())) {
                    father = person;
                    allFatherSideIDs = fillPersonHelper(father, allFatherSideIDs, allPersons);
                    break;
                }
            }
        }
        return allFatherSideIDs;
    }

    /**
     * This method gets the maternal events for the maternal ancestors of the root user
     * @param rootPerson the root user's person POJO
     * @param allPersons all persons being evaluated
     * @return all the maternal ancestor personIDs
     */
    public static ArrayList<String> getMotherEventIDs(Person rootPerson, ArrayList<Person> allPersons) {
        Person mother;
        ArrayList<String> allMotherSideIDs = new ArrayList<String>();
        if(rootPerson.getMotherID() != null) {
            allMotherSideIDs.add(rootPerson.getMotherID());
            for(Person person : allPersons) {
                if(person.getPersonID().equals(rootPerson.getMotherID())) {
                    mother = person;
                    allMotherSideIDs = fillPersonHelper(mother, allMotherSideIDs, allPersons);
                    break;
                }
            }
        }
        return allMotherSideIDs;
    }

    /**
     * This recursive method supports iterating through the ancestors of the root user both on the paternal and maternal sides
     *
     * @param person person being evaluated
     * @param allPersonIDs the personIds that have been found recursively
     * @param allPersons all persons being evaluated
     * @return the personIds that have been found recursively
     */
    private static ArrayList<String> fillPersonHelper(Person person, ArrayList<String> allPersonIDs, ArrayList<Person> allPersons) {
        if(person.getFatherID() != null) {
            allPersonIDs.add(person.getFatherID());
        }
        if(person.getMotherID() != null) {
            allPersonIDs.add(person.getMotherID());
        }
        for(Person p : allPersons) {
            if(person.getFatherID() != null && p.getPersonID().equals(person.getFatherID())) {
                fillPersonHelper(p, allPersonIDs, allPersons);
            } else if(person.getMotherID() != null && p.getPersonID().equals(person.getMotherID())) {
                fillPersonHelper(p, allPersonIDs, allPersons);
            }
        }
        return allPersonIDs;
    }
}
