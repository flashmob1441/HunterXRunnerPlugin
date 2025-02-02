package org.flashmob.hunterXHunterPlugin.utils;

public enum Role {
    HUNTERS("Охотники"),
    RUNNERS("Спидранеры");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Role fromString(String input) {
        if (input == null) return null;
        return switch (input.toLowerCase()) {
            case "hunters", "охотники" -> HUNTERS;
            case "runners", "спидранеры" -> RUNNERS;
            default -> null;
        };
    }
}
