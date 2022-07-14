package id.luckynetwork.dev.lyrams.lej.enums;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum ToggleType {
    ON,
    OFF,
    TOGGLE,
    UNKNOWN;

    public static ToggleType getToggle(@Nullable String input) {
        if (input == null) {
            return ToggleType.TOGGLE;
        }

        String uppercaseInput = input.toUpperCase();
        if (Arrays.stream(ToggleType.values()).anyMatch(it -> it.toString().equals(uppercaseInput))) {
            return ToggleType.valueOf(uppercaseInput);
        }

        switch (uppercaseInput) {
            case "TRUE":
            case "T":
            case "YES":
            case "Y": {
                return ToggleType.ON;
            }

            case "FALSE":
            case "F":
            case "NO":
            case "N": {
                return ToggleType.OFF;
            }

            case "TOGGLE":
            case "SWITCH":
            case "": {
                return ToggleType.TOGGLE;
            }

            default: {
                return ToggleType.UNKNOWN;
            }
        }
    }

}
