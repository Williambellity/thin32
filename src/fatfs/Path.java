package fatfs;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Store a file or directory name as a byte arrays representing the name and an
 * optional extension. The filename is stored in an array of length 8 which
 * contains the name of file with 0x20 characters at the end (or clamped if
 * longer). Similarly, the extension is stored in an array of length 3.
 */
class PathElement {
    public byte[] getName() {
        // TODO
        return null;
    }

    public byte[] getExtension() {
        // TODO
        return null;
    }

    public String toString() {
        // TODO
        return null;
    }

    /**
     * Generate a byte array with the string encoded with one byte per character. If
     * the string is shorter than maxSize byte, appends 0x20 (space) characters at
     * the end, if it is longer clamp it.
     *
     * @param s       string to be converted
     * @param maxSize maximal size and length of returned array
     * @return the new representation
     */
    static byte[] getBytename(String s, int maxSize) {
        byte[] bytename = Arrays.copyOfRange(StandardCharsets.UTF_8.encode(s).array(), 0, maxSize);
        int last = maxSize - 1;
        while (last >= 0 && bytename[last] == 0x00) {
            bytename[last--] = 0x20;
        }
        return bytename;
    }

    static String getStringName(ByteBuffer name, ByteBuffer ext) {
        // TODO could remove trailing spaces !
        return StandardCharsets.UTF_8.decode(name) + "." + StandardCharsets.UTF_8.decode(ext);
    }

    @Override
    public boolean equals(Object pathElement) {
        // TODO
        return true;
    }
}

public class Path {
    Path(String s) {

    }

    int numElement() {
        // TODO
        return 1;
    }

    PathElement element(int i) {
        // TODO
        return null;
    }

    PathElement filename() {
        // TODO
        return null;
    }

    boolean isAbsolute() {
        // TODO
        return true;
    }

    @Override
    public String toString() {
        // TODO
        return "";
    }
}
