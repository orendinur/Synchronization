//Oren Dinur, id:206022667
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
public class Copier implements Runnable {
    public static final int COPY_BUFFER_SIZE = 4096;
    private int id;
    private File destination;
    private SynchronizedQueue<File> resultsQueue;
    private SynchronizedQueue<String> milestonesQueue;
    private boolean isMilestones;


    public Copier(int id, File destination,
                  SynchronizedQueue<File> resultsQueue,
                  SynchronizedQueue<String> milestonesQueue, boolean isMilestones) {
        this.id = id;
        this.destination = destination;
        this.resultsQueue = resultsQueue;
        this.milestonesQueue = milestonesQueue;
        this.isMilestones = isMilestones;
    }

    public void run() {
        File fileToCopy;
        byte[] buffer = new byte[COPY_BUFFER_SIZE];
        int length;
        milestonesQueue.registerProducer();
        fileToCopy = resultsQueue.dequeue();
        while (fileToCopy != null) {
            try {
                FileInputStream sourceStream = new FileInputStream(fileToCopy);//Creates an input file stream to read from a file with the specified name
                File newFile = new File(this.destination, fileToCopy.getName()); //Creates a new File instance from a parent abstract pathname and a child pathname string.
                FileOutputStream destinationStream = new FileOutputStream(newFile); //Creates an input file stream to read from the specified File object.
                while ((length = sourceStream.read(buffer)) > 0) { //Reads up to buffer length bytes of data from this sourceStream
                    destinationStream.write(buffer, 0, length);
                }
                sourceStream.close();
                destinationStream.close();
                if (this.isMilestones) {
                    this.milestonesQueue.enqueue("Copier from thread id: " + this.id
                            + " file named " + fileToCopy.toString() + " was copied");
                }

            } catch (IOException e) {
                System.err.println(e);
            }
            fileToCopy = resultsQueue.dequeue();
        }
        milestonesQueue.unregisterProducer();
    }
}
