import java.util.*;
import java.util.concurrent.SynchronousQueue;

public class BitonicPipeline 
{
    public static final int N = 1 << 22;  // size of the final sorted array (power of two)
    public static final int TIME_ALLOWED = 10;  // seconds
    private static final int N_THREADS = 4;
    
    // Declaring SynchronousQueues
    SynchronousQueue<double[]> queue1 =  new SynchronousQueue<double[]>();
    SynchronousQueue<double[]> queue2 =  new SynchronousQueue<double[]>();
    SynchronousQueue<double[]> queue3 =  new SynchronousQueue<double[]>();
    SynchronousQueue<double[]> queue4 =  new SynchronousQueue<double[]>();
    SynchronousQueue<double[]> queue5 =  new SynchronousQueue<double[]>();
    SynchronousQueue<double[]> queue6 =  new SynchronousQueue<double[]>();
    SynchronousQueue<double[]> queue7 =  new SynchronousQueue<double[]>();

    private double[] bitonicFourThreads(double[][] data) throws InterruptedException 
    {
        Thread RandomThreads[] = new Thread[N_THREADS];
        Thread threads[] = new Thread[N_THREADS];
        Thread bitonicThreads[] = new Thread[3];

        for (int j = 0; j < N_THREADS; j++) 
        {
            SynchronousQueue<double[]> q = new SynchronousQueue<double[]>();
            RandomArrayGenerator r1 = new RandomArrayGenerator(N / 4, q);

            /// we get arrays populated from RandomArrayGenerator
            RandomThreads[j] = new Thread(r1);
            RandomThreads[j].start();

            SynchronousQueue<double[]> result = null;
            if (j == 0) result = queue1;
            if (j == 1) result = queue2;
            if (j == 2) result = queue3;
            if (j == 3) result = queue4;

            /// pass the output recieved from RandomArrayGenerator to the StageOne as inputs
            StageOne s1 = new StageOne(q, result);
            threads[j] = new Thread(s1);
            threads[j].start();

            /// interrupt threads that were created for RandomArrayGenerator
            RandomThreads[j].interrupt();
        }

        int count = 0;

        /// sending input arrays and output arrays in synchronous queues to bitonic stage
        BitonicStage bitonicStageOne = new BitonicStage(queue1, queue2, queue5);
        bitonicThreads[count] = new Thread(bitonicStageOne);
        bitonicThreads[count].start();
        count++;

        BitonicStage bitonicStageTwo = new BitonicStage(queue3, queue4, queue6);
        bitonicThreads[count]= new Thread(bitonicStageTwo);
        bitonicThreads[count].start();
        count++;

        BitonicStage bitonicStageThree = new BitonicStage(queue5, queue6, queue7);
        bitonicThreads[count] =  new Thread(bitonicStageThree);
        bitonicThreads[count].start();
        double[] result = queue7.take();
       
       ///Interrupt all threads so that they don't wait
       
        for (int i = 0; i < N_THREADS; i++) {
            threads[i].interrupt();
        }
        for (int i = 0; i < 3; i++) {
            bitonicThreads[i].interrupt();
        }
        return result;
    }

    /// main function
    public static void main(String[] args) 
    {
        long start = System.currentTimeMillis();
        int work = 0;
        BitonicPipeline bitonicpipeline = new BitonicPipeline();

        /// Calling bitonicFourThreads that starts the processing 
        while (System.currentTimeMillis() < start + TIME_ALLOWED * 1000) 
        { 
            double[][] data = new double[4][];
            double[] result = null;

            try 
            {
               result = bitonicpipeline.bitonicFourThreads(data);
            } 
            catch(InterruptedException e) 
            {
                System.out.println("Caught Exception");
            }

            if (!RandomArrayGenerator.isSorted(result) || N != result.length)
                System.out.println("failed");
            work++;
        }
        System.out.println("sorted " + work + " arrays (each: " + N + " doubles) in "
                + TIME_ALLOWED + " seconds");
    }
}
