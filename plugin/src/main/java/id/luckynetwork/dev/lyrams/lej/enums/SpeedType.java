package id.luckynetwork.dev.lyrams.lej.enums;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public enum SpeedType {
    WALKING(1, "walk"),
    FLYING(1, "flight"),
    UNKNOWN(0, "");

    @Getter
    @Setter
    private float speed;
    @Getter
    private final String display;

    SpeedType(int speed, String display) {
        this.speed = speed;
        this.display = display;
    }

    public static SpeedType getType(CommandSender sender, String input) {
        if (input == null) {
            return SpeedType.WALKING;
        }

        String uppercaseInput = input.toUpperCase();
        if (Arrays.stream(SpeedType.values()).anyMatch(it -> it.toString().equals(uppercaseInput))) {
            return SpeedType.valueOf(uppercaseInput);
        }

        switch (uppercaseInput) {
            case "WALK":
            case "W": {
                return SpeedType.WALKING;
            }

            case "FLY":
            case "FLIGHT":
            case "F": {
                return SpeedType.FLYING;
            }

            default: {
                SpeedType speedType = SpeedType.UNKNOWN;
                if (sender instanceof Player) {
                    if (((Player) sender).isFlying()) {
                        speedType = SpeedType.FLYING;
                    } else {
                        speedType = SpeedType.WALKING;
                    }

                    try {
                        int speed = Integer.parseInt(input);
                        speedType.setSpeed(speed);
                    } catch (Exception ignored) {
                    }
                }

                return speedType;
            }
        }
    }

}
