package com.liveodds.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class NameUtil {

    public static String normalize(String str) {
        return Arrays.stream(str.trim()
                        .toLowerCase()
                        .split("\\s+"))
                .map(word ->
                        word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase()
                ).collect(Collectors.joining(" "));
    }
}
