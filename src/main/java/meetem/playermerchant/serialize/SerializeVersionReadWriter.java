package meetem.playermerchant.serialize;

import java.io.*;

public final class SerializeVersionReadWriter {
    public static void writeVersion(OutputStream stream, SerializeVersion version) throws IOException {
        writeVersion(StreamAdapter.adapt(stream), version);
    }

    public static void writeVersion(ObjectOutputStream stream, SerializeVersion version) throws IOException {
        if(version == null)
            return;

        stream.writeInt(version.getMajor());
        stream.writeInt(version.getMinor());
        stream.flush();
    }

    public static SerializeVersion readVersion(InputStream stream) throws IOException {
        return readVersion(StreamAdapter.adapt(stream));
    }

    public static SerializeVersion readVersion(ObjectInputStream stream) throws IOException {
        return new SerializeVersion(stream.readInt(), stream.readInt());
    }
}
