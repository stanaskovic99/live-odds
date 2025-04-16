package com.liveodds.model;

import java.time.Instant;

public record Match(String homeTeam, String awayTeam, int homeTeamScore,  int awayTeamScore, Instant startTime, int totalScore) implements Comparable<Match> {

    public Match updateScores(int homeTeamScore, int awayTeamScore) {
        return new Match(this.homeTeam, this.awayTeam, homeTeamScore, awayTeamScore, this.startTime, homeTeamScore + awayTeamScore);
    }

    public String toString() {
        return String.format("%s %d - %s %d", homeTeam, homeTeamScore, awayTeam, awayTeamScore);
    }

    @Override
    public int compareTo(Match o) {
        int scoreComparison = Integer.compare(o.totalScore(), this.totalScore());
        return scoreComparison != 0
                ? scoreComparison
                : o.startTime.compareTo(startTime);
    }
}
