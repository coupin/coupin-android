package com.kibou.abisoyeoke_lawal.coupinapp.Utils;

import java.util.regex.Pattern;

/**
 * Created by abisoyeoke-lawal on 11/8/17.
 */

public class StringUtils {
    /**
     * Capitalize string
     * @param s
     * @return
     */
    static public String capitalize(String s) {
        int pos = 0;
        boolean capitalize = true;
        StringBuilder sb = new StringBuilder(s);
        while (pos < sb.length()) {
            if (sb.charAt(pos) == ' ') {
                capitalize = true;
            } else if (capitalize && !Character.isWhitespace(sb.charAt(pos))) {
                sb.setCharAt(pos, Character.toUpperCase(sb.charAt(pos)));
                capitalize = false;
            }
            pos++;
        }

        return sb.toString();
    }

    /**
     * Email Validation
     * @param s
     * @return
     */
    static public boolean isEmail(String s) {
        Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
        return pattern.matcher(s).matches();
    }

    /**
     * Check if string is phone number format
     * @param s
     * @return
     */
    static public boolean isPhoneNumber(String s) {
        Pattern pattern = Pattern.compile("^0+[0-9]{10}$");
        return pattern.matcher(s).matches();
    }
}
