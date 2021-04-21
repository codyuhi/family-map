/** The RandomUtil class allows for a publicly accessible group of methods that generate random strings and hashes
 *
 * @author Cody Uhi
 * @version 1.0.0
 */

package Util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class RandomUtil {

    /** The generateRandomString creates a random string for use as a UUID
     *
     * @return  Returns a randomly generated UUID string
     */
    public static String generateRandomString() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }

    /**
     * generateHash allows the password to be stored in the database, but not in plaintext
     *
     * @param password contains the password that is to be passed through the one-way hashing algorithm
     * @return provides the hashed version of the password
     */
    public static String generateHash(String password) {
//        Follow the hashing algorithm to provide a secure, repeatable hash
        String hash = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(password.getBytes());
            byte[] bytes = messageDigest.digest();
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0; i < bytes.length; i++) {
                stringBuilder.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hash = stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }
}
