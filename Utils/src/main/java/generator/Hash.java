package generator;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

    static MessageDigest md = null;

    static {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String md5Hash(String stringToHash) {

        if (md == null) {
            return null;
        }

        // Remove any remaining data from the md object.
        md.reset();

        // digest() method is called to calculate message digest
        //  of an input digest() return array of byte
        byte[] messageDigest = md.digest(stringToHash.getBytes());

        // Convert byte array into sigNum representation
        BigInteger number = new BigInteger(1, messageDigest);

        // Convert message digest into hex value
        StringBuilder hashtext = new StringBuilder(number.toString(16));
        while (hashtext.length() < 32)
            hashtext.insert(0, "0");
        return hashtext.toString();


    }
}
