package fs;

public interface IFileStream {
    /**
     * Close a stream to a file.
     */
    void close();

    /**
     * Read bytes from a file.
     *
     * @param output array to store read data, the length of the array specify the
     *               number of byte to read
     *
     * @throws ForbiddenOperation if the stream was created in write mode
     *
     * @return the number of byte effectively read, smaller than the length of the
     *         output array if the end of file is encountered
     */
    int read(byte[] output) throws ForbiddenOperation;

    /**
     * Write bytes to the end of file (effectively increasing its size by n bytes, n
     * being the length of the input array)
     *
     * @param input data to be written
     *
     * @throws ForbiddenOperation if the stream was created in read mode
     *
     * @return the number of written byte
     */
    int write(byte[] input) throws ForbiddenOperation;
}
