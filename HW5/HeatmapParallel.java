import java.util.ArrayList;

public class HeatmapParallel extends GeneralScan<Observation, Integer[][]> {
    public HeatmapParallel(ArrayList<Observation> data) {
        super(data);
    }

    // overriding init method of abstract class GeneralScan
    @Override
    public Integer[][] init() {
        Integer[][] m = new Integer[DIM][DIM];
        for(int i = 0; i<DIM; i++) {
            for(int j = 0; j< DIM; j++) {
                m[i][j] = 0;
            }
        }
        return m;
    }

    // overriding prepare method of abstract class GeneralScan
    @Override
    public Integer[][] prepare(Observation datum) {
        
        Integer[][] v = init();

        double boundaryValue = 2.0 / DIM;

        double xStart = -1.0;
        int xIndex = 0;
        
        while (xStart < 1.0) {
            double newHigh = xStart + boundaryValue;
            if(newHigh > datum.x) {
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
        v[xIndex][yIndex] = 1;
        return v;
    }

    // overriding combine method of abstract class GeneralScan
    @Override
    public Integer[][] combine(Integer[][] left, Integer[][] right) {
        Integer[][] v = init();
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                v[i][j] = left[i][j] + right[i][j];
            }
        }
        return v;
    }
}
