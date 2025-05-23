package com.liveodds.service;

import com.liveodds.exception.NonExistingException;
import com.liveodds.exception.TeamAlreadyInMatchException;
import com.liveodds.model.Match;
import com.liveodds.utils.NameUtil;
import com.liveodds.utils.Validator;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

final class ScoreboardServiceImpl implements ScoreboardService {

    private final Validator validator;
    private final MatchStore matchStore;

    public ScoreboardServiceImpl(Validator validator, MatchStore matchStore) {
        this.validator = validator;
        this.matchStore = matchStore;
    }

    @Override
    public void startNewMatch(String homeTeam, String awayTeam) {
        homeTeam = validator.validateTeam(homeTeam);
        awayTeam = validator.validateTeam(awayTeam);
        if (isTeamAlreadyInMatch(homeTeam)) {
            throw new TeamAlreadyInMatchException(String.format("Team %s already in match.", homeTeam));
        }
        if (isTeamAlreadyInMatch(awayTeam)) {
            throw new TeamAlreadyInMatchException(String.format("Team %s already in match.", awayTeam));
        }
        Match match = new Match(homeTeam, awayTeam, 0,0, Instant.now(), 0);
        matchStore.save(keyOf(homeTeam, awayTeam), match);
    }

    @Override
    public void updateMatch(String homeTeam, String awayTeam, int homeTeamScore, int awayTeamScore) {
        homeTeam = validator.validateTeam(homeTeam);
        awayTeam = validator.validateTeam(awayTeam);
        Optional<Match> optionalMatch = matchStore.findMatch(keyOf(homeTeam, awayTeam));
        if (optionalMatch.isEmpty()) {
            throw new NonExistingException(String.format("Match %s - %s does not exist.", homeTeam, awayTeam));
        }
        validator.validateScore(homeTeamScore);
        validator.validateScore(awayTeamScore);
        Match match = optionalMatch.get().updateScores(homeTeamScore, awayTeamScore);
        matchStore.save(keyOf(homeTeam, awayTeam), match);
    }

    @Override
    public void finishMatch(String homeTeam, String awayTeam) {
        homeTeam = validator.validateTeam(homeTeam);
        awayTeam = validator.validateTeam(awayTeam);
        Optional<Match> optionalMatch = matchStore.findMatch(keyOf(homeTeam, awayTeam));
        if (optionalMatch.isEmpty()) {
            throw new NonExistingException(String.format("Match %s - %s does not exist.", homeTeam, awayTeam));
        }
        matchStore.remove(keyOf(homeTeam, awayTeam));
    }

    @Override
    public List<String> getSummary() {
        return matchStore.findMatches().stream()
                .sorted()
                .map(Match::toString)
                .toList();
    }

    private boolean isTeamAlreadyInMatch(String team) {
        return matchStore.findMatches().stream()
                .anyMatch(m -> m.awayTeam().equals(team) || m.homeTeam().equals(team));
    }

    private String keyOf(String homeTeam, String awayTeam) {
        return NameUtil.normalize(homeTeam) + "_" + NameUtil.normalize(awayTeam);
    }

}
