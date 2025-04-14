package com.liveodds.service;

import com.liveodds.exception.NonExistingException;
import com.liveodds.exception.TeamAlreadyInMatchException;
import com.liveodds.service.impl.ScoreboardServiceImpl;
import com.liveodds.utils.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.*;

public class ScoreboardServiceImplTest {

    private ScoreboardService service;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Mockito.mock(Validator.class);
        service = new ScoreboardServiceImpl(validator);
        doNothing().when(validator).validateTeam("Spain");
        doNothing().when(validator).validateTeam("Italy");
        doNothing().when(validator).validateTeam("England");
        doThrow(new NonExistingException("Team Tnt does not exist.")).when(validator).validateTeam("Tnt");
        doThrow(new IllegalArgumentException("Team name cannot be null or empty.")).when(validator).validateTeam(null);
        doThrow(new IllegalArgumentException("Team name cannot be null or empty.")).when(validator).validateTeam(" ");
        doThrow(new IllegalArgumentException("Team name cannot be null or empty.")).when(validator).validateTeam("");
        doThrow(new IllegalArgumentException("Score cannot be negative.")).when(validator).validateScore(-2);
    }

    @Test
    public void given_correctParam_when_startNewMatch_then_newMatchCreated() {
        String homeTeam = "Spain";
        String awayTeam = "Italy";
        service.startNewMatch(homeTeam, awayTeam);
        List<String> summary = service.getSummary();
        Assertions.assertTrue(summary.contains("Spain 0 - Italy 0"));
    }

    @Test
    public void given_alreadyInMatchTeam_when_startNewMatch_then_correctExceptionThrown() {
        String homeTeam = "Spain";
        String awayTeam = "Italy";
        service.startNewMatch(homeTeam, awayTeam);
        TeamAlreadyInMatchException exception = Assertions.assertThrows(TeamAlreadyInMatchException.class, () -> service.startNewMatch(homeTeam, "England"));
        Assertions.assertEquals("Team Spain already in match.", exception.getMessage());
    }

    @Test
    public void given_nonExistingTeam_when_startNewMatch_then_correctExceptionThrown() {
        String homeTeam = "Spain";
        String awayTeam = "Tnt";
        NonExistingException exception = Assertions.assertThrows(NonExistingException.class, () -> service.startNewMatch(homeTeam, awayTeam));
        Assertions.assertEquals("Team Tnt does not exist.", exception.getMessage());
    }

    @Test
    public void given_nullTeam_when_startNewMatch_then_correctExceptionThrown() {
        String homeTeam = "Spain";
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> service.startNewMatch(homeTeam, null));
        Assertions.assertEquals("Team name cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void given_emptyTeam_when_startNewMatch_then_correctExceptionThrown() {
        String homeTeam = "Spain";
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> service.startNewMatch(homeTeam, " "));
        Assertions.assertEquals("Team name cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void given_correctParam_when_updateScore_then_scoresUpdated() {
        String homeTeam = "Spain";
        String awayTeam = "Italy";
        service.startNewMatch(homeTeam, awayTeam);
        service.updateMatch(homeTeam, awayTeam, 1,0);
        List<String> summary = service.getSummary();
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
    public void given_nullTeam_when_updateScore_then_correctExceptionThrown() {
        String homeTeam = "Spain";
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateMatch(homeTeam, null, 2, 0));
        Assertions.assertEquals("Team name cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void given_emptyTeam_when_updateScore_then_correctExceptionThrown() {
        String homeTeam = "Spain";
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateMatch(homeTeam, "", 2, 0));
        Assertions.assertEquals("Team name cannot be null or empty.", exception.getMessage());
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
        Assertions.assertEquals("Score cannot be negative.", exception.getMessage());
    }

    @Test
    public void given_correctParam_when_finishMatch_then_matchRemoved() {
        String homeTeam = "Spain";
        String awayTeam = "Italy";
        service.startNewMatch(homeTeam, awayTeam);
        service.finishMatch(homeTeam, awayTeam);
        List<String> summary = service.getSummary();
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
    public void given_nullTeam_when_finishMatch_then_correctExceptionThrown() {
        String homeTeam = "Spain";
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> service.finishMatch(homeTeam, null));
        Assertions.assertEquals("Team name cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void given_emptyTeam_when_finishMatch_then_correctExceptionThrown() {
        String homeTeam = "Spain";
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> service.finishMatch(homeTeam, ""));
        Assertions.assertEquals("Team name cannot be null or empty.", exception.getMessage());
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
        List<String> summary = service.getSummary();
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
        List<String> summary = service.getSummary();
        StringBuilder actualSummary = new StringBuilder();
        for(String s : summary) {
            actualSummary.append(s).append("\n");
        }
        String expectedSummary = """
                England 3 - Portugal 1
                Spain 2 - Italy 0
                France 0 - Germany 1
                """;
        Assertions.assertEquals(expectedSummary, actualSummary.toString());
    }

    @Test
    public void given_matchesWithSameScores_when_getSummary_then_orderedSummaryGotten() {
        service.startNewMatch("Mexico", "Canada");
        service.startNewMatch("Spain", "Brazil");
        service.startNewMatch("Germany", "France");
        timeout(1);
        service.startNewMatch("Uruguay", "Italy");
        service.startNewMatch("Argentina", "Australia");
        service.updateMatch("Mexico", "Canada", 0, 5);
        service.updateMatch("Spain", "Brazil", 10, 2);
        service.updateMatch("Germany", "France", 2, 2);
        service.updateMatch("Uruguay", "Italy", 6, 6);
        service.updateMatch("Argentina", "Australia", 3, 1);
        List<String> summary = service.getSummary();
        StringBuilder actualSummary = new StringBuilder();
        for(String s : summary) {
            actualSummary.append(s).append("\n");
        }
        String expectedSummary = """
                Uruguay 6 - Italy 6
                Spain 10 - Brazil 2
                Mexico 0 - Canada 5
                Argentina 3 - Australia 1
                Germany 2 - France 2
                """;
        Assertions.assertEquals(expectedSummary, actualSummary.toString());
    }
}
