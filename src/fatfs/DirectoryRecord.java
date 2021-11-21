package fatfs;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


/** The DirectoryRecord class encapsulates a ByteBuffer of 32 bytes representing a directory record.
 *
 * - octets 0-7 contiennent les 8 lettres du nom du fichier (si le premier caractère est 0x00, l'enregistrement est libre);
 * - octets 8-10 contienent les 3 lettres de l'extension du fichier;
 * - octet 11 contient les bits d'attributs du fichier  (le bit 0x08 = répertoire)
 * - octets 20-23 contiennent l'entier correspondant à l'indice du premier bloc du fichier (normalement, les différents octets représentant cette valeur ne sont pas contigus en mémoire, nous les regroupons pour simplifier l'implémentation);
 * - octets 28-31 contiennent l'entier correspondant à la taille du fichier en octets.
 *
 */
public class DirectoryRecord 
{
    private ByteBuffer content;
    final static int recordSize = 32;

    DirectoryRecord(ByteBuffer record) {
        this.content = record;
    }

    /** Create a new directory record, assume parameters are properly formated to the 8.3 filename
     * format with trailing spaces
     *
     * @param path_element name and extension of the file
     * @param first_cluster index of first cluster of the new file
     * @param is_directory whether the file is a directory
     * @return a directory record representing the file
     */
    static DirectoryRecord createNewRecord(PathElement path_element,
                                           int first_cluster, boolean is_directory) {
                                            if (is_directory) {
                                                ByteBuffer buffer = ByteBuffer.allocate(recordSize);
                                                buffer.put(path_element.getName());
                                                buffer.put(path_element.getExtension(),8,3);
                                                buffer.putInt(first_cluster, 20);
                                                DirectoryRecord dir_rec = new DirectoryRecord(buffer);
                                                return dir_rec;
                                            }
                                                else {
                                                    return null;
                                                }
                                               
                                           }

    /** Check if the record is empty
     *
     * @return true if the record is empty
     */
    public boolean isEmpty(){
        return (this.content.get() == 0);

    }

    /** Mark the record as empty
     */
    public void markAsDeleted(){
        ByteBuffer bb = ByteBuffer.allocate(1); 
        bb.put((byte) Integer.parseInt("E5",16)); 
        this.content.put(bb.array(),0,1);

    }

    /**
     * TODO check paramter type, if possible avoid copy in implementation !!!
     * @param path_element
     * @return
     */
    public boolean isFile(PathElement path_element){
        return ( (this.content.get(11) == 0) || (this.content.get(11) == 1) || (this.content.get(11) == 2) );
    }

    public ByteBuffer getName(){
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putLong(this.content.getLong());
        return bb;
    }

    public void setName(byte[] name){
        this.content.put(name,0,8);
    }

    public ByteBuffer getExtension(){
        ByteBuffer bb = ByteBuffer.allocate(3);
        bb.put(this.content.get(8));
        bb.put(this.content.get(9));
        bb.put(this.content.get(10));
        return bb;
    }

    public void setExtension(byte[] ext){
        this.content.put(ext,8,3);
    }

    public int getFirstCluster(){
        return this.content.getInt(20);
    }

    public void setFirstCluster(int l){
        this.content.putInt(l,20);
    }

    public int getLength(){
        return this.content.getInt(28);
    }

    public void setLength(int l){
        this.content.putInt(l,28);
    }

    public boolean isDirectory(){
        return (this.content.get(11) == 4) ;
    }

    public void setDirectory(boolean isDir){
        // this.content.put(11 ;
    }

    /** Access to ByteBuffer representing the record
     */
    public ByteBuffer getContent() {
        return this.content;
    }

    /** Returns a string describing the file
     */
    public String toString() {
            return StandardCharsets.UTF_8.decode(this.getName()).toString()
                    + StandardCharsets.UTF_8.decode(this.getExtension()).toString()
                    + " " + this.getLength();
    }

}
