package meetem.playermerchant.serialize;

import meetem.playermerchant.Common;
import meetem.playermerchant.PlayerMerchantData;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public final class MerchantStorageRW {
    public static @NotNull ArrayList<PlayerMerchantData> loadFiles(Path filesDirectory) throws IOException {
        ArrayList<PlayerMerchantData> merchants = new ArrayList<>();

        Common.getLogger().info(String.format("Enumerating data dir %s\n", filesDirectory.toString()));
        Stream<Path> files = Files.walk(filesDirectory, FileVisitOption.FOLLOW_LINKS);
        files.forEach((path -> {
            Common.getLogger().info(String.format("Enumerating file: %s\n", path.toAbsolutePath()));
            File fp = path.toAbsolutePath().toFile();
            if(fp.isDirectory())
                return;

            Common.getLogger().info(String.format("Loading merchant data %s\n", fp.getAbsolutePath()));
            try{
                PlayerMerchantData readData = MerchantDataFileRW.readMerchant(fp);
                merchants.add(readData);
            }catch(Exception e){
                Common.printException(e);
            }
        }));

        return merchants;
    }

    public static void saveFiles(Path filesDirectory, @NotNull ArrayList<PlayerMerchantData> storage, boolean dirtyOnly){
        for (PlayerMerchantData merchant : storage) {
            if(dirtyOnly && !merchant.isDirty())
                continue;

            try{
                File fp = new File(Paths.get(filesDirectory.toAbsolutePath().toString(), merchant.owner.getUniqueId().toString() + ".pmd").toString());
                if(!fp.exists())
                    fp.createNewFile();

                FileOutputStream outputStream = new FileOutputStream(fp, false);
                MerchantDataFileRW.writeMerchant(outputStream, merchant);
                outputStream.flush();
                outputStream.close();
                merchant.flush();
            }catch (Exception e){
                Common.getLogger().warning(String.format("Error on writing %s merchants\n", merchant.owner.getUniqueId().toString()));
                Common.printException(e);
                //merchant.flush();
            }
        }
    }
}
