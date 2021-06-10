//Oren Dinur, id:206022667
import java.io.File;
import java.io.IOError;

public class DiskSearcher {
    public static final int DIRECTORY_QUEUE_CAPACITY = 50;
    public static final int RESULTS_QUEUE_CAPACITY = 50;
    public static SynchronizedQueue<String> milestonesQueue;

//Params: <boolean of milestoneQueueFlag> <file-extension> <root directory> <destination directory>
// <# of searchers> <# of copiers>

//Usage: : java DiskSearcher true txt C:\OS_Exercises C:\temp 10 5

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        String extension = args[1];
        String root_dir = args[2];
        String dest_dir = args[3];
        int numOfSearchers = Integer.parseInt(args[4]);
        int numOfCopiers = Integer.parseInt(args[5]);
        File rootFolder;
        File destinationFolder;
        int id = 1;

        try {
            rootFolder = new File(root_dir);
        } catch (IOError e) {
            System.err.println(e);
            return;
        }

        try {
            destinationFolder = new File(dest_dir);
            if (!destinationFolder.exists()) {
                destinationFolder.mkdir();
            }
        } catch (IOError e) {
            System.err.println(e);
            return;
        }

        boolean flag = false;
        if (args[0].equalsIgnoreCase("true")) {
            flag = true;
        }

        milestonesQueue = new SynchronizedQueue(DIRECTORY_QUEUE_CAPACITY * RESULTS_QUEUE_CAPACITY);
        milestonesQueue.registerProducer();

        SynchronizedQueue<File> directoryQueue = new SynchronizedQueue<File>(DIRECTORY_QUEUE_CAPACITY);
        SynchronizedQueue<File> resultsQueue = new SynchronizedQueue<File>(RESULTS_QUEUE_CAPACITY);

        milestonesQueue.enqueue("General, program has started the search");
        milestonesQueue.unregisterProducer();
        Thread scouter = new Thread(new Scouter(id++, directoryQueue, rootFolder, milestonesQueue, flag));//new thread is created
        try {
            scouter.start();
            scouter.join(); //allows to wait until scouter.start() completes
        } catch (InterruptedException e) {
            System.out.print(e);
        }

        Searcher[] searchers = new Searcher[numOfSearchers];
        Thread[] searchersThreads = new Thread[numOfSearchers];

        for (int i = 0; i < numOfSearchers; i++) {
            searchers[i] = new Searcher(id++, extension, directoryQueue, resultsQueue, milestonesQueue, flag);
            searchersThreads[i] = new Thread(searchers[i]);
            searchersThreads[i].start();
        }


        Copier[] copiers = new Copier[numOfCopiers];
        Thread[] copiersThreads = new Thread[numOfCopiers];
        for (int i = 0; i < numOfCopiers; i++) {
            copiers[i] = new Copier(id++, destinationFolder, resultsQueue, milestonesQueue, flag);
            copiersThreads[i] = new Thread(copiers[i]);
            copiersThreads[i].start();
        }

        try {
            for (int i = 0; i < searchers.length; i++) {
                searchersThreads[i].join();
            }

            for (int i = 0; i < copiers.length; i++) {
                copiersThreads[i].join();
            }
        } catch (InterruptedException e) {
            System.err.println(e);
        }
        String[] buff = milestonesQueue.getBuffer();
        for (int i = 0; i < buff.length; i++) {
            System.out.println( buff[i] + '\n');

        }

        System.out.println("Process ended successfully");
        // Time management
        long stopTime = System.nanoTime();
        long elapsedTime = stopTime - startTime;
        System.out.println(String.format("Time elapsed %f",(double)elapsedTime / 1_000_000_000.0));
    }
}



