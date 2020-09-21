/** The Service class contains methods which are used in different child services
 * This includes methods for getting random first names and last names
 *
 * @author Cody Uhi
 * @version 1.0.0
 */
package com.uhi22.familymapserver.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Service {

    /**
     * getRandomFirstName uses the given gender to randomly select a first name from the first name asset stored at
     * resources/maleNames.txt or resources/femaleNames.txt
     *
     * If the Person is male, randomly select a first name from the male file
     * If the Person is female, randomly select a first name from the female file
     *
     * @param gender contains the gender of the Person so that the proper first name can be generated based on gender
     * @return provides a String containing the randomly selected name based on the gender
     */
    protected static String getRandomFirstName(String gender) {
        if("m".equals(gender)) {
//            If the Person is male, randomly select a first name from the male file
            try(Stream<String> allLines = Files.lines(Paths.get("resources/maleNames.txt"))) {
                return allLines.skip((int) (3901 * Math.random())).findFirst().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("f".equals(gender)){
//            If the Person is female, randomly select a first name from the female file
            try(Stream<String> allLines = Files.lines(Paths.get("resources/femaleNames.txt"))) {
                return allLines.skip((int) (4945 * Math.random())).findFirst().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * getRandomLastName randomly selects a last name from the lastName asset that was found online
     *
     * Randomly retrieve the last name from the resources/lastNames.txt file
     *
     * @return provides a randomly selected last name from the lastNames file
     */
    protected static String getRandomLastName() {
        try(Stream<String> allLines = Files.lines(Paths.get("resources/lastNames.txt"))) {
            return allLines.skip((int) (6298 * Math.random())).findFirst().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
