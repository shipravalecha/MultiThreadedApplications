/**
 * This class has init, prepare, combine methods for pass1 reduce operations. 
 */

import java.util.ArrayList;

public class HeatmapParallel extends GeneralScan<Observation, HeatMap[]> {

    public HeatmapParallel(ArrayList<Observation> data) {
        super(data);
    }

    // overriding init method of abstract class GeneralScan
    @Override
    public HeatMap[] init() {
        HeatMap[] h = new HeatMap[TS];
        for (int i = 0; i < TS; i++) {
            h[i] = new HeatMap(DIM);
        }
        return h;
    }

    // overriding prepare method of abstract class GeneralScan
    @Override
    public HeatMap[] prepare(Observation datum) {
        HeatMap v = new HeatMap(DIM);
        HeatMap[] hm = init();

        double boundaryValue = 2.0 / DIM;

        double xStart = -1.0;
        int xIndex = 0;
        
        while (xStart < 1.0) {
            double newHigh = xStart + boundaryValue;
            if (newHigh > datum.x) {
               break;
           }
           xIndex++;
           xStart = newHigh;
        }

        int yIndex = 0;
        double yStart = -1.0;
        
        while (yStart < 1.0) {
            double newHigh = yStart + boundaryValue;
            if (newHigh > datum.y) {
               break;
           }
           yIndex++;
           yStart = newHigh;
        }
        
        int timestamp =  (int) datum.time;
        v.put(xIndex, yIndex, 1);
        hm[timestamp] = v;
        return hm;
    }

    // overriding combine method of abstract class GeneralScan
    @Override
    public HeatMap[] combine(HeatMap[] left, HeatMap[] right) {
        HeatMap[] hm = init();
        for (int i = 0; i < TS; i++) {
                hm[i] = HeatMap.add(left[i], right[i]);
            }
        return hm;
    }
}
