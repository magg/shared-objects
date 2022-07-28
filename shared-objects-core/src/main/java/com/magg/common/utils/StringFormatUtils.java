package com.magg.common.utils;

/**
 * Class containing common utility methods for formatting strings.
 * <p/>
 */
public final class StringFormatUtils {
    private StringFormatUtils() {}

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first line).
     */
    public static String toIndentedString(Object object) {
        if (object == null) {
            return "null";
        }
        return object.toString().replace("\n", "\n    ");
    }
}
