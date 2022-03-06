/**
 * This class has init, prepare, combine methods for pass2 reduce scan operations. 
 */

import java.util.ArrayList;

public class HeatMapParallelPass2 extends GeneralScan<HeatMap,HeatMap> {

    public HeatMapParallelPass2(ArrayList<HeatMap> data) {
        super(data);
    }

    // overriding init method of abstract class GeneralScan
    @Override
    public HeatMap init() {
        return new HeatMap(DIM);

    }

    // overriding prepare method of abstract class GeneralScan
    @Override
    public HeatMap prepare(HeatMap datum) {
        return datum;
    }

    // overriding combine method of abstract class GeneralScan
    @Override
    public HeatMap combine(HeatMap left, HeatMap right) {
      return HeatMap.add(left, right);
    }
}
