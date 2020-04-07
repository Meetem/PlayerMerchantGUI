package meetem.playermerchant.serialize;

import meetem.playermerchant.Common;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class ItemStackSerializer {
    public static void writeStack(OutputStream outputStream, ItemStack[] itemStacks){
        // serialize the object
        try {
            BukkitObjectOutputStream so = new BukkitObjectOutputStream(outputStream);
            so.writeObject(itemStacks);
            so.flush();
        } catch (Exception e) {
            Common.printException(e);
        }
    }

    public static byte[] writeStack(ItemStack[] itemStacks){
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            writeStack(bo, itemStacks);
            bo.close();
            return bo.toByteArray();
        } catch (Exception e) {
            Common.printException(e);
            return null;
        }
    }

    /**
     * @return The ItemStack Array obtained from stream
     */
    public static ItemStack[] readItemStack(byte[] input){
        // deserialize the object
        try {
            ByteArrayInputStream bi = new ByteArrayInputStream(input);
            ItemStack[] ret = readItemStack(bi);
            bi.close();
            return ret;
        } catch (Exception e) {
            Common.printException(e);
        }

        return new ItemStack[0];
    }

    /**
     * @return The ItemStack Array obtained from stream
     */
    public static @NotNull ItemStack[] readItemStack(InputStream inputStream){
        // deserialize the object
        try {
            BukkitObjectInputStream si = new BukkitObjectInputStream(inputStream);
            return (ItemStack[]) si.readObject();
        } catch (Exception e) {
            Common.printException(e);
        }

        return new ItemStack[0];
    }
}