package com.liveodds.model;

import java.time.Instant;

public class Match implements Comparable<Match> {

    private final String homeTeam;
    private final String awayTeam;
    private int homeTeamScore;
    private int awayTeamScore;
    private final Instant startTime;

    public Match(String homeTeam, String awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeTeamScore = 0;
        this.awayTeamScore = 0;
        this.startTime = Instant.now();
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public int getHomeTeamScore() {
        return homeTeamScore;
    }

    public void setHomeTeamScore(int homeTeamScore) {
        this.homeTeamScore = homeTeamScore;
    }

    public int getAwayTeamScore() {
        return awayTeamScore;
    }

    public void setAwayTeamScore(int awayTeamScore) {
        this.awayTeamScore = awayTeamScore;
    }

    public Instant getStartTime() {
        return startTime;
    }

    private int getTotalScore() {
        return homeTeamScore + awayTeamScore;
    }

    public String toString() {
        return String.format("%s %d - %s %d", homeTeam, homeTeamScore, awayTeam, awayTeamScore);
    }

    @Override
    public int compareTo(Match o) {
        int scoreComparison = Integer.compare(o.getTotalScore(), this.getTotalScore());
        return scoreComparison != 0
                ? scoreComparison
                : o.startTime.compareTo(startTime);
    }
}
