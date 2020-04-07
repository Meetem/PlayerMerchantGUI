package meetem.playermerchant;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class Config {
    private static Config _instance;
    private ConfigData loadedData;
    private YamlConfiguration configuration;
    private Plugin plugin;

    private File getConfigFile(){
        return new File(Paths.get(plugin.getDataFolder().getAbsolutePath(), "config.yml").toString());
    }

    private Config(Plugin p){
        this.plugin = p;
        configuration = YamlConfiguration.loadConfiguration(getConfigFile());

        ConfigData defaultConfig = new ConfigData();
        loadedData = new ConfigData();

        List<ItemStack> priceItems = (List<ItemStack>) configuration.getList("config.priceItems", new ArrayList<ItemStack>());
        if(priceItems == null || priceItems.size() <= 0){
            priceItems = Arrays.asList(defaultConfig.priceItems);
        }

        loadedData.priceItems = priceItems.toArray(new ItemStack[0]);
        configuration.set("config.priceItems", loadedData.priceItems);

        try {
            configuration.save(getConfigFile());
        } catch (IOException e) {
            Common.getLogger().severe(String.format("Unable to save config %s\n", getConfigFile().getAbsolutePath()));
            Common.printException(Level.SEVERE, e);
        }
    }

    public static ConfigData getConfig(){
        return _instance.loadedData;
    }

    public static void enable(Plugin p){
        _instance = new Config(p);
    }

    public static void disable(){
        _instance = null;
    }
}
