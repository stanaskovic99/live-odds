package com.liveodds.service;

import com.liveodds.utils.Validator;

public final class ScoreboardFactory {
    public static ScoreboardService createDefaultScoreboard() {
        return new ScoreboardServiceImpl(new Validator(), new HashMapMatchStore());
    }
}
