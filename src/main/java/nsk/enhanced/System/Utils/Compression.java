package nsk.enhanced.System.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Compression {

    public static byte[] compress(String str) throws IOException {
        if (str == null || str.isEmpty()) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            gzipOutputStream.write(str.getBytes(StandardCharsets.UTF_8));
        }
        return byteArrayOutputStream.toByteArray();
    }


    public static String decompress(byte[] compressed) throws IOException {
        if (compressed == null || compressed.length == 0) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
            byte[] buffer = new byte[256];
            int len;
            while ((len = gzipInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
        }
        return byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());
    }

}
