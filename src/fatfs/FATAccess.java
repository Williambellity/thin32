package fatfs;

import fs.IDevice;

import java.nio.ByteBuffer;
//import java.util.Arrays;
//import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.OptionalInt;

class FATAccess {

    private static boolean endOfLink(int status) {
        return status < 0x02 || status >= 0xFFFFFF7;
    }

    private int addressFAT0;
    private int nbFATSector;
    private int clusterStatus;
    private IDevice dev;
    private int nbFAT;

    FATAccess(IDevice dev, int address_FAT0, int nb_FAT_sector, int nb_FAT) {
        this.addressFAT0 = address_FAT0;
        this.dev = dev;
        this.nbFATSector = nb_FAT_sector;
        this.nbFAT = nb_FAT;
    }

    /** Get the address of the FAT0 in sector */
    int getAddressFAT0() {
        return this.addressFAT0;
    }

    /** Get the number of fat */
    int getNbFATSector() {
        return this.nbFATSector;
    }

    /**
     * Get the number of fat entry per sector
     */
    private int fatEntryPerSector() {
        return this.dev.getSectorSize() / 4;
    }

    /**
     * Mark all clusters as free
     */
    public void freeFAT() {
        ByteBuffer buffer = ByteBuffer.wrap(this.dev.read(1));
        while (buffer.position() != this.dev.getSectorSize() * this.nbFATSector) {
            int status = buffer.getInt();
            status = 0;
            buffer.putInt(buffer.position(), status);
        }
        this.dev.write(buffer.array(), 1);
    }

    /**
     * Allocate a new cluster (mark it as end of file in allocation table)
     *
     * @return cluster id if found
     */
    public OptionalInt getEmptyCluster() {
        ByteBuffer buffer = ByteBuffer.wrap(this.dev.read(1));
        int position = buffer.position();
        int status = buffer.getInt();
        System.out.print("coucou" + status);
        while (status == 0) {
            position = buffer.position();
            status = buffer.getInt();
            System.out.println("coucoutt" + status);
        }

        OptionalInt result = OptionalInt.of(position);
        return result;
    }
    // optional => can be created with OptionalInt.of(value) or OptionalInt.empty();
    // can be tested with opt.isEmpty() and used with opt.getAsInt()

    /**
     * Update the status of a given sector
     *
     * @param cluster_id index of the cluster to be updated
     * @param status     new status (0x0FFFFFFF : last sector, 0x00 : empty, other :
     *                   id of next sector)
     */
    public void setClusterStatus(int cluster_id, int status) {
        ByteBuffer buffer = ByteBuffer.wrap(this.dev.read(1));
        buffer.putInt(cluster_id, status);
        this.dev.write(buffer.array(), 1);
    }

    /**
     * Return the status registered in FAT for the given cluster
     *
     * @param cluster_id index of cluster
     * @return status of cluster
     */
    public int getClusterStatus(int cluster_id) {
        ByteBuffer buffer = ByteBuffer.wrap(this.dev.read(1));
        int statut = buffer.getInt(cluster_id);
        return statut;
    }

    /**
     * Add a new cluster to a file
     *
     * @param prev_cluster_id index of the last cluster of the file
     *
     * @return return the id of the added sector
     *
     * @todo should throw an exception "out of memory"
     */
    int addClusterToFile(int prev_cluster_id) {
        OptionalInt cluster_id = getEmptyCluster();
        if (cluster_id.isEmpty()) {
            return 0;
        } else {
            int cluster_idInt = cluster_id.getAsInt();
            this.setClusterStatus(prev_cluster_id, cluster_idInt);
            return cluster_idInt;
        }

    }

    /**
     * Get the amount of free space
     *
     * @return the amount of free space in the currently mounted device, 0 if no
     *         device is mounted
     */
    public int totalFreeSpace() {
        int freeSpace = 0;
        ByteBuffer buffer = ByteBuffer.wrap(this.dev.read(1));
        while (buffer.position() != this.dev.getSectorSize() * this.nbFATSector) {
            int status = buffer.getInt();

            if (status == 0) {

                freeSpace++;
            }
        }
        return freeSpace;
    }

    /**
     * Generate a cluster iterator (cf ClusterIterator class)
     * 
     * @param cluster_id       id of first cluster of the file
     * @param allocate_cluster whether a new cluster should be allocated when the
     *                         end of file is reached
     */
    FATAccess.ClusterIterator getClusterIterator(int cluster_id, boolean allocate_cluster) {
        return new ClusterIterator();
    }

    /**
     * A ClusterIterator allows iterating over FAT entry that belong to a
     * linked-list of clusters. Also provide the capability to allocate new cluster
     * on demand.
     */
    class ClusterIterator {
        private boolean allocate_cluster;
        private int cluster;

        /**
         * @param cluster          first cluster of the linked-list
         * @param allocate_cluster whether a new cluster should be allocated when the
         *                         end of file is reached
         */
        {
            this.cluster = cluster;
            this.allocate_cluster = allocate_cluster;
        }

        /**
         * Allow to change allocation strategy (automatic allocation of cluster against
         * no allocation)
         */
        public void setAllocateCluster(boolean allocate_cluster) {
            this.allocate_cluster = !this.allocate_cluster;
        }

        /** Return whether the iteration is finished */
        public boolean end() {
            return (Integer.parseInt("0FFFFFFF", 16) == this.cluster);
        }

        /** Increment iterator to point to next cluster of the link-list */
        public void incr() throws NoSuchElementException {
            int status = getClusterStatus(this.cluster);
            this.cluster = status;
        }

        /** Get current cluster */
        public Integer current() {
            return this.cluster;
        };
    };
}