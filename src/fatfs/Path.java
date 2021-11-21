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

    byte[] b;

    PathElement(String s) {
        String[] table = s.split("\\.", 2);
        ByteBuffer content = ByteBuffer.allocate(11).put(PathElement.getBytename(table[0], 8), 0, 8);
        if (table.length == 2) {
            content.put(table[1].getBytes(), 0, 3);
        }
        this.b = content.array();
    }

    public byte[] getName() {
        return Arrays.copyOfRange(this.b, 0, 8);
    }

    public byte[] getExtension() {
        return Arrays.copyOfRange(this.b, 8, 11);
    }

    public String toString() {
        return getStringName(ByteBuffer.wrap(this.getName()), ByteBuffer.wrap(this.getExtension()));
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
        byte[] namearray = name.array();
        while (namearray[namearray.length - 1] == 0x20) {
            namearray = Arrays.copyOf(namearray, namearray.length - 1);
        }
        return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(namearray)) + "." + StandardCharsets.UTF_8.decode(ext);
    }

    public boolean equals(PathElement pathElement) {
        return Arrays.equals(this.b, pathElement.b);
    }
}

public class Path {

    private String[] path;
    private boolean isAbsolute;

    Path(String s) {
        this.path = s.split("/");
        this.isAbsolute = (s.charAt(0) == '/');
    }

    int numElement() {
        return this.path.length;
    }

    PathElement element(int i) {
        return new PathElement(this.path[i]);
    }

    PathElement filename() {
        return this.element(this.path.length - 1);
    }

    boolean isAbsolute() {
        return this.isAbsolute;
    }

    @Override
    public String toString() {
        String s = "";
        if (isAbsolute) {
            s += '/';
        }
        for (int i = 0; i < path.length - 1; i++) {
            s += path[i];
            s = s + "/";
        }
        s = s + path[path.length - 1];
        return s;
    }
}
