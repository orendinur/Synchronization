//Oren Dinur, id:206022667
import java.io.File;

public class Searcher implements Runnable{

    private int id;
    private String extension;
    private  SynchronizedQueue<File> directoryQueue;
    private  SynchronizedQueue<File> resultsQueue;
    private SynchronizedQueue<String> milestonesQueue;
    private boolean isMilestones;

    public Searcher(int id, java.lang.String extension,
                    SynchronizedQueue<File> directoryQueue,
                    SynchronizedQueue<File> resultsQueue,
				SynchronizedQueue<String> milestonesQueue, boolean isMilestones){
        this.id = id;
        this.extension = extension;
        this.directoryQueue = directoryQueue;
        this.resultsQueue = resultsQueue;
        this.milestonesQueue = milestonesQueue;
        this.isMilestones = isMilestones;
    }


    public void run() {
        File path;
        resultsQueue.registerProducer();
        milestonesQueue.registerProducer();
        path = directoryQueue.dequeue();
        while (path  != null) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (!file.isDirectory()) {
                    if (file.getName().endsWith(this.extension)) {
                        this.resultsQueue.enqueue(file);
                        if (this.isMilestones) {
                            this.milestonesQueue.enqueue("Searcher on thread id: "
                                    + this.id + " file named "
                                    + file.toString() + " was found");
                        }
                    }
                }
            }
            path = directoryQueue.dequeue();
        }
        resultsQueue.unregisterProducer();
        milestonesQueue.unregisterProducer();
    }
}
