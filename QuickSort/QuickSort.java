import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;


public class QuickSort
{
    private static final int timeout = 10;  // in seconds

    public static void main(String args[]) {
        double array[] = {7,2,1,6,8,5,3,4};
        
        // System.out.println("before sorting: ");
        // for(double element : array) {
        //     System.out.print(element + ", ");
        // }
        // System.out.println();

        int start = 0;
        int end = array.length - 1;

        // // // call method to sort
        // // System.out.println("start: " + start);
        // // System.out.println("end: " + end);
        // double[] result = QuickSortAlgo(array, start, end);

        // for (int i = 0; i < array.length; i++)
        // {
        //     result[i] = array[i];
        // }

        // System.out.println("After sorting");
        // for(double element : result) {
        //     System.out.print(element + ", ");
        // }
        // System.out.println("after sorting: ");
        // for (int i = 0; i < array.length; i++)
        // {
        //     System.out.print(array[i] + ", ");
        // }
    }

    public static void QuickSortAlgoSerial(double array[], int start, int end) 
    {
        if (start < end) 
        {  
            int partitionIndex = partition(array, start, end);
            QuickSortAlgoSerial(array, start, (partitionIndex - 1));
            QuickSortAlgoSerial(array, (partitionIndex + 1), end);
        }
    }
   
    public static void QuickSortAlgoParallel(double array[], int start, int end) 
    {  
            ForkJoinPool quickSortPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
            ForkJoin task1 = new ForkJoin(array, start, end);
            quickSortPool.invoke(task1);

            // ForkJoin task2 = new ForkJoin(array, (partitionIndex + 1), end);
            // quickSortPool.invoke(task2);
    }

    public static int partition(double array[], int start, int end) 
    {
        double pivot = array[end];
        int partitionIndex = start;
        
        for (int i = start; i < end; i++) 
        {
            if (array[i] <= pivot) 
            {
                swap(array, partitionIndex, i);

                partitionIndex++;
            }
        }
        swap(array, partitionIndex, end);
        return partitionIndex;
    }

    public static void swap(double arr[], int x, int y) {
        double temp = arr[x];
        arr[x] = arr[y];
        arr[y] = temp;
    }
} 

