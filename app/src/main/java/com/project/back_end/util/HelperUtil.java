package com.project.back_end.util;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Utility class for common helper methods.
 * Provides methods for string manipulation and formatting.
 */
public class HelperUtil {
    /**
     * Converts a given text to title case.
     * Each word's first letter is capitalized, and the rest are in lowercase.
     *
     * @param text the input text to be converted
     * @return the text in title case
     */
    public static String titleCase(String text) {
        if(text == null || text.isEmpty()) return text;

        return Arrays.stream(text.split(" "))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));

    }
}
