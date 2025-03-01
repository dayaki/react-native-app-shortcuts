package com.appshortcuts;

public class BuildConfig {
    public static final boolean DEBUG = Boolean.parseBoolean("true");
    public static final boolean IS_NEW_ARCHITECTURE_ENABLED = Boolean.parseBoolean(
            System.getProperty("newArchEnabled", "false")
    );
}
