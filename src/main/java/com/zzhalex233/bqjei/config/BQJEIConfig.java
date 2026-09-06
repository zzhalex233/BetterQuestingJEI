package com.zzhalex233.bqjei.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public final class BQJEIConfig {
    private static final String CATEGORY_BQJEI = "bqjei";

    private static Configuration config;

    public static boolean showHiddenQuestInfo = false;

    private BQJEIConfig() {
    }

    public static Configuration getConfig() {
        return config;
    }

    public static void init(File configFile) {
        config = new Configuration(configFile, true);
        sync();
    }

    public static void sync() {
        if (config == null) {
            return;
        }

        config.setCategoryLanguageKey(CATEGORY_BQJEI, "bqjei.config.category");

        showHiddenQuestInfo = config.getBoolean(
                "showHiddenQuestInfo",
                CATEGORY_BQJEI,
                false,
                "Show synced Better Questing quests in JEI even when their prerequisites are not completed or their BQ visibility would normally hide them.",
                "bqjei.config.show_hidden_quest_info"
        );

        if (config.hasChanged()) {
            config.save();
        }
    }
}
