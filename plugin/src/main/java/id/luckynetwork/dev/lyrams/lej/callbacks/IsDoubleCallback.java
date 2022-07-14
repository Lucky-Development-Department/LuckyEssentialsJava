package id.luckynetwork.dev.lyrams.lej.callbacks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class IsDoubleCallback {

    private boolean isDouble = false;
    private double value = -1;

    public IsDoubleCallback setValue(double value) {
        this.value = value;
        return this;
    }

    public IsDoubleCallback setDouble(boolean isDouble) {
        this.isDouble = isDouble;
        return this;
    }
}
