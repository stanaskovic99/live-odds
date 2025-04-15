package com.liveodds.service;

import com.liveodds.exception.NonExistingException;
import com.liveodds.exception.TeamAlreadyInMatchException;
import com.liveodds.service.impl.ScoreboardServiceImpl;
import com.liveodds.utils.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class ScoreboardServiceImplTest {

    private ScoreboardService service;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Mockito.mock(Validator.class);
        service = new ScoreboardServiceImpl(validator);
        when(validator.validateTeam("Spain")).thenReturn("Spain");
        when(validator.validateTeam("Italy")).thenReturn("Italy");
        when(validator.validateTeam("Denmark")).thenReturn("Denmark");
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
        TeamAlreadyInMatchException exception = Assertions.assertThrows(TeamAlreadyInMatchException.class, () -> service.startNewMatch(homeTeam, "Denmark"));
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
    public void given_correctTeamWithTwoWord_when_startNewMatch_then_() {
        when(validator.validateTeam("South Sudan")).thenReturn("South Sudan");
        when(validator.validateTeam("Sudan")).thenReturn("Sudan");
        String homeTeam = "Spain";
        String awayTeam = "South Sudan";
        service.startNewMatch(homeTeam, awayTeam);
        Assertions.assertDoesNotThrow(() -> service.startNewMatch("Italy", "Sudan"));
    }

    @Test
    public void given_correctParam_when_updateScore_then_scoresUpdated() {
        String homeTeam = "Spain";
        String awayTeam = "Italy";
        service.startNewMatch(homeTeam, awayTeam);
        service.updateMatch(homeTeam, awayTeam, 1, 0);
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
        NonExistingException exception = Assertions.assertThrows(NonExistingException.class, () -> service.updateMatch(homeTeam, "Denmark", 2, 0));
        Assertions.assertEquals("Match Spain - Denmark does not exist.", exception.getMessage());
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
        NonExistingException exception = Assertions.assertThrows(NonExistingException.class, () -> service.finishMatch(homeTeam, "Denmark"));
        Assertions.assertEquals("Match Spain - Denmark does not exist.", exception.getMessage());
    }

    @Test
    public void given_matches_when_getSummary_then_summaryGotten() {
        List<String> countries = List.of("Denmark", "Portugal", "France", "Germany");
        for(String country : countries) {
            when(validator.validateTeam(country)).thenReturn(country);
        }
        service.startNewMatch("Spain", "Italy");
        service.startNewMatch("Denmark", "Portugal");
        service.startNewMatch("France", "Germany");
        service.updateMatch("Spain", "Italy", 2, 0);
        service.updateMatch("Denmark", "Portugal", 3, 1);
        service.updateMatch("France", "Germany", 0, 1);
        List<String> summary = service.getSummary();
        Assertions.assertTrue(summary.contains("Spain 2 - Italy 0"));
        Assertions.assertTrue(summary.contains("Denmark 3 - Portugal 1"));
        Assertions.assertTrue(summary.contains("France 0 - Germany 1"));
    }

    @Test
    public void given_matchesWithDifferentScores_when_getSummary_then_orderedSummaryGotten() {
        List<String> countries = List.of("Denmark", "Portugal", "France", "Germany");
        for(String country : countries) {
            when(validator.validateTeam(country)).thenReturn(country);
        }
        service.startNewMatch("Spain", "Italy");
        service.startNewMatch("Denmark", "Portugal");
        service.startNewMatch("France", "Germany");
        service.updateMatch("Spain", "Italy", 2, 0);
        service.updateMatch("Denmark", "Portugal", 3, 1);
        service.updateMatch("France", "Germany", 0, 1);
        List<String> summary = service.getSummary();
        StringBuilder actualSummary = new StringBuilder();
        for (String s : summary) {
            actualSummary.append(s).append("\n");
        }
        String expectedSummary = """
                Denmark 3 - Portugal 1
                Spain 2 - Italy 0
                France 0 - Germany 1
                """;
        Assertions.assertEquals(expectedSummary, actualSummary.toString());
    }

    @Test
    public void given_matchesWithSameScores_when_getSummary_then_orderedSummaryGotten() {
        List<String> countries = List.of("Mexico", "Canada", "Brazil", "Germany", "France", "Uruguay", "Argentina", "Australia");
        for(String country : countries) {
            when(validator.validateTeam(country)).thenReturn(country);
        }
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
        for (String s : summary) {
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

    @Test
    public void given_matchesWithNonNormalizedTeamName_when_getSummary_then_nameNormalized() {
        when(validator.validateTeam("  ItaLy")).thenReturn("Italy");
        when(validator.validateTeam("DeNmark")).thenReturn("Denmark");
        when(validator.validateTeam("Cape Verde")).thenReturn("Cape Verde");
        when(validator.validateTeam("Portugal")).thenReturn("Portugal");
        when(validator.validateTeam("France")).thenReturn("France");
        service.startNewMatch("Spain", "  ItaLy");
        service.startNewMatch("DeNmark", "Portugal");
        service.startNewMatch("France", "Cape Verde");
        service.updateMatch("Spain", "Italy", 2, 0);
        service.updateMatch("Denmark", "Portugal", 3, 1);
        service.updateMatch("France", "Cape Verde", 0, 1);
        List<String> summary = service.getSummary();
        StringBuilder actualSummary = new StringBuilder();
        for (String s : summary) {
            actualSummary.append(s).append("\n");
        }
        String expectedSummary = """
                Denmark 3 - Portugal 1
                Spain 2 - Italy 0
                France 0 - Cape Verde 1
                """;
        Assertions.assertEquals(expectedSummary, actualSummary.toString());
    }
}
