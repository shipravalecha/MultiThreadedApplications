/**
 * ReduceTask class to perform reduce on observations values and perform forkjoin.
 * This class extends RecursiveAction which creats the subtasks for forkjoin implementation.
 */
import java.util.concurrent.RecursiveAction;

public class ReduceTask extends RecursiveAction 
{
    HeatmapParallel hm;
    int index;

    // ReduceTask Constructor
    public ReduceTask(HeatmapParallel hm, int index) 
    {
        this.index = index;
        this.hm = hm;
    }

    Integer[][] getAccumulatedTally(int index) 
    {
        int p = hm.N_THREADS;
        int start  = index * p;
        int end = p * (index + 1);
        Integer[][] res = hm.init();

        //Tight loop for reduce for non-forked values
        for (int i = start; i < end; i++) {
            Integer[][] tallyValue = hm.prepare(hm.data.get(i));
            res = hm.combine(res, tallyValue);
        }
        return res;
    }

    // compute method to perform forkjoin
    @Override
    protected void compute() 
    {
        if (hm.isLeaf(index)) {
            hm.reduced=true;
		}
        
		if (index > hm.N_THREADS - 2) 
        {
            int actualIndex = index - (hm.N_THREADS - 1);
            hm.accumulatedData.set(index - (hm.N_THREADS - 1), getAccumulatedTally(actualIndex));
		}
		else 
        {
			ReduceTask t1 = new ReduceTask(hm, hm.left(index));
            t1.fork();
			ReduceTask t2 = new ReduceTask(hm, hm.right(index));
            t2.fork();
            t1.join();
            t2.join();
			hm.interior.set(index,  hm.combine(hm.value(hm.left(index)), hm.value(hm.right(index))));
		}
    }
}
