/**
 * ReduceTaskPass2 class to perform reduce on observations values and perform forkjoin.
 * This class extends RecursiveAction which creats the subtasks for forkjoin implementation.
 */
import java.util.concurrent.RecursiveAction;

public class ReduceTaskPass2 extends RecursiveAction 
{
    HeatMapParallelPass2 hm;
    int index;

    // ReduceTaskPass2 Constructor
    public ReduceTaskPass2(HeatMapParallelPass2 hm, int index) 
    {
        this.index = index;
        this.hm = hm;
    }

    HeatMap getAccumulatedTally(int index) 
    {
        int p = GeneralScan.N_THREADS;
        int start  = index * (hm.data.size() / p);
        int end = (index + 1) * (hm.data.size() / p);
        HeatMap res = hm.init();

        for (int i = start; i < end; i++) 
        {
            HeatMap tallyValue = hm.prepare(hm.data.get(i));
            res = hm.combine(res, tallyValue);
        }
        return res;
    }

    // compute method
    @Override
    protected void compute() 
    {
        if (hm.isLeaf(index)) {
            hm.reduced=true;
		}
        
        int p = GeneralScan.N_THREADS;

		if (index > p - 2) 
        {
            int actualIndex = index - (p- 1);
            hm.accumulatedData.set(index - (p - 1), getAccumulatedTally(actualIndex));
		}
		else 
        {
			ReduceTaskPass2 t1 = new ReduceTaskPass2(hm, hm.left(index)); 
            t1.fork();
			ReduceTaskPass2 t2 = new ReduceTaskPass2(hm, hm.right(index));
            t2.fork();
            t1.join();
            t2.join();
			hm.interior.set(index,  hm.combine(hm.value(hm.left(index)), hm.value(hm.right(index))));
		}
    }
}
