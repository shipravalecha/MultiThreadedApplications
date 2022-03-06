/**
 * This class performs the Decay task that generates the animation with decayed effect of the observations.
 */

public class Step8DecayTask implements Runnable
{
    int threadId;
    HeatMap[] reducedOutput;
    public Step8DecayTask(int threadId, HeatMap[] reducedOutput) 
    {
        this.threadId = threadId;
        this.reducedOutput = reducedOutput;
    }

    public HeatMap getWeightedHeatMap(int index) 
    {
        int start = index - 100;
        int end = index;
        HeatMap hm = this.reducedOutput[index];

        for (int i = start; i <= end; i++) 
        {
            HeatMap val = this.reducedOutput[i];
            int lastTimeStamp = end;
            Double weight = (double) 1 / (lastTimeStamp - i + 1);
            val = val.applyWeight(weight);
            hm = HeatMap.add(hm, val);
        }
        return hm;
    }
    
    // run method
    @Override
    public void run() 
    {
        int piece = this.reducedOutput.length / GeneralScan.N_THREADS;
        int start = threadId * piece;
        int end = start + piece;
        for (int i = start; i < end; i++) {
            if(i >= 100) {
                HW6Decay.weightedHeatMap[i] = getWeightedHeatMap(i);
            }
            else {
                HW6Decay.weightedHeatMap[i] = HW6Decay.reducedHeatmaps[i];
            }
        }
    } 
}
