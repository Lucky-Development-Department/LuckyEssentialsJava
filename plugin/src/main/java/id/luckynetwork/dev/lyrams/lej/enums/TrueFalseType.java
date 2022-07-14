package id.luckynetwork.dev.lyrams.lej.enums;

import lombok.Getter;
import lombok.Setter;

public enum TrueFalseType {

    ON_OFF("On", "Off"),
    DEFAULT("True", "False"),
    ENABLED("Enabled", "Disabled");

    @Getter
    @Setter
    String ifTrue, ifFalse;

    TrueFalseType(String ifTrue, String ifFalse) {
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }
}
