package fatfs;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;

import fs.IDevice;
import fs.IFileStream;
import fs.IFileSystem;

public class FileSystem implements IFileSystem {

    public boolean isMounted = false;
    public short sector_size;
    public short nb_sector_per_cluster;
    public short reserved_sector;
    public short nb_allocation_table;
    public int nb_sector_total;
    public int nb_sector_per_allocation_table;
    public int index_first_sector;
    public String current_path = "/";

    public void byteSplit(byte[] buffer, long data, int location, int size) {
        for (int i = 0; i < size; i++) {
            buffer[location + i] = (byte) ((data & (0xFF << 8 * (size - 1 - i))) >> 8 * (size - 1 - i));
        }
    }

    @Override
    public void format(IDevice dev, int size_of_cluster) {
        for (int i = 0; i < dev.getNumberOfSectors(); i++) {
            byte[] empty_sector = new byte[dev.getSectorSize()];
            dev.write(empty_sector, i);
        }

        ByteBuffer sector0Buffer = ByteBuffer.allocate(dev.getSectorSize());

        // offset

        sector0Buffer.putShort(Integer.parseInt("00B", 16), dev.getSectorSize());

        sector0Buffer.put(Integer.parseInt("00D", 16), (byte) size_of_cluster);

        sector0Buffer.putShort(Integer.parseInt("00E", 16), (short) 32);

        short nb_allocation_table = 2;

        sector0Buffer.putShort(Integer.parseInt("010", 16), nb_allocation_table);

        sector0Buffer.putInt(Integer.parseInt("020", 16), (int) dev.getNumberOfSectors());

        sector0Buffer.putInt(Integer.parseInt("024", 16), (int) nb_allocation_table / 2);

        int index_first_sector = 2;

        sector0Buffer.putInt(Integer.parseInt("02C", 16), index_first_sector);

        dev.write(sector0Buffer.array(), 0);

    }

    @Override
    public void mount(IDevice dev) {
        ByteBuffer sector0Buffer = ByteBuffer.wrap(dev.read(0));
        this.sector_size = sector0Buffer.getShort(Integer.parseInt("00B", 16));
        this.nb_sector_per_cluster = sector0Buffer.get(Integer.parseInt("00D", 16));
        this.reserved_sector = sector0Buffer.getShort(Integer.parseInt("00E", 16));
        this.nb_allocation_table = sector0Buffer.getShort(Integer.parseInt("010", 16));
        this.nb_sector_total = sector0Buffer.getInt(Integer.parseInt("020", 16));
        this.nb_sector_per_allocation_table = sector0Buffer.getInt(Integer.parseInt("024", 16));
        this.index_first_sector = sector0Buffer.getInt(Integer.parseInt("02C", 16));
        this.isMounted = true;

        // System.out.println(this.sector_size);
        // System.out.println(this.nb_sector_per_cluster);
        // System.out.println(this.reserved_sector);
        // System.out.println(this.nb_allocation_table);
        // System.out.println(this.nb_sector_total);
        // System.out.println(this.nb_sector_per_allocation_table);
        // System.out.println(this.index_first_sector);

    }

    @Override
    public void unmount() {
        this.isMounted = false;

    }

    @Override
    public void setWorkingDirectory(String path) throws FileNotFoundException {
        // TODO Auto-generated method stub

    }

    @Override
    public int totalFreeSpace() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public IFileStream openFile(String filename, char mode) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean removeFile(String filename) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean makeDirectory(String directory_name) {
        // TODO Auto-generated method stub
        return false;
    }

}
