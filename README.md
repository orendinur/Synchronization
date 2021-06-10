# Synchronization
A multithreaded search utility.
The utility will allow searching for all files with a specific extension in a root directory.
Files with the specific extension will be copied to a specified directory.
The application consists of two queues and three groups of threads.

## About the classes:
### SynchronizedQueue
This class should allow multithreaded enqueue/dequeue operations
### Scouter that implements Runnable.
This class is responsible for listing all directories that exist under the given root directory. It
enqueues all directories into the directory queue.
There is always only one scouter thread in the system.
### Searcher that implements Runnable.
This class reads a directory from the directory queue and lists all files in this directory. Then,
it checks for each file name if it has the correct extension. Files that have the correct
extension are enqueued to the results queue (to be copied).
### Copier that implements Runnable.
This class reads a file from the results queue (the queue of files that contains the output of
the searchers), and copies it into the specified destination directory.
### DiskSearcher.
This is the main class of the application. This class contains a main method that starts the
search process according to the given command lines.
The main class also contains another SynchronizedQueue<String> for a different purpose –
to save the execution milestones for each thread. i.e., Every action performed by threads
should be written down as a String in the queue.The program should receive a “flag” argument (essentialy a boolean), that
indicates whether the milestonesQueue and written to or not (in that case, it should be set
to null). If not, the program executes without any writings and the queue is set to null (the
constructor of the all threads receives null in it’s parameter place).
. If the flag is “true”, then the milestonesQueue must be
For every important action made in the program (scouting, searching, copying), there will be
a String in the queue.



![image](https://user-images.githubusercontent.com/83717835/121573017-6d3d3900-ca2d-11eb-9b63-bf546b239383.png)
