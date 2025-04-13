package com.liveodds.service;

import com.liveodds.exception.NonExistingException;
import com.liveodds.exception.TeamAlreadyInMatchException;
import com.liveodds.service.impl.ScoreboardServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ScoreboardServiceImplTest {

    private ScoreboardService service;

    @BeforeEach
    public void setUp() {
        service = new ScoreboardServiceImpl();
    }

    @Test
    public void given_correctParam_when_startNewMatch_then_newMatchCreated() {
        String homeTeam = "Spain";
        String awayTeam = "Italy";
        service.startNewMatch(homeTeam, awayTeam);
        String summary = service.getSummary();
        Assertions.assertTrue(summary.contains("Spain 0 - Italy 0"));
    }

    @Test
    public void given_alreadyInMatchTeam_when_startNewMatch_then_correctExceptionThrown() {
        String homeTeam = "Spain";
        String awayTeam = "Italy";
        service.startNewMatch(homeTeam, awayTeam);
        TeamAlreadyInMatchException exception = Assertions.assertThrows(TeamAlreadyInMatchException.class, () -> service.startNewMatch(homeTeam, "England"));
        Assertions.assertEquals("Team Spain already in match", exception.getMessage());
    }

    @Test
    public void given_nonExistingTeam_when_startNewMatch_then_correctExceptionThrown() {
        String homeTeam = "Spain";
        String awayTeam = "Tnt";
        NonExistingException exception = Assertions.assertThrows(NonExistingException.class, () -> service.startNewMatch(homeTeam, awayTeam));
        Assertions.assertEquals("Team Tnt does not exist.", exception.getMessage());
    }

    @Test
    public void given_correctParam_when_updateScore_then_scoresUpdated() {
        String homeTeam = "Spain";
        String awayTeam = "Italy";
        service.startNewMatch(homeTeam, awayTeam);
        service.updateMatch(homeTeam, awayTeam, 1,0);
        String summary = service.getSummary();
        Assertions.assertTrue(summary.contains("Spain 1 - Italy 0"));
        Assertions.assertFalse(summary.contains("Spain 0 - Italy 0"));
    }

    @Test
    public void given_nonExistingTeam_when_updateScore_then_correctExceptionThrown() {
        String homeTeam = "Spain";
        String awayTeam = "Tnt";
        NonExistingException exception = Assertions.assertThrows(NonExistingException.class, () -> service.updateMatch(homeTeam, awayTeam, 2, 0));
        Assertions.assertEquals("Team Tnt does not exist.", exception.getMessage());
    }

    @Test
    public void given_nonExistingMatch_when_updateScore_then_correctExceptionThrown() {
        String homeTeam = "Spain";
        String awayTeam = "Italy";
        service.startNewMatch(homeTeam, awayTeam);
        NonExistingException exception = Assertions.assertThrows(NonExistingException.class, () -> service.updateMatch(homeTeam, "England", 2, 0));
        Assertions.assertEquals("Match Spain - England does not exist.", exception.getMessage());
    }

    @Test
    public void given_incorrectScore_when_updateScore_then_correctExceptionThrown() {
        String homeTeam = "Spain";
        String awayTeam = "Italy";
        service.startNewMatch(homeTeam, awayTeam);
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateMatch(homeTeam, awayTeam, -2, 0));
        Assertions.assertEquals("Incorrect score. Score must be bigger or equal to 0.", exception.getMessage());
    }

    @Test
    public void given_correctParam_when_finishMatch_then_matchRemoved() {
        String homeTeam = "Spain";
        String awayTeam = "Italy";
        service.startNewMatch(homeTeam, awayTeam);
        service.finishMatch(homeTeam, awayTeam);
        String summary = service.getSummary();
        Assertions.assertFalse(summary.contains("Spain 0 - Italy 0"));
    }

    @Test
    public void given_nonExistingTeam_when_finishMatch_then_correctExceptionThrown() {
        String homeTeam = "Spain";
        String awayTeam = "Tnt";
        NonExistingException exception = Assertions.assertThrows(NonExistingException.class, () -> service.finishMatch(homeTeam, awayTeam));
        Assertions.assertEquals("Team Tnt does not exist.", exception.getMessage());
    }

    @Test
    public void given_nonExistingMatch_when_finishMatch_then_correctExceptionThrown() {
        String homeTeam = "Spain";
        String awayTeam = "Italy";
        service.startNewMatch(homeTeam, awayTeam);
        NonExistingException exception = Assertions.assertThrows(NonExistingException.class, () -> service.finishMatch(homeTeam, "England"));
        Assertions.assertEquals("Match Spain - England does not exist.", exception.getMessage());
    }

    @Test
    public void given_matches_when_getSummary_then_summaryGotten() {
        service.startNewMatch("Spain", "Italy");
        service.startNewMatch("England", "Portugal");
        service.startNewMatch("France", "Germany");
        service.updateMatch("Spain", "Italy", 2, 0);
        service.updateMatch("England", "Portugal", 3, 1);
        service.updateMatch("France", "Germany", 0, 1);
        String summary = service.getSummary();
        Assertions.assertTrue(summary.contains("Spain 2 - Italy 0"));
        Assertions.assertTrue(summary.contains("England 3 - Portugal 1"));
        Assertions.assertTrue(summary.contains("France 0 - Germany 1"));
    }

    @Test
    public void given_matchesWithDifferentScores_when_getSummary_then_orderedSummaryGotten() {
        service.startNewMatch("Spain", "Italy");
        service.startNewMatch("England", "Portugal");
        service.startNewMatch("France", "Germany");
        service.updateMatch("Spain", "Italy", 2, 0);
        service.updateMatch("England", "Portugal", 3, 1);
        service.updateMatch("France", "Germany", 0, 1);
        String summary = service.getSummary();
        String expectedSummary = """
                England 3 - Portugal 1
                Spain 2 - Italy 0
                France 0 - Germany 1""";
        Assertions.assertEquals(expectedSummary, summary);
    }

    @Test
    public void given_matchesWithSameScores_when_getSummary_then_orderedSummaryGotten() {
        service.startNewMatch("Mexico", "Canada");
        service.startNewMatch("Spain", "Brazil");
        service.startNewMatch("Germany", "France");
        service.startNewMatch("Uruguay", "Italy");
        service.startNewMatch("Argentina", "Australia");
        service.updateMatch("Mexico", "Canada", 0, 5);
        service.updateMatch("Spain", "Brazil", 10, 2);
        service.updateMatch("Germany", "France", 2, 2);
        service.updateMatch("Uruguay", "Italy", 6, 6);
        service.updateMatch("Argentina", "Australia", 3, 1);
        String summary = service.getSummary();
        String expectedSummary = """
                Uruguay 6 - Italy 6
                Spain 10 - Brazil 2
                Mexico 0 - Canada 5
                Argentina 3 - Australia 1
                Germany 2 - France 2""";
        Assertions.assertEquals(expectedSummary, summary);
    }
}
