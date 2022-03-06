/**
 * ScanTaskPass2 class to perform scan on observations values and perform forkjoin.
 * This class extends RecursiveAction which creats the subtasks for forkjoin implementation.
 */
import java.util.concurrent.RecursiveAction;

public class ScanTaskPass2 extends RecursiveAction 
{
    HeatMapParallelPass2 hm;
    int index;
    HeatMap tallyPrior;

    // ScanTask constructor
    public ScanTaskPass2(HeatMapParallelPass2 hm, int index, HeatMap tallyPrior) 
    {
        this.index = index;
        this.hm = hm;
        this.tallyPrior = tallyPrior;
    }

    // compute method
    @Override
    protected void compute() 
    {
      if (index > GeneralScan.N_THREADS - 2) 
      {
        int p = GeneralScan.N_THREADS;
        int position = index - (p - 1);
        int pieceSize = hm.data.size() / p;
        int start  = position * pieceSize;
        int end = pieceSize* (position + 1);

      // Tight loop for scan for non-forked values
      for (int i = start; i < end; i++) 
      {
        HeatMap prefix = hm.combine(tallyPrior, hm.prepare(hm.data.get(i)));
        int interval = GeneralScan.INTERVAL;
        if (i > interval - 1) 
        {
           HeatMap removed = prefix.remove(hm.scanOutput.get(i - interval));
           hm.displayOutput.set(i, removed);
        } else {
            hm.displayOutput.set(i, prefix);
        }
        hm.scanOutput.set(i, prefix);
        tallyPrior = prefix;
      }
    }
		else
    {
        ScanTaskPass2 t1 = new ScanTaskPass2(hm, hm.left(index), tallyPrior);
        t1.fork();
        ScanTaskPass2 t2 = new ScanTaskPass2(hm, hm.right(index),  hm.combine(tallyPrior, hm.value(hm.left(index))));
        t2.fork();
        t1.join();
        t2.join();
	  }
  }
}
