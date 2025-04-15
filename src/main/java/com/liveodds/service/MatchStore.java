package com.liveodds.service;

import com.liveodds.model.Match;

import java.util.List;
import java.util.Optional;

public interface MatchStore {
    Optional<Match> findMatch(Object key);

    List<Match> findMatches();

    void save(Object key, Match match);

    void remove(Object key);
}
