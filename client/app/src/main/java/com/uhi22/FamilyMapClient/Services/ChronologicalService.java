/**
 * This class offers functionality for finding chronological order
 * The method here allows for the earliest event to be found
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient.Services;

import com.uhi22.shared.model.Event;

import java.util.ArrayList;

public class ChronologicalService {

    /**
     * This method compares all events in a given ArrayList\<Event\> and finds the earliest one
     * (this allows for sorting to take place/prioritization when building lines or knowing which event to place first)
     * @param personEvents the array list of events to be evaluated
     * @return the earliest event
     */
    public static Event getEarliestEvent(ArrayList<Event> personEvents) {
        Event earliest = new Event();
        int earliestYear = 99999;
        for(Event e : personEvents) {
            if(e.getYear() < earliestYear) {
                earliest = e;
                earliestYear = e.getYear();
            }
        }
        return earliest;
    }
}
