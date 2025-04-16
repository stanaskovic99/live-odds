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
        return Optional.ofNullable(matches.get(key));
    }

    @Override
    public List<Match> findMatches() {
        return matches.values().stream().toList();
    }

    @Override
    public void save(String key, Match match) {
        matches.put(key, match);
    }

    @Override
    public void remove(String key) {
        matches.remove(key);
    }
}
