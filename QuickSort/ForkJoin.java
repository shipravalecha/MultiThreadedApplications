import java.util.concurrent.RecursiveAction;

public class ForkJoin extends RecursiveAction 
{
   double[] array;
   int start;
   int end;

    public ForkJoin(double[] array, int start, int end) 
    {
        this.array = array;
        this.start = start;
        this.end = end;
   }

   @Override
    protected void compute() 
    {
        if (start < end) 
        {
            int partitionIndex = QuickSort.partition(array, start, end);

            ForkJoin t1 = new ForkJoin(array, start, (partitionIndex - 1));
            t1.fork();

            ForkJoin t2 = new ForkJoin(array, (partitionIndex + 1), end);
            t2.fork();

            t1.join();
            t2.join();
        }
    }
}
