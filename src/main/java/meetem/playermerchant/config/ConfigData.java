package meetem.playermerchant.config;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ConfigData {
    private static ItemStack getStack(Material material){
        return new ItemStack(material, material.getMaxStackSize());
    }

    public ConfigPromptFormat sellPromptFormat = ConfigPromptFormat.All;

    public ItemStack[] priceItems = new ItemStack[]{
            getStack(Material.ELYTRA),
            getStack(Material.DIAMOND),
            getStack(Material.DIAMOND_BLOCK),
            getStack(Material.ENCHANTED_GOLDEN_APPLE),
            getStack(Material.GOLDEN_APPLE),
            getStack(Material.GOLDEN_CARROT),
            getStack(Material.DIAMOND_PICKAXE),
            getStack(Material.DIAMOND_SWORD),
            getStack(Material.DIAMOND_CHESTPLATE),
            getStack(Material.DIAMOND_BOOTS),
            getStack(Material.DIAMOND_HELMET),
            getStack(Material.DIAMOND_LEGGINGS),
            getStack(Material.DIAMOND_HORSE_ARMOR),
            getStack(Material.TRIDENT),
            getStack(Material.FIREWORK_ROCKET),
            getStack(Material.FIREWORK_STAR),
            getStack(Material.EMERALD),
            getStack(Material.EMERALD_BLOCK),
            getStack(Material.GOLD_INGOT),
            getStack(Material.GOLD_BLOCK),
            getStack(Material.GLASS),
            getStack(Material.GLASS_BOTTLE),
            getStack(Material.ARROW),
            getStack(Material.SPECTRAL_ARROW),
            getStack(Material.TIPPED_ARROW),
            getStack(Material.PRISMARINE),
            getStack(Material.BOOK),
            getStack(Material.BOOKSHELF),
            getStack(Material.PAPER),
            getStack(Material.APPLE),
            getStack(Material.CAKE),
            getStack(Material.GUNPOWDER),
            getStack(Material.SUGAR),
            getStack(Material.SUGAR_CANE),
            getStack(Material.WHEAT),
            getStack(Material.WHEAT_SEEDS),
            getStack(Material.BREAD),
            getStack(Material.INK_SAC),
            getStack(Material.JUKEBOX),
            getStack(Material.KNOWLEDGE_BOOK),
            getStack(Material.GLOWSTONE),
            getStack(Material.GLOWSTONE_DUST),
            getStack(Material.LAPIS_LAZULI),
            getStack(Material.LAPIS_ORE),
            getStack(Material.LAPIS_BLOCK),
            getStack(Material.LEATHER),
            getStack(Material.MELON),
            getStack(Material.MILK_BUCKET),
            getStack(Material.MUTTON),
            getStack(Material.ACACIA_LOG),
            getStack(Material.BIRCH_LOG),
            getStack(Material.DARK_OAK_LOG),
            getStack(Material.JUNGLE_LOG),
            getStack(Material.OAK_LOG),
            getStack(Material.SPRUCE_LOG),
    };
    
}
