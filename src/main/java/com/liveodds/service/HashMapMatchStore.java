package com.liveodds.service;

import com.liveodds.model.Match;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

final class HashMapMatchStore implements MatchStore {

    private final Map<String, Match> matches = new HashMap<>();

    @Override
    public Optional<Match> findMatch(String key) {
        checkKey(key);
        return Optional.ofNullable(matches.get((String) key));
    }

    @Override
    public List<Match> findMatches() {
        return matches.values().stream().toList();
    }

    @Override
    public void save(String key, Match match) {
        checkKey(key);
        matches.put((String) key, match);
    }

    @Override
    public void remove(String key) {
        checkKey(key);
        matches.remove((String) key);
    }

    private void checkKey(String key) {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("Key must be a string");
        }
    }
}
