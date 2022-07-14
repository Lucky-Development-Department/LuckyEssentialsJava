package id.luckynetwork.dev.lyrams.lej.enums;

import lombok.Getter;

public enum TrollType {

    LAGBACK("LAGBACK", "Lagback"),
    ONE_TAP("ONETAP", "OneTap"),
    STICKY("STICKY", "Sticky"),
    NO_INTERACT("NOINTERACT", "NoInteract"),
    NO_PICKUP("NOPICKUP", "NoPickup"),
    NO_HIT("NOHIT", "NoHit"),
    NO_DAMAGE("NODAMAGE", "NoDamage"),
    NO_PLACE("NOPLACE", "NoPlace"),
    NO_BREAK("NOBREAK", "NoBreak"),
    FAKE_PLACE("FAKEPLACE", "FakePlace"),
    FAKE_BREAK("FAKEBREAK", "FakeBreak");

    @Getter
    private final String metadataKey, display;

    TrollType(String metadataKey, String display) {
        this.metadataKey = metadataKey;
        this.display = display;
    }
}
