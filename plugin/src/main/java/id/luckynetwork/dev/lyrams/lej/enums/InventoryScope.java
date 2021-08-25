package id.luckynetwork.dev.lyrams.lej.enums;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum InventoryScope {
    ALL("inventory items", null),
    HAND("item in hand", null),
    ARMOR("armor contents", null),
    SPECIFIC("", new ItemStack(Material.AIR)),
    UNKNOWN("", null);

    @Getter
    @Setter
    private String display;
    @Getter
    @Setter
    private ItemStack itemStack;

    InventoryScope(String display, ItemStack itemStack) {
        this.display = display;
        this.itemStack = itemStack;
    }

    public static InventoryScope getType(@Nullable String input) {
        if (input == null) {
            return InventoryScope.HAND;
        }

        String uppercaseInput = input.toUpperCase();
        if (Arrays.stream(InventoryScope.values()).anyMatch(it -> it.toString().equals(uppercaseInput))) {
            return InventoryScope.valueOf(uppercaseInput);
        }

        switch (uppercaseInput) {
            case "A":
            case "*":
            case "**": {
                return InventoryScope.ALL;
            }

            case "H":
            case "THIS": {
                return InventoryScope.HAND;
            }

            case "AR": {
                return InventoryScope.ARMOR;
            }

            default: {
                ItemStack item;
                if (uppercaseInput.contains(":")) {
                    item = LuckyEssentials.getInstance().getVersionSupport().getItemByName(uppercaseInput.split(":")[0], 1, Integer.parseInt(uppercaseInput.split(":")[1]));
                } else {
                    item = LuckyEssentials.getInstance().getVersionSupport().getItemByName(uppercaseInput, 1, 0);
                }

                if (item == null) {
                    return InventoryScope.UNKNOWN;
                }

                InventoryScope scope = InventoryScope.SPECIFIC;
                scope.setDisplay(item.getType().toString());
                scope.setItemStack(item);
                return scope;
            }
        }
    }

}
