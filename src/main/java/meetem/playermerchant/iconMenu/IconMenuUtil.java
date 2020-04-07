package meetem.playermerchant.iconMenu;

import org.bukkit.event.inventory.InventoryAction;

public final class IconMenuUtil {
    public static boolean IsPickup(InventoryAction action){
        return action == InventoryAction.PICKUP_ALL
                || action == InventoryAction.PICKUP_HALF
                || action == InventoryAction.PICKUP_ONE
                || action == InventoryAction.PICKUP_SOME;
    }

    public static boolean IsPlace(InventoryAction action){
        return action == InventoryAction.PLACE_SOME
                || action == InventoryAction.PLACE_ONE
                || action == InventoryAction.PLACE_ALL;
    }

    public static boolean IsDrop(InventoryAction action){
        return action == InventoryAction.DROP_ALL_CURSOR
                || action == InventoryAction.DROP_ALL_SLOT
                || action == InventoryAction.DROP_ONE_CURSOR
                || action == InventoryAction.DROP_ONE_SLOT;
    }
}
