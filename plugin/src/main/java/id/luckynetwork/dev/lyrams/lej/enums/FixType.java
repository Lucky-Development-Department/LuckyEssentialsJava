package id.luckynetwork.dev.lyrams.lej.enums;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum FixType {
    ALL("inventory items"),
    HAND("item in hand"),
    ARMOR("armor contents"),
    UNKNOWN("");

    @Getter
    private final String display;

    FixType(String display) {
        this.display = display;
    }

    public static FixType getType(@Nullable String input) {
        if (input == null) {
            return FixType.HAND;
        }

        String uppercaseInput = input.toUpperCase();
        if (Arrays.stream(FixType.values()).anyMatch(it -> it.toString().equals(uppercaseInput))) {
            return FixType.valueOf(uppercaseInput);
        }

        switch (uppercaseInput) {
            case "A":
            case "*":
            case "**": {
                return FixType.ALL;
            }

            case "H":
            case "THIS": {
                return FixType.HAND;
            }

            case "AR": {
                return FixType.ARMOR;
            }

            default: {
                return FixType.UNKNOWN;
            }
        }
    }

}
