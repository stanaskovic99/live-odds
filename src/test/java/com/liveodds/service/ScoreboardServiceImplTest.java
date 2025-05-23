package com.liveodds.service;

import com.liveodds.exception.NonExistingException;
import com.liveodds.exception.TeamAlreadyInMatchException;
import com.liveodds.model.Match;
import com.liveodds.utils.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class ScoreboardServiceImplTest {

    private final String SPAIN = "Spain";
    private final String ITALY = "Italy";

    private ScoreboardService service;
    private Validator validator;
    private MatchStore matchStore;

    @BeforeEach
    public void setUp() {
        validator = Mockito.mock(Validator.class);
        matchStore = Mockito.mock(MatchStore.class);
        service = new ScoreboardServiceImpl(validator, matchStore);
        when(validator.validateTeam(SPAIN)).thenReturn(SPAIN);
        when(validator.validateTeam(ITALY)).thenReturn(ITALY);
        when(validator.validateTeam("Denmark")).thenReturn("Denmark");
        doThrow(new NonExistingException("Team Tnt does not exist.")).when(validator).validateTeam("Tnt");
        doThrow(new IllegalArgumentException("Team name cannot be null or empty.")).when(validator).validateTeam(null);
        doThrow(new IllegalArgumentException("Team name cannot be null or empty.")).when(validator).validateTeam(" ");
        doThrow(new IllegalArgumentException("Team name cannot be null or empty.")).when(validator).validateTeam("");
        doThrow(new IllegalArgumentException("Score cannot be negative.")).when(validator).validateScore(-2);
    }

    @Test
    public void given_correctParam_when_startNewMatch_then_newMatchCreated() {
        service.startNewMatch(SPAIN, ITALY);
        ArgumentCaptor<Match> captor = ArgumentCaptor.forClass(Match.class);
        verify(matchStore).save(anyString(), captor.capture());
        Assertions.assertEquals(SPAIN, captor.getValue().homeTeam());
        Assertions.assertEquals(ITALY, captor.getValue().awayTeam());
        Assertions.assertEquals(0, captor.getValue().awayTeamScore());
        Assertions.assertEquals(0, captor.getValue().homeTeamScore());
    }

    @Test
    public void given_alreadyInMatchTeam_when_startNewMatch_then_correctExceptionThrown() {
        when(matchStore.findMatches())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of(new Match(SPAIN, ITALY,0,0, Instant.now(), 0)));
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
        when(validator.validateTeam("South Sudan")).thenReturn("South Sudan");
        when(validator.validateTeam("Sudan")).thenReturn("Sudan");
        service.startNewMatch(SPAIN, "South Sudan");
        Assertions.assertDoesNotThrow(() -> service.startNewMatch(ITALY, "Sudan"));
    }

    @Test
    public void given_correctParam_when_updateScore_then_scoresUpdated() {
        Optional<Match> optionalMatch = Optional.of(new Match(SPAIN, ITALY,0,0, Instant.now(), 0));
        when(matchStore.findMatch(anyString())).thenReturn(optionalMatch);
        service.startNewMatch(SPAIN, ITALY);
        service.updateMatch(SPAIN, ITALY, 1, 0);
        verify(matchStore, times(2)).save(anyString(), any(Match.class));
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
        Optional<Match> optionalMatch = Optional.of(new Match(SPAIN, ITALY,0,0, Instant.now(), 0));
        when(matchStore.findMatch(anyString())).thenReturn(optionalMatch);
        service.startNewMatch(SPAIN, ITALY);
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateMatch(SPAIN, ITALY, -2, 0));
        Assertions.assertEquals("Score cannot be negative.", exception.getMessage());
    }

    @Test
    public void given_correctParam_when_finishMatch_then_matchRemoved() {
        Optional<Match> optionalMatch = Optional.of(new Match(SPAIN, ITALY,0,0, Instant.now(), 0));
        when(matchStore.findMatch(anyString())).thenReturn(optionalMatch);
        service.startNewMatch(SPAIN, ITALY);
        service.finishMatch(SPAIN, ITALY);
        verify(matchStore).remove(anyString());
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
        List<Match> matches = new ArrayList<>();
        matches.add(new Match(SPAIN, ITALY,2,0, Instant.now(), 2));
        matches.add(new Match("Denmark", "Portugal",3,1, Instant.now(), 4));
        matches.add(new Match("France", "Germany",0,1, Instant.now(), 1));

        when(matchStore.findMatches()).thenReturn(matches);
        List<String> summary = service.getSummary();
        Assertions.assertTrue(summary.contains("Spain 2 - Italy 0"));
        Assertions.assertTrue(summary.contains("Denmark 3 - Portugal 1"));
        Assertions.assertTrue(summary.contains("France 0 - Germany 1"));
    }

    @Test
    public void given_matchesWithDifferentScores_when_getSummary_then_orderedSummaryGotten() {
        List<Match> matches = new ArrayList<>();
        matches.add(new Match(SPAIN, ITALY,2,0, Instant.now(), 2));
        matches.add(new Match("Denmark", "Portugal",3,1, Instant.now(), 4));
        matches.add(new Match("France", "Germany",0,1, Instant.now(), 1));

        when(matchStore.findMatches()).thenReturn(matches);
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
    public void given_matchesWithSameScores_when_getSummary_then_orderedSummaryGotten() throws InterruptedException {
        List<Match> matches = new ArrayList<>();
        matches.add(new Match("Mexico", "Canada",0,5, Instant.now(), 5));
        matches.add(new Match(SPAIN, "Brazil",10,2, Instant.now(), 12));
        matches.add(new Match("Germany", "France",2,2, Instant.now(), 4));
        Thread.sleep(1);
        matches.add(new Match("Uruguay", ITALY,6,6, Instant.now(), 12));
        matches.add(new Match("Argentina", "Australia",3,1, Instant.now(), 4));
        when(matchStore.findMatches()).thenReturn(matches);

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
