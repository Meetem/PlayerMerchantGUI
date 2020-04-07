package meetem.playermerchant;
import org.bukkit.NamespacedKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Common {
    private NamespacedKey sellerUUIDKey;
    private NamespacedKey sellItemKey;
    private NamespacedKey offerUUIDKey;
    private NamespacedKey playerOffersDataKey;
    private PlayerMerchant _plugin;
    private static Common _instance;

    public static Common getInstance(){
        return _instance;
    }

    public static Logger getLogger(){ return _instance._plugin.getLogger(); }

    public static PlayerMerchant getPlugin() {return _instance._plugin;}

    public static void printException(Exception e){
        printException(Level.SEVERE, e);
    }

    public static void printException(Level logLevel, Exception e){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(outputStream);
        e.printStackTrace(ps);

        ps.flush();
        try {
            outputStream.flush();
        } catch (IOException ignored) {
        }

        String trace = outputStream.toString();
        try {
            outputStream.close();
        } catch (IOException ignored) {
        }

        getLogger().log(logLevel, trace);
    }

    public static NamespacedKey getSellerUUIDKey(){
        return _instance.sellerUUIDKey;
    }

    public static NamespacedKey getSellItemKey(){
        return _instance.sellItemKey;
    }

    public static NamespacedKey getOfferUUIDKey(){
        return _instance.offerUUIDKey;
    }

    public static NamespacedKey getPlayerOffersDataKey(){
        return _instance.playerOffersDataKey;
    }

    static void disable(){
        _instance = null;
    }

    static void enable(PlayerMerchant plugin){
        new Common(plugin);
    }

    public Common(PlayerMerchant plugin){
        _instance = this;
        _plugin = plugin;

        sellerUUIDKey = new NamespacedKey(_plugin, "item-seller-uuid");
        sellItemKey = new NamespacedKey(_plugin, "item-is-for-sale");
        offerUUIDKey = new NamespacedKey(_plugin, "item-offer-uuid");
        playerOffersDataKey = new NamespacedKey(_plugin, "player-merchant-data");
    }
}
