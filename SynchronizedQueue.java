//Oren Dinur, id:206022667
/**
 * A synchronized bounded-size queue for multithreaded producer-consumer applications.
 *
 * @param <T> Type of data items
 */
public class SynchronizedQueue<T> {

	private T[] buffer;
	private int producers;
	// TODO: Add more private members here as necessary
	private int first;
	private int last;
	private Object lock;// Private lock

	/**
	 * Constructor. Allocates a buffer (an array) with the given capacity and
	 * resets pointers and counters.
	 * @param capacity Buffer capacity
	 */
	@SuppressWarnings("unchecked")
	public SynchronizedQueue(int capacity) {
		this.buffer = (T[])(new Object[capacity]);
		this.producers = 0;
		// TODO: Add more logic here as necessary
		this.first = 0;
		this.last = 0;
		this.lock = new Object();
	}

	/**
	 * Dequeues the first item from the queue and returns it.
	 * If the queue is empty but producers are still registered to this queue,
	 * this method blocks until some item is available.
	 * If the queue is empty and no more items are planned to be added to this
	 * queue (because no producers are registered), this method returns null.
	 *
	 * @return The first item, or null if there are no more items
	 * @see #registerProducer()
	 * @see #unregisterProducer()
	 */
	public T dequeue() {
		T temp;
		while (true){
			if (this.getSize() > 0) {
				synchronized (this.lock) { //active lock for the instance
					temp = buffer[first];
					buffer[first] = null;
					this.first+= 1;
					this.first %= buffer.length;
					this.lock.notifyAll();
					return temp;
				}
			}else if (this.producers > 0) { //producers are still registered to this queue but not available
				synchronized (this.lock) {
					try {
						this.lock.wait();
					} catch (InterruptedException e) {
						System.err.println(e);
					}
				}
			}else {
				return null;
			}
		}
	}



	/**
	 * Enqueues an item to the end of this queue. If the queue is full, this
	 * method blocks until some space becomes available.
	 *
	 * @param item Item to enqueue
	 */
	public void enqueue(T item) {
		while (true){
			if (this.getSize() < buffer.length) {
				synchronized (this.lock) {
					buffer[last] = item;
					this.last++;
					this.last %= this.buffer.length;
					lock.notifyAll();
					return;
				}
			}
			else{ //Queue is full
				try {
					this.wait();
				} catch (InterruptedException e) {
					System.err.println(e);
				}
			}
		}
	}

	/**
	 * Returns the capacity of this queue
	 * @return queue capacity
	 */
	public int getCapacity() {
		return buffer.length;
	}

	/**
	 * Returns the current size of the queue (number of elements in it)
	 * @return queue size
	 */
	public int getSize() {
		return (last + buffer.length - first) % buffer.length;
	}

	/**
	 * Registers a producer to this queue. This method actually increases the
	 * internal producers counter of this queue by 1. This counter is used to
	 * determine whether the queue is still active and to avoid blocking of
	 * consumer threads that try to dequeue elements from an empty queue, when
	 * no producer is expected to add any more items.
	 * Every producer of this queue must call this method before starting to
	 * enqueue items, and must also call <see>{@link #unregisterProducer()}</see> when
	 * finishes to enqueue all items.
	 *
	 * @see #dequeue()
	 * @see #unregisterProducer()
	 */
	public void registerProducer() {
		// TODO: This should be in a critical section
		synchronized(this) {
			this.producers++;
		}
	}

	/**
	 * Unregisters a producer from this queue. See <see>{@link #registerProducer()}</see>.
	 *
	 * @see #dequeue()
	 * @see #registerProducer()
	 */
	public void unregisterProducer() {
		// TODO: This should be in a critical section
		synchronized(this) {
			this.producers--;
		}
	}

	public String[] getBuffer() {
		int size = this.getSize();
		String [] buff = new String[size];
		for (int j =0; j < size; j++){
			buff[j]= buffer [j].toString();
		}

		return buff;
	}


}
