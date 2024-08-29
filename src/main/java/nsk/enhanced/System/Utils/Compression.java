package nsk.enhanced.System.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * The {@link Compression} class provides utility methods to {@link Compression#compress(String)} and {@link Compression#decompress(byte[])} strings
 * using the GZIP compression algorithm. It is useful for reducing the size of string data
 * that needs to be stored or transmitted.
 */
public class Compression {

    /**
     * Compresses a string using GZIP compression.
     *
     * @param str the string to compress; can be null or empty
     * @return a byte array containing the compressed data, or null if the input string is null or empty
     * @throws IOException if an I/O error occurs during compression
     */
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

    /**
     * Decompresses a GZIP-compressed byte array back into a string.
     *
     * @param compressed the byte array containing the compressed data; can be null or empty
     * @return the decompressed string, or null if the input byte array is null or empty
     * @throws IOException if an I/O error occurs during decompression
     */
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
