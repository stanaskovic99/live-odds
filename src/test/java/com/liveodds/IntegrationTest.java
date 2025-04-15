package com.liveodds;

import com.liveodds.exception.NonExistingException;
import com.liveodds.exception.TeamAlreadyInMatchException;
import com.liveodds.model.Match;
import com.liveodds.service.ScoreboardFactory;
import com.liveodds.service.ScoreboardService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class IntegrationTest {

    private final String SPAIN = "Spain";
    private final String ITALY = "Italy";

    private ScoreboardService service;

    @BeforeEach
    public void setUp() {
        service = ScoreboardFactory.createDefaultScoreboard();
    }

    @Test
    public void given_correctParam_when_startNewMatch_then_newMatchCreated() {
        service.startNewMatch(SPAIN, ITALY);
        List<String> summary = service.getSummary();
        Assertions.assertEquals("Spain 0 - Italy 0", summary.get(0));
    }

    @Test
    public void given_alreadyInMatchTeam_when_startNewMatch_then_correctExceptionThrown() {
        service.startNewMatch(SPAIN, ITALY);
        TeamAlreadyInMatchException exception = Assertions.assertThrows(TeamAlreadyInMatchException.class, () -> service.startNewMatch(SPAIN, "Denmark"));
        Assertions.assertEquals("Team Spain already in match.", exception.getMessage());
    }

    @Test
    public void given_nonExistingTeam_when_startNewMatch_then_correctExceptionThrown() {
        NonExistingException exception = Assertions.assertThrows(NonExistingException.class, () -> service.startNewMatch(SPAIN, "Tnt"));
        Assertions.assertEquals("Team Tnt does not exist.", exception.getMessage());
    }

    @Test
    public void given_nullTeam_when_startNewMatch_then_correctExceptionThrown() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> service.startNewMatch(SPAIN, null));
        Assertions.assertEquals("Team name cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void given_emptyTeam_when_startNewMatch_then_correctExceptionThrown() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> service.startNewMatch(SPAIN, " "));
        Assertions.assertEquals("Team name cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void given_correctTeamWithTwoWord_when_startNewMatch_then_noException() {
        service.startNewMatch(SPAIN, "South Sudan");
        Assertions.assertDoesNotThrow(() -> service.startNewMatch(ITALY, "Sudan"));
    }

    @Test
    public void given_correctParam_when_updateScore_then_scoresUpdated() {
        service.startNewMatch(SPAIN, ITALY);
        service.updateMatch(SPAIN, ITALY, 1, 0);
        List<String> summary = service.getSummary();
        Assertions.assertEquals("Spain 1 - Italy 0", summary.get(0));
    }

    @Test
    public void given_nonExistingTeam_when_updateScore_then_correctExceptionThrown() {
        NonExistingException exception = Assertions.assertThrows(NonExistingException.class, () -> service.updateMatch(SPAIN, "Tnt", 2, 0));
        Assertions.assertEquals("Team Tnt does not exist.", exception.getMessage());
    }

    @Test
    public void given_nullTeam_when_updateScore_then_correctExceptionThrown() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateMatch(SPAIN, null, 2, 0));
        Assertions.assertEquals("Team name cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void given_emptyTeam_when_updateScore_then_correctExceptionThrown() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateMatch(SPAIN, "", 2, 0));
        Assertions.assertEquals("Team name cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void given_nonExistingMatch_when_updateScore_then_correctExceptionThrown() {
        service.startNewMatch(SPAIN, ITALY);
        NonExistingException exception = Assertions.assertThrows(NonExistingException.class, () -> service.updateMatch(SPAIN, "Denmark", 2, 0));
        Assertions.assertEquals("Match Spain - Denmark does not exist.", exception.getMessage());
    }

    @Test
    public void given_incorrectScore_when_updateScore_then_correctExceptionThrown() {
        service.startNewMatch(SPAIN, ITALY);
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateMatch(SPAIN, ITALY, -2, 0));
        Assertions.assertEquals("Score cannot be negative.", exception.getMessage());
    }

    @Test
    public void given_correctParam_when_finishMatch_then_matchRemoved() {
        service.startNewMatch(SPAIN, ITALY);
        service.finishMatch(SPAIN, ITALY);
        List<String> summary = service.getSummary();
        Assertions.assertTrue(summary.isEmpty());
    }

    @Test
    public void given_nonExistingTeam_when_finishMatch_then_correctExceptionThrown() {
        NonExistingException exception = Assertions.assertThrows(NonExistingException.class, () -> service.finishMatch(SPAIN, "Tnt"));
        Assertions.assertEquals("Team Tnt does not exist.", exception.getMessage());
    }

    @Test
    public void given_nullTeam_when_finishMatch_then_correctExceptionThrown() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> service.finishMatch(SPAIN, null));
        Assertions.assertEquals("Team name cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void given_emptyTeam_when_finishMatch_then_correctExceptionThrown() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> service.finishMatch(SPAIN, ""));
        Assertions.assertEquals("Team name cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void given_nonExistingMatch_when_finishMatch_then_correctExceptionThrown() {
        service.startNewMatch(SPAIN, ITALY);
        NonExistingException exception = Assertions.assertThrows(NonExistingException.class, () -> service.finishMatch(SPAIN, "Denmark"));
        Assertions.assertEquals("Match Spain - Denmark does not exist.", exception.getMessage());
    }

    @Test
    public void given_matches_when_getSummary_then_summaryGotten() {
        service.startNewMatch(SPAIN, ITALY);
        service.startNewMatch("Denmark", "Portugal");
        service.startNewMatch("France", "Germany");

        service.updateMatch(SPAIN, ITALY, 2, 0);
        service.updateMatch("Denmark", "Portugal", 3, 1);
        service.updateMatch("France", "Germany", 0, 1);

        List<String> summary = service.getSummary();
        Assertions.assertEquals("Denmark 3 - Portugal 1", summary.get(0));
        Assertions.assertEquals("Spain 2 - Italy 0", summary.get(1));
        Assertions.assertEquals("France 0 - Germany 1", summary.get(2));
    }

    @Test
    public void given_matchesWithSameScores_when_getSummary_then_orderedSummaryGotten() {
        service.startNewMatch("Mexico", "Canada");
        service.startNewMatch(SPAIN, "Brazil");
        service.startNewMatch("Germany", "France");
        times(1);
        service.startNewMatch("Uruguay", ITALY);
        service.startNewMatch("Argentina", "Australia");

        service.updateMatch("Mexico", "Canada", 0, 5);
        service.updateMatch(SPAIN, "Brazil", 10, 2);
        service.updateMatch("Germany", "France", 2, 2);
        service.updateMatch("Uruguay", ITALY, 6, 6);
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
}
