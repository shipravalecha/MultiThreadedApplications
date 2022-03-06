import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

@SuppressWarnings("unchecked")
public class BitonicBarrier implements Runnable {
   
    final double[] data;
    int start, end, j, k;
    CyclicBarrier c;

    /// BitonicBarrier Constructor
    BitonicBarrier(double[] data, int start, int end) 
    {
        this.data = data;
        this.start = start;
        this.end = end;
    }

    /// run method
    @Override
    public void run() 
    { 
        int i,j,k;
        for (k = 2; k <= data.length; k = 2 * k) {
            for (j = k / 2; j > 0; j /= 2) {  
                Pair p = new Pair(k, j);
                CyclicBarrier cb = BitonicParallel.c.get(p);
                for (i = this.start; i < this.end; i++) {
                    int ixj = i ^ j;
                    if ((ixj) > i) {
                        if ((i & k) == 0 && this.data[i] > this.data[ixj]) swap(i, ixj, this.data);
                        if ((i & k) != 0 && this.data[i] < this.data[ixj]) swap(i, ixj, this.data);
                    }
                }
                try
                {
                    cb.await();
                } 
                catch (InterruptedException | BrokenBarrierException e) 
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /// swap method that is called from process method
    public static void swap(int x, int y, double[] data) 
    {
        double z = data[x];
        data[x] = data[y];
        data[y] = z;
    }
}


  