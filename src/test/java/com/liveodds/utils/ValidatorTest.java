package com.liveodds.utils;

import com.liveodds.exception.NonExistingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        validator = new Validator();
    }

    @Test
    public void given_correctTeam_when_validateTeam_then_normalizedTeamName() {
        Assertions.assertEquals("France", validator.validateTeam("France"));
        Assertions.assertEquals("France", validator.validateTeam("france"));
        Assertions.assertEquals("France", validator.validateTeam(" frAnce"));
        Assertions.assertEquals("France", validator.validateTeam("fRance   "));
        Assertions.assertEquals("United Kingdom", validator.validateTeam("united  kingdom   "));
    }

    @Test
    public void given_nullTeam_when_validateTeam_then_correctExceptionThrown() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateTeam(null));
        Assertions.assertEquals("Team name cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void given_emptyTeam_when_validateTeam_then_correctExceptionThrown() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateTeam(""));
        Assertions.assertEquals("Team name cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void given_blankTeam_when_validateTeam_then_correctExceptionThrown() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateTeam(" "));
        Assertions.assertEquals("Team name cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void given_wrongTeam_when_validateTeam_then_correctExceptionThrown() {
        NonExistingException exception = Assertions.assertThrows(NonExistingException.class, () -> validator.validateTeam("Sara"));
        Assertions.assertEquals("Team Sara does not exist.", exception.getMessage());
    }

    @Test
    public void given_correctScore_when_validateScore_then_noExceptionThrown() {
        Assertions.assertDoesNotThrow(() -> validator.validateScore(2));
        Assertions.assertDoesNotThrow(() -> validator.validateScore(0));
    }

    @Test
    public void given_wrongScore_when_validateScore_then_noExceptionThrown() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateScore(-2));
        Assertions.assertEquals("Score cannot be negative.", exception.getMessage());
    }
}
