public class QuickSortParallel 
{
    public static final int N = 1 << 22;
    public static final int TIME_ALLOWED = 10;  // seconds
    public static final int N_THREADS = 256;
    

    /// main function
    public static void main(String[] args) 
    {
        long start = System.currentTimeMillis();
        int work = 0;
        int startPoint = 0;
        int endPoint = N - 1;

        while (System.currentTimeMillis() < start + TIME_ALLOWED * 1000) 
        { 
            double[] array = new double[N];

            array = RandomArrayGenerator.getArray(N);
            QuickSort.QuickSortAlgoParallel(array, startPoint, endPoint);


            if (!RandomArrayGenerator.isSorted(array) || N != array.length)
                System.out.println("failed");
            work++;
        }
        System.out.println("sorted " + work + " arrays (each: " + N + " doubles) in "
                + TIME_ALLOWED + " seconds");
    }
}
