package meetem.playermerchant;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;

public final class Util {
    private static void giveItemsPrivate(Player player, ItemStack item){
        if(player.isDead()){
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            return;
        }

        HashMap<Integer, ItemStack> notFited = player.getInventory().addItem(item);
        for(Integer nf : notFited.keySet()){
            ItemStack nfItem = notFited.get(nf);
            player.getWorld().dropItemNaturally(player.getLocation(), nfItem);
        }
    }

    public static String capitalize(String s){
        if(s == null)
            return "";

        if(s.length() < 2)
            return s.toUpperCase();

        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    public static String reprintMaterial(String materialName){
        String[] parts = materialName.split("_");
        StringBuilder output = new StringBuilder();
        for(int i = 0;i<parts.length;i++){
            output.append(capitalize(parts[i]));
            if(i < parts.length - 1)
                output.append(" ");
        }

        return output.toString();
    }

    public static String prettyPrintItem(ItemStack stack){
        if(stack == null || stack.getType() == Material.AIR)
            return "empty";

        return String.format("%s x%d", reprintMaterial(stack.getType().toString()), stack.getAmount());
    }

    public static String prettyPrintItemPrice(ItemStack stack){
        if(stack == null || stack.getType() == Material.AIR)
            return "Nothing";

        return String.format("%d %s", stack.getAmount(), reprintMaterial(stack.getType().toString()));
    }

    /**
     * Gives item to player, also checking if some not fitted -- dropping them
     * If player is dead, item will be dropped.
     * @param player
     * @param item
     */
    public static void giveItems(Player player, ItemStack item){
        giveItems(player, item, false);
    }

    /**
     * Gives item to player, also checking if some not fitted -- dropping them
     * If player is dead, item will be dropped.
     * @param player
     * @param item
     * @param delayed if true, call will be delayed by 1 tick to be sure player dead flag is correct. Useful to call from events.
     */
    public static void giveItems(Player player, ItemStack item, boolean delayed){
        if(delayed){
            Bukkit.getScheduler().scheduleSyncDelayedTask(Common.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    giveItemsPrivate(player, item);
                }
            }, 1);
        }else{
            giveItemsPrivate(player, item);
        }
    }

    public static void sendErrorMessage(Player p, String message, Object ... args){
        sendErrorMessage(p, String.format(message, args));
    }

    public static void sendErrorMessage(Player p, String message){
        p.sendMessage(String.format("%s%s%s",
                ChatColor.RED, ChatColor.BOLD, message));
    }
}
