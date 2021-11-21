package fatfs;

import drives.Device;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {
        // Create the ssd
        boolean success = Device.buildDevice("SSD_2_LargeFiles_2.data", (2 << 10));
        if (!success)
            return;

        Device d = new Device();
        try {
            d.mount("SSD_2_LargeFiles_2.data");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Create the file system
        fs.IFileSystem file_system = FileSystemFactory.createFileSystem();

        if (file_system == null) {
            System.err.println("You need to implement the factory method!");
            return;
        }

        // IDK WHAT IS SIZE OF CLUSTER
        file_system.format(d, 32);
        file_system.mount(d);

        // TODO do something with the device and the filesystem...

        FATAccess FATA = new FATAccess(d, 1, 1, 1);
        System.out.println(FATA.getAddressFAT0()); // work
        System.out.println(FATA.getNbFATSector());
        System.out.println(FATA.getEmptyCluster());
    }
}
