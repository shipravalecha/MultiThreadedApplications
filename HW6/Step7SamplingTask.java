/**
 * This class performs sampling task i.e. grouping the observations to produce animation.
 */

public class Step7SamplingTask implements Runnable
{
    int threadId;
    HeatMap[] reducedOutput;
    public Step7SamplingTask(int threadId, HeatMap[] reducedOutput) 
    {
        this.threadId = threadId;
        this.reducedOutput = reducedOutput;
    }

    public HeatMap generateSample(int start, int pieceSize) 
    {
        HeatMap hm = new HeatMap(GeneralScan.DIM);
        int end = start + pieceSize;
        for (int i = start; i < end; i++) 
        {
            if (i >= reducedOutput.length) break;
            HeatMap val = this.reducedOutput[i];
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

        for (int i = start; i < end; i += GeneralScan.SAMPLING) 
        {
            HW6Sampling.sampledHeatMaps.add(generateSample(i, GeneralScan.RELEVANCE));
        }
    }
}
