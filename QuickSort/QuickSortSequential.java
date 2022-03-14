/*
 * Shipra
 * CPSC 5600, Seattle University
 * 
 * This is the quick sort algorithm to sort the double array of size that is power of 2. 
 * Input to the program is random arrays of type double. RandomArrayGenerator class will generate arrays randomly.
 * Output of the program is number of sorted arrays in 10 milliseconds.
 * This is the Sequential class for performing sorting of the arrays using QuickSort algorithm.
 */


/**
 * @class QuickSortSequential class
 * @versioon 24-Jan-2020
 */
public class QuickSortSequential {
    public static final int N = 1 << 22;  
    public static final int TIME_ALLOWED = 10;  // seconds

       public static void main(String[] args) {
        long start = System.currentTimeMillis();
        int work = 0;
        int startPoint = 0;
        int endPoint = N - 1;

        while (System.currentTimeMillis() < start + TIME_ALLOWED * 1000) {
            double[] array = new double[N];
            array = RandomArrayGenerator.getArray(N);

            // System.out.println("Before sorting");
            // for (int i = 0; i < array.length; i++) {
            //     System.out.print(array[i] + ", ");
            // }
            // System.out.println(" ");

            QuickSort.QuickSortAlgoSerial(array, startPoint, endPoint);

            // System.out.println("After sorting work " + work);
            // for (int i = 0; i < array.length; i++) {
            //     System.out.print(array[i] + ", ");
            // }
            // System.out.println(" ");
            
            if (!RandomArrayGenerator.isSorted(array) || N != array.length)
                System.out.println("failed");
            work++;
        }
        System.out.println("sorted " + work + " arrays (each: " + N + " doubles) in "
                + TIME_ALLOWED + " seconds");
    }
}
