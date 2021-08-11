package id.luckynetwork.dev.lyrams.lej.enums;

import lombok.Getter;

public enum TrueFalseType {

    ON_OFF("On", "Off"),
    DEFAULT("True", "False");

    @Getter
    String ifTrue, ifFalse;

    TrueFalseType(String ifTrue, String ifFalse) {
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }
}
