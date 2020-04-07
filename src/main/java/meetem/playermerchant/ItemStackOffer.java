package meetem.playermerchant;

import meetem.playermerchant.locale.Localization;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ItemStackOffer {
    public static @NotNull ItemStack markItem(@Nullable Player playerForLocale, @NotNull MerchantOffer offer, @NotNull ItemStack item, boolean showSellerName){
        ItemStack result = item.clone();
        ItemMeta meta = result.getItemMeta();
        if(meta == null)
            meta = Bukkit.getItemFactory().getItemMeta(result.getType());

        if(showSellerName){
            List<String> lore = meta.getLore();
            if(lore == null)
                lore = new ArrayList<String>();

            lore.add(Localization.getLocalized(playerForLocale, LocaleKeys.SellerNameLore, offer.getOwner().getName()));
            meta.setLore(lore);
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(Common.getSellerUUIDKey(), PersistentDataType.STRING, offer.getOwner().getUniqueId().toString());
        container.set(Common.getOfferUUIDKey(), PersistentDataType.STRING, offer.getOfferId().toString());

        result.setItemMeta(meta);
        return result;
    }

    public static @NotNull MerchantOfferKey getOfferKeyFromItem(@NotNull ItemStack item){
        ItemMeta meta = item.getItemMeta();
        if(meta == null)
            return MerchantOfferKey.empty();

        PersistentDataContainer c = meta.getPersistentDataContainer();
        String sellerIdStr = c.getOrDefault(Common.getSellerUUIDKey(), PersistentDataType.STRING, "");
        String offerIdStr = c.getOrDefault(Common.getOfferUUIDKey(), PersistentDataType.STRING, "");

        //Not merchantable item
        if(sellerIdStr.length() <= 0 || offerIdStr.length() <= 0)
            return MerchantOfferKey.empty();

        try{
            UUID sellerId = UUID.fromString(sellerIdStr);
            UUID offerId = UUID.fromString(offerIdStr);
            return new MerchantOfferKey(sellerId, offerId);
        }catch (Exception e){
            Common.getLogger().warning(String.format("Unable to get offer from item %s\n", item.toString()));
            Common.printException(e);
        }

        return MerchantOfferKey.empty();
    }

    public static void unmarkItem(@NotNull ItemStack item, boolean showSellerName){
        ItemMeta meta = item.getItemMeta();
        if(meta == null)
            return;

        PersistentDataContainer c = meta.getPersistentDataContainer();

        try{
            UUID sellerId = UUID.fromString(c.getOrDefault(Common.getSellerUUIDKey(), PersistentDataType.STRING, ""));
            UUID offerId = UUID.fromString(c.getOrDefault(Common.getOfferUUIDKey(), PersistentDataType.STRING, ""));

            if(showSellerName){
                List<String> lore = meta.getLore();
                if(lore != null && lore.size() > 0){
                    lore.remove(lore.size() - 1);
                    meta.setLore(lore);
                }
            }

            c.remove(Common.getSellerUUIDKey());
            c.remove(Common.getOfferUUIDKey());
            item.setItemMeta(meta);
        }catch (Exception e){
            Common.getLogger().warning(String.format("Unable to unmark offer item %s\n", item.toString()));
            Common.printException(e);
        }
    }
}
