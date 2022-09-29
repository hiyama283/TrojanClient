package net.sushiclient.client.utils;

import java.security.MessageDigest;

public class HWID {

    /**
     *
     * @return HWID in SHA-224;
     *
     */

    public static String getHWID() {
        try{
            String toEncrypt =  System.getenv("COMPUTERNAME") +
                    System.getProperty("user.name") +
                    System.getenv("PROCESSOR_IDENTIFIER") +
                    System.getenv("PROCESSOR_LEVEL") +
                    System.getenv("PROCESSOR_REVISION");
            MessageDigest md = MessageDigest.getInstance("SHA-224");
            md.update(toEncrypt.getBytes());
            StringBuilder hexString = new StringBuilder();

            byte[] byteData = md.digest();

            for (byte aByteData : byteData) {
                String hex = Integer.toHexString(0xff & aByteData);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

}