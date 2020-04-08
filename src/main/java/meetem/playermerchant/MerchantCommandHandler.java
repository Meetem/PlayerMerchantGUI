package meetem.playermerchant;

import meetem.playermerchant.screen.MerchantBuyScreen;
import meetem.playermerchant.transactions.BuyTransaction;
import meetem.playermerchant.transactions.ManageTransaction;
import meetem.playermerchant.transactions.OfferTransaction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class MerchantCommandHandler implements CommandExecutor, Listener {
    private PlayerMerchant plugin;

    public MerchantCommandHandler(PlayerMerchant _plugin) {
        this.plugin = _plugin;
    }

    private void promptInvalid(Player player) {
        player.sendMessage("Available arguments are:\n/merchant sell to post offer\n/merchant buy to see offers\n/merchant list Manage your offers");
    }

    private boolean listBuy(Player player) {
        BuyTransaction buyTransaction = new BuyTransaction(player);
        buyTransaction.start();
        return true;
    }

    private boolean listSell(Player player) {
        //System.out.println("Ok, sell");
        OfferTransaction currentSell = new OfferTransaction(player);
        currentSell.start();
        return true;
    }

    private boolean test1(Player player) {
        return true;
    }

    private boolean test2(Player player, String arg) {
        return true;
    }

    private boolean clearList(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        container.remove(Common.getPlayerOffersDataKey());
        player.sendMessage("Container cleared");
        return true;
    }

    private boolean listManaging(Player player) {
        ManageTransaction manage = new ManageTransaction(player);
        manage.start();
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Merchant is only available for players :c");
            return true;
        }

        Player sender = (Player) commandSender;
        if (args.length < 1) {
            promptInvalid(sender);
            return true;
        }

        String cmd = args[0];
        if (cmd.equalsIgnoreCase("buy")) {
            return listBuy(sender);
        } else if (cmd.equalsIgnoreCase("sell")) {
            return listSell(sender);
        } else if (cmd.equalsIgnoreCase("list")) {
            return listManaging(sender);
        } else if (cmd.equalsIgnoreCase("clear")) {
            return clearList(sender);
        } else if (cmd.equalsIgnoreCase("test1")) {
            return test1(sender);
        } else if (cmd.equalsIgnoreCase("test2")) {
            return test2(sender, (args.length > 1) ? args[1] : null);
        }

        promptInvalid(sender);
        return false;
    }
}