package com.project.back_end.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class HelperUtil {
    public static String titleCase(String text) {
        if(text == null || text.isEmpty()) return text;

        return Arrays.stream(text.split(" "))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));

    }
}
