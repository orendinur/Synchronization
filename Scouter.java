//Oren Dinur, id:206022667
import java.io.File;

public class Scouter implements Runnable{

    private int id;
    private SynchronizedQueue<File> directoryQueue;
    private File root;
    private SynchronizedQueue<String> milestonesQueue;
    private boolean isMilestones;

    public Scouter(int id, SynchronizedQueue<File> directoryQueue,
                   File root, SynchronizedQueue<String> milestonesQueue, boolean isMilestones){
        this.id = id;
        this.directoryQueue = directoryQueue;
        this.root = root;
        this.milestonesQueue = milestonesQueue;
        this.isMilestones = isMilestones;
    }

    public void run(){
        this.directoryQueue.registerProducer();
        add_dir(this.root);
        this.directoryQueue.unregisterProducer();
    }

    public void add_dir(File root) {
        File[] files = root.listFiles(); //The function returns an array of Files
        if(files == null) return;
        for(File file : files) {
            if (file.isDirectory()) {
                this.directoryQueue.enqueue(file);
                add_dir(file);
                if (isMilestones){
                    this.milestonesQueue.registerProducer();
                    this.milestonesQueue.enqueue("Scouter on thread id "+
                            this.id + ": directory named " +
                            file.getName() + " was scouted");
                    this.milestonesQueue.unregisterProducer();
                }
            }
        }
    }
}
