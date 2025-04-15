package com.liveodds.utils;

import com.liveodds.exception.NonExistingException;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class Validator {

    private final Set<String> countryNames;

    public Validator() {
        this.countryNames = loadCountryNames();
    }

    public String validateTeam(String teamName) {
        if (teamName == null || teamName.isBlank()) {
            throw new IllegalArgumentException("Team name cannot be null or empty.");
        }

        String normalized = NameUtil.normalize(teamName);

        if (!countryNames.contains(normalized)) {
            throw new NonExistingException(String.format("Team %s does not exist.", teamName));
        }
        return normalized;
    }

    public void validateScore(int score) {
        if (score < 0) {
            throw new IllegalArgumentException("Score cannot be negative.");
        }
    }

    private Set<String> loadCountryNames() {
        return Arrays.stream(Locale.getISOCountries())
                .map(code -> new Locale("", code).getDisplayCountry())
                .collect(Collectors.toSet());
    }
}
