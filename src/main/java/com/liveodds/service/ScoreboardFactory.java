package com.liveodds.service;

import com.liveodds.utils.Validator;

public final class ScoreboardFactory {
    public static ScoreboardService createDefaultScoreboard() {
        return new ScoreboardServiceImpl(new Validator(), new HashMapMatchStore());
    }

    public static ScoreboardService createScoreboard(MatchStore matchStore) {
        return new ScoreboardServiceImpl(new Validator(), matchStore);
    }
}
