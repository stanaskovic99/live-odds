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
            throw new NonExistingException("Team " + teamName + " does not exist.");
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
        return name.trim().substring(0, 1).toUpperCase() + name.trim().substring(1).toLowerCase();
    }
}
