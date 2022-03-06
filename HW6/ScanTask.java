/**
 * ScanTask class to perform scan on observations values and perform forkjoin.
 * This class extends RecursiveAction which creats the subtasks for forkjoin implementation.
 */
import java.util.concurrent.RecursiveAction;

public class ScanTask extends RecursiveAction 
{
    HeatmapParallel hm;
    int index;
    HeatMap[] tallyPrior;

    // ScanTask constructor
    public ScanTask(HeatmapParallel hm, int index, HeatMap[] tallyPrior) 
    {
        this.index = index;
        this.hm = hm;
        this.tallyPrior = tallyPrior;
    }

    // compute method to perform forkjoin
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
        HeatMap[] res = hm.combine(tallyPrior, hm.prepare(hm.data.get(i)));
        hm.scanOutput.set(i, hm.combine(tallyPrior, hm.prepare(hm.data.get(i))));
        tallyPrior = res;
      }
    }
		else
    {
      ScanTask t1 = new ScanTask(hm, hm.left(index), tallyPrior);
      t1.fork();
      ScanTask t2 = new ScanTask(hm, hm.right(index),  hm.combine(tallyPrior, hm.value(hm.left(index))));
      t2.fork();
      t1.join();
      t2.join();
		}
  }
}
