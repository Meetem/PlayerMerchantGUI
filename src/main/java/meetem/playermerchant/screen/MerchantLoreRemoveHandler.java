package meetem.playermerchant.screen;

import meetem.playermerchant.*;
import meetem.playermerchant.locale.Localization;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
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
        HumanEntity humanEntity = event.getView().getPlayer();
        Player player = null;
        if(humanEntity instanceof Player)
            player = (Player)humanEntity;

        if (removed) {
            if(player != null)
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
        } else {
            if(player != null){
                Localization.printLocalized(player, LocaleKeys.OfferExpiredError);
            }

            event.setCancelled(true);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Common.getPlugin(), new Runnable() {
                public void run() {
                    event.getView().close();
                }
            }, 1);
        }
    }
}
