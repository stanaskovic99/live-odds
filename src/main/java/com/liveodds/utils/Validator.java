package com.liveodds.utils;

import com.liveodds.exception.NonExistingException;

import java.util.*;
import java.util.stream.Collectors;

public class Validator {

    private final Set<String> countryNames;

    public Validator() {
        this.countryNames = loadCountryNames();
    }

    public void validateTeam(String teamName) {
        if(teamName == null || teamName.isBlank()){
            throw new IllegalArgumentException("Team name cannot be null or empty.");
        }

        String normalized = normalizeName(teamName);

        if(!countryNames.contains(normalized)) {
            throw new NonExistingException(String.format("Team %s does not exist.", teamName));
        }
    }

    public void validateScore(int score) {
        if(score < 0) {
            throw new IllegalArgumentException("Score cannot be negative.");
        }
    }

    private Set<String> loadCountryNames() {
        return Arrays.stream(Locale.getISOCountries())
                .map(code -> new Locale("", code).getDisplayCountry())
                .collect(Collectors.toSet());
    }

    private String normalizeName(String name) {
        return Arrays.stream(name.trim()
                        .toLowerCase()
                        .split("\\s+"))
                .map(word ->
                        word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase()
                ).collect(Collectors.joining(" "));
    }
}
