package com.liveodds.service;

import java.util.List;

public interface ScoreboardService {

    void startNewMatch(String homeTeam, String awayTeam);
    void updateMatch(String homeTeam, String awayTeam, int homeTeamScore, int awayTeamScore);
    void finishMatch(String homeTeam, String awayTeam);
    List<String> getSummary();
}
