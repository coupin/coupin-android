package com.kibou.abisoyeoke_lawal.coupinapp.Utils;

/**
 * Created by abisoyeoke-lawal on 11/8/17.
 */

public class StringUtils {
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
}
