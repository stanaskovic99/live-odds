package com.liveodds.service.impl;

import com.liveodds.exception.NonExistingException;
import com.liveodds.exception.TeamAlreadyInMatchException;
import com.liveodds.model.Match;
import com.liveodds.service.ScoreboardService;
import com.liveodds.utils.Validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ScoreboardServiceImpl implements ScoreboardService {

    private final Map<String, Match>  matches = new HashMap<>();
    private final Validator validator;

    public ScoreboardServiceImpl(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void startNewMatch(String homeTeam, String awayTeam) {
        validator.validateTeam(homeTeam);
        validator.validateTeam(awayTeam);
        if(isTeamAlreadyInMatch(homeTeam)) {
            throw new TeamAlreadyInMatchException(String.format("Team %s already in match.", homeTeam));
        }
        if(isTeamAlreadyInMatch(awayTeam)) {
            throw new TeamAlreadyInMatchException(String.format("Team %s already in match.", awayTeam));
        }
        matches.put(keyOf(homeTeam, awayTeam), new Match(homeTeam, awayTeam));
    }

    @Override
    public void updateMatch(String homeTeam, String awayTeam, int homeTeamScore, int awayTeamScore) {
        validator.validateTeam(homeTeam);
        validator.validateTeam(awayTeam);
        Match match = getMatch(homeTeam, awayTeam);
        if(match == null) {
            throw new NonExistingException(String.format("Match %s - %s does not exist.", homeTeam, awayTeam));
        }
        validator.validateScore(homeTeamScore);
        validator.validateScore(awayTeamScore);
        match.updateScores(homeTeamScore, awayTeamScore);
    }

    @Override
    public void finishMatch(String homeTeam, String awayTeam) {
        validator.validateTeam(homeTeam);
        validator.validateTeam(awayTeam);
        Match match = matches.get(keyOf(homeTeam, awayTeam));
        if(match == null) {
            throw new NonExistingException(String.format("Match %s - %s does not exist.", homeTeam, awayTeam));
        }
        matches.remove(keyOf(homeTeam, awayTeam));
    }

    @Override
    public List<String> getSummary() {
        return matches.values().stream()
                .sorted()
                .map(Match::toString)
                .toList();
    }

    private Match getMatch(String homeTeam, String awayTeam) {
        return matches.get(keyOf(homeTeam, awayTeam));
    }

    private boolean isTeamAlreadyInMatch(String team) {
        return matches.keySet().stream().anyMatch(k -> k.contains(team));
    }

    private String keyOf(String homeTeam, String awayTeam) {
        return homeTeam + "_" + awayTeam;
    }
}
