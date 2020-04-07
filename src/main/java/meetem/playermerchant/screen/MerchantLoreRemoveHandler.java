package meetem.playermerchant.screen;

import meetem.playermerchant.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;

/***
 * Handles click (pickup) event over merchant script to remove additional seller name item lore.
 */
public class MerchantLoreRemoveHandler implements Listener {
    private InventoryView target;

    public MerchantLoreRemoveHandler(InventoryView targetInventory) {
        this.target = targetInventory;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClosed(InventoryCloseEvent event) {
        if (event.getView() != target)
            return;

        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemClicked(InventoryClickEvent event) {
        boolean isPickup = event.getAction() == InventoryAction.PICKUP_ALL
                || event.getAction() == InventoryAction.PICKUP_HALF
                || event.getAction() == InventoryAction.PICKUP_ONE
                || event.getAction() == InventoryAction.PICKUP_SOME;

        if (!isPickup) {
            return;
        }

        Inventory inv = event.getClickedInventory();
        if (inv == null)
            return;

        if (inv.getType() != InventoryType.MERCHANT)
            return;

        if (event.getView() != target)
            return;

        ItemStack item = event.getCurrentItem();
        if (item == null)
            return;

        MerchantOfferKey offerKey = ItemStackOffer.getOfferKeyFromItem(item);
        MerchantOffer offer = MerchantStorage.getInstance().getOffer(offerKey);

        boolean removed = false;
        if (offer != null) {
            removed = !offer.isExpired();
            removed &= MerchantStorage.getInstance().removeOffer(offer, true);
        }
        
        ItemStackOffer.unmarkItem(item, true);
        if (removed) {
            event.getView().getPlayer().sendMessage(String.format("%sDone :3 (%s, %s)\n", ChatColor.GREEN,
                    offer.getOwner().getUniqueId(),
                    offer.getOfferId()));
        } else {
            //Bukkit.broadcastMessage(String.format("Can't find offer %s %s\n", offerKey.getOwnerId(), offerKey.getOfferId()));
            event.getView().getPlayer().sendMessage(String.format("%sOffer is expired :c\n", ChatColor.RED));
            event.setCancelled(true);

            Bukkit.getScheduler().scheduleSyncDelayedTask(Common.getPlugin(), new Runnable() {
                public void run() {
                    event.getView().close();
                }
            }, 1);
        }
    }
}
