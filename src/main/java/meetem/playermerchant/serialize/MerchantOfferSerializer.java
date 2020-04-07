package meetem.playermerchant.serialize;

import meetem.playermerchant.MerchantOffer;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class MerchantOfferSerializer {
    public static void writeOffer(OutputStream outputStream, MerchantOffer offer) throws IOException {
        ObjectOutputStream adaptedStream = StreamAdapter.adapt(outputStream);
        SerializeVersionReadWriter.writeVersion(adaptedStream, MerchantOffer.OfferVersion);

        UUID id = offer.getOfferId();
        adaptedStream.writeLong(id.getMostSignificantBits());
        adaptedStream.writeLong(id.getLeastSignificantBits());

        adaptedStream.writeInt(offer.getEnvironment().getId());

        ItemStackSerializer.writeStack(adaptedStream, offer.getResultItems().toArray(new ItemStack[0]));
        ItemStackSerializer.writeStack(adaptedStream, offer.getPriceItems().toArray(new ItemStack[0]));
    }

    public static MerchantOffer readOffer(InputStream inputStream) throws IOException {
        ObjectInputStream adaptedStream = StreamAdapter.adapt(inputStream);
        SerializeVersion version = SerializeVersionReadWriter.readVersion(adaptedStream);

        long most = adaptedStream.readLong();
        long least = adaptedStream.readLong();
        UUID offerId = new UUID(most, least);

        World.Environment env = World.Environment.getEnvironment(adaptedStream.readInt());

        ArrayList<ItemStack> offer = new ArrayList<ItemStack>(Arrays.asList(ItemStackSerializer.readItemStack(adaptedStream)));
        ArrayList<ItemStack> price = new ArrayList<ItemStack>(Arrays.asList(ItemStackSerializer.readItemStack(adaptedStream)));
        return new MerchantOffer(env, offer, price, null, offerId);
    }
}
