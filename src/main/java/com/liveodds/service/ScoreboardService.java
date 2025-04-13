package com.liveodds.service;

public interface ScoreboardService {

    void startNewMatch(String homeTeam, String awayTeam);
    void updateMatch(String homeTeam, String awayTeam, int homeTeamScore, int awayTeamScore);
    void finishMatch(String homeTeam, String awayTeam);
    String getSummary();
}
