package meetem.playermerchant.locale;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class Localization {
    private static Localization _instance;
    private HashMap<String, LocaleTranslation> translations = new HashMap<>();
    private LocaleTranslation defaultLocale;
    private Plugin plugin;

    public static Localization getInstance(){
        return _instance;
    }

    private Localization(Plugin plugin){
        this.plugin = plugin;
    }

    private void load(){
        defaultLocale = LocaleTranslation.load(plugin.getDataFolder(), "en");
        translations.put("en", defaultLocale);
    }

    public static void printLocalized(Player p, String key, Object ... args){
        if(p == null)
            return;

        p.sendMessage(getLocalized(p, key, args));
    }

    public static void broadcastLocalized(String locale, String key, Object ... args){
        Bukkit.broadcastMessage(getLocalized(locale, key, args));
    }

    public static <E extends Enum<E>> void printAllLocalized(E key, Object ... args){
        printAllLocalized(key.toString(), args);
    }

    public static void printAllLocalized(String key, Object ... args){
        for(Player p : Bukkit.getOnlinePlayers()){
            if(p == null)
                continue;

            printLocalized(p, key, args);
        }
    }

    public static <E extends Enum<E>> void printLocalized(Player p, E key, Object ... args){
        if(p == null)
            return;

        p.sendMessage(getLocalized(p, key, args));
    }

    public static <E extends Enum<E>> String getLocalized(@Nullable Player player, E key, Object ... args){
        String locale = "en";
        if(player != null)
            locale = player.getLocale();

        return getLocalized(locale, key, args);
    }

    public static <E extends Enum<E>> String getLocalized(String locale, E key, Object ... args){
        return getLocalized(locale, key.toString(), args);
    }

    public static String getLocalized(Player player, String key, Object ... args){
        String locale = "en";
        if(player != null)
            locale = player.getLocale();

        return getLocalized(locale, key, args);
    }

    public static String getLocalized(String localeName, String key, Object ... args){
        return _instance.getLocalizedFormatted(localeName, key, args);
    }

    private LocaleTranslation getOrLoadLocale(String localeName){
        localeName = localeName.toLowerCase();
        if(!translations.containsKey(localeName)){
            translations.put(localeName, LocaleTranslation.load(plugin.getDataFolder(), localeName));
        }

        return translations.getOrDefault(localeName, defaultLocale);
    }

    private String getLocalizedFormatted(String localeName, String key, Object ... args) {
        LocaleTranslation translation = getOrLoadLocale(localeName);

        key = key.toLowerCase();
        String localized = translation.getLocalized(key, defaultLocale.getLocalized(key, "NOT_FOUND_KEY " + key));
        return String.format(localized, args);
    }

    public static void enable(Plugin p){
        _instance = new Localization(p);
        _instance.load();
    }

    public static void disable(){
        _instance = null;
    }
}
