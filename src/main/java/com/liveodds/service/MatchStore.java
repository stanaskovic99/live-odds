package com.liveodds.service;

import com.liveodds.model.Match;

import java.util.List;
import java.util.Optional;

public interface MatchStore {
    Optional<Match> findMatch(String key);

    List<Match> findMatches();

    void save(String key, Match match);

    void remove(String key);
}
