/**
 * Bitonic Sequential class does BtionicSort in loops
 */

public class BitonicSequential {
    public static final int N = 1 << 22;  /// size of the final sorted array (power of two)
    public static final int TIME_ALLOWED = 10;  /// seconds    

    /// process method performs Bitonic sort in loops
    public static void process(double[] data) 
    {
       int i,j,k;
        for (k = 2; k <= data.length; k = 2 * k) {
            for (j = k / 2; j > 0; j /= 2) {
                for (i = 0; i < data.length; i++) {
                    int ixj = i ^ j;
                    if ((ixj) > i) {
                        if ((i & k) == 0 && data[i] > data[ixj]) swap(i, ixj, data);
                        if ((i & k) != 0 && data[i] < data[ixj]) swap(i, ixj, data);
                    }
                }
            }
        }
    }

    /// Swap method that is called from process method
    public static void swap(int x, int y, double[] data) 
    {
        double z = data[x];
        data[x] = data[y];
        data[y] = z;
    }

    /**
     * Main entry for HW4 Bitonic Sequential part of the assignment.
     *
     * @param args not used
     */
    public static void main(String[] args) 
    {
        long start = System.currentTimeMillis();
        int work = 0;
        double[] data = new double[N];                           /// data array with length N
        BitonicSequential bitonicSequential = new BitonicSequential();

        while (System.currentTimeMillis() < start + TIME_ALLOWED * 1000) 
        {
            data = RandomArrayGenerator.getArray(N);             /// calling RandomArrayGenerator that generate random array
            bitonicSequential.process(data);                     /// calling process method

            if (!RandomArrayGenerator.isSorted(data) || N != data.length)
                System.out.println("failed");
            work++;
        }

        System.out.println("sorted " + work + " arrays (each: " + N + " doubles) in "
                + TIME_ALLOWED + " seconds");
    }
}
