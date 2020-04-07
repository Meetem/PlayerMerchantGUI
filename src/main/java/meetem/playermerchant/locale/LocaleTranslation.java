package meetem.playermerchant.locale;

import meetem.playermerchant.Common;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class LocaleTranslation {
    private YamlConfiguration configuration;
    private HashMap<String, String> localized = new HashMap<>();
    private String localeName;

    public String getLocaleName(){
        return localeName;
    }

    private String replaceColors(String ofString){
        ChatColor[] colors = ChatColor.values();

        for(ChatColor color : colors){
            String colorName = color.name().toUpperCase();
            ofString = ofString.replace("$c" + colorName, color.toString());
        }

        ofString = ofString.replace("$$", "$");
        return ofString;
    }

    private LocaleTranslation(YamlConfiguration configuration, String localeName){
        this.configuration = configuration;
        this.localeName = localeName;

        if(this.configuration == null)
            return;

        int numKeys = 0;
        for(String key : this.configuration.getKeys(false)){
            String value = this.configuration.getString(key, null);
            if(value == null)
                continue;

            key = key.toLowerCase();
            value = replaceColors(value);

            //Common.getLogger().info(String.format("Loaded %s -> \"%s\"", key, value));
            localized.put(key, value);
            numKeys++;
        }

        Common.getLogger().info(String.format("Loaded local %s with %d valid keys", localeName, numKeys));
    }

    public String getLocalized(String key, String def){
        return localized.getOrDefault(key, def);
    }

    public static LocaleTranslation load(File dataFolder, String locale){
        locale = locale.toLowerCase();

        Path filePath = Paths.get(dataFolder.getAbsolutePath(), "Locales/", locale + ".yml");
        File localeFile = new File(filePath.toString());

        if(!localeFile.exists()){
            Common.getLogger().severe(String.format("No locale file at %s", localeFile.getAbsolutePath()));
            return new LocaleTranslation(null, locale);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(localeFile);
        return new LocaleTranslation(config, locale);
    }
}
