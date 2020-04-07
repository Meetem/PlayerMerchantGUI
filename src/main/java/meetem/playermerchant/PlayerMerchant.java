package meetem.playermerchant;

import meetem.playermerchant.locale.Localization;
import meetem.playermerchant.screen.MerchantLoreRemoveHandler;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class PlayerMerchant extends JavaPlugin {
    private MerchantCommandHandler commandHandler;

    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "Loading PlayerMerchant");
        try {
            Common.enable(this);
            MerchantStorage.enable(this);
            Localization.enable(this);
            Config.enable(this);

            getCommand("merchant").setExecutor(new MerchantCommandHandler(this));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            getLogger().log(Level.SEVERE, "Loading PlayerMerchant FAILED");
            return;
        }

        getLogger().log(Level.INFO, "Loaded PlayerMerchant");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Unloading PlayerMerchant");
        commandHandler = null;

        MerchantStorage.disable();
        Common.disable();
        Localization.disable();
        Config.disable();
        getLogger().log(Level.INFO, "Unloaded PlayerMerchant");
    }
}
