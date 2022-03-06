/**
 * BitonicThread.java for HW4 for CPSC5600, Fall 2018, Seattle University
 */
package hw4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/*
 * @class BitonicThread - HW4 implement a parallel-bitonic sorter with symmetric threads
 */
public class BitonicThread implements Runnable {
	
	/**
	 * Main test.
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
      final int TIME_ALLOWED = 10;
      final int N = 1<<20;

      // on CS1, P=16 and GRANULARITY=4 were best (about 110 million-element arrays in 10 seconds)
      // difference between 1 and 4 on granularity were modest (<10%)
      final int P = 16;
      final int GRANULARITY = 4;

      long start = System.currentTimeMillis();
      int work = 0;
      while (System.currentTimeMillis() < start + TIME_ALLOWED * 1000) {
          double[] data = randomArray(N);
          process(data, P, GRANULARITY);
          if (!isSorted(data))
              System.out.println("failed");
          work++;
      }
      System.out.println("T = " + TIME_ALLOWED + " seconds");
      System.out.println("N = " + N);
      System.out.println("P = " + P);
      System.out.println("GRANULARITY = " + GRANULARITY + " levels");
      System.out.println(work + " arrays of doubles sorted");
	}

	/**
	 * Run one array through the network to sort it.
	 * 
	 * Once k has ramped up, then we have that for
	 * every stage, it starts with a barrier spanning all threads.
	 * Then after first column, there are two separate barriers, one
	 * for top and one for bottom of array. Then after second column,
	 * there are four separate barriers, one for top quarter, then second
	 * quarter, third quarter, and bottom quarter. Then after third column
	 * eight barriers, etc. Maximum number of barriers is 2^(granularity-1).
	 * 
	 * Note that a granularity of 1 uses a single barrier for every column
	 * (except when the compare and swap all happens in the same thread's wires
	 * in which case no barrier at all is required).
	 * 
	 * @param array         array to be sorted
	 * @param threadn       number of threads to use
	 * @param granularity   number of levels of granularity
	 * @throws InterruptedException
	 */
	public static void process(double[] array, int threadn, int granularity) throws InterruptedException {
		// construct a heap to store all the barriers
		// barrierheap[0] is across all of n wires (j is n/2)
		// barrierheap[1] is for wires 0..n/2-1 (j is n/4)
		// barrierheap[2] is for wires n/2..n-1
		// barrierheap[3] is for wires 0..n/4-1 (j is n/8)
		// etc.
		// assert(granularity < log2(array.length)) 
		CyclicBarrier[] barrierheap = new CyclicBarrier[(1<<granularity) - 1];
		int n = array.length;
		int width = n;
		int threadsPerBarrier = threadn;
		int i = 1;
		for (int node = 0; node < barrierheap.length; node++) {
			if (node == i) {
				width /= 2;
				i = i*2 + 1;
				threadsPerBarrier /= 2;
			}
			//System.out.println("Making barrier[" + node + "], " + width + " wide, for " + threadsPerBarrier + " threads");
			barrierheap[node] = new CyclicBarrier(threadsPerBarrier);
		}

		// Make and start all the threads
		List<Thread> threads = new ArrayList<Thread>(threadn);
		for (int t = 0; t < threadn; t++) {
			Thread thread = new Thread(new BitonicThread(array, t, threadn, barrierheap));
			threads.add(thread);
			thread.start();
		}
		
		// Wait for all the threads
		for (Thread thread : threads) {
			thread.join();
		}
	}

	/**
	 * Construct one of the symmetrical worker threads.
	 * @param array        array we are sorting
	 * @param threadi      which thread is this (of threadn of them)
	 * @param threadn      total number of worker threads
	 * @param barrierheap  barriers to use for synchronization
	 */
	public BitonicThread(double[] array, int threadi, int threadn, CyclicBarrier[] barrierheap) {
		this.array = array;
		int size = array.length / threadn;
		this.start = threadi * size;
		this.end = this.start + size;
		this.barrierheap = barrierheap;
		this.t = threadi;
	}

	@Override
	public void run() {
		for (int k = 2; k <= array.length; k *= 2) {
			for (int j = k / 2; j > 0; j /= 2) {
				awaitBarrier(j);
				for (int i = start; i < end; i++) {
					int ixj = i ^ j;
					if (i < ixj) {
						if ((i & k) == 0)
							compareAndSwapUp(i, ixj);
						else
							compareAndSwapDown(i, ixj);
					}
				}
			}
		}
	}
	
	/**
	 * Figure out which barrier from the barrierheap to use and wait for it.
	 * 
	 * @param j  along with this.start, j determines which barrier to use
	 */
	private void awaitBarrier(int j) {
		// First optimization is to note that no barrier is necessary
		// when the sweep is entirely within my thread's range of i, 
		// i.e., when j <= size/2
		int jsweep = j*2;
		int size = end - start;
		int n = array.length;
		if (jsweep < size)
			return;
		
		// Then choose the smallest granularity barrier available that spans our jsweep.
		// (No doubt, this could be calculated analytically without the loop.)
		int width = n/2, i = 1;
		while (width > jsweep && i < barrierheap.length) {
			width /= 2; 
			i = i*2 + 1;
		}
		width *= 2;
		i /= 2;
		
		// Each barrier is for width wires; left-most heap node at this level is node i.
		// Of the barriers at this level, we need the one that serves for our sweep. 
		// So, find offset such that width*offset <= start < width*(offset+1).
		int offset = start/width;
		int node = i + offset;
		//System.out.println("Chose barrier[" + node + "] for j=" + j + ", start=" + start);
		try {
			barrierheap[node].await();
		} catch (InterruptedException | BrokenBarrierException e) {
			System.out.println(t + " broken barrier due to: " + e);
			return;
		}
	}
	
	private void compareAndSwapUp(int a, int b) {
		if (array[a] > array[b])
			swap(a, b);
	}

	private void compareAndSwapDown(int a, int b) {
		if (array[a] < array[b])
			swap(a, b);
	}

	private void swap(int a, int b) {
		double temp = array[a];
		array[a] = array[b];
		array[b] = temp;
	}

	private int t, start, end;  // our thread number, our i range (start to end)
	private double[] array;     // array we are sorting
	private CyclicBarrier[] barrierheap;  // barriers to use
	
	/*
	 * Helper stuff for tests to follow...
	 */
	private static Random rand = new Random();
	private static double[] randomArray(int n) {
		double ret[] = new double[n];
		for (int i = 0; i < n; i++)
			ret[i] = rand.nextDouble() * 100.0;
		return ret;
	}
	private static boolean isSorted(double[] a) {
		if (a == null)
			return true;
		double last = a[0];
		for (int i = 1; i < a.length; i++)
			if (a[i] < last)
				return false;
		return true;
	}
}
