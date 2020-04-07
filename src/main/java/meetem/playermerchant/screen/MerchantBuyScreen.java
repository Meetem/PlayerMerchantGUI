package meetem.playermerchant.screen;

import meetem.playermerchant.Common;
import meetem.playermerchant.ItemStackOffer;
import meetem.playermerchant.MerchantOffer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class MerchantBuyScreen implements Listener {
    private Merchant merchantScreen;
    private Player player;

    public MerchantBuyScreen(Player player, String title) {
        this.merchantScreen = Bukkit.createMerchant(title);
        this.player = player;
    }

    private MerchantRecipe constructRecipe(MerchantOffer offer) {
        ItemStack markedItem = ItemStackOffer.markItem(player, offer, offer.getResultItems().get(0), true);
        MerchantRecipe recipe = new MerchantRecipe(markedItem, 0, 1, false);

        for (ItemStack itemStack : offer.getPriceItems()) {
            recipe.addIngredient(itemStack);
        }

        recipe.setUses(0);
        return recipe;
    }

    public void setup(List<MerchantOffer> offers) {
        ArrayList<MerchantRecipe> recipes = new ArrayList<>();

        for (MerchantOffer offer : offers) {
            if (offer.isExpired())
                continue;

            recipes.add(constructRecipe(offer));
        }

        this.merchantScreen.setRecipes(recipes);
    }

    public void show() {
        InventoryView view = player.openMerchant(merchantScreen, true);
        Bukkit.getPluginManager().registerEvents(new MerchantLoreRemoveHandler(view), Common.getPlugin());
    }
}
