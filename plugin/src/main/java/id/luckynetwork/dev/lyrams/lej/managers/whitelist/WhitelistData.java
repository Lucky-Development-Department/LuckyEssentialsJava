package id.luckynetwork.dev.lyrams.lej.managers.whitelist;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName = "newBuilder")
public class WhitelistData {
    private final String uuid, name;
}
