import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;  
import java.util.concurrent.TimeoutException;  

/**
 *Bitonic Parallel class does BtionicSort in loops paralelly using 
 * threads and cyclic barrier
 */
public class BitonicParallel
{
    public static final int N = 1 << 22;  /// size of the final sorted array (power of two)
    public static final int TIME_ALLOWED = 10;  /// seconds
    private static final int N_THREADS = 8;

    /// Using a hashmap to store the cyclic barriers that will be used for a given j and k values in the threads.
    public static final HashMap<Pair, CyclicBarrier> c = new HashMap<Pair, CyclicBarrier>();

    /// process method performs Bitonic sort in loops using threads and cyclic barriers
    public static void process(double[] data1) 
    {
        int i,j,k;
        double[] data = data1;
        Thread threads[] = new Thread[N_THREADS];
        for (k = 2; k <= data.length; k = 2 * k) {
            for (j = k / 2; j > 0; j /= 2) {
                Pair<Integer, Integer> p = new Pair<Integer, Integer>(k, j);
                c.put(p, new CyclicBarrier(N_THREADS));
            }
        }
        int start = 0;

        for(int a = 0; a < N_THREADS; a++) 
        {
            int piece = N / N_THREADS;

            /// Calling BitonicBarrier method which creats cyclic barrier as per j 
            BitonicBarrier b = new BitonicBarrier(data, start, start + piece); 
            threads[a]= new Thread(b);
            threads[a].start();
            start = start + piece;
        }

        /// waiting for all threads to complete its processing
        for(int a = 0; a < N_THREADS; a++) 
        {
            try {
                threads[a].join();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    
    /**
     * Main entry for HW4 Bitonic Parallel part of the assignment.
     *
     * @param args not used
     */
    public static void main(String[] args) 
    {
        long start = System.currentTimeMillis();
        int work = 0;
        double[] data = new double[N];
        BitonicParallel bitonicparallel = new BitonicParallel();

        while (System.currentTimeMillis() < start + TIME_ALLOWED * 1000) 
        { 
            data = RandomArrayGenerator.getArray(N);            /// calling RandomArrayGenerator that generate random array
            bitonicparallel.process(data);                      /// calling process method

            if (!RandomArrayGenerator.isSorted(data) || N != data.length)
                System.out.println("failed");
            work++;
        }
        System.out.println("sorted " + work + " arrays (each: " + N + " doubles) in "
                + TIME_ALLOWED + " seconds");
    }
}
