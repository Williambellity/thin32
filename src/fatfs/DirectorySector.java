package fatfs;

import java.nio.ByteBuffer;
import java.util.Arrays;

/** Description of a record (both its position in sector and fat and content of record where it is stored), can be useful for read and write operations
 */
class RecordPosition {
    public final int cluster_id;
    public final int sector_id; // sector in which the record is stored
    public final int pos;  // position within the sector (in byte)
    public DirectoryRecord record; // read value of corresponding record, not thread safe

    /**
     *
     * @param cluster_id    
     * @param sector_id
     * @param pos
     * @param record null if empty record
     */
    RecordPosition(int cluster_id, int sector_id, int pos,
                   DirectoryRecord record) {
        this.cluster_id = cluster_id;
        this.sector_id = sector_id;
        this.pos = pos;
        this.record = record;
    }

    /**
     * @return whether the record is empty
     * @warning once a record is set, it needs to be synchronized on disc
     */
    boolean isEmpty(){
        return (this.record == null);
    }

    void setRecord(DirectoryRecord record){
        //TODO ??
    }
}

/** The DirectorySector class handles a bloc of bytes representing a subpart of a directory.
 * It is supposed to make the mapping between a Device directory sector transparent for
 * the programmer.
 *
 * Each record in the sector is represented by a DirectoryRecord object.
 */
public class DirectorySector
{
    final static int recordSize = 32;
    private byte[] sector;
    private int nbrecord;

    public DirectorySector(byte[] bb){
        this.sector = bb;
        this.nbrecord = bb.length/32;
    }

    /** Get the byte array representing the sector */
    byte[] getSector() {
        return this.sector;
    }

    /** Get the number of record that can be stored in sector*/
    int getNbRecord() {
        return this.nbrecord;
    }

    /**
     * Retrieve the i-th record in a DirBlock
     *
     * @param i index of the directory record to retrieve
     * @return the retrieved directory record
     */
    DirectoryRecord getDirectoryRecord(int i){
        return (new DirectoryRecord(ByteBuffer.wrap(Arrays.copyOfRange(this.sector, i*32, 32*(1+i)-1))));

    }

    /**
     * Store a directory record at the i-th location of a DirBlock
     *
     * @param d directory record to store
     * @param i index at which to store the record d
     */
    void setDirectoryRecord(DirectoryRecord d, int i){
        ByteBuffer dcontent = d.getContent();
        for (int j = 0; j < 32; j++) {
            this.sector[(i*32) + j] = dcontent.get();

        }
    }
}
