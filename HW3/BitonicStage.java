import java.util.Arrays;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.Collections;

public class BitonicStage implements Runnable 
{
    private static final int timeout = 10;  // in seconds
    SynchronousQueue<double[]> input1, input2, output;
    
    public BitonicStage(SynchronousQueue<double[]> input1, SynchronousQueue<double[]> input2, SynchronousQueue<double[]> output) 
    {
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
    }

    public static double[] process(double[] data1, double[] data2) 
    {
        /// reverseArray is called here which reverse the second array
        reverseArray(data2);

        /// creating new array which has 2 arrays- One in increasing order and another in decreasing order.
        double[] result = new double[data1.length + data2.length];

        /// copying the 2 arrays in new array
        for (int i = 0; i < data1.length; i++) {
            result[i] = data1[i];
        }
        for (int i = 0; i < data2.length; i++) {
            result[data1.length + i] = data2[i];
        }

        /// calling bitonicSort which sorts the bitonic sequence 
        bitonicSort(result, 0, result.length, 0); 

        return result;
    }

    /// reverse method to reverse second array
    public static void reverseArray(double[] data) 
    {
        if (data == null) 
        {
            return;
        }
        int i = 0;
        int j = data.length - 1;
        double tmp;

        while (j > i) 
        {
            tmp = data[j];
            data[j] = data[i];
            data[i] = tmp;
            j--;
            i++;
        }
    }

    public static void bitonicSort(double[] result, int start, int n, int direction)
    {
        if (n > 1)
        {
            bitonicMerge(result, start, n, direction);
            bitonicSort(result, start, n/2, direction);
            bitonicSort(result, (start + n/2), n/2, direction);
        }
    }

    /// bitonicMerge method that splits the array by recursively calling itself into half and swap elements if necessary to sort the array
    public static void bitonicMerge(double[] result, int start, int n, int direction) 
    {
        if (n > 1)
        {
            for (int i = start; i < (start + n/2); i++)
            {
                if ( (result[i] > result[i + n/2] && direction == 0) || (result[i] < result[i + n/2] && direction == 1))
                {
                    double swapData = result[i];
                    result[i] = result[i + n/2];
                    result[i + n/2] = swapData;
                }
            }
        }
    }

    /**
     * The Runnable part of the class. Polls the input queue and when ready, process (sort)
     * it and then write it to the output queue.
     */
    @Override
    public void run() 
    {
        double[] array1= new double[1];
        double[] array2 = new double[1];
        while (array1 != null && array2!=null) 
        {
            try 
            {
                array1 = this.input1.poll(timeout * 1000, TimeUnit.MILLISECONDS);
                array2 = this.input2.poll(timeout*1000, TimeUnit.MILLISECONDS);
                double[] result = process(array1, array2);
                output.offer(result, timeout * 1000, TimeUnit.MILLISECONDS);
            } 
            catch (InterruptedException e) 
            {
                return;
            }
        }
    }
}
