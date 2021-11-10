/** The JsonUtil class allows for a publicly accessible group of methods that assist in working with json data
 *  It allows for serialization and deserialization of json objects and POJOs
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package com.uhi22.shared.Util;

import com.uhi22.shared.Errors.InternalServerError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;

public class JsonUtil {

    /**
     * Empty constructor
     */
    public JsonUtil() {}

    /** The deserialize method deserializes the json object into a class member
     *  This allows for easier integration of json data into POJOs
     *
     * @param value         The value parameter contains the stringified json data which is to be deserialized into a POJO
     * @param returnType    The returnType parameter contains the POJO class entity which the json data will be deserialized into
     * @param <T>           See returnType definition
     * @return              The returned value is a class member of the given POJO class type
     */
    public static <T> T deserialize (String value, Class<T> returnType) {
        return (new Gson()).fromJson(value,returnType);
    }

    /**
     * serialize takes the given Object and Stringifies it into a String
     *
     * @param given contains an object to be Stringified
     * @return provides the Stringified version of the given Object
     */
    public static String serialize(Object given) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(given);
    }

    /**
     * getJsonFile returns a JsonObject for the file contents found at the given file path
     *
     * @param path contains the path to the file that should be parsed into a Json object
     * @return provides the JsonObject which is defined by the file at the given file path
     * @throws InternalServerError occurs when there's an issue reading the file or parsing it into a JsonObject
     */
    public static JsonObject getJsonFile(String path) throws InternalServerError {
        JsonParser jsonParser = new JsonParser();
        try {
            JsonObject jsonObject = (JsonObject) jsonParser.parse(new FileReader(path));
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerError();
        }
    }
}
