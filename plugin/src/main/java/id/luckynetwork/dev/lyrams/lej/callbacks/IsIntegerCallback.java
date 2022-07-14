package id.luckynetwork.dev.lyrams.lej.callbacks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class IsIntegerCallback {

    private boolean isInteger = false;
    private int value = -1;

    public IsIntegerCallback setValue(int value) {
        this.value = value;
        return this;
    }

    public IsIntegerCallback setInteger(boolean isInteger) {
        this.isInteger = isInteger;
        return this;
    }
}
